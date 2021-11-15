package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import java.util.HashMap;

import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
// CraftBukkit end

import me.elier.nachospigot.config.NachoConfig;

public class EntityPotion extends EntityProjectile {

    public boolean compensated = false; // IonSpigot - Lag Compensated Potions
    public ItemStack item;

    public EntityPotion(World world) {
        super(world);
    }

    public EntityPotion(World world, EntityLiving entityliving, int i) {
        this(world, entityliving, new ItemStack(Items.POTION, 1, i));
    }

    public EntityPotion(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(world, entityliving);
        this.item = itemstack;
        // IonSpigot start - Lag Compensated Potions
        if (entityliving instanceof EntityPlayer && NachoConfig.lagCompensatedPotions) {
            ((EntityPlayer) entityliving).potions.add(this);
            compensated = true;
        }
        // IonSpigot end
    }

    public EntityPotion(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(world, d0, d1, d2);
        this.item = itemstack;
    }

    protected float m() {
        return 0.05F;
    }

    protected float j() {
        return 0.5F;
    }

    protected float l() {
        return -20.0F;
    }

    public void setPotionValue(int i) {
        if (this.item == null) {
            this.item = new ItemStack(Items.POTION, 1, 0);
        }

        this.item.setData(i);
    }

    public int getPotionValue() {
        if (this.item == null) {
            this.item = new ItemStack(Items.POTION, 1, 0);
        }

        return this.item.getData();
    }

    // IonSpigot start - Lag Compensated Potions
    @Override
    public void t_() {
        if (!compensated) {
            tick();
        }
    }
    public void tick() {
        super.t_();
    }
    // IonSpigot end

    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            List list = Items.POTION.h(this.item);

            if (true || list != null && !list.isEmpty()) { // CraftBukkit - Call event even if no effects to apply
                AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
                List list1 = this.world.a(EntityLiving.class, axisalignedbb);

                if (true || !list1.isEmpty()) { // CraftBukkit - Run code even if there are no entities around
                    Iterator iterator = list1.iterator();

                    // CraftBukkit
                    HashMap<LivingEntity, Double> affected = new HashMap<LivingEntity, Double>();

                    while (iterator.hasNext()) {
                        EntityLiving entityliving = (EntityLiving) iterator.next();
                        double d0 = this.h(entityliving);

                        if (d0 < 16.0D) {
                            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                            if (entityliving == movingobjectposition.entity) {
                                d1 = 1.0D;
                            }

                            // CraftBukkit start
                            affected.put((LivingEntity) entityliving.getBukkitEntity(), d1);
                        }
                    }

                    org.bukkit.event.entity.PotionSplashEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callPotionSplashEvent(this, affected);
                    if (!event.isCancelled() && list != null && !list.isEmpty()) { // do not process effects if there are no effects to process
                        for (LivingEntity victim : event.getAffectedEntities()) {
                            if (!(victim instanceof CraftLivingEntity)) {
                                continue;
                            }

                            EntityLiving entityliving = ((CraftLivingEntity) victim).getHandle();

                            if (entityliving instanceof EntityPlayer && !((EntityPlayer) entityliving).getBukkitEntity().canSee(this.getShooter().getBukkitEntity())) {
                                continue;
                            }

                            double d1 = event.getIntensity(victim);
                            // CraftBukkit end

                            Iterator iterator1 = list.iterator();

                            while (iterator1.hasNext()) {
                                MobEffect mobeffect = (MobEffect) iterator1.next();
                                int i = mobeffect.getEffectId();

                                // CraftBukkit start - Abide by PVP settings - for players only!
                                if (!this.world.pvpMode && this.getShooter() instanceof EntityPlayer && entityliving instanceof EntityPlayer && entityliving != this.getShooter()) {
                                    // Block SLOWER_MOVEMENT, SLOWER_DIG, HARM, BLINDNESS, HUNGER, WEAKNESS and POISON potions
                                    if (i == 2 || i == 4 || i == 7 || i == 15 || i == 17 || i == 18 || i == 19) continue;
                                }
                                // CraftBukkit end

                                if (MobEffectList.byId[i].isInstant()) {
                                    MobEffectList.byId[i].applyInstantEffect(this, this.getShooter(), entityliving, mobeffect.getAmplifier(), d1);
                                } else {
                                    int j = (int) (d1 * (double) mobeffect.getDuration() + 0.5D);

                                    if (j > 20) {
                                        entityliving.addEffect(new MobEffect(i, j, mobeffect.getAmplifier()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.getShooter() instanceof EntityHuman) {
                this.world.a((EntityHuman) this.getShooter(), 2002, new BlockPosition(this), this.getPotionValue());
            } else {
                this.world.triggerEffect(2002, new BlockPosition(this), this.getPotionValue());
            }
            this.die();
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Potion", 10)) {
            this.item = ItemStack.createStack(nbttagcompound.getCompound("Potion"));
        } else {
            this.setPotionValue(nbttagcompound.getInt("potionValue"));
        }

        if (this.item == null) {
            this.die();
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.item != null) {
            nbttagcompound.set("Potion", this.item.save(new NBTTagCompound()));
        }

    }
}
