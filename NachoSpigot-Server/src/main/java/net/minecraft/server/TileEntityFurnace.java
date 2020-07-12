package net.minecraft.server;

// CraftBukkit start
import java.util.List;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
// CraftBukkit end

public class TileEntityFurnace extends TileEntityContainer implements IUpdatePlayerListBox, IWorldInventory {

    private static final int[] a = new int[] { 0};
    private static final int[] f = new int[] { 2, 1};
    private static final int[] g = new int[] { 1};
    private ItemStack[] items = new ItemStack[3];
    public int burnTime;
    private int ticksForCurrentFuel;
    public int cookTime;
    private int cookTimeTotal;
    private String m;

    // CraftBukkit start - add fields and methods
    private int lastTick = MinecraftServer.currentTick;
    private int maxStack = MAX_STACK;
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

    public ItemStack[] getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    public TileEntityFurnace() {}

    public int getSize() {
        return this.items.length;
    }

    public ItemStack getItem(int i) {
        return this.items[i];
    }

    public ItemStack splitStack(int i, int j) {
        if (this.items[i] != null) {
            ItemStack itemstack;

            if (this.items[i].count <= j) {
                itemstack = this.items[i];
                this.items[i] = null;
                return itemstack;
            } else {
                itemstack = this.items[i].cloneAndSubtract(j);
                if (this.items[i].count == 0) {
                    this.items[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (this.items[i] != null) {
            ItemStack itemstack = this.items[i];

            this.items[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        boolean flag = itemstack != null && itemstack.doMaterialsMatch(this.items[i]) && ItemStack.equals(itemstack, this.items[i]);

        this.items[i] = itemstack;
        if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
        }

        if (i == 0 && !flag) {
            this.cookTimeTotal = this.a(itemstack);
            this.cookTime = 0;
            this.update();
        }

    }

    public String getName() {
        return this.hasCustomName() ? this.m : "container.furnace";
    }

    public boolean hasCustomName() {
        return this.m != null && this.m.length() > 0;
    }

    public void a(String s) {
        this.m = s;
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

        this.burnTime = nbttagcompound.getShort("BurnTime");
        this.cookTime = nbttagcompound.getShort("CookTime");
        this.cookTimeTotal = nbttagcompound.getShort("CookTimeTotal");
        this.ticksForCurrentFuel = fuelTime(this.items[1]);
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.m = nbttagcompound.getString("CustomName");
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setShort("BurnTime", (short) this.burnTime);
        nbttagcompound.setShort("CookTime", (short) this.cookTime);
        nbttagcompound.setShort("CookTimeTotal", (short) this.cookTimeTotal);
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
            nbttagcompound.setString("CustomName", this.m);
        }

    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    public void c() {
        boolean flag = (this.w() == Blocks.LIT_FURNACE); // CraftBukkit - SPIGOT-844 - Check if furnace block is lit using the block instead of burn time // PAIL: Rename
        boolean flag1 = false;

        // CraftBukkit start - Use wall time instead of ticks for cooking
        int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.lastTick = MinecraftServer.currentTick;

        // CraftBukkit - moved from below
        if (this.isBurning() && this.canBurn()) {
            this.cookTime += elapsedTicks;
            if (this.cookTime >= this.cookTimeTotal) {
                this.cookTime = 0;
                this.cookTimeTotal = this.a(this.items[0]);
                this.burn();
                flag1 = true;
            }
        } else {
            this.cookTime = 0;
        }
        // CraftBukkit end

        if (this.isBurning()) {
            this.burnTime -= elapsedTicks; // CraftBukkit - use elapsedTicks in place of constant
        }

        if (!this.world.isClientSide) {
            if (!this.isBurning() && (this.items[1] == null || this.items[0] == null)) {
                if (!this.isBurning() && this.cookTime > 0) {
                    this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
                }
            } else {
                // CraftBukkit start - Handle multiple elapsed ticks
                if (this.burnTime <= 0 && this.canBurn()) { // CraftBukkit - == to <=
                    CraftItemStack fuel = CraftItemStack.asCraftMirror(this.items[1]);

                    FurnaceBurnEvent furnaceBurnEvent = new FurnaceBurnEvent(this.world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), fuel, fuelTime(this.items[1]));
                    this.world.getServer().getPluginManager().callEvent(furnaceBurnEvent);

                    if (furnaceBurnEvent.isCancelled()) {
                        return;
                    }

                    this.ticksForCurrentFuel = furnaceBurnEvent.getBurnTime();
                    this.burnTime += this.ticksForCurrentFuel;
                    if (this.burnTime > 0 && furnaceBurnEvent.isBurning()) {
                        // CraftBukkit end
                        flag1 = true;
                        if (this.items[1] != null) {
                            --this.items[1].count;
                            if (this.items[1].count == 0) {
                                Item item = this.items[1].getItem().q();

                                this.items[1] = item != null ? new ItemStack(item) : null;
                            }
                        }
                    }
                }

                /* CraftBukkit start - Moved up
                if (this.isBurning() && this.canBurn()) {
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.a(this.items[0]);
                        this.burn();
                        flag1 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
                */
            }

            if (flag != this.isBurning()) {
                flag1 = true;
                BlockFurnace.a(this.isBurning(), this.world, this.position);
                this.E(); // CraftBukkit - Invalidate tile entity's cached block type // PAIL: Rename
            }
        }

        if (flag1) {
            this.update();
        }

    }

    public int a(ItemStack itemstack) {
        return 200;
    }

    private boolean canBurn() {
        if (this.items[0] == null) {
            return false;
        } else {
            ItemStack itemstack = RecipesFurnace.getInstance().getResult(this.items[0]);

            // CraftBukkit - consider resultant count instead of current count
            return itemstack == null ? false : (this.items[2] == null ? true : (!this.items[2].doMaterialsMatch(itemstack) ? false : (this.items[2].count + itemstack.count <= this.getMaxStackSize() && this.items[2].count < this.items[2].getMaxStackSize() ? true : this.items[2].count + itemstack.count <= itemstack.getMaxStackSize())));
        }
    }

    public void burn() {
        if (this.canBurn()) {
            ItemStack itemstack = RecipesFurnace.getInstance().getResult(this.items[0]);

            // CraftBukkit start - fire FurnaceSmeltEvent
            CraftItemStack source = CraftItemStack.asCraftMirror(this.items[0]);
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(itemstack);

            FurnaceSmeltEvent furnaceSmeltEvent = new FurnaceSmeltEvent(this.world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), source, result);
            this.world.getServer().getPluginManager().callEvent(furnaceSmeltEvent);

            if (furnaceSmeltEvent.isCancelled()) {
                return;
            }

            result = furnaceSmeltEvent.getResult();
            itemstack = CraftItemStack.asNMSCopy(result);

            if (itemstack != null) {
                if (this.items[2] == null) {
                    this.items[2] = itemstack;
                } else if (CraftItemStack.asCraftMirror(this.items[2]).isSimilar(result)) {
                    this.items[2].count += itemstack.count;
                } else {
                    return;
                }
            }

            /*
            if (this.items[2] == null) {
                this.items[2] = itemstack.cloneItemStack();
            } else if (this.items[2].getItem() == itemstack.getItem()) {
                ++this.items[2].count;
            }
            */
            // CraftBukkit end

            if (this.items[0].getItem() == Item.getItemOf(Blocks.SPONGE) && this.items[0].getData() == 1 && this.items[1] != null && this.items[1].getItem() == Items.BUCKET) {
                this.items[1] = new ItemStack(Items.WATER_BUCKET);
            }

            --this.items[0].count;
            if (this.items[0].count <= 0) {
                this.items[0] = null;
            }

        }
    }

    public static int fuelTime(ItemStack itemstack) {
        if (itemstack == null) {
            return 0;
        } else {
            Item item = itemstack.getItem();

            if (item instanceof ItemBlock && Block.asBlock(item) != Blocks.AIR) {
                Block block = Block.asBlock(item);

                if (block == Blocks.WOODEN_SLAB) {
                    return 150;
                }

                if (block.getMaterial() == Material.WOOD) {
                    return 300;
                }

                if (block == Blocks.COAL_BLOCK) {
                    return 16000;
                }
            }

            return item instanceof ItemTool && ((ItemTool) item).h().equals("WOOD") ? 200 : (item instanceof ItemSword && ((ItemSword) item).h().equals("WOOD") ? 200 : (item instanceof ItemHoe && ((ItemHoe) item).g().equals("WOOD") ? 200 : (item == Items.STICK ? 100 : (item == Items.COAL ? 1600 : (item == Items.LAVA_BUCKET ? 20000 : (item == Item.getItemOf(Blocks.SAPLING) ? 100 : (item == Items.BLAZE_ROD ? 2400 : 0)))))));
        }
    }

    public static boolean isFuel(ItemStack itemstack) {
        return fuelTime(itemstack) > 0;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.e((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return i == 2 ? false : (i != 1 ? true : isFuel(itemstack) || SlotFurnaceFuel.c_(itemstack));
    }

    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? TileEntityFurnace.f : (enumdirection == EnumDirection.UP ? TileEntityFurnace.a : TileEntityFurnace.g);
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return this.b(i, itemstack);
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        if (enumdirection == EnumDirection.DOWN && i == 1) {
            Item item = itemstack.getItem();

            if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
                return false;
            }
        }

        return true;
    }

    public String getContainerName() {
        return "minecraft:furnace";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerFurnace(playerinventory, this);
    }

    public int getProperty(int i) {
        switch (i) {
        case 0:
            return this.burnTime;

        case 1:
            return this.ticksForCurrentFuel;

        case 2:
            return this.cookTime;

        case 3:
            return this.cookTimeTotal;

        default:
            return 0;
        }
    }

    public void b(int i, int j) {
        switch (i) {
        case 0:
            this.burnTime = j;
            break;

        case 1:
            this.ticksForCurrentFuel = j;
            break;

        case 2:
            this.cookTime = j;
            break;

        case 3:
            this.cookTimeTotal = j;
        }

    }

    public int g() {
        return 4;
    }

    public void l() {
        for (int i = 0; i < this.items.length; ++i) {
            this.items[i] = null;
        }

    }
}
