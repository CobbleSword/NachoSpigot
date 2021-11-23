package net.minecraft.server;

import java.io.IOException;

public class PacketLoginOutDisconnect implements Packet<PacketLoginOutListener> {
    private IChatBaseComponent a;

    public PacketLoginOutDisconnect() {
    }

    public PacketLoginOutDisconnect(IChatBaseComponent var1) {
        this.a = var1;
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.d();
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.a(this.a);
    }

    public void a(PacketLoginOutListener var1) {
        var1.a(this);
    }
}