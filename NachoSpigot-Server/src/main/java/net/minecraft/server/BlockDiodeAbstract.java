package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public abstract class BlockDiodeAbstract extends BlockDirectional {

    protected final boolean N;

    protected BlockDiodeAbstract(boolean flag) {
        super(Material.ORIENTABLE);
        this.N = flag;
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    public boolean d() {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return World.a((IBlockAccess) world, blockposition.down()) ? super.canPlace(world, blockposition) : false;
    }

    public boolean e(World world, BlockPosition blockposition) {
        return World.a((IBlockAccess) world, blockposition.down());
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!this.b(world, blockposition, iblockdata)) {
            boolean flag = this.e(world, blockposition, iblockdata);

            if (this.N && !flag) {
                // CraftBukkit start
                if (CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 15, 0).getNewCurrent() != 0) {
                    return;
                }
                // CraftBukkit end
                world.setTypeAndData(blockposition, this.k(iblockdata), 2);
            } else if (!this.N) {
                // CraftBukkit start
                if (CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 0, 15).getNewCurrent() != 15) {
                    return;
                }
                // CraftBukkit end
                world.setTypeAndData(blockposition, this.e(iblockdata), 2);
                if (!flag) {
                    world.a(blockposition, this.e(iblockdata).getBlock(), this.m(iblockdata), -1);
                }
            }

        }
    }

    protected boolean l(IBlockData iblockdata) {
        return this.N;
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return this.a(iblockaccess, blockposition, iblockdata, enumdirection);
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return !this.l(iblockdata) ? 0 : (iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection ? this.a(iblockaccess, blockposition, iblockdata) : 0);
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (this.e(world, blockposition)) {
            this.g(world, blockposition, iblockdata);
        } else {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            // PaperSpigot start - Fix cannons
            if (world.paperSpigotConfig.fixCannons) {
                world.applyPhysics(blockposition.shift(EnumDirection.EAST), this);
                world.applyPhysics(blockposition.shift(EnumDirection.WEST), this);
                world.applyPhysics(blockposition.shift(EnumDirection.SOUTH), this);
                world.applyPhysics(blockposition.shift(EnumDirection.NORTH), this);
                world.applyPhysics(blockposition.shift(EnumDirection.DOWN), this);
                world.applyPhysics(blockposition.shift(EnumDirection.UP), this);
                return;
            }
            // PaperSpigot end
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

        }
    }

    protected void g(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.b(world, blockposition, iblockdata)) {
            boolean flag = this.e(world, blockposition, iblockdata);

            if ((this.N && !flag || !this.N && flag) && !world.a(blockposition, (Block) this)) {
                byte b0 = -1;

                if (this.i(world, blockposition, iblockdata)) {
                    b0 = -3;
                } else if (this.N) {
                    b0 = -2;
                }

                world.a(blockposition, this, this.d(iblockdata), b0);
            }

        }
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return false;
    }

    protected boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return this.f(world, blockposition, iblockdata) > 0;
    }

    protected int f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockDiodeAbstract.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        int i = world.getBlockFacePower(blockposition1, enumdirection);

        if (i >= 15) {
            return i;
        } else {
            IBlockData iblockdata1 = world.getType(blockposition1);

            return Math.max(i, iblockdata1.getBlock() == Blocks.REDSTONE_WIRE ? ((Integer) iblockdata1.get(BlockRedstoneWire.POWER)).intValue() : 0);
        }
    }

    protected int c(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockDiodeAbstract.FACING);
        EnumDirection enumdirection1 = enumdirection.e();
        EnumDirection enumdirection2 = enumdirection.f();

        return Math.max(this.c(iblockaccess, blockposition.shift(enumdirection1), enumdirection1), this.c(iblockaccess, blockposition.shift(enumdirection2), enumdirection2));
    }

    protected int c(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        Block block = iblockdata.getBlock();

        return this.c(block) ? (block == Blocks.REDSTONE_WIRE ? ((Integer) iblockdata.get(BlockRedstoneWire.POWER)).intValue() : iblockaccess.getBlockPower(blockposition, enumdirection)) : 0;
    }

    public boolean isPowerSource() {
        return true;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockDiodeAbstract.FACING, entityliving.getDirection().opposite());
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (this.e(world, blockposition, iblockdata)) {
            world.a(blockposition, (Block) this, 1);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.h(world, blockposition, iblockdata);
    }

    protected void h(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockDiodeAbstract.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

        world.d(blockposition1, this);
        world.a(blockposition1, (Block) this, enumdirection);
    }

    public void postBreak(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.N) {
            // PaperSpigot start - Fix cannons
            if (world.paperSpigotConfig.fixCannons) {
                world.applyPhysics(blockposition.shift(EnumDirection.EAST), this);
                world.applyPhysics(blockposition.shift(EnumDirection.WEST), this);
                world.applyPhysics(blockposition.shift(EnumDirection.NORTH), this);
                world.applyPhysics(blockposition.shift(EnumDirection.SOUTH), this);
                world.applyPhysics(blockposition.shift(EnumDirection.DOWN), this);
                world.applyPhysics(blockposition.shift(EnumDirection.UP), this);
                return;
            }
            // PaperSpigot end
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this);
            }
        }

        super.postBreak(world, blockposition, iblockdata);
    }

    public boolean c() {
        return false;
    }

    protected boolean c(Block block) {
        return block.isPowerSource();
    }

    protected int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return 15;
    }

    public static boolean d(Block block) {
        return Blocks.UNPOWERED_REPEATER.e(block) || Blocks.UNPOWERED_COMPARATOR.e(block);
    }

    public boolean e(Block block) {
        return block == this.e(this.getBlockData()).getBlock() || block == this.k(this.getBlockData()).getBlock();
    }

    public boolean i(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockDiodeAbstract.FACING)).opposite();
        BlockPosition blockposition1 = blockposition.shift(enumdirection);

        return d(world.getType(blockposition1).getBlock()) ? world.getType(blockposition1).get(BlockDiodeAbstract.FACING) != enumdirection : false;
    }

    protected int m(IBlockData iblockdata) {
        return this.d(iblockdata);
    }

    protected abstract int d(IBlockData iblockdata);

    protected abstract IBlockData e(IBlockData iblockdata);

    protected abstract IBlockData k(IBlockData iblockdata);

    public boolean b(Block block) {
        return this.e(block);
    }
}
