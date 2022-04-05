package net.techcable.tacospigot;

import net.minecraft.server.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class HopperHelper {

    public static TileEntityHopper getHopper(World world, BlockPosition pos) {
        if (world.getType(pos).getBlock() != Blocks.HOPPER) return null;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityHopper) {
            return (TileEntityHopper) tileEntity;
        }
        return null;
    }

    public static IInventory getInventory(World world, BlockPosition position) {
        Block block = world.getType(position).getBlock();
        if (block instanceof BlockChest) {
            return ((BlockChest) block).getInventory(world, position);
        }
        if (block.isTileEntity()) {
            TileEntity tile = world.getTileEntity(position);
            if (tile instanceof IInventory) return (IInventory) tile;
        }
        return null;
    }

    public static boolean isFireInventoryMoveItemEvent(IHopper hopper) {
        return hopper.getWorld().tacoSpigotConfig.isHopperFireIMIE && InventoryMoveItemEvent.getHandlerList().getRegisteredListeners().length > 0;
    }
}
