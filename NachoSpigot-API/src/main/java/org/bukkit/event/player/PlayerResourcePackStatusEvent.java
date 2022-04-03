package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Called when a player takes action on a resource pack request sent via
 * {@link Player#setResourcePack(java.lang.String)}.
 */
public class PlayerResourcePackStatusEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final String hash; // TacoSpigot
    private final Status status;

    @Deprecated // TacoSpigot
    public PlayerResourcePackStatusEvent(final Player who, Status resourcePackStatus) {
        super(who);
        this.hash = null; // TacoSpigot
        this.status = resourcePackStatus;
    }

    // TacoSpigot start
    public PlayerResourcePackStatusEvent(final Player who, Status resourcePackStatus, String hash) {
        super(who);
        this.hash = hash;
        this.status = resourcePackStatus;
    }

    public String getHash() {
        return this.hash;
    }
    // TacoSpigot end

    /**
     * Gets the status of this pack.
     *
     * @return the current status
     */
    public Status getStatus() {
        return status;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Status of the resource pack.
     */
    public enum Status {

        /**
         * The resource pack has been successfully downloaded and applied to the
         * client.
         */
        SUCCESSFULLY_LOADED,
        /**
         * The client refused to accept the resource pack.
         */
        DECLINED,
        /**
         * The client accepted the pack, but download failed.
         */
        FAILED_DOWNLOAD,
        /**
         * The client accepted the pack and is beginning a download of it.
         */
        ACCEPTED
    }
}
