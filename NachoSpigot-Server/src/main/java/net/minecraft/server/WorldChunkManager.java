//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;

public class WorldChunkManager {
    private GenLayer b;
    private GenLayer c;
    private BiomeCache d;
    private List<BiomeBase> e;
    private String f;

    protected WorldChunkManager() {
        this.d = new BiomeCache(this);
        this.f = "";
        this.e = Lists.newArrayList();
        this.e.add(BiomeBase.FOREST);
        this.e.add(BiomeBase.PLAINS);
        this.e.add(BiomeBase.TAIGA);
        this.e.add(BiomeBase.TAIGA_HILLS);
        this.e.add(BiomeBase.FOREST_HILLS);
        this.e.add(BiomeBase.JUNGLE);
        this.e.add(BiomeBase.JUNGLE_HILLS);
    }

    public WorldChunkManager(long var1, WorldType var3, String var4) {
        this();
        this.f = var4;
        GenLayer[] var5 = GenLayer.a(var1, var3, var4);
        this.b = var5[0];
        this.c = var5[1];
    }

    public WorldChunkManager(World var1) {
        this(var1.getSeed(), var1.getWorldData().getType(), var1.getWorldData().getGeneratorOptions());
    }

    public List<BiomeBase> a() {
        return this.e;
    }

    public BiomeBase getBiome(BlockPosition var1) {
        return this.getBiome(var1, (BiomeBase)null);
    }

    public BiomeBase getBiome(BlockPosition var1, BiomeBase var2) {
        return this.d.a(var1.getX(), var1.getZ(), var2);
    }

    public BiomeBase getBiome(int blockPosition_x, int blockPosition_z, BiomeBase var2) {
        return this.d.a(blockPosition_x, blockPosition_z, var2);
    }

    public float[] getWetness(float[] var1, int var2, int var3, int var4, int var5) {
        IntCache.a();
        if (var1 == null || var1.length < var4 * var5) {
            var1 = new float[var4 * var5];
        }

        int[] var6 = this.c.a(var2, var3, var4, var5);

        for(int var7 = 0; var7 < var4 * var5; ++var7) {
            try {
                float var8 = (float)BiomeBase.getBiome(var6[var7], BiomeBase.ad).h() / 65536.0F;
                if (var8 > 1.0F) {
                    var8 = 1.0F;
                }

                var1[var7] = var8;
            } catch (Throwable var11) {
                CrashReport var9 = CrashReport.a(var11, "Invalid Biome id");
                CrashReportSystemDetails var10 = var9.a("DownfallBlock");
                var10.a("biome id", var7);
                var10.a("downfalls[] size", var1.length);
                var10.a("x", var2);
                var10.a("z", var3);
                var10.a("w", var4);
                var10.a("h", var5);
                throw new ReportedException(var9);
            }
        }

        return var1;
    }

    public BiomeBase[] getBiomes(BiomeBase[] var1, int var2, int var3, int var4, int var5) {
        IntCache.a();
        if (var1 == null || var1.length < var4 * var5) {
            var1 = new BiomeBase[var4 * var5];
        }

        int[] var6 = this.b.a(var2, var3, var4, var5);

        try {
            for(int var7 = 0; var7 < var4 * var5; ++var7) {
                var1[var7] = BiomeBase.getBiome(var6[var7], BiomeBase.ad);
            }

            return var1;
        } catch (Throwable var10) {
            CrashReport var8 = CrashReport.a(var10, "Invalid Biome id");
            CrashReportSystemDetails var9 = var8.a("RawBiomeBlock");
            var9.a("biomes[] size", var1.length);
            var9.a("x", var2);
            var9.a("z", var3);
            var9.a("w", var4);
            var9.a("h", var5);
            throw new ReportedException(var8);
        }
    }

    public BiomeBase[] getBiomeBlock(BiomeBase[] var1, int var2, int var3, int var4, int var5) {
        return this.a(var1, var2, var3, var4, var5, true);
    }

    public BiomeBase[] a(BiomeBase[] var1, int var2, int var3, int var4, int var5, boolean var6) {
        IntCache.a();
        if (var1 == null || var1.length < var4 * var5) {
            var1 = new BiomeBase[var4 * var5];
        }

        if (var6 && var4 == 16 && var5 == 16 && (var2 & 15) == 0 && (var3 & 15) == 0) {
            BiomeBase[] var9 = this.d.c(var2, var3);
            System.arraycopy(var9, 0, var1, 0, var4 * var5);
            return var1;
        } else {
            int[] var7 = this.c.a(var2, var3, var4, var5);

            for(int var8 = 0; var8 < var4 * var5; ++var8) {
                var1[var8] = BiomeBase.getBiome(var7[var8], BiomeBase.ad);
            }

            return var1;
        }
    }

    public boolean a(int var1, int var2, int var3, List<BiomeBase> var4) {
        IntCache.a();
        int var5 = var1 - var3 >> 2;
        int var6 = var2 - var3 >> 2;
        int var7 = var1 + var3 >> 2;
        int var8 = var2 + var3 >> 2;
        int var9 = var7 - var5 + 1;
        int var10 = var8 - var6 + 1;
        int[] var11 = this.b.a(var5, var6, var9, var10);

        try {
            for(int var12 = 0; var12 < var9 * var10; ++var12) {
                BiomeBase var16 = BiomeBase.getBiome(var11[var12]);
                if (!var4.contains(var16)) {
                    return false;
                }
            }

            return true;
        } catch (Throwable var15) {
            CrashReport var13 = CrashReport.a(var15, "Invalid Biome id");
            CrashReportSystemDetails var14 = var13.a("Layer");
            var14.a("Layer", this.b.toString());
            var14.a("x", var1);
            var14.a("z", var2);
            var14.a("radius", var3);
            var14.a("allowed", var4);
            throw new ReportedException(var13);
        }
    }

    public BlockPosition a(int var1, int var2, int var3, List<BiomeBase> var4, Random var5) {
        IntCache.a();
        int var6 = var1 - var3 >> 2;
        int var7 = var2 - var3 >> 2;
        int var8 = var1 + var3 >> 2;
        int var9 = var2 + var3 >> 2;
        int var10 = var8 - var6 + 1;
        int var11 = var9 - var7 + 1;
        int[] var12 = this.b.a(var6, var7, var10, var11);
        BlockPosition var13 = null;
        int var14 = 0;

        for(int var15 = 0; var15 < var10 * var11; ++var15) {
            int var16 = var6 + var15 % var10 << 2;
            int var17 = var7 + var15 / var10 << 2;
            BiomeBase var18 = BiomeBase.getBiome(var12[var15]);
            if (var4.contains(var18) && (var13 == null || var5.nextInt(var14 + 1) == 0)) {
                var13 = new BlockPosition(var16, 0, var17);
                ++var14;
            }
        }

        return var13;
    }

    public void b() {
        this.d.a();
    }
}
