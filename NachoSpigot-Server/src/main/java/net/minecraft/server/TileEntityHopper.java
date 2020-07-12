package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
// CraftBukkit end
import net.techcable.tacospigot.HopperHelper; // TacoSpigot

public class TileEntityHopper extends TileEntityContainer implements IHopper, IUpdatePlayerListBox {

    private ItemStack[] items = new ItemStack[5];
    private String f;
    private int g = -1;

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

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

    public TileEntityHopper() {}

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

        this.items = new ItemStack[this.getSize()];
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.f = nbttagcompound.getString("CustomName");
        }

        this.g = nbttagcompound.getInt("TransferCooldown");

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.items.length) {
                this.items[b0] = ItemStack.createStack(nbttagcompound1);
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
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
        nbttagcompound.setInt("TransferCooldown", this.g);
        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.f);
        }

    }

    public void update() {
        super.update();
    }

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
        this.items[i] = itemstack;
        if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
        }

    }

    public String getName() {
        return this.hasCustomName() ? this.f : "container.hopper";
    }

    public boolean hasCustomName() {
        return this.f != null && this.f.length() > 0;
    }

    public void a(String s) {
        this.f = s;
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.e((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public void c() {
        if (this.world != null && !this.world.isClientSide) {
            --this.g;
            if (!this.n()) {
                this.d(0);
                this.m();
            }

        }
    }

    public boolean m() {
        if (this.world != null && !this.world.isClientSide) {
            if (!this.n() && BlockHopper.f(this.u())) {
                boolean flag = false;

                if (!this.p()) {
                    flag = this.r();
                }

                if (!this.q()) {
                    flag = a((IHopper) this) || flag;
                }

                if (flag) {
                    this.d(world.spigotConfig.hopperTransfer); // Spigot
                    this.update();
                    return true;
                }
            }
            // PaperSpigot start
            if (world.paperSpigotConfig.useHopperCheck && !world.tacoSpigotConfig.isHopperPushBased && !this.n()) { // TacoSpigot - dont use hopper check in push mode
                this.d(world.spigotConfig.hopperCheck);
            }
            // PaperSpigot end
            return false;
        } else {
            return false;
        }
    }

    private boolean p() {
        ItemStack[] aitemstack = this.items;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            if (itemstack != null) {
                return false;
            }
        }

        return true;
    }

    // TacoSpigot start
    public boolean canAcceptItems() {
        return !this.n() && !this.q() && BlockHopper.f(this.u());
    }
    // TacoSpigot end

    private boolean q() {
        ItemStack[] aitemstack = this.items;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            if (itemstack == null || itemstack.count != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private boolean r() {
        // TacoSpigot start - Don't use inefficient H() which does another bounding box search
        IInventory iinventory = HopperHelper.getInventory(getWorld(), getPosition().shift(BlockHopper.b(this.u())));
        // TacoSpigot end

        if (iinventory == null) {
            return false;
        } else {
            EnumDirection enumdirection = BlockHopper.b(this.u()).opposite();

            if (this.a(iinventory, enumdirection)) {
                return false;
            } else {
                for (int i = 0; i < this.getSize(); ++i) {
                    if (this.getItem(i) != null) {
                        ItemStack itemstack = this.getItem(i).cloneItemStack();
                        // ItemStack itemstack1 = addItem(iinventory, this.splitStack(i, 1), enumdirection);

                        // CraftBukkit start - Call event when pushing items into other inventories
                        CraftItemStack oitemstack = CraftItemStack.asCraftMirror(this.splitStack(i, world.spigotConfig.hopperAmount)); // Spigot
                        // TacoSpigot start - add an option to turn of InventoryMoveItemEvent
                        final org.bukkit.inventory.ItemStack stack;
                        if (HopperHelper.isFireInventoryMoveItemEvent(this)) {
                        // TacoSpigot end

                        Inventory destinationInventory;
                        // Have to special case large chests as they work oddly
                        if (iinventory instanceof InventoryLargeChest) {
                            destinationInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((InventoryLargeChest) iinventory);
                        } else {
                            destinationInventory = iinventory.getOwner().getInventory();
                        }

                        InventoryMoveItemEvent event = new InventoryMoveItemEvent(this.getOwner().getInventory(), oitemstack.clone(), destinationInventory, true);
                        this.getWorld().getServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            this.setItem(i, itemstack);
                            this.d(world.spigotConfig.hopperTransfer); // Spigot
                            return false;
                        }
                        // TacoSpigot start
                        stack = event.getItem();
                        // handle cases where the event is not fired
                        } else {
                            stack = oitemstack;
                        }
                        int origCount = stack.getAmount(); // Spigot
                        ItemStack itemstack1 = addItem(iinventory, CraftItemStack.asNMSCopy(stack), enumdirection);
                        // TacoSpigot end

                        if (itemstack1 == null || itemstack1.count == 0) {
                            if (stack.equals(oitemstack)) { // TacoSpigot - event.getItem() -> stack
                                iinventory.update();
                            } else {
                                this.setItem(i, itemstack);
                            }
                            // CraftBukkit end
                            return true;
                        }
                        itemstack.count -= origCount - itemstack1.count; // Spigot
                        this.setItem(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private boolean a(IInventory iinventory, EnumDirection enumdirection) {
        if (iinventory instanceof IWorldInventory) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);

            for (int i = 0; i < aint.length; ++i) {
                ItemStack itemstack = iworldinventory.getItem(aint[i]);

                if (itemstack == null || itemstack.count != itemstack.getMaxStackSize()) {
                    return false;
                }
            }
        } else {
            int j = iinventory.getSize();

            for (int k = 0; k < j; ++k) {
                ItemStack itemstack1 = iinventory.getItem(k);

                if (itemstack1 == null || itemstack1.count != itemstack1.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean b(IInventory iinventory, EnumDirection enumdirection) {
        if (iinventory instanceof IWorldInventory) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);

            for (int i = 0; i < aint.length; ++i) {
                if (iworldinventory.getItem(aint[i]) != null) {
                    return false;
                }
            }
        } else {
            int j = iinventory.getSize();

            for (int k = 0; k < j; ++k) {
                if (iinventory.getItem(k) != null) {
                    return false;
                }
            }
        }

        return true;
    }

    // TacoSpigot start - Split methods, one that pushes and one that pulls
    @Deprecated
    public static boolean a(IHopper ihopper) {
        IInventory iinventory;
        if (ihopper.getWorld().tacoSpigotConfig.isHopperPushBased && ihopper instanceof TileEntityHopper) {
            BlockPosition pos = ((TileEntityHopper) ihopper).getPosition().up(); // Only pull from a above, because everything else comes to us
            iinventory = HopperHelper.getInventory(ihopper.getWorld(), pos);
        } else {
            iinventory = b(ihopper); // Use old behavior for BB entity searching
        }
        return acceptItem(ihopper, iinventory);
    }

    public static boolean acceptItem(IHopper ihopper, IInventory iinventory) {
        // TacoSpigot end
        if (iinventory != null) {
            EnumDirection enumdirection = EnumDirection.DOWN;

            if (b(iinventory, enumdirection)) {
                return false;
            }

            if (iinventory instanceof IWorldInventory) {
                IWorldInventory iworldinventory = (IWorldInventory) iinventory;
                int[] aint = iworldinventory.getSlotsForFace(enumdirection);

                for (int i = 0; i < aint.length; ++i) {
                    if (a(ihopper, iinventory, aint[i], enumdirection)) {
                        return true;
                    }
                }
            } else {
                int j = iinventory.getSize();

                for (int k = 0; k < j; ++k) {
                    if (a(ihopper, iinventory, k, enumdirection)) {
                        return true;
                    }
                }
            }
        } else if (!ihopper.getWorld().tacoSpigotConfig.isHopperPushBased || !(ihopper instanceof TileEntityHopper)) { // TacoSpigot - only search for entities in 'pull mode'
            Iterator iterator = a(ihopper.getWorld(), ihopper.A(), ihopper.B() + 1.0D, ihopper.C()).iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (a((IInventory) ihopper, entityitem)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean a(IHopper ihopper, IInventory iinventory, int i, EnumDirection enumdirection) {
        ItemStack itemstack = iinventory.getItem(i);

        if (itemstack != null && b(iinventory, itemstack, i, enumdirection)) {
            ItemStack itemstack1 = itemstack.cloneItemStack();
            // ItemStack itemstack2 = addItem(ihopper, iinventory.splitStack(i, 1), (EnumDirection) null);
            // CraftBukkit start - Call event on collection of items from inventories into the hopper
            CraftItemStack oitemstack = CraftItemStack.asCraftMirror(iinventory.splitStack(i, ihopper.getWorld().spigotConfig.hopperAmount)); // Spigot
            // TacoSpigot start - add an option to turn of InventoryMoveItemEvent
            final org.bukkit.inventory.ItemStack stack;
            if (HopperHelper.isFireInventoryMoveItemEvent(ihopper)) {
            // TacoSpigot end

            Inventory sourceInventory;
            // Have to special case large chests as they work oddly
            if (iinventory instanceof InventoryLargeChest) {
                sourceInventory = new org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest((InventoryLargeChest) iinventory);
            } else {
                sourceInventory = iinventory.getOwner().getInventory();
            }

            InventoryMoveItemEvent event = new InventoryMoveItemEvent(sourceInventory, oitemstack.clone(), ihopper.getOwner().getInventory(), false);

            ihopper.getWorld().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                iinventory.setItem(i, itemstack1);

                if (ihopper instanceof TileEntityHopper) {
                    ((TileEntityHopper) ihopper).d(ihopper.getWorld().spigotConfig.hopperTransfer); // Spigot
                } else if (ihopper instanceof EntityMinecartHopper) {
                    ((EntityMinecartHopper) ihopper).m(ihopper.getWorld().spigotConfig.hopperTransfer / 2); // Spigot
                }
                return false;
            }
            // TacoSpigot start
            stack = event.getItem();
            // handle cases where the event is not fired
            } else {
                stack = oitemstack;
            }
            int origCount = stack.getAmount(); // Spigot
            ItemStack itemstack2 = addItem(ihopper, CraftItemStack.asNMSCopy(stack), null);
            // TacoSpigot end

            if (itemstack2 == null || itemstack2.count == 0) {
                if (stack.equals(oitemstack)) {
                    iinventory.update();
                } else {
                    iinventory.setItem(i, itemstack1);
                }
                // CraftBukkit end
                return true;
            }
            itemstack1.count -= origCount - itemstack2.count; // Spigot

            iinventory.setItem(i, itemstack1);
        }

        return false;
    }

    public static boolean a(IInventory iinventory, EntityItem entityitem) {
        boolean flag = false;

        if (entityitem == null) {
            return false;
        } else {
            // CraftBukkit start
            InventoryPickupItemEvent event = new InventoryPickupItemEvent(iinventory.getOwner().getInventory(), (org.bukkit.entity.Item) entityitem.getBukkitEntity());
            entityitem.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end
            ItemStack itemstack = entityitem.getItemStack().cloneItemStack();
            ItemStack itemstack1 = addItem(iinventory, itemstack, (EnumDirection) null);

            if (itemstack1 != null && itemstack1.count != 0) {
                entityitem.setItemStack(itemstack1);
            } else {
                flag = true;
                entityitem.die();
            }

            return flag;
        }
    }

    public static ItemStack addItem(IInventory iinventory, ItemStack itemstack, EnumDirection enumdirection) {
        if (iinventory instanceof IWorldInventory && enumdirection != null) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;
            int[] aint = iworldinventory.getSlotsForFace(enumdirection);

            for (int i = 0; i < aint.length && itemstack != null && itemstack.count > 0; ++i) {
                itemstack = c(iinventory, itemstack, aint[i], enumdirection);
            }
        } else {
            int j = iinventory.getSize();

            for (int k = 0; k < j && itemstack != null && itemstack.count > 0; ++k) {
                itemstack = c(iinventory, itemstack, k, enumdirection);
            }
        }

        if (itemstack != null && itemstack.count == 0) {
            itemstack = null;
        }

        return itemstack;
    }

    private static boolean a(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        return !iinventory.b(i, itemstack) ? false : !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canPlaceItemThroughFace(i, itemstack, enumdirection);
    }

    private static boolean b(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        return !(iinventory instanceof IWorldInventory) || ((IWorldInventory) iinventory).canTakeItemThroughFace(i, itemstack, enumdirection);
    }

    private static ItemStack c(IInventory iinventory, ItemStack itemstack, int i, EnumDirection enumdirection) {
        ItemStack itemstack1 = iinventory.getItem(i);

        if (a(iinventory, itemstack, i, enumdirection)) {
            boolean flag = false;

            if (itemstack1 == null) {
                iinventory.setItem(i, itemstack);
                itemstack = null;
                flag = true;
            } else if (a(itemstack1, itemstack)) {
                int j = itemstack.getMaxStackSize() - itemstack1.count;
                int k = Math.min(itemstack.count, j);

                itemstack.count -= k;
                itemstack1.count += k;
                flag = k > 0;
            }

            if (flag) {
                if (iinventory instanceof TileEntityHopper) {
                    TileEntityHopper tileentityhopper = (TileEntityHopper) iinventory;

                    if (tileentityhopper.o()) {
                        tileentityhopper.d(tileentityhopper.world.spigotConfig.hopperTransfer); // Spigot
                    }

                    iinventory.update();
                }

                iinventory.update();
            }
        }

        return itemstack;
    }

    private IInventory H() {
        EnumDirection enumdirection = BlockHopper.b(this.u());

        return b(this.getWorld(), (double) (this.position.getX() + enumdirection.getAdjacentX()), (double) (this.position.getY() + enumdirection.getAdjacentY()), (double) (this.position.getZ() + enumdirection.getAdjacentZ()));
    }

    public static IInventory b(IHopper ihopper) {
        return b(ihopper.getWorld(), ihopper.A(), ihopper.B() + 1.0D, ihopper.C());
    }

    public static List<EntityItem> a(World world, double d0, double d1, double d2) {
        return world.a(EntityItem.class, new AxisAlignedBB(d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, d0 + 0.5D, d1 + 0.5D, d2 + 0.5D), IEntitySelector.a);
    }

    public static IInventory b(World world, double d0, double d1, double d2) {
        Object object = null;
        int i = MathHelper.floor(d0);
        int j = MathHelper.floor(d1);
        int k = MathHelper.floor(d2);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        if ( !world.isLoaded( blockposition ) ) return null; // Spigot
        Block block = world.getType(blockposition).getBlock();

        if (block.isTileEntity()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof IInventory) {
                object = (IInventory) tileentity;
                if (object instanceof TileEntityChest && block instanceof BlockChest) {
                    object = ((BlockChest) block).f(world, blockposition);
                }
            }
        }

        if (object == null) {
            List list = world.a((Entity) null, new AxisAlignedBB(d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, d0 + 0.5D, d1 + 0.5D, d2 + 0.5D), IEntitySelector.c);

            if (list.size() > 0) {
                object = (IInventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return (IInventory) object;
    }

    // TacoSpigot start
    public AxisAlignedBB getHopperLookupBoundingBox() {
        // Change this if b(IHopper) ever changes
        return getHopperLookupBoundingBox(this.A(), this.B() + 1.0D, this.C());
    }

    private static AxisAlignedBB getHopperLookupBoundingBox(double d0, double d1, double d2) {
        // Change this if the above ever changes
        return new AxisAlignedBB(d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
    }
    // TacoSpigot end

    private static boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.getItem() != itemstack1.getItem() ? false : (itemstack.getData() != itemstack1.getData() ? false : (itemstack.count > itemstack.getMaxStackSize() ? false : ItemStack.equals(itemstack, itemstack1)));
    }

    public double A() {
        return (double) this.position.getX() + 0.5D;
    }

    public double B() {
        return (double) this.position.getY() + 0.5D;
    }

    public double C() {
        return (double) this.position.getZ() + 0.5D;
    }

    public void d(int i) {
        this.g = i;
    }

    public boolean n() {
        return this.g > 0;
    }

    public boolean o() {
        return this.g <= 1;
    }

    public String getContainerName() {
        return "minecraft:hopper";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerHopper(playerinventory, this, entityhuman);
    }

    public int getProperty(int i) {
        return 0;
    }

    public void b(int i, int j) {}

    public int g() {
        return 0;
    }

    public void l() {
        for (int i = 0; i < this.items.length; ++i) {
            this.items[i] = null;
        }

    }
}
