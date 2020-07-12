package net.minecraft.server;

public class EntityChicken extends EntityAnimal {

    public float bm;
    public float bo;
    public float bp;
    public float bq;
    public float br = 1.0F;
    public int bs;
    public boolean bt;

    public EntityChicken(World world) {
        super(world);
        this.setSize(0.4F, 0.7F);
        this.bs = this.random.nextInt(6000) + 6000;
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.4D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, Items.WHEAT_SEEDS, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    public float getHeadHeight() {
        return this.length;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(4.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    public void m() {
        // CraftBukkit start
        if (this.isChickenJockey()) {
            this.persistent = !this.isTypeNotPersistent();
        }
        // CraftBukkit end
        super.m();
        this.bq = this.bm;
        this.bp = this.bo;
        this.bo = (float) ((double) this.bo + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.bo = MathHelper.a(this.bo, 0.0F, 1.0F);
        if (!this.onGround && this.br < 1.0F) {
            this.br = 1.0F;
        }

        this.br = (float) ((double) this.br * 0.9D);
        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }

        this.bm += this.br * 2.0F;
        if (!this.world.isClientSide && !this.isBaby() && !this.isChickenJockey() && --this.bs <= 0) {
            this.makeSound("mob.chicken.plop", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.a(Items.EGG, 1);
            this.bs = this.random.nextInt(6000) + 6000;
        }

    }

    public void e(float f, float f1) {}

    protected String z() {
        return "mob.chicken.say";
    }

    protected String bo() {
        return "mob.chicken.hurt";
    }

    protected String bp() {
        return "mob.chicken.hurt";
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.makeSound("mob.chicken.step", 0.15F, 1.0F);
    }

    protected Item getLoot() {
        return Items.FEATHER;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        int j = this.random.nextInt(3) + this.random.nextInt(1 + i);

        for (int k = 0; k < j; ++k) {
            this.a(Items.FEATHER, 1);
        }

        if (this.isBurning()) {
            this.a(Items.COOKED_CHICKEN, 1);
        } else {
            this.a(Items.CHICKEN, 1);
        }

    }

    public EntityChicken b(EntityAgeable entityageable) {
        return new EntityChicken(this.world);
    }

    public boolean d(ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() == Items.WHEAT_SEEDS;
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.bt = nbttagcompound.getBoolean("IsChickenJockey");
        if (nbttagcompound.hasKey("EggLayTime")) {
            this.bs = nbttagcompound.getInt("EggLayTime");
        }

    }

    protected int getExpValue(EntityHuman entityhuman) {
        return this.isChickenJockey() ? 10 : super.getExpValue(entityhuman);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("IsChickenJockey", this.bt);
        nbttagcompound.setInt("EggLayTime", this.bs);
    }

    protected boolean isTypeNotPersistent() {
        return this.isChickenJockey() && this.passenger == null;
    }

    public void al() {
        super.al();
        float f = MathHelper.sin(this.aI * 3.1415927F / 180.0F);
        float f1 = MathHelper.cos(this.aI * 3.1415927F / 180.0F);
        float f2 = 0.1F;
        float f3 = 0.0F;

        this.passenger.setPosition(this.locX + (double) (f2 * f), this.locY + (double) (this.length * 0.5F) + this.passenger.am() + (double) f3, this.locZ - (double) (f2 * f1));
        if (this.passenger instanceof EntityLiving) {
            ((EntityLiving) this.passenger).aI = this.aI;
        }

    }

    public boolean isChickenJockey() {
        return this.bt;
    }

    public void l(boolean flag) {
        this.bt = flag;
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
