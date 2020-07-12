package net.minecraft.server;

public abstract class EntityAgeable extends EntityCreature {

    protected int a;
    protected int b;
    protected int c;
    private float bm = -1.0F;
    private float bn;
    public boolean ageLocked = false; // CraftBukkit

    // Spigot start
    @Override
    public void inactiveTick()
    {
        super.inactiveTick();
        if ( this.world.isClientSide || this.ageLocked )
        { // CraftBukkit
            this.a( this.isBaby() );
        } else
        {
            int i = this.getAge();

            if ( i < 0 )
            {
                ++i;
                this.setAgeRaw( i );
            } else if ( i > 0 )
            {
                --i;
                this.setAgeRaw( i );
            }
        }
    }
    // Spigot end

    public EntityAgeable(World world) {
        super(world);
    }

    public abstract EntityAgeable createChild(EntityAgeable entityageable);

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (itemstack != null && itemstack.getItem() == Items.SPAWN_EGG) {
            if (!this.world.isClientSide) {
                Class oclass = EntityTypes.a(itemstack.getData());

                if (oclass != null && this.getClass() == oclass) {
                    EntityAgeable entityageable = this.createChild(this);

                    if (entityageable != null) {
                        entityageable.setAgeRaw(-24000);
                        entityageable.setPositionRotation(this.locX, this.locY, this.locZ, 0.0F, 0.0F);
                        this.world.addEntity(entityageable, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG); // CraftBukkit
                        if (itemstack.hasName()) {
                            entityageable.setCustomName(itemstack.getName());
                        }

                        if (!entityhuman.abilities.canInstantlyBuild) {
                            --itemstack.count;
                            if (itemstack.count == 0) { // CraftBukkit - allow less than 0 stacks as "infinite"
                                entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
                            }
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected void h() {
        super.h();
        this.datawatcher.a(12, Byte.valueOf((byte) 0));
    }

    public int getAge() {
        return this.world.isClientSide ? this.datawatcher.getByte(12) : this.a;
    }

    public void setAge(int i, boolean flag) {
        int j = this.getAge();
        int k = j;

        j += i * 20;
        if (j > 0) {
            j = 0;
            if (k < 0) {
                this.n();
            }
        }

        int l = j - k;

        this.setAgeRaw(j);
        if (flag) {
            this.b += l;
            if (this.c == 0) {
                this.c = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAgeRaw(this.b);
        }

    }

    public void setAge(int i) {
        this.setAge(i, false);
    }

    public void setAgeRaw(int i) {
        this.datawatcher.watch(12, Byte.valueOf((byte) MathHelper.clamp(i, -1, 1)));
        this.a = i;
        this.a(this.isBaby());
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Age", this.getAge());
        nbttagcompound.setInt("ForcedAge", this.b);
        nbttagcompound.setBoolean("AgeLocked", this.ageLocked); // CraftBukkit
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setAgeRaw(nbttagcompound.getInt("Age"));
        this.b = nbttagcompound.getInt("ForcedAge");
        this.ageLocked = nbttagcompound.getBoolean("AgeLocked"); // CraftBukkit
    }

    public void m() {
        super.m();
        if (this.world.isClientSide || ageLocked) { // CraftBukkit
            if (this.c > 0) {
                if (this.c % 4 == 0) {
                    this.world.addParticle(EnumParticle.VILLAGER_HAPPY, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                --this.c;
            }

            this.a(this.isBaby());
        } else {
            int i = this.getAge();

            if (i < 0) {
                ++i;
                this.setAgeRaw(i);
                if (i == 0) {
                    this.n();
                }
            } else if (i > 0) {
                --i;
                this.setAgeRaw(i);
            }
        }

    }

    protected void n() {}

    public boolean isBaby() {
        return this.getAge() < 0;
    }

    public void a(boolean flag) {
        this.a(flag ? 0.5F : 1.0F);
    }

    public final void setSize(float f, float f1) { // CraftBukkit - protected to public
        boolean flag = this.bm > 0.0F;

        this.bm = f;
        this.bn = f1;
        if (!flag) {
            this.a(1.0F);
        }

    }

    protected final void a(float f) {
        super.setSize(this.bm * f, this.bn * f);
    }
}
