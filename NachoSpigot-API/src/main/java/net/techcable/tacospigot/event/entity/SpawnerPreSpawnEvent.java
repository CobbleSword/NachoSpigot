package net.techcable.tacospigot.event.entity;

import com.google.common.base.Preconditions;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import static com.google.common.base.Preconditions.*;

/**
 * Fired before a mob spawns and entity data as calculated.
 * <p>This is a more preformat alternative to {@link org.bukkit.event.entity.SpawnerSpawnEvent}</p>
 */
public class SpawnerPreSpawnEvent extends Event implements Cancellable {

    private final Location location;
    private final EntityType spawnedType;

    public SpawnerPreSpawnEvent(Location location, EntityType spawnedType) {
        this.location = checkNotNull(location, "Null location").clone(); // Defensive copy
        this.spawnedType = checkNotNull(spawnedType, "Null spawned type");
    }

    /**
     * Get the location of the spawner where the entity is being spawned
     *
     * @return the spawner's location
     */
    public Location getLocation() {
        return location.clone(); // Defensive copy
    }

    /**
     * Get the type of entity being spawned
     *
     * @return the type being spawned
     */
    public EntityType getSpawnedType() {
        return spawnedType;
    }

    // Cancellable

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    // handlers

    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
