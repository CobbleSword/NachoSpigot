package net.minecraft.server;

import com.google.common.base.Predicate;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockTrapdoor extends Block {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", (Predicate) EnumDirection.EnumDirectionLimit.HORIZONTAL);
    public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
    public static final BlockStateEnum<BlockTrapdoor.EnumTrapdoorHalf> HALF = BlockStateEnum.of("half", BlockTrapdoor.EnumTrapdoorHalf.class);

    protected BlockTrapdoor(Material material) {
        super(material);
        this.j(this.blockStateList.getBlockData().set(BlockTrapdoor.FACING, EnumDirection.NORTH).set(BlockTrapdoor.OPEN, Boolean.valueOf(false)).set(BlockTrapdoor.HALF, BlockTrapdoor.EnumTrapdoorHalf.BOTTOM));
        float f = 0.5F;
        float f1 = 1.0F;

        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.a(CreativeModeTab.d);
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !((Boolean) iblockaccess.getType(blockposition).get(BlockTrapdoor.OPEN)).booleanValue();
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.updateShape(world, blockposition);
        return super.a(world, blockposition, iblockdata);
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        this.d(iblockaccess.getType(blockposition));
    }

    public void j() {
        float f = 0.1875F;

        this.a(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
    }

    public void d(IBlockData iblockdata) {
        if (iblockdata.getBlock() == this) {
            boolean flag = iblockdata.get(BlockTrapdoor.HALF) == BlockTrapdoor.EnumTrapdoorHalf.TOP;
            Boolean obool = (Boolean) iblockdata.get(BlockTrapdoor.OPEN);
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTrapdoor.FACING);
            float f = 0.1875F;

            if (flag) {
                this.a(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
            }

            if (obool.booleanValue()) {
                if (enumdirection == EnumDirection.NORTH) {
                    this.a(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
                }

                if (enumdirection == EnumDirection.SOUTH) {
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
                }

                if (enumdirection == EnumDirection.WEST) {
                    this.a(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }

                if (enumdirection == EnumDirection.EAST) {
                    this.a(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
                }
            }

        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        if (this.material == Material.ORE) {
            return true;
        } else {
            iblockdata = iblockdata.a(BlockTrapdoor.OPEN);
            world.setTypeAndData(blockposition, iblockdata, 2);
            world.a(entityhuman, ((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue() ? 1003 : 1006, blockposition, 0);
            return true;
        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!world.isClientSide) {
            BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockTrapdoor.FACING)).opposite());

            if (!c(world.getType(blockposition1).getBlock())) {
                world.setAir(blockposition);
                this.b(world, blockposition, iblockdata, 0);
            } else {
                boolean flag = world.isBlockIndirectlyPowered(blockposition);

                if (flag || block.isPowerSource()) {
                    // CraftBukkit start
                    org.bukkit.World bworld = world.getWorld();
                    org.bukkit.block.Block bblock = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());

                    int power = bblock.getBlockPower();
                    int oldPower = (Boolean) iblockdata.get(OPEN) ? 15 : 0;

                    if (oldPower == 0 ^ power == 0 || block.isPowerSource()) {
                        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bblock, oldPower, power);
                        world.getServer().getPluginManager().callEvent(eventRedstone);
                        flag = eventRedstone.getNewCurrent() > 0;
                    }
                    // CraftBukkit end
                    boolean flag1 = ((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue();

                    if (flag1 != flag) {
                        world.setTypeAndData(blockposition, iblockdata.set(BlockTrapdoor.OPEN, Boolean.valueOf(flag)), 2);
                        world.a((EntityHuman) null, flag ? 1003 : 1006, blockposition, 0);
                    }
                }

            }
        }
    }

    public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        this.updateShape(world, blockposition);
        return super.a(world, blockposition, vec3d, vec3d1);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = this.getBlockData();

        if (enumdirection.k().c()) {
            iblockdata = iblockdata.set(BlockTrapdoor.FACING, enumdirection).set(BlockTrapdoor.OPEN, Boolean.valueOf(false));
            iblockdata = iblockdata.set(BlockTrapdoor.HALF, f1 > 0.5F ? BlockTrapdoor.EnumTrapdoorHalf.TOP : BlockTrapdoor.EnumTrapdoorHalf.BOTTOM);
        }

        return iblockdata;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return !enumdirection.k().b() && c(world.getType(blockposition.shift(enumdirection.opposite())).getBlock());
    }

    protected static EnumDirection b(int i) {
        switch (i & 3) {
        case 0:
            return EnumDirection.NORTH;

        case 1:
            return EnumDirection.SOUTH;

        case 2:
            return EnumDirection.WEST;

        case 3:
        default:
            return EnumDirection.EAST;
        }
    }

    protected static int a(EnumDirection enumdirection) {
        switch (BlockTrapdoor.SyntheticClass_1.a[enumdirection.ordinal()]) {
        case 1:
            return 0;

        case 2:
            return 1;

        case 3:
            return 2;

        case 4:
        default:
            return 3;
        }
    }

    private static boolean c(Block block) {
        return block.material.k() && block.d() || block == Blocks.GLOWSTONE || block instanceof BlockStepAbstract || block instanceof BlockStairs;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockTrapdoor.FACING, b(i)).set(BlockTrapdoor.OPEN, Boolean.valueOf((i & 4) != 0)).set(BlockTrapdoor.HALF, (i & 8) == 0 ? BlockTrapdoor.EnumTrapdoorHalf.BOTTOM : BlockTrapdoor.EnumTrapdoorHalf.TOP);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | a((EnumDirection) iblockdata.get(BlockTrapdoor.FACING));

        if (((Boolean) iblockdata.get(BlockTrapdoor.OPEN)).booleanValue()) {
            i |= 4;
        }

        if (iblockdata.get(BlockTrapdoor.HALF) == BlockTrapdoor.EnumTrapdoorHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockTrapdoor.FACING, BlockTrapdoor.OPEN, BlockTrapdoor.HALF});
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                BlockTrapdoor.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                BlockTrapdoor.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                BlockTrapdoor.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                BlockTrapdoor.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

        }
    }

    public static enum EnumTrapdoorHalf implements INamable {

        TOP("top"), BOTTOM("bottom");

        private final String c;

        private EnumTrapdoorHalf(String s) {
            this.c = s;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }
}
