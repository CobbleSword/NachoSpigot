package org.bukkit.craftbukkit.chunkio;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;

import java.util.List;

public class QueuedChunkPacket implements Runnable {
    private final List<EntityPlayer> players;
    private final List<? extends Packet> packets;

    public QueuedChunkPacket(final List<EntityPlayer> players, final List<? extends Packet> packets) {
        this.players = players;
        this.packets = packets;
    }

    @Override
    public void run() {
        for (Packet packet : this.packets) {
            for(EntityPlayer player : this.players) {
                if (player.playerConnection.isDisconnected()) continue;
                player.playerConnection.sendPacket(packet);
            }
        }
    }
}