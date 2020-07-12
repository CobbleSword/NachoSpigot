package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TravelAgent;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Vehicle;
import co.aikar.timings.SpigotTimings; // Spigot
import co.aikar.timings.Timing; // Spigot
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.plugin.PluginManager;
// CraftBukkit end

// PaperSpigot start
import org.spigotmc.event.entity.EntityDismountEvent;
// PaperSpigot end

public abstract class Entity implements ICommandListener {

    // CraftBukkit start
    private static final int CURRENT_LEVEL = 2;
    static boolean isLevelAtLeast(NBTTagCompound tag, int level) {
        return tag.hasKey("Bukkit.updateLevel") && tag.getInt("Bukkit.updateLevel") >= level;
    }
    // CraftBukikt end

    private static final AxisAlignedBB a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static int entityCount;
    private int id;
    public double j;
    public boolean k;
    public Entity passenger;
    public Entity vehicle;
    public boolean attachedToPlayer;
    public World world;
    public double lastX;
    public double lastY;
    public double lastZ;
    public double locX;
    public double locY;
    public double locZ;
    public double motX;
    public double motY;
    public double motZ;
    public float yaw;
    public float pitch;
    public float lastYaw;
    public float lastPitch;
    private AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean positionChanged;
    public boolean E;
    public boolean F;
    public boolean velocityChanged;
    protected boolean H;
    private boolean g;
    public boolean dead;
    public float width;
    public float length;
    public float L;
    public float M;
    public float N;
    public float fallDistance;
    private int h;
    public double P;
    public double Q;
    public double R;
    public float S;
    public boolean noclip;
    public float U;
    protected Random random;
    public int ticksLived;
    public int maxFireTicks;
    public int fireTicks;
    public boolean inWater; // Spigot - protected -> public // PAIL
    public int noDamageTicks;
    protected boolean justCreated;
    protected boolean fireProof;
    protected DataWatcher datawatcher;
    private double ar;
    private double as;
    public boolean ad;
    // PaperSpigot start - EAR: Fix bug with teleporting entities
    public boolean isAddedToChunk() {
        int chunkX = MathHelper.floor(locX / 16.0D);
        int chunkY = MathHelper.floor(locY / 16.0D);
        int chunkZ = MathHelper.floor(locZ / 16.0D);

        return ad && getChunkX() == chunkX && getChunkY() == chunkY || getChunkZ() == chunkZ;
    }
    public int ae; public int getChunkX() { return ae; } // PAIL
    public int af; public int getChunkY() { return af; } // PAIL
    public int ag; public int getChunkZ() { return ag; } // PAIL
    // PaperSpigot end
    public boolean ah;
    public boolean ai;
    public int portalCooldown;
    protected boolean ak;
    protected int al;
    public int dimension;
    protected BlockPosition an;
    protected Vec3D ao;
    protected EnumDirection ap;
    private boolean invulnerable;
    protected UUID uniqueID;
    private final CommandObjectiveExecutor au;
    public boolean valid; // CraftBukkit
    public org.bukkit.projectiles.ProjectileSource projectileSource; // CraftBukkit - For projectiles only
    public boolean forceExplosionKnockback; // CraftBukkit - SPIGOT-949
    public boolean inUnloadedChunk = false; // PaperSpigot - Remove entities in unloaded chunks
    public boolean loadChunks = false; // PaperSpigot - Entities can load chunks they move through and keep them loaded

    // Spigot start
    public Timing tickTimer = SpigotTimings.getEntityTimings(this); // Spigot
    public final byte activationType = org.spigotmc.ActivationRange.initializeEntityActivationType(this);
    public final boolean defaultActivationState;
    public long activatedTick = Integer.MIN_VALUE;
    public boolean fromMobSpawner;
    public void inactiveTick() { }
    // Spigot end

    public int getId() {
        return this.id;
    }

    public void d(int i) {
        this.id = i;
    }

    public void G() {
        this.die();
    }

    public Entity(World world) {
        this.id = Entity.entityCount++;
        this.j = 1.0D;
        this.boundingBox = Entity.a;
        this.width = 0.6F;
        this.length = 1.8F;
        this.h = 1;
        this.random = new Random();
        this.maxFireTicks = 1;
        this.justCreated = true;
        this.uniqueID = MathHelper.a(this.random);
        this.au = new CommandObjectiveExecutor();
        this.world = world;
        this.setPosition(0.0D, 0.0D, 0.0D);
        if (world != null) {
            this.dimension = world.worldProvider.getDimension();
            // Spigot start
            this.defaultActivationState = org.spigotmc.ActivationRange.initializeEntityActivationState(this, world.spigotConfig);
        } else {
            this.defaultActivationState = false;
        }
        // Spigot end

        this.datawatcher = new DataWatcher(this);
        this.datawatcher.a(0, Byte.valueOf((byte) 0));
        this.datawatcher.a(1, Short.valueOf((short) 300));
        this.datawatcher.a(3, Byte.valueOf((byte) 0));
        this.datawatcher.a(2, "");
        this.datawatcher.a(4, Byte.valueOf((byte) 0));
        this.h();
    }

    protected abstract void h();

    public DataWatcher getDataWatcher() {
        return this.datawatcher;
    }

    public boolean equals(Object object) {
        return object instanceof Entity ? ((Entity) object).id == this.id : false;
    }

    public int hashCode() {
        return this.id;
    }

    public void die() {
        this.dead = true;
    }

    public void setSize(float f, float f1) {
        if (f != this.width || f1 != this.length) {
            float f2 = this.width;

            this.width = f;
            this.length = f1;
            this.a(new AxisAlignedBB(this.getBoundingBox().a, this.getBoundingBox().b, this.getBoundingBox().c, this.getBoundingBox().a + (double) this.width, this.getBoundingBox().b + (double) this.length, this.getBoundingBox().c + (double) this.width));
            if (this.width > f2 && !this.justCreated && !this.world.isClientSide) {
                this.move((double) (f2 - this.width), 0.0D, (double) (f2 - this.width));
            }
        }

    }

    protected void setYawPitch(float f, float f1) {
        // CraftBukkit start - yaw was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(f)) {
            f = 0;
        }

        if (f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY) {
            if (this instanceof EntityPlayer) {
                this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid yaw");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite yaw (Hacking?)"); //Spigot "Nope" -> Descriptive reason
            }
            f = 0;
        }

        // pitch was sometimes set to NaN, so we need to set it back to 0
        if (Float.isNaN(f1)) {
            f1 = 0;
        }

        if (f1 == Float.POSITIVE_INFINITY || f1 == Float.NEGATIVE_INFINITY) {
            if (this instanceof EntityPlayer) {
                this.world.getServer().getLogger().warning(this.getName() + " was caught trying to crash the server with an invalid pitch");
                ((CraftPlayer) this.getBukkitEntity()).kickPlayer("Infinite pitch (Hacking?)"); //Spigot "Nope" -> Descriptive reason
            }
            f1 = 0;
        }
        // CraftBukkit end

        this.yaw = f % 360.0F;
        this.pitch = f1 % 360.0F;
    }

    public void setPosition(double d0, double d1, double d2) {
        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        float f = this.width / 2.0F;
        float f1 = this.length;

        this.a(new AxisAlignedBB(d0 - (double) f, d1, d2 - (double) f, d0 + (double) f, d1 + (double) f1, d2 + (double) f));
    }

    public void t_() {
        this.K();
    }

    /**
     * PaperSpigot - Checks if the feature is enabled and the entity is above the nether world bedrock height
     */
    private boolean paperNetherCheck() {
        return this.world.paperSpigotConfig.netherVoidTopDamage && this.world.getWorld().getEnvironment() == org.bukkit.World.Environment.NETHER && this.locY >= 128.0D;
    }

    public void K() {
        this.world.methodProfiler.a("entityBaseTick");
        if (this.vehicle != null && this.vehicle.dead) {
            this.vehicle = null;
        }

        this.L = this.M;
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
        if (!this.world.isClientSide && this.world instanceof WorldServer) {
            this.world.methodProfiler.a("portal");
            MinecraftServer minecraftserver = ((WorldServer) this.world).getMinecraftServer();
            int i = this.L();

            if (this.ak) {
                if (true || minecraftserver.getAllowNether()) { // CraftBukkit
                    if (this.vehicle == null && this.al++ >= i) {
                        this.al = i;
                        this.portalCooldown = this.aq();
                        byte b0;

                        if (this.world.worldProvider.getDimension() == -1) {
                            b0 = 0;
                        } else {
                            b0 = -1;
                        }

                        this.c(b0);
                    }

                    this.ak = false;
                }
            } else {
                if (this.al > 0) {
                    this.al -= 4;
                }

                if (this.al < 0) {
                    this.al = 0;
                }
            }

            if (this.portalCooldown > 0) {
                --this.portalCooldown;
            }

            this.world.methodProfiler.b();
        }

        this.Y();
        this.W();
        if (this.world.isClientSide) {
            this.fireTicks = 0;
        } else if (this.fireTicks > 0) {
            if (this.fireProof) {
                this.fireTicks -= 4;
                if (this.fireTicks < 0) {
                    this.fireTicks = 0;
                }
            } else {
                if (this.fireTicks % 20 == 0) {
                    this.damageEntity(DamageSource.BURN, 1.0F);
                }

                --this.fireTicks;
            }
        }

        if (this.ab()) {
            this.burnFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.locY < -64.0D || paperNetherCheck()) { // PaperSpigot - Configurable top-of-nether void damage
            this.O();
        }

        if (!this.world.isClientSide) {
            this.b(0, this.fireTicks > 0);
        }

        this.justCreated = false;
        this.world.methodProfiler.b();
    }

    public int L() {
        return 0;
    }

    protected void burnFromLava() {
        if (!this.fireProof) {
            this.damageEntity(DamageSource.LAVA, 4.0F);

            // CraftBukkit start - Fallen in lava TODO: this event spams!
            if (this instanceof EntityLiving) {
                if (fireTicks <= 0) {
                    // not on fire yet
                    // TODO: shouldn't be sending null for the block
                    org.bukkit.block.Block damager = null; // ((WorldServer) this.l).getWorld().getBlockAt(i, j, k);
                    org.bukkit.entity.Entity damagee = this.getBukkitEntity();
                    EntityCombustEvent combustEvent = new org.bukkit.event.entity.EntityCombustByBlockEvent(damager, damagee, 15);
                    this.world.getServer().getPluginManager().callEvent(combustEvent);

                    if (!combustEvent.isCancelled()) {
                        this.setOnFire(combustEvent.getDuration());
                    }
                } else {
                    // This will be called every single tick the entity is in lava, so don't throw an event
                    this.setOnFire(15);
                }
                return;
            }
            // CraftBukkit end - we also don't throw an event unless the object in lava is living, to save on some event calls
            this.setOnFire(15);
        }
    }

    public void setOnFire(int i) {
        int j = i * 20;

        j = EnchantmentProtection.a(this, j);
        if (this.fireTicks < j) {
            this.fireTicks = j;
        }

    }

    public void extinguish() {
        this.fireTicks = 0;
    }

    protected void O() {
        this.die();
    }

    public boolean c(double d0, double d1, double d2) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().c(d0, d1, d2);

        return this.b(axisalignedbb);
    }

    private boolean b(AxisAlignedBB axisalignedbb) {
        return this.world.getCubes(this, axisalignedbb).isEmpty() && !this.world.containsLiquid(axisalignedbb);
    }

    /**
     * PaperSpigot - Load surrounding chunks the entity is moving through
     */
    public void loadChunks() {
        for (int cx = (int) locX >> 4; cx <= (int) (locX + motX) >> 4; ++cx) {
            for (int cz = (int) locZ >> 4; cz <= (int) (locZ + motZ) >> 4; ++cz) {
                ((ChunkProviderServer) world.chunkProvider).getChunkAt(cx, cz);
            }
        }
    }


    public void move(double d0, double d1, double d2) {
        if (this.loadChunks) loadChunks(); // PaperSpigot - Load chunks
        if (this.noclip) {
            this.a(this.getBoundingBox().c(d0, d1, d2));
            this.recalcPosition();
        } else {
            // CraftBukkit start - Don't do anything if we aren't moving
            // We need to do this regardless of whether or not we are moving thanks to portals
            try {
                this.checkBlockCollisions();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }
            // Check if we're moving
            if (d0 == 0 && d1 == 0 && d2 == 0 && this.vehicle == null && this.passenger == null) {
                return;
            }
            // CraftBukkit end
            this.world.methodProfiler.a("move");
            double d3 = this.locX;
            double d4 = this.locY;
            double d5 = this.locZ;

            if (this.H) {
                this.H = false;
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            double d6 = d0;
            double d7 = d1;
            double d8 = d2;
            boolean flag = this.onGround && this.isSneaking() && this instanceof EntityHuman;

            if (flag) {
                double d9;

                for (d9 = 0.05D; d0 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, 0.0D)).isEmpty(); d6 = d0) {
                    if (d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }
                }

                for (; d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(0.0D, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }

                for (; d0 != 0.0D && d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().c(d0, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }

                    d6 = d0;
                    if (d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }
            }

            // TacoSpigot start - do axis by axis scan if the entity is travelling a large area
            AxisAlignedBB totalArea = this.getBoundingBox().a(d0, d1, d2);
            double xLength = totalArea.d - totalArea.a;
            double yLength = totalArea.e - totalArea.b;
            double zLength = totalArea.f - totalArea.c;
            boolean axisScan = this.world.tacoSpigotConfig.optimizeTntMovement && xLength * yLength * zLength > 10;

            List list = this.world.getCubes(this, axisScan ? this.getBoundingBox().a(0, d1, 0) : totalArea);
            // TacoSpigot end

            AxisAlignedBB axisalignedbb = this.getBoundingBox();

            AxisAlignedBB axisalignedbb1;

            for (Iterator iterator = list.iterator(); iterator.hasNext(); d1 = axisalignedbb1.b(this.getBoundingBox(), d1)) {
                axisalignedbb1 = (AxisAlignedBB) iterator.next();
            }

            this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
            boolean flag1 = this.onGround || d7 != d1 && d7 < 0.0D;

            AxisAlignedBB axisalignedbb2;
            Iterator iterator1;

            if(this.world.tacoSpigotConfig.fixEastWest && Math.abs(d0) > Math.abs(d2)) { //TacoSpigot - fix east/west cannoning by calculating the z movement before x if the x velocity is greater
            if(axisScan) list = this.world.getCubes(this, this.getBoundingBox().a(0, 0, d2)); // TacoSpigot - get z axis blocks

            for (iterator1 = list.iterator(); iterator1.hasNext(); d2 = axisalignedbb2.c(this.getBoundingBox(), d2)) {
                axisalignedbb2 = (AxisAlignedBB) iterator1.next();
            }

                this.a(this.getBoundingBox().c(0.0D, 0.0D, d2));

                if(axisScan) list = this.world.getCubes(this, this.getBoundingBox().a(d0, 0, 0)); // TacoSpigot - get x axis blocks

                for (iterator1 = list.iterator(); iterator1.hasNext(); d0 = axisalignedbb2.a(this.getBoundingBox(), d0)) {
                    axisalignedbb2 = (AxisAlignedBB) iterator1.next();
                }

                this.a(this.getBoundingBox().c(d0, 0.0D, 0.0D));

            } else {
            if(axisScan) list = this.world.getCubes(this, this.getBoundingBox().a(d0, 0, 0)); // TacoSpigot - get x axis blocks

            for (iterator1 = list.iterator(); iterator1.hasNext(); d0 = axisalignedbb2.a(this.getBoundingBox(), d0)) {
                axisalignedbb2 = (AxisAlignedBB) iterator1.next();
            }

            this.a(this.getBoundingBox().c(d0, 0.0D, 0.0D));

            if(axisScan) list = this.world.getCubes(this, this.getBoundingBox().a(0, 0, d2)); // TacoSpigot - get z axis blocks

            for (iterator1 = list.iterator(); iterator1.hasNext(); d2 = axisalignedbb2.c(this.getBoundingBox(), d2)) {
                axisalignedbb2 = (AxisAlignedBB) iterator1.next();
            }

            this.a(this.getBoundingBox().c(0.0D, 0.0D, d2));
            }

            if (this.S > 0.0F && flag1 && (d6 != d0 || d8 != d2)) {
                double d10 = d0;
                double d11 = d1;
                double d12 = d2;
                AxisAlignedBB axisalignedbb3 = this.getBoundingBox();

                this.a(axisalignedbb);
                d1 = (double) this.S;
                List list1 = this.world.getCubes(this, this.getBoundingBox().a(d6, d1, d8));
                AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
                AxisAlignedBB axisalignedbb5 = axisalignedbb4.a(d6, 0.0D, d8);
                double d13 = d1;

                AxisAlignedBB axisalignedbb6;

                for (Iterator iterator2 = list1.iterator(); iterator2.hasNext(); d13 = axisalignedbb6.b(axisalignedbb5, d13)) {
                    axisalignedbb6 = (AxisAlignedBB) iterator2.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, d13, 0.0D);
                double d14 = d6;

                AxisAlignedBB axisalignedbb7;

                for (Iterator iterator3 = list1.iterator(); iterator3.hasNext(); d14 = axisalignedbb7.a(axisalignedbb4, d14)) {
                    axisalignedbb7 = (AxisAlignedBB) iterator3.next();
                }

                axisalignedbb4 = axisalignedbb4.c(d14, 0.0D, 0.0D);
                double d15 = d8;

                AxisAlignedBB axisalignedbb8;

                for (Iterator iterator4 = list1.iterator(); iterator4.hasNext(); d15 = axisalignedbb8.c(axisalignedbb4, d15)) {
                    axisalignedbb8 = (AxisAlignedBB) iterator4.next();
                }

                axisalignedbb4 = axisalignedbb4.c(0.0D, 0.0D, d15);
                AxisAlignedBB axisalignedbb9 = this.getBoundingBox();
                double d16 = d1;

                AxisAlignedBB axisalignedbb10;

                for (Iterator iterator5 = list1.iterator(); iterator5.hasNext(); d16 = axisalignedbb10.b(axisalignedbb9, d16)) {
                    axisalignedbb10 = (AxisAlignedBB) iterator5.next();
                }

                axisalignedbb9 = axisalignedbb9.c(0.0D, d16, 0.0D);
                double d17 = d6;

                AxisAlignedBB axisalignedbb11;

                for (Iterator iterator6 = list1.iterator(); iterator6.hasNext(); d17 = axisalignedbb11.a(axisalignedbb9, d17)) {
                    axisalignedbb11 = (AxisAlignedBB) iterator6.next();
                }

                axisalignedbb9 = axisalignedbb9.c(d17, 0.0D, 0.0D);
                double d18 = d8;

                AxisAlignedBB axisalignedbb12;

                for (Iterator iterator7 = list1.iterator(); iterator7.hasNext(); d18 = axisalignedbb12.c(axisalignedbb9, d18)) {
                    axisalignedbb12 = (AxisAlignedBB) iterator7.next();
                }

                axisalignedbb9 = axisalignedbb9.c(0.0D, 0.0D, d18);
                double d19 = d14 * d14 + d15 * d15;
                double d20 = d17 * d17 + d18 * d18;

                if (d19 > d20) {
                    d0 = d14;
                    d2 = d15;
                    d1 = -d13;
                    this.a(axisalignedbb4);
                } else {
                    d0 = d17;
                    d2 = d18;
                    d1 = -d16;
                    this.a(axisalignedbb9);
                }

                AxisAlignedBB axisalignedbb13;

                for (Iterator iterator8 = list1.iterator(); iterator8.hasNext(); d1 = axisalignedbb13.b(this.getBoundingBox(), d1)) {
                    axisalignedbb13 = (AxisAlignedBB) iterator8.next();
                }

                this.a(this.getBoundingBox().c(0.0D, d1, 0.0D));
                if (d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
                    d0 = d10;
                    d1 = d11;
                    d2 = d12;
                    this.a(axisalignedbb3);
                }
            }

            this.world.methodProfiler.b();
            this.world.methodProfiler.a("rest");
            this.recalcPosition();
            this.positionChanged = d6 != d0 || d8 != d2;
            this.E = d7 != d1;
            this.onGround = this.E && d7 < 0.0D;
            this.F = this.positionChanged || this.E;
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            Block block = this.world.getType(blockposition).getBlock();

            if (block.getMaterial() == Material.AIR) {
                Block block1 = this.world.getType(blockposition.down()).getBlock();

                if (block1 instanceof BlockFence || block1 instanceof BlockCobbleWall || block1 instanceof BlockFenceGate) {
                    block = block1;
                    blockposition = blockposition.down();
                }
            }

            this.a(d1, this.onGround, block, blockposition);
            if (d6 != d0) {
                this.motX = 0.0D;
            }

            if (d8 != d2) {
                this.motZ = 0.0D;
            }

            if (d7 != d1) {
                block.a(this.world, this);
            }

            // CraftBukkit start
            if (positionChanged && getBukkitEntity() instanceof Vehicle) {
                Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                org.bukkit.block.Block bl = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));

                if (d6 > d0) {
                    bl = bl.getRelative(BlockFace.EAST);
                } else if (d6 < d0) {
                    bl = bl.getRelative(BlockFace.WEST);
                } else if (d8 > d2) {
                    bl = bl.getRelative(BlockFace.SOUTH);
                } else if (d8 < d2) {
                    bl = bl.getRelative(BlockFace.NORTH);
                }

                VehicleBlockCollisionEvent event = new VehicleBlockCollisionEvent(vehicle, bl);
                world.getServer().getPluginManager().callEvent(event);
            }
            // CraftBukkit end

            if (this.s_() && !flag && this.vehicle == null) {
                double d21 = this.locX - d3;
                double d22 = this.locY - d4;
                double d23 = this.locZ - d5;

                if (block != Blocks.LADDER) {
                    d22 = 0.0D;
                }

                if (block != null && this.onGround) {
                    // block.a(this.world, blockposition, this); // CraftBukkit moved down
                }

                this.M = (float) ((double) this.M + (double) MathHelper.sqrt(d21 * d21 + d23 * d23) * 0.6D);
                this.N = (float) ((double) this.N + (double) MathHelper.sqrt(d21 * d21 + d22 * d22 + d23 * d23) * 0.6D);
                if (this.N > (float) this.h && block.getMaterial() != Material.AIR) {
                    this.h = (int) this.N + 1;
                    if (this.V()) {
                        float f = MathHelper.sqrt(this.motX * this.motX * 0.20000000298023224D + this.motY * this.motY + this.motZ * this.motZ * 0.20000000298023224D) * 0.35F;

                        if (f > 1.0F) {
                            f = 1.0F;
                        }

                        this.makeSound(this.P(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    }

                    this.a(blockposition, block);
                    block.a(this.world, blockposition, this); // CraftBukkit moved from above
                }
            }

            // CraftBukkit start - Move to the top of the method
            /*
            try {
                this.checkBlockCollisions();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }
            */
            // CraftBukkit end

            boolean flag2 = this.U();

            if (this.world.e(this.getBoundingBox().shrink(0.001D, 0.001D, 0.001D))) {
                this.burn(1);
                if (!flag2) {
                    ++this.fireTicks;
                    // CraftBukkit start - Not on fire yet
                    if (this.fireTicks <= 0) { // Only throw events on the first combust, otherwise it spams
                        EntityCombustEvent event = new EntityCombustEvent(getBukkitEntity(), 8);
                        world.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            setOnFire(event.getDuration());
                        }
                    } else {
                        // CraftBukkit end
                        this.setOnFire(8);
                    }
                }
            } else if (this.fireTicks <= 0) {
                this.fireTicks = -this.maxFireTicks;
            }

            if (flag2 && this.fireTicks > 0) {
                this.makeSound("random.fizz", 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.fireTicks = -this.maxFireTicks;
            }

            this.world.methodProfiler.b();
        }
    }

    private void recalcPosition() {
        this.locX = (this.getBoundingBox().a + this.getBoundingBox().d) / 2.0D;
        this.locY = this.getBoundingBox().b;
        this.locZ = (this.getBoundingBox().c + this.getBoundingBox().f) / 2.0D;
    }

    protected String P() {
        return "game.neutral.swim";
    }

    protected void checkBlockCollisions() {
        BlockPosition blockposition = new BlockPosition(this.getBoundingBox().a + 0.001D, this.getBoundingBox().b + 0.001D, this.getBoundingBox().c + 0.001D);
        BlockPosition blockposition1 = new BlockPosition(this.getBoundingBox().d - 0.001D, this.getBoundingBox().e - 0.001D, this.getBoundingBox().f - 0.001D);

        if (this.world.areChunksLoadedBetween(blockposition, blockposition1)) {
            for (int i = blockposition.getX(); i <= blockposition1.getX(); ++i) {
                for (int j = blockposition.getY(); j <= blockposition1.getY(); ++j) {
                    for (int k = blockposition.getZ(); k <= blockposition1.getZ(); ++k) {
                        BlockPosition blockposition2 = new BlockPosition(i, j, k);
                        IBlockData iblockdata = this.world.getType(blockposition2);

                        try {
                            iblockdata.getBlock().a(this.world, blockposition2, iblockdata, this);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.a(throwable, "Colliding entity with block");
                            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being collided with");

                            CrashReportSystemDetails.a(crashreportsystemdetails, blockposition2, iblockdata);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }

    }

    protected void a(BlockPosition blockposition, Block block) {
        Block.StepSound block_stepsound = block.stepSound;

        if (this.world.getType(blockposition.up()).getBlock() == Blocks.SNOW_LAYER) {
            block_stepsound = Blocks.SNOW_LAYER.stepSound;
            this.makeSound(block_stepsound.getStepSound(), block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
        } else if (!block.getMaterial().isLiquid()) {
            this.makeSound(block_stepsound.getStepSound(), block_stepsound.getVolume1() * 0.15F, block_stepsound.getVolume2());
        }

    }

    public void makeSound(String s, float f, float f1) {
        if (!this.R()) {
            this.world.makeSound(this, s, f, f1);
        }

    }

    public boolean R() {
        return this.datawatcher.getByte(4) == 1;
    }

    public void b(boolean flag) {
        this.datawatcher.watch(4, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    protected boolean s_() {
        return true;
    }

    protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
        if (flag) {
            if (this.fallDistance > 0.0F) {
                if (block != null) {
                    block.fallOn(this.world, blockposition, this, this.fallDistance);
                } else {
                    this.e(this.fallDistance, 1.0F);
                }

                this.fallDistance = 0.0F;
            }
        } else if (d0 < 0.0D) {
            this.fallDistance = (float) ((double) this.fallDistance - d0);
        }

    }

    public AxisAlignedBB S() {
        return null;
    }

    protected void burn(float i) { // CraftBukkit - int -> float
        if (!this.fireProof) {
            this.damageEntity(DamageSource.FIRE, (float) i);
        }

    }

    public final boolean isFireProof() {
        return this.fireProof;
    }

    public void e(float f, float f1) {
        if (this.passenger != null) {
            this.passenger.e(f, f1);
        }

    }

    public boolean U() {
        return this.inWater || this.world.isRainingAt(new BlockPosition(this.locX, this.locY, this.locZ)) || this.world.isRainingAt(new BlockPosition(this.locX, this.locY + (double) this.length, this.locZ));
    }

    public boolean V() {
        return this.inWater;
    }

    public boolean W() {
        if (this.world.a(this.getBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D, 0.001D, 0.001D), Material.WATER, this)) {
            if (!this.inWater && !this.justCreated) {
                this.X();
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fireTicks = 0;
        } else {
            this.inWater = false;
        }

        return this.inWater;
    }

    protected void X() {
        float f = MathHelper.sqrt(this.motX * this.motX * 0.20000000298023224D + this.motY * this.motY + this.motZ * this.motZ * 0.20000000298023224D) * 0.2F;

        if (f > 1.0F) {
            f = 1.0F;
        }

        this.makeSound(this.aa(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        float f1 = (float) MathHelper.floor(this.getBoundingBox().b);

        int i;
        float f2;
        float f3;

        for (i = 0; (float) i < 1.0F + this.width * 20.0F; ++i) {
            f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX + (double) f2, (double) (f1 + 1.0F), this.locZ + (double) f3, this.motX, this.motY - (double) (this.random.nextFloat() * 0.2F), this.motZ, new int[0]);
        }

        for (i = 0; (float) i < 1.0F + this.width * 20.0F; ++i) {
            f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            this.world.addParticle(EnumParticle.WATER_SPLASH, this.locX + (double) f2, (double) (f1 + 1.0F), this.locZ + (double) f3, this.motX, this.motY, this.motZ, new int[0]);
        }

    }

    public void Y() {
        if (this.isSprinting() && !this.V()) {
            this.Z();
        }

    }

    protected void Z() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locY - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        IBlockData iblockdata = this.world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (block.b() != -1) {
            this.world.addParticle(EnumParticle.BLOCK_CRACK, this.locX + ((double) this.random.nextFloat() - 0.5D) * (double) this.width, this.getBoundingBox().b + 0.1D, this.locZ + ((double) this.random.nextFloat() - 0.5D) * (double) this.width, -this.motX * 4.0D, 1.5D, -this.motZ * 4.0D, new int[] { Block.getCombinedId(iblockdata)});
        }

    }

    protected String aa() {
        return "game.neutral.swim.splash";
    }

    public boolean a(Material material) {
        double d0 = this.locY + (double) this.getHeadHeight();
        BlockPosition blockposition = new BlockPosition(this.locX, d0, this.locZ);
        IBlockData iblockdata = this.world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (block.getMaterial() == material) {
            float f = BlockFluids.b(iblockdata.getBlock().toLegacyData(iblockdata)) - 0.11111111F;
            float f1 = (float) (blockposition.getY() + 1) - f;
            boolean flag = d0 < (double) f1;

            return !flag && this instanceof EntityHuman ? false : flag;
        } else {
            return false;
        }
    }

    public boolean ab() {
        return this.world.a(this.getBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.LAVA);
    }

    public void a(float f, float f1, float f2) {
        float f3 = f * f + f1 * f1;

        if (f3 >= 1.0E-4F) {
            f3 = MathHelper.c(f3);
            if (f3 < 1.0F) {
                f3 = 1.0F;
            }

            f3 = f2 / f3;
            f *= f3;
            f1 *= f3;
            float f4 = MathHelper.sin(this.yaw * 3.1415927F / 180.0F);
            float f5 = MathHelper.cos(this.yaw * 3.1415927F / 180.0F);

            this.motX += (double) (f * f5 - f1 * f4);
            this.motZ += (double) (f1 * f5 + f * f4);
        }
    }

    public float c(float f) {
        BlockPosition blockposition = new BlockPosition(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);

        return this.world.isLoaded(blockposition) ? this.world.o(blockposition) : 0.0F;
    }

    public void spawnIn(World world) {
        // CraftBukkit start
        if (world == null) {
            die();
            this.world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
            return;
        }
        // CraftBukkit end
        this.world = world;
    }

    public void setLocation(double d0, double d1, double d2, float f, float f1) {
        this.lastX = this.locX = d0;
        this.lastY = this.locY = d1;
        this.lastZ = this.locZ = d2;
        this.lastYaw = this.yaw = f;
        this.lastPitch = this.pitch = f1;
        double d3 = (double) (this.lastYaw - f);

        if (d3 < -180.0D) {
            this.lastYaw += 360.0F;
        }

        if (d3 >= 180.0D) {
            this.lastYaw -= 360.0F;
        }

        this.setPosition(this.locX, this.locY, this.locZ);
        this.setYawPitch(f, f1);
    }

    public void setPositionRotation(BlockPosition blockposition, float f, float f1) {
        this.setPositionRotation((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, f, f1);
    }

    public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
        this.P = this.lastX = this.locX = d0;
        this.Q = this.lastY = this.locY = d1;
        this.R = this.lastZ = this.locZ = d2;
        this.yaw = f;
        this.pitch = f1;
        this.setPosition(this.locX, this.locY, this.locZ);
    }

    public float g(Entity entity) {
        float f = (float) (this.locX - entity.locX);
        float f1 = (float) (this.locY - entity.locY);
        float f2 = (float) (this.locZ - entity.locZ);

        return MathHelper.c(f * f + f1 * f1 + f2 * f2);
    }

    public double e(double d0, double d1, double d2) {
        double d3 = this.locX - d0;
        double d4 = this.locY - d1;
        double d5 = this.locZ - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double b(BlockPosition blockposition) {
        return blockposition.c(this.locX, this.locY, this.locZ);
    }

    public double c(BlockPosition blockposition) {
        return blockposition.d(this.locX, this.locY, this.locZ);
    }

    public double f(double d0, double d1, double d2) {
        double d3 = this.locX - d0;
        double d4 = this.locY - d1;
        double d5 = this.locZ - d2;

        return (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
    }

    public double h(Entity entity) {
        double d0 = this.locX - entity.locX;
        double d1 = this.locY - entity.locY;
        double d2 = this.locZ - entity.locZ;

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void d(EntityHuman entityhuman) {}

    int numCollisions = 0; // Spigot
    public void collide(Entity entity) {
        if (entity.passenger != this && entity.vehicle != this) {
            if (!entity.noclip && !this.noclip) {
                double d0 = entity.locX - this.locX;
                double d1 = entity.locZ - this.locZ;
                double d2 = MathHelper.a(d0, d1);

                if (d2 >= 0.009999999776482582D) {
                    d2 = (double) MathHelper.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05000000074505806D;
                    d1 *= 0.05000000074505806D;
                    d0 *= (double) (1.0F - this.U);
                    d1 *= (double) (1.0F - this.U);
                    if (this.passenger == null) {
                        this.g(-d0, 0.0D, -d1);
                    }

                    if (entity.passenger == null) {
                        entity.g(d0, 0.0D, d1);
                    }
                }

            }
        }
    }

    public void g(double d0, double d1, double d2) {
        this.motX += d0;
        this.motY += d1;
        this.motZ += d2;
        this.ai = true;
    }

    protected void ac() {
        this.velocityChanged = true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.ac();
            return false;
        }
    }

    public Vec3D d(float f) {
        if (f == 1.0F) {
            return this.f(this.pitch, this.yaw);
        } else {
            float f1 = this.lastPitch + (this.pitch - this.lastPitch) * f;
            float f2 = this.lastYaw + (this.yaw - this.lastYaw) * f;

            return this.f(f1, f2);
        }
    }

    protected final Vec3D f(float f, float f1) {
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);

        return new Vec3D((double) (f3 * f4), (double) f5, (double) (f2 * f4));
    }

    public boolean ad() {
        return false;
    }

    public boolean ae() {
        return false;
    }

    public void b(Entity entity, int i) {}

    public boolean c(NBTTagCompound nbttagcompound) {
        String s = this.ag();

        if (!this.dead && s != null) {
            nbttagcompound.setString("id", s);
            this.e(nbttagcompound);
            return true;
        } else {
            return false;
        }
    }

    public boolean d(NBTTagCompound nbttagcompound) {
        String s = this.ag();

        if (!this.dead && s != null && this.passenger == null) {
            nbttagcompound.setString("id", s);
            this.e(nbttagcompound);
            return true;
        } else {
            return false;
        }
    }

    public void e(NBTTagCompound nbttagcompound) {
        try {
            nbttagcompound.set("Pos", this.a(new double[] { this.locX, this.locY, this.locZ}));
            nbttagcompound.set("Motion", this.a(new double[] { this.motX, this.motY, this.motZ}));

            // CraftBukkit start - Checking for NaN pitch/yaw and resetting to zero
            // TODO: make sure this is the best way to address this.
            if (Float.isNaN(this.yaw)) {
                this.yaw = 0;
            }

            if (Float.isNaN(this.pitch)) {
                this.pitch = 0;
            }
            // CraftBukkit end

            nbttagcompound.set("Rotation", this.a(new float[] { this.yaw, this.pitch}));
            nbttagcompound.setFloat("FallDistance", this.fallDistance);
            nbttagcompound.setShort("Fire", (short) this.fireTicks);
            nbttagcompound.setShort("Air", (short) this.getAirTicks());
            nbttagcompound.setBoolean("OnGround", this.onGround);
            nbttagcompound.setInt("Dimension", this.dimension);
            nbttagcompound.setBoolean("Invulnerable", this.invulnerable);
            nbttagcompound.setInt("PortalCooldown", this.portalCooldown);
            nbttagcompound.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
            nbttagcompound.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
            // CraftBukkit start
            nbttagcompound.setLong("WorldUUIDLeast", this.world.getDataManager().getUUID().getLeastSignificantBits());
            nbttagcompound.setLong("WorldUUIDMost", this.world.getDataManager().getUUID().getMostSignificantBits());
            nbttagcompound.setInt("Bukkit.updateLevel", CURRENT_LEVEL);
            nbttagcompound.setInt("Spigot.ticksLived", this.ticksLived);
            // CraftBukkit end
            if (this.getCustomName() != null && this.getCustomName().length() > 0) {
                nbttagcompound.setString("CustomName", this.getCustomName());
                nbttagcompound.setBoolean("CustomNameVisible", this.getCustomNameVisible());
            }

            this.au.b(nbttagcompound);
            if (this.R()) {
                nbttagcompound.setBoolean("Silent", this.R());
            }

            this.b(nbttagcompound);
            if (this.vehicle != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                if (this.vehicle.c(nbttagcompound1)) {
                    nbttagcompound.set("Riding", nbttagcompound1);
                }
            }

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Saving entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being saved");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    public void f(NBTTagCompound nbttagcompound) {
        try {
            NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
            NBTTagList nbttaglist1 = nbttagcompound.getList("Motion", 6);
            NBTTagList nbttaglist2 = nbttagcompound.getList("Rotation", 5);

            this.motX = nbttaglist1.d(0);
            this.motY = nbttaglist1.d(1);
            this.motZ = nbttaglist1.d(2);

            /* CraftBukkit start - Moved section down
            if (Math.abs(this.motX) > 10.0D) {
                this.motX = 0.0D;
            }

            if (Math.abs(this.motY) > 10.0D) {
                this.motY = 0.0D;
            }

            if (Math.abs(this.motZ) > 10.0D) {
                this.motZ = 0.0D;
            }
            // CraftBukkit end */

            this.lastX = this.P = this.locX = nbttaglist.d(0);
            this.lastY = this.Q = this.locY = nbttaglist.d(1);
            this.lastZ = this.R = this.locZ = nbttaglist.d(2);
            this.lastYaw = this.yaw = nbttaglist2.e(0);
            this.lastPitch = this.pitch = nbttaglist2.e(1);
            this.f(this.yaw);
            this.g(this.yaw);
            this.fallDistance = nbttagcompound.getFloat("FallDistance");
            this.fireTicks = nbttagcompound.getShort("Fire");
            this.setAirTicks(nbttagcompound.getShort("Air"));
            this.onGround = nbttagcompound.getBoolean("OnGround");
            this.dimension = nbttagcompound.getInt("Dimension");
            this.invulnerable = nbttagcompound.getBoolean("Invulnerable");
            this.portalCooldown = nbttagcompound.getInt("PortalCooldown");
            if (nbttagcompound.hasKeyOfType("UUIDMost", 4) && nbttagcompound.hasKeyOfType("UUIDLeast", 4)) {
                this.uniqueID = new UUID(nbttagcompound.getLong("UUIDMost"), nbttagcompound.getLong("UUIDLeast"));
            } else if (nbttagcompound.hasKeyOfType("UUID", 8)) {
                this.uniqueID = UUID.fromString(nbttagcompound.getString("UUID"));
            }

            this.setPosition(this.locX, this.locY, this.locZ);
            this.setYawPitch(this.yaw, this.pitch);
            if (nbttagcompound.hasKeyOfType("CustomName", 8) && nbttagcompound.getString("CustomName").length() > 0) {
                this.setCustomName(nbttagcompound.getString("CustomName"));
            }

            this.setCustomNameVisible(nbttagcompound.getBoolean("CustomNameVisible"));
            this.au.a(nbttagcompound);
            this.b(nbttagcompound.getBoolean("Silent"));
            this.a(nbttagcompound);
            if (this.af()) {
                this.setPosition(this.locX, this.locY, this.locZ);
            }

            // CraftBukkit start
            if (this instanceof EntityLiving) {
                EntityLiving entity = (EntityLiving) this;

                this.ticksLived = nbttagcompound.getInt("Spigot.ticksLived");

                // Reset the persistence for tamed animals
                if (entity instanceof EntityTameableAnimal && !isLevelAtLeast(nbttagcompound, 2) && !nbttagcompound.getBoolean("PersistenceRequired")) {
                    EntityInsentient entityinsentient = (EntityInsentient) entity;
                    entityinsentient.persistent = !entityinsentient.isTypeNotPersistent();
                }
            }
            // CraftBukkit end

            // CraftBukkit start - Exempt Vehicles from notch's sanity check
            if (!(getBukkitEntity() instanceof Vehicle)) {
                if (Math.abs(this.motX) > 10.0D) {
                    this.motX = 0.0D;
                }

                if (Math.abs(this.motY) > 10.0D) {
                    this.motY = 0.0D;
                }

                if (Math.abs(this.motZ) > 10.0D) {
                    this.motZ = 0.0D;
                }
            }
            // CraftBukkit end

            // CraftBukkit start - Reset world
            if (this instanceof EntityPlayer) {
                Server server = Bukkit.getServer();
                org.bukkit.World bworld = null;

                // TODO: Remove World related checks, replaced with WorldUID
                String worldName = nbttagcompound.getString("world");

                if (nbttagcompound.hasKey("WorldUUIDMost") && nbttagcompound.hasKey("WorldUUIDLeast")) {
                    UUID uid = new UUID(nbttagcompound.getLong("WorldUUIDMost"), nbttagcompound.getLong("WorldUUIDLeast"));
                    bworld = server.getWorld(uid);
                } else {
                    bworld = server.getWorld(worldName);
                }

                if (bworld == null) {
                    EntityPlayer entityPlayer = (EntityPlayer) this;
                    bworld = ((org.bukkit.craftbukkit.CraftServer) server).getServer().getWorldServer(entityPlayer.dimension).getWorld();
                }

                spawnIn(bworld == null? null : ((CraftWorld) bworld).getHandle());
            }
            // CraftBukkit end

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Loading entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being loaded");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean af() {
        return true;
    }

    protected final String ag() {
        return EntityTypes.b(this);
    }

    protected abstract void a(NBTTagCompound nbttagcompound);

    protected abstract void b(NBTTagCompound nbttagcompound);

    public void ah() {}

    protected NBTTagList a(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            nbttaglist.add(new NBTTagDouble(d0));
        }

        return nbttaglist;
    }

    protected NBTTagList a(float... afloat) {
        NBTTagList nbttaglist = new NBTTagList();
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f = afloat1[j];

            nbttaglist.add(new NBTTagFloat(f));
        }

        return nbttaglist;
    }

    public EntityItem a(Item item, int i) {
        return this.a(item, i, 0.0F);
    }

    public EntityItem a(Item item, int i, float f) {
        return this.a(new ItemStack(item, i, 0), f);
    }

    public EntityItem a(ItemStack itemstack, float f) {
        if (itemstack.count != 0 && itemstack.getItem() != null) {
            // CraftBukkit start - Capture drops for death event
            if (this instanceof EntityLiving && ((EntityLiving) this).drops != null) {
                ((EntityLiving) this).drops.add(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemstack));
                return null;
            }
            // CraftBukkit end
            EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY + (double) f, this.locZ, itemstack);

            entityitem.p();
            this.world.addEntity(entityitem);
            return entityitem;
        } else {
            return null;
        }
    }

    public boolean isAlive() {
        return !this.dead;
    }

    public boolean inBlock() {
        if (this.noclip) {
            return false;
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

            for (int i = 0; i < 8; ++i) {
                int j = MathHelper.floor(this.locY + (double) (((float) ((i >> 0) % 2) - 0.5F) * 0.1F) + (double) this.getHeadHeight());
                int k = MathHelper.floor(this.locX + (double) (((float) ((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
                int l = MathHelper.floor(this.locZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * this.width * 0.8F));

                if (blockposition_mutableblockposition.getX() != k || blockposition_mutableblockposition.getY() != j || blockposition_mutableblockposition.getZ() != l) {
                    blockposition_mutableblockposition.c(k, j, l);
                    if (this.world.getType(blockposition_mutableblockposition).getBlock().w()) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean e(EntityHuman entityhuman) {
        return false;
    }

    public AxisAlignedBB j(Entity entity) {
        return null;
    }

    public void ak() {
        if (this.vehicle.dead) {
            this.vehicle = null;
        } else {
            this.motX = 0.0D;
            this.motY = 0.0D;
            this.motZ = 0.0D;
            this.t_();
            if (this.vehicle != null) {
                this.vehicle.al();
                this.as += (double) (this.vehicle.yaw - this.vehicle.lastYaw);

                for (this.ar += (double) (this.vehicle.pitch - this.vehicle.lastPitch); this.as >= 180.0D; this.as -= 360.0D) {
                    ;
                }

                while (this.as < -180.0D) {
                    this.as += 360.0D;
                }

                while (this.ar >= 180.0D) {
                    this.ar -= 360.0D;
                }

                while (this.ar < -180.0D) {
                    this.ar += 360.0D;
                }

                double d0 = this.as * 0.5D;
                double d1 = this.ar * 0.5D;
                float f = 10.0F;

                if (d0 > (double) f) {
                    d0 = (double) f;
                }

                if (d0 < (double) (-f)) {
                    d0 = (double) (-f);
                }

                if (d1 > (double) f) {
                    d1 = (double) f;
                }

                if (d1 < (double) (-f)) {
                    d1 = (double) (-f);
                }

                this.as -= d0;
                this.ar -= d1;
            }
        }
    }

    public void al() {
        if (this.passenger != null) {
            this.passenger.setPosition(this.locX, this.locY + this.an() + this.passenger.am(), this.locZ);
        }
    }

    public double am() {
        return 0.0D;
    }

    public double an() {
        return (double) this.length * 0.75D;
    }

    // CraftBukkit start
    protected CraftEntity bukkitEntity;

    public CraftEntity getBukkitEntity() {
        if (bukkitEntity == null) {
            bukkitEntity = CraftEntity.getEntity(world.getServer(), this);
        }
        return bukkitEntity;
    }

    public void mount(Entity entity) {
        Entity originalVehicle = this.vehicle;
        Entity originalPassenger = this.vehicle == null ? null : this.vehicle.passenger;
        PluginManager pluginManager = Bukkit.getPluginManager();
        getBukkitEntity(); // make sure bukkitEntity is initialised
        // CraftBukkit end
        this.ar = 0.0D;
        this.as = 0.0D;
        if (entity == null) {
            if (this.vehicle != null) {
                // CraftBukkit start
                if ((this.bukkitEntity instanceof LivingEntity) && (this.vehicle.getBukkitEntity() instanceof Vehicle)) {
                    VehicleExitEvent event = new VehicleExitEvent((Vehicle) this.vehicle.getBukkitEntity(), (LivingEntity) this.bukkitEntity);
                    pluginManager.callEvent(event);

                    if (event.isCancelled() || vehicle != originalVehicle) {
                        return;
                    }
                }
                // CraftBukkit end
                // PaperSpigot start - make EntityDismountEvent cancellable
                EntityDismountEvent dismountEvent = new EntityDismountEvent(this.getBukkitEntity(), this.vehicle.getBukkitEntity()); // Spigot
                pluginManager.callEvent(dismountEvent);
                if (dismountEvent.isCancelled()) return;
                // PaperSpigot end
                this.setPositionRotation(this.vehicle.locX, this.vehicle.getBoundingBox().b + (double) this.vehicle.length, this.vehicle.locZ, this.yaw, this.pitch);
                this.vehicle.passenger = null;
            }

            this.vehicle = null;
        } else {
            // CraftBukkit start
            if ((this.bukkitEntity instanceof LivingEntity) && (entity.getBukkitEntity() instanceof Vehicle) && entity.world.isChunkLoaded((int) entity.locX >> 4, (int) entity.locZ >> 4, true)) {
                // It's possible to move from one vehicle to another.  We need to check if they're already in a vehicle, and fire an exit event if they are.
                VehicleExitEvent exitEvent = null;
                if (this.vehicle != null && this.vehicle.getBukkitEntity() instanceof Vehicle) {
                    exitEvent = new VehicleExitEvent((Vehicle) this.vehicle.getBukkitEntity(), (LivingEntity) this.bukkitEntity);
                    pluginManager.callEvent(exitEvent);

                    if (exitEvent.isCancelled() || this.vehicle != originalVehicle || (this.vehicle != null && this.vehicle.passenger != originalPassenger)) {
                        return;
                    }
                }

                VehicleEnterEvent event = new VehicleEnterEvent((Vehicle) entity.getBukkitEntity(), this.bukkitEntity);
                pluginManager.callEvent(event);

                // If a plugin messes with the vehicle or the vehicle's passenger
                if (event.isCancelled() || this.vehicle != originalVehicle || (this.vehicle != null && this.vehicle.passenger != originalPassenger)) {
                    // If we only cancelled the enterevent then we need to put the player in a decent position.
                    if (exitEvent != null && this.vehicle == originalVehicle && this.vehicle != null && this.vehicle.passenger == originalPassenger) {
                        this.setPositionRotation(this.vehicle.locX, this.vehicle.getBoundingBox().b + (double) this.vehicle.length, this.vehicle.locZ, this.yaw, this.pitch);
                        this.vehicle.passenger = null;
                        this.vehicle = null;
                    }
                    return;
                }
            }
            // CraftBukkit end
            // Spigot Start
            if ( entity.world.isChunkLoaded( (int) entity.locX >> 4, (int) entity.locZ >> 4, true ) )
            {
                org.spigotmc.event.entity.EntityMountEvent event = new org.spigotmc.event.entity.EntityMountEvent( this.getBukkitEntity(), entity.getBukkitEntity() );
                pluginManager.callEvent( event );
                if ( event.isCancelled() )
                {
                    return;
                }
            }
            // Spigot End

            if (this.vehicle != null) {
                this.vehicle.passenger = null;
            }

            if (entity != null) {
                for (Entity entity1 = entity.vehicle; entity1 != null; entity1 = entity1.vehicle) {
                    if (entity1 == this) {
                        return;
                    }
                }
            }

            this.vehicle = entity;
            entity.passenger = this;
        }
    }

    public float ao() {
        return 0.1F;
    }

    public Vec3D ap() {
        return null;
    }

    public void d(BlockPosition blockposition) {
        if (this.portalCooldown > 0) {
            this.portalCooldown = this.aq();
        } else {
            if (!this.world.isClientSide && !blockposition.equals(this.an)) {
                this.an = blockposition;
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = Blocks.PORTAL.f(this.world, blockposition);
                double d0 = shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X ? (double) shapedetector_shapedetectorcollection.a().getZ() : (double) shapedetector_shapedetectorcollection.a().getX();
                double d1 = shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X ? this.locZ : this.locX;

                d1 = Math.abs(MathHelper.c(d1 - (double) (shapedetector_shapedetectorcollection.b().e().c() == EnumDirection.EnumAxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double) shapedetector_shapedetectorcollection.d()));
                double d2 = MathHelper.c(this.locY - 1.0D, (double) shapedetector_shapedetectorcollection.a().getY(), (double) (shapedetector_shapedetectorcollection.a().getY() - shapedetector_shapedetectorcollection.e()));

                this.ao = new Vec3D(d1, d2, 0.0D);
                this.ap = shapedetector_shapedetectorcollection.b();
            }

            this.ak = true;
        }
    }

    public int aq() {
        return 300;
    }

    public ItemStack[] getEquipment() {
        return null;
    }

    public void setEquipment(int i, ItemStack itemstack) {}

    public boolean isBurning() {
        boolean flag = this.world != null && this.world.isClientSide;

        return !this.fireProof && (this.fireTicks > 0 || flag && this.g(0));
    }

    public boolean au() {
        return this.vehicle != null;
    }

    public boolean isSneaking() {
        return this.g(1);
    }

    public void setSneaking(boolean flag) {
        this.b(1, flag);
    }

    public boolean isSprinting() {
        return this.g(3);
    }

    public void setSprinting(boolean flag) {
        this.b(3, flag);
    }

    public boolean isInvisible() {
        return this.g(5);
    }

    public void setInvisible(boolean flag) {
        this.b(5, flag);
    }

    public void f(boolean flag) {
        this.b(4, flag);
    }

    protected boolean g(int i) {
        return (this.datawatcher.getByte(0) & 1 << i) != 0;
    }

    protected void b(int i, boolean flag) {
        byte b0 = this.datawatcher.getByte(0);

        if (flag) {
            this.datawatcher.watch(0, Byte.valueOf((byte) (b0 | 1 << i)));
        } else {
            this.datawatcher.watch(0, Byte.valueOf((byte) (b0 & ~(1 << i))));
        }

    }

    public int getAirTicks() {
        return this.datawatcher.getShort(1);
    }

    public void setAirTicks(int i) {
        this.datawatcher.watch(1, Short.valueOf((short) i));
    }

    public void onLightningStrike(EntityLightning entitylightning) {
        // CraftBukkit start
        final org.bukkit.entity.Entity thisBukkitEntity = this.getBukkitEntity();
        final org.bukkit.entity.Entity stormBukkitEntity = entitylightning.getBukkitEntity();
        final PluginManager pluginManager = Bukkit.getPluginManager();

        if (thisBukkitEntity instanceof Hanging) {
            HangingBreakByEntityEvent hangingEvent = new HangingBreakByEntityEvent((Hanging) thisBukkitEntity, stormBukkitEntity);
            PaintingBreakByEntityEvent paintingEvent = null;

            if (thisBukkitEntity instanceof Painting) {
                paintingEvent = new PaintingBreakByEntityEvent((Painting) thisBukkitEntity, stormBukkitEntity);
            }

            pluginManager.callEvent(hangingEvent);

            if (paintingEvent != null) {
                paintingEvent.setCancelled(hangingEvent.isCancelled());
                pluginManager.callEvent(paintingEvent);
            }

            if (hangingEvent.isCancelled() || (paintingEvent != null && paintingEvent.isCancelled())) {
                return;
            }
        }

        if (this.fireProof) {
            return;
        }
        CraftEventFactory.entityDamage = entitylightning;
        if (!this.damageEntity(DamageSource.LIGHTNING, 5.0F)) {
            CraftEventFactory.entityDamage = null;
            return;
        }
        // CraftBukkit end
        ++this.fireTicks;
        if (this.fireTicks == 0) {
            // CraftBukkit start - Call a combust event when lightning strikes
            EntityCombustByEntityEvent entityCombustEvent = new EntityCombustByEntityEvent(stormBukkitEntity, thisBukkitEntity, 8);
            pluginManager.callEvent(entityCombustEvent);
            if (!entityCombustEvent.isCancelled()) {
                this.setOnFire(entityCombustEvent.getDuration());
            }
            // CraftBukkit end
        }

    }

    public void a(EntityLiving entityliving) {}

    protected boolean j(double d0, double d1, double d2) {
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        double d3 = d0 - (double) blockposition.getX();
        double d4 = d1 - (double) blockposition.getY();
        double d5 = d2 - (double) blockposition.getZ();
        List list = this.world.a(this.getBoundingBox());

        if (list.isEmpty() && !this.world.u(blockposition)) {
            return false;
        } else {
            byte b0 = 3;
            double d6 = 9999.0D;

            if (!this.world.u(blockposition.west()) && d3 < d6) {
                d6 = d3;
                b0 = 0;
            }

            if (!this.world.u(blockposition.east()) && 1.0D - d3 < d6) {
                d6 = 1.0D - d3;
                b0 = 1;
            }

            if (!this.world.u(blockposition.up()) && 1.0D - d4 < d6) {
                d6 = 1.0D - d4;
                b0 = 3;
            }

            if (!this.world.u(blockposition.north()) && d5 < d6) {
                d6 = d5;
                b0 = 4;
            }

            if (!this.world.u(blockposition.south()) && 1.0D - d5 < d6) {
                d6 = 1.0D - d5;
                b0 = 5;
            }

            float f = this.random.nextFloat() * 0.2F + 0.1F;

            if (b0 == 0) {
                this.motX = (double) (-f);
            }

            if (b0 == 1) {
                this.motX = (double) f;
            }

            if (b0 == 3) {
                this.motY = (double) f;
            }

            if (b0 == 4) {
                this.motZ = (double) (-f);
            }

            if (b0 == 5) {
                this.motZ = (double) f;
            }

            return true;
        }
    }

    public void aA() {
        this.H = true;
        this.fallDistance = 0.0F;
    }

    public String getName() {
        if (this.hasCustomName()) {
            return this.getCustomName();
        } else {
            String s = EntityTypes.b(this);

            if (s == null) {
                s = "generic";
            }

            return LocaleI18n.get("entity." + s + ".name");
        }
    }

    public Entity[] aB() {
        return null;
    }

    public boolean k(Entity entity) {
        return this == entity;
    }

    public float getHeadRotation() {
        return 0.0F;
    }

    public void f(float f) {}

    public void g(float f) {}

    public boolean aD() {
        return true;
    }

    public boolean l(Entity entity) {
        return false;
    }

    public String toString() {
        return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[] { this.getClass().getSimpleName(), this.getName(), Integer.valueOf(this.id), this.world == null ? "~NULL~" : this.world.getWorldData().getName(), Double.valueOf(this.locX), Double.valueOf(this.locY), Double.valueOf(this.locZ)});
    }

    public boolean isInvulnerable(DamageSource damagesource) {
        return this.invulnerable && damagesource != DamageSource.OUT_OF_WORLD && !damagesource.u();
    }

    public void m(Entity entity) {
        this.setPositionRotation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
    }

    public void n(Entity entity) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        entity.e(nbttagcompound);
        this.f(nbttagcompound);
        this.portalCooldown = entity.portalCooldown;
        this.an = entity.an;
        this.ao = entity.ao;
        this.ap = entity.ap;
    }

    public void c(int i) {
        if (!this.world.isClientSide && !this.dead) {
            this.world.methodProfiler.a("changeDimension");
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            // CraftBukkit start - Move logic into new function "teleportToLocation"
            // int j = this.dimension;
            // WorldServer worldserver = minecraftserver.getWorldServer(j);
            // WorldServer worldserver1 = minecraftserver.getWorldServer(i);
            WorldServer exitWorld = null;
            if (this.dimension < CraftWorld.CUSTOM_DIMENSION_OFFSET) { // Plugins must specify exit from custom Bukkit worlds
                // Only target existing worlds (compensate for allow-nether/allow-end as false)
                for (WorldServer world : minecraftserver.worlds) {
                    if (world.dimension == i) {
                        exitWorld = world;
                    }
                }
            }

            Location enter = this.getBukkitEntity().getLocation();
            Location exit = exitWorld != null ? minecraftserver.getPlayerList().calculateTarget(enter, minecraftserver.getWorldServer(i)) : null;
            boolean useTravelAgent = exitWorld != null && !(this.dimension == 1 && exitWorld.dimension == 1); // don't use agent for custom worlds or return from THE_END

            TravelAgent agent = exit != null ? (TravelAgent) ((CraftWorld) exit.getWorld()).getHandle().getTravelAgent() : org.bukkit.craftbukkit.CraftTravelAgent.DEFAULT; // return arbitrary TA to compensate for implementation dependent plugins
            EntityPortalEvent event = new EntityPortalEvent(this.getBukkitEntity(), enter, exit, agent);
            event.useTravelAgent(useTravelAgent);
            event.getEntity().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled() || event.getTo() == null || event.getTo().getWorld() == null || !this.isAlive()) {
                return;
            }
            exit = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
            this.teleportTo(exit, true);
        }
    }

    public void teleportTo(Location exit, boolean portal) {
        if (true) {
            WorldServer worldserver = ((CraftWorld) getBukkitEntity().getLocation().getWorld()).getHandle();
            WorldServer worldserver1 = ((CraftWorld) exit.getWorld()).getHandle();
            int i = worldserver1.dimension;
            // CraftBukkit end

            this.dimension = i;
            /* CraftBukkit start - TODO: Check if we need this
            if (j == 1 && i == 1) {
                worldserver1 = minecraftserver.getWorldServer(0);
                this.dimension = 0;
            }
            // CraftBukkit end */

            this.world.kill(this);
            this.dead = false;
            this.world.methodProfiler.a("reposition");
            // CraftBukkit start - Ensure chunks are loaded in case TravelAgent is not used which would initially cause chunks to load during find/create
            // minecraftserver.getPlayerList().changeWorld(this, j, worldserver, worldserver1);
            boolean before = worldserver1.chunkProviderServer.forceChunkLoad;
            worldserver1.chunkProviderServer.forceChunkLoad = true;
            worldserver1.getMinecraftServer().getPlayerList().repositionEntity(this, exit, portal);
            worldserver1.chunkProviderServer.forceChunkLoad = before;
            // CraftBukkit end
            this.world.methodProfiler.c("reloading");
            Entity entity = EntityTypes.createEntityByName(EntityTypes.b(this), worldserver1);

            if (entity != null) {
                entity.n(this);
                /* CraftBukkit start - We need to do this...
                if (j == 1 && i == 1) {
                    BlockPosition blockposition = this.world.r(worldserver1.getSpawn());

                    entity.setPositionRotation(blockposition, entity.yaw, entity.pitch);
                }
                // CraftBukkit end */

                worldserver1.addEntity(entity);
                // CraftBukkit start - Forward the CraftEntity to the new entity
                this.getBukkitEntity().setHandle(entity);
                entity.bukkitEntity = this.getBukkitEntity();

                if (this instanceof EntityInsentient) {
                    ((EntityInsentient)this).unleash(true, false); // Unleash to prevent duping of leads.
                }
                // CraftBukkit end
            }

            this.dead = true;
            this.world.methodProfiler.b();
            worldserver.j();
            worldserver1.j();
            this.world.methodProfiler.b();
        }
    }

    public float a(Explosion explosion, World world, BlockPosition blockposition, IBlockData iblockdata) {
        return iblockdata.getBlock().a(this);
    }

    public boolean a(Explosion explosion, World world, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return true;
    }

    public int aE() {
        return 3;
    }

    public Vec3D aG() {
        return this.ao;
    }

    public EnumDirection aH() {
        return this.ap;
    }

    public boolean aI() {
        return false;
    }

    public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Entity Type", new Callable() {
            public String a() throws Exception {
                return EntityTypes.b(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Entity ID", (Object) Integer.valueOf(this.id));
        crashreportsystemdetails.a("Entity Name", new Callable() {
            public String a() throws Exception {
                return Entity.this.getName();
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Entity\'s Exact location", (Object) String.format("%.2f, %.2f, %.2f", new Object[] { Double.valueOf(this.locX), Double.valueOf(this.locY), Double.valueOf(this.locZ)}));
        crashreportsystemdetails.a("Entity\'s Block location", (Object) CrashReportSystemDetails.a((double) MathHelper.floor(this.locX), (double) MathHelper.floor(this.locY), (double) MathHelper.floor(this.locZ)));
        crashreportsystemdetails.a("Entity\'s Momentum", (Object) String.format("%.2f, %.2f, %.2f", new Object[] { Double.valueOf(this.motX), Double.valueOf(this.motY), Double.valueOf(this.motZ)}));
        crashreportsystemdetails.a("Entity\'s Rider", new Callable() {
            public String a() throws Exception {
                return Entity.this.passenger.toString();
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Entity\'s Vehicle", new Callable() {
            public String a() throws Exception {
                return Entity.this.vehicle.toString();
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public boolean aL() {
        return true;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        ChatComponentText chatcomponenttext = new ChatComponentText(this.getName());

        chatcomponenttext.getChatModifier().setChatHoverable(this.aQ());
        chatcomponenttext.getChatModifier().setInsertion(this.getUniqueID().toString());
        return chatcomponenttext;
    }

    public void setCustomName(String s) {
        // CraftBukkit start - Add a sane limit for name length
        if (s.length() > 256) {
            s = s.substring(0, 256);
        }
        // CraftBukkit end
        this.datawatcher.watch(2, s);
    }

    public String getCustomName() {
        return this.datawatcher.getString(2);
    }

    public boolean hasCustomName() {
        return this.datawatcher.getString(2).length() > 0;
    }

    public void setCustomNameVisible(boolean flag) {
        this.datawatcher.watch(3, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    public boolean getCustomNameVisible() {
        return this.datawatcher.getByte(3) == 1;
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
    }

    public void i(int i) {}

    public EnumDirection getDirection() {
        return EnumDirection.fromType2(MathHelper.floor((double) (this.yaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    protected ChatHoverable aQ() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        String s = EntityTypes.b(this);

        nbttagcompound.setString("id", this.getUniqueID().toString());
        if (s != null) {
            nbttagcompound.setString("type", s);
        }

        nbttagcompound.setString("name", this.getName());
        return new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ENTITY, new ChatComponentText(nbttagcompound.toString()));
    }

    public boolean a(EntityPlayer entityplayer) {
        return true;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void a(AxisAlignedBB axisalignedbb) {
        // CraftBukkit start - block invalid bounding boxes
        double a = axisalignedbb.a,
                b = axisalignedbb.b,
                c = axisalignedbb.c,
                d = axisalignedbb.d,
                e = axisalignedbb.e,
                f = axisalignedbb.f;
        double len = axisalignedbb.d - axisalignedbb.a;
        if (len < 0) d = a;
        if (len > 64) d = a + 64.0;

        len = axisalignedbb.e - axisalignedbb.b;
        if (len < 0) e = b;
        if (len > 64) e = b + 64.0;

        len = axisalignedbb.f - axisalignedbb.c;
        if (len < 0) f = c;
        if (len > 64) f = c + 64.0;
        this.boundingBox = new AxisAlignedBB(a, b, c, d, e, f);
        // CraftBukkit end
    }

    public float getHeadHeight() {
        return this.length * 0.85F;
    }

    public boolean aT() {
        return this.g;
    }

    public void h(boolean flag) {
        this.g = flag;
    }

    public boolean d(int i, ItemStack itemstack) {
        return false;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

    public boolean a(int i, String s) {
        return true;
    }

    public BlockPosition getChunkCoordinates() {
        return new BlockPosition(this.locX, this.locY + 0.5D, this.locZ);
    }

    public Vec3D d() {
        return new Vec3D(this.locX, this.locY, this.locZ);
    }

    public World getWorld() {
        return this.world;
    }

    public Entity f() {
        return this;
    }

    public boolean getSendCommandFeedback() {
        return false;
    }

    public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
        this.au.a(this, commandobjectiveexecutor_enumcommandresult, i);
    }

    public CommandObjectiveExecutor aU() {
        return this.au;
    }

    public void o(Entity entity) {
        this.au.a(entity.aU());
    }

    public NBTTagCompound getNBTTag() {
        return null;
    }

    public boolean a(EntityHuman entityhuman, Vec3D vec3d) {
        return false;
    }

    public boolean aW() {
        return false;
    }

    protected void a(EntityLiving entityliving, Entity entity) {
        if (entity instanceof EntityLiving) {
            EnchantmentManager.a((EntityLiving) entity, (Entity) entityliving);
        }

        EnchantmentManager.b(entityliving, entity);
    }
}
