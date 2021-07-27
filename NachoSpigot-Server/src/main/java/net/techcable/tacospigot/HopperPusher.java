package net.techcable.tacospigot;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Entity;
import net.minecraft.server.MathHelper;
import net.minecraft.server.TileEntityHopper;
import net.minecraft.server.World;

public interface HopperPusher {
    
    public default TileEntityHopper findHopper() {
        int y1 = MathHelper.floor(getY());
        for (int y = y1; y > y1 - 2; y--) {
            TileEntityHopper hopper = HopperHelper.getHopper(getWorld(), new BlockPosition(getX(), y, getZ()));
            if (hopper != null && y1 - y < 2) {
                return hopper;
            }
        }
        return null;
    }

    public boolean acceptItem(TileEntityHopper hopper);

    public default boolean tryPutInHopper() {
        if (!getWorld().tacoSpigotConfig.isHopperPushBased) return false;
        TileEntityHopper hopper = findHopper();
        return hopper != null && hopper.canAcceptItems() && acceptItem(hopper);
    }

    public AxisAlignedBB getBoundingBox();

    public World getWorld();

    // Default implementations for entities

    public default double getX() {
        return ((Entity) this).locX;
    }

    public default double getY() {
        return ((Entity) this).locY;
    }

    public default double getZ() {
        return ((Entity) this).locZ;
    }

}
