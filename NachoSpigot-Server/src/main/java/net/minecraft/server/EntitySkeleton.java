package net.minecraft.server;

import java.util.Calendar;

import org.bukkit.event.entity.EntityCombustEvent; // CraftBukkit

public class EntitySkeleton extends EntityMonster implements IRangedEntity {

    private PathfinderGoalArrowAttack a = new PathfinderGoalArrowAttack(this, 1.0D, 20, 60, 15.0F);
    private PathfinderGoalMeleeAttack b = new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.2D, false);

    public EntitySkeleton(World world) {
        super(world);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalRestrictSun(this));
        this.goalSelector.a(3, new PathfinderGoalFleeSun(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalAvoidTarget(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.a(4, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
        if (world != null && !world.isClientSide) {
            this.n();
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    protected void h() {
        super.h();
        this.datawatcher.a(13, new Byte((byte) 0));
    }

    protected String z() {
        return "mob.skeleton.say";
    }

    protected String bo() {
        return "mob.skeleton.hurt";
    }

    protected String bp() {
        return "mob.skeleton.death";
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.makeSound("mob.skeleton.step", 0.15F, 1.0F);
    }

    public boolean r(Entity entity) {
        if (super.r(entity)) {
            if (this.getSkeletonType() == 1 && entity instanceof EntityLiving) {
                ((EntityLiving) entity).addEffect(new MobEffect(MobEffectList.WITHER.id, 200));
            }

            return true;
        } else {
            return false;
        }
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    public void m() {
        if (this.world.w() && !this.world.isClientSide) {
            float f = this.c(1.0F);
            BlockPosition blockposition = new BlockPosition(this.locX, (double) Math.round(this.locY), this.locZ);

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.i(blockposition)) {
                boolean flag = true;
                ItemStack itemstack = this.getEquipment(4);

                if (itemstack != null) {
                    if (itemstack.e()) {
                        itemstack.setData(itemstack.h() + this.random.nextInt(2));
                        if (itemstack.h() >= itemstack.j()) {
                            this.b(itemstack);
                            this.setEquipment(4, (ItemStack) null);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    // CraftBukkit start
                    EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), 8);
                    this.world.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        this.setOnFire(event.getDuration());
                    }
                    // CraftBukkit end
                }
            }
        }

        if (this.world.isClientSide && this.getSkeletonType() == 1) {
            this.setSize(0.72F, 2.535F);
        }

        super.m();
    }

    public void ak() {
        super.ak();
        if (this.vehicle instanceof EntityCreature) {
            EntityCreature entitycreature = (EntityCreature) this.vehicle;

            this.aI = entitycreature.aI;
        }

    }

    public void die(DamageSource damagesource) {
        // super.die(damagesource); // CraftBukkit
        if (damagesource.i() instanceof EntityArrow && damagesource.getEntity() instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) damagesource.getEntity();
            double d0 = entityhuman.locX - this.locX;
            double d1 = entityhuman.locZ - this.locZ;

            if (d0 * d0 + d1 * d1 >= 2500.0D) {
                entityhuman.b((Statistic) AchievementList.v);
            }
        } else if (damagesource.getEntity() instanceof EntityCreeper && ((EntityCreeper) damagesource.getEntity()).isPowered() && ((EntityCreeper) damagesource.getEntity()).cp()) {
            ((EntityCreeper) damagesource.getEntity()).cq();
            // CraftBukkit start
            // this.a(new ItemStack(Items.SKULL, 1, this.getSkeletonType() == 1 ? 1 : 0), 0.0F);
            headDrop = new ItemStack(Items.SKULL, 1, this.getSkeletonType() == 1 ? 1 : 0);
            // CraftBukkit end

        }

        super.die(damagesource); // CraftBukkit - moved from above

    }

    /* CraftBukkit start
    protected Item getLoot() {
        return Items.ARROW;
    }
    // CraftBukkit end */

    protected void dropDeathLoot(boolean flag, int i) {
        super.dropDeathLoot(flag, i); // CraftBukkit
        int j;
        int k;

        if (this.getSkeletonType() == 1) {
            j = this.random.nextInt(3 + i) - 1;

            for (k = 0; k < j; ++k) {
                this.a(Items.COAL, 1);
            }
        } else {
            j = this.random.nextInt(3 + i);

            for (k = 0; k < j; ++k) {
                this.a(Items.ARROW, 1);
            }
        }

        j = this.random.nextInt(3 + i);

        for (k = 0; k < j; ++k) {
            this.a(Items.BONE, 1);
        }

    }

    protected void getRareDrop() {
        if (this.getSkeletonType() == 1) {
            this.a(new ItemStack(Items.SKULL, 1, 1), 0.0F);
        }

    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        super.a(difficultydamagescaler);
        this.setEquipment(0, new ItemStack(Items.BOW));
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity);
        if (this.world.worldProvider instanceof WorldProviderHell && this.bc().nextInt(5) > 0) {
            this.goalSelector.a(4, this.b);
            this.setSkeletonType(1);
            this.setEquipment(0, new ItemStack(Items.STONE_SWORD));
            this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
        } else {
            this.goalSelector.a(4, this.a);
            this.a(difficultydamagescaler);
            this.b(difficultydamagescaler);
        }

        this.j(this.random.nextFloat() < 0.55F * difficultydamagescaler.c());
        if (this.getEquipment(4) == null) {
            Calendar calendar = this.world.Y();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
                this.setEquipment(4, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
                this.dropChances[4] = 0.0F;
            }
        }

        return groupdataentity;
    }

    public void n() {
        this.goalSelector.a((PathfinderGoal) this.b);
        this.goalSelector.a((PathfinderGoal) this.a);
        ItemStack itemstack = this.bA();

        if (itemstack != null && itemstack.getItem() == Items.BOW) {
            this.goalSelector.a(4, this.a);
        } else {
            this.goalSelector.a(4, this.b);
        }

    }

    public void a(EntityLiving entityliving, float f) {
        EntityArrow entityarrow = new EntityArrow(this.world, this, entityliving, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        int i = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_DAMAGE.id, this.bA());
        int j = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK.id, this.bA());

        entityarrow.b((double) (f * 2.0F) + this.random.nextGaussian() * 0.25D + (double) ((float) this.world.getDifficulty().a() * 0.11F));
        if (i > 0) {
            entityarrow.b(entityarrow.j() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            entityarrow.setKnockbackStrength(j);
        }

        if (EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_FIRE.id, this.bA()) > 0 || this.getSkeletonType() == 1) {
            // CraftBukkit start - call EntityCombustEvent
            EntityCombustEvent event = new EntityCombustEvent(entityarrow.getBukkitEntity(), 100);
            this.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                entityarrow.setOnFire(event.getDuration());
            }
            // CraftBukkit end
        }

        // CraftBukkit start
        org.bukkit.event.entity.EntityShootBowEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callEntityShootBowEvent(this, this.bA(), entityarrow, 0.8F);
        if (event.isCancelled()) {
            event.getProjectile().remove();
            return;
        }

        if (event.getProjectile() == entityarrow.getBukkitEntity()) {
            world.addEntity(entityarrow);
        }
        // CraftBukkit end

        this.makeSound("random.bow", 1.0F, 1.0F / (this.bc().nextFloat() * 0.4F + 0.8F));
        // this.world.addEntity(entityarrow); // CraftBukkit - moved up
    }

    public int getSkeletonType() {
        return this.datawatcher.getByte(13);
    }

    public void setSkeletonType(int i) {
        this.datawatcher.watch(13, Byte.valueOf((byte) i));
        this.fireProof = i == 1;
        if (i == 1) {
            this.setSize(0.72F, 2.535F);
        } else {
            this.setSize(0.6F, 1.95F);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("SkeletonType", 99)) {
            byte b0 = nbttagcompound.getByte("SkeletonType");

            this.setSkeletonType(b0);
        }

        this.n();
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setByte("SkeletonType", (byte) this.getSkeletonType());
    }

    public void setEquipment(int i, ItemStack itemstack) {
        super.setEquipment(i, itemstack);
        if (!this.world.isClientSide && i == 0) {
            this.n();
        }

    }

    public float getHeadHeight() {
        return this.getSkeletonType() == 1 ? super.getHeadHeight() : 1.74F;
    }

    public double am() {
        return this.isBaby() ? 0.0D : -0.35D;
    }
}
