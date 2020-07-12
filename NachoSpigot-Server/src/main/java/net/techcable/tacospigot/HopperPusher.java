package net.techcable.tacospigot;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Entity;
import net.minecraft.server.IHopper;
import net.minecraft.server.TileEntityHopper;
import net.minecraft.server.World;

public interface HopperPusher {

    public default TileEntityHopper findHopper() {
        BlockPosition pos = new BlockPosition(getX(), getY(), getZ());
        int startX = pos.getX() - 1;
        int endX = pos.getX() + 1;
        int startY = Math.max(0, pos.getY() - 1);
        int endY = Math.min(255, pos.getY() + 1);
        int startZ = pos.getZ() - 1;
        int endZ = pos.getZ() + 1;
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    BlockPosition adjacentPos = new BlockPosition(x, y, z);
                    TileEntityHopper hopper = HopperHelper.getHopper(getWorld(), adjacentPos);
                    if (hopper == null) continue; // Avoid playing with the bounding boxes, if at all possible
                    /**
                     * We add one to getY(), just like {@link TileEntityHopper#b(IHopper)}
                     */
                    AxisAlignedBB hopperBoundingBox = hopper.getHopperLookupBoundingBox();
                    if (hopperBoundingBox.b(this.getBoundingBox())) { // AxisAlignedBB.b(AxisAlignedBB) -> isIntersect()
                        return hopper;
                    }
                }
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
