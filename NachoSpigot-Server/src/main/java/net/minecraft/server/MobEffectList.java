package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
// CraftBukkit end

public class MobEffectList {

    public static final MobEffectList[] byId = new MobEffectList[32];
    private static final Map<MinecraftKey, MobEffectList> I = Maps.newHashMap();
    public static final MobEffectList b = null;
    public static final MobEffectList FASTER_MOVEMENT = (new MobEffectList(1, new MinecraftKey("speed"), false, 8171462)).c("potion.moveSpeed").b(0, 0).a(GenericAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2);
    public static final MobEffectList SLOWER_MOVEMENT = (new MobEffectList(2, new MinecraftKey("slowness"), true, 5926017)).c("potion.moveSlowdown").b(1, 0).a(GenericAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, 2);
    public static final MobEffectList FASTER_DIG = (new MobEffectList(3, new MinecraftKey("haste"), false, 14270531)).c("potion.digSpeed").b(2, 0).a(1.5D);
    public static final MobEffectList SLOWER_DIG = (new MobEffectList(4, new MinecraftKey("mining_fatigue"), true, 4866583)).c("potion.digSlowDown").b(3, 0);
    public static final MobEffectList INCREASE_DAMAGE = (new MobEffectAttackDamage(5, new MinecraftKey("strength"), false, 9643043)).c("potion.damageBoost").b(4, 0).a(GenericAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5D, 2);
    public static final MobEffectList HEAL = (new InstantMobEffect(6, new MinecraftKey("instant_health"), false, 16262179)).c("potion.heal");
    public static final MobEffectList HARM = (new InstantMobEffect(7, new MinecraftKey("instant_damage"), true, 4393481)).c("potion.harm");
    public static final MobEffectList JUMP = (new MobEffectList(8, new MinecraftKey("jump_boost"), false, 2293580)).c("potion.jump").b(2, 1);
    public static final MobEffectList CONFUSION = (new MobEffectList(9, new MinecraftKey("nausea"), true, 5578058)).c("potion.confusion").b(3, 1).a(0.25D);
    public static final MobEffectList REGENERATION = (new MobEffectList(10, new MinecraftKey("regeneration"), false, 13458603)).c("potion.regeneration").b(7, 0).a(0.25D);
    public static final MobEffectList RESISTANCE = (new MobEffectList(11, new MinecraftKey("resistance"), false, 10044730)).c("potion.resistance").b(6, 1);
    public static final MobEffectList FIRE_RESISTANCE = (new MobEffectList(12, new MinecraftKey("fire_resistance"), false, 14981690)).c("potion.fireResistance").b(7, 1);
    public static final MobEffectList WATER_BREATHING = (new MobEffectList(13, new MinecraftKey("water_breathing"), false, 3035801)).c("potion.waterBreathing").b(0, 2);
    public static final MobEffectList INVISIBILITY = (new MobEffectList(14, new MinecraftKey("invisibility"), false, 8356754)).c("potion.invisibility").b(0, 1);
    public static final MobEffectList BLINDNESS = (new MobEffectList(15, new MinecraftKey("blindness"), true, 2039587)).c("potion.blindness").b(5, 1).a(0.25D);
    public static final MobEffectList NIGHT_VISION = (new MobEffectList(16, new MinecraftKey("night_vision"), false, 2039713)).c("potion.nightVision").b(4, 1);
    public static final MobEffectList HUNGER = (new MobEffectList(17, new MinecraftKey("hunger"), true, 5797459)).c("potion.hunger").b(1, 1);
    public static final MobEffectList WEAKNESS = (new MobEffectAttackDamage(18, new MinecraftKey("weakness"), true, 4738376)).c("potion.weakness").b(5, 0).a(GenericAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);
    public static final MobEffectList POISON = (new MobEffectList(19, new MinecraftKey("poison"), true, 5149489)).c("potion.poison").b(6, 0).a(0.25D);
    public static final MobEffectList WITHER = (new MobEffectList(20, new MinecraftKey("wither"), true, 3484199)).c("potion.wither").b(1, 2).a(0.25D);
    public static final MobEffectList HEALTH_BOOST = (new MobEffectHealthBoost(21, new MinecraftKey("health_boost"), false, 16284963)).c("potion.healthBoost").b(2, 2).a(GenericAttributes.maxHealth, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0);
    public static final MobEffectList ABSORBTION = (new MobEffectAbsorption(22, new MinecraftKey("absorption"), false, 2445989)).c("potion.absorption").b(2, 2);
    public static final MobEffectList SATURATION = (new InstantMobEffect(23, new MinecraftKey("saturation"), false, 16262179)).c("potion.saturation");
    public static final MobEffectList z = null;
    public static final MobEffectList A = null;
    public static final MobEffectList B = null;
    public static final MobEffectList C = null;
    public static final MobEffectList D = null;
    public static final MobEffectList E = null;
    public static final MobEffectList F = null;
    public static final MobEffectList G = null;
    public final int id;
    private final Map<IAttribute, AttributeModifier> J = Maps.newHashMap();
    private final boolean K;
    private final int L;
    private String M = "";
    private int N = -1;
    private double O;
    private boolean P;

    protected MobEffectList(int i, MinecraftKey minecraftkey, boolean flag, int j) {
        this.id = i;
        MobEffectList.byId[i] = this;
        MobEffectList.I.put(minecraftkey, this);
        this.K = flag;
        if (flag) {
            this.O = 0.5D;
        } else {
            this.O = 1.0D;
        }

        this.L = j;
        org.bukkit.potion.PotionEffectType.registerPotionEffectType(new org.bukkit.craftbukkit.potion.CraftPotionEffectType(this)); // CraftBukkit
    }

    public static MobEffectList b(String s) {
        return (MobEffectList) MobEffectList.I.get(new MinecraftKey(s));
    }

    public static Set<MinecraftKey> c() {
        return MobEffectList.I.keySet();
    }

    protected MobEffectList b(int i, int j) {
        this.N = i + j * 8;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public void tick(EntityLiving entityliving, int i) {
        if (this.id == MobEffectList.REGENERATION.id) {
            if (entityliving.getHealth() < entityliving.getMaxHealth()) {
                entityliving.heal(1.0F, RegainReason.MAGIC_REGEN); // CraftBukkit
            }
        } else if (this.id == MobEffectList.POISON.id) {
            if (entityliving.getHealth() > 1.0F) {
                entityliving.damageEntity(CraftEventFactory.POISON, 1.0F);  // CraftBukkit - DamageSource.MAGIC -> CraftEventFactory.POISON
            }
        } else if (this.id == MobEffectList.WITHER.id) {
            entityliving.damageEntity(DamageSource.WITHER, 1.0F);
        } else if (this.id == MobEffectList.HUNGER.id && entityliving instanceof EntityHuman) {
            ((EntityHuman) entityliving).applyExhaustion(0.025F * (float) (i + 1));
        } else if (this.id == MobEffectList.SATURATION.id && entityliving instanceof EntityHuman) {
            if (!entityliving.world.isClientSide) {
                // CraftBukkit start
                EntityHuman entityhuman = (EntityHuman) entityliving;
                int oldFoodLevel = entityhuman.getFoodData().foodLevel;

                org.bukkit.event.entity.FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(entityhuman, i + 1 + oldFoodLevel);

                if (!event.isCancelled()) {
                    entityhuman.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 1.0F);
                }

                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutUpdateHealth(((EntityPlayer) entityhuman).getBukkitEntity().getScaledHealth(), entityhuman.getFoodData().foodLevel, entityhuman.getFoodData().saturationLevel));
                // CraftBukkit end
            }
        } else if ((this.id != MobEffectList.HEAL.id || entityliving.bm()) && (this.id != MobEffectList.HARM.id || !entityliving.bm())) {
            if (this.id == MobEffectList.HARM.id && !entityliving.bm() || this.id == MobEffectList.HEAL.id && entityliving.bm()) {
                entityliving.damageEntity(DamageSource.MAGIC, (float) (6 << i));
            }
        } else {
            entityliving.heal((float) Math.max(4 << i, 0), RegainReason.MAGIC); // CraftBukkit
        }

    }

    public void applyInstantEffect(Entity entity, Entity entity1, EntityLiving entityliving, int i, double d0) {
        int j;

        if ((this.id != MobEffectList.HEAL.id || entityliving.bm()) && (this.id != MobEffectList.HARM.id || !entityliving.bm())) {
            if (this.id == MobEffectList.HARM.id && !entityliving.bm() || this.id == MobEffectList.HEAL.id && entityliving.bm()) {
                j = (int) (d0 * (double) (6 << i) + 0.5D);
                if (entity == null) {
                    entityliving.damageEntity(DamageSource.MAGIC, (float) j);
                } else {
                    entityliving.damageEntity(DamageSource.b(entity, entity1), (float) j);
                }
            }
        } else {
            j = (int) (d0 * (double) (4 << i) + 0.5D);
            entityliving.heal((float) j, RegainReason.MAGIC); // CraftBukkit
        }

    }

    public boolean isInstant() {
        return false;
    }

    public boolean a(int i, int j) {
        int k;

        if (this.id == MobEffectList.REGENERATION.id) {
            k = 50 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this.id == MobEffectList.POISON.id) {
            k = 25 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this.id == MobEffectList.WITHER.id) {
            k = 40 >> j;
            return k > 0 ? i % k == 0 : true;
        } else {
            return this.id == MobEffectList.HUNGER.id;
        }
    }

    public MobEffectList c(String s) {
        this.M = s;
        return this;
    }

    public String a() {
        return this.M;
    }

    protected MobEffectList a(double d0) {
        this.O = d0;
        return this;
    }

    public double getDurationModifier() {
        return this.O;
    }

    public boolean j() {
        return this.P;
    }

    public int k() {
        return this.L;
    }

    public MobEffectList a(IAttribute iattribute, String s, double d0, int i) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(s), this.a(), d0, i);

        this.J.put(iattribute, attributemodifier);
        return this;
    }

    public void a(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.J.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            AttributeInstance attributeinstance = attributemapbase.a((IAttribute) entry.getKey());

            if (attributeinstance != null) {
                attributeinstance.c((AttributeModifier) entry.getValue());
            }
        }

    }

    public void b(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.J.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            AttributeInstance attributeinstance = attributemapbase.a((IAttribute) entry.getKey());

            if (attributeinstance != null) {
                AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();

                attributeinstance.c(attributemodifier);
                attributeinstance.b(new AttributeModifier(attributemodifier.a(), this.a() + " " + i, this.a(i, attributemodifier), attributemodifier.c()));
            }
        }

    }

    public double a(int i, AttributeModifier attributemodifier) {
        return attributemodifier.d() * (double) (i + 1);
    }
}
