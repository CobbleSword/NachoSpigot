package dev.cobblesword.nachospigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerIllegalBehaviourEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final IllegalType illegalType;
    private Boolean cancel;

    public PlayerIllegalBehaviourEvent(final Player player, IllegalType illegalType) {
        super(player);
        this.illegalType = illegalType;
        this.cancel = false;
    }

    public IllegalType getIllegalType()
    {
        return illegalType;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum IllegalType {
        CREATIVE_ACTION_NOT_IN_CREATIVE
    }
}