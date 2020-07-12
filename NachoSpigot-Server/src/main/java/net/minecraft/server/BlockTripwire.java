package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.event.entity.EntityInteractEvent; // CraftBukkit

public class BlockTripwire extends Block {

    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    public static final BlockStateBoolean SUSPENDED = BlockStateBoolean.of("suspended");
    public static final BlockStateBoolean ATTACHED = BlockStateBoolean.of("attached");
    public static final BlockStateBoolean DISARMED = BlockStateBoolean.of("disarmed");
    public static final BlockStateBoolean NORTH = BlockStateBoolean.of("north");
    public static final BlockStateBoolean EAST = BlockStateBoolean.of("east");
    public static final BlockStateBoolean SOUTH = BlockStateBoolean.of("south");
    public static final BlockStateBoolean WEST = BlockStateBoolean.of("west");

    public BlockTripwire() {
        super(Material.ORIENTABLE);
        this.j(this.blockStateList.getBlockData().set(BlockTripwire.POWERED, Boolean.valueOf(false)).set(BlockTripwire.SUSPENDED, Boolean.valueOf(false)).set(BlockTripwire.ATTACHED, Boolean.valueOf(false)).set(BlockTripwire.DISARMED, Boolean.valueOf(false)).set(BlockTripwire.NORTH, Boolean.valueOf(false)).set(BlockTripwire.EAST, Boolean.valueOf(false)).set(BlockTripwire.SOUTH, Boolean.valueOf(false)).set(BlockTripwire.WEST, Boolean.valueOf(false)));
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
        this.a(true);
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.set(BlockTripwire.NORTH, Boolean.valueOf(c(iblockaccess, blockposition, iblockdata, EnumDirection.NORTH))).set(BlockTripwire.EAST, Boolean.valueOf(c(iblockaccess, blockposition, iblockdata, EnumDirection.EAST))).set(BlockTripwire.SOUTH, Boolean.valueOf(c(iblockaccess, blockposition, iblockdata, EnumDirection.SOUTH))).set(BlockTripwire.WEST, Boolean.valueOf(c(iblockaccess, blockposition, iblockdata, EnumDirection.WEST)));
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

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.STRING;
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        boolean flag = ((Boolean) iblockdata.get(BlockTripwire.SUSPENDED)).booleanValue();
        boolean flag1 = !World.a((IBlockAccess) world, blockposition.down());

        if (flag != flag1) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

    }

    public void updateShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        boolean flag = ((Boolean) iblockdata.get(BlockTripwire.ATTACHED)).booleanValue();
        boolean flag1 = ((Boolean) iblockdata.get(BlockTripwire.SUSPENDED)).booleanValue();

        if (!flag1) {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
        } else if (!flag) {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        } else {
            this.a(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        iblockdata = iblockdata.set(BlockTripwire.SUSPENDED, Boolean.valueOf(!World.a((IBlockAccess) world, blockposition.down())));
        world.setTypeAndData(blockposition, iblockdata, 3);
        this.e(world, blockposition, iblockdata);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.e(world, blockposition, iblockdata.set(BlockTripwire.POWERED, Boolean.valueOf(true)));
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            if (entityhuman.bZ() != null && entityhuman.bZ().getItem() == Items.SHEARS) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockTripwire.DISARMED, Boolean.valueOf(true)), 4);
            }

        }
    }

    private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection[] aenumdirection = new EnumDirection[] { EnumDirection.SOUTH, EnumDirection.WEST};
        int i = aenumdirection.length;
        int j = 0;

        while (j < i) {
            EnumDirection enumdirection = aenumdirection[j];
            int k = 1;

            while (true) {
                if (k < 42) {
                    BlockPosition blockposition1 = blockposition.shift(enumdirection, k);
                    IBlockData iblockdata1 = world.getType(blockposition1);

                    if (iblockdata1.getBlock() == Blocks.TRIPWIRE_HOOK) {
                        if (iblockdata1.get(BlockTripwireHook.FACING) == enumdirection.opposite()) {
                            Blocks.TRIPWIRE_HOOK.a(world, blockposition1, iblockdata1, false, true, k, iblockdata);
                        }
                    } else if (iblockdata1.getBlock() == Blocks.TRIPWIRE) {
                        ++k;
                        continue;
                    }
                }

                ++j;
                break;
            }
        }

    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        if (!world.isClientSide) {
            if (!((Boolean) iblockdata.get(BlockTripwire.POWERED)).booleanValue()) {
                this.e(world, blockposition);
            }
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (((Boolean) world.getType(blockposition).get(BlockTripwire.POWERED)).booleanValue()) {
                this.e(world, blockposition);
            }
        }
    }

    private void e(World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition);
        boolean flag = ((Boolean) iblockdata.get(BlockTripwire.POWERED)).booleanValue();
        boolean flag1 = false;
        List list = world.getEntities((Entity) null, new AxisAlignedBB((double) blockposition.getX() + this.minX, (double) blockposition.getY() + this.minY, (double) blockposition.getZ() + this.minZ, (double) blockposition.getX() + this.maxX, (double) blockposition.getY() + this.maxY, (double) blockposition.getZ() + this.maxZ));

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (!entity.aI()) {
                    flag1 = true;
                    break;
                }
            }
        }

        // CraftBukkit start - Call interact even when triggering connected tripwire
        if (flag != flag1 && flag1 && (Boolean)iblockdata.get(ATTACHED)) {
            org.bukkit.World bworld = world.getWorld();
            org.bukkit.plugin.PluginManager manager = world.getServer().getPluginManager();
            org.bukkit.block.Block block = bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            boolean allowed = false;

            // If all of the events are cancelled block the tripwire trigger, else allow
            for (Object object : list) {
                if (object != null) {
                    org.bukkit.event.Cancellable cancellable;

                    if (object instanceof EntityHuman) {
                        cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((EntityHuman) object, org.bukkit.event.block.Action.PHYSICAL, blockposition, null, null);
                    } else if (object instanceof Entity) {
                        cancellable = new EntityInteractEvent(((Entity) object).getBukkitEntity(), block);
                        manager.callEvent((EntityInteractEvent) cancellable);
                    } else {
                        continue;
                    }

                    if (!cancellable.isCancelled()) {
                        allowed = true;
                        break;
                    }
                }
            }

            if (!allowed) {
                return;
            }
        }
        // CraftBukkit end

        if (flag1 != flag) {
            iblockdata = iblockdata.set(BlockTripwire.POWERED, Boolean.valueOf(flag1));
            world.setTypeAndData(blockposition, iblockdata, 3);
            this.e(world, blockposition, iblockdata);
        }

        if (flag1) {
            world.a(blockposition, (Block) this, this.a(world));
        }

    }

    public static boolean c(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata1 = iblockaccess.getType(blockposition1);
        Block block = iblockdata1.getBlock();

        if (block == Blocks.TRIPWIRE_HOOK) {
            EnumDirection enumdirection1 = enumdirection.opposite();

            return iblockdata1.get(BlockTripwireHook.FACING) == enumdirection1;
        } else if (block == Blocks.TRIPWIRE) {
            boolean flag = ((Boolean) iblockdata.get(BlockTripwire.SUSPENDED)).booleanValue();
            boolean flag1 = ((Boolean) iblockdata1.get(BlockTripwire.SUSPENDED)).booleanValue();

            return flag == flag1;
        } else {
            return false;
        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockTripwire.POWERED, Boolean.valueOf((i & 1) > 0)).set(BlockTripwire.SUSPENDED, Boolean.valueOf((i & 2) > 0)).set(BlockTripwire.ATTACHED, Boolean.valueOf((i & 4) > 0)).set(BlockTripwire.DISARMED, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        int i = 0;

        if (((Boolean) iblockdata.get(BlockTripwire.POWERED)).booleanValue()) {
            i |= 1;
        }

        if (((Boolean) iblockdata.get(BlockTripwire.SUSPENDED)).booleanValue()) {
            i |= 2;
        }

        if (((Boolean) iblockdata.get(BlockTripwire.ATTACHED)).booleanValue()) {
            i |= 4;
        }

        if (((Boolean) iblockdata.get(BlockTripwire.DISARMED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockTripwire.POWERED, BlockTripwire.SUSPENDED, BlockTripwire.ATTACHED, BlockTripwire.DISARMED, BlockTripwire.NORTH, BlockTripwire.EAST, BlockTripwire.WEST, BlockTripwire.SOUTH});
    }
}
