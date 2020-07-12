package net.minecraft.server;

public class TileEntityLightDetector extends TileEntity implements IUpdatePlayerListBox {

    public TileEntityLightDetector() {}

    public void c() {
        if (this.world != null && !this.world.isClientSide && this.world.getTime() % 20L == 0L) {
            this.e = this.w();
            if (this.e instanceof BlockDaylightDetector) {
                ((BlockDaylightDetector) this.e).f(this.world, this.position);
            }
        }

    }
}
