package net.minecraft.server;

public class TileEntityEnderChest extends TileEntity { // PaperSpigot - remove IUpdatePlayerListBox

    public float a; // PaperSpigot - lidAngle
    public float f;
    public int g; // PaperSpigot - numPlayersUsing
    private int h;

    public TileEntityEnderChest() {}

    public void c() {
        // PaperSpigot start - Move enderchest sound handling out of the tick loop
        /*
        if (++this.h % 20 * 4 == 0) {
            this.world.playBlockAction(this.position, Blocks.ENDER_CHEST, 1, this.g);
        }

        this.f = this.a;
        int i = this.position.getX();
        int j = this.position.getY();
        int k = this.position.getZ();
        float f = 0.1F;
        double d0;

        if (this.g > 0 && this.a == 0.0F) {
            double d1 = (double) i + 0.5D;

            d0 = (double) k + 0.5D;
            this.world.makeSound(d1, (double) j + 0.5D, d0, "random.chestopen", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }

        if (this.g == 0 && this.a > 0.0F || this.g > 0 && this.a < 1.0F) {
            float f1 = this.a;

            if (this.g > 0) {
                this.a += f;
            } else {
                this.a -= f;
            }

            if (this.a > 1.0F) {
                this.a = 1.0F;
            }

            float f2 = 0.5F;

            if (this.a < f2 && f1 >= f2) {
                d0 = (double) i + 0.5D;
                double d2 = (double) k + 0.5D;

                this.world.makeSound(d0, (double) j + 0.5D, d2, "random.chestclosed", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (this.a < 0.0F) {
                this.a = 0.0F;
            }
        }
        */
        // PaperSpigot end
    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.g = j;
            return true;
        } else {
            return super.c(i, j);
        }
    }

    public void y() {
        this.E();
        super.y();
    }

    public void b() {
        ++this.g;

        // PaperSpigot start - Move enderchest open sounds out of the tick loop
        if (this.g > 0 && this.a == 0.0F) {
            this.a = 0.7F;

            double d1 = (double) this.getPosition().getX() + 0.5D;
            double d0 = (double) this.getPosition().getZ() + 0.5D;

            this.world.makeSound(d1, (double) this.getPosition().getY() + 0.5D, d0, "random.chestopen", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }
        // PaperSpigot end

        this.world.playBlockAction(this.position, Blocks.ENDER_CHEST, 1, this.g);
    }

    public void d() {
        --this.g;

        // PaperSpigot start - Move enderchest close sounds out of the tick loop
        if (this.g == 0 && this.a > 0.0F || this.g > 0 && this.a < 1.0F) {
            double d0 = (double) this.getPosition().getX() + 0.5D;
            double d2 = (double) this.getPosition().getZ() + 0.5D;

            this.world.makeSound(d0, (double) this.getPosition().getY() + 0.5D, d2, "random.chestclosed", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            this.a = 0.0F;
        }
        // PaperSpigot end

        this.world.playBlockAction(this.position, Blocks.ENDER_CHEST, 1, this.g);
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.e((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }
}
