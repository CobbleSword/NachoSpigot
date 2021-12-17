package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

// Nacho start
import dev.cobblesword.nachospigot.commons.Constants;
import dev.cobblesword.nachospigot.commons.minecraft.MCUtils;
import me.elier.nachospigot.config.NachoConfig;
import net.jafama.FastMath;
// Nacho end
// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import xyz.sculas.nacho.async.AsyncExplosions;
// CraftBukkit end

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Explosion {

    public static final Random CACHED_RANDOM = new Random();
    private final boolean a;
    private final boolean b;
    private final Random c = CACHED_RANDOM;
    private final World world;
    private final double posX;
    private final double posY;
    private final double posZ;
    public final Entity source;
    private final float size;
    private final List<BlockPosition> blocks = Lists.newArrayList();
    private final Map<EntityHuman, Vec3D> k = Maps.newHashMap();
    public boolean wasCanceled = false; // CraftBukkit - add field

    public Explosion(World world, Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        this.world = world;
        this.source = entity;
        this.size = (float) Math.max(f, 0.0); // CraftBukkit - clamp bad values
        this.posX = d0;
        this.posY = d1;
        this.posZ = d2;
        this.a = flag;
        this.b = flag1;
    }

    public void a() {
        // CraftBukkit start
        if (this.size < 0.1F) {
            return;
        }
        // CraftBukkit end
        // HashSet<BlockPosition> hashset = Sets.newHashSet();

        int i;
        int j;

        // IonSpigot start - Block Searching Improvements
        BlockPosition pos = new BlockPosition(posX, posY, posZ);
        Chunk chunk = world.getChunkAt(pos.getX() >> 4, pos.getZ() >> 4);
        Block b = chunk.getBlockData(pos).getBlock(); // TacoSpigot - get block of the explosion

        if (!this.world.tacoSpigotConfig.optimizeLiquidExplosions || !b.getMaterial().isLiquid()) { // TacoSpigot - skip calculating what blocks to blow up in water/lava
            it.unimi.dsi.fastutil.longs.LongSet set = new it.unimi.dsi.fastutil.longs.LongOpenHashSet();
            searchForBlocks(set, chunk);
            for (it.unimi.dsi.fastutil.longs.LongIterator iterator = set.iterator(); iterator.hasNext(); ) {
                this.blocks.add(BlockPosition.fromLong(iterator.nextLong()));
            }
        }

        // this.blocks.addAll(hashset);
        float f3 = this.size * 2.0F;

        // IonSpigot start - Faster Entity Iteration
        i = MathHelper.floor(this.posX - (double) f3 - 1.0D) >> 4;
        j = MathHelper.floor(this.posX + (double) f3 + 1.0D) >> 4;
        int l = MathHelper.clamp(MathHelper.floor(this.posY - (double) f3 - 1.0D) >> 4, 0, 15);
        int i1 = MathHelper.clamp(MathHelper.floor(this.posY + (double) f3 + 1.0D) >> 4, 0, 15);
        int j1 = MathHelper.floor(this.posZ - (double) f3 - 1.0D) >> 4;
        int k1 = MathHelper.floor(this.posZ + (double) f3 + 1.0D) >> 4;
        // PaperSpigot start - Fix lag from explosions processing dead entities
        // List<Entity> list = this.world.a(this.source, new AxisAlignedBB(i, l, j1, j, i1, k1), entity -> IEntitySelector.d.apply(entity) && !entity.dead);
        // PaperSpigot end
        Vec3D vec3d = new Vec3D(this.posX, this.posY, this.posZ);

        for (int chunkX = i; chunkX <= j; ++chunkX) {
            for (int chunkZ = j1; chunkZ <= k1; ++chunkZ) {
                chunk = world.getChunkIfLoaded(chunkX, chunkZ);

                if (chunk == null) {
                    continue;
                }

                for (int chunkY = l; chunkY <= i1; ++chunkY) {
                    affectEntities(chunk.entitySlices[chunkY], vec3d, f3);
                }
            }
        }
    }

    public void affectEntities(List<Entity> list, Vec3D vec3d, float f3) {
        for (Entity entity : list) {
            if (!entity.aW()) {
                if (!entity.dead) {
                    double d8 = entity.locX - this.posX;
                    double d9 = entity.locY + entity.getHeadHeight() - this.posY;
                    double d10 = entity.locZ - this.posZ;
                    double distanceSquared = d8 * d8 + d9 * d9 + d10 * d10;

                    if (distanceSquared <= 64.0D && distanceSquared != 0.0D) {
                        double d11 = MathHelper.sqrt(distanceSquared);
                        double d7 = d11 / (double) f3;
                        d8 /= d11;
                        d9 /= d11;
                        d10 /= d11;

                        // Paper - Optimize explosions
                        // double d12 = this.getBlockDensity(vec3d, entity);
                        double finalD = d8;
                        double finalD1 = d9;
                        double finalD11 = d10;
                        this.getBlockDensity(vec3d, entity.getBoundingBox()).thenAccept((d12) -> MCUtils.ensureMain(() -> {
                            double d13 = (1.0D - d7) * d12;

                            if (entity.isCannoningEntity) {
                                entity.g(finalD * d13, finalD1 * d13, finalD11 * d13);
                                return;
                            }
                            // IonSpigot end

                            // entity.damageEntity(DamageSource.explosion(this), (float) ((int) ((d13 * d13 + d13) / 2.0D * 8.0D * (double) f3 + 1.0D))); // CraftBukkit start
                            CraftEventFactory.entityDamage = source;
                            entity.forceExplosionKnockback = false;
                            boolean wasDamaged = entity.damageEntity(DamageSource.explosion(this), (float) ((int) ((d13 * d13 + d13) / 2.0D * 8.0D * (double) f3 + 1.0D)));
                            CraftEventFactory.entityDamage = null;

                            if (!wasDamaged && !(entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock) && !entity.forceExplosionKnockback) {
                                return;
                            }

                            // CraftBukkit end
                            double d14 = entity instanceof EntityHuman && world.paperSpigotConfig.disableExplosionKnockback ? 0 : EnchantmentProtection.a(entity, d13); // PaperSpigot

                            // PaperSpigot start - Fix cannons
                            // This impulse method sets the dirty flag, so clients will get an immediate velocity update
                            entity.g(finalD * d14, finalD1 * d14, finalD11 * d14);
                            // PaperSpigot end

                            if (entity instanceof EntityHuman && !((EntityHuman) entity).abilities.isInvulnerable && !world.paperSpigotConfig.disableExplosionKnockback) { // PaperSpigot
                                this.k.put((EntityHuman) entity, new Vec3D(finalD * d13, finalD1 * d13, finalD11 * d13));
                            }
                        }));
                    }
                }
            }
        }
    }

    public void a(boolean flag) {
        // PaperSpigot start - Configurable TNT explosion volume.
        float volume = source instanceof EntityTNTPrimed ? world.paperSpigotConfig.tntExplosionVolume : 4.0F;
        this.world.makeSound(this.posX, this.posY, this.posZ, "random.explode", volume, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);
        // PaperSpigot end
        if (this.size >= 2.0F && this.b) {
            this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, Constants.EMPTY_ARRAY);
        } else {
            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D, Constants.EMPTY_ARRAY);
        }

        Iterator iterator;
        BlockPosition blockposition;

        if (this.b) {
            // CraftBukkit start
            org.bukkit.World bworld = this.world.getWorld();
            org.bukkit.entity.Entity explode = this.source == null ? null : this.source.getBukkitEntity();
            Location location = new Location(bworld, this.posX, this.posY, this.posZ);

            List<org.bukkit.block.Block> blockList = Lists.newArrayList();
            for (int i1 = this.blocks.size() - 1; i1 >= 0; i1--) {
                BlockPosition cpos = this.blocks.get(i1);
                org.bukkit.block.Block bblock = bworld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
                if (bblock.getType() != org.bukkit.Material.AIR) {
                    blockList.add(bblock);
                }
            }

            boolean cancelled = false;
            List<org.bukkit.block.Block> bukkitBlocks = blockList;
            float yield = 0.3F; // default

            if (explode != null) {
                if (NachoConfig.fireEntityExplodeEvent) {
                    EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, yield);
                    this.world.getServer().getPluginManager().callEvent(event);
                    cancelled = event.isCancelled();
                    bukkitBlocks = event.blockList();
                    yield = event.getYield();
                }
            } else {
                BlockExplodeEvent event = new BlockExplodeEvent(location.getBlock(), blockList, yield);
                this.world.getServer().getPluginManager().callEvent(event);
                cancelled = event.isCancelled();
                bukkitBlocks = event.blockList();
                yield = event.getYield();
            }

            this.blocks.clear();

            for (org.bukkit.block.Block bblock : bukkitBlocks) {
                BlockPosition coords = new BlockPosition(bblock.getX(), bblock.getY(), bblock.getZ());
                blocks.add(coords);
            }

            if (cancelled) {
                this.wasCanceled = true;
                return;
            }
            // CraftBukkit end
            iterator = this.blocks.iterator();

            while (iterator.hasNext()) {
                blockposition = (BlockPosition) iterator.next();
                Block block = this.world.getType(blockposition).getBlock();

                world.spigotConfig.antiXrayInstance.updateNearbyBlocks(world, blockposition); // Spigot
                // IonSpigot start - Optimise Explosions
                /*
                if (flag) {
                    double d0 = (float) blockposition.getX() + this.world.random.nextFloat();
                    double d1 = (float) blockposition.getY() + this.world.random.nextFloat();
                    double d2 = (float) blockposition.getZ() + this.world.random.nextFloat();
                    double d3 = d0 - this.posX;
                    double d4 = d1 - this.posY;
                    double d5 = d2 - this.posZ;
                    double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5D / (d6 / (double) this.size + 0.1D);

                    d7 *= this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F;
                    d3 *= d7;
                    d4 *= d7;
                    d5 *= d7;
                    this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, (d0 + this.posX) / 2.0D, (d1 + this.posY) / 2.0D, (d2 + this.posZ) / 2.0D, d3, d4, d5);
                    this.world.addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
                }
                */
                // IonSpigot end

                if (block.getMaterial() != Material.AIR) {
                    if (block.a(this)) {
                        // CraftBukkit - add yield
                        block.dropNaturally(this.world, blockposition, this.world.getType(blockposition), yield, 0);
                    }

                    this.world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
                    block.wasExploded(this.world, blockposition, this);
                }
            }
        }

        if (this.a) {
            iterator = this.blocks.iterator();

            while (iterator.hasNext()) {
                blockposition = (BlockPosition) iterator.next();
                // Nacho - revert >> // Nacho - optimize TNT by Lew_x
                if (this.world.getType(blockposition).getBlock().getMaterial() == Material.AIR && this.world.getType(blockposition.down()).getBlock().o() && this.c.nextInt(3) == 0) {
                    // CraftBukkit start - Ignition by explosion
                    if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(this.world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this).isCancelled()) {
                        this.world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                    }
                    // CraftBukkit end
                }
            }
        }

    }

    public Map<EntityHuman, Vec3D> b() {
        return this.k;
    }

    public EntityLiving getSource() {
        // CraftBukkit start - obtain Fireball shooter for explosion tracking
        return this.source == null ? null : (this.source instanceof EntityTNTPrimed ? ((EntityTNTPrimed) this.source).getSource() : (this.source instanceof EntityLiving ? (EntityLiving) this.source : (this.source instanceof EntityFireball ? ((EntityFireball) this.source).shooter : null)));
        // CraftBukkit end
    }

    public void clearBlocks() {
        this.blocks.clear();
    }

    public List<BlockPosition> getBlocks() {
        return this.blocks;
    }

    // IonSpigot start - Block Searching Improvements
    private final static List<double[]> VECTORS = Lists.newArrayListWithCapacity(1352);

    static {
        for (int k = 0; k < 16; ++k) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (k == 0 || k == 15 || i == 0 || i == 15 || j == 0 || j == 15) {
                        double d0 = (float) k / 15.0F * 2.0F - 1.0F;
                        double d1 = (float) i / 15.0F * 2.0F - 1.0F;
                        double d2 = (float) j / 15.0F * 2.0F - 1.0F;
                        double d3 = (NachoConfig.enableFastMath ? FastMath.sqrt(d0 * d0 + d1 * d1 + d2 * d2) : Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2));

                        d0 = (d0 / d3) * 0.30000001192092896D;
                        d1 = (d1 / d3) * 0.30000001192092896D;
                        d2 = (d2 / d3) * 0.30000001192092896D;
                        VECTORS.add(new double[]{d0, d1, d2});
                    }
                }
            }
        }
    }

    // https://github.com/jellysquid3/lithium-fabric/blob/1.16.x/dev/src/main/java/me/jellysquid/mods/lithium/mixin/world/explosions/ExplosionMixin.java
    private void searchForBlocks(it.unimi.dsi.fastutil.longs.LongSet set, Chunk chunk) {
        BlockPosition.MutableBlockPosition position = new BlockPosition.MutableBlockPosition();

        for (double[] vector : VECTORS) {
            double d0 = vector[0];
            double d1 = vector[1];
            double d2 = vector[2];

            float f = this.size * (0.7F + (world.nachoSpigotConfig.constantExplosions ? 0.7F : this.world.random.nextFloat()) * 0.6F);
            float resistance = 0;

            double stepX = this.posX;
            double stepY = this.posY;
            double stepZ = this.posZ;

            for (; f > 0.0F; f -= 0.22500001F) {
                int floorX = (NachoConfig.enableFastMath ? FastMath.floorToInt((Double.doubleToRawLongBits(stepX) >>> 63)) : org.bukkit.util.NumberConversions.floor(stepX));
                int floorY = (NachoConfig.enableFastMath ? FastMath.floorToInt((Double.doubleToRawLongBits(stepY) >>> 63)) : org.bukkit.util.NumberConversions.floor(stepY));
                int floorZ = (NachoConfig.enableFastMath ? FastMath.floorToInt((Double.doubleToRawLongBits(stepZ) >>> 63)) : org.bukkit.util.NumberConversions.floor(stepZ));

                if (position.getX() != floorX || position.getY() != floorY || position.getZ() != floorZ) {
                    position.setValues(floorX, floorY, floorZ);

                    int chunkX = floorX >> 4;
                    int chunkZ = floorZ >> 4;
                    if (chunk == null || !chunk.o() || chunk.locX != chunkX || chunk.locZ != chunkZ) {
                        chunk = world.getChunkAt(chunkX, chunkZ);
                    }

                    IBlockData iblockdata = chunk.getBlockData(position);
                    Block block = iblockdata.getBlock();

                    if (block != Blocks.AIR) {
                        float blockResistance = block.durability / 5.0f;
                        resistance = (blockResistance + 0.3F) * 0.3F;
                        f -= resistance;

                        if (f > 0.0F && (this.source == null || this.source.a(this, this.world, position, iblockdata, f)) && position.getY() < 256 && position.getY() >= 0) { // CraftBukkit - don't wrap explosions
                            set.add(position.asLong());
                        }
                    }
                } else {
                    f -= resistance;
                }

                stepX += d0;
                stepY += d1;
                stepZ += d2;
            }
        }
    }
    // IonSpigot end

    // Paper start - Optimize explosions
    private CompletableFuture<Float> getBlockDensity(Vec3D vec3d, AxisAlignedBB aabb) {
        return CompletableFuture.supplyAsync(() -> {
            // IonSpigot start - Optimise Density Cache
            int key = createKey(this, aabb);
            float blockDensity = this.world.explosionDensityCache.get(key);
            if (blockDensity == -1.0f) {
                blockDensity = calculateDensity(vec3d, aabb);
                this.world.explosionDensityCache.put(key, blockDensity);
            }
            return blockDensity;
        }, AsyncExplosions.EXECUTOR);
    }

    private float calculateDensity(Vec3D vec3d, AxisAlignedBB aabb) {
        if (world.nachoSpigotConfig.reducedDensityRays) {
            return calculateDensityReducedRays(vec3d, aabb);
        } else {
            return this.world.a(vec3d, aabb);
        }
    }

    private float calculateDensityReducedRays(Vec3D vec3d, AxisAlignedBB aabb) {
        int arrived = 0;
        int rays = 0;

        for (Vec3D vector : calculateVectors(aabb)) {
            // If rays from the corners don't hit a block
            // it should be safe to return the best outcome
            if (rays == 8 && arrived == 8) {
                return 1.0F;
            }

            if (world.rayTrace(vector, vec3d) == null) {
                ++arrived;
            }

            ++rays;
        }

        return (float) arrived / (float) rays;
    }

    private List<Vec3D> calculateVectors(AxisAlignedBB aabb) {
        double d0 = 1.0D / ((aabb.d - aabb.a) * 2.0D + 1.0D);
        double d1 = 1.0D / ((aabb.e - aabb.b) * 2.0D + 1.0D);
        double d2 = 1.0D / ((aabb.f - aabb.c) * 2.0D + 1.0D);
        double d3 = (1.0D - ((NachoConfig.enableFastMath ? FastMath.floor(1.0D / d0) : Math.floor(1.0D / d0)) * d0)) / 2.0D;
        double d4 = (1.0D - ((NachoConfig.enableFastMath ? FastMath.floor(1.0D / d2) : Math.floor(1.0D / d2)) * d2)) / 2.0D;

        if (d0 < 0.0 || d1 < 0.0 || d2 < 0.0) {
            return Collections.emptyList();
        }

        List<Vec3D> vectors = new LinkedList<>();

        for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
            for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                    double d5 = aabb.a + (aabb.d - aabb.a) * (double) f;
                    double d6 = aabb.b + (aabb.e - aabb.b) * (double) f1;
                    double d7 = aabb.c + (aabb.f - aabb.c) * (double) f2;
                    Vec3D vector = new Vec3D(d5 + d3, d6, d7 + d4);

                    if ((f == 0 || f + d0 > 1.0F) && (f1 == 0 || f1 + d1 > 1.0F) && (f2 == 0 || f2 + d2 > 1.0F)) {
                        vectors.add(0, vector);
                    } else {
                        vectors.add(vector);
                    }
                }
            }
        }

        return vectors;
    }

    static int createKey(Explosion explosion, AxisAlignedBB aabb) {
        int result;
        long temp;
        result = explosion.world.hashCode();
        temp = Double.doubleToLongBits(explosion.posX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(explosion.posY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(explosion.posZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.a);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.c);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.d);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.e);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(aabb.f);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    /* :: IonSpigot - comment this out
    static class CacheKey {
        private final World world;
        private final double posX, posY, posZ;
        private final double minX, minY, minZ;
        private final double maxX, maxY, maxZ;

        public CacheKey(Explosion explosion, AxisAlignedBB aabb) {
            this.world = explosion.world;
            this.posX = explosion.posX;
            this.posY = explosion.posY;
            this.posZ = explosion.posZ;
            this.minX = aabb.getMinX();
            this.minY = aabb.getMinY();
            this.minZ = aabb.getMinZ();
            this.maxX = aabb.getMaxX();
            this.maxY = aabb.getMaxY();
            this.maxZ = aabb.getMaxZ();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            if (Double.compare(cacheKey.posX, posX) != 0) return false;
            if (Double.compare(cacheKey.posY, posY) != 0) return false;
            if (Double.compare(cacheKey.posZ, posZ) != 0) return false;
            if (Double.compare(cacheKey.minX, minX) != 0) return false;
            if (Double.compare(cacheKey.minY, minY) != 0) return false;
            if (Double.compare(cacheKey.minZ, minZ) != 0) return false;
            if (Double.compare(cacheKey.maxX, maxX) != 0) return false;
            if (Double.compare(cacheKey.maxY, maxY) != 0) return false;
            if (Double.compare(cacheKey.maxZ, maxZ) != 0) return false;
            return world.equals(cacheKey.world);
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = world.hashCode();
            temp = Double.doubleToLongBits(posX);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(posY);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(posZ);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(minX);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(minY);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(minZ);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(maxX);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(maxY);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(maxZ);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
    */
    // IonSpigot end
    // Paper end
}
