package net.minecraft.server;

public class PathfinderGoalSwell extends PathfinderGoal {

    EntityCreeper a;
    EntityLiving b;

    public PathfinderGoalSwell(EntityCreeper entitycreeper) {
        this.a = entitycreeper;
        this.a(1);
    }

    public boolean a() {
        EntityLiving entityliving = this.a.getGoalTarget();

        return this.a.cm() > 0 || entityliving != null && this.a.h(entityliving) < 9.0D;
    }

    public void c() {
        this.a.getNavigation().n();
        this.b = this.a.getGoalTarget();
    }

    public void d() {
        this.b = null;
    }

    public void e() {
        if (this.b == null) {
            this.a.a(-1);
        } else if (this.a.h(this.b) > 49.0D) {
            this.a.a(-1);
        } else if (!this.a.getEntitySenses().a(this.b)) {
            this.a.a(-1);
        } else {
            this.a.a(1);
        }
    }
}
