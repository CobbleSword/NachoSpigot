package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
// CraftBukkit end
// TacoSpigot start
import net.techcable.tacospigot.event.entity.ArrowCollideEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
// TacoSpigot end

public class EntityArrow extends Entity implements IProjectile {

    private int d = -1;
    private int e = -1;
    private int f = -1;
    private Block g;
    private int h;
    public boolean inGround = false; // Spigot - private -> public
    public int fromPlayer;
    public int shake;
    public Entity shooter;
    private int ar;
    private int as;
    private double damage = 2.0D;
    public int knockbackStrength;

    // Spigot Start
    @Override
    public void inactiveTick()
    {
        if ( this.inGround )
        {
            this.ar += 1; // Despawn counter. First int after shooter
        }
        super.inactiveTick();
    }
    // Spigot End

    public EntityArrow(World world) {
        super(world);
        this.j = 10.0D;
        this.setSize(0.5F, 0.5F);
    }

    public EntityArrow(World world, double d0, double d1, double d2) {
        super(world);
        this.j = 10.0D;
        this.setSize(0.5F, 0.5F);
        this.setPosition(d0, d1, d2);
    }

    public EntityArrow(World world, EntityLiving entityliving, EntityLiving entityliving1, float f, float f1) {
        super(world);
        this.j = 10.0D;
        this.shooter = entityliving;
        this.projectileSource = (LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
        if (entityliving instanceof EntityHuman) {
            this.fromPlayer = 1;
        }

        this.locY = entityliving.locY + (double) entityliving.getHeadHeight() - 0.10000000149011612D;
        double d0 = entityliving1.locX - entityliving.locX;
        double d1 = entityliving1.getBoundingBox().b + (double) (entityliving1.length / 3.0F) - this.locY;
        double d2 = entityliving1.locZ - entityliving.locZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        if (d3 >= 1.0E-7D) {
            float f2 = (float) (MathHelper.b(d2, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
            float f3 = (float) (-(MathHelper.b(d1, d3) * 180.0D / 3.1415927410125732D));
            double d4 = d0 / d3;
            double d5 = d2 / d3;

            this.setPositionRotation(entityliving.locX + d4, this.locY, entityliving.locZ + d5, f2, f3);
            float f4 = (float) (d3 * 0.20000000298023224D);

            this.shoot(d0, d1 + (double) f4, d2, f, f1);
        }
    }

    public EntityArrow(World world, EntityLiving entityliving, float f) {
        super(world);
        this.j = 10.0D;
        this.shooter = entityliving;
        this.projectileSource = (LivingEntity) entityliving.getBukkitEntity(); // CraftBukkit
        if (entityliving instanceof EntityHuman) {
            this.fromPlayer = 1;
        }

        this.setSize(0.5F, 0.5F);
        this.setPositionRotation(entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight(), entityliving.locZ, entityliving.yaw, entityliving.pitch);
        this.locX -= (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.locY -= 0.10000000149011612D;
        this.locZ -= (double) (MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.motX = (double) (-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.motZ = (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.motY = (double) (-MathHelper.sin(this.pitch / 180.0F * 3.1415927F));
        this.shoot(this.motX, this.motY, this.motZ, f * 1.5F, 1.0F);
    }

    protected void h() {
        this.datawatcher.a(16, Byte.valueOf((byte) 0));
    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        d0 /= (double) f2;
        d1 /= (double) f2;
        d2 /= (double) f2;
        d0 += this.random.nextGaussian() * (double) (this.random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) f1;
        d1 += this.random.nextGaussian() * (double) (this.random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) f1;
        d2 += this.random.nextGaussian() * (double) (this.random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) f1;
        d0 *= (double) f;
        d1 *= (double) f;
        d2 *= (double) f;
        this.motX = d0;
        this.motY = d1;
        this.motZ = d2;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

        this.lastYaw = this.yaw = (float) (MathHelper.b(d0, d2) * 180.0D / 3.1415927410125732D);
        this.lastPitch = this.pitch = (float) (MathHelper.b(d1, (double) f3) * 180.0D / 3.1415927410125732D);
        this.ar = 0;
    }

    public void t_() {
        super.t_();
        if (this.lastPitch == 0.0F && this.lastYaw == 0.0F) {
            float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

            this.lastYaw = this.yaw = (float) (MathHelper.b(this.motX, this.motZ) * 180.0D / 3.1415927410125732D);
            this.lastPitch = this.pitch = (float) (MathHelper.b(this.motY, (double) f) * 180.0D / 3.1415927410125732D);
        }

        BlockPosition blockposition = new BlockPosition(this.d, this.e, this.f);
        IBlockData iblockdata = this.world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (block.getMaterial() != Material.AIR) {
            block.updateShape(this.world, blockposition);
            AxisAlignedBB axisalignedbb = block.a(this.world, blockposition, iblockdata);

            if (axisalignedbb != null && axisalignedbb.a(new Vec3D(this.locX, this.locY, this.locZ))) {
                this.inGround = true;
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            int i = block.toLegacyData(iblockdata);

            if (block == this.g && i == this.h) {
                ++this.ar;
                if (this.ar >= world.spigotConfig.arrowDespawnRate) { // Spigot - First int after shooter
                    this.die();
                }
            } else {
                this.inGround = false;
                this.motX *= (double) (this.random.nextFloat() * 0.2F);
                this.motY *= (double) (this.random.nextFloat() * 0.2F);
                this.motZ *= (double) (this.random.nextFloat() * 0.2F);
                this.ar = 0;
                this.as = 0;
            }

        } else {
            ++this.as;
            Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
            Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, false, true, false);

            vec3d = new Vec3D(this.locX, this.locY, this.locZ);
            vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            if (movingobjectposition != null) {
                vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
            }

            Entity entity = null;
            List list = this.world.getEntities(this, this.getBoundingBox().a(this.motX, this.motY, this.motZ).grow(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;

            int j;
            float f1;

            for (j = 0; j < list.size(); ++j) {
                Entity entity1 = (Entity) list.get(j);

                if (entity1.ad() && (entity1 != this.shooter || this.as >= 5)) {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.getBoundingBox().grow((double) f1, (double) f1, (double) f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.a(vec3d, vec3d1);

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

            if (movingobjectposition != null && movingobjectposition.entity != null && movingobjectposition.entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) movingobjectposition.entity;

                if (entityhuman.abilities.isInvulnerable || this.shooter instanceof EntityHuman && !((EntityHuman) this.shooter).a(entityhuman)) {
                    movingobjectposition = null;
                }
            }

            float f2;
            float f3;

            // PaperSpigot start - Allow arrows to fly through vanished players the shooter can't see
            if (movingobjectposition != null && movingobjectposition.entity instanceof EntityPlayer && shooter != null && shooter instanceof EntityPlayer) {
                if (!((EntityPlayer) shooter).getBukkitEntity().canSee(((EntityPlayer) movingobjectposition.entity).getBukkitEntity())) {
                    movingobjectposition = null;
                }
            }
            // PaperSpigot end
            // TacoSpigot start - call better event
            if (movingobjectposition != null && movingobjectposition.entity != null) {
                ArrowCollideEvent collideEvent = new ArrowCollideEvent((Arrow) getBukkitEntity(), movingobjectposition.entity.getBukkitEntity());
                Bukkit.getPluginManager().callEvent(collideEvent);
                if (collideEvent.isCancelled()) movingobjectposition = null;
            }
            // TacoSpigot end
            if (movingobjectposition != null) {
                org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this); // CraftBukkit - Call event
                if (movingobjectposition.entity != null) {
                    f2 = MathHelper.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);
                    int k = MathHelper.f((double) f2 * this.damage);

                    if (this.isCritical()) {
                        k += this.random.nextInt(k / 2 + 2);
                    }

                    DamageSource damagesource;

                    if (this.shooter == null) {
                        damagesource = DamageSource.arrow(this, this);
                    } else {
                        damagesource = DamageSource.arrow(this, this.shooter);
                    }

                    // CraftBukkit start - Moved damage call
                    if (movingobjectposition.entity.damageEntity(damagesource, (float) k)) {
                    if (this.isBurning() && !(movingobjectposition.entity instanceof EntityEnderman) && (!(movingobjectposition.entity instanceof EntityPlayer) || !(this.shooter instanceof EntityPlayer) || this.world.pvpMode)) { // CraftBukkit - abide by pvp setting if destination is a player
                        EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), 5);
                        org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);
                        if (!combustEvent.isCancelled()) {
                            movingobjectposition.entity.setOnFire(combustEvent.getDuration());
                        }
                        // CraftBukkit end
                    }

                    // if (movingobjectposition.entity.damageEntity(damagesource, (float) k)) { // CraftBukkit - moved up
                        if (movingobjectposition.entity instanceof EntityLiving) {
                            EntityLiving entityliving = (EntityLiving) movingobjectposition.entity;

                            if (!this.world.isClientSide) {
                                entityliving.o(entityliving.bv() + 1);
                            }

                            if (this.knockbackStrength > 0) {
                                f3 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                                if (f3 > 0.0F) {
                                    movingobjectposition.entity.g(this.motX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f3, 0.1D, this.motZ * (double) this.knockbackStrength * 0.6000000238418579D / (double) f3);
                                }
                            }

                            if (this.shooter instanceof EntityLiving) {
                                EnchantmentManager.a(entityliving, this.shooter);
                                EnchantmentManager.b((EntityLiving) this.shooter, entityliving);
                            }

                            if (this.shooter != null && movingobjectposition.entity != this.shooter && movingobjectposition.entity instanceof EntityHuman && this.shooter instanceof EntityPlayer) {
                                ((EntityPlayer) this.shooter).playerConnection.sendPacket(new PacketPlayOutGameStateChange(6, 0.0F));
                            }
                        }

                        this.makeSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                        if (!(movingobjectposition.entity instanceof EntityEnderman)) {
                            this.die();
                        }
                    } else {
                        this.motX *= -0.10000000149011612D;
                        this.motY *= -0.10000000149011612D;
                        this.motZ *= -0.10000000149011612D;
                        this.yaw += 180.0F;
                        this.lastYaw += 180.0F;
                        this.as = 0;
                    }
                } else {
                    BlockPosition blockposition1 = movingobjectposition.a();

                    this.d = blockposition1.getX();
                    this.e = blockposition1.getY();
                    this.f = blockposition1.getZ();
                    IBlockData iblockdata1 = this.world.getType(blockposition1);

                    this.g = iblockdata1.getBlock();
                    this.h = this.g.toLegacyData(iblockdata1);
                    this.motX = (double) ((float) (movingobjectposition.pos.a - this.locX));
                    this.motY = (double) ((float) (movingobjectposition.pos.b - this.locY));
                    this.motZ = (double) ((float) (movingobjectposition.pos.c - this.locZ));
                    f1 = MathHelper.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);
                    this.locX -= this.motX / (double) f1 * 0.05000000074505806D;
                    this.locY -= this.motY / (double) f1 * 0.05000000074505806D;
                    this.locZ -= this.motZ / (double) f1 * 0.05000000074505806D;
                    this.makeSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.shake = 7;
                    this.setCritical(false);
                    if (this.g.getMaterial() != Material.AIR) {
                        this.g.a(this.world, blockposition1, iblockdata1, (Entity) this);
                    }
                }
            }

            if (this.isCritical()) {
                for (j = 0; j < 4; ++j) {
                    this.world.addParticle(EnumParticle.CRIT, this.locX + this.motX * (double) j / 4.0D, this.locY + this.motY * (double) j / 4.0D, this.locZ + this.motZ * (double) j / 4.0D, -this.motX, -this.motY + 0.2D, -this.motZ, new int[0]);
                }
            }

            this.locX += this.motX;
            this.locY += this.motY;
            this.locZ += this.motZ;
            f2 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
            this.yaw = (float) (MathHelper.b(this.motX, this.motZ) * 180.0D / 3.1415927410125732D);

            for (this.pitch = (float) (MathHelper.b(this.motY, (double) f2) * 180.0D / 3.1415927410125732D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
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
            float f4 = 0.99F;

            f1 = 0.05F;
            if (this.V()) {
                for (int l = 0; l < 4; ++l) {
                    f3 = 0.25F;
                    this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX - this.motX * (double) f3, this.locY - this.motY * (double) f3, this.locZ - this.motZ * (double) f3, this.motX, this.motY, this.motZ, new int[0]);
                }

                f4 = 0.6F;
            }

            if (this.U()) {
                this.extinguish();
            }

            this.motX *= (double) f4;
            this.motY *= (double) f4;
            this.motZ *= (double) f4;
            this.motY -= (double) f1;
            this.setPosition(this.locX, this.locY, this.locZ);
            this.checkBlockCollisions();
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("xTile", (short) this.d);
        nbttagcompound.setShort("yTile", (short) this.e);
        nbttagcompound.setShort("zTile", (short) this.f);
        nbttagcompound.setShort("life", (short) this.ar);
        MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.c(this.g);

        nbttagcompound.setString("inTile", minecraftkey == null ? "" : minecraftkey.toString());
        nbttagcompound.setByte("inData", (byte) this.h);
        nbttagcompound.setByte("shake", (byte) this.shake);
        nbttagcompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        nbttagcompound.setByte("pickup", (byte) this.fromPlayer);
        nbttagcompound.setDouble("damage", this.damage);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.d = nbttagcompound.getShort("xTile");
        this.e = nbttagcompound.getShort("yTile");
        this.f = nbttagcompound.getShort("zTile");
        this.ar = nbttagcompound.getShort("life");
        if (nbttagcompound.hasKeyOfType("inTile", 8)) {
            this.g = Block.getByName(nbttagcompound.getString("inTile"));
        } else {
            this.g = Block.getById(nbttagcompound.getByte("inTile") & 255);
        }

        this.h = nbttagcompound.getByte("inData") & 255;
        this.shake = nbttagcompound.getByte("shake") & 255;
        this.inGround = nbttagcompound.getByte("inGround") == 1;
        if (nbttagcompound.hasKeyOfType("damage", 99)) {
            this.damage = nbttagcompound.getDouble("damage");
        }

        if (nbttagcompound.hasKeyOfType("pickup", 99)) {
            this.fromPlayer = nbttagcompound.getByte("pickup");
        } else if (nbttagcompound.hasKeyOfType("player", 99)) {
            this.fromPlayer = nbttagcompound.getBoolean("player") ? 1 : 0;
        }

    }

    public void d(EntityHuman entityhuman) {
        if (!this.world.isClientSide && this.inGround && this.shake <= 0) {
            // CraftBukkit start
            ItemStack itemstack = new ItemStack(Items.ARROW);
            if (this.fromPlayer == 1 && entityhuman.inventory.canHold(itemstack) > 0) {
                EntityItem item = new EntityItem(this.world, this.locX, this.locY, this.locZ, itemstack);

                PlayerPickupItemEvent event = new PlayerPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), new org.bukkit.craftbukkit.entity.CraftItem(this.world.getServer(), this, item), 0);
                // event.setCancelled(!entityhuman.canPickUpLoot); TODO
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
            }
            // CraftBukkit end
            boolean flag = this.fromPlayer == 1 || this.fromPlayer == 2 && entityhuman.abilities.canInstantlyBuild;

            if (this.fromPlayer == 1 && !entityhuman.inventory.pickup(new ItemStack(Items.ARROW, 1))) {
                flag = false;
            }

            if (flag) {
                this.makeSound("random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityhuman.receive(this, 1);
                this.die();
            }

        }
    }

    protected boolean s_() {
        return false;
    }

    public void b(double d0) {
        this.damage = d0;
    }

    public double j() {
        return this.damage;
    }

    public void setKnockbackStrength(int i) {
        this.knockbackStrength = i;
    }

    public boolean aD() {
        return false;
    }

    public float getHeadHeight() {
        return 0.0F;
    }

    public void setCritical(boolean flag) {
        byte b0 = this.datawatcher.getByte(16);

        if (flag) {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 | 1)));
        } else {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 & -2)));
        }

    }

    public boolean isCritical() {
        byte b0 = this.datawatcher.getByte(16);

        return (b0 & 1) != 0;
    }

    // CraftBukkit start
    public boolean isInGround() {
        return inGround;
    }
    // CraftBukkit end
}
