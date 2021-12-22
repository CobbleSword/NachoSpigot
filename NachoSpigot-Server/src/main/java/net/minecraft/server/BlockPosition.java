package net.minecraft.server;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

public class BlockPosition extends BaseBlockPosition {

    public static final BlockPosition ZERO = new BlockPosition(0, 0, 0);
    public static final int c = 1 + MathHelper.c(MathHelper.b(30000000));
    public static final int d = BlockPosition.c;
    public static final int e = 64 - BlockPosition.c - BlockPosition.d;
    public static final int f = BlockPosition.d;
    public static final int g = BlockPosition.f + BlockPosition.e;
    public static final long h = (1L << BlockPosition.c) - 1L;
    public static final long i = (1L << BlockPosition.e) - 1L;
    public static final long j = (1L << BlockPosition.d) - 1L;

    public BlockPosition(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPosition(double d0, double d1, double d2) {
        super(d0, d1, d2);
    }

    public BlockPosition(Entity entity) {
        this(entity.locX, entity.locY, entity.locZ);
    }

    public BlockPosition(Vec3D vec3d) {
        this(vec3d.a, vec3d.b, vec3d.c);
    }

    public BlockPosition(BaseBlockPosition baseblockposition) {
        this(baseblockposition.getX(), baseblockposition.getY(), baseblockposition.getZ());
    }

    public static BlockPosition fromLong(long i) {
        int j = (int) (i << 64 - BlockPosition.g - BlockPosition.c >> 64 - BlockPosition.c);
        int k = (int) (i << 64 - BlockPosition.f - BlockPosition.e >> 64 - BlockPosition.e);
        int l = (int) (i << 64 - BlockPosition.d >> 64 - BlockPosition.d);

        return new BlockPosition(j, k, l);
    }

    public static Iterable<BlockPosition> a(BlockPosition blockposition, BlockPosition blockposition1) {
        return new Iterable<BlockPosition>() {
            public Iterator<BlockPosition> iterator() {
                return new AbstractIterator<BlockPosition>() {
                    private BlockPosition b = null;
                    protected BlockPosition computeNext() {
                        if (this.b == null) {
                            this.b = blockposition;
                            return this.b;
                        } else if (this.b.equals(blockposition1)) {
                            return this.endOfData();
                        } else {
                            int i = this.b.getX();
                            int j = this.b.getY();
                            int k = this.b.getZ();

                            if (i < blockposition1.getX()) {
                                ++i;
                            } else if (j < blockposition1.getY()) {
                                i = blockposition.getX();
                                ++j;
                            } else if (k < blockposition1.getZ()) {
                                i = blockposition.getX();
                                j = blockposition.getY();
                                ++k;
                            }

                            this.b = new BlockPosition(i, j, k);
                            return this.b;
                        }
                    }
                };
            }
        };
    }

    public static Iterable<BlockPosition.MutableBlockPosition> b(BlockPosition blockposition, BlockPosition blockposition1) {
        return new Iterable<BlockPosition.MutableBlockPosition>() {
            public Iterator<BlockPosition.MutableBlockPosition> iterator() {
                return new AbstractIterator<BlockPosition.MutableBlockPosition>() {
                    private BlockPosition.MutableBlockPosition b = null;
                    protected BlockPosition.MutableBlockPosition computeNext() {
                        if (this.b == null) {
                            this.b = new BlockPosition.MutableBlockPosition(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                            return this.b;
                        } else if (this.b.equals(blockposition1)) {
                            return this.endOfData();
                        } else {
                            int i = this.b.getX();
                            int j = this.b.getY();
                            int k = this.b.getZ();

                            if (i < blockposition1.getX()) {
                                ++i;
                            } else if (j < blockposition1.getY()) {
                                i = blockposition.getX();
                                ++j;
                            } else if (k < blockposition1.getZ()) {
                                i = blockposition.getX();
                                j = blockposition.getY();
                                ++k;
                            }

                            // PaperSpigot start
                            this.b.setX(i);
                            this.b.setY(j);
                            this.b.setZ(k);
                            // PaperSpigot stop
                            return this.b;
                        }
                    }
                };
            }
        };
    }

    public BlockPosition a(double d0, double d1, double d2) {
        return d0 == 0.0D && d1 == 0.0D && d2 == 0.0D ? this : new BlockPosition((double) this.getX() + d0, (double) this.getY() + d1, (double) this.getZ() + d2);
    }

    public BlockPosition a(int i, int j, int k) {
        return i == 0 && j == 0 && k == 0 ? this : new BlockPosition(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public BlockPosition a(BaseBlockPosition baseblockposition) {
        return baseblockposition.getX() == 0 && baseblockposition.getY() == 0 && baseblockposition.getZ() == 0 ? this : new BlockPosition(this.getX() + baseblockposition.getX(), this.getY() + baseblockposition.getY(), this.getZ() + baseblockposition.getZ());
    }

    public BlockPosition b(BaseBlockPosition baseblockposition) {
        return baseblockposition.getX() == 0 && baseblockposition.getY() == 0 && baseblockposition.getZ() == 0 ? this : new BlockPosition(this.getX() - baseblockposition.getX(), this.getY() - baseblockposition.getY(), this.getZ() - baseblockposition.getZ());
    }

    public BlockPosition up() {
        return new BlockPosition(this.getX(), this.getY() + 1, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition up(int i) {
        return i == 0 ? this : new BlockPosition(this.getX(), this.getY() + i, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition down() {
        return new BlockPosition(this.getX(), this.getY() - 1, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition down(int i) {
        return i == 0 ? this : new BlockPosition(this.getX(), this.getY() - i, this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition north() {
        return new BlockPosition(this.getX(), this.getY(), this.getZ() - 1); // Paper - Optimize BlockPosition
    }

    public BlockPosition north(int i) {
        return i == 0 ? this : new BlockPosition(this.getX(), this.getY(), this.getZ() - i); // Paper - Optimize BlockPosition
    }

    public BlockPosition south() {
        return new BlockPosition(this.getX(), this.getY(), this.getZ() + 1); // Paper - Optimize BlockPosition
    }

    public BlockPosition south(int i) {
        return i == 0 ? this : new BlockPosition(this.getX(), this.getY(), this.getZ() + i); // Paper - Optimize BlockPosition
    }

    public BlockPosition west() {
        return new BlockPosition(this.getX() - 1, this.getY(), this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition west(int i) {
        return i == 0 ? this : new BlockPosition(this.getX() - i, this.getY(), this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition east() {
        return new BlockPosition(this.getX() + 1, this.getY(), this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition east(int i) {
        return i == 0 ? this : new BlockPosition(this.getX() + i, this.getY(), this.getZ()); // Paper - Optimize BlockPosition
    }

    public BlockPosition shift(EnumDirection enumdirection) {
        // Paper Start - Optimize BlockPosition
        switch (enumdirection) {
            case UP:
                return new BlockPosition(this.getX(), this.getY() + 1, this.getZ());
            case DOWN:
                return new BlockPosition(this.getX(), this.getY() - 1, this.getZ());
            case NORTH:
                return new BlockPosition(this.getX(), this.getY(), this.getZ() - 1);
            case SOUTH:
                return new BlockPosition(this.getX(), this.getY(), this.getZ() + 1);
            case WEST:
                return new BlockPosition(this.getX() - 1, this.getY(), this.getZ());
            case EAST:
                return new BlockPosition(this.getX() + 1, this.getY(), this.getZ());
            default:
                return new BlockPosition(this.getX() + enumdirection.getAdjacentX(), this.getY() + enumdirection.getAdjacentY(), this.getZ() + enumdirection.getAdjacentZ());
        }
        // Paper End
    }

    public BlockPosition shift(EnumDirection enumdirection, int i) {
        // Paper Start - Optimize BlockPosition
        if (i == 0) {
            return this;
        }

        switch (enumdirection) {
            case UP:
                return new BlockPosition(this.getX(), this.getY() + i, this.getZ());
            case DOWN:
                return new BlockPosition(this.getX(), this.getY() - i, this.getZ());
            case NORTH:
                return new BlockPosition(this.getX(), this.getY(), this.getZ() - i);
            case SOUTH:
                return new BlockPosition(this.getX(), this.getY(), this.getZ() + i);
            case WEST:
                return new BlockPosition(this.getX() - i, this.getY(), this.getZ());
            case EAST:
                return new BlockPosition(this.getX() + i, this.getY(), this.getZ());
            default:
                return new BlockPosition(this.getX() + enumdirection.getAdjacentX() * i, this.getY() + enumdirection.getAdjacentY() * i, this.getZ() + enumdirection.getAdjacentZ() * i);
        }
        // Paper End
    }

    public BlockPosition c(BaseBlockPosition baseblockposition) {
        return new BlockPosition(this.getY() * baseblockposition.getZ() - this.getZ() * baseblockposition.getY(), this.getZ() * baseblockposition.getX() - this.getX() * baseblockposition.getZ(), this.getX() * baseblockposition.getY() - this.getY() * baseblockposition.getX());
    }

    public long asLong() {
        return ((long) this.getX() & BlockPosition.h) << BlockPosition.g | ((long) this.getY() & BlockPosition.i) << BlockPosition.f | ((long) this.getZ() & BlockPosition.j);
    }

    public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
        return this.c(baseblockposition);
    }

    public static final class MutableBlockPosition extends BlockPosition {

        // PaperSpigot start - remove our overriding variables
        /*
        private int c;
        private int d;
        private int e;
        */

        public MutableBlockPosition() {
            this(0, 0, 0);
        }

        public MutableBlockPosition(int i, int j, int k) {
            super(0, 0, 0);
            // PaperSpigot start - modify base x,y,z
            this.setX(i);
            this.setY(j);
            this.setZ(k);
        }

        public void setX(int x) {
            this.a = x;
        }
        // PaperSpigot end

        public void setY(int y) {
            ((BaseBlockPosition) this).c = y;
        }

        public void setZ(int z) {
            ((BaseBlockPosition) this).d = z;
        }

        /*
        public int getX() {
            return this.c;
        }

        public int getY() {
            return this.d;
        }

        public int getZ() {
            return this.e;
        }
        */

        // Nacho start - deobfuscate
        public BlockPosition.MutableBlockPosition setValues(int x, int y, int z) {
            setX(x);
            setY(y);
            setZ(z);
            // Nacho end
            // PaperSpigot end
            return this;
        }

        public BaseBlockPosition d(BaseBlockPosition baseblockposition) {
            return super.c(baseblockposition);
        }
    }
}
