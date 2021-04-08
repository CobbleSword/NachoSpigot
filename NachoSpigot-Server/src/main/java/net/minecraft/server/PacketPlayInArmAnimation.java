package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInArmAnimation implements Packet<PacketListenerPlayIn> {

    public long timestamp; // Spigot

    public PacketPlayInArmAnimation() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        timestamp = System.currentTimeMillis(); // Spigot
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {}

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }
}
