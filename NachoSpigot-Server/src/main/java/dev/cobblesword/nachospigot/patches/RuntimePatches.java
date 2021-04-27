package dev.cobblesword.nachospigot.patches;

import javassist.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class RuntimePatches {

    private static final Logger logger = Bukkit.getLogger();

    public static CompletableFuture<Boolean> applyProtocolLibPatch(Plugin plugin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Patching ProtocolLib, please wait.");

                // TODO Remove this message if you know a better way for this!
                logger.warning(
                        "This patch is a nasty way to patch ProtocolLib, since if an " +
                                "breaking update is done to the ProtocolInjector this WILL break ProtocolLib. " +
                                "Please do note that the latest update on the ProtocolInjector class was made " +
                                "before 2018, so there shouldn't be anything to worry about. " +
                                "If you do know how to fix this though, please make a PR at: https://github.com/Sculas/NachoSpigot"
                );

                ClassPool pool = ClassPool.getDefault();
                pool.insertClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));

                CtClass defaultProtocolInjector = pool.get("com.comphenix.protocol.injector.netty.ProtocolInjector$1");
                if (defaultProtocolInjector.isFrozen()) {
                    defaultProtocolInjector.defrost();
                }

                CtClass clazz = pool.makeClass(CraftServer.class.getClassLoader().getResourceAsStream("protpatch.class"));
                clazz.replaceClassName(clazz.getName(), "com.comphenix.protocol.injector.netty.ProtocolInjector$1");
                clazz.toClass(plugin.getClass().getClassLoader(), plugin.getClass().getProtectionDomain());

                logger.info("Successfully patched ProtocolLib!");
                return true;
            } catch (Exception e) {
                logger.warning("Could not patch ProtocolLib.");
                e.printStackTrace();
            }
            return false;
        });
    }

    public static CompletableFuture<Boolean> applyCitizensPatch(Plugin plugin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Patching Citizens, please wait.");

                ClassPool pool = ClassPool.getDefault();
                pool.insertClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));
                pool.importPackage("io.netty.channel.ChannelMetadata");

                CtClass emptyChannel = pool.get("net.citizensnpcs.nms.v1_8_R3.network.EmptyChannel");
                if (emptyChannel.isFrozen()) {
                    emptyChannel.defrost();
                }

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

    private static Method getMethod(Class<?> clazz, String methodName) {
         return Arrays
                 .stream(clazz.getDeclaredMethods())
                 .filter(method -> method.getName().equalsIgnoreCase(methodName))
                 .findFirst()
                 .orElse(null);
    }

}
