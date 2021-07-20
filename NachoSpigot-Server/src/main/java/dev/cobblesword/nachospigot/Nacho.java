package dev.cobblesword.nachospigot;

import xyz.sculas.nacho.anticrash.AntiCrash;
import dev.cobblesword.nachospigot.commons.FileUtils;
import xyz.sculas.nacho.async.AsyncExplosions;
import xyz.sculas.nacho.patches.RuntimePatches;
import dev.cobblesword.nachospigot.protocol.PacketListener;
import dev.cobblesword.nachospigot.protocol.MovementListener;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.defaults.nacho.SetMaxSlotCommand;
import org.bukkit.command.defaults.nacho.SpawnMobCommand;

import com.google.common.collect.Sets;
import java.util.Set;
import java.io.File;

public class Nacho {

    private static Nacho INSTANCE;

    private static final File CONFIG_FILE = new File("nacho.json");
    private NachoConfig config;

    private final Set<PacketListener> packetListeners = Sets.newConcurrentHashSet();
    private final Set<MovementListener> movementListeners = Sets.newConcurrentHashSet();

    public Nacho() {
        INSTANCE = this;

        this.config = new NachoConfig();
        while (!CONFIG_FILE.exists()) FileUtils.toFile(this.config, CONFIG_FILE);
        this.config = FileUtils.toObject(CONFIG_FILE, NachoConfig.class);
        assert this.config != null;

        AsyncExplosions.initExecutor(config.useFixedPoolForTNT, config.fixedPoolSize);

        if(this.config.enableAntiCrash) {
            System.out.println("[NS-AntiCrash] Activating Anti Crash.");
            Nacho.get().registerPacketListener(new AntiCrash());
            System.out.println("[NS-AntiCrash] Activated Anti Crash.");
        }
    }

    public void reloadConfig() {
        this.config = FileUtils.toObject(CONFIG_FILE, NachoConfig.class);
    }

    public static Nacho get() {
        return INSTANCE == null ? new Nacho() : INSTANCE;
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
    }

    public void unregisterPacketListener(PacketListener packetListener) {
        this.packetListeners.remove(packetListener);
    }

    public Set<PacketListener> getPacketListeners() { return packetListeners; }

    public void registerMovementListener(MovementListener movementListener) {
        this.movementListeners.add(movementListener);
    }

    public void unregisterMovementListener(MovementListener movementListener) {
        this.movementListeners.remove(movementListener);
    }

    public Set<MovementListener> getMovementListeners() { return movementListeners; }

    public void applyPatches() {
        // Nacho start - [Nacho-0041] Fix block placement
        RuntimePatches.applyViaVersionBlockPatch();
        // Nacho end
    }
}
