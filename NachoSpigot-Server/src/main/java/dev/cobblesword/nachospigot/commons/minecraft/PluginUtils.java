package dev.cobblesword.nachospigot.commons.minecraft;

import org.bukkit.plugin.Plugin;

/**
 * @author Elierrr
 */
public final class PluginUtils {
    private PluginUtils() {}

    public static int getCitizensBuild(Plugin plugin) {
        try {
            return Integer.parseInt(plugin.getDescription().getVersion().split("\\(build ")[1].replace(")", ""));
        } catch (Throwable ignored) {
            return 2396;
        }
    }
}
