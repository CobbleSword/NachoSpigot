package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a player joins a server
 */
public class PlayerJoinEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    // Paper start
    private net.kyori.adventure.text.Component joinMessage;
    public PlayerJoinEvent(@NotNull final Player playerJoined, @Nullable final net.kyori.adventure.text.Component joinMessage) {
        super(playerJoined);
        this.joinMessage = joinMessage;
    }
<<<<<<< found

    public PlayerJoinEvent(final Player playerJoined, final String joinMessage) {
||||||| expected

    public PlayerJoinEvent(@NotNull final Player playerJoined, @Nullable final String joinMessage) {
=======

    @Deprecated // Paper end
    public PlayerJoinEvent(@NotNull final Player playerJoined, @Nullable final String joinMessage) {
>>>>>>> replacement
        super(playerJoined);
        this.joinMessage = joinMessage != null ? org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().deserialize(joinMessage) : null; // Paper end
    }

    // Paper start
    /**
     * Gets the join message to send to all online players
     *
     * @return string join message. Can be null
     */
    public @Nullable net.kyori.adventure.text.Component joinMessage() {
        return this.joinMessage;
    }

    /**
     * Sets the join message to send to all online players
     *
     * @param joinMessage join message. If null, no message will be sent
     */
    public void joinMessage(@Nullable net.kyori.adventure.text.Component joinMessage) {
        this.joinMessage = joinMessage;
    }
    // Paper end

    /**
     * Gets the join message to send to all online players
     *
     * @return string join message
     * @deprecated in favour of {@link #joinMessage()}
     */
    @Deprecated // Paper
    public String getJoinMessage() {
        return this.joinMessage == null ? null : org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().serialize(this.joinMessage); // Paper
    }

    /**
     * Sets the join message to send to all online players
     *
     * @param joinMessage join message
     * @deprecated in favour of {@link #joinMessage(net.kyori.adventure.text.Component)}
<<<<<<< found
     */
    public void setJoinMessage(String joinMessage) {
||||||| expected
     */
    public void setJoinMessage(@Nullable String joinMessage) {
=======
     */
    @Deprecated // Paper
    public void setJoinMessage(@Nullable String joinMessage) {
        this.joinMessage = joinMessage != null ? org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().deserialize(joinMessage) : null; // Paper
>>>>>>> replacement
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
