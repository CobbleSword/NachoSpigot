package net.minecraft.server;

public class EntityMinecartFurnace extends EntityMinecartAbstract {

    private int c;
    public double a;
    public double b;

    public EntityMinecartFurnace(World world) {
        super(world);
    }

    public EntityMinecartFurnace(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public EnumMinecartType s() {
        return EnumMinecartType.FURNACE;
    }

    protected void h() {
        super.h();
        this.datawatcher.a(16, new Byte((byte) 0));
    }

    public void t_() {
        super.t_();
        if (this.c > 0) {
            --this.c;
        }

        if (this.c <= 0) {
            this.a = this.b = 0.0D;
        }

        this.i(this.c > 0);
        if (this.j() && this.random.nextInt(4) == 0) {
            this.world.addParticle(EnumParticle.SMOKE_LARGE, this.locX, this.locY + 0.8D, this.locZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }

    }

    protected double m() {
        return 0.2D;
    }

    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (!damagesource.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.a(new ItemStack(Blocks.FURNACE, 1), 0.0F);
        }

    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        super.a(blockposition, iblockdata);
        double d0 = this.a * this.a + this.b * this.b;

        if (d0 > 1.0E-4D && this.motX * this.motX + this.motZ * this.motZ > 0.001D) {
            d0 = (double) MathHelper.sqrt(d0);
            // PaperSpigot - Don't lose all your velocity on corners
            // https://bugs.mojang.com/browse/MC-51053?focusedCommentId=223854
            double d1 = (double) MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
            this.a = (motX / d1) * d0;
            this.b = (motZ / d1) * d0;
            // PaperSpigot end
        }

    }

    protected void o() {
        double d0 = this.a * this.a + this.b * this.b;

        if (d0 > 1.0E-4D) {
            d0 = (double) MathHelper.sqrt(d0);
            this.a /= d0;
            this.b /= d0;
            double d1 = 1.0D;

            this.motX *= 0.800000011920929D;
            this.motY *= 0.0D;
            this.motZ *= 0.800000011920929D;
            this.motX += this.a * d1;
            this.motZ += this.b * d1;
        } else {
            this.motX *= 0.9800000190734863D;
            this.motY *= 0.0D;
            this.motZ *= 0.9800000190734863D;
        }

        super.o();
    }

    public boolean e(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (itemstack != null && itemstack.getItem() == Items.COAL) {
            if (!entityhuman.abilities.canInstantlyBuild && --itemstack.count == 0) {
                entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
            }

            this.c += 3600;
        }

        this.a = this.locX - entityhuman.locX;
        this.b = this.locZ - entityhuman.locZ;
        return true;
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setDouble("PushX", this.a);
        nbttagcompound.setDouble("PushZ", this.b);
        nbttagcompound.setShort("Fuel", (short) this.c);
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.a = nbttagcompound.getDouble("PushX");
        this.b = nbttagcompound.getDouble("PushZ");
        this.c = nbttagcompound.getShort("Fuel");
    }

    protected boolean j() {
        return (this.datawatcher.getByte(16) & 1) != 0;
    }

    protected void i(boolean flag) {
        if (flag) {
            this.datawatcher.watch(16, Byte.valueOf((byte) (this.datawatcher.getByte(16) | 1)));
        } else {
            this.datawatcher.watch(16, Byte.valueOf((byte) (this.datawatcher.getByte(16) & -2)));
        }

    }

    public IBlockData u() {
        return (this.j() ? Blocks.LIT_FURNACE : Blocks.FURNACE).getBlockData().set(BlockFurnace.FACING, EnumDirection.NORTH);
    }
}
