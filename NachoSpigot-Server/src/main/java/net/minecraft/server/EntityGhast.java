package net.minecraft.server;

import java.util.Random;

public class EntityGhast extends EntityFlying implements IMonster {

    private int a = 1;

    public EntityGhast(World world) {
        super(world);
        this.setSize(4.0F, 4.0F);
        this.fireProof = true;
        this.b_ = 5;
        this.moveController = new EntityGhast.ControllerGhast(this);
        this.goalSelector.a(5, new EntityGhast.PathfinderGoalGhastIdleMove(this));
        this.goalSelector.a(7, new EntityGhast.PathfinderGoalGhastMoveTowardsTarget(this));
        this.goalSelector.a(7, new EntityGhast.PathfinderGoalGhastAttackTarget(this));
        this.targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
    }

    public void a(boolean flag) {
        this.datawatcher.watch(16, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    public int cf() {
        return this.a;
    }

    public void t_() {
        super.t_();
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if ("fireball".equals(damagesource.p()) && damagesource.getEntity() instanceof EntityHuman) {
            super.damageEntity(damagesource, 1000.0F);
            ((EntityHuman) damagesource.getEntity()).b((Statistic) AchievementList.z);
            return true;
        } else {
            return super.damageEntity(damagesource, f);
        }
    }

    protected void h() {
        super.h();
        this.datawatcher.a(16, Byte.valueOf((byte) 0));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100.0D);
    }

    protected String z() {
        return "mob.ghast.moan";
    }

    protected String bo() {
        return "mob.ghast.scream";
    }

    protected String bp() {
        return "mob.ghast.death";
    }

    protected Item getLoot() {
        return Items.GUNPOWDER;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        int j = this.random.nextInt(2) + this.random.nextInt(1 + i);

        int k;

        for (k = 0; k < j; ++k) {
            this.a(Items.GHAST_TEAR, 1);
        }

        j = this.random.nextInt(3) + this.random.nextInt(1 + i);

        for (k = 0; k < j; ++k) {
            this.a(Items.GUNPOWDER, 1);
        }

    }

    protected float bB() {
        return 10.0F;
    }

    public boolean bR() {
        return this.random.nextInt(20) == 0 && super.bR() && this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public int bV() {
        return 1;
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("ExplosionPower", this.a);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("ExplosionPower", 99)) {
            this.a = nbttagcompound.getInt("ExplosionPower");
        }

    }

    public float getHeadHeight() {
        return 2.6F;
    }

    static class PathfinderGoalGhastAttackTarget extends PathfinderGoal {

        private EntityGhast b;
        public int a;

        public PathfinderGoalGhastAttackTarget(EntityGhast entityghast) {
            this.b = entityghast;
        }

        public boolean a() {
            return this.b.getGoalTarget() != null;
        }

        public void c() {
            this.a = 0;
        }

        public void d() {
            this.b.a(false);
        }

        public void e() {
            EntityLiving entityliving = this.b.getGoalTarget();
            double d0 = 64.0D;

            if (entityliving.h(this.b) < d0 * d0 && this.b.hasLineOfSight(entityliving)) {
                World world = this.b.world;

                ++this.a;
                if (this.a == 10) {
                    world.a((EntityHuman) null, 1007, new BlockPosition(this.b), 0);
                }

                if (this.a == 20) {
                    double d1 = 4.0D;
                    Vec3D vec3d = this.b.d(1.0F);
                    double d2 = entityliving.locX - (this.b.locX + vec3d.a * d1);
                    double d3 = entityliving.getBoundingBox().b + (double) (entityliving.length / 2.0F) - (0.5D + this.b.locY + (double) (this.b.length / 2.0F));
                    double d4 = entityliving.locZ - (this.b.locZ + vec3d.c * d1);

                    world.a((EntityHuman) null, 1008, new BlockPosition(this.b), 0);
                    EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.b, d2, d3, d4);


                    // CraftBukkit - set bukkitYield when setting explosionpower
                    entitylargefireball.bukkitYield = entitylargefireball.yield = this.b.cf();
                    entitylargefireball.locX = this.b.locX + vec3d.a * d1;
                    entitylargefireball.locY = this.b.locY + (double) (this.b.length / 2.0F) + 0.5D;
                    entitylargefireball.locZ = this.b.locZ + vec3d.c * d1;
                    world.addEntity(entitylargefireball);
                    this.a = -40;
                }
            } else if (this.a > 0) {
                --this.a;
            }

            this.b.a(this.a > 10);
        }
    }

    static class PathfinderGoalGhastMoveTowardsTarget extends PathfinderGoal {

        private EntityGhast a;

        public PathfinderGoalGhastMoveTowardsTarget(EntityGhast entityghast) {
            this.a = entityghast;
            this.a(2);
        }

        public boolean a() {
            return true;
        }

        public void e() {
            if (this.a.getGoalTarget() == null) {
                this.a.aI = this.a.yaw = -((float) MathHelper.b(this.a.motX, this.a.motZ)) * 180.0F / 3.1415927F;
            } else {
                EntityLiving entityliving = this.a.getGoalTarget();
                double d0 = 64.0D;

                if (entityliving.h(this.a) < d0 * d0) {
                    double d1 = entityliving.locX - this.a.locX;
                    double d2 = entityliving.locZ - this.a.locZ;

                    this.a.aI = this.a.yaw = -((float) MathHelper.b(d1, d2)) * 180.0F / 3.1415927F;
                }
            }

        }
    }

    static class PathfinderGoalGhastIdleMove extends PathfinderGoal {

        private EntityGhast a;

        public PathfinderGoalGhastIdleMove(EntityGhast entityghast) {
            this.a = entityghast;
            this.a(1);
        }

        public boolean a() {
            ControllerMove controllermove = this.a.getControllerMove();

            if (!controllermove.a()) {
                return true;
            } else {
                double d0 = controllermove.d() - this.a.locX;
                double d1 = controllermove.e() - this.a.locY;
                double d2 = controllermove.f() - this.a.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        public boolean b() {
            return false;
        }

        public void c() {
            Random random = this.a.bc();
            double d0 = this.a.locX + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.a.locY + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.a.locZ + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);

            this.a.getControllerMove().a(d0, d1, d2, 1.0D);
        }
    }

    static class ControllerGhast extends ControllerMove {

        private EntityGhast g;
        private int h;

        public ControllerGhast(EntityGhast entityghast) {
            super(entityghast);
            this.g = entityghast;
        }

        public void c() {
            if (this.f) {
                double d0 = this.b - this.g.locX;
                double d1 = this.c - this.g.locY;
                double d2 = this.d - this.g.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (this.h-- <= 0) {
                    this.h += this.g.bc().nextInt(5) + 2;
                    d3 = (double) MathHelper.sqrt(d3);
                    if (this.b(this.b, this.c, this.d, d3)) {
                        this.g.motX += d0 / d3 * 0.1D;
                        this.g.motY += d1 / d3 * 0.1D;
                        this.g.motZ += d2 / d3 * 0.1D;
                    } else {
                        this.f = false;
                    }
                }

            }
        }

        private boolean b(double d0, double d1, double d2, double d3) {
            double d4 = (d0 - this.g.locX) / d3;
            double d5 = (d1 - this.g.locY) / d3;
            double d6 = (d2 - this.g.locZ) / d3;
            AxisAlignedBB axisalignedbb = this.g.getBoundingBox();

            for (int i = 1; (double) i < d3; ++i) {
                axisalignedbb = axisalignedbb.c(d4, d5, d6);
                if (!this.g.world.getCubes(this.g, axisalignedbb).isEmpty()) {
                    return false;
                }
            }

            return true;
        }
    }
}
