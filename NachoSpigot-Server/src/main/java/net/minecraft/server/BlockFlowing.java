package net.minecraft.server;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

// CraftBukkit start
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;
// CraftBukkit end

public class BlockFlowing extends BlockFluids {

    int a;

    protected BlockFlowing(Material material) {
        super(material);
    }

    private void f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.setTypeAndData(blockposition, b(this.material).getBlockData().set(BlockFlowing.LEVEL, iblockdata.get(BlockFlowing.LEVEL)), 2);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        // CraftBukkit start
        org.bukkit.World bworld = world.getWorld();
        org.bukkit.Server server = world.getServer();
        org.bukkit.block.Block source = bworld == null ? null : bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        // CraftBukkit end
        int i = ((Integer) iblockdata.get(BlockFlowing.LEVEL)).intValue();
        byte b0 = 1;

        if (this.material == Material.LAVA && !world.worldProvider.n()) {
            b0 = 2;
        }

        int j = this.getFlowSpeed(world, blockposition); // PaperSpigot
        int k;

        if (i > 0) {
            int l = -100;

            this.a = 0;

            EnumDirection enumdirection;

            for (Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator(); iterator.hasNext(); l = this.a(world, blockposition.shift(enumdirection), l)) {
                enumdirection = (EnumDirection) iterator.next();
            }

            int i1 = l + b0;

            if (i1 >= 8 || l < 0) {
                i1 = -1;
            }

            if (this.e(world, blockposition.up()) >= 0) {
                k = this.e(world, blockposition.up());
                if (k >= 8) {
                    i1 = k;
                } else {
                    i1 = k + 8;
                }
            }

            if (this.a >= 2 && this.material == Material.WATER) {
                IBlockData iblockdata1 = world.getType(blockposition.down());

                if (iblockdata1.getBlock().getMaterial().isBuildable()) {
                    i1 = 0;
                } else if (iblockdata1.getBlock().getMaterial() == this.material && ((Integer) iblockdata1.get(BlockFlowing.LEVEL)).intValue() == 0) {
                    i1 = 0;
                }
            }

            if (!world.paperSpigotConfig.fastDrainLava && this.material == Material.LAVA && i < 8 && i1 < 8 && i1 > i && random.nextInt(4) != 0) { // PaperSpigot
                j *= 4;
            }

            if (i1 == i) {
                this.f(world, blockposition, iblockdata);
            } else {
                i = i1;
                if (i1 < 0 || canFastDrain(world, blockposition)) { // PaperSpigot - Fast draining
                    world.setAir(blockposition);
                } else {
                    iblockdata = iblockdata.set(BlockFlowing.LEVEL, Integer.valueOf(i1));
                    world.setTypeAndData(blockposition, iblockdata, 2);
                    world.a(blockposition, (Block) this, j);
                    // PaperSpigot start - Optimize draining
                    world.d(blockposition.west(), this);
                    world.d(blockposition.east(), this);
                    world.d(blockposition.up(), this);
                    world.d(blockposition.north(), this);
                    world.d(blockposition.south(), this);
                    world.spigotConfig.antiXrayInstance.updateNearbyBlocks(world, blockposition); // Spigot
                    // PaperSpigot end
                }
            }
        } else {
            this.f(world, blockposition, iblockdata);
        }

        if (world.getType(blockposition).getBlock().getMaterial() != material) return; // PaperSpigot - Stop updating flowing block if material has changed
        IBlockData iblockdata2 = world.getType(blockposition.down());

        if (this.h(world, blockposition.down(), iblockdata2)) {
            // CraftBukkit start - Send "down" to the server
            BlockFromToEvent event = new BlockFromToEvent(source, BlockFace.DOWN);
            if (server != null) {
                server.getPluginManager().callEvent(event);
            }
            if (!event.isCancelled()) {
            if (this.material == Material.LAVA && world.getType(blockposition.down()).getBlock().getMaterial() == Material.WATER) {
                world.setTypeUpdate(blockposition.down(), Blocks.STONE.getBlockData());
                this.fizz(world, blockposition.down());
                return;
            }

            if (i >= 8) {
                this.flow(world, blockposition.down(), iblockdata2, i);
            } else {
                this.flow(world, blockposition.down(), iblockdata2, i + 8);
            }
            }
            // CraftBukkit end
        } else if (i >= 0 && (i == 0 || this.g(world, blockposition.down(), iblockdata2))) {
            Set set = this.f(world, blockposition);

            k = i + b0;
            if (i >= 8) {
                k = 1;
            }

            if (k >= 8) {
                return;
            }

            Iterator iterator1 = set.iterator();

            while (iterator1.hasNext()) {
                EnumDirection enumdirection1 = (EnumDirection) iterator1.next();

                // CraftBukkit start
                BlockFromToEvent event = new BlockFromToEvent(source, org.bukkit.craftbukkit.block.CraftBlock.notchToBlockFace(enumdirection1));
                if (server != null) {
                    server.getPluginManager().callEvent(event);
                }

                if (!event.isCancelled()) {
                    this.flow(world, blockposition.shift(enumdirection1), world.getType(blockposition.shift(enumdirection1)), k);
                }
                // CraftBukkit end
            }
        }

    }

    private void flow(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        if (world.isLoaded(blockposition) && this.h(world, blockposition, iblockdata)) { // CraftBukkit - add isLoaded check
            if (iblockdata.getBlock() != Blocks.AIR) {
                if (this.material == Material.LAVA) {
                    this.fizz(world, blockposition);
                } else {
                    iblockdata.getBlock().b(world, blockposition, iblockdata, 0);
                }
            }

            world.setTypeAndData(blockposition, this.getBlockData().set(BlockFlowing.LEVEL, Integer.valueOf(i)), 3);
        }

    }

    private int a(World world, BlockPosition blockposition, int i, EnumDirection enumdirection) {
        int j = 1000;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection1 = (EnumDirection) iterator.next();

            if (enumdirection1 != enumdirection) {
                BlockPosition blockposition1 = blockposition.shift(enumdirection1);
                IBlockData iblockdata = world.getType(blockposition1);

                if (!this.g(world, blockposition1, iblockdata) && (iblockdata.getBlock().getMaterial() != this.material || ((Integer) iblockdata.get(BlockFlowing.LEVEL)).intValue() > 0)) {
                    if (!this.g(world, blockposition1.down(), iblockdata)) {
                        return i;
                    }

                    if (i < 4) {
                        int k = this.a(world, blockposition1, i + 1, enumdirection1.opposite());

                        if (k < j) {
                            j = k;
                        }
                    }
                }
            }
        }

        return j;
    }

    private Set<EnumDirection> f(World world, BlockPosition blockposition) {
        int i = 1000;
        EnumSet enumset = EnumSet.noneOf(EnumDirection.class);
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            IBlockData iblockdata = world.getType(blockposition1);

            if (!this.g(world, blockposition1, iblockdata) && (iblockdata.getBlock().getMaterial() != this.material || ((Integer) iblockdata.get(BlockFlowing.LEVEL)).intValue() > 0)) {
                int j;

                if (this.g(world, blockposition1.down(), world.getType(blockposition1.down()))) {
                    j = this.a(world, blockposition1, 1, enumdirection.opposite());
                } else {
                    j = 0;
                }

                if (j < i) {
                    enumset.clear();
                }

                if (j <= i) {
                    enumset.add(enumdirection);
                    i = j;
                }
            }
        }

        return enumset;
    }

    private boolean g(World world, BlockPosition blockposition, IBlockData iblockdata) {
        Block block = world.getType(blockposition).getBlock();

        return !(block instanceof BlockDoor) && block != Blocks.STANDING_SIGN && block != Blocks.LADDER && block != Blocks.REEDS ? (block.material == Material.PORTAL ? true : block.material.isSolid()) : true;
    }

    protected int a(World world, BlockPosition blockposition, int i) {
        int j = this.e(world, blockposition);

        if (j < 0) {
            return i;
        } else {
            if (j == 0) {
                ++this.a;
            }

            if (j >= 8) {
                j = 0;
            }

            return i >= 0 && j >= i ? i : j;
        }
    }

    private boolean h(World world, BlockPosition blockposition, IBlockData iblockdata) {
        Material material = iblockdata.getBlock().getMaterial();

        return material != this.material && material != Material.LAVA && !this.g(world, blockposition, iblockdata);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!this.e(world, blockposition, iblockdata)) {
            world.a(blockposition, (Block) this, this.getFlowSpeed(world, blockposition)); // PaperSpigot
        }

    }

    /**
     * PaperSpigot - Get flow speed. Throttle if its water and flowing adjacent to lava
     */
    public int getFlowSpeed(World world, BlockPosition blockposition) {
        if (this.getMaterial() == Material.LAVA) {
            return world.worldProvider.o() ? world.paperSpigotConfig.lavaFlowSpeedNether : world.paperSpigotConfig.lavaFlowSpeedNormal;
        }
        if (this.getMaterial() == Material.WATER && (
                world.getType(blockposition.north(1)).getBlock().getMaterial() == Material.LAVA ||
                        world.getType(blockposition.south(1)).getBlock().getMaterial() == Material.LAVA ||
                        world.getType(blockposition.west(1)).getBlock().getMaterial() == Material.LAVA ||
                        world.getType(blockposition.east(1)).getBlock().getMaterial() == Material.LAVA)) {
            return world.paperSpigotConfig.waterOverLavaFlowSpeed;
        }
        return super.a(world);
    }

    /**
     * PaperSpigot - Data check method for fast draining
     */
    public int getData(World world, BlockPosition position) {
        int data = this.e(world, position);
        return data < 8 ? data : 0;
    }

    /**
     * PaperSpigot - Checks surrounding blocks to determine if block can be fast drained
     */
    public boolean canFastDrain(World world, BlockPosition position) {
        boolean result = false;
        int data = getData(world, position);
        if (this.material == Material.WATER) {
            if (world.paperSpigotConfig.fastDrainWater) {
                result = true;
                if (getData(world, position.down()) < 0) {
                    result = false;
                } else if (world.getType(position.north()).getBlock().getMaterial() == Material.WATER && getData(world, position.north()) < data) {
                    result = false;
                } else if (world.getType(position.south()).getBlock().getMaterial() == Material.WATER && getData(world, position.south()) < data) {
                    result = false;
                } else if (world.getType(position.west()).getBlock().getMaterial() == Material.WATER && getData(world, position.west()) < data) {
                    result = false;
                } else if (world.getType(position.east()).getBlock().getMaterial() == Material.WATER && getData(world, position.east()) < data) {
                    result = false;
                }
            }
        } else if (this.material == Material.LAVA) {
            if (world.paperSpigotConfig.fastDrainLava) {
                result = true;
                if (getData(world, position.down()) < 0 || world.getType(position.up()).getBlock().getMaterial() != Material.AIR) {
                    result = false;
                } else if (world.getType(position.north()).getBlock().getMaterial() == Material.LAVA && getData(world, position.north()) < data) {
                    result = false;
                } else if (world.getType(position.south()).getBlock().getMaterial() == Material.LAVA && getData(world, position.south()) < data) {
                    result = false;
                } else if (world.getType(position.west()).getBlock().getMaterial() == Material.LAVA && getData(world, position.west()) < data) {
                    result = false;
                } else if (world.getType(position.east()).getBlock().getMaterial() == Material.LAVA && getData(world, position.east()) < data) {
                    result = false;
                }
            }
        }
        return result;
    }
}
