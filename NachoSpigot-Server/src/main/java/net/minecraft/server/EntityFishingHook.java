package net.minecraft.server;

import java.util.Arrays;
import java.util.List;

// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.entity.Fish;
import org.bukkit.event.player.PlayerFishEvent;
// CraftBukkit end

public class EntityFishingHook extends Entity {

    private static final List<PossibleFishingResult> d = Arrays.asList(new PossibleFishingResult[] { (new PossibleFishingResult(new ItemStack(Items.LEATHER_BOOTS), 10)).a(0.9F), new PossibleFishingResult(new ItemStack(Items.LEATHER), 10), new PossibleFishingResult(new ItemStack(Items.BONE), 10), new PossibleFishingResult(new ItemStack(Items.POTION), 10), new PossibleFishingResult(new ItemStack(Items.STRING), 5), (new PossibleFishingResult(new ItemStack(Items.FISHING_ROD), 2)).a(0.9F), new PossibleFishingResult(new ItemStack(Items.BOWL), 10), new PossibleFishingResult(new ItemStack(Items.STICK), 5), new PossibleFishingResult(new ItemStack(Items.DYE, 10, EnumColor.BLACK.getInvColorIndex()), 1), new PossibleFishingResult(new ItemStack(Blocks.TRIPWIRE_HOOK), 10), new PossibleFishingResult(new ItemStack(Items.ROTTEN_FLESH), 10)});
    private static final List<PossibleFishingResult> e = Arrays.asList(new PossibleFishingResult[] { new PossibleFishingResult(new ItemStack(Blocks.WATERLILY), 1), new PossibleFishingResult(new ItemStack(Items.NAME_TAG), 1), new PossibleFishingResult(new ItemStack(Items.SADDLE), 1), (new PossibleFishingResult(new ItemStack(Items.BOW), 1)).a(0.25F).a(), (new PossibleFishingResult(new ItemStack(Items.FISHING_ROD), 1)).a(0.25F).a(), (new PossibleFishingResult(new ItemStack(Items.BOOK), 1)).a()});
    private static final List<PossibleFishingResult> f = Arrays.asList(new PossibleFishingResult[] { new PossibleFishingResult(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.COD.a()), 60), new PossibleFishingResult(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.SALMON.a()), 25), new PossibleFishingResult(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.CLOWNFISH.a()), 2), new PossibleFishingResult(new ItemStack(Items.FISH, 1, ItemFish.EnumFish.PUFFERFISH.a()), 13)});
    private int g = -1;
    private int h = -1;
    private int i = -1;
    private Block ar;
    private boolean as;
    public int a;
    public EntityHuman owner;
    private int at;
    private int au;
    private int av;
    private int aw;
    private int ax;
    private float ay;
    public Entity hooked;
    private int az;
    private double aA;
    private double aB;
    private double aC;
    private double aD;
    private double aE;

    public static List<PossibleFishingResult> j() {
        return EntityFishingHook.f;
    }

    public EntityFishingHook(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.ah = true;
    }

    public EntityFishingHook(World world, EntityHuman entityhuman) {
        super(world);
        this.ah = true;
        this.owner = entityhuman;
        this.owner.hookedFish = this;
        this.setSize(0.25F, 0.25F);
        this.setPositionRotation(entityhuman.locX, entityhuman.locY + (double) entityhuman.getHeadHeight(), entityhuman.locZ, entityhuman.yaw, entityhuman.pitch);
        this.locX -= (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.locY -= 0.10000000149011612D;
        this.locZ -= (double) (MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.setPosition(this.locX, this.locY, this.locZ);
        float f = 0.4F;

        this.motX = (double) (-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
        this.motZ = (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
        this.motY = (double) (-MathHelper.sin(this.pitch / 180.0F * 3.1415927F) * f);
        this.c(this.motX, this.motY, this.motZ, 1.5F, 1.0F);
    }

    protected void h() {}

    public void c(double d0, double d1, double d2, float f, float f1) {
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
        this.at = 0;
    }

    public void t_() {
        super.t_();
        if (this.az > 0) {
            double d0 = this.locX + (this.aA - this.locX) / (double) this.az;
            double d1 = this.locY + (this.aB - this.locY) / (double) this.az;
            double d2 = this.locZ + (this.aC - this.locZ) / (double) this.az;
            double d3 = MathHelper.g(this.aD - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.az);
            this.pitch = (float) ((double) this.pitch + (this.aE - (double) this.pitch) / (double) this.az);
            --this.az;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        } else {
            if (!this.world.isClientSide) {
                ItemStack itemstack = this.owner.bZ();

                if (this.owner.dead || !this.owner.isAlive() || itemstack == null || itemstack.getItem() != Items.FISHING_ROD || this.h(this.owner) > 1024.0D) {
                    this.die();
                    this.owner.hookedFish = null;
                    return;
                }

                if (this.hooked != null) {
                    if (!this.hooked.dead) {
                        this.locX = this.hooked.locX;
                        double d4 = (double) this.hooked.length;

                        this.locY = this.hooked.getBoundingBox().b + d4 * 0.8D;
                        this.locZ = this.hooked.locZ;
                        return;
                    }

                    this.hooked = null;
                }
            }

            if (this.a > 0) {
                --this.a;
            }

            if (this.as) {
                if (this.world.getType(new BlockPosition(this.g, this.h, this.i)).getBlock() == this.ar) {
                    ++this.at;
                    if (this.at == 1200) {
                        this.die();
                    }

                    return;
                }

                this.as = false;
                this.motX *= (double) (this.random.nextFloat() * 0.2F);
                this.motY *= (double) (this.random.nextFloat() * 0.2F);
                this.motZ *= (double) (this.random.nextFloat() * 0.2F);
                this.at = 0;
                this.au = 0;
            } else {
                ++this.au;
            }

            Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
            Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1);

            vec3d = new Vec3D(this.locX, this.locY, this.locZ);
            vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            if (movingobjectposition != null) {
                vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
            }

            Entity entity = null;
            List list = this.world.getEntities(this, this.getBoundingBox().a(this.motX, this.motY, this.motZ).grow(1.0D, 1.0D, 1.0D));
            double d5 = 0.0D;

            double d6;

            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = (Entity) list.get(i);

                if (entity1.ad() && (entity1 != this.owner || this.au >= 5)) {
                    float f = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double) f, (double) f, (double) f);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.a(vec3d, vec3d1);

                    if (movingobjectposition1 != null) {
                        d6 = vec3d.distanceSquared(movingobjectposition1.pos);
                        if (d6 < d5 || d5 == 0.0D) {
                            entity = entity1;
                            d5 = d6;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            // PaperSpigot start - Allow fishing hooks to fly through vanished players the shooter can't see
            if (movingobjectposition != null && movingobjectposition.entity instanceof EntityPlayer && owner != null && owner instanceof EntityPlayer) {
                if (!((EntityPlayer) owner).getBukkitEntity().canSee(((EntityPlayer) movingobjectposition.entity).getBukkitEntity())) {
                    movingobjectposition = null;
                }
            }
            // PaperSpigot end

            if (movingobjectposition != null) {
                org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this); // Craftbukkit - Call event
                if (movingobjectposition.entity != null) {
                    if (movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.owner), 0.0F)) {
                        this.hooked = movingobjectposition.entity;
                    }
                } else {
                    this.as = true;
                }
            }

            if (!this.as) {
                this.move(this.motX, this.motY, this.motZ);
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
                float f2 = 0.92F;

                if (this.onGround || this.positionChanged) {
                    f2 = 0.5F;
                }

                byte b0 = 5;
                double d7 = 0.0D;

                double d8;

                for (int j = 0; j < b0; ++j) {
                    AxisAlignedBB axisalignedbb1 = this.getBoundingBox();
                    double d9 = axisalignedbb1.e - axisalignedbb1.b;
                    double d10 = axisalignedbb1.b + d9 * (double) j / (double) b0;

                    d8 = axisalignedbb1.b + d9 * (double) (j + 1) / (double) b0;
                    AxisAlignedBB axisalignedbb2 = new AxisAlignedBB(axisalignedbb1.a, d10, axisalignedbb1.c, axisalignedbb1.d, d8, axisalignedbb1.f);

                    if (this.world.b(axisalignedbb2, Material.WATER)) {
                        d7 += 1.0D / (double) b0;
                    }
                }

                if (!this.world.isClientSide && d7 > 0.0D) {
                    WorldServer worldserver = (WorldServer) this.world;
                    int k = 1;
                    BlockPosition blockposition = (new BlockPosition(this)).up();

                    if (this.random.nextFloat() < 0.25F && this.world.isRainingAt(blockposition)) {
                        k = 2;
                    }

                    if (this.random.nextFloat() < 0.5F && !this.world.i(blockposition)) {
                        --k;
                    }

                    if (this.av > 0) {
                        --this.av;
                        if (this.av <= 0) {
                            this.aw = 0;
                            this.ax = 0;
                        }
                    } else {
                        float f3;
                        float f4;
                        double d11;
                        Block block;
                        float f5;
                        double d12;

                        if (this.ax > 0) {
                            this.ax -= k;
                            if (this.ax <= 0) {
                                this.motY -= 0.20000000298023224D;
                                this.makeSound("random.splash", 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                                f3 = (float) MathHelper.floor(this.getBoundingBox().b);
                                worldserver.a(EnumParticle.WATER_BUBBLE, this.locX, (double) (f3 + 1.0F), this.locZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
                                worldserver.a(EnumParticle.WATER_WAKE, this.locX, (double) (f3 + 1.0F), this.locZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
                                this.av = MathHelper.nextInt(this.random, 10, 30);
                            } else {
                                this.ay = (float) ((double) this.ay + this.random.nextGaussian() * 4.0D);
                                f3 = this.ay * 0.017453292F;
                                f5 = MathHelper.sin(f3);
                                f4 = MathHelper.cos(f3);
                                d8 = this.locX + (double) (f5 * (float) this.ax * 0.1F);
                                d12 = (double) ((float) MathHelper.floor(this.getBoundingBox().b) + 1.0F);
                                d11 = this.locZ + (double) (f4 * (float) this.ax * 0.1F);
                                block = worldserver.getType(new BlockPosition((int) d8, (int) d12 - 1, (int) d11)).getBlock();
                                if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                                    if (this.random.nextFloat() < 0.15F) {
                                        worldserver.a(EnumParticle.WATER_BUBBLE, d8, d12 - 0.10000000149011612D, d11, 1, (double) f5, 0.1D, (double) f4, 0.0D, new int[0]);
                                    }

                                    float f6 = f5 * 0.04F;
                                    float f7 = f4 * 0.04F;

                                    worldserver.a(EnumParticle.WATER_WAKE, d8, d12, d11, 0, (double) f7, 0.01D, (double) (-f6), 1.0D, new int[0]);
                                    worldserver.a(EnumParticle.WATER_WAKE, d8, d12, d11, 0, (double) (-f7), 0.01D, (double) f6, 1.0D, new int[0]);
                                }
                            }
                        } else if (this.aw > 0) {
                            this.aw -= k;
                            f3 = 0.15F;
                            if (this.aw < 20) {
                                f3 = (float) ((double) f3 + (double) (20 - this.aw) * 0.05D);
                            } else if (this.aw < 40) {
                                f3 = (float) ((double) f3 + (double) (40 - this.aw) * 0.02D);
                            } else if (this.aw < 60) {
                                f3 = (float) ((double) f3 + (double) (60 - this.aw) * 0.01D);
                            }

                            if (this.random.nextFloat() < f3) {
                                f5 = MathHelper.a(this.random, 0.0F, 360.0F) * 0.017453292F;
                                f4 = MathHelper.a(this.random, 25.0F, 60.0F);
                                d8 = this.locX + (double) (MathHelper.sin(f5) * f4 * 0.1F);
                                d12 = (double) ((float) MathHelper.floor(this.getBoundingBox().b) + 1.0F);
                                d11 = this.locZ + (double) (MathHelper.cos(f5) * f4 * 0.1F);
                                block = worldserver.getType(new BlockPosition((int) d8, (int) d12 - 1, (int) d11)).getBlock();
                                if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                                    worldserver.a(EnumParticle.WATER_SPLASH, d8, d12, d11, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D, new int[0]);
                                }
                            }

                            if (this.aw <= 0) {
                                this.ay = MathHelper.a(this.random, 0.0F, 360.0F);
                                this.ax = MathHelper.nextInt(this.random, 20, 80);
                            }
                        } else {
                            this.aw = MathHelper.nextInt(this.random, this.world.paperSpigotConfig.fishingMinTicks, this.world.paperSpigotConfig.fishingMaxTicks); // PaperSpigot - Configurable fishing tick range
                            this.aw -= EnchantmentManager.h(this.owner) * 20 * 5;
                        }
                    }

                    if (this.av > 0) {
                        this.motY -= (double) (this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat()) * 0.2D;
                    }
                }

                d6 = d7 * 2.0D - 1.0D;
                this.motY += 0.03999999910593033D * d6;
                if (d7 > 0.0D) {
                    f2 = (float) ((double) f2 * 0.9D);
                    this.motY *= 0.8D;
                }

                this.motX *= (double) f2;
                this.motY *= (double) f2;
                this.motZ *= (double) f2;
                this.setPosition(this.locX, this.locY, this.locZ);
            }
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("xTile", (short) this.g);
        nbttagcompound.setShort("yTile", (short) this.h);
        nbttagcompound.setShort("zTile", (short) this.i);
        MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.c(this.ar);

        nbttagcompound.setString("inTile", minecraftkey == null ? "" : minecraftkey.toString());
        nbttagcompound.setByte("shake", (byte) this.a);
        nbttagcompound.setByte("inGround", (byte) (this.as ? 1 : 0));
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.g = nbttagcompound.getShort("xTile");
        this.h = nbttagcompound.getShort("yTile");
        this.i = nbttagcompound.getShort("zTile");
        if (nbttagcompound.hasKeyOfType("inTile", 8)) {
            this.ar = Block.getByName(nbttagcompound.getString("inTile"));
        } else {
            this.ar = Block.getById(nbttagcompound.getByte("inTile") & 255);
        }

        this.a = nbttagcompound.getByte("shake") & 255;
        this.as = nbttagcompound.getByte("inGround") == 1;
    }

    public int l() {
        if (this.world.isClientSide) {
            return 0;
        } else {
            byte b0 = 0;

            if (this.hooked != null) {
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), this.hooked.getBukkitEntity(), (Fish) this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_ENTITY);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
                // CraftBukkit end
                
                double d0 = this.owner.locX - this.locX;
                double d1 = this.owner.locY - this.locY;
                double d2 = this.owner.locZ - this.locZ;
                double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                double d4 = 0.1D;

                this.hooked.motX += d0 * d4;
                this.hooked.motY += d1 * d4 + (double) MathHelper.sqrt(d3) * 0.08D;
                this.hooked.motZ += d2 * d4;
                b0 = 3;
            } else if (this.av > 0) {
                EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY, this.locZ, this.m());
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), entityitem.getBukkitEntity(), (Fish) this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_FISH);
                playerFishEvent.setExpToDrop(this.random.nextInt(6) + 1);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
                // CraftBukkit end
                double d5 = this.owner.locX - this.locX;
                double d6 = this.owner.locY - this.locY;
                double d7 = this.owner.locZ - this.locZ;
                double d8 = (double) MathHelper.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
                double d9 = 0.1D;

                entityitem.motX = d5 * d9;
                entityitem.motY = d6 * d9 + (double) MathHelper.sqrt(d8) * 0.08D;
                entityitem.motZ = d7 * d9;
                this.world.addEntity(entityitem);
                // CraftBukkit start - this.random.nextInt(6) + 1 -> playerFishEvent.getExpToDrop()
                if (playerFishEvent.getExpToDrop() > 0) {
                this.owner.world.addEntity(new EntityExperienceOrb(this.owner.world, this.owner.locX, this.owner.locY + 0.5D, this.owner.locZ + 0.5D, playerFishEvent.getExpToDrop()));
                } // CraftBukkit end
                b0 = 1;
            }

            if (this.as) {
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (Fish) this.getBukkitEntity(), PlayerFishEvent.State.IN_GROUND);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
                // CraftBukkit end
                b0 = 2;
            }

            // CraftBukkit start
            if (b0 == 0) {
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (Fish) this.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);
                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
            }
            // CraftBukkit end

            this.die();
            this.owner.hookedFish = null;
            return b0;
        }
    }

    private ItemStack m() {
        float f = this.world.random.nextFloat();
        int i = EnchantmentManager.g(this.owner);
        int j = EnchantmentManager.h(this.owner);
        float f1 = 0.1F - (float) i * 0.025F - (float) j * 0.01F;
        float f2 = 0.05F + (float) i * 0.01F - (float) j * 0.01F;

        f1 = MathHelper.a(f1, 0.0F, 1.0F);
        f2 = MathHelper.a(f2, 0.0F, 1.0F);
        if (f < f1) {
            this.owner.b(StatisticList.D);
            return ((PossibleFishingResult) WeightedRandom.a(this.random, EntityFishingHook.d)).a(this.random);
        } else {
            f -= f1;
            if (f < f2) {
                this.owner.b(StatisticList.E);
                return ((PossibleFishingResult) WeightedRandom.a(this.random, EntityFishingHook.e)).a(this.random);
            } else {
                float f3 = f - f2;

                this.owner.b(StatisticList.C);
                return ((PossibleFishingResult) WeightedRandom.a(this.random, EntityFishingHook.f)).a(this.random);
            }
        }
    }

    public void die() {
        super.die();
        if (this.owner != null) {
            this.owner.hookedFish = null;
        }

    }
}
