package net.minecraft.server;

import com.google.common.base.MoreObjects;

public class StructureBoundingBox {
   public int a;
   public int b;
   public int c;
   public int d;
   public int e;
   public int f;

   public StructureBoundingBox() {
   }

   public StructureBoundingBox(int[] var1) {
      if (var1.length == 6) {
         this.a = var1[0];
         this.b = var1[1];
         this.c = var1[2];
         this.d = var1[3];
         this.e = var1[4];
         this.f = var1[5];
      }

   }

   public static StructureBoundingBox a() {
      return new StructureBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
   }

   public static StructureBoundingBox a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, EnumDirection var9) {
      switch(var9) {
      case NORTH:
         return new StructureBoundingBox(var0 + var3, var1 + var4, var2 - var8 + 1 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var5);
      case SOUTH:
         return new StructureBoundingBox(var0 + var3, var1 + var4, var2 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var8 - 1 + var5);
      case WEST:
         return new StructureBoundingBox(var0 - var8 + 1 + var5, var1 + var4, var2 + var3, var0 + var5, var1 + var7 - 1 + var4, var2 + var6 - 1 + var3);
      case EAST:
         return new StructureBoundingBox(var0 + var5, var1 + var4, var2 + var3, var0 + var8 - 1 + var5, var1 + var7 - 1 + var4, var2 + var6 - 1 + var3);
      default:
         return new StructureBoundingBox(var0 + var3, var1 + var4, var2 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var8 - 1 + var5);
      }
   }

   public static StructureBoundingBox a(int var0, int var1, int var2, int var3, int var4, int var5) {
      return new StructureBoundingBox(
         Math.min(var0, var3), Math.min(var1, var4), Math.min(var2, var5), Math.max(var0, var3), Math.max(var1, var4), Math.max(var2, var5)
      );
   }

   public StructureBoundingBox(StructureBoundingBox var1) {
      this.a = var1.a;
      this.b = var1.b;
      this.c = var1.c;
      this.d = var1.d;
      this.e = var1.e;
      this.f = var1.f;
   }

   public StructureBoundingBox(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
      this.f = var6;
   }

   public StructureBoundingBox(BaseBlockPosition var1, BaseBlockPosition var2) {
      this.a = Math.min(var1.getX(), var2.getX());
      this.b = Math.min(var1.getY(), var2.getY());
      this.c = Math.min(var1.getZ(), var2.getZ());
      this.d = Math.max(var1.getX(), var2.getX());
      this.e = Math.max(var1.getY(), var2.getY());
      this.f = Math.max(var1.getZ(), var2.getZ());
   }

   public StructureBoundingBox(int var1, int var2, int var3, int var4) {
      this.a = var1;
      this.c = var2;
      this.d = var3;
      this.f = var4;
      this.b = 1;
      this.e = 512;
   }

   public boolean a(StructureBoundingBox var1) {
      return this.d >= var1.a && this.a <= var1.d && this.f >= var1.c && this.c <= var1.f && this.e >= var1.b && this.b <= var1.e;
   }

   public boolean a(int var1, int var2, int var3, int var4) {
      return this.d >= var1 && this.a <= var3 && this.f >= var2 && this.c <= var4;
   }

   public void b(StructureBoundingBox var1) {
      this.a = Math.min(this.a, var1.a);
      this.b = Math.min(this.b, var1.b);
      this.c = Math.min(this.c, var1.c);
      this.d = Math.max(this.d, var1.d);
      this.e = Math.max(this.e, var1.e);
      this.f = Math.max(this.f, var1.f);
   }

   public void a(int var1, int var2, int var3) {
      this.a += var1;
      this.b += var2;
      this.c += var3;
      this.d += var1;
      this.e += var2;
      this.f += var3;
   }

   public boolean b(BaseBlockPosition var1) {
      return var1.getX() >= this.a && var1.getX() <= this.d && var1.getZ() >= this.c && var1.getZ() <= this.f && var1.getY() >= this.b && var1.getY() <= this.e;
   }

   public BaseBlockPosition b() {
      return new BaseBlockPosition(this.d - this.a, this.e - this.b, this.f - this.c);
   }

   public int c() {
      return this.d - this.a + 1;
   }

   public int d() {
      return this.e - this.b + 1;
   }

   public int e() {
      return this.f - this.c + 1;
   }

   public BaseBlockPosition f() {
      return new BlockPosition(this.a + (this.d - this.a + 1) / 2, this.b + (this.e - this.b + 1) / 2, this.c + (this.f - this.c + 1) / 2);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this)
         .add("x0", this.a)
         .add("y0", this.b)
         .add("z0", this.c)
         .add("x1", this.d)
         .add("y1", this.e)
         .add("z1", this.f)
         .toString();
   }

   public NBTTagIntArray g() {
      return new NBTTagIntArray(new int[]{this.a, this.b, this.c, this.d, this.e, this.f});
   }
}
