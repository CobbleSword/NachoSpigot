package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import com.google.common.collect.ImmutableList;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
// CraftBukkit end

public class BlockPiston extends Block {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing");
    public static final BlockStateBoolean EXTENDED = BlockStateBoolean.of("extended");
    private final boolean sticky;

    public BlockPiston(boolean flag) {
        super(Material.PISTON);
        this.j(this.blockStateList.getBlockData().set(BlockPiston.FACING, EnumDirection.NORTH).set(BlockPiston.EXTENDED, Boolean.valueOf(false)));
        this.sticky = flag;
        this.a(BlockPiston.i);
        this.c(0.5F);
        this.a(CreativeModeTab.d);
    }

    public boolean c() {
        return false;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        world.setTypeAndData(blockposition, iblockdata.set(BlockPiston.FACING, a(world, blockposition, entityliving)), 2);
        if (!world.isClientSide) {
            this.e(world, blockposition, iblockdata);
        }

    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!world.isClientSide) {
            this.e(world, blockposition, iblockdata);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide && world.getTileEntity(blockposition) == null) {
            this.e(world, blockposition, iblockdata);
        }

    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockPiston.FACING, a(world, blockposition, entityliving)).set(BlockPiston.EXTENDED, Boolean.valueOf(false));
    }

    private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockPiston.FACING);
        boolean flag = this.a(world, blockposition, enumdirection);

        if (flag && !((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
            if ((new PistonExtendsChecker(world, blockposition, enumdirection, true)).a()) {
                world.playBlockAction(blockposition, this, 0, enumdirection.a());
            }
        } else if (!flag && ((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
            // CraftBukkit start
            if (!this.sticky) {
                org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                BlockPistonRetractEvent event = new BlockPistonRetractEvent(block, ImmutableList.<org.bukkit.block.Block>of(), CraftBlock.notchToBlockFace(enumdirection));
                world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
            }
            // CraftBukkit end
            world.setTypeAndData(blockposition, iblockdata.set(BlockPiston.EXTENDED, Boolean.valueOf(false)), 2);
            world.playBlockAction(blockposition, this, 1, enumdirection.a());
        }

    }

    private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        int j;

        for (j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];

            if (enumdirection1 != enumdirection && world.isBlockFacePowered(blockposition.shift(enumdirection1), enumdirection1)) {
                return true;
            }
        }

        if (world.isBlockFacePowered(blockposition, EnumDirection.DOWN)) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.up();
            EnumDirection[] aenumdirection1 = EnumDirection.values();

            j = aenumdirection1.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection2 = aenumdirection1[k];

                if (enumdirection2 != EnumDirection.DOWN && world.isBlockFacePowered(blockposition1.shift(enumdirection2), enumdirection2)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockPiston.FACING);

        if (!world.isClientSide) {
            boolean flag = this.a(world, blockposition, enumdirection);

            if (flag && i == 1) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockPiston.EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!flag && i == 0) {
                return false;
            }
        }

        if (i == 0) {
            if (!this.a(world, blockposition, enumdirection, true)) {
                return false;
            }

            world.setTypeAndData(blockposition, iblockdata.set(BlockPiston.EXTENDED, Boolean.valueOf(true)), 2);
            world.makeSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, "tile.piston.out", 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
        } else if (i == 1) {
            TileEntity tileentity = world.getTileEntity(blockposition.shift(enumdirection));

            if (tileentity instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity).h();
            }

            world.setTypeAndData(blockposition, Blocks.PISTON_EXTENSION.getBlockData().set(BlockPistonMoving.FACING, enumdirection).set(BlockPistonMoving.TYPE, this.sticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            world.setTileEntity(blockposition, BlockPistonMoving.a(this.fromLegacyData(j), enumdirection, false, true));
            if (this.sticky) {
                BlockPosition blockposition1 = blockposition.a(enumdirection.getAdjacentX() * 2, enumdirection.getAdjacentY() * 2, enumdirection.getAdjacentZ() * 2);
                Block block = world.getType(blockposition1).getBlock();
                boolean flag1 = false;

                if (block == Blocks.PISTON_EXTENSION) {
                    TileEntity tileentity1 = world.getTileEntity(blockposition1);

                    if (tileentity1 instanceof TileEntityPiston) {
                        TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity1;

                        if (tileentitypiston.e() == enumdirection && tileentitypiston.d()) {
                            tileentitypiston.h();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && a(block, world, blockposition1, enumdirection.opposite(), false) && (block.k() == 0 || block == Blocks.PISTON || block == Blocks.STICKY_PISTON)) { // CraftBukkit - remove 'block.getMaterial() != Material.AIR' condition
                    this.a(world, blockposition, enumdirection, false);
                }
            } else {
                world.setAir(blockposition.shift(enumdirection));
            }

            world.makeSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, "tile.piston.in", 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        if (iblockdata.getBlock() == this && ((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
            float f = 0.25F;
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockPiston.FACING);

            if (enumdirection != null) {
                switch (BlockPiston.SyntheticClass_1.a[enumdirection.ordinal()]) {
                case 1:
                    this.a(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;

                case 2:
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                    break;

                case 3:
                    this.a(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                    break;

                case 4:
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                    break;

                case 5:
                    this.a(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;

                case 6:
                    this.a(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
                }
            }
        } else {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

    }

    public void j() {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.updateShape(world, blockposition);
        return super.a(world, blockposition, iblockdata);
    }

    public boolean d() {
        return false;
    }

    public static EnumDirection b(int i) {
        int j = i & 7;

        return j > 5 ? null : EnumDirection.fromType1(j);
    }

    public static EnumDirection a(World world, BlockPosition blockposition, EntityLiving entityliving) {
        if (MathHelper.e((float) entityliving.locX - (float) blockposition.getX()) < 2.0F && MathHelper.e((float) entityliving.locZ - (float) blockposition.getZ()) < 2.0F) {
            double d0 = entityliving.locY + (double) entityliving.getHeadHeight();

            if (d0 - (double) blockposition.getY() > 2.0D) {
                return EnumDirection.UP;
            }

            if ((double) blockposition.getY() - d0 > 0.0D) {
                return EnumDirection.DOWN;
            }
        }

        return entityliving.getDirection().opposite();
    }

    public static boolean a(Block block, World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        if (block == Blocks.OBSIDIAN) {
            return false;
        } else if (!world.getWorldBorder().a(blockposition)) {
            return false;
        } else if (blockposition.getY() >= 0 && (enumdirection != EnumDirection.DOWN || blockposition.getY() != 0)) {
            if (blockposition.getY() <= world.getHeight() - 1 && (enumdirection != EnumDirection.UP || blockposition.getY() != world.getHeight() - 1)) {
                if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
                    if (block.g(world, blockposition) == -1.0F) {
                        return false;
                    }

                    if (block.k() == 2) {
                        return false;
                    }

                    if (block.k() == 1) {
                        if (!flag) {
                            return false;
                        }

                        return true;
                    }
                } else if (((Boolean) world.getType(blockposition).get(BlockPiston.EXTENDED)).booleanValue()) {
                    return false;
                }

                return !(block instanceof IContainer);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        if (!flag) {
            world.setAir(blockposition.shift(enumdirection));
        }

        PistonExtendsChecker pistonextendschecker = new PistonExtendsChecker(world, blockposition, enumdirection, flag);
        List list = pistonextendschecker.getMovedBlocks();
        List list1 = pistonextendschecker.getBrokenBlocks();

        if (!pistonextendschecker.a()) {
            return false;
        } else {
            // CraftBukkit start
            final org.bukkit.block.Block bblock = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());

            final List<BlockPosition> moved = pistonextendschecker.getMovedBlocks();
            final List<BlockPosition> broken = pistonextendschecker.getBrokenBlocks();

            List<org.bukkit.block.Block> blocks = new AbstractList<org.bukkit.block.Block>() {

                @Override
                public int size() {
                    return moved.size() + broken.size();
                }

                @Override
                public org.bukkit.block.Block get(int index) {
                    if (index >= size() || index < 0) {
                        throw new ArrayIndexOutOfBoundsException(index);
                    }
                    BlockPosition pos = (BlockPosition) (index < moved.size() ? moved.get(index) : broken.get(index - moved.size()));
                    return bblock.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
                }
            };

            int i = list.size() + list1.size();
            Block[] ablock = new Block[i];
            EnumDirection enumdirection1 = flag ? enumdirection : enumdirection.opposite();

            org.bukkit.event.block.BlockPistonEvent event;
            if (flag) {
                event = new BlockPistonExtendEvent(bblock, blocks, CraftBlock.notchToBlockFace(enumdirection1));
            } else {
                event = new BlockPistonRetractEvent(bblock, blocks, CraftBlock.notchToBlockFace(enumdirection1));
            }
            world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                for (BlockPosition b : broken) {
                    world.notify(b);
                }
                for (BlockPosition b : moved) {
                    world.notify(b);
                    world.notify(b.shift(enumdirection1));
                }
                return false;
            }
            // CraftBukkit end

            int j;
            BlockPosition blockposition1;

            for (j = list1.size() - 1; j >= 0; --j) {
                blockposition1 = (BlockPosition) list1.get(j);
                Block block = world.getType(blockposition1).getBlock();

                block.b(world, blockposition1, world.getType(blockposition1), 0);
                world.setAir(blockposition1);
                --i;
                ablock[i] = block;
            }

            IBlockData iblockdata;

            for (j = list.size() - 1; j >= 0; --j) {
                blockposition1 = (BlockPosition) list.get(j);
                iblockdata = world.getType(blockposition1);
                Block block1 = iblockdata.getBlock();

                block1.toLegacyData(iblockdata);
                world.setAir(blockposition1);
                blockposition1 = blockposition1.shift(enumdirection1);
                world.setTypeAndData(blockposition1, Blocks.PISTON_EXTENSION.getBlockData().set(BlockPiston.FACING, enumdirection), 4);
                world.setTileEntity(blockposition1, BlockPistonMoving.a(iblockdata, enumdirection, flag, false));
                --i;
                ablock[i] = block1;
            }

            BlockPosition blockposition2 = blockposition.shift(enumdirection);

            if (flag) {
                BlockPistonExtension.EnumPistonType blockpistonextension_enumpistontype = this.sticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;

                iblockdata = Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.FACING, enumdirection).set(BlockPistonExtension.TYPE, blockpistonextension_enumpistontype);
                IBlockData iblockdata1 = Blocks.PISTON_EXTENSION.getBlockData().set(BlockPistonMoving.FACING, enumdirection).set(BlockPistonMoving.TYPE, this.sticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);

                world.setTypeAndData(blockposition2, iblockdata1, 4);
                world.setTileEntity(blockposition2, BlockPistonMoving.a(iblockdata, enumdirection, true, false));
            }

            int k;

            for (k = list1.size() - 1; k >= 0; --k) {
                world.applyPhysics((BlockPosition) list1.get(k), ablock[i++]);
            }

            for (k = list.size() - 1; k >= 0; --k) {
                world.applyPhysics((BlockPosition) list.get(k), ablock[i++]);
            }

            if (flag) {
                world.applyPhysics(blockposition2, Blocks.PISTON_HEAD);
                world.applyPhysics(blockposition, this);
            }

            return true;
        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockPiston.FACING, b(i)).set(BlockPiston.EXTENDED, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockPiston.FACING)).a();

        if (((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockPiston.FACING, BlockPiston.EXTENDED});
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                BlockPiston.SyntheticClass_1.a[EnumDirection.DOWN.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                BlockPiston.SyntheticClass_1.a[EnumDirection.UP.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                BlockPiston.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                BlockPiston.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

            try {
                BlockPiston.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror4) {
                ;
            }

            try {
                BlockPiston.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 6;
            } catch (NoSuchFieldError nosuchfielderror5) {
                ;
            }

        }
    }
}
