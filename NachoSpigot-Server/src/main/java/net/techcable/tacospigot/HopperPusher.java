package net.techcable.tacospigot;

import net.minecraft.server.*;

public interface HopperPusher {
    
    default TileEntityHopper findHopper() {
        int y1 = MathHelper.floor(getY());
        for (int y = y1; y > y1 - 2; y--) {
            TileEntityHopper hopper = HopperHelper.getHopper(getWorld(), new BlockPosition(getX(), y, getZ()));
            if (hopper != null && y1 - y < 2) {
                return hopper;
            }
        }
        return null;
    }

    boolean acceptItem(TileEntityHopper hopper);

    default boolean tryPutInHopper() {
        if (!getWorld().tacoSpigotConfig.isHopperPushBased) return false;
        TileEntityHopper hopper = findHopper();
        return hopper != null && hopper.canAcceptItems() && acceptItem(hopper);
    }

    AxisAlignedBB getBoundingBox();

    World getWorld();

    // Default implementations for entities

    default double getX() {
        return ((Entity) this).locX;
    }

    default double getY() {
        return ((Entity) this).locY;
    }

    default double getZ() {
        return ((Entity) this).locZ;
    }

}
