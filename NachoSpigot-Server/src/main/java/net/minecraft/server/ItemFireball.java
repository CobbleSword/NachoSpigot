package net.minecraft.server;

public class ItemFireball extends Item {

    public ItemFireball() {
        this.a(CreativeModeTab.f);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            blockposition = blockposition.shift(enumdirection);
            if (!entityhuman.a(blockposition, enumdirection, itemstack)) {
                return false;
            } else {
                if (world.getType(blockposition).getBlock().getMaterial() == Material.AIR) {
                    // CraftBukkit start - fire BlockIgniteEvent
                    if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), org.bukkit.event.block.BlockIgniteEvent.IgniteCause.FIREBALL, entityhuman).isCancelled()) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            --itemstack.count;
                        }
                        return false;
                    }
                    // CraftBukkit end
                    world.makeSound((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, "item.fireCharge.use", 1.0F, (ItemFireball.g.nextFloat() - ItemFireball.g.nextFloat()) * 0.2F + 1.0F);
                    world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                }

                if (!entityhuman.abilities.canInstantlyBuild) {
                    --itemstack.count;
                }

                return true;
            }
        }
    }
}
