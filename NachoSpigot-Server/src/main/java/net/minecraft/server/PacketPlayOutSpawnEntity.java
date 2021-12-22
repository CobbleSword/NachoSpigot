package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutSpawnEntity implements Packet<PacketListenerPlayOut> {
    private int a; public int getEntityId() { return a; } public void setEntityId(int id) { this.a = id; }
    private int b; public int getRawX() { return b; }
    private int c; public int getRawY() { return c; }
    private int d; public int getRawZ() { return d; }
    private int e;
    private int f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k; public int getMetaData() { return this.k; } public void setMetaData(int meta) { this.k = meta; }

    public PacketPlayOutSpawnEntity() {
    }

    public PacketPlayOutSpawnEntity(Entity var1, int var2) {
        this(var1, var2, 0);
    }

    public PacketPlayOutSpawnEntity(Entity var1, int var2, int var3) {
        this.a = var1.getId();
        this.b = MathHelper.floor(var1.locX * 32.0D);
        this.c = MathHelper.floor(var1.locY * 32.0D);
        this.d = MathHelper.floor(var1.locZ * 32.0D);
        this.h = MathHelper.d(var1.pitch * 256.0F / 360.0F);
        this.i = MathHelper.d(var1.yaw * 256.0F / 360.0F);
        this.j = var2;
        this.k = var3;
        if (var3 > 0) {
            double var4 = var1.motX;
            double var6 = var1.motY;
            double var8 = var1.motZ;
            double var10 = 3.9D;
            if (var4 < -var10) {
                var4 = -var10;
            }

            if (var6 < -var10) {
                var6 = -var10;
            }

            if (var8 < -var10) {
                var8 = -var10;
            }

            if (var4 > var10) {
                var4 = var10;
            }

            if (var6 > var10) {
                var6 = var10;
            }

            if (var8 > var10) {
                var8 = var10;
            }

            this.e = (int)(var4 * 8000.0D);
            this.f = (int)(var6 * 8000.0D);
            this.g = (int)(var8 * 8000.0D);
        }

    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readVarInt();
        this.j = var1.readByte();
        this.b = var1.readInt();
        this.c = var1.readInt();
        this.d = var1.readInt();
        this.h = var1.readByte();
        this.i = var1.readByte();
        this.k = var1.readInt();
        if (this.k > 0) {
            this.e = var1.readShort();
            this.f = var1.readShort();
            this.g = var1.readShort();
        }

    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeVarInt(this.a); // Nacho - deobfuscate writeVarInt
        var1.writeByte(this.j);
        var1.writeInt(this.b);
        var1.writeInt(this.c);
        var1.writeInt(this.d);
        var1.writeByte(this.h);
        var1.writeByte(this.i);
        var1.writeInt(this.k);
        if (this.k > 0) {
            var1.writeShort(this.e);
            var1.writeShort(this.f);
            var1.writeShort(this.g);
        }

    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }

    public void a(int var1) {
        this.b = var1;
    }

    public void b(int var1) {
        this.c = var1;
    }

    public void c(int var1) {
        this.d = var1;
    }

    public void d(int var1) {
        this.e = var1;
    }

    public void e(int var1) {
        this.f = var1;
    }

    public void f(int var1) {
        this.g = var1;
    }
}
