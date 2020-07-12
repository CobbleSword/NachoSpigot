package net.minecraft.server;

public class RecipeBookClone extends ShapelessRecipes implements IRecipe { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class
    public RecipeBookClone() {
        super(new ItemStack(Items.WRITTEN_BOOK, 0, -1), java.util.Arrays.asList(new ItemStack(Items.WRITABLE_BOOK, 0, 0)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (itemstack1 != null) {
                if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                    if (itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return itemstack != null && i > 0;
    }

    public ItemStack craftItem(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (itemstack1 != null) {
                if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                    if (itemstack != null) {
                        return null;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                        return null;
                    }

                    ++i;
                }
            }
        }

        if (itemstack != null && i >= 1 && ItemWrittenBook.h(itemstack) < 2) {
            ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);

            itemstack2.setTag((NBTTagCompound) itemstack.getTag().clone());
            itemstack2.getTag().setInt("generation", ItemWrittenBook.h(itemstack) + 1);
            if (itemstack.hasName()) {
                itemstack2.c(itemstack.getName());
            }

            return itemstack2;
        } else {
            return null;
        }
    }

    public int a() {
        return 9;
    }

    public ItemStack b() {
        return null;
    }

    public ItemStack[] b(InventoryCrafting inventorycrafting) {
        ItemStack[] aitemstack = new ItemStack[inventorycrafting.getSize()];

        for (int i = 0; i < aitemstack.length; ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (itemstack != null && itemstack.getItem() instanceof ItemWrittenBook) {
                aitemstack[i] = itemstack;
                break;
            }
        }

        return aitemstack;
    }
}
