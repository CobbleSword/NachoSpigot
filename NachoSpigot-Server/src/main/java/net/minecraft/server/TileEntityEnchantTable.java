package net.minecraft.server;

import java.util.Random;

public class TileEntityEnchantTable extends TileEntity implements IUpdatePlayerListBox, ITileEntityContainer {
    public int a;
    public float f;
    public float g;
    public float h;
    public float i;
    public float j;
    public float k;
    public float l;
    public float m;
    public float n;
    private static Random o = new Random();
    private String p;

    public TileEntityEnchantTable() {
    }

    public void b(NBTTagCompound var1) {
        super.b(var1);
        if (this.hasCustomName()) {
            var1.setString("CustomName", this.p);
        }

    }

    public void a(NBTTagCompound var1) {
        super.a(var1);
        if (var1.hasKeyOfType("CustomName", 8)) {
            this.p = var1.getString("CustomName");
        }

    }

    public void c() {
        // Nacho-0050 - Don't tick Enchantment tables
        if(!world.nachoSpigotConfig.shouldTickEnchantmentTables)
        {
            return;
        }
        // Nacho-0050 End
        this.k = this.j;
        this.m = this.l;
        EntityHuman var1 = this.world.findNearbyPlayer((double)((float)this.position.getX() + 0.5F), (double)((float)this.position.getY() + 0.5F), (double)((float)this.position.getZ() + 0.5F), 3.0D);
        if (var1 != null) {
            double var2 = var1.locX - (double)((float)this.position.getX() + 0.5F);
            double var4 = var1.locZ - (double)((float)this.position.getZ() + 0.5F);
            this.n = (float)MathHelper.b(var4, var2);
            this.j += 0.1F;
            if (this.j < 0.5F || o.nextInt(40) == 0) {
                float var6 = this.h;

                do {
                    this.h += (float)(o.nextInt(4) - o.nextInt(4));
                } while(var6 == this.h);
            }
        } else {
            this.n += 0.02F;
            this.j -= 0.1F;
        }

        while(this.l >= 3.1415927F) {
            this.l -= 6.2831855F;
        }

        while(this.l < -3.1415927F) {
            this.l += 6.2831855F;
        }

        while(this.n >= 3.1415927F) {
            this.n -= 6.2831855F;
        }

        while(this.n < -3.1415927F) {
            this.n += 6.2831855F;
        }

        float var7;
        for(var7 = this.n - this.l; var7 >= 3.1415927F; var7 -= 6.2831855F) {
        }

        while(var7 < -3.1415927F) {
            var7 += 6.2831855F;
        }

        this.l += var7 * 0.4F;
        this.j = MathHelper.a(this.j, 0.0F, 1.0F);
        ++this.a;
        this.g = this.f;
        float var3 = (this.h - this.f) * 0.4F;
        float var8 = 0.2F;
        var3 = MathHelper.a(var3, -var8, var8);
        this.i += (var3 - this.i) * 0.9F;
        this.f += this.i;
    }

    public String getName() {
        return this.hasCustomName() ? this.p : "container.enchant";
    }

    public boolean hasCustomName() {
        return this.p != null && this.p.length() > 0;
    }

    public void a(String var1) {
        this.p = var1;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return (IChatBaseComponent)(this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName(), new Object[0]));
    }

    public Container createContainer(PlayerInventory var1, EntityHuman var2) {
        return new ContainerEnchantTable(var1, this.world, this.position);
    }

    public String getContainerName() {
        return "minecraft:enchanting_table";
    }
}
