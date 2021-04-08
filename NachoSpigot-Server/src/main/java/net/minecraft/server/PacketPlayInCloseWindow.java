package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInCloseWindow implements Packet<PacketListenerPlayIn> {

    private int id;

    public PacketPlayInCloseWindow() {}

    // CraftBukkit start
    public PacketPlayInCloseWindow(int id) {
        this.id = id;
    }
    // CraftBukkit end


    public int getId()
    {
        return id;
    }

    public void processPacket(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public void readPacketData(PacketDataSerializer packetdataserializer) throws IOException {
        this.id = packetdataserializer.readByte();
    }

    public void writePacketData(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.id);
    }
}
