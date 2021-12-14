package dev.cobblesword.nachospigot.commons.minecraft;

import org.bukkit.plugin.Plugin;

/**
 * @author Elierrr
 */
public class PluginUtils {
    public static int getCitizensBuild(Plugin plugin) {
        return Integer.parseInt(plugin.getDescription().getVersion().split("\\(build ")[1].replace(")", ""));
    }
}
