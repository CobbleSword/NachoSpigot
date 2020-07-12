package org.spigotmc.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

// PaperSpigot start
import org.bukkit.event.Cancellable;
// PaperSpigot end

/**
 * Called when an entity stops riding another entity.
 *
 */
public class EntityDismountEvent extends EntityEvent implements Cancellable // PaperSpigot - implement Cancellable
{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Entity dismounted;

    public EntityDismountEvent(Entity what, Entity dismounted)
    {
        super( what );
        this.dismounted = dismounted;
    }

    public Entity getDismounted()
    {
        return dismounted;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    // PaperSpigot start - implement Cancellable methods
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    // PaperSpigot end
}
