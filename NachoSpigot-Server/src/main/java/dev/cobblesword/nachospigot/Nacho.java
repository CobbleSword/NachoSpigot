package dev.cobblesword.nachospigot;

import dev.cobblesword.nachospigot.commons.FileUtils;
import dev.cobblesword.nachospigot.patches.RuntimePatches;
import dev.cobblesword.nachospigot.protocol.PacketListener;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.defaults.nacho.SetMaxSlotCommand;
import org.bukkit.command.defaults.nacho.SpawnMobCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Nacho {
    private static Nacho INSTANCE;
    private static final File CONFIG_FILE = new File("nacho.json");
    private NachoConfig config;
    public List<PacketListener> packetListeners = new ArrayList<>();

    public Nacho() {
        INSTANCE = this;
        this.config = new NachoConfig();
        while (!CONFIG_FILE.exists()) FileUtils.toFile(this.config, CONFIG_FILE);
        this.config = FileUtils.toObject(CONFIG_FILE, NachoConfig.class);
    }

    public void reloadConfig() {
        this.config = FileUtils.toObject(CONFIG_FILE, NachoConfig.class);
    }

    public static Nacho get() {
        return INSTANCE == null ? new Nacho() : INSTANCE;
    }

    public static boolean exists() {
        return INSTANCE != null;
    }

    public NachoConfig getConfig()
    {
        return config;
    }

    public void registerCommands() {
        SetMaxSlotCommand setMaxSlotCommand = new SetMaxSlotCommand("sms"); //[Nacho-0021] Add setMaxPlayers within Bukkit.getServer() and SetMaxSlot Command
        SpawnMobCommand spawnMobCommand = new SpawnMobCommand("spawnmob");
        MinecraftServer.getServer().server.getCommandMap().register(setMaxSlotCommand.getName(), "ns", setMaxSlotCommand);
        MinecraftServer.getServer().server.getCommandMap().register(spawnMobCommand.getName(), "ns", spawnMobCommand);
    }

    public void registerPacketListener(PacketListener packetListener) {
        this.packetListeners.add(packetListener);
        System.out.println("[NachoSpigot] Register PacketListener @ " + packetListener.getClass().getName());
    }

    public List<PacketListener> getPacketListeners()
    {
        return packetListeners;
    }

    public void applyPatches() {
        // Nacho start - [Nacho-0041] Fix block placement
        RuntimePatches.applyViaVersionBlockPatch();
        // Nacho end
    }
}