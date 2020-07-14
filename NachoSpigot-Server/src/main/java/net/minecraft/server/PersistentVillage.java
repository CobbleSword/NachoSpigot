package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class PersistentVillage extends PersistentBase {
    private World world;
    private final List<BlockPosition> c = Lists.newArrayList();
    private final List<VillageDoor> d = Lists.newArrayList();
    private final List<Village> villages = Lists.newArrayList();
    private int time;

    public PersistentVillage(String var1) {
        super(var1);
    }

    public PersistentVillage(World var1) {
        super(a(var1.worldProvider));
        this.world = var1;
        this.c();
    }

    public void a(World var1) {
        this.world = var1;
        Iterator var2 = this.villages.iterator();

        while(var2.hasNext()) {
            Village var3 = (Village)var2.next();
            var3.a(var1);
        }

    }

    public void a(BlockPosition var1) {
        if (this.c.size() <= 64) {
            if (!this.e(var1)) {
                this.c.add(var1);
            }

        }
    }

    public void tick() {
        ++this.time;
        Iterator var1 = this.villages.iterator();

        while(var1.hasNext()) {
            Village var2 = (Village)var1.next();
            var2.a(this.time);
        }

        this.e();
        this.f();
        this.g();
        if (this.time % 400 == 0) {
            this.c();
        }

    }

    private void e() {
        Iterator var1 = this.villages.iterator();

        while(var1.hasNext()) {
            Village var2 = (Village)var1.next();
            if (var2.g()) {
                var1.remove();
                this.c();
            }
        }

    }

    public List<Village> getVillages() {
        return this.villages;
    }

    public Village getClosestVillage(BlockPosition var1, int var2) {
        Village var3 = null;
        double var4 = 3.4028234663852886E38D;
        Iterator var6 = this.villages.iterator();

        while(var6.hasNext()) {
            Village var7 = (Village)var6.next();
            double var8 = var7.a().i(var1);
            if (var8 < var4) {
                float var10 = (float)(var2 + var7.b());
                if (var8 <= (double)(var10 * var10)) {
                    var3 = var7;
                    var4 = var8;
                }
            }
        }

        return var3;
    }

    private void f() {
        if (!this.c.isEmpty()) {
            this.b((BlockPosition)this.c.remove(0));
        }
    }

    private void g() {
        for(int var1 = 0; var1 < this.d.size(); ++var1) {
            VillageDoor var2 = (VillageDoor)this.d.get(var1);
            Village var3 = this.getClosestVillage(var2.d(), 32);
            if (var3 == null) {
                var3 = new Village(this.world);
                this.villages.add(var3);
                this.c();
            }

            var3.a(var2);
        }

        this.d.clear();
    }

    private void b(BlockPosition var1) {
        byte var2 = 16;
        byte var3 = 4;
        byte var4 = 16;

        for(int var5 = -var2; var5 < var2; ++var5) {
            for(int var6 = -var3; var6 < var3; ++var6) {
                for(int var7 = -var4; var7 < var4; ++var7) {
                    BlockPosition var8 = var1.a(var5, var6, var7);
                    if (this.f(var8)) {
                        VillageDoor var9 = this.c(var8);
                        if (var9 == null) {
                            this.d(var8);
                        } else {
                            var9.a(this.time);
                        }
                    }
                }
            }
        }

    }

    private VillageDoor c(BlockPosition var1) {
        Iterator var2 = this.d.iterator();

        VillageDoor var3;
        do {
            if (!var2.hasNext()) {
                var2 = this.villages.iterator();

                VillageDoor var4;
                do {
                    if (!var2.hasNext()) {
                        return null;
                    }

                    Village var5 = (Village)var2.next();
                    var4 = var5.e(var1);
                } while(var4 == null);

                return var4;
            }

            var3 = (VillageDoor)var2.next();
        } while(var3.d().getX() != var1.getX() || var3.d().getZ() != var1.getZ() || Math.abs(var3.d().getY() - var1.getY()) > 1);

        return var3;
    }

    private void d(BlockPosition var1) {
        EnumDirection var2 = BlockDoor.h(this.world, var1);
        EnumDirection var3 = var2.opposite();
        int var4 = this.a(var1, var2, 5);
        int var5 = this.a(var1, var3, var4 + 1);
        if (var4 != var5) {
            this.d.add(new VillageDoor(var1, var4 < var5 ? var2 : var3, this.time));
        }

    }

    private int a(BlockPosition var1, EnumDirection var2, int var3) {
        int var4 = 0;

        for(int var5 = 1; var5 <= 5; ++var5) {
            if (this.world.i(var1.shift(var2, var5))) {
                ++var4;
                if (var4 >= var3) {
                    return var4;
                }
            }
        }

        return var4;
    }

    private boolean e(BlockPosition var1) {
        Iterator var2 = this.c.iterator();

        BlockPosition var3;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            var3 = (BlockPosition)var2.next();
        } while(!var3.equals(var1));

        return true;
    }

    private boolean f(BlockPosition var1) {
        // Paper start
        IBlockData iblockdata = this.world.getTypeIfLoaded(var1);
        if (iblockdata == null)
            return false;
        Block var2 = iblockdata.getBlock();
        // Paper end
        if (var2 instanceof BlockDoor) {
            return var2.getMaterial() == Material.WOOD;
        } else {
            return false;
        }
    }

    public void a(NBTTagCompound var1) {
        this.time = var1.getInt("Tick");
        NBTTagList var2 = var1.getList("Villages", 10);

        for(int var3 = 0; var3 < var2.size(); ++var3) {
            NBTTagCompound var4 = var2.get(var3);
            Village var5 = new Village();
            var5.a(var4);
            this.villages.add(var5);
        }

    }

    public void b(NBTTagCompound var1) {
        var1.setInt("Tick", this.time);
        NBTTagList var2 = new NBTTagList();
        Iterator var3 = this.villages.iterator();

        while(var3.hasNext()) {
            Village var4 = (Village)var3.next();
            NBTTagCompound var5 = new NBTTagCompound();
            var4.b(var5);
            var2.add(var5);
        }

        var1.set("Villages", var2);
    }

    public static String a(WorldProvider var0) {
        return "villages" + var0.getSuffix();
    }
}
