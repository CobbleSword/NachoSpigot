//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutWorldEvent implements Packet<PacketListenerPlayOut> {
    public int a;
    private BlockPosition b;
    public int c;
    public boolean d;

    public PacketPlayOutWorldEvent() {
    }

    public PacketPlayOutWorldEvent(int var1, BlockPosition var2, int var3, boolean var4) {
        this.a = var1;
        this.b = var2;
        this.c = var3;
        this.d = var4;
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readInt();
        this.b = var1.c();
        this.c = var1.readInt();
        this.d = var1.readBoolean();
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeInt(this.a);
        var1.a(this.b);
        var1.writeInt(this.c);
        var1.writeBoolean(this.d);
    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }
}
