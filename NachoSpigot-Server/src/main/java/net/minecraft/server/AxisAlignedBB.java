package net.minecraft.server;

public class AxisAlignedBB {
    public final double a; public double getMinX() { return a; }
    public final double b; public double getMinY() { return b; }
    public final double c; public double getMinZ() { return c; }
    public final double d; public double getMaxX() { return d; }
    public final double e; public double getMaxY() { return e; }
    public final double f; public double getMaxZ() { return f; }

    public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        this.a = Math.min(x1, x2);
        this.b = Math.min(y1, y2);
        this.c = Math.min(z1, z2);
        this.d = Math.max(x1, x2);
        this.e = Math.max(y1, y2);
        this.f = Math.max(z1, z2);
    }

    public AxisAlignedBB(BlockPosition var1, BlockPosition var2) {
        this.a = (double)var1.getX();
        this.b = (double)var1.getY();
        this.c = (double)var1.getZ();
        this.d = (double)var2.getX();
        this.e = (double)var2.getY();
        this.f = (double)var2.getZ();
    }

    public AxisAlignedBB a(double var1, double var3, double var5) {
        double var7 = this.a;
        double var9 = this.b;
        double var11 = this.c;
        double var13 = this.d;
        double var15 = this.e;
        double var17 = this.f;
        if (var1 < 0.0D) {
            var7 += var1;
        } else if (var1 > 0.0D) {
            var13 += var1;
        }

        if (var3 < 0.0D) {
            var9 += var3;
        } else if (var3 > 0.0D) {
            var15 += var3;
        }

        if (var5 < 0.0D) {
            var11 += var5;
        } else if (var5 > 0.0D) {
            var17 += var5;
        }

        return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
    }

    public AxisAlignedBB grow(double var1, double var3, double var5) {
        double var7 = this.a - var1;
        double var9 = this.b - var3;
        double var11 = this.c - var5;
        double var13 = this.d + var1;
        double var15 = this.e + var3;
        double var17 = this.f + var5;
        return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
    }

    public AxisAlignedBB a(AxisAlignedBB var1) {
        double var2 = Math.min(this.a, var1.a);
        double var4 = Math.min(this.b, var1.b);
        double var6 = Math.min(this.c, var1.c);
        double var8 = Math.max(this.d, var1.d);
        double var10 = Math.max(this.e, var1.e);
        double var12 = Math.max(this.f, var1.f);
        return new AxisAlignedBB(var2, var4, var6, var8, var10, var12);
    }

    public static AxisAlignedBB a(double var0, double var2, double var4, double var6, double var8, double var10) {
        double var12 = Math.min(var0, var6);
        double var14 = Math.min(var2, var8);
        double var16 = Math.min(var4, var10);
        double var18 = Math.max(var0, var6);
        double var20 = Math.max(var2, var8);
        double var22 = Math.max(var4, var10);
        return new AxisAlignedBB(var12, var14, var16, var18, var20, var22);
    }

    public AxisAlignedBB c(double var1, double var3, double var5) {
        return new AxisAlignedBB(this.a + var1, this.b + var3, this.c + var5, this.d + var1, this.e + var3, this.f + var5);
    }

    public double a(AxisAlignedBB var1, double var2) {
        if (var1.e > this.b && var1.b < this.e && var1.f > this.c && var1.c < this.f) {
            double var4;
            if (var2 > 0.0D && var1.d <= this.a) {
                var4 = this.a - var1.d;
                if (var4 < var2) {
                    var2 = var4;
                }
            } else if (var2 < 0.0D && var1.a >= this.d) {
                var4 = this.d - var1.a;
                if (var4 > var2) {
                    var2 = var4;
                }
            }

            return var2;
        } else {
            return var2;
        }
    }

    public double b(AxisAlignedBB var1, double var2) {
        if (var1.d > this.a && var1.a < this.d && var1.f > this.c && var1.c < this.f) {
            double var4;
            if (var2 > 0.0D && var1.e <= this.b) {
                var4 = this.b - var1.e;
                if (var4 < var2) {
                    var2 = var4;
                }
            } else if (var2 < 0.0D && var1.b >= this.e) {
                var4 = this.e - var1.b;
                if (var4 > var2) {
                    var2 = var4;
                }
            }

            return var2;
        } else {
            return var2;
        }
    }

    public double c(AxisAlignedBB var1, double var2) {
        if (var1.d > this.a && var1.a < this.d && var1.e > this.b && var1.b < this.e) {
            double var4;
            if (var2 > 0.0D && var1.f <= this.c) {
                var4 = this.c - var1.f;
                if (var4 < var2) {
                    var2 = var4;
                }
            } else if (var2 < 0.0D && var1.c >= this.f) {
                var4 = this.f - var1.c;
                if (var4 > var2) {
                    var2 = var4;
                }
            }

            return var2;
        } else {
            return var2;
        }
    }

    public boolean contains(AxisAlignedBB var1)
    {
        return this.b(var1);
    }

    public boolean b(AxisAlignedBB var1) {
        if (var1.d > this.a && var1.a < this.d) {
            if (var1.e > this.b && var1.b < this.e) {
                return var1.f > this.c && var1.c < this.f;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean contains(Vec3D var1)
    {
        return this.a(var1);
    }

    public boolean a(Vec3D var1) {
        if (var1.a > this.a && var1.a < this.d) {
            if (var1.b > this.b && var1.b < this.e) {
                return var1.c > this.c && var1.c < this.f;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public double a()
    {
        double var1 = this.d - this.a;
        double var3 = this.e - this.b;
        double var5 = this.f - this.c;
        return (var1 + var3 + var5) / 3.0D;
    }

    public AxisAlignedBB shrink(double var1, double var3, double var5) {
        double var7 = this.a + var1;
        double var9 = this.b + var3;
        double var11 = this.c + var5;
        double var13 = this.d - var1;
        double var15 = this.e - var3;
        double var17 = this.f - var5;
        return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
    }

    public MovingObjectPosition a(Vec3D var1, Vec3D var2) {
        Vec3D var3 = var1.a(var2, this.a);
        Vec3D var4 = var1.a(var2, this.d);
        Vec3D var5 = var1.b(var2, this.b);
        Vec3D var6 = var1.b(var2, this.e);
        Vec3D var7 = var1.c(var2, this.c);
        Vec3D var8 = var1.c(var2, this.f);
        if (!this.b(var3)) {
            var3 = null;
        }

        if (!this.b(var4)) {
            var4 = null;
        }

        if (!this.c(var5)) {
            var5 = null;
        }

        if (!this.c(var6)) {
            var6 = null;
        }

        if (!this.d(var7)) {
            var7 = null;
        }

        if (!this.d(var8)) {
            var8 = null;
        }

        Vec3D var9 = null;
        if (var3 != null) {
            var9 = var3;
        }

        if (var4 != null && (var9 == null || var1.distanceSquared(var4) < var1.distanceSquared(var9))) {
            var9 = var4;
        }

        if (var5 != null && (var9 == null || var1.distanceSquared(var5) < var1.distanceSquared(var9))) {
            var9 = var5;
        }

        if (var6 != null && (var9 == null || var1.distanceSquared(var6) < var1.distanceSquared(var9))) {
            var9 = var6;
        }

        if (var7 != null && (var9 == null || var1.distanceSquared(var7) < var1.distanceSquared(var9))) {
            var9 = var7;
        }

        if (var8 != null && (var9 == null || var1.distanceSquared(var8) < var1.distanceSquared(var9))) {
            var9 = var8;
        }

        if (var9 == null) {
            return null;
        } else {
            EnumDirection var10 = null;
            if (var9 == var3) {
                var10 = EnumDirection.WEST;
            } else if (var9 == var4) {
                var10 = EnumDirection.EAST;
            } else if (var9 == var5) {
                var10 = EnumDirection.DOWN;
            } else if (var9 == var6) {
                var10 = EnumDirection.UP;
            } else if (var9 == var7) {
                var10 = EnumDirection.NORTH;
            } else {
                var10 = EnumDirection.SOUTH;
            }

            return new MovingObjectPosition(var9, var10);
        }
    }

    private boolean b(Vec3D var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1.b >= this.b && var1.b <= this.e && var1.c >= this.c && var1.c <= this.f;
        }
    }

    private boolean c(Vec3D var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1.a >= this.a && var1.a <= this.d && var1.c >= this.c && var1.c <= this.f;
        }
    }

    private boolean d(Vec3D var1) {
        if (var1 == null) {
            return false;
        } else {
            return var1.a >= this.a && var1.a <= this.d && var1.b >= this.b && var1.b <= this.e;
        }
    }

    public String toString() {
        return "box[" + this.a + ", " + this.b + ", " + this.c + " -> " + this.d + ", " + this.e + ", " + this.f + "]";
    }
}
