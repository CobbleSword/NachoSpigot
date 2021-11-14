package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutPlayerListHeaderFooter implements Packet<PacketListenerPlayOut> {

    public net.md_5.bungee.api.chat.BaseComponent[] header, footer; // Paper

    private IChatBaseComponent a;
    private IChatBaseComponent b;

    public PacketPlayOutPlayerListHeaderFooter() {}

    public PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent ichatbasecomponent) {
        this.a = ichatbasecomponent;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.a = serializer.d();
        this.b = serializer.d();
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        // Paper start
        if (this.header != null) {
            serializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(this.header));
        } else {
            serializer.a(this.a);
        }

        if (this.footer != null) {
            serializer.a(net.md_5.bungee.chat.ComponentSerializer.toString(this.footer));
        } else {
            serializer.a(this.b);
        }
        // Paper end
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    // PaperSpigot start - fix compile error
    /*
    public void a(PacketListener packetlistener) {
        this.a((PacketListenerPlayOut) packetlistener);
    }
    */
    // PaperSpigot end
}
