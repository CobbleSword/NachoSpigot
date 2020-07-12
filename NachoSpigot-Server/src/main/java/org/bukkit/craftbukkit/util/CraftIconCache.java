package org.bukkit.craftbukkit.util;

import org.bukkit.util.CachedServerIcon;

public class CraftIconCache implements CachedServerIcon {
    public final String value;

    public String getData() { return value; } // Spigot
    public CraftIconCache(final String value) {
        this.value = value;
    }
}
