package net.minecraft.server;

public class TileEntityNote extends TileEntity {

    public byte note;
    public boolean f;

    public TileEntityNote() {}

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setByte("note", this.note);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.note = nbttagcompound.getByte("note");
        this.note = (byte) MathHelper.clamp(this.note, 0, 24);
    }

    public void b() {
        this.note = (byte) ((this.note + 1) % 25);
        this.update();
    }

    public void play(World world, BlockPosition blockposition) {
        if (world.getType(blockposition.up()).getBlock().getMaterial() == Material.AIR) {
            Material material = world.getType(blockposition.down()).getBlock().getMaterial();
            byte b0 = 0;

            if (material == Material.STONE) {
                b0 = 1;
            }

            if (material == Material.SAND) {
                b0 = 2;
            }

            if (material == Material.SHATTERABLE) {
                b0 = 3;
            }

            if (material == Material.WOOD) {
                b0 = 4;
            }

            // CraftBukkit start
            org.bukkit.event.block.NotePlayEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callNotePlayEvent(this.world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), b0, this.note);
            if (!event.isCancelled()) {
                world.playBlockAction(blockposition, Blocks.NOTEBLOCK, event.getInstrument().getType(), event.getNote().getId());
            }
            // CraftBukkit end
        }
    }
}
