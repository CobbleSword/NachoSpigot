package dev.cobblesword.nachospigot.patches;

import dev.cobblesword.nachospigot.Nacho;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class RuntimePatches {

    public static void applyViaVersionBlockPatch(Logger logger) {
        try {
            if(
                    Bukkit.getPluginManager().isPluginEnabled("ViaVersion") &&
                            !Nacho.get().getConfig().serverBrandName.contains("paper") &&
                            !Nacho.get().getConfig().serverBrandName.contains("taco") &&
                            !Nacho.get().getConfig().serverBrandName.contains("torch")
            ) {
                logger.warn("Patching block placement, please wait.");
                // This was the line of code I'm representing here in Reflection.
                // storeListener(new PaperPatch(plugin)).register();
                // Fun, isn't it?
                Class<?> bukkitViaLoader = Class.forName("us.myles.ViaVersion.bukkit.platform.BukkitViaLoader");
                Method storeListener = bukkitViaLoader.getMethod("storeListener");
                Class<?> paperPatchClass = Class.forName("us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8.PaperPatch");
                Field pluginField = bukkitViaLoader.getDeclaredField("plugin");
                pluginField.setAccessible(true);
                Object plugin = pluginField.get(bukkitViaLoader);
                Object paperPatch = paperPatchClass.getDeclaredConstructor().newInstance(plugin);
                Object listener = storeListener.invoke(bukkitViaLoader, paperPatch);
                Method register = listener.getClass().getMethod("register");
                register.invoke(listener);
                logger.warn("Successfully patched block placement!");
            }
        } catch (Exception e) {
            logger.warn("Could not patch block placement.");
            e.printStackTrace();
        }
    }

    public static void applyProtocolLibPatch(Logger logger) {
        try {
            if(
                    Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")
            ) {
                logger.warn("Patching ProtocolLib, please wait.");
                // Big thanks to MacacoLew!
//                ChannelInboundHandler replacer = new ChannelInitializer<Channel>() {
//                    @Override
//                    protected void initChannel(final Channel channel) throws Exception {
//                        try {
//                            synchronized (networkManagers) {
//                                // For some reason it needs to be delayed when using netty 4.1.24 (minecraft  1.12) or newer,
//                                // but the delay breaks older minecraft versions
//                                // TODO I see this more as a temporary hotfix than a permanent solution
//
//                                // Check if the netty version is greater than 4.1.24, that's the version bundled with spigot 1.12
//                                NettyVersion ver = NettyVersion.getVersion();
//                                if ((ver.isValid() && ver.isGreaterThan(4,1,24)) ||
//                                        MinecraftVersion.getCurrentVersion().getMinor() >= 12) { // fallback if netty version couldn't be detected
//                                    channel.eventLoop().submit(() ->
//                                            injectionFactory.fromChannel(channel, ProtocolInjector.this, playerFactory).inject());
//                                } else {
//                                    injectionFactory.fromChannel(channel, ProtocolInjector.this, playerFactory).inject();
//                                }
//                            }
//                        } catch (Exception ex) {
//                            reporter.reportDetailed(ProtocolInjector.this, Report.newBuilder(REPORT_CANNOT_INJECT_INCOMING_CHANNEL).messageParam(channel).error(ex));
//                        }
//                    }
//                };
                logger.warn("Successfully patched ProtocolLib!");
            }
        } catch (Exception e) {
            logger.warn("Could not patch ProtocolLib.");
            e.printStackTrace();
        }
    }

}
