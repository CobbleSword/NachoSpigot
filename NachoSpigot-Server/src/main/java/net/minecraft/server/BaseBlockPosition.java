package net.minecraft.server;

import com.google.common.base.Objects;

public class BaseBlockPosition implements Comparable<BaseBlockPosition> {

    public static final BaseBlockPosition ZERO = new BaseBlockPosition(0, 0, 0);
    // PaperSpigot start - Make mutable and protected for MutableBlockPos
    protected int a;
    protected int c;
    protected int d;
    // PaperSpigot end

    public BaseBlockPosition(int i, int j, int k) {
        this.a = i;
        this.c = j;
        this.d = k;
    }

    public BaseBlockPosition(double d0, double d1, double d2) {
        this(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof BaseBlockPosition)) {
            return false;
        } else {
            BaseBlockPosition baseblockposition = (BaseBlockPosition) object;

            return this.getX() != baseblockposition.getX() ? false : (this.getY() != baseblockposition.getY() ? false : this.getZ() == baseblockposition.getZ());
        }
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int g(BaseBlockPosition baseblockposition) {
        return this.getY() == baseblockposition.getY() ? (this.getZ() == baseblockposition.getZ() ? this.getX() - baseblockposition.getX() : this.getZ() - baseblockposition.getZ()) : this.getY() - baseblockposition.getY();
    }

    // PaperSpigot start - Only allow one implementation of these methods (make final)
    public final int getX() {
        return this.a;
    }

    public final int getY() {
        return this.c;
    }

    public final int getZ() {
        return this.d;
    }
    // PaperSpigot end

    public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
        return new BaseBlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public double c(double d0, double d1, double d2) {
        double d3 = (double) this.getX() - d0;
        double d4 = (double) this.getY() - d1;
        double d5 = (double) this.getZ() - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double d(double d0, double d1, double d2) {
        double d3 = (double) this.getX() + 0.5D - d0;
        double d4 = (double) this.getY() + 0.5D - d1;
        double d5 = (double) this.getZ() + 0.5D - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double i(BaseBlockPosition baseblockposition) {
        return this.c((double) baseblockposition.getX(), (double) baseblockposition.getY(), (double) baseblockposition.getZ());
    }

    public String toString() {
        return Objects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    // Paperspigot - Signature change, Object -> BaseBlockPosition
    public int compareTo(BaseBlockPosition object) {
        return this.g((BaseBlockPosition) object);
    }
}
