package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutPlayerListHeaderFooter implements Packet<PacketListenerPlayOut> {

    private IChatBaseComponent header;
    private IChatBaseComponent footer;
    // Paper start
    public net.kyori.adventure.text.Component adventure$header;
    public net.kyori.adventure.text.Component adventure$footer;
    // Paper end

    public PacketPlayOutPlayerListHeaderFooter() {}

    public PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent ichatbasecomponent) {
        this.header = ichatbasecomponent;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.header = serializer.d();
        this.footer = serializer.d();
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        // Paper start
        if (this.adventure$header != null && this.adventure$footer != null) {
            serializer.writeComponent(this.adventure$header);
            serializer.writeComponent(this.adventure$footer);
            return;
        }
        // Paper end
        serializer.writeComponent(this.header);
        serializer.writeComponent(this.footer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
