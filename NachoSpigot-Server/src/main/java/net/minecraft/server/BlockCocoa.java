package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockCocoa extends BlockDirectional implements IBlockFragilePlantElement {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 2);

    public BlockCocoa() {
        super(Material.PLANT);
        this.j(this.blockStateList.getBlockData().set(BlockCocoa.FACING, EnumDirection.NORTH).set(BlockCocoa.AGE, Integer.valueOf(0)));
        this.a(true);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!this.e(world, blockposition, iblockdata)) {
            this.f(world, blockposition, iblockdata);
        } else if (world.random.nextInt(5) == 0) {
            int i = ((Integer) iblockdata.get(BlockCocoa.AGE)).intValue();

            if (i < 2) {
                // CraftBukkit start
                IBlockData data = iblockdata.set(AGE, Integer.valueOf(i + 1));
                CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this, toLegacyData(data));
                // CraftBukkit end
            }
        }

    }

    public boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        blockposition = blockposition.shift((EnumDirection) iblockdata.get(BlockCocoa.FACING));
        IBlockData iblockdata1 = world.getType(blockposition);

        return iblockdata1.getBlock() == Blocks.LOG && iblockdata1.get(BlockWood.VARIANT) == BlockWood.EnumLogVariant.JUNGLE;
    }

    public boolean d() {
        return false;
    }

    public boolean c() {
        return false;
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.updateShape(world, blockposition);
        return super.a(world, blockposition, iblockdata);
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockCocoa.FACING);
        int i = ((Integer) iblockdata.get(BlockCocoa.AGE)).intValue();
        int j = 4 + i * 2;
        int k = 5 + i * 2;
        float f = (float) j / 2.0F;

        switch (BlockCocoa.SyntheticClass_1.a[enumdirection.ordinal()]) {
        case 1:
            this.a((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, (15.0F - (float) j) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
            break;

        case 2:
            this.a((8.0F - f) / 16.0F, (12.0F - (float) k) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float) j) / 16.0F);
            break;

        case 3:
            this.a(0.0625F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float) j) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
            break;

        case 4:
            this.a((15.0F - (float) j) / 16.0F, (12.0F - (float) k) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
        }

    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        EnumDirection enumdirection = EnumDirection.fromAngle((double) entityliving.yaw);

        world.setTypeAndData(blockposition, iblockdata.set(BlockCocoa.FACING, enumdirection), 2);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        if (!enumdirection.k().c()) {
            enumdirection = EnumDirection.NORTH;
        }

        return this.getBlockData().set(BlockCocoa.FACING, enumdirection.opposite()).set(BlockCocoa.AGE, Integer.valueOf(0));
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!this.e(world, blockposition, iblockdata)) {
            this.f(world, blockposition, iblockdata);
        }

    }

    private void f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
        this.b(world, blockposition, iblockdata, 0);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        int j = ((Integer) iblockdata.get(BlockCocoa.AGE)).intValue();
        byte b0 = 1;

        if (j >= 2) {
            b0 = 3;
        }

        for (int k = 0; k < b0; ++k) {
            a(world, blockposition, new ItemStack(Items.DYE, 1, EnumColor.BROWN.getInvColorIndex()));
        }

    }

    public int getDropData(World world, BlockPosition blockposition) {
        return EnumColor.BROWN.getInvColorIndex();
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return ((Integer) iblockdata.get(BlockCocoa.AGE)).intValue() < 2;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        // CraftBukkit start
        IBlockData data = iblockdata.set(AGE, Integer.valueOf(((Integer) iblockdata.get(AGE)).intValue() + 1));
        CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this, toLegacyData(data));
        // CraftBukkit end
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCocoa.FACING, EnumDirection.fromType2(i)).set(BlockCocoa.AGE, Integer.valueOf((i & 15) >> 2));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockCocoa.FACING)).b();

        i |= ((Integer) iblockdata.get(BlockCocoa.AGE)).intValue() << 2;
        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockCocoa.FACING, BlockCocoa.AGE});
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                BlockCocoa.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                BlockCocoa.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                BlockCocoa.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                BlockCocoa.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

        }
    }
}
