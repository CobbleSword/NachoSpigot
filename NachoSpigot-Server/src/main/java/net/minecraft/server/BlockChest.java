package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;

public class BlockChest extends BlockContainer {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", (Predicate) EnumDirection.EnumDirectionLimit.HORIZONTAL);
    public final int b;

    protected BlockChest(int i) {
        super(Material.WOOD);
        this.j(this.blockStateList.getBlockData().set(BlockChest.FACING, EnumDirection.NORTH));
        this.b = i;
        this.a(CreativeModeTab.c);
        this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public int b() {
        return 2;
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (iblockaccess.getType(blockposition.north()).getBlock() == this) {
            this.a(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        } else if (iblockaccess.getType(blockposition.south()).getBlock() == this) {
            this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        } else if (iblockaccess.getType(blockposition.west()).getBlock() == this) {
            this.a(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        } else if (iblockaccess.getType(blockposition.east()).getBlock() == this) {
            this.a(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        } else {
            this.a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata);
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            IBlockData iblockdata1 = world.getType(blockposition1);

            if (iblockdata1.getBlock() == this) {
                this.e(world, blockposition1, iblockdata1);
            }
        }

    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockChest.FACING, entityliving.getDirection());
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        EnumDirection enumdirection = EnumDirection.fromType2(MathHelper.floor((double) (entityliving.yaw * 4.0F / 360.0F) + 0.5D) & 3).opposite();

        iblockdata = iblockdata.set(BlockChest.FACING, enumdirection);
        BlockPosition blockposition1 = blockposition.north();
        BlockPosition blockposition2 = blockposition.south();
        BlockPosition blockposition3 = blockposition.west();
        BlockPosition blockposition4 = blockposition.east();
        boolean flag = this == world.getType(blockposition1).getBlock();
        boolean flag1 = this == world.getType(blockposition2).getBlock();
        boolean flag2 = this == world.getType(blockposition3).getBlock();
        boolean flag3 = this == world.getType(blockposition4).getBlock();

        if (!flag && !flag1 && !flag2 && !flag3) {
            world.setTypeAndData(blockposition, iblockdata, 3);
        } else if (enumdirection.k() == EnumDirection.EnumAxis.X && (flag || flag1)) {
            if (flag) {
                world.setTypeAndData(blockposition1, iblockdata, 3);
            } else {
                world.setTypeAndData(blockposition2, iblockdata, 3);
            }

            world.setTypeAndData(blockposition, iblockdata, 3);
        } else if (enumdirection.k() == EnumDirection.EnumAxis.Z && (flag2 || flag3)) {
            if (flag2) {
                world.setTypeAndData(blockposition3, iblockdata, 3);
            } else {
                world.setTypeAndData(blockposition4, iblockdata, 3);
            }

            world.setTypeAndData(blockposition, iblockdata, 3);
        }

        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).a(itemstack.getName());
            }
        }

    }

    public IBlockData e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (world.isClientSide) {
            return iblockdata;
        } else {
            IBlockData iblockdata1 = world.getType(blockposition.north());
            IBlockData iblockdata2 = world.getType(blockposition.south());
            IBlockData iblockdata3 = world.getType(blockposition.west());
            IBlockData iblockdata4 = world.getType(blockposition.east());
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockChest.FACING);
            Block block = iblockdata1.getBlock();
            Block block1 = iblockdata2.getBlock();
            Block block2 = iblockdata3.getBlock();
            Block block3 = iblockdata4.getBlock();

            if (block != this && block1 != this) {
                boolean flag = block.o();
                boolean flag1 = block1.o();

                if (block2 == this || block3 == this) {
                    BlockPosition blockposition1 = block2 == this ? blockposition.west() : blockposition.east();
                    IBlockData iblockdata5 = world.getType(blockposition1.north());
                    IBlockData iblockdata6 = world.getType(blockposition1.south());

                    enumdirection = EnumDirection.SOUTH;
                    EnumDirection enumdirection1;

                    if (block2 == this) {
                        enumdirection1 = (EnumDirection) iblockdata3.get(BlockChest.FACING);
                    } else {
                        enumdirection1 = (EnumDirection) iblockdata4.get(BlockChest.FACING);
                    }

                    if (enumdirection1 == EnumDirection.NORTH) {
                        enumdirection = EnumDirection.NORTH;
                    }

                    Block block4 = iblockdata5.getBlock();
                    Block block5 = iblockdata6.getBlock();

                    if ((flag || block4.o()) && !flag1 && !block5.o()) {
                        enumdirection = EnumDirection.SOUTH;
                    }

                    if ((flag1 || block5.o()) && !flag && !block4.o()) {
                        enumdirection = EnumDirection.NORTH;
                    }
                }
            } else {
                BlockPosition blockposition2 = block == this ? blockposition.north() : blockposition.south();
                IBlockData iblockdata7 = world.getType(blockposition2.west());
                IBlockData iblockdata8 = world.getType(blockposition2.east());

                enumdirection = EnumDirection.EAST;
                EnumDirection enumdirection2;

                if (block == this) {
                    enumdirection2 = (EnumDirection) iblockdata1.get(BlockChest.FACING);
                } else {
                    enumdirection2 = (EnumDirection) iblockdata2.get(BlockChest.FACING);
                }

                if (enumdirection2 == EnumDirection.WEST) {
                    enumdirection = EnumDirection.WEST;
                }

                Block block6 = iblockdata7.getBlock();
                Block block7 = iblockdata8.getBlock();

                if ((block2.o() || block6.o()) && !block3.o() && !block7.o()) {
                    enumdirection = EnumDirection.EAST;
                }

                if ((block3.o() || block7.o()) && !block2.o() && !block6.o()) {
                    enumdirection = EnumDirection.WEST;
                }
            }

            iblockdata = iblockdata.set(BlockChest.FACING, enumdirection);
            world.setTypeAndData(blockposition, iblockdata, 3);
            return iblockdata;
        }
    }

    public IBlockData f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = null;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection1 = (EnumDirection) iterator.next();
            IBlockData iblockdata1 = world.getType(blockposition.shift(enumdirection1));

            if (iblockdata1.getBlock() == this) {
                return iblockdata;
            }

            if (iblockdata1.getBlock().o()) {
                if (enumdirection != null) {
                    enumdirection = null;
                    break;
                }

                enumdirection = enumdirection1;
            }
        }

        if (enumdirection != null) {
            return iblockdata.set(BlockChest.FACING, enumdirection.opposite());
        } else {
            EnumDirection enumdirection2 = (EnumDirection) iblockdata.get(BlockChest.FACING);

            if (world.getType(blockposition.shift(enumdirection2)).getBlock().o()) {
                enumdirection2 = enumdirection2.opposite();
            }

            if (world.getType(blockposition.shift(enumdirection2)).getBlock().o()) {
                enumdirection2 = enumdirection2.e();
            }

            if (world.getType(blockposition.shift(enumdirection2)).getBlock().o()) {
                enumdirection2 = enumdirection2.opposite();
            }

            return iblockdata.set(BlockChest.FACING, enumdirection2);
        }
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        int i = 0;
        BlockPosition blockposition1 = blockposition.west();
        BlockPosition blockposition2 = blockposition.east();
        BlockPosition blockposition3 = blockposition.north();
        BlockPosition blockposition4 = blockposition.south();

        if (world.getType(blockposition1).getBlock() == this) {
            if (this.m(world, blockposition1)) {
                return false;
            }

            ++i;
        }

        if (world.getType(blockposition2).getBlock() == this) {
            if (this.m(world, blockposition2)) {
                return false;
            }

            ++i;
        }

        if (world.getType(blockposition3).getBlock() == this) {
            if (this.m(world, blockposition3)) {
                return false;
            }

            ++i;
        }

        if (world.getType(blockposition4).getBlock() == this) {
            if (this.m(world, blockposition4)) {
                return false;
            }

            ++i;
        }

        return i <= 1;
    }

    private boolean m(World world, BlockPosition blockposition) {
        if (world.getType(blockposition).getBlock() != this) {
            return false;
        } else {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                enumdirection = (EnumDirection) iterator.next();
            } while (world.getType(blockposition.shift(enumdirection)).getBlock() != this);

            return true;
        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        super.doPhysics(world, blockposition, iblockdata, block);
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityChest) {
            tileentity.E();
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof IInventory) {
            InventoryUtils.dropInventory(world, blockposition, (IInventory) tileentity);
            world.updateAdjacentComparators(blockposition, this);
        }

        super.remove(world, blockposition, iblockdata);
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            ITileInventory itileinventory = this.f(world, blockposition);

            if (itileinventory != null) {
                entityhuman.openContainer(itileinventory);
                if (this.b == 0) {
                    entityhuman.b(StatisticList.aa);
                } else if (this.b == 1) {
                    entityhuman.b(StatisticList.U);
                }
            }

            return true;
        }
    }

    public ITileInventory f(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (!(tileentity instanceof TileEntityChest)) {
            return null;
        } else {
            Object object = (TileEntityChest) tileentity;

            if (this.n(world, blockposition)) {
                return null;
            } else {
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();
                    BlockPosition blockposition1 = blockposition.shift(enumdirection);
                    Block block = world.getType(blockposition1).getBlock();

                    if (block == this) {
                        if (this.n(world, blockposition1)) {
                            return null;
                        }

                        TileEntity tileentity1 = world.getTileEntity(blockposition1);

                        if (tileentity1 instanceof TileEntityChest) {
                            if (enumdirection != EnumDirection.WEST && enumdirection != EnumDirection.NORTH) {
                                object = new InventoryLargeChest("container.chestDouble", (ITileInventory) object, (TileEntityChest) tileentity1);
                            } else {
                                object = new InventoryLargeChest("container.chestDouble", (TileEntityChest) tileentity1, (ITileInventory) object);
                            }
                        }
                    }
                }

                return (ITileInventory) object;
            }
        }
    }

    public TileEntity a(World world, int i) {
        return new TileEntityChest();
    }

    public boolean isPowerSource() {
        return this.b == 1;
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        if (!this.isPowerSource()) {
            return 0;
        } else {
            int i = 0;
            TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                i = ((TileEntityChest) tileentity).l;
            }

            return MathHelper.clamp(i, 0, 15);
        }
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? this.a(iblockaccess, blockposition, iblockdata, enumdirection) : 0;
    }

    private boolean n(World world, BlockPosition blockposition) {
        return this.o(world, blockposition) || this.p(world, blockposition);
    }

    private boolean o(World world, BlockPosition blockposition) {
        return world.getType(blockposition.up()).getBlock().isOccluding();
    }

    private boolean p(World world, BlockPosition blockposition) {
        // PaperSpigot start - Option to disable chest's cat detection (Performance++)
        if (world.paperSpigotConfig.disableChestCatDetection) {
            return false;
        }
        // PaperSpigot end
        Iterator iterator = world.a(EntityOcelot.class, new AxisAlignedBB((double) blockposition.getX(), (double) (blockposition.getY() + 1), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 2), (double) (blockposition.getZ() + 1))).iterator();

        EntityOcelot entityocelot;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            Entity entity = (Entity) iterator.next();

            entityocelot = (EntityOcelot) entity;
        } while (!entityocelot.isSitting());

        return true;
    }

    public boolean isComplexRedstone() {
        return true;
    }

    public int l(World world, BlockPosition blockposition) {
        return Container.b((IInventory) this.f(world, blockposition));
    }

    public IBlockData fromLegacyData(int i) {
        EnumDirection enumdirection = EnumDirection.fromType1(i);

        if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
            enumdirection = EnumDirection.NORTH;
        }

        return this.getBlockData().set(BlockChest.FACING, enumdirection);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumDirection) iblockdata.get(BlockChest.FACING)).a();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockChest.FACING});
    }
}
