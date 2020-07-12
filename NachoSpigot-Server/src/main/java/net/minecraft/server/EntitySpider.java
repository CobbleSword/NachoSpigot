package net.minecraft.server;

import java.util.Random;

public class EntitySpider extends EntityMonster {

    public EntitySpider(World world) {
        super(world);
        this.setSize(1.4F, 0.9F);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(4, new EntitySpider.PathfinderGoalSpiderMeleeAttack(this, EntityHuman.class));
        this.goalSelector.a(4, new EntitySpider.PathfinderGoalSpiderMeleeAttack(this, EntityIronGolem.class));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.8D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new EntitySpider.PathfinderGoalSpiderNearestAttackableTarget(this, EntityHuman.class));
        this.targetSelector.a(3, new EntitySpider.PathfinderGoalSpiderNearestAttackableTarget(this, EntityIronGolem.class));
    }

    public double an() {
        return (double) (this.length * 0.5F);
    }

    protected NavigationAbstract b(World world) {
        return new NavigationSpider(this, world);
    }

    protected void h() {
        super.h();
        this.datawatcher.a(16, new Byte((byte) 0));
    }

    public void t_() {
        super.t_();
        if (!this.world.isClientSide) {
            this.a(this.positionChanged);
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(16.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
    }

    protected String z() {
        return "mob.spider.say";
    }

    protected String bo() {
        return "mob.spider.say";
    }

    protected String bp() {
        return "mob.spider.death";
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.makeSound("mob.spider.step", 0.15F, 1.0F);
    }

    protected Item getLoot() {
        return Items.STRING;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        super.dropDeathLoot(flag, i);
        if (flag && (this.random.nextInt(3) == 0 || this.random.nextInt(1 + i) > 0)) {
            this.a(Items.SPIDER_EYE, 1);
        }

    }

    public boolean k_() {
        return this.n();
    }

    public void aA() {}

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    public boolean d(MobEffect mobeffect) {
        return mobeffect.getEffectId() == MobEffectList.POISON.id ? false : super.d(mobeffect);
    }

    public boolean n() {
        return (this.datawatcher.getByte(16) & 1) != 0;
    }

    public void a(boolean flag) {
        byte b0 = this.datawatcher.getByte(16);

        if (flag) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 &= -2;
        }

        this.datawatcher.watch(16, Byte.valueOf(b0));
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity);

        if (this.world.random.nextInt(100) == 0) {
            EntitySkeleton entityskeleton = new EntitySkeleton(this.world);

            entityskeleton.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
            entityskeleton.prepare(difficultydamagescaler, (GroupDataEntity) null);
            this.world.addEntity(entityskeleton, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.JOCKEY); // CraftBukkit - add SpawnReason
            entityskeleton.mount(this);
        }

        if (object == null) {
            object = new EntitySpider.GroupDataSpider();
            if (this.world.getDifficulty() == EnumDifficulty.HARD && this.world.random.nextFloat() < 0.1F * difficultydamagescaler.c()) {
                ((EntitySpider.GroupDataSpider) object).a(this.world.random);
            }
        }

        if (object instanceof EntitySpider.GroupDataSpider) {
            int i = ((EntitySpider.GroupDataSpider) object).a;

            if (i > 0 && MobEffectList.byId[i] != null) {
                this.addEffect(new MobEffect(i, Integer.MAX_VALUE));
            }
        }

        return (GroupDataEntity) object;
    }

    public float getHeadHeight() {
        return 0.65F;
    }

    static class PathfinderGoalSpiderNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget {

        public PathfinderGoalSpiderNearestAttackableTarget(EntitySpider entityspider, Class<T> oclass) {
            super(entityspider, oclass, true);
        }

        public boolean a() {
            float f = this.e.c(1.0F);

            return f >= 0.5F ? false : super.a();
        }
    }

    static class PathfinderGoalSpiderMeleeAttack extends PathfinderGoalMeleeAttack {

        public PathfinderGoalSpiderMeleeAttack(EntitySpider entityspider, Class<? extends Entity> oclass) {
            super(entityspider, oclass, 1.0D, true);
        }

        public boolean b() {
            float f = this.b.c(1.0F);

            if (f >= 0.5F && this.b.bc().nextInt(100) == 0) {
                this.b.setGoalTarget((EntityLiving) null);
                return false;
            } else {
                return super.b();
            }
        }

        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.width);
        }
    }

    public static class GroupDataSpider implements GroupDataEntity {

        public int a;

        public GroupDataSpider() {}

        public void a(Random random) {
            int i = random.nextInt(5);

            if (i <= 1) {
                this.a = MobEffectList.FASTER_MOVEMENT.id;
            } else if (i <= 2) {
                this.a = MobEffectList.INCREASE_DAMAGE.id;
            } else if (i <= 3) {
                this.a = MobEffectList.REGENERATION.id;
            } else if (i <= 4) {
                this.a = MobEffectList.INVISIBILITY.id;
            }

        }
    }
}
