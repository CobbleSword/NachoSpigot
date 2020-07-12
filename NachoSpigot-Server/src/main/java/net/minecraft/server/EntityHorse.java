package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;

import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason; // CraftBukkit

public class EntityHorse extends EntityAnimal implements IInventoryListener {

    private static final Predicate<Entity> bs = new Predicate() {
        public boolean a(Entity entity) {
            return entity instanceof EntityHorse && ((EntityHorse) entity).cA();
        }

        public boolean apply(Object object) {
            return this.a((Entity) object);
        }
    };
    public static final IAttribute attributeJumpStrength = (new AttributeRanged((IAttribute) null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).a("Jump Strength").a(true);
    private static final String[] bu = new String[] { null, "textures/entity/horse/armor/horse_armor_iron.png", "textures/entity/horse/armor/horse_armor_gold.png", "textures/entity/horse/armor/horse_armor_diamond.png"};
    private static final String[] bv = new String[] { "", "meo", "goo", "dio"};
    private static final int[] bw = new int[] { 0, 5, 7, 11};
    private static final String[] bx = new String[] { "textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
    private static final String[] by = new String[] { "hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    private static final String[] bz = new String[] { null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] bA = new String[] { "", "wo_", "wmo", "wdo", "bdo"};
    private int bB;
    private int bC;
    private int bD;
    public int bm;
    public int bo;
    protected boolean bp;
    public InventoryHorseChest inventoryChest;
    private boolean bF;
    protected int bq;
    protected float br;
    private boolean bG;
    private float bH;
    private float bI;
    private float bJ;
    private float bK;
    private float bL;
    private float bM;
    private int bN;
    private String bO;
    private String[] bP = new String[3];
    private boolean bQ = false;
    public int maxDomestication = 100; // CraftBukkit - store max domestication value

    public EntityHorse(World world) {
        super(world);
        this.setSize(1.4F, 1.6F);
        this.fireProof = false;
        this.setHasChest(false);
        ((Navigation) this.getNavigation()).a(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.a(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.7D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.loadChest();
    }

    protected void h() {
        super.h();
        this.datawatcher.a(16, Integer.valueOf(0));
        this.datawatcher.a(19, Byte.valueOf((byte) 0));
        this.datawatcher.a(20, Integer.valueOf(0));
        this.datawatcher.a(21, String.valueOf(""));
        this.datawatcher.a(22, Integer.valueOf(0));
    }

    public void setType(int i) {
        this.datawatcher.watch(19, Byte.valueOf((byte) i));
        this.dc();
    }

    public int getType() {
        return this.datawatcher.getByte(19);
    }

    public void setVariant(int i) {
        this.datawatcher.watch(20, Integer.valueOf(i));
        this.dc();
    }

    public int getVariant() {
        return this.datawatcher.getInt(20);
    }

    public String getName() {
        if (this.hasCustomName()) {
            return this.getCustomName();
        } else {
            int i = this.getType();

            switch (i) {
            case 0:
            default:
                return LocaleI18n.get("entity.horse.name");

            case 1:
                return LocaleI18n.get("entity.donkey.name");

            case 2:
                return LocaleI18n.get("entity.mule.name");

            case 3:
                return LocaleI18n.get("entity.zombiehorse.name");

            case 4:
                return LocaleI18n.get("entity.skeletonhorse.name");
            }
        }
    }

    private boolean w(int i) {
        return (this.datawatcher.getInt(16) & i) != 0;
    }

    private void c(int i, boolean flag) {
        int j = this.datawatcher.getInt(16);

        if (flag) {
            this.datawatcher.watch(16, Integer.valueOf(j | i));
        } else {
            this.datawatcher.watch(16, Integer.valueOf(j & ~i));
        }

    }

    public boolean cn() {
        return !this.isBaby();
    }

    public boolean isTame() {
        return this.w(2);
    }

    public boolean cp() {
        return this.cn();
    }

    public String getOwnerUUID() {
        return this.datawatcher.getString(21);
    }

    public void setOwnerUUID(String s) {
        this.datawatcher.watch(21, s);
    }

    public float cu() {
        return 0.5F;
    }

    public void a(boolean flag) {
        if (flag) {
            this.a(this.cu());
        } else {
            this.a(1.0F);
        }

    }

    public boolean cv() {
        return this.bp;
    }

    public void setTame(boolean flag) {
        this.c(2, flag);
    }

    public void m(boolean flag) {
        this.bp = flag;
    }

    public boolean cb() {
        // PaperSpigot start - Configurable undead horse leashing
        if (this.world.paperSpigotConfig.allowUndeadHorseLeashing) {
            return super.cb();
        }
        // PaperSpigot end
        return !this.cR() && super.cb();
    }

    protected void o(float f) {
        if (f > 6.0F && this.cy()) {
            this.r(false);
        }

    }

    public boolean hasChest() {
        return this.w(8);
    }

    public int cx() {
        return this.datawatcher.getInt(22);
    }

    private int f(ItemStack itemstack) {
        if (itemstack == null) {
            return 0;
        } else {
            Item item = itemstack.getItem();

            return item == Items.IRON_HORSE_ARMOR ? 1 : (item == Items.GOLDEN_HORSE_ARMOR ? 2 : (item == Items.DIAMOND_HORSE_ARMOR ? 3 : 0));
        }
    }

    public boolean cy() {
        return this.w(32);
    }

    public boolean cz() {
        return this.w(64);
    }

    public boolean cA() {
        return this.w(16);
    }

    public boolean cB() {
        return this.bF;
    }

    public void e(ItemStack itemstack) {
        this.datawatcher.watch(22, Integer.valueOf(this.f(itemstack)));
        this.dc();
    }

    public void n(boolean flag) {
        this.c(16, flag);
    }

    public void setHasChest(boolean flag) {
        this.c(8, flag);
    }

    public void p(boolean flag) {
        this.bF = flag;
    }

    public void q(boolean flag) {
        this.c(4, flag);
    }

    public int getTemper() {
        return this.bq;
    }

    public void setTemper(int i) {
        this.bq = i;
    }

    public int u(int i) {
        int j = MathHelper.clamp(this.getTemper() + i, 0, this.getMaxDomestication());

        this.setTemper(j);
        return j;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        Entity entity = damagesource.getEntity();

        return this.passenger != null && this.passenger.equals(entity) ? false : super.damageEntity(damagesource, f);
    }

    public int br() {
        return EntityHorse.bw[this.cx()];
    }

    public boolean ae() {
        return this.passenger == null;
    }

    public boolean cD() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locZ);

        this.world.getBiome(new BlockPosition(i, 0, j));
        return true;
    }

    public void cE() {
        if (!this.world.isClientSide && this.hasChest()) {
            this.a(Item.getItemOf(Blocks.CHEST), 1);
            this.setHasChest(false);
        }
    }

    private void cY() {
        this.df();
        if (!this.R()) {
            this.world.makeSound(this, "eating", 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

    }

    public void e(float f, float f1) {
        if (f > 1.0F) {
            this.makeSound("mob.horse.land", 0.4F, 1.0F);
        }

        int i = MathHelper.f((f * 0.5F - 3.0F) * f1);

        if (i > 0) {
            this.damageEntity(DamageSource.FALL, (float) i);
            if (this.passenger != null) {
                this.passenger.damageEntity(DamageSource.FALL, (float) i);
            }

            Block block = this.world.getType(new BlockPosition(this.locX, this.locY - 0.2D - (double) this.lastYaw, this.locZ)).getBlock();

            if (block.getMaterial() != Material.AIR && !this.R()) {
                Block.StepSound block_stepsound = block.stepSound;

                this.world.makeSound(this, block_stepsound.getStepSound(), block_stepsound.getVolume1() * 0.5F, block_stepsound.getVolume2() * 0.75F);
            }

        }
    }

    private int cZ() {
        int i = this.getType();

        return this.hasChest() /* && (i == 1 || i == 2) */ ? 17 : 2; // CraftBukkit - Remove type check
    }

    public void loadChest() {
        InventoryHorseChest inventoryhorsechest = this.inventoryChest;

        this.inventoryChest = new InventoryHorseChest("HorseChest", this.cZ(), this); // CraftBukkit - add this horse
        this.inventoryChest.a(this.getName());
        if (inventoryhorsechest != null) {
            inventoryhorsechest.b(this);
            int i = Math.min(inventoryhorsechest.getSize(), this.inventoryChest.getSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventoryhorsechest.getItem(j);

                if (itemstack != null) {
                    this.inventoryChest.setItem(j, itemstack.cloneItemStack());
                }
            }
        }

        this.inventoryChest.a((IInventoryListener) this);
        this.db();
    }

    private void db() {
        if (!this.world.isClientSide) {
            this.q(this.inventoryChest.getItem(0) != null);
            if (this.cO()) {
                this.e(this.inventoryChest.getItem(1));
            }
        }

    }

    public void a(InventorySubcontainer inventorysubcontainer) {
        int i = this.cx();
        boolean flag = this.cG();

        this.db();
        if (this.ticksLived > 20) {
            if (i == 0 && i != this.cx()) {
                this.makeSound("mob.horse.armor", 0.5F, 1.0F);
            } else if (i != this.cx()) {
                this.makeSound("mob.horse.armor", 0.5F, 1.0F);
            }

            if (!flag && this.cG()) {
                this.makeSound("mob.horse.leather", 0.5F, 1.0F);
            }
        }

    }

    public boolean bR() {
        this.cD();
        return super.bR();
    }

    protected EntityHorse a(Entity entity, double d0) {
        double d1 = Double.MAX_VALUE;
        Entity entity1 = null;
        List list = this.world.a(entity, entity.getBoundingBox().a(d0, d0, d0), EntityHorse.bs);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity2 = (Entity) iterator.next();
            double d2 = entity2.e(entity.locX, entity.locY, entity.locZ);

            if (d2 < d1) {
                entity1 = entity2;
                d1 = d2;
            }
        }

        return (EntityHorse) entity1;
    }

    public double getJumpStrength() {
        return this.getAttributeInstance(EntityHorse.attributeJumpStrength).getValue();
    }

    protected String bp() {
        this.df();
        int i = this.getType();

        return i == 3 ? "mob.horse.zombie.death" : (i == 4 ? "mob.horse.skeleton.death" : (i != 1 && i != 2 ? "mob.horse.death" : "mob.horse.donkey.death"));
    }

    protected Item getLoot() {
        boolean flag = this.random.nextInt(4) == 0;
        int i = this.getType();

        return i == 4 ? Items.BONE : (i == 3 ? (flag ? null : Items.ROTTEN_FLESH) : Items.LEATHER);
    }

    protected String bo() {
        this.df();
        if (this.random.nextInt(3) == 0) {
            this.dh();
        }

        int i = this.getType();

        return i == 3 ? "mob.horse.zombie.hit" : (i == 4 ? "mob.horse.skeleton.hit" : (i != 1 && i != 2 ? "mob.horse.hit" : "mob.horse.donkey.hit"));
    }

    public boolean cG() {
        return this.w(4);
    }

    protected String z() {
        this.df();
        if (this.random.nextInt(10) == 0 && !this.bD()) {
            this.dh();
        }

        int i = this.getType();

        return i == 3 ? "mob.horse.zombie.idle" : (i == 4 ? "mob.horse.skeleton.idle" : (i != 1 && i != 2 ? "mob.horse.idle" : "mob.horse.donkey.idle"));
    }

    protected String cH() {
        this.df();
        this.dh();
        int i = this.getType();

        return i != 3 && i != 4 ? (i != 1 && i != 2 ? "mob.horse.angry" : "mob.horse.donkey.angry") : null;
    }

    protected void a(BlockPosition blockposition, Block block) {
        Block.StepSound block_stepsound = block.stepSound;

        if (this.world.getType(blockposition.up()).getBlock() == Blocks.SNOW_LAYER) {
            block_stepsound = Blocks.SNOW_LAYER.stepSound;
        }

        if (!block.getMaterial().isLiquid()) {
            int i = this.getType();

            if (this.passenger != null && i != 1 && i != 2) {
                ++this.bN;
                if (this.bN > 5 && this.bN % 3 == 0) {
                    this.makeSound("mob.horse.gallop", block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
                    if (i == 0 && this.random.nextInt(10) == 0) {
                        this.makeSound("mob.horse.breathe", block_stepsound.getVolume1() * 0.6F, block_stepsound.getVolume2());
                    }
                } else if (this.bN <= 5) {
                    this.makeSound("mob.horse.wood", block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
                }
            } else if (block_stepsound == Block.f) {
                this.makeSound("mob.horse.wood", block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
            } else {
                this.makeSound("mob.horse.soft", block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
            }
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(EntityHorse.attributeJumpStrength);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(53.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.22499999403953552D);
    }

    public int bV() {
        return 6;
    }

    public int getMaxDomestication() {
        return this.maxDomestication; // CraftBukkit - return stored max domestication instead of 100
    }

    protected float bB() {
        return 0.8F;
    }

    public int w() {
        return 400;
    }

    private void dc() {
        this.bO = null;
    }

    public void g(EntityHuman entityhuman) {
        if (!this.world.isClientSide && (this.passenger == null || this.passenger == entityhuman) && this.isTame()) {
            this.inventoryChest.a(this.getName());
            entityhuman.openHorseInventory(this, this.inventoryChest);
        }

    }

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (itemstack != null && itemstack.getItem() == Items.SPAWN_EGG) {
            return super.a(entityhuman);
        } else if (!this.isTame() && this.cR()) {
            return false;
        } else if (this.isTame() && this.cn() && entityhuman.isSneaking()) {
            this.g(entityhuman);
            return true;
        } else if (this.cp() && this.passenger != null) {
            return super.a(entityhuman);
        } else {
            if (itemstack != null) {
                boolean flag = false;

                if (this.cO()) {
                    byte b0 = -1;

                    if (itemstack.getItem() == Items.IRON_HORSE_ARMOR) {
                        b0 = 1;
                    } else if (itemstack.getItem() == Items.GOLDEN_HORSE_ARMOR) {
                        b0 = 2;
                    } else if (itemstack.getItem() == Items.DIAMOND_HORSE_ARMOR) {
                        b0 = 3;
                    }

                    if (b0 >= 0) {
                        if (!this.isTame()) {
                            this.cW();
                            return true;
                        }

                        this.g(entityhuman);
                        return true;
                    }
                }

                if (!flag && !this.cR()) {
                    float f = 0.0F;
                    short short0 = 0;
                    byte b1 = 0;

                    if (itemstack.getItem() == Items.WHEAT) {
                        f = 2.0F;
                        short0 = 20;
                        b1 = 3;
                    } else if (itemstack.getItem() == Items.SUGAR) {
                        f = 1.0F;
                        short0 = 30;
                        b1 = 3;
                    } else if (Block.asBlock(itemstack.getItem()) == Blocks.HAY_BLOCK) {
                        f = 20.0F;
                        short0 = 180;
                    } else if (itemstack.getItem() == Items.APPLE) {
                        f = 3.0F;
                        short0 = 60;
                        b1 = 3;
                    } else if (itemstack.getItem() == Items.GOLDEN_CARROT) {
                        f = 4.0F;
                        short0 = 60;
                        b1 = 5;
                        if (this.isTame() && this.getAge() == 0) {
                            flag = true;
                            this.c(entityhuman);
                        }
                    } else if (itemstack.getItem() == Items.GOLDEN_APPLE) {
                        f = 10.0F;
                        short0 = 240;
                        b1 = 10;
                        if (this.isTame() && this.getAge() == 0) {
                            flag = true;
                            this.c(entityhuman);
                        }
                    }

                    if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
                        this.heal(f, RegainReason.EATING); // CraftBukkit
                        flag = true;
                    }

                    if (!this.cn() && short0 > 0) {
                        this.setAge(short0);
                        flag = true;
                    }

                    if (b1 > 0 && (flag || !this.isTame()) && b1 < this.getMaxDomestication()) {
                        flag = true;
                        this.u(b1);
                    }

                    if (flag) {
                        this.cY();
                    }
                }

                if (!this.isTame() && !flag) {
                    if (itemstack != null && itemstack.a(entityhuman, (EntityLiving) this)) {
                        return true;
                    }

                    this.cW();
                    return true;
                }

                if (!flag && this.cP() && !this.hasChest() && itemstack.getItem() == Item.getItemOf(Blocks.CHEST)) {
                    this.setHasChest(true);
                    this.makeSound("mob.chickenplop", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    flag = true;
                    this.loadChest();
                }

                if (!flag && this.cp() && !this.cG() && itemstack.getItem() == Items.SADDLE) {
                    this.g(entityhuman);
                    return true;
                }

                if (flag) {
                    if (!entityhuman.abilities.canInstantlyBuild && --itemstack.count == 0) {
                        entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
                    }

                    return true;
                }
            }

            if (this.cp() && this.passenger == null) {
                if (itemstack != null && itemstack.a(entityhuman, (EntityLiving) this)) {
                    return true;
                } else {
                    this.i(entityhuman);
                    return true;
                }
            } else {
                return super.a(entityhuman);
            }
        }
    }

    private void i(EntityHuman entityhuman) {
        entityhuman.yaw = this.yaw;
        entityhuman.pitch = this.pitch;
        this.r(false);
        this.s(false);
        if (!this.world.isClientSide) {
            entityhuman.mount(this);
        }

    }

    public boolean cO() {
        return this.getType() == 0;
    }

    public boolean cP() {
        int i = this.getType();

        return i == 2 || i == 1;
    }

    protected boolean bD() {
        return this.passenger != null && this.cG() ? true : this.cy() || this.cz();
    }

    public boolean cR() {
        int i = this.getType();

        return i == 3 || i == 4;
    }

    public boolean cS() {
        return this.cR() || this.getType() == 2;
    }

    public boolean d(ItemStack itemstack) {
        return false;
    }

    private void de() {
        this.bm = 1;
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        /* CraftBukkit start - Handle chest dropping in dropDeathLoot below
        if (!this.world.isClientSide) {
            this.dropChest();
        }
        // CraftBukkit end */
    }

    // CraftBukkit start - Add method
    @Override
    protected void dropDeathLoot(boolean flag, int i) {
        super.dropDeathLoot(flag, i);

        // Moved from die method above
        if (!this.world.isClientSide) {
            this.dropChest();
        }
    }
    // CraftBukkit end

    public void m() {
        if (this.random.nextInt(200) == 0) {
            this.de();
        }

        super.m();
        if (!this.world.isClientSide) {
            if (this.random.nextInt(900) == 0 && this.deathTicks == 0) {
                this.heal(1.0F, RegainReason.REGEN); // CraftBukkit
            }

            if (!this.cy() && this.passenger == null && this.random.nextInt(300) == 0 && this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.locY) - 1, MathHelper.floor(this.locZ))).getBlock() == Blocks.GRASS) {
                this.r(true);
            }

            if (this.cy() && ++this.bB > 50) {
                this.bB = 0;
                this.r(false);
            }

            if (this.cA() && !this.cn() && !this.cy()) {
                EntityHorse entityhorse = this.a(this, 16.0D);

                if (entityhorse != null && this.h((Entity) entityhorse) > 4.0D) {
                    this.navigation.a((Entity) entityhorse);
                }
            }
        }

    }

    public void t_() {
        super.t_();
        if (this.world.isClientSide && this.datawatcher.a()) {
            this.datawatcher.e();
            this.dc();
        }

        if (this.bC > 0 && ++this.bC > 30) {
            this.bC = 0;
            this.c(128, false);
        }

        if (!this.world.isClientSide && this.bD > 0 && ++this.bD > 20) {
            this.bD = 0;
            this.s(false);
        }

        if (this.bm > 0 && ++this.bm > 8) {
            this.bm = 0;
        }

        if (this.bo > 0) {
            ++this.bo;
            if (this.bo > 300) {
                this.bo = 0;
            }
        }

        this.bI = this.bH;
        if (this.cy()) {
            this.bH += (1.0F - this.bH) * 0.4F + 0.05F;
            if (this.bH > 1.0F) {
                this.bH = 1.0F;
            }
        } else {
            this.bH += (0.0F - this.bH) * 0.4F - 0.05F;
            if (this.bH < 0.0F) {
                this.bH = 0.0F;
            }
        }

        this.bK = this.bJ;
        if (this.cz()) {
            this.bI = this.bH = 0.0F;
            this.bJ += (1.0F - this.bJ) * 0.4F + 0.05F;
            if (this.bJ > 1.0F) {
                this.bJ = 1.0F;
            }
        } else {
            this.bG = false;
            this.bJ += (0.8F * this.bJ * this.bJ * this.bJ - this.bJ) * 0.6F - 0.05F;
            if (this.bJ < 0.0F) {
                this.bJ = 0.0F;
            }
        }

        this.bM = this.bL;
        if (this.w(128)) {
            this.bL += (1.0F - this.bL) * 0.7F + 0.05F;
            if (this.bL > 1.0F) {
                this.bL = 1.0F;
            }
        } else {
            this.bL += (0.0F - this.bL) * 0.7F - 0.05F;
            if (this.bL < 0.0F) {
                this.bL = 0.0F;
            }
        }

    }

    private void df() {
        if (!this.world.isClientSide) {
            this.bC = 1;
            this.c(128, true);
        }

    }

    private boolean dg() {
        return this.passenger == null && this.vehicle == null && this.isTame() && this.cn() && !this.cS() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    public void f(boolean flag) {
        this.c(32, flag);
    }

    public void r(boolean flag) {
        this.f(flag);
    }

    public void s(boolean flag) {
        if (flag) {
            this.r(false);
        }

        this.c(64, flag);
    }

    private void dh() {
        if (!this.world.isClientSide) {
            this.bD = 1;
            this.s(true);
        }

    }

    public void cW() {
        this.dh();
        String s = this.cH();

        if (s != null) {
            this.makeSound(s, this.bB(), this.bC());
        }

    }

    public void dropChest() {
        this.a((Entity) this, this.inventoryChest);
        this.cE();
    }

    private void a(Entity entity, InventoryHorseChest inventoryhorsechest) {
        if (inventoryhorsechest != null && !this.world.isClientSide) {
            for (int i = 0; i < inventoryhorsechest.getSize(); ++i) {
                ItemStack itemstack = inventoryhorsechest.getItem(i);

                if (itemstack != null) {
                    this.a(itemstack, 0.0F);
                }
            }

        }
    }

    public boolean h(EntityHuman entityhuman) {
        this.setOwnerUUID(entityhuman.getUniqueID().toString());
        this.setTame(true);
        return true;
    }

    public void g(float f, float f1) {
        if (this.passenger != null && this.passenger instanceof EntityLiving && this.cG()) {
            this.lastYaw = this.yaw = this.passenger.yaw;
            this.pitch = this.passenger.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aK = this.aI = this.yaw;
            f = ((EntityLiving) this.passenger).aZ * 0.5F;
            f1 = ((EntityLiving) this.passenger).ba;
            if (f1 <= 0.0F) {
                f1 *= 0.25F;
                this.bN = 0;
            }

            if (this.onGround && this.br == 0.0F && this.cz() && !this.bG) {
                f = 0.0F;
                f1 = 0.0F;
            }

            if (this.br > 0.0F && !this.cv() && this.onGround) {
                this.motY = this.getJumpStrength() * (double) this.br;
                if (this.hasEffect(MobEffectList.JUMP)) {
                    this.motY += (double) ((float) (this.getEffect(MobEffectList.JUMP).getAmplifier() + 1) * 0.1F);
                }

                this.m(true);
                this.ai = true;
                if (f1 > 0.0F) {
                    float f2 = MathHelper.sin(this.yaw * 3.1415927F / 180.0F);
                    float f3 = MathHelper.cos(this.yaw * 3.1415927F / 180.0F);

                    this.motX += (double) (-0.4F * f2 * this.br);
                    this.motZ += (double) (0.4F * f3 * this.br);
                    this.makeSound("mob.horse.jump", 0.4F, 1.0F);
                }

                this.br = 0.0F;
            }

            this.S = 1.0F;
            this.aM = this.bI() * 0.1F;
            if (!this.world.isClientSide) {
                this.k((float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
                super.g(f, f1);
            }

            if (this.onGround) {
                this.br = 0.0F;
                this.m(false);
            }

            this.aA = this.aB;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.aB += (f4 - this.aB) * 0.4F;
            this.aC += this.aB;
        } else {
            this.S = 0.5F;
            this.aM = 0.02F;
            super.g(f, f1);
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("EatingHaystack", this.cy());
        nbttagcompound.setBoolean("ChestedHorse", this.hasChest());
        nbttagcompound.setBoolean("HasReproduced", this.cB());
        nbttagcompound.setBoolean("Bred", this.cA());
        nbttagcompound.setInt("Type", this.getType());
        nbttagcompound.setInt("Variant", this.getVariant());
        nbttagcompound.setInt("Temper", this.getTemper());
        nbttagcompound.setBoolean("Tame", this.isTame());
        nbttagcompound.setString("OwnerUUID", this.getOwnerUUID());
        nbttagcompound.setInt("Bukkit.MaxDomestication", this.maxDomestication); // CraftBukkit
        if (this.hasChest()) {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 2; i < this.inventoryChest.getSize(); ++i) {
                ItemStack itemstack = this.inventoryChest.getItem(i);

                if (itemstack != null) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    nbttagcompound1.setByte("Slot", (byte) i);
                    itemstack.save(nbttagcompound1);
                    nbttaglist.add(nbttagcompound1);
                }
            }

            nbttagcompound.set("Items", nbttaglist);
        }

        if (this.inventoryChest.getItem(1) != null) {
            nbttagcompound.set("ArmorItem", this.inventoryChest.getItem(1).save(new NBTTagCompound()));
        }

        if (this.inventoryChest.getItem(0) != null) {
            nbttagcompound.set("SaddleItem", this.inventoryChest.getItem(0).save(new NBTTagCompound()));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.r(nbttagcompound.getBoolean("EatingHaystack"));
        this.n(nbttagcompound.getBoolean("Bred"));
        this.setHasChest(nbttagcompound.getBoolean("ChestedHorse"));
        this.p(nbttagcompound.getBoolean("HasReproduced"));
        this.setType(nbttagcompound.getInt("Type"));
        this.setVariant(nbttagcompound.getInt("Variant"));
        this.setTemper(nbttagcompound.getInt("Temper"));
        this.setTame(nbttagcompound.getBoolean("Tame"));
        String s = "";

        if (nbttagcompound.hasKeyOfType("OwnerUUID", 8)) {
            s = nbttagcompound.getString("OwnerUUID");
        } else {
            String s1 = nbttagcompound.getString("Owner");
            // Spigot start
            if ( s1 == null || s1.isEmpty() )
            {
                if (nbttagcompound.hasKey("OwnerName")) {
                String owner = nbttagcompound.getString("OwnerName");
                    if (owner != null && !owner.isEmpty()) {
                        s1 = owner;
                    }
                }
            }
            // Spigot end

            s = NameReferencingFileConverter.a(s1);
        }

        if (s.length() > 0) {
            this.setOwnerUUID(s);
        }

        // CraftBukkit start
        if (nbttagcompound.hasKey("Bukkit.MaxDomestication")) {
            this.maxDomestication = nbttagcompound.getInt("Bukkit.MaxDomestication");
        }
        // CraftBukkit end

        AttributeInstance attributeinstance = this.getAttributeMap().a("Speed");

        if (attributeinstance != null) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(attributeinstance.b() * 0.25D);
        }

        if (this.hasChest()) {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

            this.loadChest();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
                int j = nbttagcompound1.getByte("Slot") & 255;

                if (j >= 2 && j < this.inventoryChest.getSize()) {
                    this.inventoryChest.setItem(j, ItemStack.createStack(nbttagcompound1));
                }
            }
        }

        ItemStack itemstack;

        if (nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
            itemstack = ItemStack.createStack(nbttagcompound.getCompound("ArmorItem"));
            if (itemstack != null && a(itemstack.getItem())) {
                this.inventoryChest.setItem(1, itemstack);
            }
        }

        if (nbttagcompound.hasKeyOfType("SaddleItem", 10)) {
            itemstack = ItemStack.createStack(nbttagcompound.getCompound("SaddleItem"));
            if (itemstack != null && itemstack.getItem() == Items.SADDLE) {
                this.inventoryChest.setItem(0, itemstack);
            }
        } else if (nbttagcompound.getBoolean("Saddle")) {
            this.inventoryChest.setItem(0, new ItemStack(Items.SADDLE));
        }

        this.db();
    }

    public boolean mate(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (entityanimal.getClass() != this.getClass()) {
            return false;
        } else {
            EntityHorse entityhorse = (EntityHorse) entityanimal;

            if (this.dg() && entityhorse.dg()) {
                int i = this.getType();
                int j = entityhorse.getType();

                return i == j || i == 0 && j == 1 || i == 1 && j == 0;
            } else {
                return false;
            }
        }
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        EntityHorse entityhorse = (EntityHorse) entityageable;
        EntityHorse entityhorse1 = new EntityHorse(this.world);
        int i = this.getType();
        int j = entityhorse.getType();
        int k = 0;

        if (i == j) {
            k = i;
        } else if (i == 0 && j == 1 || i == 1 && j == 0) {
            k = 2;
        }

        if (k == 0) {
            int l = this.random.nextInt(9);
            int i1;

            if (l < 4) {
                i1 = this.getVariant() & 255;
            } else if (l < 8) {
                i1 = entityhorse.getVariant() & 255;
            } else {
                i1 = this.random.nextInt(7);
            }

            int j1 = this.random.nextInt(5);

            if (j1 < 2) {
                i1 |= this.getVariant() & '\uff00';
            } else if (j1 < 4) {
                i1 |= entityhorse.getVariant() & '\uff00';
            } else {
                i1 |= this.random.nextInt(5) << 8 & '\uff00';
            }

            entityhorse1.setVariant(i1);
        }

        entityhorse1.setType(k);
        double d0 = this.getAttributeInstance(GenericAttributes.maxHealth).b() + entityageable.getAttributeInstance(GenericAttributes.maxHealth).b() + (double) this.di();

        entityhorse1.getAttributeInstance(GenericAttributes.maxHealth).setValue(d0 / 3.0D);
        double d1 = this.getAttributeInstance(EntityHorse.attributeJumpStrength).b() + entityageable.getAttributeInstance(EntityHorse.attributeJumpStrength).b() + this.dj();

        entityhorse1.getAttributeInstance(EntityHorse.attributeJumpStrength).setValue(d1 / 3.0D);
        double d2 = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).b() + entityageable.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).b() + this.dk();

        entityhorse1.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(d2 / 3.0D);
        return entityhorse1;
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity);
        boolean flag = false;
        int i = 0;
        int j;

        if (object instanceof EntityHorse.GroupDataHorse) {
            j = ((EntityHorse.GroupDataHorse) object).a;
            i = ((EntityHorse.GroupDataHorse) object).b & 255 | this.random.nextInt(5) << 8;
        } else {
            if (this.random.nextInt(10) == 0) {
                j = 1;
            } else {
                int k = this.random.nextInt(7);
                int l = this.random.nextInt(5);

                j = 0;
                i = k | l << 8;
            }

            object = new EntityHorse.GroupDataHorse(j, i);
        }

        this.setType(j);
        this.setVariant(i);
        if (this.random.nextInt(5) == 0) {
            this.setAgeRaw(-24000);
        }

        if (j != 4 && j != 3) {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) this.di());
            if (j == 0) {
                this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.dk());
            } else {
                this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.17499999701976776D);
            }
        } else {
            this.getAttributeInstance(GenericAttributes.maxHealth).setValue(15.0D);
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
        }

        if (j != 2 && j != 1) {
            this.getAttributeInstance(EntityHorse.attributeJumpStrength).setValue(this.dj());
        } else {
            this.getAttributeInstance(EntityHorse.attributeJumpStrength).setValue(0.5D);
        }

        this.setHealth(this.getMaxHealth());
        return (GroupDataEntity) object;
    }

    public void v(int i) {
        if (this.cG()) {
            // CraftBukkit start - fire HorseJumpEvent, use event power
            if (i < 0) {
                i = 0;
            }

            float power;
            if (i >= 90) {
                power = 1.0F;
            } else {
                power = 0.4F + 0.4F * (float) i / 90.0F;
            }

            org.bukkit.event.entity.HorseJumpEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callHorseJumpEvent(this, power);
            if (!event.isCancelled()) {
                this.bG = true;
                this.dh();
                this.br = power;
            }
            // CraftBukkit end
        }

    }

    public void al() {
        super.al();
        if (this.bK > 0.0F) {
            float f = MathHelper.sin(this.aI * 3.1415927F / 180.0F);
            float f1 = MathHelper.cos(this.aI * 3.1415927F / 180.0F);
            float f2 = 0.7F * this.bK;
            float f3 = 0.15F * this.bK;

            this.passenger.setPosition(this.locX + (double) (f2 * f), this.locY + this.an() + this.passenger.am() + (double) f3, this.locZ - (double) (f2 * f1));
            if (this.passenger instanceof EntityLiving) {
                ((EntityLiving) this.passenger).aI = this.aI;
            }
        }

    }

    private float di() {
        return 15.0F + (float) this.random.nextInt(8) + (float) this.random.nextInt(9);
    }

    private double dj() {
        return 0.4000000059604645D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D;
    }

    private double dk() {
        return (0.44999998807907104D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D;
    }

    public static boolean a(Item item) {
        return item == Items.IRON_HORSE_ARMOR || item == Items.GOLDEN_HORSE_ARMOR || item == Items.DIAMOND_HORSE_ARMOR;
    }

    public boolean k_() {
        return false;
    }

    public float getHeadHeight() {
        return this.length;
    }

    public boolean d(int i, ItemStack itemstack) {
        if (i == 499 && this.cP()) {
            if (itemstack == null && this.hasChest()) {
                this.setHasChest(false);
                this.loadChest();
                return true;
            }

            if (itemstack != null && itemstack.getItem() == Item.getItemOf(Blocks.CHEST) && !this.hasChest()) {
                this.setHasChest(true);
                this.loadChest();
                return true;
            }
        }

        int j = i - 400;

        if (j >= 0 && j < 2 && j < this.inventoryChest.getSize()) {
            if (j == 0 && itemstack != null && itemstack.getItem() != Items.SADDLE) {
                return false;
            } else if (j == 1 && (itemstack != null && !a(itemstack.getItem()) || !this.cO())) {
                return false;
            } else {
                this.inventoryChest.setItem(j, itemstack);
                this.db();
                return true;
            }
        } else {
            int k = i - 500 + 2;

            if (k >= 2 && k < this.inventoryChest.getSize()) {
                this.inventoryChest.setItem(k, itemstack);
                return true;
            } else {
                return false;
            }
        }
    }

    public static class GroupDataHorse implements GroupDataEntity {

        public int a;
        public int b;

        public GroupDataHorse(int i, int j) {
            this.a = i;
            this.b = j;
        }
    }
}
