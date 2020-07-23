package net.minecraft.server;

public interface IWorldAccess {
    void a(BlockPosition var1);

    void b(BlockPosition var1);

    void a(int var1, int var2, int var3, int var4, int var5, int var6);

    void a(String var1, double var2, double var4, double var6, float var8, float var9);

    void a(EntityHuman var1, String var2, double var3, double var5, double var7, float var9, float var10);

    void a(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15);

    void a(Entity var1);

    void b(Entity var1);

    void a(String var1, BlockPosition var2);

    void a(int var1, BlockPosition var2, int var3);

    void a(EntityHuman var1, int var2, BlockPosition var3, int var4);

    void sendPlayWorldPacket(EntityHuman var1, int var2, int blockPosition_x, int blockPosition_y,int blockPosition_z, int var4);

    void b(int var1, BlockPosition var2, int var3);
}
