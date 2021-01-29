package org.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the locale of the player is changed.
 */
public class PlayerLocaleChangeEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final String newLocale;

    // Paper start
    private final java.util.Locale adventure$locale;
    /**
     * @see Player#locale() // Nacho - use new locale method
     *
     * @return the player's new locale
     */
    public @NotNull java.util.Locale locale() {
        return this.adventure$locale;
    }
    // Paper end

    public PlayerLocaleChangeEvent(final Player player, final String locale) {
        super(player);
        this.newLocale = locale;
        this.adventure$locale = net.kyori.adventure.translation.Translator.parseLocale(newLocale); // Paper
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return the player's new locale
     * @see Player#getLocale()
     * @deprecated in favour of {@link #locale()}
     */
    @NotNull
    @Deprecated // Paper
    public String getLocale() {
        return newLocale;
    }
}