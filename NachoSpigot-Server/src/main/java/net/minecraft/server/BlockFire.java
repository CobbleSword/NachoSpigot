package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
// CraftBukkit end

public class BlockFire extends Block {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 15);
    public static final BlockStateBoolean FLIP = BlockStateBoolean.of("flip");
    public static final BlockStateBoolean ALT = BlockStateBoolean.of("alt");
    public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
    public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
    public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
    public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");
    public static final BlockStateInteger UPPER = BlockStateInteger.of("upper", 0, 2);
    private final Map<Block, Integer> flameChances = Maps.newIdentityHashMap();
    private final Map<Block, Integer> U = Maps.newIdentityHashMap();

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();

        if (!World.a(iblockaccess, blockposition.down()) && !Blocks.FIRE.e(iblockaccess, blockposition.down())) {
            boolean flag = (i + j + k & 1) == 1;
            boolean flag1 = (i / 2 + j / 2 + k / 2 & 1) == 1;
            int l = 0;

            if (this.e(iblockaccess, blockposition.up())) {
                l = flag ? 1 : 2;
            }

            return iblockdata.set(BlockFire.NORTH, Boolean.valueOf(this.e(iblockaccess, blockposition.north()))).set(BlockFire.EAST, Boolean.valueOf(this.e(iblockaccess, blockposition.east()))).set(BlockFire.SOUTH, Boolean.valueOf(this.e(iblockaccess, blockposition.south()))).set(BlockFire.WEST, Boolean.valueOf(this.e(iblockaccess, blockposition.west()))).set(BlockFire.UPPER, Integer.valueOf(l)).set(BlockFire.FLIP, Boolean.valueOf(flag1)).set(BlockFire.ALT, Boolean.valueOf(flag));
        } else {
            return this.getBlockData();
        }
    }

    protected BlockFire() {
        super(Material.FIRE);
        this.j(this.blockStateList.getBlockData().set(BlockFire.AGE, Integer.valueOf(0)).set(BlockFire.FLIP, Boolean.valueOf(false)).set(BlockFire.ALT, Boolean.valueOf(false)).set(BlockFire.NORTH, Boolean.valueOf(false)).set(BlockFire.EAST, Boolean.valueOf(false)).set(BlockFire.SOUTH, Boolean.valueOf(false)).set(BlockFire.WEST, Boolean.valueOf(false)).set(BlockFire.UPPER, Integer.valueOf(0)));
        this.a(true);
    }

    public static void l() {
        Blocks.FIRE.a(Blocks.PLANKS, 5, 20);
        Blocks.FIRE.a(Blocks.DOUBLE_WOODEN_SLAB, 5, 20);
        Blocks.FIRE.a(Blocks.WOODEN_SLAB, 5, 20);
        Blocks.FIRE.a(Blocks.FENCE_GATE, 5, 20);
        Blocks.FIRE.a(Blocks.SPRUCE_FENCE_GATE, 5, 20);
        Blocks.FIRE.a(Blocks.BIRCH_FENCE_GATE, 5, 20);
        Blocks.FIRE.a(Blocks.JUNGLE_FENCE_GATE, 5, 20);
        Blocks.FIRE.a(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
        Blocks.FIRE.a(Blocks.ACACIA_FENCE_GATE, 5, 20);
        Blocks.FIRE.a(Blocks.FENCE, 5, 20);
        Blocks.FIRE.a(Blocks.SPRUCE_FENCE, 5, 20);
        Blocks.FIRE.a(Blocks.BIRCH_FENCE, 5, 20);
        Blocks.FIRE.a(Blocks.JUNGLE_FENCE, 5, 20);
        Blocks.FIRE.a(Blocks.DARK_OAK_FENCE, 5, 20);
        Blocks.FIRE.a(Blocks.ACACIA_FENCE, 5, 20);
        Blocks.FIRE.a(Blocks.OAK_STAIRS, 5, 20);
        Blocks.FIRE.a(Blocks.BIRCH_STAIRS, 5, 20);
        Blocks.FIRE.a(Blocks.SPRUCE_STAIRS, 5, 20);
        Blocks.FIRE.a(Blocks.JUNGLE_STAIRS, 5, 20);
        Blocks.FIRE.a(Blocks.LOG, 5, 5);
        Blocks.FIRE.a(Blocks.LOG2, 5, 5);
        Blocks.FIRE.a(Blocks.LEAVES, 30, 60);
        Blocks.FIRE.a(Blocks.LEAVES2, 30, 60);
        Blocks.FIRE.a(Blocks.BOOKSHELF, 30, 20);
        Blocks.FIRE.a(Blocks.TNT, 15, 100);
        Blocks.FIRE.a(Blocks.TALLGRASS, 60, 100);
        Blocks.FIRE.a(Blocks.DOUBLE_PLANT, 60, 100);
        Blocks.FIRE.a(Blocks.YELLOW_FLOWER, 60, 100);
        Blocks.FIRE.a(Blocks.RED_FLOWER, 60, 100);
        Blocks.FIRE.a(Blocks.DEADBUSH, 60, 100);
        Blocks.FIRE.a(Blocks.WOOL, 30, 60);
        Blocks.FIRE.a(Blocks.VINE, 15, 100);
        Blocks.FIRE.a(Blocks.COAL_BLOCK, 5, 5);
        Blocks.FIRE.a(Blocks.HAY_BLOCK, 60, 20);
        Blocks.FIRE.a(Blocks.CARPET, 60, 20);
    }

    public void a(Block block, int i, int j) {
        this.flameChances.put(block, Integer.valueOf(i));
        this.U.put(block, Integer.valueOf(j));
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

    public int a(Random random) {
        return 0;
    }

    public int a(World world) {
        return 30;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (world.getGameRules().getBoolean("doFireTick")) {
            if (!this.canPlace(world, blockposition)) {
                fireExtinguished(world, blockposition); // CraftBukkit - invalid place location
            }

            Block block = world.getType(blockposition.down()).getBlock();
            boolean flag = block == Blocks.NETHERRACK;

            if (world.worldProvider instanceof WorldProviderTheEnd && block == Blocks.BEDROCK) {
                flag = true;
            }

            if (!flag && world.S() && this.e(world, blockposition)) {
                fireExtinguished(world, blockposition); // CraftBukkit - extinguished by rain
            } else {
                int i = ((Integer) iblockdata.get(BlockFire.AGE)).intValue();

                if (i < 15) {
                    iblockdata = iblockdata.set(BlockFire.AGE, Integer.valueOf(i + random.nextInt(3) / 2));
                    world.setTypeAndData(blockposition, iblockdata, 4);
                }

                world.a(blockposition, (Block) this, this.a(world) + random.nextInt(10));
                if (!flag) {
                    if (!this.f(world, blockposition)) {
                        if (!World.a((IBlockAccess) world, blockposition.down()) || i > 3) {
                            fireExtinguished(world, blockposition); // CraftBukkit
                        }

                        return;
                    }

                    if (!this.e((IBlockAccess) world, blockposition.down()) && i == 15 && random.nextInt(4) == 0) {
                        fireExtinguished(world, blockposition); // CraftBukkit
                        return;
                    }
                }

                boolean flag1 = world.D(blockposition);
                byte b0 = 0;

                if (flag1) {
                    b0 = -50;
                }

                this.a(world, blockposition.east(), 300 + b0, random, i);
                this.a(world, blockposition.west(), 300 + b0, random, i);
                this.a(world, blockposition.down(), 250 + b0, random, i);
                this.a(world, blockposition.up(), 250 + b0, random, i);
                this.a(world, blockposition.north(), 300 + b0, random, i);
                this.a(world, blockposition.south(), 300 + b0, random, i);

                for (int j = -1; j <= 1; ++j) {
                    for (int k = -1; k <= 1; ++k) {
                        for (int l = -1; l <= 4; ++l) {
                            if (j != 0 || l != 0 || k != 0) {
                                int i1 = 100;

                                if (l > 1) {
                                    i1 += (l - 1) * 100;
                                }

                                BlockPosition blockposition1 = blockposition.a(j, l, k);
                                int j1 = this.m(world, blockposition1);

                                if (j1 > 0) {
                                    int k1 = (j1 + 40 + world.getDifficulty().a() * 7) / (i + 30);

                                    if (flag1) {
                                        k1 /= 2;
                                    }

                                    if (k1 > 0 && random.nextInt(i1) <= k1 && (!world.S() || !this.e(world, blockposition1))) {
                                        int l1 = i + random.nextInt(5) / 4;

                                        if (l1 > 15) {
                                            l1 = 15;
                                        }

                                        // CraftBukkit start - Call to stop spread of fire
                                        if (world.getType(blockposition1) != Blocks.FIRE) {
                                            if (CraftEventFactory.callBlockIgniteEvent(world, blockposition1.getX(), blockposition1.getY(), blockposition1.getZ(), blockposition.getX(), blockposition.getY(), blockposition.getZ()).isCancelled()) {
                                                continue;
                                            }

                                            org.bukkit.Server server = world.getServer();
                                            org.bukkit.World bworld = world.getWorld();
                                            org.bukkit.block.BlockState blockState = bworld.getBlockAt(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ()).getState();
                                            blockState.setTypeId(Block.getId(this));
                                            blockState.setData(new org.bukkit.material.MaterialData(Block.getId(this), (byte) l1));

                                            BlockSpreadEvent spreadEvent = new BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), blockState);
                                            server.getPluginManager().callEvent(spreadEvent);

                                            if (!spreadEvent.isCancelled()) {
                                                blockState.update(true);
                                            }
                                        }
                                        // CraftBukkit end
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    protected boolean e(World world, BlockPosition blockposition) {
        return world.isRainingAt(blockposition) || world.isRainingAt(blockposition.west()) || world.isRainingAt(blockposition.east()) || world.isRainingAt(blockposition.north()) || world.isRainingAt(blockposition.south());
    }

    public boolean N() {
        return false;
    }

    private int c(Block block) {
        Integer integer = (Integer) this.U.get(block);

        return integer == null ? 0 : integer.intValue();
    }

    private int d(Block block) {
        Integer integer = (Integer) this.flameChances.get(block);

        return integer == null ? 0 : integer.intValue();
    }

    private void a(World world, BlockPosition blockposition, int i, Random random, int j) {
        int k = this.c(world.getType(blockposition).getBlock());

        if (random.nextInt(i) < k) {
            IBlockData iblockdata = world.getType(blockposition);

            // CraftBukkit start
            org.bukkit.block.Block theBlock = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());

            BlockBurnEvent event = new BlockBurnEvent(theBlock);
            world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }
            // CraftBukkit end

            if (random.nextInt(j + 10) < 5 && !world.isRainingAt(blockposition)) {
                int l = j + random.nextInt(5) / 4;

                if (l > 15) {
                    l = 15;
                }

                world.setTypeAndData(blockposition, this.getBlockData().set(BlockFire.AGE, Integer.valueOf(l)), 3);
            } else {
                fireExtinguished(world, blockposition); // CraftBukkit
            }

            if (iblockdata.getBlock() == Blocks.TNT) {
                Blocks.TNT.postBreak(world, blockposition, iblockdata.set(BlockTNT.EXPLODE, Boolean.valueOf(true)));
            }
        }

    }

    private boolean f(World world, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.e((IBlockAccess) world, blockposition.shift(enumdirection))) {
                return true;
            }
        }

        return false;
    }

    private int m(World world, BlockPosition blockposition) {
        if (!world.isEmpty(blockposition)) {
            return 0;
        } else {
            int i = 0;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                i = Math.max(this.d(world.getType(blockposition.shift(enumdirection)).getBlock()), i);
            }

            return i;
        }
    }

    public boolean A() {
        return false;
    }

    public boolean e(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.d(iblockaccess.getType(blockposition).getBlock()) > 0;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return World.a((IBlockAccess) world, blockposition.down()) || this.f(world, blockposition);
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!World.a((IBlockAccess) world, blockposition.down()) && !this.f(world, blockposition)) {
            fireExtinguished(world, blockposition); // CraftBukkit - fuel block gone
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (world.worldProvider.getDimension() > 0 || !Blocks.PORTAL.e(world, blockposition)) {
            if (!World.a((IBlockAccess) world, blockposition.down()) && !this.f(world, blockposition)) {
                fireExtinguished(world, blockposition); // CraftBukkit - fuel block broke
            } else {
                world.a(blockposition, (Block) this, this.a(world) + world.random.nextInt(10));
            }
        }
    }

    public MaterialMapColor g(IBlockData iblockdata) {
        return MaterialMapColor.f;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockFire.AGE, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockFire.AGE)).intValue();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockFire.AGE, BlockFire.NORTH, BlockFire.EAST, BlockFire.SOUTH, BlockFire.WEST, BlockFire.UPPER, BlockFire.FLIP, BlockFire.ALT});
    }

    // CraftBukkit start
    private void fireExtinguished(World world, BlockPosition position) {
        if (!CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), Blocks.AIR).isCancelled()) {
            world.setAir(position);
        }
    }
    // CraftBukkit end
}
