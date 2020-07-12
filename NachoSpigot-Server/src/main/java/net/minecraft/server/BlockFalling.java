package net.minecraft.server;

import java.util.Random;

public class BlockFalling extends Block {

    public static boolean instaFall;

    public BlockFalling() {
        super(Material.SAND);
        this.a(CreativeModeTab.b);
    }

    public BlockFalling(Material material) {
        super(material);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.a(blockposition, (Block) this, this.a(world));
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        world.a(blockposition, (Block) this, this.a(world));
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            this.f(world, blockposition);
        }

    }

    private void f(World world, BlockPosition blockposition) {
        if (canFall(world, blockposition.down()) && blockposition.getY() >= 0) {
            byte b0 = 32;

            if (!BlockFalling.instaFall && world.areChunksLoadedBetween(blockposition.a(-b0, -b0, -b0), blockposition.a(b0, b0, b0))) {
                if (!world.isClientSide) {
                    // PaperSpigot start - Add FallingBlock source location API
                    org.bukkit.Location loc = new org.bukkit.Location(world.getWorld(), (double) ((float) blockposition.getX() + 0.5F), (double) blockposition.getY(), (double) ((float) blockposition.getZ() + 0.5F));
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(loc, world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, world.getType(blockposition));
                    // PaperSpigot end

                    this.a(entityfallingblock);
                    world.addEntity(entityfallingblock);
                }
            } else {
                world.setAir(blockposition);

                BlockPosition blockposition1;

                for (blockposition1 = blockposition.down(); canFall(world, blockposition1) && blockposition1.getY() > 0; blockposition1 = blockposition1.down()) {
                    ;
                }

                if (blockposition1.getY() > 0) {
                    world.setTypeUpdate(blockposition1.up(), this.getBlockData());
                }
            }

        }
    }

    protected void a(EntityFallingBlock entityfallingblock) {}

    public int a(World world) {
        return 2;
    }

    public static boolean canFall(World world, BlockPosition blockposition) {
        Block block = world.getType(blockposition).getBlock();
        Material material = block.material;

        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

    public void a_(World world, BlockPosition blockposition) {}
}
