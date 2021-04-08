package net.minecraft.server;

import java.io.IOException;

public class PacketHandshakingInSetProtocol implements Packet<PacketHandshakingInListener> {

    private int a;
    public String hostname;
    public int port;
    private EnumProtocol d;

    public PacketHandshakingInSetProtocol() {}

    public void readPacketData(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.hostname = packetdataserializer.c(Short.MAX_VALUE); // Spigot
        this.port = packetdataserializer.readUnsignedShort();
        this.d = EnumProtocol.a(packetdataserializer.e());
    }

    public void writePacketData(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.b(this.a);
        packetdataserializer.a(this.hostname);
        packetdataserializer.writeShort(this.port);
        packetdataserializer.b(this.d.a());
    }

    public void processPacket(PacketHandshakingInListener packethandshakinginlistener) {
        packethandshakinginlistener.a(this);
    }

    public EnumProtocol a() {
        return this.d;
    }

    public int b() {
        return this.a;
    }
}
