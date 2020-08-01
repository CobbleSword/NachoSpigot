package net.minecraft.server;

import java.io.IOException;

public class Int3PacketPlayOutWorldEvent extends PacketPlayOutWorldEvent {
    private int blockPosition_x;
    private int blockPosition_y;
    private int blockPosition_z;

    public Int3PacketPlayOutWorldEvent() {
    }

    public Int3PacketPlayOutWorldEvent(int var1, int blockPosition_x, int blockPosition_y, int blockPosition_z, int var3, boolean var4) {
        this.a = var1;
        this.blockPosition_x = blockPosition_x;
        this.blockPosition_y = blockPosition_y;
        this.blockPosition_z = blockPosition_z;
        this.c = var3;
        this.d = var4;
    }

    public void setX(int x)
    {
        this.blockPosition_x = x;
    }

    public void setY(int y)
    {
        this.blockPosition_y = y;
    }

    public void setZ(int z)
    {
        this.blockPosition_z = z;
    }

    public int getX() {
        return blockPosition_x;
    }

    public int getY() {
        return blockPosition_y;
    }

    public int getZ() {
        return blockPosition_z;
    }

    @Override
    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readInt();

        long i = var1.readLong();
        this.blockPosition_x  = (int) (i << 64 - BlockPosition.g - BlockPosition.c >> 64 - BlockPosition.c);
        this.blockPosition_y  = (int) (i << 64 - BlockPosition.f - BlockPosition.e >> 64 - BlockPosition.e);
        this.blockPosition_z = (int) (i << 64 - BlockPosition.d >> 64 - BlockPosition.d);

        this.c = var1.readInt();
        this.d = var1.readBoolean();
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeInt(this.a);
        var1.writeLong(((long) this.blockPosition_x & BlockPosition.h) << BlockPosition.g | ((long) this.blockPosition_y & BlockPosition.i) << BlockPosition.f | ((long) this.blockPosition_z & BlockPosition.j) << 0);
        var1.writeInt(this.c);
        var1.writeBoolean(this.d);
    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }
}
