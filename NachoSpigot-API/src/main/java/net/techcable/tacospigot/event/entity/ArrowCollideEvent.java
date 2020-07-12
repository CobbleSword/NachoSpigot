package net.techcable.tacospigot.event.entity;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Called when an arrow collides with an entity
 * <p>
 * This event is called <b>before</b> {@link org.bukkit.event.entity.EntityDamageByEntityEvent}, and cancelling it will allow the arrow to continue flying
 */
public class ArrowCollideEvent extends EntityEvent implements Cancellable {
    private final Entity collidedWith;

    /**
     * Get the entity the arrow collided wit
     *
     * @return the entity
     */
    public Entity getCollidedWith() {
        return collidedWith;
    }

    public ArrowCollideEvent(Arrow what, Entity collidedWith) {
        super(what);
        this.collidedWith = collidedWith;
    }

    /**
     * Get the arrow that collided
     *
     * @return the arrow that collided
     */
    public Arrow getEntity() {
        return (Arrow) super.getEntity();
    }

    private static final HandlerList handlerList = new HandlerList();
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    private boolean cancelled = false;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
