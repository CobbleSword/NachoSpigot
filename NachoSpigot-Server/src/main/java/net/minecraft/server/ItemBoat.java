package net.minecraft.server;

import java.util.List;

public class ItemBoat extends Item {

    public ItemBoat() {
        this.maxStackSize = 1;
        this.a(CreativeModeTab.e);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        float f = 1.0F;
        float f1 = entityhuman.lastPitch + (entityhuman.pitch - entityhuman.lastPitch) * f;
        float f2 = entityhuman.lastYaw + (entityhuman.yaw - entityhuman.lastYaw) * f;
        double d0 = entityhuman.lastX + (entityhuman.locX - entityhuman.lastX) * (double) f;
        double d1 = entityhuman.lastY + (entityhuman.locY - entityhuman.lastY) * (double) f + (double) entityhuman.getHeadHeight();
        double d2 = entityhuman.lastZ + (entityhuman.locZ - entityhuman.lastZ) * (double) f;
        Vec3D vec3d = new Vec3D(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        Vec3D vec3d1 = vec3d.add((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        MovingObjectPosition movingobjectpositionblock = world.rayTrace(vec3d, vec3d1, true);

        if (movingobjectpositionblock == null) {
            return itemstack;
        } else {
            Vec3D vec3d2 = entityhuman.d(f);
            boolean flag = false;
            float f9 = 1.0F;
            List list = world.getEntities(entityhuman, entityhuman.getBoundingBox().a(vec3d2.a * d3, vec3d2.b * d3, vec3d2.c * d3).grow((double) f9, (double) f9, (double) f9));

            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity.ad()) {
                    float f10 = entity.ao();
                    AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow((double) f10, (double) f10, (double) f10);

                    if (axisalignedbb.a(vec3d)) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                return itemstack;
            } else {
                if (movingobjectpositionblock.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {

                    BlockPosition blockposition = movingobjectpositionblock.a();
                    /* Paper - Remove unneeded interaction event trigger
                    // CraftBukkit start - Boat placement
                    org.bukkit.event.player.PlayerInteractEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent(entityhuman, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK, blockposition, movingobjectposition.direction, itemstack);

                    if (event.isCancelled()) {
                        return itemstack;
                    }
                    // CraftBukkit end
                    */ // Paper - Remove unneeded interaction event trigger

                    if (world.getType(blockposition).getBlock() == Blocks.SNOW_LAYER) {
                        blockposition = blockposition.down();
                    }

                    EntityBoat entityboat = new EntityBoat(world, (double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 1.0F), (double) ((float) blockposition.getZ() + 0.5F));
                    entityboat.yaw = (float) (((MathHelper.floor((double) (entityhuman.yaw * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);
                    if (!world.getCubes(entityboat, entityboat.getBoundingBox().grow(-0.1D, -0.1D, -0.1D)).isEmpty()) {
                        return itemstack;
                    }

                    if (!world.isClientSide) {
                        world.addEntity(entityboat);
                    }

                    if (!entityhuman.abilities.canInstantlyBuild) {
                        --itemstack.count;
                    }

                    entityhuman.b(StatisticList.USE_ITEM_COUNT[Item.getId(this)]);
                }

                return itemstack;
            }
        }
    }
}
