package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockDoor extends Block {

    public static final BlockStateDirection FACING = BlockStateDirection.of("facing", (Predicate) EnumDirection.EnumDirectionLimit.HORIZONTAL);
    public static final BlockStateBoolean OPEN = BlockStateBoolean.of("open");
    public static final BlockStateEnum<BlockDoor.EnumDoorHinge> HINGE = BlockStateEnum.of("hinge", BlockDoor.EnumDoorHinge.class);
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    public static final BlockStateEnum<BlockDoor.EnumDoorHalf> HALF = BlockStateEnum.of("half", BlockDoor.EnumDoorHalf.class);

    protected BlockDoor(Material material) {
        super(material);
        this.j(this.blockStateList.getBlockData().set(BlockDoor.FACING, EnumDirection.NORTH).set(BlockDoor.OPEN, Boolean.valueOf(false)).set(BlockDoor.HINGE, BlockDoor.EnumDoorHinge.LEFT).set(BlockDoor.POWERED, Boolean.valueOf(false)).set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER));
    }

    public String getName() {
        return LocaleI18n.get((this.a() + ".name").replaceAll("tile", "item"));
    }

    public boolean c() {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return g(e(iblockaccess, blockposition));
    }

    public boolean d() {
        return false;
    }

    public AxisAlignedBB a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.updateShape(world, blockposition);
        return super.a(world, blockposition, iblockdata);
    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        this.k(e(iblockaccess, blockposition));
    }

    private void k(int i) {
        float f = 0.1875F;

        this.a(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        EnumDirection enumdirection = f(i);
        boolean flag = g(i);
        boolean flag1 = j(i);

        if (flag) {
            if (enumdirection == EnumDirection.EAST) {
                if (!flag1) {
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                } else {
                    this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            } else if (enumdirection == EnumDirection.SOUTH) {
                if (!flag1) {
                    this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            } else if (enumdirection == EnumDirection.WEST) {
                if (!flag1) {
                    this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                } else {
                    this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            } else if (enumdirection == EnumDirection.NORTH) {
                if (!flag1) {
                    this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                } else {
                    this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        } else if (enumdirection == EnumDirection.EAST) {
            this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        } else if (enumdirection == EnumDirection.SOUTH) {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        } else if (enumdirection == EnumDirection.WEST) {
            this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else if (enumdirection == EnumDirection.NORTH) {
            this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        }

    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        if (this.material == Material.ORE) {
            return true;
        } else {
            BlockPosition blockposition1 = iblockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? blockposition : blockposition.down();
            IBlockData iblockdata1 = blockposition.equals(blockposition1) ? iblockdata : world.getType(blockposition1);

            if (iblockdata1.getBlock() != this) {
                return false;
            } else {
                iblockdata = iblockdata1.a(BlockDoor.OPEN);
                world.setTypeAndData(blockposition1, iblockdata, 2);
                world.b(blockposition1, blockposition);
                world.a(entityhuman, ((Boolean) iblockdata.get(BlockDoor.OPEN)).booleanValue() ? 1003 : 1006, blockposition, 0);
                return true;
            }
        }
    }

    public void setDoor(World world, BlockPosition blockposition, boolean flag) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() == this) {
            BlockPosition blockposition1 = iblockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? blockposition : blockposition.down();
            IBlockData iblockdata1 = blockposition == blockposition1 ? iblockdata : world.getType(blockposition1);

            if (iblockdata1.getBlock() == this && ((Boolean) iblockdata1.get(BlockDoor.OPEN)).booleanValue() != flag) {
                world.setTypeAndData(blockposition1, iblockdata1.set(BlockDoor.OPEN, Boolean.valueOf(flag)), 2);
                world.b(blockposition1, blockposition);
                world.a((EntityHuman) null, flag ? 1003 : 1006, blockposition, 0);
            }

        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (iblockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata1 = world.getType(blockposition1);

            if (iblockdata1.getBlock() != this) {
                world.setAir(blockposition);
            } else if (block != this) {
                this.doPhysics(world, blockposition1, iblockdata1, block);
            }
        } else {
            boolean flag = false;
            BlockPosition blockposition2 = blockposition.up();
            IBlockData iblockdata2 = world.getType(blockposition2);

            if (iblockdata2.getBlock() != this) {
                world.setAir(blockposition);
                flag = true;
            }

            if (!World.a((IBlockAccess) world, blockposition.down())) {
                world.setAir(blockposition);
                flag = true;
                if (iblockdata2.getBlock() == this) {
                    world.setAir(blockposition2);
                }
            }

            if (flag) {
                if (!world.isClientSide) {
                    this.b(world, blockposition, iblockdata, 0);
                }
            } else {

                // CraftBukkit start
                org.bukkit.World bworld = world.getWorld();
                org.bukkit.block.Block bukkitBlock = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                org.bukkit.block.Block blockTop = bworld.getBlockAt(blockposition2.getX(), blockposition2.getY(), blockposition2.getZ());

                int power = bukkitBlock.getBlockPower();
                int powerTop = blockTop.getBlockPower();
                if (powerTop > power) power = powerTop;
                int oldPower = (Boolean)iblockdata2.get(POWERED) ? 15 : 0;

                if (oldPower == 0 ^ power == 0) {
                    BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, oldPower, power);
                    world.getServer().getPluginManager().callEvent(eventRedstone);

                    boolean flag1 = eventRedstone.getNewCurrent() > 0;
                    world.setTypeAndData(blockposition2, iblockdata2.set(BlockDoor.POWERED, Boolean.valueOf(flag1)), 2);
                    if (flag1 != ((Boolean) iblockdata.get(BlockDoor.OPEN)).booleanValue()) {
                        world.setTypeAndData(blockposition, iblockdata.set(BlockDoor.OPEN, Boolean.valueOf(flag1)), 2);
                        world.b(blockposition, blockposition);
                        world.a((EntityHuman) null, flag1 ? 1003 : 1006, blockposition, 0);
                    }
                }
                // CraftBukkit end
            }
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return iblockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.l();
    }

    public MovingObjectPosition a(World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        this.updateShape(world, blockposition);
        return super.a(world, blockposition, vec3d, vec3d1);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return blockposition.getY() >= 255 ? false : World.a((IBlockAccess) world, blockposition.down()) && super.canPlace(world, blockposition) && super.canPlace(world, blockposition.up());
    }

    public int k() {
        return 1;
    }

    public static int e(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        int i = iblockdata.getBlock().toLegacyData(iblockdata);
        boolean flag = i(i);
        IBlockData iblockdata1 = iblockaccess.getType(blockposition.down());
        int j = iblockdata1.getBlock().toLegacyData(iblockdata1);
        int k = flag ? j : i;
        IBlockData iblockdata2 = iblockaccess.getType(blockposition.up());
        int l = iblockdata2.getBlock().toLegacyData(iblockdata2);
        int i1 = flag ? i : l;
        boolean flag1 = (i1 & 1) != 0;
        boolean flag2 = (i1 & 2) != 0;

        return b(k) | (flag ? 8 : 0) | (flag1 ? 16 : 0) | (flag2 ? 32 : 0);
    }

    private Item l() {
        return this == Blocks.IRON_DOOR ? Items.IRON_DOOR : (this == Blocks.SPRUCE_DOOR ? Items.SPRUCE_DOOR : (this == Blocks.BIRCH_DOOR ? Items.BIRCH_DOOR : (this == Blocks.JUNGLE_DOOR ? Items.JUNGLE_DOOR : (this == Blocks.ACACIA_DOOR ? Items.ACACIA_DOOR : (this == Blocks.DARK_OAK_DOOR ? Items.DARK_OAK_DOOR : Items.WOODEN_DOOR)))));
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        BlockPosition blockposition1 = blockposition.down();

        if (entityhuman.abilities.canInstantlyBuild && iblockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER && world.getType(blockposition1).getBlock() == this) {
            world.setAir(blockposition1);
        }

    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata1;

        if (iblockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
            iblockdata1 = iblockaccess.getType(blockposition.up());
            if (iblockdata1.getBlock() == this) {
                iblockdata = iblockdata.set(BlockDoor.HINGE, iblockdata1.get(BlockDoor.HINGE)).set(BlockDoor.POWERED, iblockdata1.get(BlockDoor.POWERED));
            }
        } else {
            iblockdata1 = iblockaccess.getType(blockposition.down());
            if (iblockdata1.getBlock() == this) {
                iblockdata = iblockdata.set(BlockDoor.FACING, iblockdata1.get(BlockDoor.FACING)).set(BlockDoor.OPEN, iblockdata1.get(BlockDoor.OPEN));
            }
        }

        return iblockdata;
    }

    public IBlockData fromLegacyData(int i) {
        return (i & 8) > 0 ? this.getBlockData().set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER).set(BlockDoor.HINGE, (i & 1) > 0 ? BlockDoor.EnumDoorHinge.RIGHT : BlockDoor.EnumDoorHinge.LEFT).set(BlockDoor.POWERED, Boolean.valueOf((i & 2) > 0)) : this.getBlockData().set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER).set(BlockDoor.FACING, EnumDirection.fromType2(i & 3).f()).set(BlockDoor.OPEN, Boolean.valueOf((i & 4) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i;

        if (iblockdata.get(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            i = b0 | 8;
            if (iblockdata.get(BlockDoor.HINGE) == BlockDoor.EnumDoorHinge.RIGHT) {
                i |= 1;
            }

            if (((Boolean) iblockdata.get(BlockDoor.POWERED)).booleanValue()) {
                i |= 2;
            }
        } else {
            i = b0 | ((EnumDirection) iblockdata.get(BlockDoor.FACING)).e().b();
            if (((Boolean) iblockdata.get(BlockDoor.OPEN)).booleanValue()) {
                i |= 4;
            }
        }

        return i;
    }

    protected static int b(int i) {
        return i & 7;
    }

    public static boolean f(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return g(e(iblockaccess, blockposition));
    }

    public static EnumDirection h(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return f(e(iblockaccess, blockposition));
    }

    public static EnumDirection f(int i) {
        return EnumDirection.fromType2(i & 3).f();
    }

    protected static boolean g(int i) {
        return (i & 4) != 0;
    }

    protected static boolean i(int i) {
        return (i & 8) != 0;
    }

    protected static boolean j(int i) {
        return (i & 16) != 0;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockDoor.HALF, BlockDoor.FACING, BlockDoor.OPEN, BlockDoor.HINGE, BlockDoor.POWERED});
    }

    public static enum EnumDoorHinge implements INamable {

        LEFT, RIGHT;

        private EnumDoorHinge() {}

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this == BlockDoor.EnumDoorHinge.LEFT ? "left" : "right";
        }
    }

    public static enum EnumDoorHalf implements INamable {

        UPPER, LOWER;

        private EnumDoorHalf() {}

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this == BlockDoor.EnumDoorHalf.UPPER ? "upper" : "lower";
        }
    }
}
