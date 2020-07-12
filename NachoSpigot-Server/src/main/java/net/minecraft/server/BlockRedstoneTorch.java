package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockRedstoneTorch extends BlockTorch {

    private static Map<World, List<BlockRedstoneTorch.RedstoneUpdateInfo>> b = new java.util.WeakHashMap(); // Spigot
    private final boolean isOn;

    private boolean a(World world, BlockPosition blockposition, boolean flag) {
        if (!BlockRedstoneTorch.b.containsKey(world)) {
            BlockRedstoneTorch.b.put(world, Lists.<BlockRedstoneTorch.RedstoneUpdateInfo>newArrayList()); // CraftBukkit - fix decompile error
        }

        List list = (List) BlockRedstoneTorch.b.get(world);

        if (flag) {
            list.add(new BlockRedstoneTorch.RedstoneUpdateInfo(blockposition, world.getTime()));
        }

        int i = 0;

        for (int j = 0; j < list.size(); ++j) {
            BlockRedstoneTorch.RedstoneUpdateInfo blockredstonetorch_redstoneupdateinfo = (BlockRedstoneTorch.RedstoneUpdateInfo) list.get(j);

            if (blockredstonetorch_redstoneupdateinfo.a.equals(blockposition)) {
                ++i;
                if (i >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockRedstoneTorch(boolean flag) {
        this.isOn = flag;
        this.a(true);
        this.a((CreativeModeTab) null);
    }

    public int a(World world) {
        return 2;
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.isOn) {
            // PaperSpigot start - Fix cannons
            if (world.paperSpigotConfig.fixCannons) {
                world.applyPhysics(blockposition.shift(EnumDirection.DOWN), this);
                world.applyPhysics(blockposition.shift(EnumDirection.UP), this);
                world.applyPhysics(blockposition.shift(EnumDirection.WEST), this);
                world.applyPhysics(blockposition.shift(EnumDirection.EAST), this);
                world.applyPhysics(blockposition.shift(EnumDirection.SOUTH), this);
                world.applyPhysics(blockposition.shift(EnumDirection.NORTH), this);
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

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.isOn) {
            // PaperSpigot start - Fix cannons
            if (world.paperSpigotConfig.fixCannons) {
                world.applyPhysics(blockposition.shift(EnumDirection.DOWN), this);
                world.applyPhysics(blockposition.shift(EnumDirection.UP), this);
                world.applyPhysics(blockposition.shift(EnumDirection.WEST), this);
                world.applyPhysics(blockposition.shift(EnumDirection.EAST), this);
                world.applyPhysics(blockposition.shift(EnumDirection.SOUTH), this);
                world.applyPhysics(blockposition.shift(EnumDirection.NORTH), this);
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

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return this.isOn && iblockdata.get(BlockRedstoneTorch.FACING) != enumdirection ? 15 : 0;
    }

    private boolean g(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockRedstoneTorch.FACING)).opposite();

        return world.isBlockFacePowered(blockposition.shift(enumdirection), enumdirection);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        boolean flag = this.g(world, blockposition, iblockdata);
        List list = (List) BlockRedstoneTorch.b.get(world);

        while (list != null && !list.isEmpty() && world.getTime() - ((BlockRedstoneTorch.RedstoneUpdateInfo) list.get(0)).b > 60L) {
            list.remove(0);
        }

        // CraftBukkit start
        org.bukkit.plugin.PluginManager manager = world.getServer().getPluginManager();
        org.bukkit.block.Block block = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        int oldCurrent = this.isOn ? 15 : 0;

        BlockRedstoneEvent event = new BlockRedstoneEvent(block, oldCurrent, oldCurrent);
        // CraftBukkit end

        if (this.isOn) {
            if (flag) {
                // CraftBukkit start
                if (oldCurrent != 0) {
                    event.setNewCurrent(0);
                    manager.callEvent(event);
                    if (event.getNewCurrent() != 0) {
                        return;
                    }
                }
                // CraftBukkit end
                world.setTypeAndData(blockposition, Blocks.UNLIT_REDSTONE_TORCH.getBlockData().set(BlockRedstoneTorch.FACING, iblockdata.get(BlockRedstoneTorch.FACING)), 3);
                if (this.a(world, blockposition, true)) {
                    world.makeSound((double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 0.5F), (double) ((float) blockposition.getZ() + 0.5F), "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                    for (int i = 0; i < 5; ++i) {
                        double d0 = (double) blockposition.getX() + random.nextDouble() * 0.6D + 0.2D;
                        double d1 = (double) blockposition.getY() + random.nextDouble() * 0.6D + 0.2D;
                        double d2 = (double) blockposition.getZ() + random.nextDouble() * 0.6D + 0.2D;

                        world.addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
                    }

                    world.a(blockposition, world.getType(blockposition).getBlock(), 160);
                }
            }
        } else if (!flag && !this.a(world, blockposition, false)) {
            // CraftBukkit start
            if (oldCurrent != 15) {
                event.setNewCurrent(15);
                manager.callEvent(event);
                if (event.getNewCurrent() != 15) {
                    return;
                }
            }
            // CraftBukkit end
            world.setTypeAndData(blockposition, Blocks.REDSTONE_TORCH.getBlockData().set(BlockRedstoneTorch.FACING, iblockdata.get(BlockRedstoneTorch.FACING)), 3);
        }

    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!this.e(world, blockposition, iblockdata)) {
            if (this.isOn == this.g(world, blockposition, iblockdata)) {
                world.a(blockposition, (Block) this, this.a(world));
            }

        }
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? this.a(iblockaccess, blockposition, iblockdata, enumdirection) : 0;
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.REDSTONE_TORCH);
    }

    public boolean isPowerSource() {
        return true;
    }

    public boolean b(Block block) {
        return block == Blocks.UNLIT_REDSTONE_TORCH || block == Blocks.REDSTONE_TORCH;
    }

    static class RedstoneUpdateInfo {

        BlockPosition a;
        long b;

        public RedstoneUpdateInfo(BlockPosition blockposition, long i) {
            this.a = blockposition;
            this.b = i;
        }
    }
}
