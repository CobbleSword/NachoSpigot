package me.elier.util.minecraft;

import org.bukkit.plugin.Plugin;

public class PluginUtils {

    public static int getCitizensBuild(Plugin plugin) {
       return Integer.parseInt(plugin.getDescription().getVersion().split("\\(build ")[1].replace(")", ""));
    }

}
