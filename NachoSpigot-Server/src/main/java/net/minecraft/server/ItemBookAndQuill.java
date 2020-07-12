package net.minecraft.server;

public class ItemBookAndQuill extends Item {

    public ItemBookAndQuill() {
        this.c(1);
    }

    public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        entityhuman.openBook(itemstack);
        entityhuman.b(StatisticList.USE_ITEM_COUNT[Item.getId(this)]);
        return itemstack;
    }

    public static boolean b(NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            return false;
        } else if (!nbttagcompound.hasKeyOfType("pages", 9)) {
            return false;
        } else {
            NBTTagList nbttaglist = nbttagcompound.getList("pages", 8);

            long start = System.currentTimeMillis();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                String s = nbttaglist.getString(i);

                if (s == null) {
                    return false;
                }

                if (s.length() > 32767) {
                    return false;
                }
            }

            return true;
        }
    }
}
