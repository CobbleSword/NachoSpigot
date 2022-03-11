package dev.cobblesword.nachospigot;

import dev.cobblesword.nachospigot.commands.KnockbackCommand;
import dev.cobblesword.nachospigot.hitdetection.LagCompensator;
import dev.cobblesword.nachospigot.protocol.MovementListener;
import me.elier.nachospigot.config.NachoConfig;
import xyz.sculas.nacho.anticrash.AntiCrash;
import xyz.sculas.nacho.async.AsyncExplosions;
import dev.cobblesword.nachospigot.protocol.PacketListener;
import net.minecraft.server.MinecraftServer;
import dev.cobblesword.nachospigot.commands.SetMaxSlotCommand;
import dev.cobblesword.nachospigot.commands.SpawnMobCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;
import java.util.Set;

public class Nacho {
    public static final Logger LOGGER = LogManager.getLogger(Nacho.class);
    private static Nacho INSTANCE;

    private final Set<PacketListener> packetListeners = Sets.newConcurrentHashSet();
    private final Set<MovementListener> movementListeners = Sets.newConcurrentHashSet();

    private final LagCompensator lagCompensator;

    public Nacho() {
        INSTANCE = this;

        AsyncExplosions.initExecutor(NachoConfig.useFixedPoolForTNT, NachoConfig.fixedPoolSize);

        lagCompensator = new LagCompensator();

        if(NachoConfig.enableAntiCrash) {
            this.packetListeners.add(new AntiCrash());
        }
    }

    public static Nacho get() {
        return INSTANCE == null ? new Nacho() : INSTANCE;
    }

    public void registerCommands() {
        SetMaxSlotCommand setMaxSlotCommand = new SetMaxSlotCommand("sms"); //[Nacho-0021] Add setMaxPlayers within Bukkit.getServer() and SetMaxSlot Command
        SpawnMobCommand spawnMobCommand = new SpawnMobCommand("spawnmob");
        KnockbackCommand knockbackCommand = new KnockbackCommand("kb");
        MinecraftServer.getServer().server.getCommandMap().register(setMaxSlotCommand.getName(), "ns", setMaxSlotCommand);
        MinecraftServer.getServer().server.getCommandMap().register(spawnMobCommand.getName(), "ns", spawnMobCommand);
        MinecraftServer.getServer().server.getCommandMap().register(knockbackCommand.getName(), "ns", knockbackCommand);
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

    public LagCompensator getLagCompensator() {
        return lagCompensator;
    }
}
