package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockVine extends Block {

    public static final BlockStateBoolean UP = BlockStateBoolean.of("up");
    public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
    public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
    public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
    public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");
    public static final BlockStateBoolean[] Q = new BlockStateBoolean[] { BlockVine.UP, BlockVine.NORTH, BlockVine.SOUTH, BlockVine.WEST, BlockVine.EAST};

    public BlockVine() {
        super(Material.REPLACEABLE_PLANT);
        this.j(this.blockStateList.getBlockData().set(BlockVine.UP, Boolean.valueOf(false)).set(BlockVine.NORTH, Boolean.valueOf(false)).set(BlockVine.EAST, Boolean.valueOf(false)).set(BlockVine.SOUTH, Boolean.valueOf(false)).set(BlockVine.WEST, Boolean.valueOf(false)));
        this.a(true);
        this.a(CreativeModeTab.c);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.set(BlockVine.UP, Boolean.valueOf(iblockaccess.getType(blockposition.up()).getBlock().u()));
    }

    public void j() {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean a(World world, BlockPosition blockposition) {
        return true;
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = 0.0625F;
        float f1 = 1.0F;
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag = false;

        if (((Boolean) iblockaccess.getType(blockposition).get(BlockVine.WEST)).booleanValue()) {
            f4 = Math.max(f4, 0.0625F);
            f1 = 0.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if (((Boolean) iblockaccess.getType(blockposition).get(BlockVine.EAST)).booleanValue()) {
            f1 = Math.min(f1, 0.9375F);
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if (((Boolean) iblockaccess.getType(blockposition).get(BlockVine.NORTH)).booleanValue()) {
            f6 = Math.max(f6, 0.0625F);
            f3 = 0.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (((Boolean) iblockaccess.getType(blockposition).get(BlockVine.SOUTH)).booleanValue()) {
            f3 = Math.min(f3, 0.9375F);
            f6 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (!flag && this.c(iblockaccess.getType(blockposition.up()).getBlock())) {
            f2 = Math.min(f2, 0.9375F);
            f5 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
        }

        this.a(f1, f2, f3, f4, f5, f6);
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return null;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        switch (BlockVine.SyntheticClass_1.a[enumdirection.ordinal()]) {
        case 1:
            return this.c(world.getType(blockposition.up()).getBlock());

        case 2:
        case 3:
        case 4:
        case 5:
            return this.c(world.getType(blockposition.shift(enumdirection.opposite())).getBlock());

        default:
            return false;
        }
    }

    private boolean c(Block block) {
        return block.d() && block.material.isSolid();
    }

    private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        IBlockData iblockdata1 = iblockdata;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockStateBoolean blockstateboolean = getDirection(enumdirection);

            if (((Boolean) iblockdata.get(blockstateboolean)).booleanValue() && !this.c(world.getType(blockposition.shift(enumdirection)).getBlock())) {
                IBlockData iblockdata2 = world.getType(blockposition.up());

                if (iblockdata2.getBlock() != this || !((Boolean) iblockdata2.get(blockstateboolean)).booleanValue()) {
                    iblockdata = iblockdata.set(blockstateboolean, Boolean.valueOf(false));
                }
            }
        }

        if (d(iblockdata) == 0) {
            return false;
        } else {
            if (iblockdata1 != iblockdata) {
                world.setTypeAndData(blockposition, iblockdata, 2);
            }

            return true;
        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!world.isClientSide && !this.e(world, blockposition, iblockdata)) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (world.random.nextInt(4) == 0) {
                byte b0 = 4;
                int i = 5;
                boolean flag = false;

                label188:
                for (int j = -b0; j <= b0; ++j) {
                    for (int k = -b0; k <= b0; ++k) {
                        for (int l = -1; l <= 1; ++l) {
                            if (world.getType(blockposition.a(j, l, k)).getBlock() == this) {
                                --i;
                                if (i <= 0) {
                                    flag = true;
                                    break label188;
                                }
                            }
                        }
                    }
                }

                EnumDirection enumdirection = EnumDirection.a(random);
                BlockPosition blockposition1 = blockposition.up();
                EnumDirection enumdirection1;

                if (enumdirection == EnumDirection.UP && blockposition.getY() < 255 && world.isEmpty(blockposition1)) {
                    if (!flag) {
                        IBlockData iblockdata1 = iblockdata;
                        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                        while (iterator.hasNext()) {
                            enumdirection1 = (EnumDirection) iterator.next();
                            if (random.nextBoolean() || !this.c(world.getType(blockposition1.shift(enumdirection1)).getBlock())) {
                                iblockdata1 = iblockdata1.set(getDirection(enumdirection1), Boolean.valueOf(false));
                            }
                        }

                        if (((Boolean) iblockdata1.get(BlockVine.NORTH)).booleanValue() || ((Boolean) iblockdata1.get(BlockVine.EAST)).booleanValue() || ((Boolean) iblockdata1.get(BlockVine.SOUTH)).booleanValue() || ((Boolean) iblockdata1.get(BlockVine.WEST)).booleanValue()) {
                            // CraftBukkit start - Call BlockSpreadEvent
                            // world.setTypeAndData(blockposition1, iblockdata1, 2);
                            BlockPosition target = blockposition1;
                            org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                            org.bukkit.block.Block block = world.getWorld().getBlockAt(target.getX(), target.getY(), target.getZ());
                            CraftEventFactory.handleBlockSpreadEvent(block, source, this, toLegacyData(iblockdata1));
                            // CraftBukkit end
                        }

                    }
                } else {
                    BlockPosition blockposition2;

                    if (enumdirection.k().c() && !((Boolean) iblockdata.get(getDirection(enumdirection))).booleanValue()) {
                        if (!flag) {
                            blockposition2 = blockposition.shift(enumdirection);
                            Block block = world.getType(blockposition2).getBlock();

                            if (block.material == Material.AIR) {
                                enumdirection1 = enumdirection.e();
                                EnumDirection enumdirection2 = enumdirection.f();
                                boolean flag1 = ((Boolean) iblockdata.get(getDirection(enumdirection1))).booleanValue();
                                boolean flag2 = ((Boolean) iblockdata.get(getDirection(enumdirection2))).booleanValue();
                                BlockPosition blockposition3 = blockposition2.shift(enumdirection1);
                                BlockPosition blockposition4 = blockposition2.shift(enumdirection2);

                                // CraftBukkit start - Call BlockSpreadEvent
                                org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                                org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(blockposition2.getX(), blockposition2.getY(), blockposition2.getZ());

                                if (flag1 && this.c(world.getType(blockposition3).getBlock())) {
                                    // world.setTypeAndData(blockposition2, this.getBlockData().set(a(enumdirection1), Boolean.valueOf(true)), 2);
                                    CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this, toLegacyData(this.getBlockData().set(getDirection(enumdirection1), Boolean.valueOf(true))));
                                } else if (flag2 && this.c(world.getType(blockposition4).getBlock())) {
                                    // world.setTypeAndData(blockposition2, this.getBlockData().set(a(enumdirection2), Boolean.valueOf(true)), 2);
                                    CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this, toLegacyData(this.getBlockData().set(getDirection(enumdirection2), Boolean.valueOf(true))));
                                } else if (flag1 && world.isEmpty(blockposition3) && this.c(world.getType(blockposition.shift(enumdirection1)).getBlock())) {
                                    // world.setTypeAndData(blockposition3, this.getBlockData().set(a(enumdirection.opposite()), Boolean.valueOf(true)), 2);
                                    bukkitBlock = world.getWorld().getBlockAt(blockposition3.getX(), blockposition3.getY(), blockposition3.getZ());
                                    CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this, toLegacyData(this.getBlockData().set(getDirection(enumdirection.opposite()), Boolean.valueOf(true))));
                                } else if (flag2 && world.isEmpty(blockposition4) && this.c(world.getType(blockposition.shift(enumdirection2)).getBlock())) {
                                    // world.setTypeAndData(blockposition4, this.getBlockData().set(a(enumdirection.opposite()), Boolean.valueOf(true)), 2);
                                    bukkitBlock = world.getWorld().getBlockAt(blockposition4.getX(), blockposition4.getY(), blockposition4.getZ());
                                    CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this, toLegacyData(this.getBlockData().set(getDirection(enumdirection.opposite()), Boolean.valueOf(true))));
                                } else if (this.c(world.getType(blockposition2.up()).getBlock())) {
                                    // world.setTypeAndData(blockposition2, this.getBlockData(), 2);
                                    CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this, toLegacyData(this.getBlockData()));
                                }
                                // CraftBukkit end
                            } else if (block.material.k() && block.d()) {
                                world.setTypeAndData(blockposition, iblockdata.set(getDirection(enumdirection), Boolean.valueOf(true)), 2);
                            }

                        }
                    } else {
                        if (blockposition.getY() > 1) {
                            blockposition2 = blockposition.down();
                            IBlockData iblockdata2 = world.getType(blockposition2);
                            Block block1 = iblockdata2.getBlock();
                            IBlockData iblockdata3;
                            Iterator iterator1;
                            EnumDirection enumdirection3;

                            if (block1.material == Material.AIR) {
                                iblockdata3 = iblockdata;
                                iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                while (iterator1.hasNext()) {
                                    enumdirection3 = (EnumDirection) iterator1.next();
                                    if (random.nextBoolean()) {
                                        iblockdata3 = iblockdata3.set(getDirection(enumdirection3), Boolean.valueOf(false));
                                    }
                                }

                                if (((Boolean) iblockdata3.get(BlockVine.NORTH)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.EAST)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.SOUTH)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.WEST)).booleanValue()) {
                                    // CraftBukkit start - Call BlockSpreadEvent
                                    // world.setTypeAndData(blockposition2, iblockdata3, 2);
                                    org.bukkit.block.Block source = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                                    org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(blockposition2.getX(), blockposition2.getY(), blockposition2.getZ());
                                    CraftEventFactory.handleBlockSpreadEvent(bukkitBlock, source, this, toLegacyData(iblockdata3));
                                    // CraftBukkit end
                                }
                            } else if (block1 == this) {
                                iblockdata3 = iblockdata2;
                                iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                                while (iterator1.hasNext()) {
                                    enumdirection3 = (EnumDirection) iterator1.next();
                                    BlockStateBoolean blockstateboolean = getDirection(enumdirection3);

                                    if (random.nextBoolean() && ((Boolean) iblockdata.get(blockstateboolean)).booleanValue()) {
                                        iblockdata3 = iblockdata3.set(blockstateboolean, Boolean.valueOf(true));
                                    }
                                }

                                if (((Boolean) iblockdata3.get(BlockVine.NORTH)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.EAST)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.SOUTH)).booleanValue() || ((Boolean) iblockdata3.get(BlockVine.WEST)).booleanValue()) {
                                    world.setTypeAndData(blockposition2, iblockdata3, 2);
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = this.getBlockData().set(BlockVine.UP, Boolean.valueOf(false)).set(BlockVine.NORTH, Boolean.valueOf(false)).set(BlockVine.EAST, Boolean.valueOf(false)).set(BlockVine.SOUTH, Boolean.valueOf(false)).set(BlockVine.WEST, Boolean.valueOf(false));

        return enumdirection.k().c() ? iblockdata.set(getDirection(enumdirection.opposite()), Boolean.valueOf(true)) : iblockdata;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return null;
    }

    public int a(Random random) {
        return 0;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
        if (!world.isClientSide && entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
            entityhuman.b(StatisticList.MINE_BLOCK_COUNT[Block.getId(this)]);
            a(world, blockposition, new ItemStack(Blocks.VINE, 1, 0));
        } else {
            super.a(world, entityhuman, blockposition, iblockdata, tileentity);
        }

    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockVine.SOUTH, Boolean.valueOf((i & 1) > 0)).set(BlockVine.WEST, Boolean.valueOf((i & 2) > 0)).set(BlockVine.NORTH, Boolean.valueOf((i & 4) > 0)).set(BlockVine.EAST, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        int i = 0;

        if (((Boolean) iblockdata.get(BlockVine.SOUTH)).booleanValue()) {
            i |= 1;
        }

        if (((Boolean) iblockdata.get(BlockVine.WEST)).booleanValue()) {
            i |= 2;
        }

        if (((Boolean) iblockdata.get(BlockVine.NORTH)).booleanValue()) {
            i |= 4;
        }

        if (((Boolean) iblockdata.get(BlockVine.EAST)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockVine.UP, BlockVine.NORTH, BlockVine.EAST, BlockVine.SOUTH, BlockVine.WEST});
    }

    public static BlockStateBoolean getDirection(EnumDirection enumdirection) {
        switch (BlockVine.SyntheticClass_1.a[enumdirection.ordinal()]) {
        case 1:
            return BlockVine.UP;

        case 2:
            return BlockVine.NORTH;

        case 3:
            return BlockVine.SOUTH;

        case 4:
            return BlockVine.EAST;

        case 5:
            return BlockVine.WEST;

        default:
            throw new IllegalArgumentException(enumdirection + " is an invalid choice");
        }
    }

    public static int d(IBlockData iblockdata) {
        int i = 0;
        BlockStateBoolean[] ablockstateboolean = BlockVine.Q;
        int j = ablockstateboolean.length;

        for (int k = 0; k < j; ++k) {
            BlockStateBoolean blockstateboolean = ablockstateboolean[k];

            if (((Boolean) iblockdata.get(blockstateboolean)).booleanValue()) {
                ++i;
            }
        }

        return i;
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                BlockVine.SyntheticClass_1.a[EnumDirection.UP.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                BlockVine.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                BlockVine.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                BlockVine.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

            try {
                BlockVine.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror4) {
                ;
            }

        }
    }
}
