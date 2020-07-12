package net.minecraft.server;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockTripwireHook extends Block {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", (Predicate) EnumDirection.EnumDirectionLimit.HORIZONTAL);
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    public static final BlockStateBoolean ATTACHED = BlockStateBoolean.of("attached");
    public static final BlockStateBoolean SUSPENDED = BlockStateBoolean.of("suspended");

    public BlockTripwireHook() {
        super(Material.ORIENTABLE);
        this.j(this.blockStateList.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.NORTH).set(BlockTripwireHook.POWERED, Boolean.valueOf(false)).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(false)).set(BlockTripwireHook.SUSPENDED, Boolean.valueOf(false)));
        this.a(CreativeModeTab.d);
        this.a(true);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.set(BlockTripwireHook.SUSPENDED, Boolean.valueOf(!World.a(iblockaccess, blockposition.down())));
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return null;
    }

    public boolean c() {
        return false;
    }

    public boolean d() {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection.k().c() && world.getType(blockposition.shift(enumdirection.opposite())).getBlock().isOccluding();
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        EnumDirection enumdirection;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            enumdirection = (EnumDirection) iterator.next();
        } while (!world.getType(blockposition.shift(enumdirection)).getBlock().isOccluding());

        return true;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = this.getBlockData().set(BlockTripwireHook.POWERED, Boolean.valueOf(false)).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(false)).set(BlockTripwireHook.SUSPENDED, Boolean.valueOf(false));

        if (enumdirection.k().c()) {
            iblockdata = iblockdata.set(BlockTripwireHook.FACING, enumdirection);
        }

        return iblockdata;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        this.a(world, blockposition, iblockdata, false, false, -1, (IBlockData) null);
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (block != this) {
            if (this.e(world, blockposition, iblockdata)) {
                EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTripwireHook.FACING);

                if (!world.getType(blockposition.shift(enumdirection.opposite())).getBlock().isOccluding()) {
                    this.b(world, blockposition, iblockdata, 0);
                    world.setAir(blockposition);
                }
            }

        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag, boolean flag1, int i, IBlockData iblockdata1) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockTripwireHook.FACING);
        boolean flag2 = ((Boolean) iblockdata.get(BlockTripwireHook.ATTACHED)).booleanValue();
        boolean flag3 = ((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue();
        boolean flag4 = !World.a((IBlockAccess) world, blockposition.down());
        boolean flag5 = !flag;
        boolean flag6 = false;
        int j = 0;
        IBlockData[] aiblockdata = new IBlockData[42];

        BlockPosition blockposition1;

        for (int k = 1; k < 42; ++k) {
            blockposition1 = blockposition.shift(enumdirection, k);
            IBlockData iblockdata2 = world.getType(blockposition1);

            if (iblockdata2.getBlock() == Blocks.TRIPWIRE_HOOK) {
                if (iblockdata2.get(BlockTripwireHook.FACING) == enumdirection.opposite()) {
                    j = k;
                }
                break;
            }

            if (iblockdata2.getBlock() != Blocks.TRIPWIRE && k != i) {
                aiblockdata[k] = null;
                flag5 = false;
            } else {
                if (k == i) {
                    iblockdata2 = (IBlockData) Objects.firstNonNull(iblockdata1, iblockdata2);
                }

                boolean flag7 = !((Boolean) iblockdata2.get(BlockTripwire.DISARMED)).booleanValue();
                boolean flag8 = ((Boolean) iblockdata2.get(BlockTripwire.POWERED)).booleanValue();
                boolean flag9 = ((Boolean) iblockdata2.get(BlockTripwire.SUSPENDED)).booleanValue();

                flag5 &= flag9 == flag4;
                flag6 |= flag7 && flag8;
                aiblockdata[k] = iblockdata2;
                if (k == i) {
                    world.a(blockposition, (Block) this, this.a(world));
                    flag5 &= flag7;
                }
            }
        }

        flag5 &= j > 1;
        flag6 &= flag5;
        IBlockData iblockdata3 = this.getBlockData().set(BlockTripwireHook.ATTACHED, Boolean.valueOf(flag5)).set(BlockTripwireHook.POWERED, Boolean.valueOf(flag6));

        if (j > 0) {
            blockposition1 = blockposition.shift(enumdirection, j);
            EnumDirection enumdirection1 = enumdirection.opposite();

            world.setTypeAndData(blockposition1, iblockdata3.set(BlockTripwireHook.FACING, enumdirection1), 3);
            this.a(world, blockposition1, enumdirection1);
            this.a(world, blockposition1, flag5, flag6, flag2, flag3);
        }

        // CraftBukkit start
        org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());

        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, 15, 0);
        world.getServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() > 0) {
            return;
        }
        // CraftBukkit end

        this.a(world, blockposition, flag5, flag6, flag2, flag3);
        if (!flag) {
            world.setTypeAndData(blockposition, iblockdata3.set(BlockTripwireHook.FACING, enumdirection), 3);
            if (flag1) {
                this.a(world, blockposition, enumdirection);
            }
        }

        if (flag2 != flag5) {
            for (int l = 1; l < j; ++l) {
                BlockPosition blockposition2 = blockposition.shift(enumdirection, l);
                IBlockData iblockdata4 = aiblockdata[l];

                if (iblockdata4 != null && world.getType(blockposition2).getBlock() != Blocks.AIR) {
                    world.setTypeAndData(blockposition2, iblockdata4.set(BlockTripwireHook.ATTACHED, Boolean.valueOf(flag5)), 3);
                }
            }
        }

    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        this.a(world, blockposition, iblockdata, false, true, -1, (IBlockData) null);
    }

    private void a(World world, BlockPosition blockposition, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        if (flag1 && !flag3) {
            world.makeSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.1D, (double) blockposition.getZ() + 0.5D, "random.click", 0.4F, 0.6F);
        } else if (!flag1 && flag3) {
            world.makeSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.1D, (double) blockposition.getZ() + 0.5D, "random.click", 0.4F, 0.5F);
        } else if (flag && !flag2) {
            world.makeSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.1D, (double) blockposition.getZ() + 0.5D, "random.click", 0.4F, 0.7F);
        } else if (!flag && flag2) {
            world.makeSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.1D, (double) blockposition.getZ() + 0.5D, "random.bowhit", 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
        }

    }

    private void a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        world.applyPhysics(blockposition, this);
        world.applyPhysics(blockposition.shift(enumdirection.opposite()), this);
    }

    private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.canPlace(world, blockposition)) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            return false;
        } else {
            return true;
        }
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = 0.1875F;

        switch (BlockTripwireHook.SyntheticClass_1.a[((EnumDirection) iblockaccess.getType(blockposition).get(BlockTripwireHook.FACING)).ordinal()]) {
        case 1:
            this.a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
            break;

        case 2:
            this.a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
            break;

        case 3:
            this.a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
            break;

        case 4:
            this.a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = ((Boolean) iblockdata.get(BlockTripwireHook.ATTACHED)).booleanValue();
        boolean flag1 = ((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue();

        if (flag || flag1) {
            this.a(world, blockposition, iblockdata, true, false, -1, (IBlockData) null);
        }

        if (flag1) {
            world.applyPhysics(blockposition, this);
            world.applyPhysics(blockposition.shift(((EnumDirection) iblockdata.get(BlockTripwireHook.FACING)).opposite()), this);
        }

        super.remove(world, blockposition, iblockdata);
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return ((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue() ? 15 : 0;
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return !((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue() ? 0 : (iblockdata.get(BlockTripwireHook.FACING) == enumdirection ? 15 : 0);
    }

    public boolean isPowerSource() {
        return true;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockTripwireHook.FACING, EnumDirection.fromType2(i & 3)).set(BlockTripwireHook.POWERED, Boolean.valueOf((i & 8) > 0)).set(BlockTripwireHook.ATTACHED, Boolean.valueOf((i & 4) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockTripwireHook.FACING)).b();

        if (((Boolean) iblockdata.get(BlockTripwireHook.POWERED)).booleanValue()) {
            i |= 8;
        }

        if (((Boolean) iblockdata.get(BlockTripwireHook.ATTACHED)).booleanValue()) {
            i |= 4;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockTripwireHook.FACING, BlockTripwireHook.POWERED, BlockTripwireHook.ATTACHED, BlockTripwireHook.SUSPENDED});
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                BlockTripwireHook.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                BlockTripwireHook.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                BlockTripwireHook.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                BlockTripwireHook.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

        }
    }
}
