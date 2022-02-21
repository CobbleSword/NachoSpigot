package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutChat implements Packet<PacketListenerPlayOut> {

    private IChatBaseComponent message;
    public net.kyori.adventure.text.Component adventure$message; // Paper
    public net.md_5.bungee.api.chat.BaseComponent[] components; // Spigot
    private byte b;

    public PacketPlayOutChat() {}

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent) {
        this(ichatbasecomponent, (byte) 1);
    }

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent, byte b0) {
        this.message = ichatbasecomponent;
        this.b = b0;
    }

    public IChatBaseComponent getChatComponent()
    {
        return this.message;
    }

    public byte getChatType()
    {
        return b;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.message = serializer.d();
        this.b = serializer.readByte();
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        // Paper start
        if (this.adventure$message != null) {
            serializer.writeComponent(this.adventure$message);
        } else
        // Paper end
        // Spigot start
        if (components != null) {
            //serializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(components)); // Paper - comment, replaced with below
            // Paper start - don't nest if we don't need to so that we can preserve formatting
            if (this.components.length == 1) {
                serializer.writeUtf(net.md_5.bungee.chat.ComponentSerializer.toString(this.components[0]));
            } else {
                serializer.writeUtf(net.md_5.bungee.chat.ComponentSerializer.toString(this.components));
            }
            // Paper end
        } else {
            serializer.writeComponent(this.message);
        }
        // Spigot end
        serializer.writeByte(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public boolean b() {
        return this.b == 1 || this.b == 2;
    }
}
