package dev.cobblesword.nachospigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerEvent;

public class PlayerIllegalBehaviourEvent extends PlayerEvent 
{
    private static final HandlerList handlers = new HandlerList();
    private IllegalType illegalType;

    public PlayerIllegalBehaviourEvent(final Player player, IllegalType illegalType)
    {
        super(player);

        this.illegalType = illegalType;
    }

    public IllegalType getIllegalType()
    {
        return illegalType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public enum IllegalType
    {
        CREATIVE_ACTION_NOT_IN_CREATIVE
    }
}