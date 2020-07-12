package org.bukkit.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerInitialSpawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private Location spawnLocation;

    public PlayerInitialSpawnEvent(final Player player, final Location spawnLocation) {
        super(player);
        this.spawnLocation = spawnLocation;
    }

    /**
     * Gets the current spawn location
     *
     * @return Location current spawn location
     */
    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    /**
     * Sets the new spawn location
     *
     * @param spawnLocation new location for the spawn
     */
    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}