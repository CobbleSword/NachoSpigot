package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class WorldBorder {

    private final List<IWorldBorderListener> a = Lists.newArrayList();
    private double b = 0.0D;
    private double c = 0.0D;
    private double d = 6.0E7D;
    private double e;
    private long f;
    private long g;
    private int h;
    private double i;
    private double j;
    private int k;
    private int l;
    public WorldServer world; // CraftBukkit

    public WorldBorder() {
        this.e = this.d;
        this.h = 29999984;
        this.i = 0.2D;
        this.j = 5.0D;
        this.k = 15;
        this.l = 5;
    }

    public boolean a(BlockPosition blockposition) {
        return (double) (blockposition.getX() + 1) > this.b() && (double) blockposition.getX() < this.d() && (double) (blockposition.getZ() + 1) > this.c() && (double) blockposition.getZ() < this.e();
    }

    // CraftBukkit start - split method
    public boolean isInBounds(ChunkCoordIntPair chunkcoordintpair) {
        return isInBounds(chunkcoordintpair.x, chunkcoordintpair.z);
    }

    // Inlined the getters from ChunkCoordIntPair
    public boolean isInBounds(long chunkcoords) {
        return isInBounds(org.bukkit.craftbukkit.util.LongHash.msw(chunkcoords), org.bukkit.craftbukkit.util.LongHash.lsw(chunkcoords));
    }

    // Inlined the getters from ChunkCoordIntPair
    public boolean isInBounds(int x, int z) {
        return (double) ((x  << 4) + 15) > this.b() && (double) (x << 4) < this.d() && (double) ((z  << 4) + 15) > this.c() && (double) (x << 4) < this.e();
    }

    public boolean a(AxisAlignedBB axisalignedbb) {
        return axisalignedbb.d > this.b() && axisalignedbb.a < this.d() && axisalignedbb.f > this.c() && axisalignedbb.c < this.e();
    }

    public double a(Entity entity) {
        return this.b(entity.locX, entity.locZ);
    }

    public double b(double d0, double d1) {
        double d2 = d1 - this.c();
        double d3 = this.e() - d1;
        double d4 = d0 - this.b();
        double d5 = this.d() - d0;
        double d6 = Math.min(d4, d5);

        d6 = Math.min(d6, d2);
        return Math.min(d6, d3);
    }

    public EnumWorldBorderState getState() {
        return this.e < this.d ? EnumWorldBorderState.SHRINKING : (this.e > this.d ? EnumWorldBorderState.GROWING : EnumWorldBorderState.STATIONARY);
    }

    public double b() {
        double d0 = this.getCenterX() - this.getSize() / 2.0D;

        if (d0 < (double) (-this.h)) {
            d0 = (double) (-this.h);
        }

        return d0;
    }

    public double c() {
        double d0 = this.getCenterZ() - this.getSize() / 2.0D;

        if (d0 < (double) (-this.h)) {
            d0 = (double) (-this.h);
        }

        return d0;
    }

    public double d() {
        double d0 = this.getCenterX() + this.getSize() / 2.0D;

        if (d0 > (double) this.h) {
            d0 = (double) this.h;
        }

        return d0;
    }

    public double e() {
        double d0 = this.getCenterZ() + this.getSize() / 2.0D;

        if (d0 > (double) this.h) {
            d0 = (double) this.h;
        }

        return d0;
    }

    public double getCenterX() {
        return this.b;
    }

    public double getCenterZ() {
        return this.c;
    }

    public void setCenter(double d0, double d1) {
        this.b = d0;
        this.c = d1;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0, d1);
        }

    }

    public double getSize() {
        if (this.getState() != EnumWorldBorderState.STATIONARY) {
            double d0 = (double) ((float) (System.currentTimeMillis() - this.g) / (float) (this.f - this.g));

            if (d0 < 1.0D) {
                return this.d + (this.e - this.d) * d0;
            }

            this.setSize(this.e);
        }

        return this.d;
    }

    public long i() {
        return this.getState() != EnumWorldBorderState.STATIONARY ? this.f - System.currentTimeMillis() : 0L;
    }

    public double j() {
        return this.e;
    }

    public void setSize(double d0) {
        this.d = d0;
        this.e = d0;
        this.f = System.currentTimeMillis();
        this.g = this.f;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0);
        }

    }

    public void transitionSizeBetween(double d0, double d1, long i) {
        this.d = d0;
        this.e = d1;
        this.g = System.currentTimeMillis();
        this.f = this.g + i;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0, d1, i);
        }

    }

    protected List<IWorldBorderListener> k() {
        return Lists.newArrayList(this.a);
    }

    public void a(IWorldBorderListener iworldborderlistener) {
        if (a.contains(iworldborderlistener)) return; // CraftBukkit
        this.a.add(iworldborderlistener);
    }

    public void a(int i) {
        this.h = i;
    }

    public int l() {
        return this.h;
    }

    public double getDamageBuffer() {
        return this.j;
    }

    public void setDamageBuffer(double d0) {
        this.j = d0;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.c(this, d0);
        }

    }

    public double getDamageAmount() {
        return this.i;
    }

    public void setDamageAmount(double d0) {
        this.i = d0;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.b(this, d0);
        }

    }

    public int getWarningTime() {
        return this.k;
    }

    public void setWarningTime(int i) {
        this.k = i;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, i);
        }

    }

    public int getWarningDistance() {
        return this.l;
    }

    public void setWarningDistance(int i) {
        this.l = i;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.b(this, i);
        }

    }
}
