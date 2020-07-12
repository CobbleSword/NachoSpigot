package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class BlockReed extends Block {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 15);

    protected BlockReed() {
        super(Material.PLANT);
        this.j(this.blockStateList.getBlockData().set(BlockReed.AGE, Integer.valueOf(0)));
        float f = 0.375F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        this.a(true);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (world.getType(blockposition.down()).getBlock() == Blocks.REEDS || this.e(world, blockposition, iblockdata)) {
            if (world.isEmpty(blockposition.up())) {
                int i;

                for (i = 1; world.getType(blockposition.down(i)).getBlock() == this; ++i) {
                    ;
                }

                if (i < world.paperSpigotConfig.reedMaxHeight) { // PaperSpigot - Configurable max growth height for reed blocks) {
                    int j = ((Integer) iblockdata.get(BlockReed.AGE)).intValue();

                    if (j >= (byte) range(3, (world.growthOdds / world.spigotConfig.caneModifier * 15) + 0.5F, 15)) { // Spigot
                        // CraftBukkit start
                        // world.setTypeUpdate(blockposition.up(), this.getBlockData()); // CraftBukkit
                        BlockPosition upPos = blockposition.up();
                        org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(world, upPos.getX(), upPos.getY(), upPos.getZ(), this, 0);
                        world.setTypeAndData(blockposition, iblockdata.set(BlockReed.AGE, Integer.valueOf(0)), 4);
                        // CraftBukkit end
                    } else {
                        world.setTypeAndData(blockposition, iblockdata.set(BlockReed.AGE, Integer.valueOf(j + 1)), 4);
                    }
                }
            }

        }
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        Block block = world.getType(blockposition.down()).getBlock();

        if (block == this) {
            return true;
        } else if (block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.SAND) {
            return false;
        } else {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                enumdirection = (EnumDirection) iterator.next();
            } while (world.getType(blockposition.shift(enumdirection).down()).getBlock().getMaterial() != Material.WATER);

            return true;
        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        this.e(world, blockposition, iblockdata);
    }

    protected final boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.e(world, blockposition)) {
            return true;
        } else {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            return false;
        }
    }

    public boolean e(World world, BlockPosition blockposition) {
        return this.canPlace(world, blockposition);
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return null;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REEDS;
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockReed.AGE, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockReed.AGE)).intValue();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockReed.AGE});
    }
}
