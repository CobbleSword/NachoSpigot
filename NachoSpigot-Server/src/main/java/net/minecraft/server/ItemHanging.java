package net.minecraft.server;

// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
// CraftBukkit end

public class ItemHanging extends Item {

    private final Class<? extends EntityHanging> a;

    public ItemHanging(Class<? extends EntityHanging> oclass) {
        this.a = oclass;
        this.a(CreativeModeTab.c);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        if (enumdirection == EnumDirection.DOWN) {
            return false;
        } else if (enumdirection == EnumDirection.UP) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            if (!entityhuman.a(blockposition1, enumdirection, itemstack)) {
                return false;
            } else {
                EntityHanging entityhanging = this.a(world, blockposition1, enumdirection);

                if (entityhanging != null && entityhanging.survives()) {
                    if (!world.isClientSide) {
                        // CraftBukkit start - fire HangingPlaceEvent
                        Player who = (entityhuman == null) ? null : (Player) entityhuman.getBukkitEntity();
                        org.bukkit.block.Block blockClicked = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                        org.bukkit.block.BlockFace blockFace = org.bukkit.craftbukkit.block.CraftBlock.notchToBlockFace(enumdirection);

                        HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) entityhanging.getBukkitEntity(), who, blockClicked, blockFace);
                        world.getServer().getPluginManager().callEvent(event);

                        PaintingPlaceEvent paintingEvent = null;
                        if (entityhanging instanceof EntityPainting) {
                            // Fire old painting event until it can be removed
                            paintingEvent = new PaintingPlaceEvent((org.bukkit.entity.Painting) entityhanging.getBukkitEntity(), who, blockClicked, blockFace);
                            paintingEvent.setCancelled(event.isCancelled());
                            world.getServer().getPluginManager().callEvent(paintingEvent);
                        }

                        if (event.isCancelled() || (paintingEvent != null && paintingEvent.isCancelled())) {
                            return false;
                        }
                        // CraftBukkit end
                        world.addEntity(entityhanging);
                    }

                    --itemstack.count;
                }

                return true;
            }
        }
    }

    private EntityHanging a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return (EntityHanging) (this.a == EntityPainting.class ? new EntityPainting(world, blockposition, enumdirection) : (this.a == EntityItemFrame.class ? new EntityItemFrame(world, blockposition, enumdirection) : null));
    }
}
