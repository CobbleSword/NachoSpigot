package net.minecraft.server;

public class Slot {

    public final int index;
    public final IInventory inventory;
    public int rawSlotIndex;
    public int f;
    public int g;

    public Slot(IInventory iinventory, int i, int j, int k) {
        this.inventory = iinventory;
        this.index = i;
        this.f = j;
        this.g = k;
    }

    public void a(ItemStack itemstack, ItemStack itemstack1) {
        if (itemstack != null && itemstack1 != null) {
            if (itemstack.getItem() == itemstack1.getItem()) {
                int i = itemstack1.count - itemstack.count;

                if (i > 0) {
                    this.a(itemstack, i);
                }

            }
        }
    }

    protected void a(ItemStack itemstack, int i) {}

    protected void c(ItemStack itemstack) {}

    public void a(EntityHuman entityhuman, ItemStack itemstack) {
        this.f();
    }

    public boolean isAllowed(ItemStack itemstack) {
        return true;
    }

    public ItemStack getItem() {
        return this.inventory.getItem(this.index);
    }

    public boolean hasItem() {
        if (getItem() != null && getItem().count == 0) {
            set(null);
        }
        return this.getItem() != null;
    }

    public void set(ItemStack itemstack) {
        this.inventory.setItem(this.index, itemstack);
        this.f();
    }

    public void f() {
        this.inventory.update();
    }

    public int getMaxStackSize() {
        return this.inventory.getMaxStackSize();
    }

    public int getMaxStackSize(ItemStack itemstack) {
        return this.getMaxStackSize();
    }

    public ItemStack a(int i) {
        return this.inventory.splitStack(this.index, i);
    }

    public boolean a(IInventory iinventory, int i) {
        return iinventory == this.inventory && i == this.index;
    }

    public boolean isAllowed(EntityHuman entityhuman) {
        return true;
    }
}
