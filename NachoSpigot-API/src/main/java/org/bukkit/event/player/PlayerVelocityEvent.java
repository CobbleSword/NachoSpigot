package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

/**
 * Called when the velocity of a player changes.
 */
public class PlayerVelocityEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private Vector velocity;
    private final boolean fromAttack;

    public PlayerVelocityEvent(final Player player, final Vector velocity, final boolean fromPlayerBeingAttacked) {
        super(player);
        this.velocity = velocity;
        this.fromAttack = fromPlayerBeingAttacked;
    }

    public PlayerVelocityEvent(final Player player, final Vector velocity) {
        this(player, velocity, false);
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets the velocity vector that will be sent to the player
     *
     * @return Vector the player will get
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * Sets the velocity vector that will be sent to the player
     *
     * @param velocity The velocity vector that will be sent to the player
     */
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     *
     * @return Whether this velocity change was triggered due to the player being attacked by another player.
     */
    public boolean fromPlayerBeingAttacked(){
        return fromAttack;
    }
}
