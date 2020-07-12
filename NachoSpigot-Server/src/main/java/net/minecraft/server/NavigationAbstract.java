package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public abstract class NavigationAbstract {

    protected EntityInsentient b;
    protected World c;
    protected PathEntity d;
    protected double e;
    private final AttributeInstance a;
    private int f;
    private int g;
    private Vec3D h = new Vec3D(0.0D, 0.0D, 0.0D);
    private float i = 1.0F;
    private final Pathfinder j;

    public NavigationAbstract(EntityInsentient entityinsentient, World world) {
        this.b = entityinsentient;
        this.c = world;
        this.a = entityinsentient.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        this.j = this.a();
    }

    protected abstract Pathfinder a();

    public void a(double d0) {
        this.e = d0;
    }

    public float i() {
        return (float) this.a.getValue();
    }

    public final PathEntity a(double d0, double d1, double d2) {
        return this.a(new BlockPosition(MathHelper.floor(d0), (int) d1, MathHelper.floor(d2)));
    }

    public PathEntity a(BlockPosition blockposition) {
        if (!this.b()) {
            return null;
        } else {
            float f = this.i();

            this.c.methodProfiler.a("pathfind");
            BlockPosition blockposition1 = new BlockPosition(this.b);
            int i = (int) (f + 8.0F);
            ChunkCache chunkcache = new ChunkCache(this.c, blockposition1.a(-i, -i, -i), blockposition1.a(i, i, i), 0);
            PathEntity pathentity = this.j.a((IBlockAccess) chunkcache, (Entity) this.b, blockposition, f);

            this.c.methodProfiler.b();
            return pathentity;
        }
    }

    public boolean a(double d0, double d1, double d2, double d3) {
        PathEntity pathentity = this.a((double) MathHelper.floor(d0), (double) ((int) d1), (double) MathHelper.floor(d2));

        return this.a(pathentity, d3);
    }

    public void a(float f) {
        this.i = f;
    }

    public PathEntity a(Entity entity) {
        if (!this.b()) {
            return null;
        } else {
            float f = this.i();

            this.c.methodProfiler.a("pathfind");
            BlockPosition blockposition = (new BlockPosition(this.b)).up();
            int i = (int) (f + 16.0F);
            ChunkCache chunkcache = new ChunkCache(this.c, blockposition.a(-i, -i, -i), blockposition.a(i, i, i), 0);
            PathEntity pathentity = this.j.a((IBlockAccess) chunkcache, (Entity) this.b, entity, f);

            this.c.methodProfiler.b();
            return pathentity;
        }
    }

    public boolean a(Entity entity, double d0) {
        // PaperSpigot start - Pathfinding optimizations
        if (this.pathfindFailures > 10 && this.d == null && MinecraftServer.currentTick < this.lastFailure + 40) {
            return false;
        }
        PathEntity pathentity = this.a(entity);

        if (pathentity != null && this.a(pathentity, d0)) {
            this.lastFailure = 0;
            this.pathfindFailures = 0;
            return true;
        } else {
            this.pathfindFailures++;
            this.lastFailure = MinecraftServer.currentTick;
            return false;
        }
    }
    private int lastFailure = 0;
    private int pathfindFailures = 0;
    // PaperSpigot end

    public boolean a(PathEntity pathentity, double d0) {
        if (pathentity == null) {
            this.d = null;
            return false;
        } else {
            if (!pathentity.a(this.d)) {
                this.d = pathentity;
            }

            this.d();
            if (this.d.d() == 0) {
                return false;
            } else {
                this.e = d0;
                Vec3D vec3d = this.c();

                this.g = this.f;
                this.h = vec3d;
                return true;
            }
        }
    }

    public PathEntity j() {
        return this.d;
    }

    public void k() {
        ++this.f;
        if (!this.m()) {
            Vec3D vec3d;

            if (this.b()) {
                this.l();
            } else if (this.d != null && this.d.e() < this.d.d()) {
                vec3d = this.c();
                Vec3D vec3d1 = this.d.a(this.b, this.d.e());

                if (vec3d.b > vec3d1.b && !this.b.onGround && MathHelper.floor(vec3d.a) == MathHelper.floor(vec3d1.a) && MathHelper.floor(vec3d.c) == MathHelper.floor(vec3d1.c)) {
                    this.d.c(this.d.e() + 1);
                }
            }

            if (!this.m()) {
                vec3d = this.d.a((Entity) this.b);
                if (vec3d != null) {
                    AxisAlignedBB axisalignedbb = (new AxisAlignedBB(vec3d.a, vec3d.b, vec3d.c, vec3d.a, vec3d.b, vec3d.c)).grow(0.5D, 0.5D, 0.5D);
                    List list = this.c.getCubes(this.b, axisalignedbb.a(0.0D, -1.0D, 0.0D));
                    double d0 = -1.0D;

                    axisalignedbb = axisalignedbb.c(0.0D, 1.0D, 0.0D);

                    AxisAlignedBB axisalignedbb1;

                    for (Iterator iterator = list.iterator(); iterator.hasNext(); d0 = axisalignedbb1.b(axisalignedbb, d0)) {
                        axisalignedbb1 = (AxisAlignedBB) iterator.next();
                    }

                    this.b.getControllerMove().a(vec3d.a, vec3d.b + d0, vec3d.c, this.e);
                }
            }
        }
    }

    protected void l() {
        Vec3D vec3d = this.c();
        int i = this.d.d();

        for (int j = this.d.e(); j < this.d.d(); ++j) {
            if (this.d.a(j).b != (int) vec3d.b) {
                i = j;
                break;
            }
        }

        float f = this.b.width * this.b.width * this.i;

        int k;

        for (k = this.d.e(); k < i; ++k) {
            Vec3D vec3d1 = this.d.a(this.b, k);

            if (vec3d.distanceSquared(vec3d1) < (double) f) {
                this.d.c(k + 1);
            }
        }

        k = MathHelper.f(this.b.width);
        int l = (int) this.b.length + 1;
        int i1 = k;

        for (int j1 = i - 1; j1 >= this.d.e(); --j1) {
            if (this.a(vec3d, this.d.a(this.b, j1), k, l, i1)) {
                this.d.c(j1);
                break;
            }
        }

        this.a(vec3d);
    }

    protected void a(Vec3D vec3d) {
        if (this.f - this.g > 100) {
            if (vec3d.distanceSquared(this.h) < 2.25D) {
                this.n();
            }

            this.g = this.f;
            this.h = vec3d;
        }

    }

    public boolean m() {
        return this.d == null || this.d.b();
    }

    public void n() {
        this.pathfindFailures = 0; this.lastFailure = 0; // PaperSpigot - Pathfinding optimizations
        this.d = null;
    }

    protected abstract Vec3D c();

    protected abstract boolean b();

    protected boolean o() {
        return this.b.V() || this.b.ab();
    }

    protected void d() {}

    protected abstract boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k);
}
