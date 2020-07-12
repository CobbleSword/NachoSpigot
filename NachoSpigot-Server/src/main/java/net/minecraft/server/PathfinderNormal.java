package net.minecraft.server;

public class PathfinderNormal extends PathfinderAbstract {

    private boolean f;
    private boolean g;
    private boolean h;
    private boolean i;
    private boolean j;

    public PathfinderNormal() {}

    public void a(IBlockAccess iblockaccess, Entity entity) {
        super.a(iblockaccess, entity);
        this.j = this.h;
    }

    public void a() {
        super.a();
        this.h = this.j;
    }

    public PathPoint a(Entity entity) {
        int i;

        if (this.i && entity.V()) {
            i = (int) entity.getBoundingBox().b;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(MathHelper.floor(entity.locX), i, MathHelper.floor(entity.locZ));

            for (Block block = this.a.getType(blockposition_mutableblockposition).getBlock(); block == Blocks.FLOWING_WATER || block == Blocks.WATER; block = this.a.getType(blockposition_mutableblockposition).getBlock()) {
                ++i;
                blockposition_mutableblockposition.c(MathHelper.floor(entity.locX), i, MathHelper.floor(entity.locZ));
            }

            this.h = false;
        } else {
            i = MathHelper.floor(entity.getBoundingBox().b + 0.5D);
        }

        return this.a(MathHelper.floor(entity.getBoundingBox().a), i, MathHelper.floor(entity.getBoundingBox().c));
    }

    public PathPoint a(Entity entity, double d0, double d1, double d2) {
        return this.a(MathHelper.floor(d0 - (double) (entity.width / 2.0F)), MathHelper.floor(d1), MathHelper.floor(d2 - (double) (entity.width / 2.0F)));
    }

    public int a(PathPoint[] apathpoint, Entity entity, PathPoint pathpoint, PathPoint pathpoint1, float f) {
        int i = 0;
        byte b0 = 0;

        if (this.a(entity, pathpoint.a, pathpoint.b + 1, pathpoint.c) == 1) {
            b0 = 1;
        }

        PathPoint pathpoint2 = this.a(entity, pathpoint.a, pathpoint.b, pathpoint.c + 1, b0);
        PathPoint pathpoint3 = this.a(entity, pathpoint.a - 1, pathpoint.b, pathpoint.c, b0);
        PathPoint pathpoint4 = this.a(entity, pathpoint.a + 1, pathpoint.b, pathpoint.c, b0);
        PathPoint pathpoint5 = this.a(entity, pathpoint.a, pathpoint.b, pathpoint.c - 1, b0);

        if (pathpoint2 != null && !pathpoint2.i && pathpoint2.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint2;
        }

        if (pathpoint3 != null && !pathpoint3.i && pathpoint3.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint3;
        }

        if (pathpoint4 != null && !pathpoint4.i && pathpoint4.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint4;
        }

        if (pathpoint5 != null && !pathpoint5.i && pathpoint5.a(pathpoint1) < f) {
            apathpoint[i++] = pathpoint5;
        }

        return i;
    }

    private PathPoint a(Entity entity, int i, int j, int k, int l) {
        PathPoint pathpoint = null;
        int i1 = this.a(entity, i, j, k);

        if (i1 == 2) {
            return this.a(i, j, k);
        } else {
            if (i1 == 1) {
                pathpoint = this.a(i, j, k);
            }

            if (pathpoint == null && l > 0 && i1 != -3 && i1 != -4 && this.a(entity, i, j + l, k) == 1) {
                pathpoint = this.a(i, j + l, k);
                j += l;
            }

            if (pathpoint != null) {
                int j1 = 0;

                int k1;

                for (k1 = 0; j > 0; pathpoint = this.a(i, j, k)) {
                    k1 = this.a(entity, i, j - 1, k);
                    if (this.h && k1 == -1) {
                        return null;
                    }

                    if (k1 != 1) {
                        break;
                    }

                    if (j1++ >= entity.aE()) {
                        return null;
                    }

                    --j;
                    if (j <= 0) {
                        return null;
                    }
                }

                if (k1 == -2) {
                    return null;
                }
            }

            return pathpoint;
        }
    }

    private int a(Entity entity, int i, int j, int k) {
        return a(this.a, entity, i, j, k, this.c, this.d, this.e, this.h, this.g, this.f);
    }

    public static int a(IBlockAccess iblockaccess, Entity entity, int i, int j, int k, int l, int i1, int j1, boolean flag, boolean flag1, boolean flag2) {
        boolean flag3 = false;
        BlockPosition blockposition = new BlockPosition(entity);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k1 = i; k1 < i + l; ++k1) {
            for (int l1 = j; l1 < j + i1; ++l1) {
                for (int i2 = k; i2 < k + j1; ++i2) {
                    blockposition_mutableblockposition.c(k1, l1, i2);
                    Block block = iblockaccess.getType(blockposition_mutableblockposition).getBlock();

                    if (block.getMaterial() != Material.AIR) {
                        if (block != Blocks.TRAPDOOR && block != Blocks.IRON_TRAPDOOR) {
                            if (block != Blocks.FLOWING_WATER && block != Blocks.WATER) {
                                if (!flag2 && block instanceof BlockDoor && block.getMaterial() == Material.WOOD) {
                                    return 0;
                                }
                            } else {
                                if (flag) {
                                    return -1;
                                }

                                flag3 = true;
                            }
                        } else {
                            flag3 = true;
                        }

                        if (block instanceof BlockMinecartTrackAbstract) { // PaperSpigot - Pathfinder optimizations
                            if (!(iblockaccess.getType(blockposition).getBlock() instanceof BlockMinecartTrackAbstract) && !(iblockaccess.getType(blockposition.down()).getBlock() instanceof BlockMinecartTrackAbstract)) { // PaperSpigot - Pathfinder optimizations
                                return -3;
                            }
                        } else if (!block.b(iblockaccess, blockposition_mutableblockposition) && (!flag1 || !(block instanceof BlockDoor) || block.getMaterial() != Material.WOOD)) {
                            if (block instanceof BlockFence || block instanceof BlockFenceGate || block instanceof BlockCobbleWall) {
                                return -3;
                            }

                            if (block == Blocks.TRAPDOOR || block == Blocks.IRON_TRAPDOOR) {
                                return -4;
                            }

                            Material material = block.getMaterial();

                            if (material != Material.LAVA) {
                                return 0;
                            }

                            if (!entity.ab()) {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return flag3 ? 2 : 1;
    }

    public void a(boolean flag) {
        this.f = flag;
    }

    public void b(boolean flag) {
        this.g = flag;
    }

    public void c(boolean flag) {
        this.h = flag;
    }

    public void d(boolean flag) {
        this.i = flag;
    }

    public boolean b() {
        return this.f;
    }

    public boolean d() {
        return this.i;
    }

    public boolean e() {
        return this.h;
    }
}
