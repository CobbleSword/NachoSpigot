package net.minecraft.server;

import java.util.Random;

public class BlockSnow extends Block {

    public static final BlockStateInteger LAYERS = BlockStateInteger.of("layers", 1, 8);

    protected BlockSnow() {
        super(Material.PACKED_ICE);
        this.j(this.blockStateList.getBlockData().set(BlockSnow.LAYERS, Integer.valueOf(1)));
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.a(true);
        this.a(CreativeModeTab.c);
        this.j();
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return ((Integer) iblockaccess.getType(blockposition).get(BlockSnow.LAYERS)).intValue() < 5;
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = ((Integer) iblockdata.get(BlockSnow.LAYERS)).intValue() - 1;
        float f = 0.125F;

        return new AxisAlignedBB((double) blockposition.getX() + this.minX, (double) blockposition.getY() + this.minY, (double) blockposition.getZ() + this.minZ, (double) blockposition.getX() + this.maxX, (double) ((float) blockposition.getY() + (float) i * f), (double) blockposition.getZ() + this.maxZ);
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public void j() {
        this.b(0);
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        this.b(((Integer) iblockdata.get(BlockSnow.LAYERS)).intValue());
    }

    protected void b(int i) {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, (float) i / 8.0F, 1.0F);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition.down());
        Block block = iblockdata.getBlock();

        return block != Blocks.ICE && block != Blocks.PACKED_ICE ? (block.getMaterial() == Material.LEAVES ? true : (block == this && ((Integer) iblockdata.get(BlockSnow.LAYERS)).intValue() >= 7 ? true : block.c() && block.material.isSolid())) : false;
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        this.e(world, blockposition, iblockdata);
    }

    private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.canPlace(world, blockposition)) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            return false;
        } else {
            return true;
        }
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
        a(world, blockposition, new ItemStack(Items.SNOWBALL, ((Integer) iblockdata.get(BlockSnow.LAYERS)).intValue() + 1, 0));
        world.setAir(blockposition);
        entityhuman.b(StatisticList.MINE_BLOCK_COUNT[Block.getId(this)]);
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.SNOWBALL;
    }

    public int a(Random random) {
        return 0;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (world.b(EnumSkyBlock.BLOCK, blockposition) > 11) {
            // CraftBukkit start
            if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), Blocks.AIR).isCancelled()) {
                return;
            }
            // CraftBukkit end
            this.b(world, blockposition, world.getType(blockposition), 0);
            world.setAir(blockposition);
        }

    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockSnow.LAYERS, Integer.valueOf((i & 7) + 1));
    }

    public boolean a(World world, BlockPosition blockposition) {
        return ((Integer) world.getType(blockposition).get(BlockSnow.LAYERS)).intValue() == 1;
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockSnow.LAYERS)).intValue() - 1;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockSnow.LAYERS});
    }
}
