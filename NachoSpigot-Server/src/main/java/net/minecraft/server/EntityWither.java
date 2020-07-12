package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
// CraftBukkit end

public class EntityWither extends EntityMonster implements IRangedEntity {

    private float[] a = new float[2];
    private float[] b = new float[2];
    private float[] c = new float[2];
    private float[] bm = new float[2];
    private int[] bn = new int[2];
    private int[] bo = new int[2];
    private int bp;
    private static final Predicate<Entity> bq = new Predicate() {
        public boolean a(Entity entity) {
            return entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() != EnumMonsterType.UNDEAD;
        }

        public boolean apply(Object object) {
            return this.a((Entity) object);
        }
    };

    public EntityWither(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());
        this.setSize(0.9F, 3.5F);
        this.fireProof = true;
        ((Navigation) this.getNavigation()).d(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 0, false, false, EntityWither.bq));
        this.b_ = 50;
    }

    protected void h() {
        super.h();
        this.datawatcher.a(17, new Integer(0));
        this.datawatcher.a(18, new Integer(0));
        this.datawatcher.a(19, new Integer(0));
        this.datawatcher.a(20, new Integer(0));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Invul", this.cl());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.r(nbttagcompound.getInt("Invul"));
    }

    protected String z() {
        return "mob.wither.idle";
    }

    protected String bo() {
        return "mob.wither.hurt";
    }

    protected String bp() {
        return "mob.wither.death";
    }

    public void m() {
        this.motY *= 0.6000000238418579D;
        double d0;
        double d1;
        double d2;

        if (!this.world.isClientSide && this.s(0) > 0) {
            Entity entity = this.world.a(this.s(0));

            if (entity != null) {
                if (this.locY < entity.locY || !this.cm() && this.locY < entity.locY + 5.0D) {
                    if (this.motY < 0.0D) {
                        this.motY = 0.0D;
                    }

                    this.motY += (0.5D - this.motY) * 0.6000000238418579D;
                }

                double d3 = entity.locX - this.locX;

                d0 = entity.locZ - this.locZ;
                d1 = d3 * d3 + d0 * d0;
                if (d1 > 9.0D) {
                    d2 = (double) MathHelper.sqrt(d1);
                    this.motX += (d3 / d2 * 0.5D - this.motX) * 0.6000000238418579D;
                    this.motZ += (d0 / d2 * 0.5D - this.motZ) * 0.6000000238418579D;
                }
            }
        }

        if (this.motX * this.motX + this.motZ * this.motZ > 0.05000000074505806D) {
            this.yaw = (float) MathHelper.b(this.motZ, this.motX) * 57.295776F - 90.0F;
        }

        super.m();

        int i;

        for (i = 0; i < 2; ++i) {
            this.bm[i] = this.b[i];
            this.c[i] = this.a[i];
        }

        int j;

        for (i = 0; i < 2; ++i) {
            j = this.s(i + 1);
            Entity entity1 = null;

            if (j > 0) {
                entity1 = this.world.a(j);
            }

            if (entity1 != null) {
                d0 = this.t(i + 1);
                d1 = this.u(i + 1);
                d2 = this.v(i + 1);
                double d4 = entity1.locX - d0;
                double d5 = entity1.locY + (double) entity1.getHeadHeight() - d1;
                double d6 = entity1.locZ - d2;
                double d7 = (double) MathHelper.sqrt(d4 * d4 + d6 * d6);
                float f = (float) (MathHelper.b(d6, d4) * 180.0D / 3.1415927410125732D) - 90.0F;
                float f1 = (float) (-(MathHelper.b(d5, d7) * 180.0D / 3.1415927410125732D));

                this.a[i] = this.b(this.a[i], f1, 40.0F);
                this.b[i] = this.b(this.b[i], f, 10.0F);
            } else {
                this.b[i] = this.b(this.b[i], this.aI, 10.0F);
            }
        }

        boolean flag = this.cm();

        for (j = 0; j < 3; ++j) {
            double d8 = this.t(j);
            double d9 = this.u(j);
            double d10 = this.v(j);

            this.world.addParticle(EnumParticle.SMOKE_NORMAL, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D, new int[0]);
            if (flag && this.world.random.nextInt(4) == 0) {
                this.world.addParticle(EnumParticle.SPELL_MOB, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D, new int[0]);
            }
        }

        if (this.cl() > 0) {
            for (j = 0; j < 3; ++j) {
                this.world.addParticle(EnumParticle.SPELL_MOB, this.locX + this.random.nextGaussian() * 1.0D, this.locY + (double) (this.random.nextFloat() * 3.3F), this.locZ + this.random.nextGaussian() * 1.0D, 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D, new int[0]);
            }
        }

    }

    protected void E() {
        int i;

        if (this.cl() > 0) {
            i = this.cl() - 1;
            if (i <= 0) {
                // CraftBukkit start
                // this.world.createExplosion(this, this.locX, this.locY + (double) this.getHeadHeight(), this.locZ, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
                ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 7.0F, false);
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.world.createExplosion(this, this.locX, this.locY + (double) this.getHeadHeight(), this.locZ, event.getRadius(), event.getFire(), this.world.getGameRules().getBoolean("mobGriefing"));
                }
                // CraftBukkit end

                // CraftBukkit start - Use relative location for far away sounds
                // this.world.a(1013, new BlockPosition(this), 0);
                int viewDistance = ((WorldServer) this.world).getServer().getViewDistance() * 16;
                for (EntityPlayer player : (List<EntityPlayer>) MinecraftServer.getServer().getPlayerList().players) {
                    double deltaX = this.locX - player.locX;
                    double deltaZ = this.locZ - player.locZ;
                    double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
                    if ( world.spigotConfig.witherSpawnSoundRadius > 0 && distanceSquared > world.spigotConfig.witherSpawnSoundRadius * world.spigotConfig.witherSpawnSoundRadius ) continue; // Spigot
                    if (distanceSquared > viewDistance * viewDistance) {
                        double deltaLength = Math.sqrt(distanceSquared);
                        double relativeX = player.locX + (deltaX / deltaLength) * viewDistance;
                        double relativeZ = player.locZ + (deltaZ / deltaLength) * viewDistance;
                        player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1013, new BlockPosition((int) relativeX, (int) this.locY, (int) relativeZ), 0, true));
                    } else {
                        player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1013, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0, true));
                    }
                }
                // CraftBukkit end
            }

            this.r(i);
            if (this.ticksLived % 10 == 0) {
                this.heal(10.0F, EntityRegainHealthEvent.RegainReason.WITHER_SPAWN); // CraftBukkit
            }

        } else {
            super.E();

            int j;

            for (i = 1; i < 3; ++i) {
                if (this.ticksLived >= this.bn[i - 1]) {
                    this.bn[i - 1] = this.ticksLived + 10 + this.random.nextInt(10);
                    if (this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD) {
                        int k = i - 1;
                        int l = this.bo[i - 1];

                        this.bo[k] = this.bo[i - 1] + 1;
                        if (l > 15) {
                            float f = 10.0F;
                            float f1 = 5.0F;
                            double d0 = MathHelper.a(this.random, this.locX - (double) f, this.locX + (double) f);
                            double d1 = MathHelper.a(this.random, this.locY - (double) f1, this.locY + (double) f1);
                            double d2 = MathHelper.a(this.random, this.locZ - (double) f, this.locZ + (double) f);

                            this.a(i + 1, d0, d1, d2, true);
                            this.bo[i - 1] = 0;
                        }
                    }

                    j = this.s(i);
                    if (j > 0) {
                        Entity entity = this.world.a(j);

                        if (entity != null && entity.isAlive() && this.h(entity) <= 900.0D && this.hasLineOfSight(entity)) {
                            if (entity instanceof EntityHuman && ((EntityHuman) entity).abilities.isInvulnerable) {
                                this.b(i, 0);
                            } else {
                                this.a(i + 1, (EntityLiving) entity);
                                this.bn[i - 1] = this.ticksLived + 40 + this.random.nextInt(20);
                                this.bo[i - 1] = 0;
                            }
                        } else {
                            this.b(i, 0);
                        }
                    } else {
                        List list = this.world.a(EntityLiving.class, this.getBoundingBox().grow(20.0D, 8.0D, 20.0D), Predicates.and(EntityWither.bq, IEntitySelector.d));

                        for (int i1 = 0; i1 < 10 && !list.isEmpty(); ++i1) {
                            EntityLiving entityliving = (EntityLiving) list.get(this.random.nextInt(list.size()));

                            if (entityliving != this && entityliving.isAlive() && this.hasLineOfSight(entityliving)) {
                                if (entityliving instanceof EntityHuman) {
                                    if (!((EntityHuman) entityliving).abilities.isInvulnerable) {
                                        this.b(i, entityliving.getId());
                                    }
                                } else {
                                    this.b(i, entityliving.getId());
                                }
                                break;
                            }

                            list.remove(entityliving);
                        }
                    }
                }
            }

            if (this.getGoalTarget() != null) {
                this.b(0, this.getGoalTarget().getId());
            } else {
                this.b(0, 0);
            }

            if (this.bp > 0) {
                --this.bp;
                if (this.bp == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
                    i = MathHelper.floor(this.locY);
                    j = MathHelper.floor(this.locX);
                    int j1 = MathHelper.floor(this.locZ);
                    boolean flag = false;

                    for (int k1 = -1; k1 <= 1; ++k1) {
                        for (int l1 = -1; l1 <= 1; ++l1) {
                            for (int i2 = 0; i2 <= 3; ++i2) {
                                int j2 = j + k1;
                                int k2 = i + i2;
                                int l2 = j1 + l1;
                                BlockPosition blockposition = new BlockPosition(j2, k2, l2);
                                Block block = this.world.getType(blockposition).getBlock();

                                if (block.getMaterial() != Material.AIR && a(block)) {
                                    // CraftBukkit start
                                    if (CraftEventFactory.callEntityChangeBlockEvent(this, j2, k2, l2, Blocks.AIR, 0).isCancelled()) {
                                        continue;
                                    }
                                    // CraftBukkit end
                                    flag = this.world.setAir(blockposition, true) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.world.a((EntityHuman) null, 1012, new BlockPosition(this), 0);
                    }
                }
            }

            if (this.ticksLived % 20 == 0) {
                this.heal(1.0F, EntityRegainHealthEvent.RegainReason.REGEN); // CraftBukkit
            }

        }
    }

    public static boolean a(Block block) {
        return block != Blocks.BEDROCK && block != Blocks.END_PORTAL && block != Blocks.END_PORTAL_FRAME && block != Blocks.COMMAND_BLOCK && block != Blocks.BARRIER;
    }

    public void n() {
        this.r(220);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public void aA() {}

    public int br() {
        return 4;
    }

    private double t(int i) {
        if (i <= 0) {
            return this.locX;
        } else {
            float f = (this.aI + (float) (180 * (i - 1))) / 180.0F * 3.1415927F;
            float f1 = MathHelper.cos(f);

            return this.locX + (double) f1 * 1.3D;
        }
    }

    private double u(int i) {
        return i <= 0 ? this.locY + 3.0D : this.locY + 2.2D;
    }

    private double v(int i) {
        if (i <= 0) {
            return this.locZ;
        } else {
            float f = (this.aI + (float) (180 * (i - 1))) / 180.0F * 3.1415927F;
            float f1 = MathHelper.sin(f);

            return this.locZ + (double) f1 * 1.3D;
        }
    }

    private float b(float f, float f1, float f2) {
        float f3 = MathHelper.g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    private void a(int i, EntityLiving entityliving) {
        this.a(i, entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight() * 0.5D, entityliving.locZ, i == 0 && this.random.nextFloat() < 0.001F);
    }

    private void a(int i, double d0, double d1, double d2, boolean flag) {
        this.world.a((EntityHuman) null, 1014, new BlockPosition(this), 0);
        double d3 = this.t(i);
        double d4 = this.u(i);
        double d5 = this.v(i);
        double d6 = d0 - d3;
        double d7 = d1 - d4;
        double d8 = d2 - d5;
        EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.world, this, d6, d7, d8);

        if (flag) {
            entitywitherskull.setCharged(true);
        }

        entitywitherskull.locY = d4;
        entitywitherskull.locX = d3;
        entitywitherskull.locZ = d5;
        this.world.addEntity(entitywitherskull);
    }

    public void a(EntityLiving entityliving, float f) {
        this.a(0, entityliving);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource != DamageSource.DROWN && !(damagesource.getEntity() instanceof EntityWither)) {
            if (this.cl() > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                Entity entity;

                if (this.cm()) {
                    entity = damagesource.i();
                    if (entity instanceof EntityArrow) {
                        return false;
                    }
                }

                entity = damagesource.getEntity();
                if (entity != null && !(entity instanceof EntityHuman) && entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == this.getMonsterType()) {
                    return false;
                } else {
                    if (this.bp <= 0) {
                        this.bp = 20;
                    }

                    for (int i = 0; i < this.bo.length; ++i) {
                        this.bo[i] += 3;
                    }

                    return super.damageEntity(damagesource, f);
                }
            }
        } else {
            return false;
        }
    }

    protected void dropDeathLoot(boolean flag, int i) {
        EntityItem entityitem = this.a(Items.NETHER_STAR, 1);

        if (entityitem != null) {
            entityitem.u();
        }

        if (!this.world.isClientSide) {
            Iterator iterator = this.world.a(EntityHuman.class, this.getBoundingBox().grow(50.0D, 100.0D, 50.0D)).iterator();

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                entityhuman.b((Statistic) AchievementList.J);
            }
        }

    }

    protected void D() {
        this.ticksFarFromPlayer = 0;
    }

    public void e(float f, float f1) {}

    public void addEffect(MobEffect mobeffect) {}

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(300.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.6000000238418579D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(40.0D);
    }

    public int cl() {
        return this.datawatcher.getInt(20);
    }

    public void r(int i) {
        this.datawatcher.watch(20, Integer.valueOf(i));
    }

    public int s(int i) {
        return this.datawatcher.getInt(17 + i);
    }

    public void b(int i, int j) {
        this.datawatcher.watch(17 + i, Integer.valueOf(j));
    }

    public boolean cm() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    public void mount(Entity entity) {
        this.vehicle = null;
    }
}
