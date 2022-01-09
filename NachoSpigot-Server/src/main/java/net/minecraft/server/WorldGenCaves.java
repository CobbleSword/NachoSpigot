package net.minecraft.server;

import com.google.common.base.MoreObjects;
import java.util.Random;

public class WorldGenCaves extends WorldGenBase {
   public WorldGenCaves() {
   }

   protected void a(long var1, int var3, int var4, ChunkSnapshot var5, double var6, double var8, double var10) {
      this.a(var1, var3, var4, var5, var6, var8, var10, 1.0F + this.b.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   protected void a(
      long var1,
      int var3,
      int var4,
      ChunkSnapshot var5,
      double var6,
      double var8,
      double var10,
      float var12,
      float var13,
      float var14,
      int var15,
      int var16,
      double var17
   ) {
      double var19 = (double)(var3 * 16 + 8);
      double var21 = (double)(var4 * 16 + 8);
      float var23 = 0.0F;
      float var24 = 0.0F;
      Random var25 = new Random(var1);
      if (var16 <= 0) {
         int var26 = this.a * 16 - 16;
         var16 = var26 - var25.nextInt(var26 / 4);
      }

      boolean var58 = false;
      if (var15 == -1) {
         var15 = var16 / 2;
         var58 = true;
      }

      int var27 = var25.nextInt(var16 / 2) + var16 / 4;

      for(boolean var28 = var25.nextInt(6) == 0; var15 < var16; ++var15) {
         double var29 = 1.5 + (double)(MathHelper.sin((float)var15 * (float) Math.PI / (float)var16) * var12 * 1.0F);
         double var31 = var29 * var17;
         float var33 = MathHelper.cos(var14);
         float var34 = MathHelper.sin(var14);
         var6 += (double)(MathHelper.cos(var13) * var33);
         var8 += (double)var34;
         var10 += (double)(MathHelper.sin(var13) * var33);
         if (var28) {
            var14 *= 0.92F;
         } else {
            var14 *= 0.7F;
         }

         var14 += var24 * 0.1F;
         var13 += var23 * 0.1F;
         var24 *= 0.9F;
         var23 *= 0.75F;
         var24 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 2.0F;
         var23 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 4.0F;
         if (!var58 && var15 == var27 && var12 > 1.0F && var16 > 0) {
            this.a(
               var25.nextLong(),
               var3,
               var4,
               var5,
               var6,
               var8,
               var10,
               var25.nextFloat() * 0.5F + 0.5F,
               var13 - (float) (Math.PI / 2),
               var14 / 3.0F,
               var15,
               var16,
               1.0
            );
            this.a(
               var25.nextLong(),
               var3,
               var4,
               var5,
               var6,
               var8,
               var10,
               var25.nextFloat() * 0.5F + 0.5F,
               var13 + (float) (Math.PI / 2),
               var14 / 3.0F,
               var15,
               var16,
               1.0
            );
            return;
         }

         if (var58 || var25.nextInt(4) != 0) {
            double var35 = var6 - var19;
            double var37 = var10 - var21;
            double var39 = (double)(var16 - var15);
            double var41 = (double)(var12 + 2.0F + 16.0F);
            if (var35 * var35 + var37 * var37 - var39 * var39 > var41 * var41) {
               return;
            }

            if (!(var6 < var19 - 16.0 - var29 * 2.0)
               && !(var10 < var21 - 16.0 - var29 * 2.0)
               && !(var6 > var19 + 16.0 + var29 * 2.0)
               && !(var10 > var21 + 16.0 + var29 * 2.0)) {
               int var59 = MathHelper.floor(var6 - var29) - var3 * 16 - 1;
               int var36 = MathHelper.floor(var6 + var29) - var3 * 16 + 1;
               int var60 = MathHelper.floor(var8 - var31) - 1;
               int var38 = MathHelper.floor(var8 + var31) + 1;
               int var61 = MathHelper.floor(var10 - var29) - var4 * 16 - 1;
               int var40 = MathHelper.floor(var10 + var29) - var4 * 16 + 1;
               if (var59 < 0) {
                  var59 = 0;
               }

               if (var36 > 16) {
                  var36 = 16;
               }

               if (var60 < 1) {
                  var60 = 1;
               }

               if (var38 > 248) {
                  var38 = 248;
               }

               if (var61 < 0) {
                  var61 = 0;
               }

               if (var40 > 16) {
                  var40 = 16;
               }

               boolean var62 = false;

               for(int var42 = var59; !var62 && var42 < var36; ++var42) {
                  for(int var43 = var61; !var62 && var43 < var40; ++var43) {
                     for(int var44 = var38 + 1; !var62 && var44 >= var60 - 1; --var44) {
                        if (var44 >= 0 && var44 < 256) {
                           IBlockData var45 = var5.a(var42, var44, var43);
                           if (var45.getBlock() == Blocks.FLOWING_WATER || var45.getBlock() == Blocks.WATER) {
                              var62 = true;
                           }

                           if (var44 != var60 - 1 && var42 != var59 && var42 != var36 - 1 && var43 != var61 && var43 != var40 - 1) {
                              var44 = var60;
                           }
                        }
                     }
                  }
               }

               if (!var62) {
                  BlockPosition.MutableBlockPosition var63 = new BlockPosition.MutableBlockPosition();

                  for(int var64 = var59; var64 < var36; ++var64) {
                     double var65 = ((double)(var64 + var3 * 16) + 0.5 - var6) / var29;

                     for(int var46 = var61; var46 < var40; ++var46) {
                        double var47 = ((double)(var46 + var4 * 16) + 0.5 - var10) / var29;
                        boolean var49 = false;
                        if (var65 * var65 + var47 * var47 < 1.0) {
                           for(int var50 = var38; var50 > var60; --var50) {
                              double var51 = ((double)(var50 - 1) + 0.5 - var8) / var31;
                              if (var51 > -0.7 && var65 * var65 + var51 * var51 + var47 * var47 < 1.0) {
                                 IBlockData var53 = var5.a(var64, var50, var46);
                                 IBlockData var54 = (IBlockData)MoreObjects.firstNonNull(var5.a(var64, var50 + 1, var46), Blocks.AIR.getBlockData());
                                 if (var53.getBlock() == Blocks.GRASS || var53.getBlock() == Blocks.MYCELIUM) {
                                    var49 = true;
                                 }

                                 if (this.a(var53, var54)) {
                                    if (var50 - 1 < 10) {
                                       var5.a(var64, var50, var46, Blocks.LAVA.getBlockData());
                                    } else {
                                       var5.a(var64, var50, var46, Blocks.AIR.getBlockData());
                                       if (var54.getBlock() == Blocks.SAND) {
                                          var5.a(
                                             var64,
                                             var50 + 1,
                                             var46,
                                             var54.get(BlockSand.VARIANT) == BlockSand.EnumSandVariant.RED_SAND
                                                ? Blocks.RED_SANDSTONE.getBlockData()
                                                : Blocks.SANDSTONE.getBlockData()
                                          );
                                       }

                                       if (var49 && var5.a(var64, var50 - 1, var46).getBlock() == Blocks.DIRT) {
                                          var63.setValues(var64 + var3 * 16, 0, var46 + var4 * 16);
                                          var5.a(var64, var50 - 1, var46, this.c.getBiome(var63).ak.getBlock().getBlockData());
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var58) {
                     break;
                  }
               }
            }
         }
      }

   }

   protected boolean a(IBlockData var1, IBlockData var2) {
      if (var1.getBlock() == Blocks.STONE) {
         return true;
      } else if (var1.getBlock() == Blocks.DIRT) {
         return true;
      } else if (var1.getBlock() == Blocks.GRASS) {
         return true;
      } else if (var1.getBlock() == Blocks.HARDENED_CLAY) {
         return true;
      } else if (var1.getBlock() == Blocks.STAINED_HARDENED_CLAY) {
         return true;
      } else if (var1.getBlock() == Blocks.SANDSTONE) {
         return true;
      } else if (var1.getBlock() == Blocks.RED_SANDSTONE) {
         return true;
      } else if (var1.getBlock() == Blocks.MYCELIUM) {
         return true;
      } else if (var1.getBlock() == Blocks.SNOW_LAYER) {
         return true;
      } else {
         return (var1.getBlock() == Blocks.SAND || var1.getBlock() == Blocks.GRAVEL) && var2.getBlock().getMaterial() != Material.WATER;
      }
   }

   @Override
   protected void a(World var1, int var2, int var3, int var4, int var5, ChunkSnapshot var6) {
      int var7 = this.b.nextInt(this.b.nextInt(this.b.nextInt(15) + 1) + 1);
      if (this.b.nextInt(7) != 0) {
         var7 = 0;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         double var9 = (double)(var2 * 16 + this.b.nextInt(16));
         double var11 = (double)this.b.nextInt(this.b.nextInt(120) + 8);
         double var13 = (double)(var3 * 16 + this.b.nextInt(16));
         int var15 = 1;
         if (this.b.nextInt(4) == 0) {
            this.a(this.b.nextLong(), var4, var5, var6, var9, var11, var13);
            var15 += this.b.nextInt(4);
         }

         for(int var16 = 0; var16 < var15; ++var16) {
            float var17 = this.b.nextFloat() * (float) Math.PI * 2.0F;
            float var18 = (this.b.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var19 = this.b.nextFloat() * 2.0F + this.b.nextFloat();
            if (this.b.nextInt(10) == 0) {
               var19 *= this.b.nextFloat() * this.b.nextFloat() * 3.0F + 1.0F;
            }

            this.a(this.b.nextLong(), var4, var5, var6, var9, var11, var13, var19, var17, var18, 0, 0, 1.0);
         }
      }

   }
}
