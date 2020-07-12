package net.minecraft.server;

import java.util.Random;

public class BlockOre extends Block {

    public BlockOre() {
        this(Material.STONE.r());
    }

    public BlockOre(MaterialMapColor materialmapcolor) {
        super(Material.STONE, materialmapcolor);
        this.a(CreativeModeTab.b);
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return this == Blocks.COAL_ORE ? Items.COAL : (this == Blocks.DIAMOND_ORE ? Items.DIAMOND : (this == Blocks.LAPIS_ORE ? Items.DYE : (this == Blocks.EMERALD_ORE ? Items.EMERALD : (this == Blocks.QUARTZ_ORE ? Items.QUARTZ : Item.getItemOf(this)))));
    }

    public int a(Random random) {
        return this == Blocks.LAPIS_ORE ? 4 + random.nextInt(5) : 1;
    }

    public int getDropCount(int i, Random random) {
        if (i > 0 && Item.getItemOf(this) != this.getDropType((IBlockData) this.P().a().iterator().next(), random, i)) {
            int j = random.nextInt(i + 2) - 1;

            if (j < 0) {
                j = 0;
            }

            return this.a(random) * (j + 1);
        } else {
            return this.a(random);
        }
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        super.dropNaturally(world, blockposition, iblockdata, f, i);
        /* CraftBukkit start - Delegated to getExpDrop
        if (this.getDropType(iblockdata, world.random, i) != Item.getItemOf(this)) {
            int j = 0;

            if (this == Blocks.COAL_ORE) {
                j = MathHelper.nextInt(world.random, 0, 2);
            } else if (this == Blocks.DIAMOND_ORE) {
                j = MathHelper.nextInt(world.random, 3, 7);
            } else if (this == Blocks.EMERALD_ORE) {
                j = MathHelper.nextInt(world.random, 3, 7);
            } else if (this == Blocks.LAPIS_ORE) {
                j = MathHelper.nextInt(world.random, 2, 5);
            } else if (this == Blocks.QUARTZ_ORE) {
                j = MathHelper.nextInt(world.random, 2, 5);
            }

            this.dropExperience(world, blockposition, j);
        }
        // */

    }

    @Override
    public int getExpDrop(World world, IBlockData iblockdata, int i) {
        if (this.getDropType(iblockdata, world.random, i) != Item.getItemOf(this)) {
            int j = 0;

            if (this == Blocks.COAL_ORE) {
                j = MathHelper.nextInt(world.random, 0, 2);
            } else if (this == Blocks.DIAMOND_ORE) {
                j = MathHelper.nextInt(world.random, 3, 7);
            } else if (this == Blocks.EMERALD_ORE) {
                j = MathHelper.nextInt(world.random, 3, 7);
            } else if (this == Blocks.LAPIS_ORE) {
                j = MathHelper.nextInt(world.random, 2, 5);
            } else if (this == Blocks.QUARTZ_ORE) {
                j = MathHelper.nextInt(world.random, 2, 5);
            }

            return j;
        }

        return 0;
        // CraftBukkit end
    }

    public int getDropData(World world, BlockPosition blockposition) {
        return 0;
    }

    public int getDropData(IBlockData iblockdata) {
        return this == Blocks.LAPIS_ORE ? EnumColor.BLUE.getInvColorIndex() : 0;
    }
}
