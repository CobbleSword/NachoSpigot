package net.minecraft.server;

public class PathfinderGoalFloat extends PathfinderGoal {

    private EntityInsentient a;

    public PathfinderGoalFloat(EntityInsentient entityinsentient) {
        this.a = entityinsentient;
        entityinsentient.goalFloat = this; // PaperSpigot
        this.a(4);
        ((Navigation) entityinsentient.getNavigation()).d(true);
    }

    public boolean a() {
        return this.a.V() || this.a.ab();
    }

    public void e() {
        if (this.a.bc().nextFloat() < 0.8F) {
            this.a.getControllerJump().a();
        }

    }
}
