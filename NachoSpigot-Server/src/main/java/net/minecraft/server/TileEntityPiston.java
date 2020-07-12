package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class TileEntityPiston extends TileEntity implements IUpdatePlayerListBox {

    private IBlockData a;
    private EnumDirection f;
    private boolean g;
    private boolean h;
    private float i;
    private float j;
    private List<Entity> k = Lists.newArrayList();

    public TileEntityPiston() {}

    public TileEntityPiston(IBlockData iblockdata, EnumDirection enumdirection, boolean flag, boolean flag1) {
        this.a = iblockdata;
        this.f = enumdirection;
        this.g = flag;
        this.h = flag1;
    }

    public IBlockData b() {
        return this.a;
    }

    public int u() {
        return 0;
    }

    public boolean d() {
        return this.g;
    }

    public EnumDirection e() {
        return this.f;
    }

    public float a(float f) {
        if (f > 1.0F) {
            f = 1.0F;
        }

        return this.j + (this.i - this.j) * f;
    }

    private void a(float f, float f1) {
        if (this.g) {
            f = 1.0F - f;
        } else {
            --f;
        }

        AxisAlignedBB axisalignedbb = Blocks.PISTON_EXTENSION.a(this.world, this.position, this.a, f, this.f);

        if (axisalignedbb != null) {
            List list = this.world.getEntities((Entity) null, axisalignedbb);

            if (!list.isEmpty()) {
                this.k.addAll(list);
                Iterator iterator = this.k.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    if (this.a.getBlock() == Blocks.SLIME && this.g) {
                        switch (TileEntityPiston.SyntheticClass_1.a[this.f.k().ordinal()]) {
                        case 1:
                            entity.motX = (double) this.f.getAdjacentX();
                            break;

                        case 2:
                            entity.motY = (double) this.f.getAdjacentY();
                            break;

                        case 3:
                            entity.motZ = (double) this.f.getAdjacentZ();
                        }
                    } else {
                        entity.move((double) (f1 * (float) this.f.getAdjacentX()), (double) (f1 * (float) this.f.getAdjacentY()), (double) (f1 * (float) this.f.getAdjacentZ()));
                    }
                }

                this.k.clear();
            }
        }

    }

    public void h() {
        if (this.j < 1.0F && this.world != null) {
            this.j = this.i = 1.0F;
            this.world.t(this.position);
            this.y();
            if (this.world.getType(this.position).getBlock() == Blocks.PISTON_EXTENSION) {
                this.world.setTypeAndData(this.position, this.a, 3);
                this.world.d(this.position, this.a.getBlock());
            }
        }

    }

    public void c() {
        if (this.world == null) return; // CraftBukkit
        this.j = this.i;
        if (this.j >= 1.0F) {
            this.a(1.0F, 0.25F);
            this.world.t(this.position);
            this.y();
            if (this.world.getType(this.position).getBlock() == Blocks.PISTON_EXTENSION) {
                this.world.setTypeAndData(this.position, this.a, 3);
                this.world.d(this.position, this.a.getBlock());
            }

        } else {
            this.i += 0.5F;
            if (this.i >= 1.0F) {
                this.i = 1.0F;
            }

            if (this.g) {
                this.a(this.i, this.i - this.j + 0.0625F);
            }

        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.a = Block.getById(nbttagcompound.getInt("blockId")).fromLegacyData(nbttagcompound.getInt("blockData"));
        this.f = EnumDirection.fromType1(nbttagcompound.getInt("facing"));
        this.j = this.i = nbttagcompound.getFloat("progress");
        this.g = nbttagcompound.getBoolean("extending");
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("blockId", Block.getId(this.a.getBlock()));
        nbttagcompound.setInt("blockData", this.a.getBlock().toLegacyData(this.a));
        nbttagcompound.setInt("facing", this.f.a());
        nbttagcompound.setFloat("progress", this.j);
        nbttagcompound.setBoolean("extending", this.g);
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.EnumAxis.values().length];

        static {
            try {
                TileEntityPiston.SyntheticClass_1.a[EnumDirection.EnumAxis.X.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                TileEntityPiston.SyntheticClass_1.a[EnumDirection.EnumAxis.Y.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                TileEntityPiston.SyntheticClass_1.a[EnumDirection.EnumAxis.Z.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

        }
    }
}
