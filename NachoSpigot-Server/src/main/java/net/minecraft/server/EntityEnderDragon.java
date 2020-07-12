package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.Bukkit;
// CraftBukkit end

public class EntityEnderDragon extends EntityInsentient implements IComplex, IMonster {

    public double a;
    public double b;
    public double c;
    public double[][] bk = new double[64][3];
    public int bl = -1;
    public EntityComplexPart[] children;
    public EntityComplexPart bn;
    public EntityComplexPart bo;
    public EntityComplexPart bp;
    public EntityComplexPart bq;
    public EntityComplexPart br;
    public EntityComplexPart bs;
    public EntityComplexPart bt;
    public float bu;
    public float bv;
    public boolean bw;
    public boolean bx;
    public Entity target;
    public int by;
    public EntityEnderCrystal bz;
    private Explosion explosionSource = new Explosion(null, this, Double.NaN, Double.NaN, Double.NaN, Float.NaN, true, true); // CraftBukkit - reusable source for CraftTNTPrimed.getSource()

    public EntityEnderDragon(World world) {
        super(world);
        this.children = new EntityComplexPart[] { this.bn = new EntityComplexPart(this, "head", 6.0F, 6.0F), this.bo = new EntityComplexPart(this, "body", 8.0F, 8.0F), this.bp = new EntityComplexPart(this, "tail", 4.0F, 4.0F), this.bq = new EntityComplexPart(this, "tail", 4.0F, 4.0F), this.br = new EntityComplexPart(this, "tail", 4.0F, 4.0F), this.bs = new EntityComplexPart(this, "wing", 4.0F, 4.0F), this.bt = new EntityComplexPart(this, "wing", 4.0F, 4.0F)};
        this.setHealth(this.getMaxHealth());
        this.setSize(16.0F, 8.0F);
        this.noclip = true;
        this.fireProof = true;
        this.b = 100.0D;
        this.ah = true;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(200.0D);
    }

    protected void h() {
        super.h();
    }

    public double[] b(int i, float f) {
        if (this.getHealth() <= 0.0F) {
            f = 0.0F;
        }

        f = 1.0F - f;
        int j = this.bl - i * 1 & 63;
        int k = this.bl - i * 1 - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.bk[j][0];
        double d1 = MathHelper.g(this.bk[k][0] - d0);

        adouble[0] = d0 + d1 * (double) f;
        d0 = this.bk[j][1];
        d1 = this.bk[k][1] - d0;
        adouble[1] = d0 + d1 * (double) f;
        adouble[2] = this.bk[j][2] + (this.bk[k][2] - this.bk[j][2]) * (double) f;
        return adouble;
    }

    public void m() {
        float f;
        float f1;

        if (this.world.isClientSide) {
            f = MathHelper.cos(this.bv * 3.1415927F * 2.0F);
            f1 = MathHelper.cos(this.bu * 3.1415927F * 2.0F);
            if (f1 <= -0.3F && f >= -0.3F && !this.R()) {
                this.world.a(this.locX, this.locY, this.locZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
            }
        }

        this.bu = this.bv;
        float f2;

        if (this.getHealth() <= 0.0F) {
            f = (this.random.nextFloat() - 0.5F) * 8.0F;
            f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.locX + (double) f, this.locY + 2.0D + (double) f1, this.locZ + (double) f2, 0.0D, 0.0D, 0.0D, new int[0]);
        } else {
            this.n();
            f = 0.2F / (MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 10.0F + 1.0F);
            f *= (float) Math.pow(2.0D, this.motY);
            if (this.bx) {
                this.bv += f * 0.5F;
            } else {
                this.bv += f;
            }

            this.yaw = MathHelper.g(this.yaw);
            if (this.ce()) {
                this.bv = 0.5F;
            } else {
                if (this.bl < 0) {
                    for (int i = 0; i < this.bk.length; ++i) {
                        this.bk[i][0] = (double) this.yaw;
                        this.bk[i][1] = this.locY;
                    }
                }

                if (++this.bl == this.bk.length) {
                    this.bl = 0;
                }

                this.bk[this.bl][0] = (double) this.yaw;
                this.bk[this.bl][1] = this.locY;
                double d0;
                double d1;
                double d2;
                double d3;
                float f3;

                if (this.world.isClientSide) {
                    if (this.bc > 0) {
                        d3 = this.locX + (this.bd - this.locX) / (double) this.bc;
                        d0 = this.locY + (this.be - this.locY) / (double) this.bc;
                        d1 = this.locZ + (this.bf - this.locZ) / (double) this.bc;
                        d2 = MathHelper.g(this.bg - (double) this.yaw);
                        this.yaw = (float) ((double) this.yaw + d2 / (double) this.bc);
                        this.pitch = (float) ((double) this.pitch + (this.bh - (double) this.pitch) / (double) this.bc);
                        --this.bc;
                        this.setPosition(d3, d0, d1);
                        this.setYawPitch(this.yaw, this.pitch);
                    }
                } else {
                    d3 = this.a - this.locX;
                    d0 = this.b - this.locY;
                    d1 = this.c - this.locZ;
                    d2 = d3 * d3 + d0 * d0 + d1 * d1;
                    double d4;

                    if (this.target != null) {
                        this.a = this.target.locX;
                        this.c = this.target.locZ;
                        double d5 = this.a - this.locX;
                        double d6 = this.c - this.locZ;
                        double d7 = Math.sqrt(d5 * d5 + d6 * d6);

                        d4 = 0.4000000059604645D + d7 / 80.0D - 1.0D;
                        if (d4 > 10.0D) {
                            d4 = 10.0D;
                        }

                        this.b = this.target.getBoundingBox().b + d4;
                    } else {
                        this.a += this.random.nextGaussian() * 2.0D;
                        this.c += this.random.nextGaussian() * 2.0D;
                    }

                    if (this.bw || d2 < 100.0D || d2 > 22500.0D || this.positionChanged || this.E) {
                        this.cf();
                    }

                    d0 /= (double) MathHelper.sqrt(d3 * d3 + d1 * d1);
                    f3 = 0.6F;
                    d0 = MathHelper.a(d0, (double) (-f3), (double) f3);
                    this.motY += d0 * 0.10000000149011612D;
                    this.yaw = MathHelper.g(this.yaw);
                    double d8 = 180.0D - MathHelper.b(d3, d1) * 180.0D / 3.1415927410125732D;
                    double d9 = MathHelper.g(d8 - (double) this.yaw);

                    if (d9 > 50.0D) {
                        d9 = 50.0D;
                    }

                    if (d9 < -50.0D) {
                        d9 = -50.0D;
                    }

                    Vec3D vec3d = (new Vec3D(this.a - this.locX, this.b - this.locY, this.c - this.locZ)).a();

                    d4 = (double) (-MathHelper.cos(this.yaw * 3.1415927F / 180.0F));
                    Vec3D vec3d1 = (new Vec3D((double) MathHelper.sin(this.yaw * 3.1415927F / 180.0F), this.motY, d4)).a();
                    float f4 = ((float) vec3d1.b(vec3d) + 0.5F) / 1.5F;

                    if (f4 < 0.0F) {
                        f4 = 0.0F;
                    }

                    this.bb *= 0.8F;
                    float f5 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0F + 1.0F;
                    double d10 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 1.0D + 1.0D;

                    if (d10 > 40.0D) {
                        d10 = 40.0D;
                    }

                    this.bb = (float) ((double) this.bb + d9 * (0.699999988079071D / d10 / (double) f5));
                    this.yaw += this.bb * 0.1F;
                    float f6 = (float) (2.0D / (d10 + 1.0D));
                    float f7 = 0.06F;

                    this.a(0.0F, -1.0F, f7 * (f4 * f6 + (1.0F - f6)));
                    if (this.bx) {
                        this.move(this.motX * 0.800000011920929D, this.motY * 0.800000011920929D, this.motZ * 0.800000011920929D);
                    } else {
                        this.move(this.motX, this.motY, this.motZ);
                    }

                    Vec3D vec3d2 = (new Vec3D(this.motX, this.motY, this.motZ)).a();
                    float f8 = ((float) vec3d2.b(vec3d1) + 1.0F) / 2.0F;

                    f8 = 0.8F + 0.15F * f8;
                    this.motX *= (double) f8;
                    this.motZ *= (double) f8;
                    this.motY *= 0.9100000262260437D;
                }

                this.aI = this.yaw;
                this.bn.width = this.bn.length = 3.0F;
                this.bp.width = this.bp.length = 2.0F;
                this.bq.width = this.bq.length = 2.0F;
                this.br.width = this.br.length = 2.0F;
                this.bo.length = 3.0F;
                this.bo.width = 5.0F;
                this.bs.length = 2.0F;
                this.bs.width = 4.0F;
                this.bt.length = 3.0F;
                this.bt.width = 4.0F;
                f1 = (float) (this.b(5, 1.0F)[1] - this.b(10, 1.0F)[1]) * 10.0F / 180.0F * 3.1415927F;
                f2 = MathHelper.cos(f1);
                float f9 = -MathHelper.sin(f1);
                float f10 = this.yaw * 3.1415927F / 180.0F;
                float f11 = MathHelper.sin(f10);
                float f12 = MathHelper.cos(f10);

                this.bo.t_();
                this.bo.setPositionRotation(this.locX + (double) (f11 * 0.5F), this.locY, this.locZ - (double) (f12 * 0.5F), 0.0F, 0.0F);
                this.bs.t_();
                this.bs.setPositionRotation(this.locX + (double) (f12 * 4.5F), this.locY + 2.0D, this.locZ + (double) (f11 * 4.5F), 0.0F, 0.0F);
                this.bt.t_();
                this.bt.setPositionRotation(this.locX - (double) (f12 * 4.5F), this.locY + 2.0D, this.locZ - (double) (f11 * 4.5F), 0.0F, 0.0F);
                if (!this.world.isClientSide && this.hurtTicks == 0) {
                    this.a(this.world.getEntities(this, this.bs.getBoundingBox().grow(4.0D, 2.0D, 4.0D).c(0.0D, -2.0D, 0.0D)));
                    this.a(this.world.getEntities(this, this.bt.getBoundingBox().grow(4.0D, 2.0D, 4.0D).c(0.0D, -2.0D, 0.0D)));
                    this.b(this.world.getEntities(this, this.bn.getBoundingBox().grow(1.0D, 1.0D, 1.0D)));
                }

                double[] adouble = this.b(5, 1.0F);
                double[] adouble1 = this.b(0, 1.0F);

                f3 = MathHelper.sin(this.yaw * 3.1415927F / 180.0F - this.bb * 0.01F);
                float f13 = MathHelper.cos(this.yaw * 3.1415927F / 180.0F - this.bb * 0.01F);

                this.bn.t_();
                this.bn.setPositionRotation(this.locX + (double) (f3 * 5.5F * f2), this.locY + (adouble1[1] - adouble[1]) * 1.0D + (double) (f9 * 5.5F), this.locZ - (double) (f13 * 5.5F * f2), 0.0F, 0.0F);

                for (int j = 0; j < 3; ++j) {
                    EntityComplexPart entitycomplexpart = null;

                    if (j == 0) {
                        entitycomplexpart = this.bp;
                    }

                    if (j == 1) {
                        entitycomplexpart = this.bq;
                    }

                    if (j == 2) {
                        entitycomplexpart = this.br;
                    }

                    double[] adouble2 = this.b(12 + j * 2, 1.0F);
                    float f14 = this.yaw * 3.1415927F / 180.0F + this.b(adouble2[0] - adouble[0]) * 3.1415927F / 180.0F * 1.0F;
                    float f15 = MathHelper.sin(f14);
                    float f16 = MathHelper.cos(f14);
                    float f17 = 1.5F;
                    float f18 = (float) (j + 1) * 2.0F;

                    entitycomplexpart.t_();
                    entitycomplexpart.setPositionRotation(this.locX - (double) ((f11 * f17 + f15 * f18) * f2), this.locY + (adouble2[1] - adouble[1]) * 1.0D - (double) ((f18 + f17) * f9) + 1.5D, this.locZ + (double) ((f12 * f17 + f16 * f18) * f2), 0.0F, 0.0F);
                }

                if (!this.world.isClientSide) {
                    this.bx = this.b(this.bn.getBoundingBox()) | this.b(this.bo.getBoundingBox());
                }

            }
        }
    }

    private void n() {
        if (this.bz != null) {
            if (this.bz.dead) {
                if (!this.world.isClientSide) {
                    CraftEventFactory.entityDamage = this.bz; // CraftBukkit
                    this.a(this.bn, DamageSource.explosion((Explosion) null), 10.0F);
                    CraftEventFactory.entityDamage = null; // CraftBukkit
                }

                this.bz = null;
            } else if (this.ticksLived % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                // CraftBukkit start
                EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), 1.0D, EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL);
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.setHealth((float) (this.getHealth() + event.getAmount()));
                }
                // CraftBukkit end
            }
        }

        if (this.random.nextInt(10) == 0) {
            float f = 32.0F;
            List list = this.world.a(EntityEnderCrystal.class, this.getBoundingBox().grow((double) f, (double) f, (double) f));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal) iterator.next();
                double d1 = entityendercrystal1.h(this);

                if (d1 < d0) {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }

            this.bz = entityendercrystal;
        }

    }

    private void a(List<Entity> list) {
        double d0 = (this.bo.getBoundingBox().a + this.bo.getBoundingBox().d) / 2.0D;
        double d1 = (this.bo.getBoundingBox().c + this.bo.getBoundingBox().f) / 2.0D;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLiving) {
                double d2 = entity.locX - d0;
                double d3 = entity.locZ - d1;
                double d4 = d2 * d2 + d3 * d3;

                entity.g(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
            }
        }

    }

    private void b(List<Entity> list) {
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);

            if (entity instanceof EntityLiving) {
                entity.damageEntity(DamageSource.mobAttack(this), 10.0F);
                this.a((EntityLiving) this, entity);
            }
        }

    }

    private void cf() {
        this.bw = false;
        ArrayList arraylist = Lists.newArrayList(this.world.players);
        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            if (((EntityHuman) iterator.next()).isSpectator()) {
                iterator.remove();
            }
        }

        if (this.random.nextInt(2) == 0 && !arraylist.isEmpty()) {
            // CraftBukkit start
            Entity target = (Entity) this.world.players.get(this.random.nextInt(this.world.players.size()));
            EntityTargetEvent event = new EntityTargetEvent(this.getBukkitEntity(), target.getBukkitEntity(), EntityTargetEvent.TargetReason.RANDOM_TARGET);
            this.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (event.getTarget() == null) {
                    this.target = null;
                } else {
                    this.target = ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();
                }
            }
            // CraftBukkit end
        } else {
            boolean flag;

            do {
                this.a = 0.0D;
                this.b = (double) (70.0F + this.random.nextFloat() * 50.0F);
                this.c = 0.0D;
                this.a += (double) (this.random.nextFloat() * 120.0F - 60.0F);
                this.c += (double) (this.random.nextFloat() * 120.0F - 60.0F);
                double d0 = this.locX - this.a;
                double d1 = this.locY - this.b;
                double d2 = this.locZ - this.c;

                flag = d0 * d0 + d1 * d1 + d2 * d2 > 100.0D;
            } while (!flag);

            this.target = null;
        }

    }

    private float b(double d0) {
        return (float) MathHelper.g(d0);
    }

    private boolean b(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.b);
        int k = MathHelper.floor(axisalignedbb.c);
        int l = MathHelper.floor(axisalignedbb.d);
        int i1 = MathHelper.floor(axisalignedbb.e);
        int j1 = MathHelper.floor(axisalignedbb.f);
        boolean flag = false;
        boolean flag1 = false;

        // CraftBukkit start - Create a list to hold all the destroyed blocks
        List<org.bukkit.block.Block> destroyedBlocks = new java.util.ArrayList<org.bukkit.block.Block>();
        org.bukkit.craftbukkit.CraftWorld craftWorld = this.world.getWorld();
        // CraftBukkit end

        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    BlockPosition blockposition = new BlockPosition(k1, l1, i2);
                    Block block = this.world.getType(blockposition).getBlock();

                    if (block.getMaterial() != Material.AIR) {
                        if (block != Blocks.BARRIER && block != Blocks.OBSIDIAN && block != Blocks.END_STONE && block != Blocks.BEDROCK && block != Blocks.COMMAND_BLOCK && this.world.getGameRules().getBoolean("mobGriefing")) {
                            // CraftBukkit start - Add blocks to list rather than destroying them
                            // flag1 = this.world.setAir(new BlockPosition(blockposition)) || flag1;
                            flag1 = true;
                            destroyedBlocks.add(craftWorld.getBlockAt(k1, l1, i2));
                            // CraftBukkit end
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            // CraftBukkit start - Set off an EntityExplodeEvent for the dragon exploding all these blocks
            org.bukkit.entity.Entity bukkitEntity = this.getBukkitEntity();
            EntityExplodeEvent event = new EntityExplodeEvent(bukkitEntity, bukkitEntity.getLocation(), destroyedBlocks, 0F);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                // This flag literally means 'Dragon hit something hard' (Obsidian, White Stone or Bedrock) and will cause the dragon to slow down.
                // We should consider adding an event extension for it, or perhaps returning true if the event is cancelled.
                return flag;
            } else if (event.getYield() == 0F) {
                // Yield zero ==> no drops
                for (org.bukkit.block.Block block : event.blockList()) {
                    this.world.setAir(new BlockPosition(block.getX(), block.getY(), block.getZ()));
                }
            } else {
                for (org.bukkit.block.Block block : event.blockList()) {
                    org.bukkit.Material blockId = block.getType();
                    if (blockId == org.bukkit.Material.AIR) {
                        continue;
                    }

                    int blockX = block.getX();
                    int blockY = block.getY();
                    int blockZ = block.getZ();

                    Block nmsBlock = org.bukkit.craftbukkit.util.CraftMagicNumbers.getBlock(blockId);
                    if (nmsBlock.a(explosionSource)) {
                        nmsBlock.dropNaturally(this.world, new BlockPosition(blockX, blockY, blockZ), nmsBlock.fromLegacyData(block.getData()), event.getYield(), 0);
                    }
                    nmsBlock.wasExploded(world, new BlockPosition(blockX, blockY, blockZ), explosionSource);

                    this.world.setAir(new BlockPosition(blockX, blockY, blockZ));
                }
            }
            // CraftBukkit end
            double d0 = axisalignedbb.a + (axisalignedbb.d - axisalignedbb.a) * (double) this.random.nextFloat();
            double d1 = axisalignedbb.b + (axisalignedbb.e - axisalignedbb.b) * (double) this.random.nextFloat();
            double d2 = axisalignedbb.c + (axisalignedbb.f - axisalignedbb.c) * (double) this.random.nextFloat();

            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        }

        return flag;
    }

    public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, float f) {
        if (entitycomplexpart != this.bn) {
            f = f / 4.0F + 1.0F;
        }

        float f1 = this.yaw * 3.1415927F / 180.0F;
        float f2 = MathHelper.sin(f1);
        float f3 = MathHelper.cos(f1);

        this.a = this.locX + (double) (f2 * 5.0F) + (double) ((this.random.nextFloat() - 0.5F) * 2.0F);
        this.b = this.locY + (double) (this.random.nextFloat() * 3.0F) + 1.0D;
        this.c = this.locZ - (double) (f3 * 5.0F) + (double) ((this.random.nextFloat() - 0.5F) * 2.0F);
        this.target = null;
        if (damagesource.getEntity() instanceof EntityHuman || damagesource.isExplosion()) {
            this.dealDamage(damagesource, f);
        }

        return true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).w()) {
            this.dealDamage(damagesource, f);
        }

        return false;
    }

    protected boolean dealDamage(DamageSource damagesource, float f) {
        return super.damageEntity(damagesource, f);
    }

    public void G() {
        this.die();
    }

    protected void aZ() {
        if (this.dead) return; // CraftBukkit - can't kill what's already dead
        ++this.by;
        if (this.by >= 180 && this.by <= 200) {
            float f = (this.random.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;

            this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.locX + (double) f, this.locY + 2.0D + (double) f1, this.locZ + (double) f2, 0.0D, 0.0D, 0.0D, new int[0]);
        }

        boolean flag = this.world.getGameRules().getBoolean("doMobLoot");
        int i;
        int j;

        if (!this.world.isClientSide) {
            if (this.by > 150 && this.by % 5 == 0 && flag) {
                i = this.expToDrop / 12; // CraftBukkit - drop experience as dragon falls from sky. use experience drop from death event. This is now set in getExpReward()

                while (i > 0) {
                    j = EntityExperienceOrb.getOrbValue(i);
                    i -= j;
                    this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
                }
            }

            if (this.by == 1) {
                // CraftBukkit start - Use relative location for far away sounds
                // this.world.a(1018, new BlockPosition(this), 0);
                int viewDistance = ((WorldServer) this.world).getServer().getViewDistance() * 16;
                for (EntityPlayer player : (List<EntityPlayer>) MinecraftServer.getServer().getPlayerList().players) {
                    double deltaX = this.locX - player.locX;
                    double deltaZ = this.locZ - player.locZ;
                    double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
                    if ( world.spigotConfig.dragonDeathSoundRadius > 0 && distanceSquared > world.spigotConfig.dragonDeathSoundRadius * world.spigotConfig.dragonDeathSoundRadius ) continue; // Spigot
                    if (distanceSquared > viewDistance * viewDistance) {
                        double deltaLength = Math.sqrt(distanceSquared);
                        double relativeX = player.locX + (deltaX / deltaLength) * viewDistance;
                        double relativeZ = player.locZ + (deltaZ / deltaLength) * viewDistance;
                        player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1018, new BlockPosition((int) relativeX, (int) this.locY, (int) relativeZ), 0, true));
                    } else {
                        player.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1018, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0, true));
                    }
                }
                // CraftBukkit end
            }
        }

        this.move(0.0D, 0.10000000149011612D, 0.0D);
        this.aI = this.yaw += 20.0F;
        if (this.by == 200 && !this.world.isClientSide) {
            if (flag) {
                i = this.expToDrop - (10 * this.expToDrop / 12); // CraftBukkit - drop the remaining experience

                while (i > 0) {
                    j = EntityExperienceOrb.getOrbValue(i);
                    i -= j;
                    this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
                }
            }

            this.a(new BlockPosition(this.locX, 64.0D, this.locZ));
            this.die();
        }

    }

    private void a(BlockPosition blockposition) {
        boolean flag = true;
        double d0 = 12.25D;
        double d1 = 6.25D;

        // CraftBukkit start - Replace any "this.world" in the following with just "world"!
        BlockStateListPopulator world = new BlockStateListPopulator(this.world.getWorld());

        for (int i = -1; i <= 32; ++i) {
            for (int j = -4; j <= 4; ++j) {
                for (int k = -4; k <= 4; ++k) {
                    double d2 = (double) (j * j + k * k);

                    if (d2 <= 12.25D) {
                        BlockPosition blockposition1 = blockposition.a(j, i, k);

                        if (i < 0) {
                            if (d2 <= 6.25D) {
                                world.setTypeUpdate(blockposition1, Blocks.BEDROCK.getBlockData());
                            }
                        } else if (i > 0) {
                            world.setTypeUpdate(blockposition1, Blocks.AIR.getBlockData());
                        } else if (d2 > 6.25D) {
                            world.setTypeUpdate(blockposition1, Blocks.BEDROCK.getBlockData());
                        } else {
                            world.setTypeUpdate(blockposition1, Blocks.END_PORTAL.getBlockData());
                        }
                    }
                }
            }
        }

        world.setTypeUpdate(blockposition, Blocks.BEDROCK.getBlockData());
        world.setTypeUpdate(blockposition.up(), Blocks.BEDROCK.getBlockData());
        BlockPosition blockposition2 = blockposition.up(2);

        world.setTypeUpdate(blockposition2, Blocks.BEDROCK.getBlockData());
        world.setTypeUpdate(blockposition2.west(), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.EAST));
        world.setTypeUpdate(blockposition2.east(), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.WEST));
        world.setTypeUpdate(blockposition2.north(), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.SOUTH));
        world.setTypeUpdate(blockposition2.south(), Blocks.TORCH.getBlockData().set(BlockTorch.FACING, EnumDirection.NORTH));
        world.setTypeUpdate(blockposition.up(3), Blocks.BEDROCK.getBlockData());
        world.setTypeUpdate(blockposition.up(4), Blocks.DRAGON_EGG.getBlockData());

        EntityCreatePortalEvent event = new EntityCreatePortalEvent((org.bukkit.entity.LivingEntity) this.getBukkitEntity(), java.util.Collections.unmodifiableList(world.getList()), org.bukkit.PortalType.ENDER);
        this.world.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            for (BlockState state : event.getBlocks()) {
                state.update(true);
            }
        } else {
            for (BlockState state : event.getBlocks()) {
                PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(this.world, new BlockPosition(state.getX(), state.getY(), state.getZ()));
                for (Iterator it = this.world.players.iterator(); it.hasNext();) {
                    EntityHuman entity = (EntityHuman) it.next();
                    if (entity instanceof EntityPlayer) {
                        ((EntityPlayer) entity).playerConnection.sendPacket(packet);
                    }
                }
            }
        }
        // CraftBukkit end
    }

    protected void D() {}

    public Entity[] aB() {
        return this.children;
    }

    public boolean ad() {
        return false;
    }

    public World a() {
        return this.world;
    }

    protected String z() {
        return "mob.enderdragon.growl";
    }

    protected String bo() {
        return "mob.enderdragon.hit";
    }

    protected float bB() {
        return 5.0F;
    }

    // CraftBukkit start
    public int getExpReward() {
        // This value is equal to the amount of experience dropped while falling from the sky (10 * 1000)
        // plus what is dropped when the dragon hits the ground (2000)
        return 12000;
    }
    // CraftBukkit end
}
