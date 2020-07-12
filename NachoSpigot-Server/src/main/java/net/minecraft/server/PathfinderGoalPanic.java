package net.minecraft.server;

public class PathfinderGoalPanic extends PathfinderGoal {

    private EntityCreature b;
    protected double a;
    private double c;
    private double d;
    private double e;

    public PathfinderGoalPanic(EntityCreature entitycreature, double d0) {
        this.b = entitycreature;
        this.a = d0;
        this.a(1);
    }

    public boolean a() {
        if (this.b.getLastDamager() == null && !this.b.isBurning()) {
            return false;
        } else {
            Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);

            if (vec3d == null) {
                return false;
            } else {
                this.c = vec3d.a;
                this.d = vec3d.b;
                this.e = vec3d.c;
                return true;
            }
        }
    }

    public void c() {
        this.b.getNavigation().a(this.c, this.d, this.e, this.a);
    }

    public boolean b() {
        // CraftBukkit start - introduce a temporary timeout hack until this is fixed properly
        if ((this.b.ticksLived - this.b.hurtTimestamp) > 100) {
            this.b.b((EntityLiving) null);
            return false;
        }
        // CraftBukkit end
        return !this.b.getNavigation().m();
    }
}
