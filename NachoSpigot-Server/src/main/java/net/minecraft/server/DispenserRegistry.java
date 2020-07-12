package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
// CraftBukkit end

public class DispenserRegistry {

    private static final PrintStream a = System.out;
    private static boolean b = false;
    private static final Logger c = LogManager.getLogger();

    public static boolean a() {
        return DispenserRegistry.b;
    }

    static void b() {
        BlockDispenser.REGISTRY.a(Items.ARROW, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition) {
                EntityArrow entityarrow = new EntityArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entityarrow.fromPlayer = 1;
                return entityarrow;
            }
        });
        BlockDispenser.REGISTRY.a(Items.EGG, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition) {
                return new EntityEgg(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }
        });
        BlockDispenser.REGISTRY.a(Items.SNOWBALL, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition) {
                return new EntitySnowball(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }
        });
        BlockDispenser.REGISTRY.a(Items.EXPERIENCE_BOTTLE, new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition) {
                return new EntityThrownExpBottle(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }

            protected float a() {
                return super.a() * 0.5F;
            }

            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        });
        BlockDispenser.REGISTRY.a(Items.POTION, new IDispenseBehavior() {
            private final DispenseBehaviorItem b = new DispenseBehaviorItem();

            public ItemStack a(ISourceBlock isourceblock, final ItemStack itemstack) {
                return ItemPotion.f(itemstack.getData()) ? (new DispenseBehaviorProjectile() {
                    protected IProjectile a(World world, IPosition iposition) {
                        return new EntityPotion(world, iposition.getX(), iposition.getY(), iposition.getZ(), itemstack.cloneItemStack());
                    }

                    protected float a() {
                        return super.a() * 0.5F;
                    }

                    protected float getPower() {
                        return super.getPower() * 1.25F;
                    }
                }).a(isourceblock, itemstack) : this.b.a(isourceblock, itemstack);
            }
        });
        BlockDispenser.REGISTRY.a(Items.SPAWN_EGG, new DispenseBehaviorItem() {
            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = BlockDispenser.b(isourceblock.f());
                double d0 = isourceblock.getX() + (double) enumdirection.getAdjacentX();
                double d1 = (double) ((float) isourceblock.getBlockPosition().getY() + 0.2F);
                double d2 = isourceblock.getZ() + (double) enumdirection.getAdjacentZ();
                // Entity entity = ItemMonsterEgg.a(isourceblock.getWorld(), itemstack.getData(), d0, d1, d2);

                // CraftBukkit start
                World world = isourceblock.getWorld();
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d0, d1, d2));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.count++;
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.count++;
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.a(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                itemstack1 = CraftItemStack.asNMSCopy(event.getItem());

                Entity entity = ItemMonsterEgg.spawnCreature(isourceblock.getWorld(), itemstack.getData(), event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DISPENSE_EGG);

                if (entity instanceof EntityLiving && itemstack.hasName()) {
                    ((EntityInsentient) entity).setCustomName(itemstack.getName());
                }

                // itemstack.a(1); // Handled during event processing
                // CraftBukkit end
                return itemstack;
            }
        });
        BlockDispenser.REGISTRY.a(Items.FIREWORKS, new DispenseBehaviorItem() {
            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = BlockDispenser.b(isourceblock.f());
                double d0 = isourceblock.getX() + (double) enumdirection.getAdjacentX();
                double d1 = (double) ((float) isourceblock.getBlockPosition().getY() + 0.2F);
                double d2 = isourceblock.getZ() + (double) enumdirection.getAdjacentZ();
                // CraftBukkit start
                World world = isourceblock.getWorld();
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d0, d1, d2));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.count++;
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.count++;
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.a(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                itemstack1 = CraftItemStack.asNMSCopy(event.getItem());
                EntityFireworks entityfireworks = new EntityFireworks(isourceblock.getWorld(), event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), itemstack1);

                isourceblock.getWorld().addEntity(entityfireworks);
                // itemstack.a(1); // Handled during event processing
                // CraftBukkit end
                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1002, isourceblock.getBlockPosition(), 0);
            }
        });
        BlockDispenser.REGISTRY.a(Items.FIRE_CHARGE, new DispenseBehaviorItem() {
            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = BlockDispenser.b(isourceblock.f());
                IPosition iposition = BlockDispenser.a(isourceblock);
                double d0 = iposition.getX() + (double) ((float) enumdirection.getAdjacentX() * 0.3F);
                double d1 = iposition.getY() + (double) ((float) enumdirection.getAdjacentY() * 0.3F);
                double d2 = iposition.getZ() + (double) ((float) enumdirection.getAdjacentZ() * 0.3F);
                World world = isourceblock.getWorld();
                Random random = world.random;
                double d3 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentX();
                double d4 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentY();
                double d5 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentZ();

                // CraftBukkit start
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d3, d4, d5));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.count++;
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.count++;
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.a(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                EntitySmallFireball entitysmallfireball = new EntitySmallFireball(world, d0, d1, d2, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
                entitysmallfireball.projectileSource = new org.bukkit.craftbukkit.projectiles.CraftBlockProjectileSource((TileEntityDispenser) isourceblock.getTileEntity());

                world.addEntity(entitysmallfireball);
                // itemstack.a(1); // Handled during event processing
                // CraftBukkit end
                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1009, isourceblock.getBlockPosition(), 0);
            }
        });
        BlockDispenser.REGISTRY.a(Items.BOAT, new DispenseBehaviorItem() {
            private final DispenseBehaviorItem b = new DispenseBehaviorItem();

            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = BlockDispenser.b(isourceblock.f());
                World world = isourceblock.getWorld();
                double d0 = isourceblock.getX() + (double) ((float) enumdirection.getAdjacentX() * 1.125F);
                double d1 = isourceblock.getY() + (double) ((float) enumdirection.getAdjacentY() * 1.125F);
                double d2 = isourceblock.getZ() + (double) ((float) enumdirection.getAdjacentZ() * 1.125F);
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
                Material material = world.getType(blockposition).getBlock().getMaterial();
                double d3;

                if (Material.WATER.equals(material)) {
                    d3 = 1.0D;
                } else {
                    if (!Material.AIR.equals(material) || !Material.WATER.equals(world.getType(blockposition.down()).getBlock().getMaterial())) {
                        return this.b.a(isourceblock, itemstack);
                    }

                    d3 = 0.0D;
                }

                // EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
                // CraftBukkit start
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d0, d1 + d3, d2));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.count++;
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.count++;
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.a(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                EntityBoat entityboat = new EntityBoat(world, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
                // CraftBukkit end

                world.addEntity(entityboat);
                // itemstack.a(1); // CraftBukkit - handled during event processing
                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
            }
        });
        DispenseBehaviorItem dispensebehavioritem = new DispenseBehaviorItem() {
            private final DispenseBehaviorItem b = new DispenseBehaviorItem();

            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                ItemBucket itembucket = (ItemBucket) itemstack.getItem();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(BlockDispenser.b(isourceblock.f()));

                // CraftBukkit start
                World world = isourceblock.getWorld();
                int x = blockposition.getX();
                int y = blockposition.getY();
                int z = blockposition.getZ();
                if (world.isEmpty(blockposition) || !world.getType(blockposition).getBlock().getMaterial().isBuildable()) {
                    org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                    CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                    BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(x, y, z));
                    if (!BlockDispenser.eventFired) {
                        world.getServer().getPluginManager().callEvent(event);
                    }

                    if (event.isCancelled()) {
                        return itemstack;
                    }

                    if (!event.getItem().equals(craftItem)) {
                        // Chain to handler for new item
                        ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                        IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                        if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                            idispensebehavior.a(isourceblock, eventStack);
                            return itemstack;
                        }
                    }

                    itembucket = (ItemBucket) CraftItemStack.asNMSCopy(event.getItem()).getItem();
                }
                // CraftBukkit end          

                if (itembucket.a(isourceblock.getWorld(), blockposition)) {
                    // CraftBukkit start - Handle stacked buckets
                    Item item = Items.BUCKET;
                    if (--itemstack.count == 0) {
                        itemstack.setItem(Items.BUCKET);
                        itemstack.count = 1;
                    } else if (((TileEntityDispenser) isourceblock.getTileEntity()).addItem(new ItemStack(item)) < 0) {
                        this.b.a(isourceblock, new ItemStack(item));
                    }
                    // CraftBukkit end
                    return itemstack;
                } else {
                    return this.b.a(isourceblock, itemstack);
                }
            }
        };

        BlockDispenser.REGISTRY.a(Items.LAVA_BUCKET, dispensebehavioritem);
        BlockDispenser.REGISTRY.a(Items.WATER_BUCKET, dispensebehavioritem);
        BlockDispenser.REGISTRY.a(Items.BUCKET, new DispenseBehaviorItem() {
            private final DispenseBehaviorItem b = new DispenseBehaviorItem();

            public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(BlockDispenser.b(isourceblock.f()));
                IBlockData iblockdata = world.getType(blockposition);
                Block block = iblockdata.getBlock();
                Material material = block.getMaterial();
                Item item;

                if (Material.WATER.equals(material) && block instanceof BlockFluids && ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() == 0) {
                    item = Items.WATER_BUCKET;
                } else {
                    if (!Material.LAVA.equals(material) || !(block instanceof BlockFluids) || ((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() != 0) {
                        return super.b(isourceblock, itemstack);
                    }

                    item = Items.LAVA_BUCKET;
                }

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.a(isourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                world.setAir(blockposition);
                if (--itemstack.count == 0) {
                    itemstack.setItem(item);
                    itemstack.count = 1;
                } else if (((TileEntityDispenser) isourceblock.getTileEntity()).addItem(new ItemStack(item)) < 0) {
                    this.b.a(isourceblock, new ItemStack(item));
                }

                return itemstack;
            }
        });
        BlockDispenser.REGISTRY.a(Items.FLINT_AND_STEEL, new DispenseBehaviorItem() {
            private boolean b = true;

            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(BlockDispenser.b(isourceblock.f()));

                // CraftBukkit start
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.a(isourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                if (world.isEmpty(blockposition)) {
                    // CraftBukkit start - Ignition by dispensing flint and steel
                    if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ()).isCancelled()) {
                        world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                        if (itemstack.isDamaged(1, world.random)) {
                            itemstack.count = 0;
                        }
                    }
                    // CraftBukkit end
                } else if (world.getType(blockposition).getBlock() == Blocks.TNT) {
                    Blocks.TNT.postBreak(world, blockposition, Blocks.TNT.getBlockData().set(BlockTNT.EXPLODE, Boolean.valueOf(true)));
                    world.setAir(blockposition);
                } else {
                    this.b = false;
                }

                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                if (this.b) {
                    isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
                } else {
                    isourceblock.getWorld().triggerEffect(1001, isourceblock.getBlockPosition(), 0);
                }

            }
        });
        BlockDispenser.REGISTRY.a(Items.DYE, new DispenseBehaviorItem() {
            private boolean b = true;

            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                if (EnumColor.WHITE == EnumColor.fromInvColorIndex(itemstack.getData())) {
                    World world = isourceblock.getWorld();
                    BlockPosition blockposition = isourceblock.getBlockPosition().shift(BlockDispenser.b(isourceblock.f()));

                    // CraftBukkit start
                    org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                    CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack); // Spigot

                    BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                    if (!BlockDispenser.eventFired) {
                        world.getServer().getPluginManager().callEvent(event);
                    }

                    if (event.isCancelled()) {
                        return itemstack;
                    }

                    if (!event.getItem().equals(craftItem)) {
                        // Chain to handler for new item
                        ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                        IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                        if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                            idispensebehavior.a(isourceblock, eventStack);
                            return itemstack;
                        }
                    }
                    // CraftBukkit end

                    if (ItemDye.a(itemstack, world, blockposition)) {
                        if (!world.isClientSide) {
                            world.triggerEffect(2005, blockposition, 0);
                        }
                    } else {
                        this.b = false;
                    }

                    return itemstack;
                } else {
                    return super.b(isourceblock, itemstack);
                }
            }

            protected void a(ISourceBlock isourceblock) {
                if (this.b) {
                    isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
                } else {
                    isourceblock.getWorld().triggerEffect(1001, isourceblock.getBlockPosition(), 0);
                }

            }
        });
        BlockDispenser.REGISTRY.a(Item.getItemOf(Blocks.TNT), new DispenseBehaviorItem() {
            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(BlockDispenser.b(isourceblock.f()));
                // EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, (EntityLiving) null);

                // CraftBukkit start
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                // PaperSpigot start - Fix cannons
                double y = blockposition.getY();
                if (!world.paperSpigotConfig.fixCannons) y += 0.5;
                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX() + 0.5, y, blockposition.getZ() + 0.5));
                // PaperSpigot end
                if (!BlockDispenser.eventFired) {
                   world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.count++;
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.count++;
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.a(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(block.getLocation(), world, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), (EntityLiving) null); // PaperSpigot
                // CraftBukkit end

                world.addEntity(entitytntprimed);
                world.makeSound(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
                // --itemstack.count; // CraftBukkit - handled above
                return itemstack;
            }
        });
        BlockDispenser.REGISTRY.a(Items.SKULL, new DispenseBehaviorItem() {
            private boolean b = true;

            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                EnumDirection enumdirection = BlockDispenser.b(isourceblock.f());
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
                BlockSkull blockskull = Blocks.SKULL;

                if (world.isEmpty(blockposition) && blockskull.b(world, blockposition, itemstack)) {
                    if (!world.isClientSide) {
                        world.setTypeAndData(blockposition, blockskull.getBlockData().set(BlockSkull.FACING, EnumDirection.UP), 3);
                        TileEntity tileentity = world.getTileEntity(blockposition);

                        if (tileentity instanceof TileEntitySkull) {
                            if (itemstack.getData() == 3) {
                                GameProfile gameprofile = null;

                                if (itemstack.hasTag()) {
                                    NBTTagCompound nbttagcompound = itemstack.getTag();

                                    if (nbttagcompound.hasKeyOfType("SkullOwner", 10)) {
                                        gameprofile = GameProfileSerializer.deserialize(nbttagcompound.getCompound("SkullOwner"));
                                    } else if (nbttagcompound.hasKeyOfType("SkullOwner", 8)) {
                                        String s = nbttagcompound.getString("SkullOwner");

                                        if (!UtilColor.b(s)) {
                                            gameprofile = new GameProfile((UUID) null, s);
                                        }
                                    }
                                }

                                ((TileEntitySkull) tileentity).setGameProfile(gameprofile);
                            } else {
                                ((TileEntitySkull) tileentity).setSkullType(itemstack.getData());
                            }

                            ((TileEntitySkull) tileentity).setRotation(enumdirection.opposite().b() * 4);
                            Blocks.SKULL.a(world, blockposition, (TileEntitySkull) tileentity);
                        }

                        --itemstack.count;
                    }
                } else {
                    this.b = false;
                }

                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                if (this.b) {
                    isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
                } else {
                    isourceblock.getWorld().triggerEffect(1001, isourceblock.getBlockPosition(), 0);
                }

            }
        });
        BlockDispenser.REGISTRY.a(Item.getItemOf(Blocks.PUMPKIN), new DispenseBehaviorItem() {
            private boolean b = true;

            protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(BlockDispenser.b(isourceblock.f()));
                BlockPumpkin blockpumpkin = (BlockPumpkin) Blocks.PUMPKIN;

                if (world.isEmpty(blockposition) && blockpumpkin.e(world, blockposition)) {
                    if (!world.isClientSide) {
                        world.setTypeAndData(blockposition, blockpumpkin.getBlockData(), 3);
                    }

                    --itemstack.count;
                } else {
                    this.b = false;
                }

                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                if (this.b) {
                    isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
                } else {
                    isourceblock.getWorld().triggerEffect(1001, isourceblock.getBlockPosition(), 0);
                }

            }
        });
    }

    public static void c() {
        if (!DispenserRegistry.b) {
            DispenserRegistry.b = true;
            if (DispenserRegistry.c.isDebugEnabled()) {
                d();
            }

            Block.S();
            BlockFire.l();
            Item.t();
            StatisticList.a();
            b();
        }
    }

    private static void d() {
        System.setErr(new RedirectStream("STDERR", System.err));
        System.setOut(new RedirectStream("STDOUT", DispenserRegistry.a));
    }
}
