package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
// CraftBukkit end

public class ContainerBrewingStand extends Container {

    private IInventory brewingStand;
    private final Slot f;
    private int g;

    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private PlayerInventory player;
    // CraftBukkit end

    public ContainerBrewingStand(PlayerInventory playerinventory, IInventory iinventory) {
        player = playerinventory; // CraftBukkit
        this.brewingStand = iinventory;
        this.a((Slot) (new ContainerBrewingStand.SlotPotionBottle(playerinventory.player, iinventory, 0, 56, 46)));
        this.a((Slot) (new ContainerBrewingStand.SlotPotionBottle(playerinventory.player, iinventory, 1, 79, 53)));
        this.a((Slot) (new ContainerBrewingStand.SlotPotionBottle(playerinventory.player, iinventory, 2, 102, 46)));
        this.f = this.a((Slot) (new ContainerBrewingStand.SlotBrewing(iinventory, 3, 79, 17)));

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(playerinventory, i, 8 + i * 18, 142));
        }

    }

    public void addSlotListener(ICrafting icrafting) {
        super.addSlotListener(icrafting);
        icrafting.setContainerData(this, this.brewingStand);
    }

    public void b() {
        super.b();

        for (int i = 0; i < this.listeners.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.listeners.get(i);

            if (this.g != this.brewingStand.getProperty(0)) {
                icrafting.setContainerData(this, 0, this.brewingStand.getProperty(0));
            }
        }

        this.g = this.brewingStand.getProperty(0);
    }

    public boolean a(EntityHuman entityhuman) {
        if (!this.checkReachable) return true; // CraftBukkit
        return this.brewingStand.a(entityhuman);
    }

    public ItemStack b(EntityHuman entityhuman, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.c.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if ((i < 0 || i > 2) && i != 3) {
                if (!this.f.hasItem() && this.f.isAllowed(itemstack1)) {
                    if (!this.a(itemstack1, 3, 4, false)) {
                        return null;
                    }
                } else if (ContainerBrewingStand.SlotPotionBottle.b_(itemstack)) {
                    if (!this.a(itemstack1, 0, 3, false)) {
                        return null;
                    }
                } else if (i >= 4 && i < 31) {
                    if (!this.a(itemstack1, 31, 40, false)) {
                        return null;
                    }
                } else if (i >= 31 && i < 40) {
                    if (!this.a(itemstack1, 4, 31, false)) {
                        return null;
                    }
                } else if (!this.a(itemstack1, 4, 40, false)) {
                    return null;
                }
            } else {
                if (!this.a(itemstack1, 4, 40, true)) {
                    return null;
                }

                slot.a(itemstack1, itemstack);
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

    class SlotBrewing extends Slot {

        public SlotBrewing(IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
        }

        public boolean isAllowed(ItemStack itemstack) {
            return itemstack != null ? itemstack.getItem().l(itemstack) : false;
        }

        public int getMaxStackSize() {
            return 64;
        }
    }

    static class SlotPotionBottle extends Slot {

        private EntityHuman a;

        public SlotPotionBottle(EntityHuman entityhuman, IInventory iinventory, int i, int j, int k) {
            super(iinventory, i, j, k);
            this.a = entityhuman;
        }

        public boolean isAllowed(ItemStack itemstack) {
            return b_(itemstack);
        }

        public int getMaxStackSize() {
            return 1;
        }

        public void a(EntityHuman entityhuman, ItemStack itemstack) {
            if (itemstack.getItem() == Items.POTION && itemstack.getData() > 0) {
                this.a.b((Statistic) AchievementList.B);
            }

            super.a(entityhuman, itemstack);
        }

        public static boolean b_(ItemStack itemstack) {
            return itemstack != null && (itemstack.getItem() == Items.POTION || itemstack.getItem() == Items.GLASS_BOTTLE);
        }
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryBrewer inventory = new CraftInventoryBrewer(this.brewingStand);
        bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
