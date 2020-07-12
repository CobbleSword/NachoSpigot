package net.minecraft.server;

import java.util.Random;

public class BlockCake extends Block {

    public static final BlockStateInteger BITES = BlockStateInteger.of("bites", 0, 6);

    protected BlockCake() {
        super(Material.CAKE);
        this.j(this.blockStateList.getBlockData().set(BlockCake.BITES, Integer.valueOf(0)));
        this.a(true);
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = 0.0625F;
        float f1 = (float) (1 + ((Integer) iblockaccess.getType(blockposition).get(BlockCake.BITES)).intValue() * 2) / 16.0F;
        float f2 = 0.5F;

        this.a(f1, 0.0F, f, 1.0F - f, f2, 1.0F - f);
    }

    public void j() {
        float f = 0.0625F;
        float f1 = 0.5F;

        this.a(f, 0.0F, f, 1.0F - f, f1, 1.0F - f);
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        float f = 0.0625F;
        float f1 = (float) (1 + ((Integer) iblockdata.get(BlockCake.BITES)).intValue() * 2) / 16.0F;
        float f2 = 0.5F;

        return new AxisAlignedBB((double) ((float) blockposition.getX() + f1), (double) blockposition.getY(), (double) ((float) blockposition.getZ() + f), (double) ((float) (blockposition.getX() + 1) - f), (double) ((float) blockposition.getY() + f2), (double) ((float) (blockposition.getZ() + 1) - f));
    }

    public boolean d() {
        return false;
    }

    public boolean c() {
        return false;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        this.b(world, blockposition, iblockdata, entityhuman);
        return true;
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.b(world, blockposition, world.getType(blockposition), entityhuman);
    }

    private void b(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (entityhuman.j(false)) {
            entityhuman.b(StatisticList.H);
            // CraftBukkit start
            // entityhuman.getFoodData().eat(2, 0.1F);
            int oldFoodLevel = entityhuman.getFoodData().foodLevel;

            org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callFoodLevelChangeEvent(entityhuman, 2 + oldFoodLevel);

            if (!event.isCancelled()) {
                entityhuman.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 0.1F);
            }

            ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutUpdateHealth(((EntityPlayer) entityhuman).getBukkitEntity().getScaledHealth(), entityhuman.getFoodData().foodLevel, entityhuman.getFoodData().saturationLevel));
            // CraftBukkit end
            int i = ((Integer) iblockdata.get(BlockCake.BITES)).intValue();

            if (i < 6) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockCake.BITES, Integer.valueOf(i + 1)), 3);
            } else {
                world.setAir(blockposition);
            }

        }
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return super.canPlace(world, blockposition) ? this.e(world, blockposition) : false;
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!this.e(world, blockposition)) {
            world.setAir(blockposition);
        }

    }

    private boolean e(World world, BlockPosition blockposition) {
        return world.getType(blockposition.down()).getBlock().getMaterial().isBuildable();
    }

    public int a(Random random) {
        return 0;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return null;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCake.BITES, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockCake.BITES)).intValue();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockCake.BITES});
    }

    public int l(World world, BlockPosition blockposition) {
        return (7 - ((Integer) world.getType(blockposition).get(BlockCake.BITES)).intValue()) * 2;
    }

    public boolean isComplexRedstone() {
        return true;
    }
}
