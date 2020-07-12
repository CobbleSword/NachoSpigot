package net.minecraft.server;

import java.util.List;
import java.util.Random;

// CraftBukkit start
import java.util.Map;

import org.bukkit.craftbukkit.inventory.CraftInventoryEnchanting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.entity.Player;
// CraftBukkit end

public class ContainerEnchantTable extends Container {

    // CraftBukkit - make type specific (changed from IInventory)
    public InventorySubcontainer enchantSlots = new InventorySubcontainer("Enchant", true, 2) {
        public int getMaxStackSize() {
            return 64;
        }

        public void update() {
            super.update();
            ContainerEnchantTable.this.a((IInventory) this);
        }
    };
    private World world;
    private BlockPosition position;
    private Random k = new Random();
    public int f;
    public int[] costs = new int[3];
    public int[] h = new int[] { -1, -1, -1};
    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private Player player;
    // CraftBukkit end

    public ContainerEnchantTable(PlayerInventory playerinventory, World world, BlockPosition blockposition) {
        this.world = world;
        this.position = blockposition;
        this.f = playerinventory.player.cj();
        this.a(new Slot(this.enchantSlots, 0, 15, 47) {
            public boolean isAllowed(ItemStack itemstack) {
                return true;
            }

            public int getMaxStackSize() {
                return 1;
            }
        });
        this.a(new Slot(this.enchantSlots, 1, 35, 47) {
            public boolean isAllowed(ItemStack itemstack) {
                return itemstack.getItem() == Items.DYE && EnumColor.fromInvColorIndex(itemstack.getData()) == EnumColor.BLUE;
            }
        });

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(playerinventory, i, 8 + i * 18, 142));
        }

        // CraftBukkit start
        player = (Player) playerinventory.player.getBukkitEntity();
        // CraftBukkit end
    }

    public void addSlotListener(ICrafting icrafting) {
        super.addSlotListener(icrafting);
        icrafting.setContainerData(this, 0, this.costs[0]);
        icrafting.setContainerData(this, 1, this.costs[1]);
        icrafting.setContainerData(this, 2, this.costs[2]);
        icrafting.setContainerData(this, 3, this.f & -16);
        icrafting.setContainerData(this, 4, this.h[0]);
        icrafting.setContainerData(this, 5, this.h[1]);
        icrafting.setContainerData(this, 6, this.h[2]);
    }

    public void b() {
        super.b();

        for (int i = 0; i < this.listeners.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.listeners.get(i);

            icrafting.setContainerData(this, 0, this.costs[0]);
            icrafting.setContainerData(this, 1, this.costs[1]);
            icrafting.setContainerData(this, 2, this.costs[2]);
            icrafting.setContainerData(this, 3, this.f & -16);
            icrafting.setContainerData(this, 4, this.h[0]);
            icrafting.setContainerData(this, 5, this.h[1]);
            icrafting.setContainerData(this, 6, this.h[2]);
        }

    }

    public void a(IInventory iinventory) {
        if (iinventory == this.enchantSlots) {
            ItemStack itemstack = iinventory.getItem(0);
            int i;

            if (itemstack != null) { // CraftBukkit - relax condition
                if (!this.world.isClientSide) {
                    i = 0;

                    int j;

                    for (j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if ((j != 0 || k != 0) && this.world.isEmpty(this.position.a(k, 0, j)) && this.world.isEmpty(this.position.a(k, 1, j))) {
                                if (this.world.getType(this.position.a(k * 2, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                    ++i;
                                }

                                if (this.world.getType(this.position.a(k * 2, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                    ++i;
                                }

                                if (k != 0 && j != 0) {
                                    if (this.world.getType(this.position.a(k * 2, 0, j)).getBlock() == Blocks.BOOKSHELF) {
                                        ++i;
                                    }

                                    if (this.world.getType(this.position.a(k * 2, 1, j)).getBlock() == Blocks.BOOKSHELF) {
                                        ++i;
                                    }

                                    if (this.world.getType(this.position.a(k, 0, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                        ++i;
                                    }

                                    if (this.world.getType(this.position.a(k, 1, j * 2)).getBlock() == Blocks.BOOKSHELF) {
                                        ++i;
                                    }
                                }
                            }
                        }
                    }

                    this.k.setSeed((long) this.f);

                    for (j = 0; j < 3; ++j) {
                        this.costs[j] = EnchantmentManager.a(this.k, j, i, itemstack);
                        this.h[j] = -1;
                        if (this.costs[j] < j + 1) {
                            this.costs[j] = 0;
                        }
                    }

                    // CraftBukkit start
                    CraftItemStack item = CraftItemStack.asCraftMirror(itemstack);
                    PrepareItemEnchantEvent event = new PrepareItemEnchantEvent(player, this.getBukkitView(), this.world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), item, this.costs, i);
                    event.setCancelled(!itemstack.v());
                    this.world.getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        for (i = 0; i < 3; ++i) {
                            this.costs[i] = 0;
                        }
                        return;
                    }
                    // CraftBukkit end

                    for (j = 0; j < 3; ++j) {
                        if (this.costs[j] > 0) {
                            List list = this.a(itemstack, j, this.costs[j]);

                            if (list != null && !list.isEmpty()) {
                                WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) list.get(this.k.nextInt(list.size()));

                                this.h[j] = weightedrandomenchant.enchantment.id | weightedrandomenchant.level << 8;
                            }
                        }
                    }

                    this.b();
                }
            } else {
                for (i = 0; i < 3; ++i) {
                    this.costs[i] = 0;
                    this.h[i] = -1;
                }
            }
        }

    }

    public boolean a(EntityHuman entityhuman, int i) {
        ItemStack itemstack = this.enchantSlots.getItem(0);
        ItemStack itemstack1 = this.enchantSlots.getItem(1);
        int j = i + 1;

        if ((itemstack1 == null || itemstack1.count < j) && !entityhuman.abilities.canInstantlyBuild) {
            return false;
        } else if (this.costs[i] > 0 && itemstack != null && (entityhuman.expLevel >= j && entityhuman.expLevel >= this.costs[i] || entityhuman.abilities.canInstantlyBuild)) {
            if (!this.world.isClientSide) {
                List list = this.a(itemstack, i, this.costs[i]);
                // CraftBukkit start - Provide an empty enchantment list
                if (list == null) {
                    list = new java.util.ArrayList<WeightedRandomEnchant>();
                }
                // CraftBukkit end
                boolean flag = itemstack.getItem() == Items.BOOK;

                if (list != null) {
                    // CraftBukkit start
                    Map<org.bukkit.enchantments.Enchantment, Integer> enchants = new java.util.HashMap<org.bukkit.enchantments.Enchantment, Integer>();
                    for (Object obj : list) {
                        WeightedRandomEnchant instance = (WeightedRandomEnchant) obj;
                        enchants.put(org.bukkit.enchantments.Enchantment.getById(instance.enchantment.id), instance.level);
                    }
                    CraftItemStack item = CraftItemStack.asCraftMirror(itemstack);

                    EnchantItemEvent event = new EnchantItemEvent((Player) entityhuman.getBukkitEntity(), this.getBukkitView(), this.world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), item, this.costs[i], enchants, i);
                    this.world.getServer().getPluginManager().callEvent(event);

                    int level = event.getExpLevelCost();
                    if (event.isCancelled() || (level > entityhuman.expLevel && !entityhuman.abilities.canInstantlyBuild) || event.getEnchantsToAdd().isEmpty()) {
                        return false;
                    }
                    if (flag) {
                        itemstack.setItem(Items.ENCHANTED_BOOK);
                    }

                    for (Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : event.getEnchantsToAdd().entrySet()) {
                        try {
                            if (flag) {
                                int enchantId = entry.getKey().getId();
                                if (Enchantment.getById(enchantId) == null) {
                                    continue;
                                }

                                WeightedRandomEnchant enchantment = new WeightedRandomEnchant(Enchantment.getById(enchantId), entry.getValue());
                                Items.ENCHANTED_BOOK.a(itemstack, enchantment);
                            } else {
                                item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                            }
                        } catch (IllegalArgumentException e) {
                            /* Just swallow invalid enchantments */
                        }
                    }

                    entityhuman.enchantDone(j);
                    // CraftBukkit end

                    // CraftBukkit - TODO: let plugins change this
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack1.count -= j;
                        if (itemstack1.count <= 0) {
                            this.enchantSlots.setItem(1, (ItemStack) null);
                        }
                    }

                    entityhuman.b(StatisticList.W);
                    this.enchantSlots.update();
                    this.f = entityhuman.cj();
                    this.a(this.enchantSlots);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private List<WeightedRandomEnchant> a(ItemStack itemstack, int i, int j) {
        this.k.setSeed((long) (this.f + i));
        List list = EnchantmentManager.b(this.k, itemstack, j);

        if (itemstack.getItem() == Items.BOOK && list != null && list.size() > 1) {
            list.remove(this.k.nextInt(list.size()));
        }

        return list;
    }

    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        // CraftBukkit Start - If an enchantable was opened from a null location, set the world to the player's world, preventing a crash
        if(this.world == null) {
            this.world = entityhuman.getWorld();
        }
        // CraftBukkit end
        if (!this.world.isClientSide) {
            for (int i = 0; i < this.enchantSlots.getSize(); ++i) {
                ItemStack itemstack = this.enchantSlots.splitWithoutUpdate(i);

                if (itemstack != null) {
                    entityhuman.drop(itemstack, false);
                }
            }

        }
    }

    public boolean a(EntityHuman entityhuman) {
        if (!this.checkReachable) return true; // CraftBukkit
        return this.world.getType(this.position).getBlock() != Blocks.ENCHANTING_TABLE ? false : entityhuman.e((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public ItemStack b(EntityHuman entityhuman, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.c.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 0) {
                if (!this.a(itemstack1, 2, 38, true)) {
                    return null;
                }
            } else if (i == 1) {
                if (!this.a(itemstack1, 2, 38, true)) {
                    return null;
                }
            } else if (itemstack1.getItem() == Items.DYE && EnumColor.fromInvColorIndex(itemstack1.getData()) == EnumColor.BLUE) {
                if (!this.a(itemstack1, 1, 2, true)) {
                    return null;
                }
            } else {
                if (((Slot) this.c.get(0)).hasItem() || !((Slot) this.c.get(0)).isAllowed(itemstack1)) {
                    return null;
                }

                if (itemstack1.hasTag() && itemstack1.count == 1) {
                    ((Slot) this.c.get(0)).set(itemstack1.cloneItemStack());
                    itemstack1.count = 0;
                } else if (itemstack1.count >= 1) {
                    // Spigot start
                    ItemStack clone = itemstack1.cloneItemStack();
                    clone.count = 1;
                    ((Slot) this.c.get(0)).set(clone);
                    // Spigot end
                    --itemstack1.count;
                }
            }

            if (itemstack1.count == 0) {
                slot.set((ItemStack) null);
            } else {
                slot.f();
            }

            if (itemstack1.count == itemstack.count) {
                return null;
            }

            slot.a(entityhuman, itemstack1);
        }

        return itemstack;
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryEnchanting inventory = new CraftInventoryEnchanting(this.enchantSlots);
        bukkitEntity = new CraftInventoryView(this.player, inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
