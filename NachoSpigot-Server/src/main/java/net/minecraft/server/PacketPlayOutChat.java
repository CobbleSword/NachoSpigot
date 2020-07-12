package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutChat implements Packet<PacketListenerPlayOut> {

    private IChatBaseComponent a;
    public net.md_5.bungee.api.chat.BaseComponent[] components; // Spigot
    private byte b;

    public PacketPlayOutChat() {}

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent) {
        this(ichatbasecomponent, (byte) 1);
    }

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent, byte b0) {
        this.a = ichatbasecomponent;
        this.b = b0;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.d();
        this.b = packetdataserializer.readByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        // Spigot start
        if (components != null) {
            //packetdataserializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(components)); // Paper - comment, replaced with below
            // Paper start - don't nest if we don't need to so that we can preserve formatting
            if (this.components.length == 1) {
                packetdataserializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(this.components[0]));
            } else {
                packetdataserializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(this.components));
            }
            // Paper end
        } else {
            packetdataserializer.a(this.a);
        }
        // Spigot end
        packetdataserializer.writeByte(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public boolean b() {
        return this.b == 1 || this.b == 2;
    }
}
