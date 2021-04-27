package me.suicidalkids.ion.movement;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;

public class MovementCache {

    private AxisAlignedBB bb;
    private double locX, locY, locZ, lastX, lastY, lastZ;
    private double motX, motY, motZ, lastMotX, lastMotY, lastMotZ;
    private boolean onGround;

    public boolean move(Entity entity) {
        if (entity.locX == lastX    && entity.locY == lastY    && entity.locZ == lastZ    &&
                entity.motX == lastMotX && entity.motY == lastMotY && entity.motZ == lastMotZ) {
            entity.boundingBox = bb;
            entity.onGround = onGround;
            entity.locX = locX;
            entity.locY = locY;
            entity.locZ = locZ;
            entity.motX = motX;
            entity.motY = motY;
            entity.motZ = motZ;
            return true;
        }

        return false;
    }

    public void cache(Entity entity) {
        onGround = entity.onGround;
        bb = entity.boundingBox;
        lastX = entity.lastX;
        lastY = entity.lastY;
        lastZ = entity.lastZ;
        lastMotX = entity.lastMotX;
        lastMotY = entity.lastMotY;
        lastMotZ = entity.lastMotZ;
        locX = entity.locX;
        locY = entity.locY;
        locZ = entity.locZ;
        motX = entity.motX;
        motY = entity.motY;
        motZ = entity.motZ;
    }

    public void clear() {
        lastX = Double.MAX_VALUE;
    }

}