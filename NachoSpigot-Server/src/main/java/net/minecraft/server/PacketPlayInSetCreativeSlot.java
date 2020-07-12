package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInSetCreativeSlot implements Packet<PacketListenerPlayIn>
{
    private int slot;
    private ItemStack b;

    public PacketPlayInSetCreativeSlot() {}

    @Override
    public void a(PacketDataSerializer packetDataSerializer) throws IOException {
        this.slot = packetDataSerializer.readShort();
        this.b = packetDataSerializer.decodeItemStack();
    }

    @Override
    public void b(PacketDataSerializer packetDataSerializer) throws IOException {

        packetDataSerializer.writeShort(this.slot);
        packetDataSerializer.a(this.b);
    }

    @Override
    public void a(PacketListenerPlayIn packetListenerPlayIn)
    {
        packetListenerPlayIn.a(this);
    }

    public int a() {
        return this.slot;
    }

    public ItemStack getItemStack() {
        return this.b;
    }
}
