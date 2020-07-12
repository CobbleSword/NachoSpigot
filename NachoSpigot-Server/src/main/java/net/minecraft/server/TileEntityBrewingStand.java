package net.minecraft.server;

import java.util.Arrays;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.BrewEvent;
// CraftBukkit end

public class TileEntityBrewingStand extends TileEntityContainer implements IUpdatePlayerListBox, IWorldInventory {

    private static final int[] a = new int[] { 3};
    private static final int[] f = new int[] { 0, 1, 2};
    private ItemStack[] items = new ItemStack[4];
    public int brewTime;
    private boolean[] i;
    private Item j;
    private String k;
    private int lastTick = MinecraftServer.currentTick; // CraftBukkit - add field

    public TileEntityBrewingStand() {}

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = 64;

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public ItemStack[] getContents() {
        return this.items;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    public String getName() {
        return this.hasCustomName() ? this.k : "container.brewing";
    }

    public boolean hasCustomName() {
        return this.k != null && this.k.length() > 0;
    }

    public void a(String s) {
        this.k = s;
    }

    public int getSize() {
        return this.items.length;
    }

    public void c() {
        // CraftBukkit start - Use wall time instead of ticks for brewing
        int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.lastTick = MinecraftServer.currentTick;

        if (this.brewTime > 0) {
            this.brewTime -= elapsedTicks;
            if (this.brewTime <= 0) { // == -> <=
                // CraftBukkit end
                this.o();
                this.update();
            } else if (!this.n()) {
                this.brewTime = 0;
                this.update();
            } else if (this.j != this.items[3].getItem()) {
                this.brewTime = 0;
                this.update();
            }
        } else if (this.n()) {
            this.brewTime = 400;
            this.j = this.items[3].getItem();
        }

        if (!this.world.isClientSide) {
            boolean[] aboolean = this.m();

            if (!Arrays.equals(aboolean, this.i)) {
                this.i = aboolean;
                IBlockData iblockdata = this.world.getType(this.getPosition());

                if (!(iblockdata.getBlock() instanceof BlockBrewingStand)) {
                    return;
                }

                for (int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; ++i) {
                    iblockdata = iblockdata.set(BlockBrewingStand.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
                }

                this.world.setTypeAndData(this.position, iblockdata, 2);
            }
        }

    }

    private boolean n() {
        if (this.items[3] != null && this.items[3].count > 0) {
            ItemStack itemstack = this.items[3];

            if (!itemstack.getItem().l(itemstack)) {
                return false;
            } else {
                boolean flag = false;

                for (int i = 0; i < 3; ++i) {
                    if (this.items[i] != null && this.items[i].getItem() == Items.POTION) {
                        int j = this.items[i].getData();
                        int k = this.c(j, itemstack);

                        if (!ItemPotion.f(j) && ItemPotion.f(k)) {
                            flag = true;
                            break;
                        }

                        List list = Items.POTION.e(j);
                        List list1 = Items.POTION.e(k);

                        if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null) && j != k) {
                            flag = true;
                            break;
                        }
                    }
                }

                return flag;
            }
        } else {
            return false;
        }
    }

    private void o() {
        if (this.n()) {
            ItemStack itemstack = this.items[3];

            // CraftBukkit start
            if (getOwner() != null) {
                BrewEvent event = new BrewEvent(world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), (org.bukkit.inventory.BrewerInventory) this.getOwner().getInventory());
                org.bukkit.Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
            }
            // CraftBukkit end

            for (int i = 0; i < 3; ++i) {
                if (this.items[i] != null && this.items[i].getItem() == Items.POTION) {
                    int j = this.items[i].getData();
                    int k = this.c(j, itemstack);
                    List list = Items.POTION.e(j);
                    List list1 = Items.POTION.e(k);

                    if ((j <= 0 || list != list1) && (list == null || !list.equals(list1) && list1 != null)) {
                        if (j != k) {
                            this.items[i].setData(k);
                        }
                    } else if (!ItemPotion.f(j) && ItemPotion.f(k)) {
                        this.items[i].setData(k);
                    }
                }
            }

            if (itemstack.getItem().r()) {
                this.items[3] = new ItemStack(itemstack.getItem().q());
            } else {
                --this.items[3].count;
                if (this.items[3].count <= 0) {
                    this.items[3] = null;
                }
            }

        }
    }

    private int c(int i, ItemStack itemstack) {
        return itemstack == null ? i : (itemstack.getItem().l(itemstack) ? PotionBrewer.a(i, itemstack.getItem().j(itemstack)) : i);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

        this.items = new ItemStack[this.getSize()];

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.items.length) {
                this.items[b0] = ItemStack.createStack(nbttagcompound1);
            }
        }

        this.brewTime = nbttagcompound.getShort("BrewTime");
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.k = nbttagcompound.getString("CustomName");
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setShort("BrewTime", (short) this.brewTime);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setByte("Slot", (byte) i);
                this.items[i].save(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }
        }

        nbttagcompound.set("Items", nbttaglist);
        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.k);
        }

    }

    public ItemStack getItem(int i) {
        return i >= 0 && i < this.items.length ? this.items[i] : null;
    }

    public ItemStack splitStack(int i, int j) {
        if (i >= 0 && i < this.items.length) {
            ItemStack itemstack = this.items[i];

            this.items[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (i >= 0 && i < this.items.length) {
            ItemStack itemstack = this.items[i];

            this.items[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i >= 0 && i < this.items.length) {
            this.items[i] = itemstack;
        }

    }

    public int getMaxStackSize() {
        return this.maxStack; // CraftBukkit
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.e((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return i == 3 ? itemstack.getItem().l(itemstack) : itemstack.getItem() == Items.POTION || itemstack.getItem() == Items.GLASS_BOTTLE;
    }

    public boolean[] m() {
        boolean[] aboolean = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (this.items[i] != null) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? TileEntityBrewingStand.a : TileEntityBrewingStand.f;
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return this.b(i, itemstack);
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return true;
    }

    public String getContainerName() {
        return "minecraft:brewing_stand";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerBrewingStand(playerinventory, this);
    }

    public int getProperty(int i) {
        switch (i) {
        case 0:
            return this.brewTime;

        default:
            return 0;
        }
    }

    public void b(int i, int j) {
        switch (i) {
        case 0:
            this.brewTime = j;

        default:
        }
    }

    public int g() {
        return 1;
    }

    public void l() {
        for (int i = 0; i < this.items.length; ++i) {
            this.items[i] = null;
        }

    }
}
