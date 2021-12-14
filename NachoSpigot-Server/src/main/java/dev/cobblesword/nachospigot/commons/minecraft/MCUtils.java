package dev.cobblesword.nachospigot.commons.minecraft;

import net.minecraft.server.MinecraftServer;

/**
 * @author Sculas
 */
public class MCUtils {
    public static void ensureMain(Runnable runnable) {
        MinecraftServer.getServer().processQueue.add(runnable);
    }
}
