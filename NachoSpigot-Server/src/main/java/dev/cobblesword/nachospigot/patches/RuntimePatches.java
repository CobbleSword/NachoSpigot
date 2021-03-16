package dev.cobblesword.nachospigot.patches;

import com.comphenix.protocol.injector.netty.InjectionFactory;
import com.comphenix.protocol.injector.netty.ProtocolInjector;
import com.comphenix.protocol.injector.server.TemporaryPlayerFactory;
import dev.cobblesword.nachospigot.Nacho;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.PluginClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class RuntimePatches {

    private static final Logger logger = Bukkit.getLogger();

    public static void applyViaVersionBlockPatch() {
        try {
            if(
                    Bukkit.getPluginManager().isPluginEnabled("ViaVersion") &&
                            !Nacho.get().getConfig().serverBrandName.contains("paper") &&
                            !Nacho.get().getConfig().serverBrandName.contains("taco") &&
                            !Nacho.get().getConfig().serverBrandName.contains("torch")
            ) {
                logger.info("Patching block placement, please wait.");
                ClassLoader cl = Bukkit.getPluginManager().getPlugin("ViaVersion").getClass().getClassLoader();
                // This was the line of code I'm representing here in Reflection.
                // storeListener(new PaperPatch(plugin)).register();
                // Fun, isn't it?
                Class<?> bukkitViaLoader = Class.forName("us.myles.ViaVersion.bukkit.platform.BukkitViaLoader", false, cl);
                Method storeListener = Arrays
                        .stream(bukkitViaLoader.getDeclaredMethods())
                        .filter(method -> method.getName().equalsIgnoreCase("storeListener"))
                        .findFirst()
                        .orElse(null);
                if(storeListener == null) throw new IllegalStateException("storeListener was not found in the BukkitViaLoader class");
                Class<?> paperPatchClass = Class.forName("us.myles.ViaVersion.bukkit.listeners.protocol1_9to1_8.PaperPatch", true, cl);
                Field pluginField = bukkitViaLoader.getDeclaredField("plugin");
                pluginField.setAccessible(true);
                Object plugin = pluginField.get(bukkitViaLoader); // fix this final modifier error
                Object paperPatch = paperPatchClass.getDeclaredConstructor().newInstance(plugin);
                Object listener = storeListener.invoke(bukkitViaLoader, paperPatch);
                Method register = listener.getClass().getDeclaredMethod("register");
                register.invoke(listener);
                logger.info("Successfully patched block placement!");
            }
        } catch (Exception e) {
            logger.warning("Could not patch block placement.");
            e.printStackTrace();
        }
    }

    public static void applyProtocolLibPatch() {
        try {
            if(
                    Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") &&
                            Nacho.get().getConfig().patchProtocolLib
            ) {
                logger.info("Patching ProtocolLib, please wait.");
                ClassLoader cl = Bukkit.getPluginManager().getPlugin("ProtocolLib").getClass().getClassLoader();
                // Big thanks to MacacoLew!
                // Note to self: I am never doing this EVER again. This is not going to be fun.

                // Requirements
                Class<?> protocolLib = Class.forName("com.comphenix.protocol.ProtocolLib", false, cl);

                Field protocolManagerField = protocolLib.getDeclaredField("protocolManager");
                protocolManagerField.setAccessible(true);
                Object protocolManager = protocolManagerField.get(protocolLib);

                Field nettyInjectorField = protocolManager.getClass().getDeclaredField("nettyInjector");
                nettyInjectorField.setAccessible(true);
                Object nettyInjector = nettyInjectorField.get(protocolManager);

                Field networkManagersField = nettyInjector.getClass().getDeclaredField("networkManagers");
                networkManagersField.setAccessible(true);
                List<Object> networkManagers = (List<Object>) networkManagersField.get(nettyInjector); // don't ask

                Field injectionFactoryField = nettyInjector.getClass().getDeclaredField("injectionFactory");
                injectionFactoryField.setAccessible(true);
                InjectionFactory injectionFactory = (InjectionFactory) injectionFactoryField.get(nettyInjector);

                Field temporaryPlayerFactoryField = nettyInjector.getClass().getDeclaredField("playerFactory");
                temporaryPlayerFactoryField.setAccessible(true);
                TemporaryPlayerFactory temporaryPlayerFactory = (TemporaryPlayerFactory) temporaryPlayerFactoryField.get(nettyInjector);

                Field endInitProtocolField = nettyInjector.getClass().getDeclaredField("endInitProtocol");
                endInitProtocolField.setAccessible(true);

                // Replacer
                ChannelInboundHandler replacer = new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(final Channel channel) {
                        try {
                            synchronized (networkManagers) {
                                channel.eventLoop().submit(() -> injectionFactory.fromChannel(channel, (ProtocolInjector)nettyInjector, temporaryPlayerFactory).inject());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };

                // Replacing
                endInitProtocolField.set(nettyInjector, replacer);

                logger.info("Successfully patched ProtocolLib!");
            }
        } catch (Exception e) {
            logger.warning("Could not patch ProtocolLib.");
            e.printStackTrace();
        }
    }

}
