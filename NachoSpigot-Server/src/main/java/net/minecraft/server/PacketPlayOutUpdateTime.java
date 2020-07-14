package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutUpdateTime implements Packet<PacketListenerPlayOut> {
    private long a;
    private long b;

    public PacketPlayOutUpdateTime() {
    }

    public PacketPlayOutUpdateTime(long var1, long var3, boolean var5) {
        this.a = var1;
        this.b = var3;
        if (!var5) {
            this.b = -this.b;
            if (this.b == 0L) {
                this.b = -1L;
            }
        }

        this.a = this.a % 192000; //
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readLong();
        this.b = var1.readLong();
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeLong(this.a);
        var1.writeLong(this.b);
    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }
}
