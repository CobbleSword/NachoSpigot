package net.minecraft.server;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ShapeDetector {
   private final Predicate<ShapeDetectorBlock>[][][] a;
   private final int b;
   private final int c;
   private final int d;

   public ShapeDetector(Predicate<ShapeDetectorBlock>[][][] var1) {
      this.a = var1;
      this.b = var1.length;
      if (this.b > 0) {
         this.c = var1[0].length;
         if (this.c > 0) {
            this.d = var1[0][0].length;
         } else {
            this.d = 0;
         }
      } else {
         this.c = 0;
         this.d = 0;
      }

   }

   public int b() {
      return this.c;
   }

   public int c() {
      return this.d;
   }

   private ShapeDetector.ShapeDetectorCollection a(
      BlockPosition var1, EnumDirection var2, EnumDirection var3, LoadingCache<BlockPosition, ShapeDetectorBlock> var4
   ) {
      for(int var5 = 0; var5 < this.d; ++var5) {
         for(int var6 = 0; var6 < this.c; ++var6) {
            for(int var7 = 0; var7 < this.b; ++var7) {
               if (!this.a[var7][var6][var5].apply(var4.getUnchecked(a(var1, var2, var3, var5, var6, var7)))) {
                  return null;
               }
            }
         }
      }

      return new ShapeDetector.ShapeDetectorCollection(var1, var2, var3, var4, this.d, this.c, this.b);
   }

   public ShapeDetector.ShapeDetectorCollection a(World var1, BlockPosition var2) {
      LoadingCache var3 = a(var1, false);
      int var4 = Math.max(Math.max(this.d, this.c), this.b);

      for(Object var6 : BlockPosition.a(var2, var2.a(var4 - 1, var4 - 1, var4 - 1))) {
         for(Object var10 : EnumDirection.values()) {
            for(Object var14 : EnumDirection.values()) {
               if (var14 != var10 && var14 != ((EnumDirection)var10).opposite()) {
                  ShapeDetector.ShapeDetectorCollection var15 = this.a((BlockPosition)var6, (EnumDirection)var10, (EnumDirection)var14, var3);
                  if (var15 != null) {
                     return var15;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPosition, ShapeDetectorBlock> a(World var0, boolean var1) {
      return CacheBuilder.newBuilder().build(new ShapeDetector.BlockLoader(var0, var1));
   }

   protected static BlockPosition a(BlockPosition var0, EnumDirection var1, EnumDirection var2, int var3, int var4, int var5) {
      if (var1 != var2 && var1 != var2.opposite()) {
         BaseBlockPosition var6 = new BaseBlockPosition(var1.getAdjacentX(), var1.getAdjacentY(), var1.getAdjacentZ());
         BaseBlockPosition var7 = new BaseBlockPosition(var2.getAdjacentX(), var2.getAdjacentY(), var2.getAdjacentZ());
         BaseBlockPosition var8 = var6.d(var7);
         return var0.a(
            var7.getX() * -var4 + var8.getX() * var3 + var6.getX() * var5,
            var7.getY() * -var4 + var8.getY() * var3 + var6.getY() * var5,
            var7.getZ() * -var4 + var8.getZ() * var3 + var6.getZ() * var5
         );
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class BlockLoader extends CacheLoader<BlockPosition, ShapeDetectorBlock> {
      private final World a;
      private final boolean b;

      public BlockLoader(World var1, boolean var2) {
         this.a = var1;
         this.b = var2;
      }

      public ShapeDetectorBlock a(BlockPosition var1) throws Exception {
         return new ShapeDetectorBlock(this.a, var1, this.b);
      }

      @Override
      public ShapeDetectorBlock load(BlockPosition var1) throws Exception {
         return this.a(var1);
      }
   }

   public static class ShapeDetectorCollection {
      private final BlockPosition a;
      private final EnumDirection b;
      private final EnumDirection c;
      private final LoadingCache<BlockPosition, ShapeDetectorBlock> d;
      private final int e;
      private final int f;
      private final int g;

      public ShapeDetectorCollection(
         BlockPosition var1, EnumDirection var2, EnumDirection var3, LoadingCache<BlockPosition, ShapeDetectorBlock> var4, int var5, int var6, int var7
      ) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = var4;
         this.e = var5;
         this.f = var6;
         this.g = var7;
      }

      public BlockPosition a() {
         return this.a;
      }

      public EnumDirection b() {
         return this.b;
      }

      public EnumDirection c() {
         return this.c;
      }

      public int d() {
         return this.e;
      }

      public int e() {
         return this.f;
      }

      public ShapeDetectorBlock a(int var1, int var2, int var3) {
         return (ShapeDetectorBlock)this.d.getUnchecked(ShapeDetector.a(this.a, this.b(), this.c(), var1, var2, var3));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.c).add("forwards", this.b).add("frontTopLeft", this.a).toString();
      }
   }
}
