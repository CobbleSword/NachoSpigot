package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityInteractEvent;
// CraftBukkit end

public class BlockRedstoneOre extends Block {

    private final boolean a;

    public BlockRedstoneOre(boolean flag) {
        super(Material.STONE);
        if (flag) {
            this.a(true);
        }

        this.a = flag;
    }

    public int a(World world) {
        return 30;
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.e(world, blockposition, entityhuman); // CraftBukkit - add entityhuman
        super.attack(world, blockposition, entityhuman);
    }

    public void a(World world, BlockPosition blockposition, Entity entity) {
        // CraftBukkit start
        // this.e(world, blockposition);
        // super.a(world, blockposition, entity);
        if (entity instanceof EntityHuman) {
            org.bukkit.event.player.PlayerInteractEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((EntityHuman) entity, org.bukkit.event.block.Action.PHYSICAL, blockposition, null, null);
            if (!event.isCancelled()) {
                this.e(world, blockposition, entity); // add entity
                super.a(world, blockposition, entity);
            }
        } else {
            EntityInteractEvent event = new EntityInteractEvent(entity.getBukkitEntity(), world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
            world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.e(world, blockposition, entity); // add entity
                super.a(world, blockposition, entity);
            }
        }
        // CraftBukkit end
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        this.e(world, blockposition, entityhuman); // CraftBukkit - add entityhuman
        return super.interact(world, blockposition, iblockdata, entityhuman, enumdirection, f, f1, f2);
    }

    private void e(World world, BlockPosition blockposition, Entity entity) { // CraftBukkit - add Entity
        this.f(world, blockposition);
        if (this == Blocks.REDSTONE_ORE) {
            // CraftBukkit start
            if (CraftEventFactory.callEntityChangeBlockEvent(entity, blockposition.getX(), blockposition.getY(), blockposition.getZ(), Blocks.LIT_REDSTONE_ORE, 0).isCancelled()) {
                return;
            }
            // CraftBukkit end
            world.setTypeUpdate(blockposition, Blocks.LIT_REDSTONE_ORE.getBlockData());
        }

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (this == Blocks.LIT_REDSTONE_ORE) {
            // CraftBukkit start
            if (CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), Blocks.REDSTONE_ORE).isCancelled()) {
                return;
            }
            // CraftBukkit end
            world.setTypeUpdate(blockposition, Blocks.REDSTONE_ORE.getBlockData());
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REDSTONE;
    }

    public int getDropCount(int i, Random random) {
        return this.a(random) + random.nextInt(i + 1);
    }

    public int a(Random random) {
        return 4 + random.nextInt(2);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        super.dropNaturally(world, blockposition, iblockdata, f, i);
        /* CraftBukkit start - Delegated to getExpDrop
        if (this.getDropType(iblockdata, world.random, i) != Item.getItemOf(this)) {
            int j = 1 + world.random.nextInt(5);

            this.dropExperience(world, blockposition, j);
        }
        // */
    }

    @Override
    public int getExpDrop(World world, IBlockData data, int i) {
        if (this.getDropType(data, world.random, i) != Item.getItemOf(this)) {
            int j = 1 + world.random.nextInt(5);

            return j;
        }
        return 0;
        // CraftBukkit end
    }

    private void f(World world, BlockPosition blockposition) {
        Random random = world.random;
        double d0 = 0.0625D;

        for (int i = 0; i < 6; ++i) {
            double d1 = (double) ((float) blockposition.getX() + random.nextFloat());
            double d2 = (double) ((float) blockposition.getY() + random.nextFloat());
            double d3 = (double) ((float) blockposition.getZ() + random.nextFloat());

            if (i == 0 && !world.getType(blockposition.up()).getBlock().c()) {
                d2 = (double) blockposition.getY() + d0 + 1.0D;
            }

            if (i == 1 && !world.getType(blockposition.down()).getBlock().c()) {
                d2 = (double) blockposition.getY() - d0;
            }

            if (i == 2 && !world.getType(blockposition.south()).getBlock().c()) {
                d3 = (double) blockposition.getZ() + d0 + 1.0D;
            }

            if (i == 3 && !world.getType(blockposition.north()).getBlock().c()) {
                d3 = (double) blockposition.getZ() - d0;
            }

            if (i == 4 && !world.getType(blockposition.east()).getBlock().c()) {
                d1 = (double) blockposition.getX() + d0 + 1.0D;
            }

            if (i == 5 && !world.getType(blockposition.west()).getBlock().c()) {
                d1 = (double) blockposition.getX() - d0;
            }

            if (d1 < (double) blockposition.getX() || d1 > (double) (blockposition.getX() + 1) || d2 < 0.0D || d2 > (double) (blockposition.getY() + 1) || d3 < (double) blockposition.getZ() || d3 > (double) (blockposition.getZ() + 1)) {
                world.addParticle(EnumParticle.REDSTONE, d1, d2, d3, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }

    }

    protected ItemStack i(IBlockData iblockdata) {
        return new ItemStack(Blocks.REDSTONE_ORE);
    }
}
