package net.minecraft.server;

import org.bukkit.event.player.PlayerFishEvent; // CraftBukkit

public class ItemFishingRod extends Item {

    public ItemFishingRod() {
        this.setMaxDurability(64);
        this.c(1);
        this.a(CreativeModeTab.i);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (entityhuman.hookedFish != null) {
            int i = entityhuman.hookedFish.l();

            itemstack.damage(i, entityhuman);
            entityhuman.bw();
        } else {
            // CraftBukkit start
            EntityFishingHook hook = new EntityFishingHook(world, entityhuman);
            PlayerFishEvent playerFishEvent = new PlayerFishEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), null, (org.bukkit.entity.Fish) hook.getBukkitEntity(), PlayerFishEvent.State.FISHING);
            world.getServer().getPluginManager().callEvent(playerFishEvent);

            if (playerFishEvent.isCancelled()) {
                entityhuman.hookedFish = null;
                return itemstack;
            }
            // CraftBukkit end
            world.makeSound(entityhuman, "random.bow", 0.5F, 0.4F / (ItemFishingRod.g.nextFloat() * 0.4F + 0.8F));
            if (!world.isClientSide) {
                world.addEntity(hook); // CraftBukkit - moved creation up
            }

            entityhuman.bw();
            entityhuman.b(StatisticList.USE_ITEM_COUNT[Item.getId(this)]);
        }

        return itemstack;
    }

    public boolean f_(ItemStack itemstack) {
        return super.f_(itemstack);
    }

    public int b() {
        return 1;
    }
}
