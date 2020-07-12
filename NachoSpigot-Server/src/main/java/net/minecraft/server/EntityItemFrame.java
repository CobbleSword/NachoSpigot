package net.minecraft.server;

import java.util.UUID;
import org.apache.commons.codec.Charsets;

public class EntityItemFrame extends EntityHanging {

    private float c = 1.0F;

    public EntityItemFrame(World world) {
        super(world);
    }

    public EntityItemFrame(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        super(world, blockposition);
        this.setDirection(enumdirection);
    }

    protected void h() {
        this.getDataWatcher().add(8, 5);
        this.getDataWatcher().a(9, Byte.valueOf((byte) 0));
    }

    public float ao() {
        return 0.0F;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!damagesource.isExplosion() && this.getItem() != null) {
            if (!this.world.isClientSide) {
                // CraftBukkit start - fire EntityDamageEvent
                if (org.bukkit.craftbukkit.event.CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f, false) || this.dead) {
                    return true;
                }
                // CraftBukkit end
                this.a(damagesource.getEntity(), false);
                this.setItem((ItemStack) null);
            }

            return true;
        } else {
            return super.damageEntity(damagesource, f);
        }
    }

    public int l() {
        return 12;
    }

    public int m() {
        return 12;
    }

    public void b(Entity entity) {
        this.a(entity, true);
    }

    public void a(Entity entity, boolean flag) {
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            ItemStack itemstack = this.getItem();

            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                if (entityhuman.abilities.canInstantlyBuild) {
                    this.b(itemstack);
                    return;
                }
            }

            if (flag) {
                this.a(new ItemStack(Items.ITEM_FRAME), 0.0F);
            }

            if (itemstack != null && this.random.nextFloat() < this.c) {
                itemstack = itemstack.cloneItemStack();
                this.b(itemstack);
                this.a(itemstack, 0.0F);
            }

        }
    }

    private void b(ItemStack itemstack) {
        if (itemstack != null) {
            if (itemstack.getItem() == Items.FILLED_MAP) {
                WorldMap worldmap = ((ItemWorldMap) itemstack.getItem()).getSavedMap(itemstack, this.world);

                worldmap.decorations.remove(UUID.nameUUIDFromBytes(("frame-" + this.getId()).getBytes(Charsets.US_ASCII))); // Spigot
            }

            itemstack.a((EntityItemFrame) null);
        }
    }

    public ItemStack getItem() {
        return this.getDataWatcher().getItemStack(8);
    }

    public void setItem(ItemStack itemstack) {
        this.setItem(itemstack, true);
    }

    private void setItem(ItemStack itemstack, boolean flag) {
        if (itemstack != null) {
            itemstack = itemstack.cloneItemStack();
            itemstack.count = 1;
            itemstack.a(this);
        }

        this.getDataWatcher().watch(8, itemstack);
        this.getDataWatcher().update(8);
        if (flag && this.blockPosition != null) {
            this.world.updateAdjacentComparators(this.blockPosition, Blocks.AIR);
        }

    }

    public int getRotation() {
        return this.getDataWatcher().getByte(9);
    }

    public void setRotation(int i) {
        this.setRotation(i, true);
    }

    private void setRotation(int i, boolean flag) {
        this.getDataWatcher().watch(9, Byte.valueOf((byte) (i % 8)));
        if (flag && this.blockPosition != null) {
            this.world.updateAdjacentComparators(this.blockPosition, Blocks.AIR);
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        if (this.getItem() != null) {
            nbttagcompound.set("Item", this.getItem().save(new NBTTagCompound()));
            nbttagcompound.setByte("ItemRotation", (byte) this.getRotation());
            nbttagcompound.setFloat("ItemDropChance", this.c);
        }

        super.b(nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.isEmpty()) {
            this.setItem(ItemStack.createStack(nbttagcompound1), false);
            this.setRotation(nbttagcompound.getByte("ItemRotation"), false);
            if (nbttagcompound.hasKeyOfType("ItemDropChance", 99)) {
                this.c = nbttagcompound.getFloat("ItemDropChance");
            }

            if (nbttagcompound.hasKey("Direction")) {
                this.setRotation(this.getRotation() * 2, false);
            }
        }

        super.a(nbttagcompound);
    }

    public boolean e(EntityHuman entityhuman) {
        if (this.getItem() == null) {
            ItemStack itemstack = entityhuman.bA();

            if (itemstack != null && !this.world.isClientSide) {
                this.setItem(itemstack);
                if (!entityhuman.abilities.canInstantlyBuild && --itemstack.count <= 0) {
                    entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
                }
            }
        } else if (!this.world.isClientSide) {
            this.setRotation(this.getRotation() + 1);
        }

        return true;
    }

    public int q() {
        return this.getItem() == null ? 0 : this.getRotation() % 8 + 1;
    }
}
