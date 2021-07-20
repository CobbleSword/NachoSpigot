package net.minecraft.server;

public class PathfinderGoalSwell extends PathfinderGoal {

    EntityCreeper a;
    // EntityLiving b; // KigPaper

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
        // this.b = this.a.getGoalTarget(); // KigPaper
    }

    public void d() {
        // this.b = null; // KigPaper
    }

    public void e() {
        // KigPaper - use Entity GoalTarget
        EntityLiving target = this.a.getGoalTarget();
        if (target == null) {
            this.a.a(-1);
        } else if (this.a.h(target) > 49.0D) {
            this.a.a(-1);
        } else if (!this.a.getEntitySenses().a(target)) {
            this.a.a(-1);
        } else {
            this.a.a(1);
        }
    }
}
