package net.minecraft.server;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockBloodStone extends Block {

    public BlockBloodStone() {
        super(Material.STONE);
        this.a(CreativeModeTab.b);
    }

    public MaterialMapColor g(IBlockData iblockdata) {
        return MaterialMapColor.K;
    }

    // CraftBukkit start
    //@Override // PaperSpigot - Remove completely invalid Redstone event for Netherrack
    public void doPhysics_nvmplsdont(World world, BlockPosition position, IBlockData iblockdata, Block block) {
        if (block != null && block.isPowerSource()) {
            org.bukkit.block.Block bl = world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
            int power = bl.getBlockPower();

            BlockRedstoneEvent event = new BlockRedstoneEvent(bl, power, power);
            world.getServer().getPluginManager().callEvent(event);
        }
    }
    // CraftBukkit end
}
