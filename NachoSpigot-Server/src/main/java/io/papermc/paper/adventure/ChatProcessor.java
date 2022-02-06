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

    @SuppressWarnings({"CodeBlock2Expr", "deprecated"})
    public void process() {
        this.processingLegacyFirst(
            // continuing from AsyncPlayerChatEvent (without PlayerChatEvent)
            event -> {
                this.processModern(
                    legacyRenderer(event.getFormat()),
                    this.viewersFromLegacy(event.getRecipients()),
                    PaperAdventure.LEGACY_SECTION_UXRC.deserialize(event.getMessage()),
                    event.isCancelled()
                );
            },
            // continuing from AsyncPlayerChatEvent and PlayerChatEvent
            event -> {
                this.processModern(
                    legacyRenderer(event.getFormat()),
                    this.viewersFromLegacy(event.getRecipients()),
                    PaperAdventure.LEGACY_SECTION_UXRC.deserialize(event.getMessage()),
                    event.isCancelled()
                );
            },
            // no legacy events called, all nice and fresh!
            () -> {
                this.processModern(
                    ChatRenderer.defaultRenderer(),
                    new LazyChatAudienceSet(this.server),
                    Component.text(this.message).replaceText(URL_REPLACEMENT_CONFIG),
                    false
                );
            }
        );
    }

    private Set<Audience> viewersFromLegacy(final Set<Player> recipients) {
        if (recipients instanceof LazyPlayerSet && ((LazyPlayerSet) recipients).isLazy()) {
            return new LazyChatAudienceSet(this.server);
        }
        final HashSet<Audience> viewers = new HashSet<>(recipients);
        viewers.add(this.server.console);
        return viewers;
    }

    @SuppressWarnings("deprecation")
    private void processingLegacyFirst(
        final Consumer<AsyncPlayerChatEvent> continueAfterAsync,
        final Consumer<PlayerChatEvent> continueAfterAsyncAndSync,
        final Runnable modernOnly
    ) {
        final boolean listenersOnAsyncEvent = anyListeners(AsyncPlayerChatEvent.getHandlerList());
        final boolean listenersOnSyncEvent = anyListeners(PlayerChatEvent.getHandlerList());
        if (listenersOnAsyncEvent || listenersOnSyncEvent) {
            final CraftPlayer player = this.player.getBukkitEntity();
            final AsyncPlayerChatEvent ae = new AsyncPlayerChatEvent(this.async, player, this.message, new LazyPlayerSet(this.server));
            post(ae);
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
                continueAfterAsyncAndSync.accept(se);
            } else {
                continueAfterAsync.accept(ae);
            }
        } else {
            modernOnly.run();
        }
    }

    private void processModern(final ChatRenderer renderer, final Set<Audience> viewers, final Component message, final boolean cancelled) {
        final AsyncChatEvent ae = this.createAsync(renderer, viewers, message);
        ae.setCancelled(cancelled); // propagate cancelled state
        post(ae);
        final boolean listenersOnSyncEvent = anyListeners(ChatEvent.getHandlerList());
        if (listenersOnSyncEvent) {
            this.continueWithSyncFromWhereAsyncLeftOff(ae);
        } else {
            this.complete(ae);
        }
    }

    private void continueWithSyncFromWhereAsyncLeftOff(final AsyncChatEvent ae) {
        this.queueIfAsyncOrRunImmediately(new Waitable<Void>() {
            @Override
            protected Void evaluate() {
                final ChatEvent se = ChatProcessor.this.createSync(ae.renderer(), ae.viewers(), ae.message());
                se.setCancelled(ae.isCancelled()); // propagate cancelled state
                post(se);
                ChatProcessor.this.complete(se);
                return null;
            }
        });
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

    private AsyncChatEvent createAsync(final ChatRenderer renderer, final Set<Audience> viewers, final Component message) {
        return new AsyncChatEvent(this.async, this.player.getBukkitEntity(), viewers, renderer, message, this.originalMessage);
    }

    private ChatEvent createSync(final ChatRenderer renderer, final Set<Audience> viewers, final Component message) {
        return new ChatEvent(this.player.getBukkitEntity(), viewers, renderer, message, this.originalMessage);
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

    private static void post(final Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    private static boolean anyListeners(final HandlerList handlers) {
        return handlers.getRegisteredListeners().length > 0;
    }
}
