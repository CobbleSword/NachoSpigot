package net.minecraft.server;

import java.util.Random;

public abstract class WorldGenMegaTreeAbstract extends WorldGenTreeAbstract {

    protected final int a;
    protected final IBlockData b;
    protected final IBlockData c;
    protected int d;

    public WorldGenMegaTreeAbstract(boolean flag, int i, int j, IBlockData iblockdata, IBlockData iblockdata1) {
        super(flag);
        this.a = i;
        this.d = j;
        this.b = iblockdata;
        this.c = iblockdata1;
    }

    protected int a(Random random) {
        int i = random.nextInt(3) + this.a;

        if (this.d > 1) {
            i += random.nextInt(this.d);
        }

        return i;
    }

    private boolean c(World world, BlockPosition blockposition, int i) {
        boolean flag = true;

        if (blockposition.getY() >= 1 && blockposition.getY() + i + 1 <= 256) {
            for (int j = 0; j <= 1 + i; ++j) {
                byte b0 = 2;

                if (j == 0) {
                    b0 = 1;
                } else if (j >= 1 + i - 2) {
                    b0 = 2;
                }

                for (int k = -b0; k <= b0 && flag; ++k) {
                    for (int l = -b0; l <= b0 && flag; ++l) {
                        if (blockposition.getY() + j < 0 || blockposition.getY() + j >= 256 || (!this.a(world.getType(blockposition.a(k, j, l)).getBlock()) && world.getType(blockposition.a(k, j, l)).getBlock() != Blocks.SAPLING)) { // CraftBukkit - ignore our own saplings
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    private boolean a(BlockPosition blockposition, World world) {
        BlockPosition blockposition1 = blockposition.down();
        Block block = world.getType(blockposition1).getBlock();

        if ((block == Blocks.GRASS || block == Blocks.DIRT) && blockposition.getY() >= 2) {
            this.a(world, blockposition1);
            this.a(world, blockposition1.east());
            this.a(world, blockposition1.south());
            this.a(world, blockposition1.south().east());
            return true;
        } else {
            return false;
        }
    }

    protected boolean a(World world, Random random, BlockPosition blockposition, int i) {
        return this.c(world, blockposition, i) && this.a(blockposition, world);
    }

    protected void a(World world, BlockPosition blockposition, int i) {
        int j = i * i;

        for (int k = -i; k <= i + 1; ++k) {
            for (int l = -i; l <= i + 1; ++l) {
                int i1 = k - 1;
                int j1 = l - 1;

                if (k * k + l * l <= j || i1 * i1 + j1 * j1 <= j || k * k + j1 * j1 <= j || i1 * i1 + l * l <= j) {
                    BlockPosition blockposition1 = blockposition.a(k, 0, l);
                    Material material = world.getType(blockposition1).getBlock().getMaterial();

                    if (material == Material.AIR || material == Material.LEAVES) {
                        this.a(world, blockposition1, this.c);
                    }
                }
            }
        }

    }

    protected void b(World world, BlockPosition blockposition, int i) {
        int j = i * i;

        for (int k = -i; k <= i; ++k) {
            for (int l = -i; l <= i; ++l) {
                if (k * k + l * l <= j) {
                    BlockPosition blockposition1 = blockposition.a(k, 0, l);
                    Material material = world.getType(blockposition1).getBlock().getMaterial();

                    if (material == Material.AIR || material == Material.LEAVES) {
                        this.a(world, blockposition1, this.c);
                    }
                }
            }
        }

    }
}
