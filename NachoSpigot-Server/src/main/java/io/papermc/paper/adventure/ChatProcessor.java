package io.papermc.paper.adventure;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AbstractChatEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.LazyPlayerSet;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatProcessor {
    // <-- copied from adventure-text-serializer-legacy
    private static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    private static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");
    private static final TextReplacementConfig URL_REPLACEMENT_CONFIG = TextReplacementConfig.builder()
        .match(DEFAULT_URL_PATTERN)
        .replacement(url -> {
            String clickUrl = url.content();
            if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                clickUrl = "http://" + clickUrl;
            }
            return url.clickEvent(ClickEvent.openUrl(clickUrl));
        }).build();
    // copied from adventure-text-serializer-legacy -->
    private static final String DEFAULT_LEGACY_FORMAT = "<%1$s> %2$s"; // copied from PlayerChatEvent/AsyncPlayerChatEvent
    final MinecraftServer server;
    final EntityPlayer player;
    final String message;
    final boolean async;
    final Component originalMessage;

    public ChatProcessor(final MinecraftServer server, final EntityPlayer player, final String message, final boolean async) {
        this.server = server;
        this.player = player;
        this.message = message;
        this.async = async;
        this.originalMessage = Component.text(message);
    }

    @SuppressWarnings("deprecated")
    public void process() {
        final boolean listenersOnAsyncEvent = canYouHearMe(AsyncPlayerChatEvent.getHandlerList());
        final boolean listenersOnSyncEvent = canYouHearMe(PlayerChatEvent.getHandlerList());
        if (listenersOnAsyncEvent || listenersOnSyncEvent) {
            final CraftPlayer player = this.player.getBukkitEntity();
            final AsyncPlayerChatEvent ae = new AsyncPlayerChatEvent(this.async, player, this.message, new LazyPlayerSet(this.server));
            this.post(ae);
            if (listenersOnSyncEvent) {
                final PlayerChatEvent se = new PlayerChatEvent(player, ae.getMessage(), ae.getFormat(), ae.getRecipients());
                se.setCancelled(ae.isCancelled()); // propagate cancelled state
                this.queueIfAsyncOrRunImmediately(new Waitable<Void>() {
                    @Override
                    protected Void evaluate() {
                        post(se);
                        return null;
                    }
                });
                this.processModern(
                    legacyRenderer(se.getFormat()),
                    this.viewersFromLegacy(se.getRecipients()),
                    PaperAdventure.LEGACY_SECTION_UXRC.deserialize(se.getMessage()),
                    se.isCancelled()
                );
            } else {
                this.processModern(
                    legacyRenderer(ae.getFormat()),
                    this.viewersFromLegacy(ae.getRecipients()),
                    PaperAdventure.LEGACY_SECTION_UXRC.deserialize(ae.getMessage()),
                    ae.isCancelled()
                );
            }
        } else {
            this.processModern(
                ChatRenderer.defaultRenderer(),
                new LazyChatAudienceSet(this.server),
                Component.text(this.message).replaceText(URL_REPLACEMENT_CONFIG),
                false
            );
        }
    }

    private void processModern(final ChatRenderer renderer, final Set<Audience> viewers, final Component message, final boolean cancelled) {
        final CraftPlayer player = this.player.getBukkitEntity();
        final AsyncChatEvent ae = new AsyncChatEvent(this.async, player, viewers, renderer, message, this.originalMessage);
        ae.setCancelled(cancelled); // propagate cancelled state
        this.post(ae);
        final boolean listenersOnSyncEvent = canYouHearMe(ChatEvent.getHandlerList());
        if (listenersOnSyncEvent) {
            this.queueIfAsyncOrRunImmediately(new Waitable<Void>() {
                @Override
                protected Void evaluate() {
                    final ChatEvent se = new ChatEvent(player, ae.viewers(), ae.renderer(), ae.message(), ChatProcessor.this.originalMessage);
                    se.setCancelled(ae.isCancelled()); // propagate cancelled state
                    ChatProcessor.this.post(se);
                    ChatProcessor.this.complete(se);
                    return null;
                }
            });
        } else {
            this.complete(ae);
        }
    }

    private void complete(final AbstractChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final CraftPlayer player = this.player.getBukkitEntity();
        final Component displayName = displayName(player);
        final Component message = event.message();
        final ChatRenderer renderer = event.renderer();

        final Set<Audience> viewers = event.viewers();

        if (viewers instanceof LazyChatAudienceSet && ((LazyChatAudienceSet) viewers).isLazy()) {
            this.server.console.sendMessage(player, renderer.render(player, displayName, message, this.server.console), MessageType.CHAT);
            for (final EntityPlayer viewer : this.server.getPlayerList().players) {
                final Player bukkit = viewer.getBukkitEntity();
                bukkit.sendMessage(player, renderer.render(player, displayName, message, bukkit), MessageType.CHAT);
            }
        } else {
            for (final Audience viewer : viewers) {
                viewer.sendMessage(player, renderer.render(player, displayName, message, viewer), MessageType.CHAT);
            }
        }
    }

    private Set<Audience> viewersFromLegacy(final Set<Player> recipients) {
        if (recipients instanceof LazyPlayerSet && ((LazyPlayerSet) recipients).isLazy()) {
            return new LazyChatAudienceSet(this.server);
        }
        final HashSet<Audience> viewers = new HashSet<>(recipients);
        viewers.add(this.server.console);
        return viewers;
    }

    private static String legacyDisplayName(final CraftPlayer player) {
        return player.getDisplayName();
    }

    private static Component displayName(final CraftPlayer player) {
        return player.displayName();
    }

    private static ChatRenderer legacyRenderer(final String format) {
        if (DEFAULT_LEGACY_FORMAT.equals(format)) {
            return ChatRenderer.defaultRenderer();
        }
        return ChatRenderer.viewerUnaware((player, displayName, message) -> PaperAdventure.LEGACY_SECTION_UXRC.deserialize(String.format(format, legacyDisplayName((CraftPlayer) player), PaperAdventure.LEGACY_SECTION_UXRC.serialize(message))).replaceText(URL_REPLACEMENT_CONFIG));
    }

    private void queueIfAsyncOrRunImmediately(final Waitable<Void> waitable) {
        if (this.async) {
            this.server.processQueue.add(waitable);
        } else {
            waitable.run();
        }
        try {
            waitable.get();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt(); // tag, you're it
        } catch (final ExecutionException e) {
            throw new RuntimeException("Exception processing chat", e.getCause());
        }
    }

    private void post(final Event event) {
        this.server.server.getPluginManager().callEvent(event);
    }

    private static boolean canYouHearMe(final HandlerList handlers) {
        return handlers.getRegisteredListeners().length > 0;
    }
}
