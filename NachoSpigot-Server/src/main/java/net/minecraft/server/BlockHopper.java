package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.List;

public class BlockHopper extends BlockContainer {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", new Predicate() {
        public boolean a(EnumDirection enumdirection) {
            return enumdirection != EnumDirection.UP;
        }

        public boolean apply(Object object) {
            return this.a((EnumDirection) object);
        }
    });
    public static final BlockStateBoolean ENABLED = BlockStateBoolean.of("enabled");

    public BlockHopper() {
        super(Material.ORE, MaterialMapColor.m);
        this.j(this.blockStateList.getBlockData().set(BlockHopper.FACING, EnumDirection.DOWN).set(BlockHopper.ENABLED, Boolean.valueOf(true)));
        this.a(CreativeModeTab.d);
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity entity) {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
        float f = 0.125F;

        this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
        this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
        this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.a(world, blockposition, iblockdata, axisalignedbb, list, entity);
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        EnumDirection enumdirection1 = enumdirection.opposite();

        if (enumdirection1 == EnumDirection.UP) {
            enumdirection1 = EnumDirection.DOWN;
        }

        return this.getBlockData().set(BlockHopper.FACING, enumdirection1).set(BlockHopper.ENABLED, Boolean.valueOf(true));
    }

    public TileEntity a(World world, int i) {
        return new TileEntityHopper();
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        super.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                ((TileEntityHopper) tileentity).a(itemstack.getName());
            }
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata);
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityHopper) {
                entityhuman.openContainer((TileEntityHopper) tileentity);
                entityhuman.b(StatisticList.P);
            }

            return true;
        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        this.e(world, blockposition, iblockdata);
    }

    private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = !world.isBlockIndirectlyPowered(blockposition);

        if (flag != ((Boolean) iblockdata.get(BlockHopper.ENABLED)).booleanValue()) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockHopper.ENABLED, Boolean.valueOf(flag)), 4);
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityHopper) {
            InventoryUtils.dropInventory(world, blockposition, (TileEntityHopper) tileentity);
            world.updateAdjacentComparators(blockposition, this);
        }

        super.remove(world, blockposition, iblockdata);
    }

    public int b() {
        return 3;
    }

    public boolean d() {
        return false;
    }

    public boolean c() {
        return false;
    }

    public static EnumDirection b(int i) {
        return EnumDirection.fromType1(i & 7);
    }

    public static boolean f(int i) {
        return (i & 8) != 8;
    }

    public boolean isComplexRedstone() {
        return true;
    }

    public int l(World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockHopper.FACING, b(i)).set(BlockHopper.ENABLED, Boolean.valueOf(f(i)));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockHopper.FACING)).a();

        if (!((Boolean) iblockdata.get(BlockHopper.ENABLED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockHopper.FACING, BlockHopper.ENABLED});
    }
}
