package net.minecraft.server;

public abstract class TileEntityContainer extends TileEntity implements ITileEntityContainer, ITileInventory {
    private ChestLock a;

    public TileEntityContainer() {
        this.a = ChestLock.a;
    }

    public void a(NBTTagCompound var1) {
        super.a(var1);
        this.a = ChestLock.b(var1);
    }

    public void b(NBTTagCompound var1) {
        super.b(var1);
        if (this.a != null) {
            this.a.a(var1);
        }

    }

    public boolean r_() {
        return this.a != null && !this.a.a();
    }

    public ChestLock i() {
        return this.a;
    }

    public void a(ChestLock var1) {
        this.a = var1;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName());
    }

    public abstract void setCustomName(String name); // Nacho
}