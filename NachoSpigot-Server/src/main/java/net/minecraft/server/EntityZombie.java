package net.minecraft.server;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

//CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent;
//CraftBukkit end

public class EntityZombie extends EntityMonster {

    protected static final IAttribute a = (new AttributeRanged((IAttribute) null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).a("Spawn Reinforcements Chance");
    private static final UUID b = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier c = new AttributeModifier(EntityZombie.b, "Baby speed boost", org.github.paperspigot.PaperSpigotConfig.babyZombieMovementSpeed, 1); // PaperSpigot - Configurable baby zombie movement speed
    private final PathfinderGoalBreakDoor bm = new PathfinderGoalBreakDoor(this);
    private int bn;
    private boolean bo = false;
    private float bp = -1.0F;
    private float bq;
    private int lastTick = MinecraftServer.currentTick; // CraftBukkit - add field

    public EntityZombie(World world) {
        super(world);
        ((Navigation) this.getNavigation()).b(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.n();
        this.setSize(0.6F, 1.95F);
    }

    protected void n() {
        if ( world.spigotConfig.zombieAggressiveTowardsVillager ) this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityVillager.class, 1.0D, true)); // Spigot
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityIronGolem.class, 1.0D, true));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] { EntityPigZombie.class}));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        if ( world.spigotConfig.zombieAggressiveTowardsVillager ) this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, false)); // Spigot
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(35.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(3.0D);
        this.getAttributeMap().b(EntityZombie.a).setValue(this.random.nextDouble() * 0.10000000149011612D);
    }

    protected void h() {
        super.h();
        this.getDataWatcher().a(12, Byte.valueOf((byte) 0));
        this.getDataWatcher().a(13, Byte.valueOf((byte) 0));
        this.getDataWatcher().a(14, Byte.valueOf((byte) 0));
    }

    public int br() {
        int i = super.br() + 2;

        if (i > 20) {
            i = 20;
        }

        return i;
    }

    public boolean cn() {
        return this.bo;
    }

    public void a(boolean flag) {
        if (this.bo != flag) {
            this.bo = flag;
            if (flag) {
                this.goalSelector.a(1, this.bm);
            } else {
                this.goalSelector.a((PathfinderGoal) this.bm);
            }
        }

    }

    public boolean isBaby() {
        return this.getDataWatcher().getByte(12) == 1;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        if (this.isBaby()) {
            this.b_ = (int) ((float) this.b_ * 2.5F);
        }

        return super.getExpValue(entityhuman);
    }

    public void setBaby(boolean flag) {
        this.getDataWatcher().watch(12, Byte.valueOf((byte) (flag ? 1 : 0)));
        if (this.world != null && !this.world.isClientSide) {
            AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributeinstance.c(EntityZombie.c);
            if (flag) {
                attributeinstance.b(EntityZombie.c);
            }
        }

        this.n(flag);
    }

    public boolean isVillager() {
        return this.getDataWatcher().getByte(13) == 1;
    }

    public void setVillager(boolean flag) {
        this.getDataWatcher().watch(13, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    public void m() {
        if (this.world.w() && !this.world.isClientSide && !this.isBaby()) {
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

        if (this.au() && this.getGoalTarget() != null && this.vehicle instanceof EntityChicken) {
            ((EntityInsentient) this.vehicle).getNavigation().a(this.getNavigation().j(), 1.5D);
        }

        super.m();
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (super.damageEntity(damagesource, f)) {
            EntityLiving entityliving = this.getGoalTarget();

            if (entityliving == null && damagesource.getEntity() instanceof EntityLiving) {
                entityliving = (EntityLiving) damagesource.getEntity();
            }

            if (entityliving != null && this.world.getDifficulty() == EnumDifficulty.HARD && (double) this.random.nextFloat() < this.getAttributeInstance(EntityZombie.a).getValue()) {
                int i = MathHelper.floor(this.locX);
                int j = MathHelper.floor(this.locY);
                int k = MathHelper.floor(this.locZ);
                EntityZombie entityzombie = new EntityZombie(this.world);

                for (int l = 0; l < 50; ++l) {
                    int i1 = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int j1 = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int k1 = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);

                    if (World.a((IBlockAccess) this.world, new BlockPosition(i1, j1 - 1, k1)) && this.world.getLightLevel(new BlockPosition(i1, j1, k1)) < 10) {
                        entityzombie.setPosition((double) i1, (double) j1, (double) k1);
                        if (!this.world.isPlayerNearby((double) i1, (double) j1, (double) k1, 7.0D) && this.world.a(entityzombie.getBoundingBox(), (Entity) entityzombie) && this.world.getCubes(entityzombie, entityzombie.getBoundingBox()).isEmpty() && !this.world.containsLiquid(entityzombie.getBoundingBox())) {
                            this.world.addEntity(entityzombie, CreatureSpawnEvent.SpawnReason.REINFORCEMENTS); // CraftBukkit
                            entityzombie.setGoalTarget(entityliving, EntityTargetEvent.TargetReason.REINFORCEMENT_TARGET, true);
                            entityzombie.prepare(this.world.E(new BlockPosition(entityzombie)), (GroupDataEntity) null);
                            this.getAttributeInstance(EntityZombie.a).b(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, 0));
                            entityzombie.getAttributeInstance(EntityZombie.a).b(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, 0));
                            break;
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void t_() {
        if (!this.world.isClientSide && this.cp()) {
            int i = this.cr();

            // CraftBukkit start - Use wall time instead of ticks for villager conversion
            int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
            this.lastTick = MinecraftServer.currentTick;
            i *= elapsedTicks;
            // CraftBukkit end

            this.bn -= i;
            if (this.bn <= 0) {
                this.cq();
            }
        }

        super.t_();
    }

    public boolean r(Entity entity) {
        boolean flag = super.r(entity);

        if (flag) {
            int i = this.world.getDifficulty().a();

            if (this.bA() == null && this.isBurning() && this.random.nextFloat() < (float) i * 0.3F) {
                // CraftBukkit start
                EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 2 * i);
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    entity.setOnFire(event.getDuration());
                }
                // CraftBukkit end
            }
        }

        return flag;
    }

    protected String z() {
        return "mob.zombie.say";
    }

    protected String bo() {
        return "mob.zombie.hurt";
    }

    protected String bp() {
        return "mob.zombie.death";
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.makeSound("mob.zombie.step", 0.15F, 1.0F);
    }

    protected Item getLoot() {
        return Items.ROTTEN_FLESH;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    protected void getRareDrop() {
        switch (this.random.nextInt(3)) {
        case 0:
            this.a(Items.IRON_INGOT, 1);
            break;

        case 1:
            this.a(Items.CARROT, 1);
            break;

        case 2:
            this.a(Items.POTATO, 1);
        }

    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        super.a(difficultydamagescaler);
        if (this.random.nextFloat() < (this.world.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
            int i = this.random.nextInt(3);

            if (i == 0) {
                this.setEquipment(0, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setEquipment(0, new ItemStack(Items.IRON_SHOVEL));
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.setBoolean("IsBaby", true);
        }

        if (this.isVillager()) {
            nbttagcompound.setBoolean("IsVillager", true);
        }

        nbttagcompound.setInt("ConversionTime", this.cp() ? this.bn : -1);
        nbttagcompound.setBoolean("CanBreakDoors", this.cn());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.getBoolean("IsBaby")) {
            this.setBaby(true);
        }

        if (nbttagcompound.getBoolean("IsVillager")) {
            this.setVillager(true);
        }

        if (nbttagcompound.hasKeyOfType("ConversionTime", 99) && nbttagcompound.getInt("ConversionTime") > -1) {
            this.a(nbttagcompound.getInt("ConversionTime"));
        }

        this.a(nbttagcompound.getBoolean("CanBreakDoors"));
    }

    public void a(EntityLiving entityliving) {
        super.a(entityliving);
        if ((this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) && entityliving instanceof EntityVillager) {
            if (this.world.getDifficulty() != EnumDifficulty.HARD && this.random.nextBoolean()) {
                return;
            }

            EntityInsentient entityinsentient = (EntityInsentient) entityliving;
            EntityZombie entityzombie = new EntityZombie(this.world);

            entityzombie.m(entityliving);
            this.world.kill(entityliving);
            entityzombie.prepare(this.world.E(new BlockPosition(entityzombie)), (GroupDataEntity) null);
            entityzombie.setVillager(true);
            if (entityliving.isBaby()) {
                entityzombie.setBaby(true);
            }

            entityzombie.k(entityinsentient.ce());
            if (entityinsentient.hasCustomName()) {
                entityzombie.setCustomName(entityinsentient.getCustomName());
                entityzombie.setCustomNameVisible(entityinsentient.getCustomNameVisible());
            }

            this.world.addEntity(entityzombie, CreatureSpawnEvent.SpawnReason.INFECTION); // CraftBukkit - add SpawnReason
            this.world.a((EntityHuman) null, 1016, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0);
        }

    }

    public float getHeadHeight() {
        float f = 1.74F;

        if (this.isBaby()) {
            f = (float) ((double) f - 0.81D);
        }

        return f;
    }

    protected boolean a(ItemStack itemstack) {
        return itemstack.getItem() == Items.EGG && this.isBaby() && this.au() ? false : super.a(itemstack);
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity);
        float f = difficultydamagescaler.c();

        this.j(this.random.nextFloat() < 0.55F * f);
        if (object == null) {
            object = new EntityZombie.GroupDataZombie(this.world.random.nextFloat() < 0.05F, this.world.random.nextFloat() < 0.05F, (EntityZombie.SyntheticClass_1) null);
        }

        if (object instanceof EntityZombie.GroupDataZombie) {
            EntityZombie.GroupDataZombie entityzombie_groupdatazombie = (EntityZombie.GroupDataZombie) object;

            if (entityzombie_groupdatazombie.b) {
                this.setVillager(true);
            }

            if (entityzombie_groupdatazombie.a) {
                this.setBaby(true);
                if ((double) this.world.random.nextFloat() < 0.05D) {
                    List list = this.world.a(EntityChicken.class, this.getBoundingBox().grow(5.0D, 3.0D, 5.0D), IEntitySelector.b);

                    if (!list.isEmpty()) {
                        EntityChicken entitychicken = (EntityChicken) list.get(0);

                        entitychicken.l(true);
                        this.mount(entitychicken);
                    }
                } else if ((double) this.world.random.nextFloat() < 0.05D) {
                    EntityChicken entitychicken1 = new EntityChicken(this.world);

                    entitychicken1.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
                    entitychicken1.prepare(difficultydamagescaler, (GroupDataEntity) null);
                    entitychicken1.l(true);
                    this.world.addEntity(entitychicken1, CreatureSpawnEvent.SpawnReason.MOUNT);
                    this.mount(entitychicken1);
                }
            }
        }

        this.a(this.random.nextFloat() < f * 0.1F);
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        if (this.getEquipment(4) == null) {
            Calendar calendar = this.world.Y();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
                this.setEquipment(4, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
                this.dropChances[4] = 0.0F;
            }
        }

        this.getAttributeInstance(GenericAttributes.c).b(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806D, 0));
        double d0 = this.random.nextDouble() * 1.5D * (double) f;

        if (d0 > 1.0D) {
            this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).b(new AttributeModifier("Random zombie-spawn bonus", d0, 2));
        }

        if (this.random.nextFloat() < f * 0.05F) {
            this.getAttributeInstance(EntityZombie.a).b(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, 0));
            this.getAttributeInstance(GenericAttributes.maxHealth).b(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, 2));
            this.a(true);
        }

        return (GroupDataEntity) object;
    }

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.bZ();

        if (itemstack != null && itemstack.getItem() == Items.GOLDEN_APPLE && itemstack.getData() == 0 && this.isVillager() && this.hasEffect(MobEffectList.WEAKNESS)) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                --itemstack.count;
            }

            if (itemstack.count <= 0) {
                entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
            }

            if (!this.world.isClientSide) {
                this.a(this.random.nextInt(2401) + 3600);
            }

            return true;
        } else {
            return false;
        }
    }

    protected void a(int i) {
        this.bn = i;
        this.getDataWatcher().watch(14, Byte.valueOf((byte) 1));
        this.removeEffect(MobEffectList.WEAKNESS.id);
        this.addEffect(new MobEffect(MobEffectList.INCREASE_DAMAGE.id, i, Math.min(this.world.getDifficulty().a() - 1, 0)));
        this.world.broadcastEntityEffect(this, (byte) 16);
    }

    protected boolean isTypeNotPersistent() {
        return !this.cp();
    }

    public boolean cp() {
        return this.getDataWatcher().getByte(14) == 1;
    }

    protected void cq() {
        EntityVillager entityvillager = new EntityVillager(this.world);

        entityvillager.m(this);
        entityvillager.prepare(this.world.E(new BlockPosition(entityvillager)), (GroupDataEntity) null);
        entityvillager.cp();
        if (this.isBaby()) {
            entityvillager.setAgeRaw(-24000);
        }

        this.world.kill(this);
        entityvillager.k(this.ce());
        if (this.hasCustomName()) {
            entityvillager.setCustomName(this.getCustomName());
            entityvillager.setCustomNameVisible(this.getCustomNameVisible());
        }

        this.world.addEntity(entityvillager, CreatureSpawnEvent.SpawnReason.CURED); // CraftBukkit - add SpawnReason
        entityvillager.addEffect(new MobEffect(MobEffectList.CONFUSION.id, 200, 0));
        this.world.a((EntityHuman) null, 1017, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0);
    }

    protected int cr() {
        int i = 1;

        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = (int) this.locX - 4; k < (int) this.locX + 4 && j < 14; ++k) {
                for (int l = (int) this.locY - 4; l < (int) this.locY + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.locZ - 4; i1 < (int) this.locZ + 4 && j < 14; ++i1) {
                        Block block = this.world.getType(blockposition_mutableblockposition.c(k, l, i1)).getBlock();

                        if (block == Blocks.IRON_BARS || block == Blocks.BED) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    public void n(boolean flag) {
        this.a(flag ? 0.5F : 1.0F);
    }

    public final void setSize(float f, float f1) { // CraftBukkit - public
        boolean flag = this.bp > 0.0F && this.bq > 0.0F;

        this.bp = f;
        this.bq = f1;
        if (!flag) {
            this.a(1.0F);
        }

    }

    protected final void a(float f) {
        super.setSize(this.bp * f, this.bq * f);
    }

    public double am() {
        return this.isBaby() ? 0.0D : -0.35D;
    }

    public void die(DamageSource damagesource) {
        // super.die(damagesource); // CraftBukkit
        if (damagesource.getEntity() instanceof EntityCreeper && !(this instanceof EntityPigZombie) && ((EntityCreeper) damagesource.getEntity()).isPowered() && ((EntityCreeper) damagesource.getEntity()).cp()) {
            ((EntityCreeper) damagesource.getEntity()).cq();
            // CraftBukkit start
            // this.a(new ItemStack(Items.SKULL, 1, 2), 0.0F);
            headDrop = new ItemStack(Items.SKULL, 1, 2);
            // CraftBukkit end
        }

        super.die(damagesource); // CraftBukkit - moved from above
    }

    static class SyntheticClass_1 {    }

    class GroupDataZombie implements GroupDataEntity {

        public boolean a;
        public boolean b;

        private GroupDataZombie(boolean flag, boolean flag1) {
            this.a = false;
            this.b = false;
            this.a = flag;
            this.b = flag1;
        }

        GroupDataZombie(boolean flag, boolean flag1, EntityZombie.SyntheticClass_1 entityzombie_syntheticclass_1) {
            this(flag, flag1);
        }
    }
}
