package net.minecraft.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.DataWatcher.WatchableObject;

public class PacketPlayOutNamedEntitySpawn implements Packet<PacketListenerPlayOut> {
    private int a; public int getEntityId() { return this.a; } public void setEntityId(int id) { this.a = id; }
    private UUID b; public UUID getUUID() { return this.b; } public void setUUID(UUID uuid) { this.b = uuid; }
    private int c; public int getRawX() { return this.c; }
    private int d; public int getRawY() { return this.d; }
    private int e; public int getRawZ() { return this.e; }
    private byte f; public byte getRawPitch() { return this.f; }
    private byte g; public byte getRawYaw() { return this.g; }
    private int h; public int getData() { return this.h; }
    private DataWatcher i;
    private List<WatchableObject> j;

    public PacketPlayOutNamedEntitySpawn() {
    }

    public PacketPlayOutNamedEntitySpawn(EntityHuman var1) {
        this.a = var1.getId();
        this.b = var1.getProfile().getId();
        this.c = MathHelper.floor(var1.locX * 32.0D);
        this.d = MathHelper.floor(var1.locY * 32.0D);
        this.e = MathHelper.floor(var1.locZ * 32.0D);
        this.f = (byte)((int)(var1.yaw * 256.0F / 360.0F));
        this.g = (byte)((int)(var1.pitch * 256.0F / 360.0F));
        ItemStack var2 = var1.inventory.getItemInHand();
        this.h = var2 == null ? 0 : Item.getId(var2.getItem());
        this.i = var1.getDataWatcher();
    }

    public void setX(double x)
    {
        this.c = MathHelper.floor(x * 32.0D);;
    }

    public void setY(double y)
    {
        this.d = MathHelper.floor(y * 32.0D);;
    }

    public void setZ(double z)
    {
        this.e = MathHelper.floor(z * 32.0D);;
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readVarInt();
        this.b = var1.readUUID(); // Nacho - deobfuscate readUUID
        this.c = var1.readInt();
        this.d = var1.readInt();
        this.e = var1.readInt();
        this.f = var1.readByte();
        this.g = var1.readByte();
        this.h = var1.readShort();
        this.j = DataWatcher.b(var1);
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeVarInt(this.a); // Nacho - deobfuscate writeVarInt
        var1.a(this.b);
        var1.writeInt(this.c);
        var1.writeInt(this.d);
        var1.writeInt(this.e);
        var1.writeByte(this.f);
        var1.writeByte(this.g);
        var1.writeShort(this.h);
        this.i.a(var1);
    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }
}
