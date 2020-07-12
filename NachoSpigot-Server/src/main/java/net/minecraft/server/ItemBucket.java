package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
// CraftBukkit end

import org.github.paperspigot.PaperSpigotConfig; // PaperSpigot

public class ItemBucket extends Item {

    private Block a;

    public ItemBucket(Block block) {
        this.maxStackSize = 1;
        this.a = block;
        this.a(CreativeModeTab.f);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        boolean flag = this.a == Blocks.AIR;
        MovingObjectPosition movingobjectposition = this.a(world, entityhuman, flag);

        if (movingobjectposition == null) {
            return itemstack;
        } else {
            if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                BlockPosition blockposition = movingobjectposition.a();

                if (!world.a(entityhuman, blockposition)) {
                    return itemstack;
                }

                if (flag) {
                    if (!entityhuman.a(blockposition.shift(movingobjectposition.direction), movingobjectposition.direction, itemstack)) {
                        return itemstack;
                    }

                    IBlockData iblockdata = world.getType(blockposition);
                    Material material = iblockdata.getBlock().getMaterial();

                    if (material == Material.WATER && ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() == 0) {
                        // CraftBukkit start
                        PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(entityhuman, blockposition.getX(), blockposition.getY(), blockposition.getZ(), null, itemstack, Items.WATER_BUCKET);

                        if (event.isCancelled()) {
                            return itemstack;
                        }
                        // CraftBukkit end
                        world.setAir(blockposition);
                        entityhuman.b(StatisticList.USE_ITEM_COUNT[Item.getId(this)]);
                        return this.a(itemstack, entityhuman, Items.WATER_BUCKET, event.getItemStack()); // CraftBukkit - added Event stack
                    }

                    if (material == Material.LAVA && ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() == 0) {
                        // CraftBukkit start
                        PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(entityhuman, blockposition.getX(), blockposition.getY(), blockposition.getZ(), null, itemstack, Items.LAVA_BUCKET);

                        if (event.isCancelled()) {
                            return itemstack;
                        }
                        // CraftBukkit end
                        world.setAir(blockposition);
                        entityhuman.b(StatisticList.USE_ITEM_COUNT[Item.getId(this)]);
                        return this.a(itemstack, entityhuman, Items.LAVA_BUCKET, event.getItemStack()); // CraftBukkit - added Event stack
                    }
                } else {
                    if (this.a == Blocks.AIR) {
                        // CraftBukkit start
                        PlayerBucketEmptyEvent event = CraftEventFactory.callPlayerBucketEmptyEvent(entityhuman, blockposition.getX(), blockposition.getY(), blockposition.getZ(), movingobjectposition.direction, itemstack);

                        if (event.isCancelled()) {
                            return itemstack;
                        }

                        return CraftItemStack.asNMSCopy(event.getItemStack());
                        // CraftBukkit end
                    }

                    BlockPosition blockposition1 = blockposition.shift(movingobjectposition.direction);

                    if (!entityhuman.a(blockposition1, movingobjectposition.direction, itemstack)) {
                        return itemstack;
                    }

                    // CraftBukkit start
                    PlayerBucketEmptyEvent event = CraftEventFactory.callPlayerBucketEmptyEvent(entityhuman, blockposition.getX(), blockposition.getY(), blockposition.getZ(), movingobjectposition.direction, itemstack);

                    if (event.isCancelled()) {
                        return itemstack;
                    }
                    // CraftBukkit end

                    if (this.a(world, blockposition1) && !entityhuman.abilities.canInstantlyBuild) {
                        entityhuman.b(StatisticList.USE_ITEM_COUNT[Item.getId(this)]);
                        // PaperSpigot start - Stackable Buckets
                        if ((this == Items.LAVA_BUCKET && PaperSpigotConfig.stackableLavaBuckets) ||
                                (this == Items.WATER_BUCKET && PaperSpigotConfig.stackableWaterBuckets)) {
                            if (--itemstack.count <= 0) {
                                return CraftItemStack.asNMSCopy(event.getItemStack());
                            }
                            if (!entityhuman.inventory.pickup(CraftItemStack.asNMSCopy(event.getItemStack()))) {
                                entityhuman.drop(CraftItemStack.asNMSCopy(event.getItemStack()), false);
                            }
                            return itemstack;
                        }
                        // PaperSpigot end
                        return CraftItemStack.asNMSCopy(event.getItemStack()); // CraftBukkit
                    }
                }
            }

            return itemstack;
        }
    }

    // CraftBukkit - added ob.ItemStack result - TODO: Is this... the right way to handle this?
    private ItemStack a(ItemStack itemstack, EntityHuman entityhuman, Item item, org.bukkit.inventory.ItemStack result) {
        if (entityhuman.abilities.canInstantlyBuild) {
            return itemstack;
        } else if (--itemstack.count <= 0) {
            return CraftItemStack.asNMSCopy(result); // CraftBukkit
        } else {
            if (!entityhuman.inventory.pickup(CraftItemStack.asNMSCopy(result))) {
                entityhuman.drop(CraftItemStack.asNMSCopy(result), false);
            }

            return itemstack;
        }
    }

    public boolean a(World world, BlockPosition blockposition) {
        if (this.a == Blocks.AIR) {
            return false;
        } else {
            Material material = world.getType(blockposition).getBlock().getMaterial();
            boolean flag = !material.isBuildable();

            if (!world.isEmpty(blockposition) && !flag) {
                return false;
            } else {
                if (world.worldProvider.n() && this.a == Blocks.FLOWING_WATER) {
                    int i = blockposition.getX();
                    int j = blockposition.getY();
                    int k = blockposition.getZ();

                    world.makeSound((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                    for (int l = 0; l < 8; ++l) {
                        world.addParticle(EnumParticle.SMOKE_LARGE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                } else {
                    if (!world.isClientSide && flag && !material.isLiquid()) {
                        world.setAir(blockposition, true);
                    }

                    world.setTypeAndData(blockposition, this.a.getBlockData(), 3);
                }

                return true;
            }
        }
    }
}
