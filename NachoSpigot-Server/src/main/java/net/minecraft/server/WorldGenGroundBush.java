package net.minecraft.server;

import java.util.Random;

public class WorldGenGroundBush extends WorldGenTrees {

    private final IBlockData a;
    private final IBlockData b;

    public WorldGenGroundBush(IBlockData iblockdata, IBlockData iblockdata1) {
        super(false);
        this.b = iblockdata;
        this.a = iblockdata1;
    }

    public boolean generate(World world, Random random, BlockPosition blockposition) {
        Block block;

        while (((block = world.getType(blockposition).getBlock()).getMaterial() == Material.AIR || block.getMaterial() == Material.LEAVES) && blockposition.getY() > 0) {
            blockposition = blockposition.down();
        }

        Block block1 = world.getType(blockposition).getBlock();

        if (block1 == Blocks.DIRT || block1 == Blocks.GRASS) {
            blockposition = blockposition.up();
            this.a(world, blockposition, this.b);

            for (int i = blockposition.getY(); i <= blockposition.getY() + 2; ++i) {
                int j = i - blockposition.getY();
                int k = 2 - j;

                for (int l = blockposition.getX() - k; l <= blockposition.getX() + k; ++l) {
                    int i1 = l - blockposition.getX();

                    for (int j1 = blockposition.getZ() - k; j1 <= blockposition.getZ() + k; ++j1) {
                        int k1 = j1 - blockposition.getZ();

                        if (Math.abs(i1) != k || Math.abs(k1) != k || random.nextInt(2) != 0) {
                            BlockPosition blockposition1 = new BlockPosition(l, i, j1);

                            if (!world.getType(blockposition1).getBlock().o()) {
                                this.a(world, blockposition1, this.a);
                            }
                        }
                    }
                }
            }
        // CraftBukkit start - Return false if gen was unsuccessful
        } else {
            return false;
        }
        // CraftBukkit end


        return true;
    }
}
