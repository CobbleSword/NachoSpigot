package io.papermc.paper.event.player;

import java.util.Set;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event fired when a {@link Player} sends a chat message to the server.
 */
public final class AsyncChatEvent extends AbstractChatEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public AsyncChatEvent(final boolean async, final @NotNull Player player, final @NotNull Set<Audience> viewers, final @NotNull ChatRenderer renderer, final @NotNull Component message, final @NotNull Component originalMessage) {
        super(async, player, viewers, renderer, message, originalMessage);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
