package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
// CraftBukkit end

public class ContainerWorkbench extends Container {

    public InventoryCrafting craftInventory; // CraftBukkit - move initialization into constructor
    public IInventory resultInventory; // CraftBukkit - move initialization into constructor
    private World g;
    private BlockPosition h;
    // CraftBukkit start
    private CraftInventoryView bukkitEntity = null;
    private PlayerInventory player;
    // CraftBukkit end

    public ContainerWorkbench(PlayerInventory playerinventory, World world, BlockPosition blockposition) {
        // CraftBukkit start - Switched order of IInventory construction and stored player
        this.resultInventory = new InventoryCraftResult();
        this.craftInventory = new InventoryCrafting(this, 3, 3, playerinventory.player); // CraftBukkit - pass player
        this.craftInventory.resultInventory = this.resultInventory;
        this.player = playerinventory;
        // CraftBukkit end
        this.g = world;
        this.h = blockposition;
        this.a((Slot) (new SlotResult(playerinventory.player, this.craftInventory, this.resultInventory, 0, 124, 35)));

        int i;
        int j;

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.a(new Slot(this.craftInventory, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.a(new Slot(playerinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.a(new Slot(playerinventory, i, 8 + i * 18, 142));
        }

        this.a((IInventory) this.craftInventory);
    }

    public void a(IInventory iinventory) {
        // this.resultInventory.setItem(0, CraftingManager.getInstance().craft(this.craftInventory, this.g));
        // CraftBukkit start
        CraftingManager.getInstance().lastCraftView = getBukkitView();
        ItemStack craftResult = CraftingManager.getInstance().craft(this.craftInventory, this.g);
        this.resultInventory.setItem(0, craftResult);
        if (super.listeners.size() < 1) {
            return;
        }
        // See CraftBukkit PR #39
        if (craftResult != null && craftResult.getItem() == Items.FILLED_MAP) {
            return;
        }
        EntityPlayer player = (EntityPlayer) super.listeners.get(0); // TODO: Is this _always_ correct? Seems like it.
        player.playerConnection.sendPacket(new PacketPlayOutSetSlot(player.activeContainer.windowId, 0, craftResult));
        // CraftBukkit end
    }

    public void b(EntityHuman entityhuman) {
        super.b(entityhuman);
        if (!this.g.isClientSide) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemstack = this.craftInventory.splitWithoutUpdate(i);

                if (itemstack != null) {
                    entityhuman.drop(itemstack, false);
                }
            }

        }
    }

    public boolean a(EntityHuman entityhuman) {
        if (!this.checkReachable) return true; // CraftBukkit
        return this.g.getType(this.h).getBlock() != Blocks.CRAFTING_TABLE ? false : entityhuman.e((double) this.h.getX() + 0.5D, (double) this.h.getY() + 0.5D, (double) this.h.getZ() + 0.5D) <= 64.0D;
    }

    public ItemStack b(EntityHuman entityhuman, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.c.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();

            itemstack = itemstack1.cloneItemStack();
            if (i == 0) {
                if (!this.a(itemstack1, 10, 46, true)) {
                    return null;
                }

                slot.a(itemstack1, itemstack);
            } else if (i >= 10 && i < 37) {
                if (!this.a(itemstack1, 37, 46, false)) {
                    return null;
                }
            } else if (i >= 37 && i < 46) {
                if (!this.a(itemstack1, 10, 37, false)) {
                    return null;
                }
            } else if (!this.a(itemstack1, 10, 46, false)) {
                return null;
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

    public boolean a(ItemStack itemstack, Slot slot) {
        return slot.inventory != this.resultInventory && super.a(itemstack, slot);
    }

    // CraftBukkit start
    @Override
    public CraftInventoryView getBukkitView() {
        if (bukkitEntity != null) {
            return bukkitEntity;
        }

        CraftInventoryCrafting inventory = new CraftInventoryCrafting(this.craftInventory, this.resultInventory);
        bukkitEntity = new CraftInventoryView(this.player.player.getBukkitEntity(), inventory, this);
        return bukkitEntity;
    }
    // CraftBukkit end
}
