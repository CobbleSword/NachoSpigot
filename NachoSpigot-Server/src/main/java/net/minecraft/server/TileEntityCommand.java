package net.minecraft.server;

public class TileEntityCommand extends TileEntity {

    private final CommandBlockListenerAbstract a = new CommandBlockListenerAbstract() {
        {
            sender = new org.bukkit.craftbukkit.command.CraftBlockCommandSender(this); // CraftBukkit - add sender
        }
        public BlockPosition getChunkCoordinates() {
            return TileEntityCommand.this.position;
        }

        public Vec3D d() {
            return new Vec3D((double) TileEntityCommand.this.position.getX() + 0.5D, (double) TileEntityCommand.this.position.getY() + 0.5D, (double) TileEntityCommand.this.position.getZ() + 0.5D);
        }

        public World getWorld() {
            return TileEntityCommand.this.getWorld();
        }

        public void setCommand(String s) {
            super.setCommand(s);
            TileEntityCommand.this.update();
        }

        public void h() {
            TileEntityCommand.this.getWorld().notify(TileEntityCommand.this.position);
        }

        public Entity f() {
            return null;
        }
    };

    public TileEntityCommand() {}

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        this.a.a(nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.a.b(nbttagcompound);
    }

    public Packet getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.b(nbttagcompound);
        return new PacketPlayOutTileEntityData(this.position, 2, nbttagcompound);
    }

    public boolean F() {
        return true;
    }

    public CommandBlockListenerAbstract getCommandBlock() {
        return this.a;
    }

    public CommandObjectiveExecutor c() {
        return this.a.n();
    }
}
