package net.minecraft.server;

import java.util.List;
import java.util.UUID;

public abstract class EntityProjectile extends Entity implements IProjectile {

    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private Block inBlockId;
    protected boolean inGround;
    public int shake;
    public EntityLiving shooter;
    public String shooterName;
    private int i;
    private int ar;

    public EntityProjectile(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    protected void h() {}

    public EntityProjectile(World world, EntityLiving entityliving) {
        super(world);
        this.shooter = entityliving;
        this.projectileSource = (org.bukkit.entity.LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
        this.setSize(0.25F, 0.25F);
        this.setPositionRotation(entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight(), entityliving.locZ, entityliving.yaw, entityliving.pitch);
        this.locX -= (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.locY -= 0.10000000149011612D;
        this.locZ -= (double) (MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.setPosition(this.locX, this.locY, this.locZ);
        float f = 0.4F;

        this.motX = (double) (-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
        this.motZ = (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
        this.motY = (double) (-MathHelper.sin((this.pitch + this.l()) / 180.0F * 3.1415927F) * f);
        this.shoot(this.motX, this.motY, this.motZ, this.j(), 1.0F);
    }

    public EntityProjectile(World world, double d0, double d1, double d2) {
        super(world);
        this.i = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(d0, d1, d2);
    }

    protected float j() {
        return 1.5F;
    }

    protected float l() {
        return 0.0F;
    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        d0 /= (double) f2;
        d1 /= (double) f2;
        d2 /= (double) f2;
        d0 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d1 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d2 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d0 *= (double) f;
        d1 *= (double) f;
        d2 *= (double) f;
        this.motX = d0;
        this.motY = d1;
        this.motZ = d2;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

        this.lastYaw = this.yaw = (float) (MathHelper.b(d0, d2) * 180.0D / 3.1415927410125732D);
        this.lastPitch = this.pitch = (float) (MathHelper.b(d1, (double) f3) * 180.0D / 3.1415927410125732D);
        this.i = 0;
    }

    public void t_() {
        this.P = this.locX;
        this.Q = this.locY;
        this.R = this.locZ;
        super.t_();
        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            if (this.world.getType(new BlockPosition(this.blockX, this.blockY, this.blockZ)).getBlock() == this.inBlockId) {
                ++this.i;
                if (this.i == 1200) {
                    this.die();
                }

                return;
            }

            this.inGround = false;
            this.motX *= (double) (this.random.nextFloat() * 0.2F);
            this.motY *= (double) (this.random.nextFloat() * 0.2F);
            this.motZ *= (double) (this.random.nextFloat() * 0.2F);
            this.i = 0;
            this.ar = 0;
        } else {
            ++this.ar;
        }

        Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1);

        vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        if (movingobjectposition != null) {
            vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
        }

        if (!this.world.isClientSide) {
            Entity entity = null;
            List list = this.world.getEntities(this, this.getBoundingBox().a(this.motX, this.motY, this.motZ).grow(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            EntityLiving entityliving = this.getShooter();

            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = (Entity) list.get(i);

                if (entity1.ad() && (entity1 != entityliving || this.ar >= 5)) {
                    float f = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double) f, (double) f, (double) f);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.a(vec3d, vec3d1);

                    if (movingobjectposition1 != null) {
                        double d1 = vec3d.distanceSquared(movingobjectposition1.pos);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }
        }

        // PaperSpigot start - Allow projectiles to fly through vanished players the shooter can't see
        if (movingobjectposition != null && movingobjectposition.entity instanceof EntityPlayer && shooter != null && shooter instanceof EntityPlayer) {
            if (!((EntityPlayer) shooter).getBukkitEntity().canSee(((EntityPlayer) movingobjectposition.entity).getBukkitEntity())) {
                movingobjectposition = null;
            }
        }
        // PaperSpigot end

        if (movingobjectposition != null) {
            if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK && this.world.getType(movingobjectposition.a()).getBlock() == Blocks.PORTAL) {
                this.d(movingobjectposition.a());
            } else {
                this.a(movingobjectposition);
                // CraftBukkit start
                if (this.dead) {
                    org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this);
                }
                // CraftBukkit end
            }
        }

        this.locX += this.motX;
        this.locY += this.motY;
        this.locZ += this.motZ;
        float f1 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        this.yaw = (float) (MathHelper.b(this.motX, this.motZ) * 180.0D / 3.1415927410125732D);

        for (this.pitch = (float) (MathHelper.b(this.motY, (double) f1) * 180.0D / 3.1415927410125732D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
            ;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
        this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
        float f2 = 0.99F;
        float f3 = this.m();

        if (this.V()) {
            for (int j = 0; j < 4; ++j) {
                float f4 = 0.25F;

                this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX - this.motX * (double) f4, this.locY - this.motY * (double) f4, this.locZ - this.motZ * (double) f4, this.motX, this.motY, this.motZ, new int[0]);
            }

            f2 = 0.8F;
        }

        this.motX *= (double) f2;
        this.motY *= (double) f2;
        this.motZ *= (double) f2;
        this.motY -= (double) f3;
        this.setPosition(this.locX, this.locY, this.locZ);
    }

    protected float m() {
        return 0.03F;
    }

    protected abstract void a(MovingObjectPosition movingobjectposition);

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("xTile", (short) this.blockX);
        nbttagcompound.setShort("yTile", (short) this.blockY);
        nbttagcompound.setShort("zTile", (short) this.blockZ);
        MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.c(this.inBlockId);

        nbttagcompound.setString("inTile", minecraftkey == null ? "" : minecraftkey.toString());
        nbttagcompound.setByte("shake", (byte) this.shake);
        nbttagcompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        if ((this.shooterName == null || this.shooterName.length() == 0) && this.shooter instanceof EntityHuman) {
            this.shooterName = this.shooter.getName();
        }

        nbttagcompound.setString("ownerName", this.shooterName == null ? "" : this.shooterName);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.blockX = nbttagcompound.getShort("xTile");
        this.blockY = nbttagcompound.getShort("yTile");
        this.blockZ = nbttagcompound.getShort("zTile");
        if (nbttagcompound.hasKeyOfType("inTile", 8)) {
            this.inBlockId = Block.getByName(nbttagcompound.getString("inTile"));
        } else {
            this.inBlockId = Block.getById(nbttagcompound.getByte("inTile") & 255);
        }

        this.shake = nbttagcompound.getByte("shake") & 255;
        this.inGround = nbttagcompound.getByte("inGround") == 1;
        this.shooter = null;
        this.shooterName = nbttagcompound.getString("ownerName");
        if (this.shooterName != null && this.shooterName.length() == 0) {
            this.shooterName = null;
        }

        this.shooter = this.getShooter();
    }

    public EntityLiving getShooter() {
        if (this.shooter == null && this.shooterName != null && this.shooterName.length() > 0) {
            this.shooter = this.world.a(this.shooterName);
            if (this.shooter == null && this.world instanceof WorldServer) {
                try {
                    Entity entity = ((WorldServer) this.world).getEntity(UUID.fromString(this.shooterName));

                    if (entity instanceof EntityLiving) {
                        this.shooter = (EntityLiving) entity;
                    }
                } catch (Throwable throwable) {
                    this.shooter = null;
                }
            }
        }

        return this.shooter;
    }
}
