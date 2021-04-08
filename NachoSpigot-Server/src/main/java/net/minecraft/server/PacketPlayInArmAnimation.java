package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInArmAnimation implements Packet<PacketListenerPlayIn> {

    public long timestamp; // Spigot

    public PacketPlayInArmAnimation() {}

    public void readPacketData(PacketDataSerializer packetdataserializer) throws IOException {
        timestamp = System.currentTimeMillis(); // Spigot
    }

    public void writePacketData(PacketDataSerializer packetdataserializer) throws IOException {}

    public void processPacket(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }
}
