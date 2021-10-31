package xyz.sculas.nacho.patches;

import javassist.*;
import me.elier.nachospigot.config.NachoConfig;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class RuntimePatches 
{

    private static final Logger logger = Bukkit.getLogger();

    public static void applyViaVersionBlockPatch() {
        try {
            final String name = NachoConfig.serverBrandName.toLowerCase();
            if(
                    Bukkit.getPluginManager().isPluginEnabled("ViaVersion") &&
                            !name.contains("paper") &&
                            !name.contains("taco") &&
                            !name.contains("torch")
            ) {
                logger.info("Patching block placement, please wait.");
                ClassLoader cl = Bukkit.getPluginManager().getPlugin("ViaVersion").getClass().getClassLoader();
                
                String viaVersionPackage = "us.myles.ViaVersion."; // old
                try {
                	Class.forName("com.viaversion.viaversion.api.Via", true, cl); // Checking for the new ViaVersion version
                	viaVersionPackage = "com.viaversion.viaversion."; // new
                } catch (ClassNotFoundException ignore) {
                    logger.info("Using an old ViaVersion version, please update!");
                }
                // This was the line of code I'm representing here in Reflection.
                // Via.getManager().getLoader().storeListener(new PaperPatch(plugin)).register();
                // Fun, isn't it?
                Class<?> via = Class.forName(viaVersionPackage + "api.Via", true, cl);
                Method getManager = via.getMethod("getManager");
                Object viaManager = getManager.invoke(null);
                Class<?> viaManagerClass = viaManager.getClass();
                Method getLoader = getMethod(viaManagerClass, "getLoader");
                if(getLoader == null)
                    throw new IllegalStateException("getLoader was not found in the ViaManager class");
                Object bukkitViaLoader = getLoader.invoke(viaManager);
                Class<?> bukkitViaLoaderClass = bukkitViaLoader.getClass();
                Method storeListener = getMethod(bukkitViaLoaderClass, "storeListener");
                if(storeListener == null)
                    throw new IllegalStateException("storeListener was not found in the BukkitViaLoader class");
                Class<?> paperPatchClass = Class.forName(viaVersionPackage + "bukkit.listeners.protocol1_9to1_8.PaperPatch", true, cl);
                Class<?> viaVersionPlugin = Class.forName(viaVersionPackage + "ViaVersionPlugin", true, cl);
                Method getInstance = viaVersionPlugin.getDeclaredMethod("getInstance");
                Object plugin = getInstance.invoke(viaVersionPlugin);
                Object paperPatch = paperPatchClass.getDeclaredConstructor(Plugin.class).newInstance(plugin);
                Object listener = storeListener.invoke(bukkitViaLoader, paperPatch);
                Method register = getMethod(listener.getClass().getSuperclass(), "register");
                if(register == null)
                    throw new IllegalStateException("register was not found in the Listener class");
                register.invoke(listener);
                logger.info("Successfully patched block placement!");
            }
        } catch (Exception e) {
            logger.warning("Could not patch block placement. Setting brand name to TacoSpigot to make it work properly.");
            NachoConfig.serverBrandName = "TacoSpigot";
            e.printStackTrace();
        }
    }

    public static CompletableFuture<Boolean> applyProtocolLibPatch(Plugin plugin) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Patching ProtocolLib, please wait.");

                try {
                    String[] tmp = plugin.getDescription().getVersion().split("\\.");
                    if (Integer.parseInt(tmp[0]) <= 4 && Integer.parseInt(tmp[1]) <= 6) {
                        logger.warning(
                                "Please update to ProtocolLib version 4.7.0 or higher!\n" +
                                        "In version 4.6.0 and lower, we have to do a nasty fix to make it work.\n" +
                                        "So.. once again, please update!\n" +
                                        "You can update with this link: " +
                                        "https://github.com/dmulloy2/ProtocolLib/releases/latest\n" +
                                        "Sleeping for 10s so this message can be read."
                        );
                        Thread.sleep(10000);
                    } else {
                        logger.info("It seems that you are using ProtocolLib version 4.7 or higher, which is supported!");
                        logger.info("No need to patch ProtocolLib, skipping.");
                        return true;
                    }
                } catch (Exception ignored) {}

                ClassPool pool = ClassPool.getDefault();
                pool.insertClassPath(new LoaderClassPath(plugin.getClass().getClassLoader()));

                CtClass defaultProtocolInjector = pool.get("com.comphenix.protocol.injector.netty.ProtocolInjector$1");
                if (defaultProtocolInjector.isFrozen()) defaultProtocolInjector.defrost();


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

    private static Method getMethod(Class<?> clazz, String methodName) {
         return Arrays
                 .stream(clazz.getDeclaredMethods())
                 .filter(method -> method.getName().equalsIgnoreCase(methodName))
                 .findFirst()
                 .orElse(null);
    }

}
