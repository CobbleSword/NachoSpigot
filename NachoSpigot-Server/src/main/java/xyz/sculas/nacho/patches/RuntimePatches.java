package xyz.sculas.nacho.patches;

import javassist.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class RuntimePatches {

    private static final Logger logger = Bukkit.getLogger();

    public static CompletableFuture<Boolean> applyCitizensPatch(Plugin plugin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Patching Citizens, please wait.");

                ClassPool pool = ClassPool.getDefault();
                pool.insertClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));
                pool.importPackage("io.netty.channel.ChannelMetadata");

                CtClass emptyChannel = pool.get("net.citizensnpcs.nms.v1_8_R3.network.EmptyChannel");
                if (emptyChannel.isFrozen()) emptyChannel.defrost();

                CtMethod metaData = emptyChannel.getDeclaredMethods("metadata")[0];
                metaData.setBody("{ return new ChannelMetadata(true); }");

                emptyChannel.toClass(plugin.getClass().getClassLoader(), plugin.getClass().getProtectionDomain());

                logger.info("Successfully patched Citizens!");
                return true;
            } catch (Exception e) {
                logger.warning("Could not patch Citizens.");
                e.printStackTrace();
            }
            return false;
        });
    }
}