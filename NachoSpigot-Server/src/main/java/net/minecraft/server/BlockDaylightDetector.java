package net.minecraft.server;

import java.util.Random;

public class BlockDaylightDetector extends BlockContainer {

    public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);
    private final boolean b;

    public BlockDaylightDetector(boolean flag) {
        super(Material.WOOD);
        this.b = flag;
        this.j(this.blockStateList.getBlockData().set(BlockDaylightDetector.POWER, Integer.valueOf(0)));
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
        this.a(CreativeModeTab.d);
        this.c(0.2F);
        this.a(BlockDaylightDetector.f);
        this.c("daylightDetector");
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return ((Integer) iblockdata.get(BlockDaylightDetector.POWER)).intValue();
    }

    public void f(World world, BlockPosition blockposition) {
        if (!world.worldProvider.o()) {
            IBlockData iblockdata = world.getType(blockposition);
            int i = world.b(EnumSkyBlock.SKY, blockposition) - world.ab();
            float f = world.d(1.0F);
            float f1 = f < 3.1415927F ? 0.0F : 6.2831855F;

            f += (f1 - f) * 0.2F;
            i = Math.round((float) i * MathHelper.cos(f));
            i = MathHelper.clamp(i, 0, 15);
            if (this.b) {
                i = 15 - i;
            }

            if (((Integer) iblockdata.get(BlockDaylightDetector.POWER)).intValue() != i) {
                i = org.bukkit.craftbukkit.event.CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), ((Integer) iblockdata.get(POWER)), i).getNewCurrent(); // CraftBukkit - Call BlockRedstoneEvent
                world.setTypeAndData(blockposition, iblockdata.set(BlockDaylightDetector.POWER, Integer.valueOf(i)), 3);
            }

        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        if (entityhuman.cn()) {
            if (world.isClientSide) {
                return true;
            } else {
                if (this.b) {
                    world.setTypeAndData(blockposition, Blocks.DAYLIGHT_DETECTOR.getBlockData().set(BlockDaylightDetector.POWER, iblockdata.get(BlockDaylightDetector.POWER)), 4);
                    Blocks.DAYLIGHT_DETECTOR.f(world, blockposition);
                } else {
                    world.setTypeAndData(blockposition, Blocks.DAYLIGHT_DETECTOR_INVERTED.getBlockData().set(BlockDaylightDetector.POWER, iblockdata.get(BlockDaylightDetector.POWER)), 4);
                    Blocks.DAYLIGHT_DETECTOR_INVERTED.f(world, blockposition);
                }

                return true;
            }
        } else {
            return super.interact(world, blockposition, iblockdata, entityhuman, enumdirection, f, f1, f2);
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.DAYLIGHT_DETECTOR);
    }

    public boolean d() {
        return false;
    }

    public boolean c() {
        return false;
    }

    public int b() {
        return 3;
    }

    public boolean isPowerSource() {
        return true;
    }

    public TileEntity a(World world, int i) {
        return new TileEntityLightDetector();
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockDaylightDetector.POWER, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockDaylightDetector.POWER)).intValue();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockDaylightDetector.POWER});
    }
}
