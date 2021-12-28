package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInWindowClick implements Packet<PacketListenerPlayIn> {
    private int a;
    private int slot;
    private int button;
    private short d;
    // private ItemStack item;
    private byte[] itemHash;
    private int shift;

    public PacketPlayInWindowClick() {
    }

    public void a(PacketListenerPlayIn var1) {
        var1.a(this);
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readByte();
        this.slot = var1.readShort();
        this.button = var1.readByte();
        this.d = var1.readShort();
        this.shift = var1.readByte();
        // this.item = var1.i();
        this.itemHash = var1.readItemHash();
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeByte(this.a);
        var1.writeShort(this.slot);
        var1.writeByte(this.button);
        var1.writeShort(this.d);
        var1.writeByte(this.shift);
        // var1.a(this.item);
    }

    public int a() {
        return this.a;
    }

    public int b() {
        return this.slot;
    }

    public int c() {
        return this.button;
    }

    public short d() {
        return this.d;
    }

    public ItemStack e() {
        return null;
    }

    public byte[] getItemHash() { return itemHash; }

    public int f() {
        return this.shift;
    }
}

