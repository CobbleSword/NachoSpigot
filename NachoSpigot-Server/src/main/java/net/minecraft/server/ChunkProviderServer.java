package net.minecraft.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import dev.cobblesword.nachospigot.events.ChunkPreLoadEvent;
import it.unimi.dsi.fastutil.longs.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import java.util.Random;
import org.bukkit.Server;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.event.world.ChunkUnloadEvent;
// CraftBukkit end

public class ChunkProviderServer implements IChunkProvider {

    private static final Logger b = LogManager.getLogger();
    public LongOpenHashSet unloadQueue = new LongOpenHashSet(); // CraftBukkit - LongHashSet // TacoSpigot - LongHashSet -> HashArraySet // Nacho - LongHashSet -> LongOpenHashSet
    public Chunk emptyChunk;
    public IChunkProvider chunkProvider;
    // FlamePaper - Make chunkLoader public
    public IChunkLoader chunkLoader;
    public boolean forceChunkLoad = false; // CraftBukkit - true -> false
    public Long2ObjectMap<Chunk> chunks = new Long2ObjectOpenHashMap<>(4096, 0.5f); // TacoSpigot - use trove Long2ObjectOpenHashMap instead of craftbukkit implementation (using inital capacity and load factor chosen by Amaranth in an old impl)
    public WorldServer world;

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
        this.emptyChunk = new EmptyChunk(worldserver, Integer.MIN_VALUE, Integer.MIN_VALUE); // MinetickMod
        this.world = worldserver;
        this.chunkLoader = ichunkloader;
        this.chunkProvider = ichunkprovider;
    }

    public boolean isChunkLoaded(int i, int j) {
        return this.chunks.containsKey(LongHash.toLong(i, j)); // CraftBukkit
    }

    // CraftBukkit start - Change return type to Collection and return the values of our chunk map
    public java.util.Collection<Chunk> a() {
        // return this.chunkList;
        return this.chunks.values();
        // CraftBukkit end
    }

    public void queueUnload(int i, int j) {
        if (!world.nachoSpigotConfig.doChunkUnload) {
            return;
        }

        long key = LongHash.toLong(i, j); // IonSpigot - Only create key once

        // PaperSpigot start - Asynchronous lighting updates
        Chunk chunk = chunks.get(key);
        if (chunk != null && chunk.world.paperSpigotConfig.useAsyncLighting && (chunk.pendingLightUpdates.get() > 0 || chunk.world.getTime() - chunk.lightUpdateTime < 20)) {
            return;
        }
        // PaperSpigot end
        // PaperSpigot start - Don't unload chunk if it contains an entity that loads chunks
        if (chunk != null) {
            for (List<Entity> entities : chunk.entitySlices) {
                for (Entity entity : entities) {
                    if (entity.loadChunks) {
                        return;
                    }
                }
            }
        }
        // PaperSpigot end
        if (this.world.worldProvider.e()) {
            if (!this.world.shouldStayLoaded(i, j)) { // Nacho - deobfuscate shouldStayLoaded
                // CraftBukkit start
                this.unloadQueue.add(key);  // TacoSpigot - directly invoke LongHash

                Chunk c = chunks.get(key);
                if (c != null) {
                    c.mustSave = true;
                }
                // CraftBukkit end
            }
        } else {
            // CraftBukkit start
            this.unloadQueue.add(key); // TacoSpigot - directly invoke LongHash

            Chunk c = chunks.get(key);
            if (c != null) {
                c.mustSave = true;
            }
            // CraftBukkit end
        }

    }

    public void b() {
        Iterator iterator = this.chunks.values().iterator();

        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();

            this.queueUnload(chunk.locX, chunk.locZ);
        }

    }

    private boolean callChunkPreLoad(int i, int j)
    {
        ChunkPreLoadEvent event = new ChunkPreLoadEvent(world.getWorld(), i, j);
        world.getServer().getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    // CraftBukkit start - Add async variant, provide compatibility
    public Chunk getChunkIfLoaded(int x, int z) {
        return chunks.get(LongHash.toLong(x, z));
    }

    public Chunk getChunkAt(int i, int j) {
        return getChunkAt(i, j, null);
    }

    public Chunk getChunkAt(int i, int j, Runnable runnable) {
        long key = LongHash.toLong(i, j); // IonSpigot - Only create key once
        unloadQueue.remove(key); // TacoSpigot - directly invoke LongHash
        Chunk chunk = chunks.get(key);
        ChunkRegionLoader loader = null;

        if (this.chunkLoader instanceof ChunkRegionLoader) {
            loader = (ChunkRegionLoader) this.chunkLoader;

        }
        // We can only use the queue for already generated chunks
        if (chunk == null && loader != null && loader.chunkExists(world, i, j)) {
            if (runnable != null) {
                if (callChunkPreLoad(i, j))
                {
                    runnable.run();
                    chunk = new EmptyChunk(world, i, j);
                    chunk.setDone(true);
                    chunks.put(LongHash.toLong(i, j), chunk);
                }
                else
                {
                    ChunkIOExecutor.queueChunkLoad(world, loader, this, i, j, runnable);
                }
                return null;
            } else {
                if (callChunkPreLoad(i, j))
                {
                    chunk = new EmptyChunk(world, i, j);
                    chunk.setDone(true);
                    chunks.put(LongHash.toLong(i, j), chunk);
                }
                else
                {
                    chunk = ChunkIOExecutor.syncChunkLoad(world, loader, this, i, j);
                }
            }
        } else if (chunk == null) {
            chunk = originalGetChunkAt(i, j);
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null) {
            runnable.run();
        }

        return chunk;
    }
    public Chunk originalGetChunkAt(int i, int j) {
        long key = LongHash.toLong(i, j); // IonSpigot - Only create key once
        this.unloadQueue.remove(key); // TacoSpigot - directly invoke LongHash
        Chunk chunk = (Chunk) this.chunks.get(key);
        boolean newChunk = false;
        // CraftBukkit end

        Server server = world.getServer();

        if (chunk == null && server != null)
        {
            if (callChunkPreLoad(i, j))
            {
                chunk = new EmptyChunk(world, i, j);
                chunk.setDone(true);
                chunks.put(LongHash.toLong(i, j), chunk);

                return chunk;
            }
        }

        if (chunk == null) {
            world.timings.syncChunkLoadTimer.startTiming(); // Spigot
            chunk = this.loadChunk(i, j);
            if (chunk == null) {
                if (this.chunkProvider == null) {
                    chunk = this.emptyChunk;
                } else {
                    try {
                        chunk = this.chunkProvider.getOrCreateChunk(i, j);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");

                        crashreportsystemdetails.a("Location", (Object) String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j)}));
                        crashreportsystemdetails.a("Position hash", (Object) key); // CraftBukkit - Use LongHash
                        crashreportsystemdetails.a("Generator", (Object) this.chunkProvider.getName());
                        throw new ReportedException(crashreport);
                    }
                }
                newChunk = true; // CraftBukkit
            }

            this.chunks.put(key, chunk);
            
            chunk.addEntities();
            
            // CraftBukkit start
            if (server != null) {
                /*
                 * If it's a new world, the first few chunks are generated inside
                 * the World constructor. We can't reliably alter that, so we have
                 * no way of creating a CraftWorld/CraftServer at that point.
                 */
                server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk.bukkitChunk, newChunk));
            }

            // Update neighbor counts
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    if (x == 0 && z == 0) {
                        continue;
                    }

                    Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                    if (neighbor != null) {
                        neighbor.setNeighborLoaded(-x, -z);
                        chunk.setNeighborLoaded(x, z);
                    }
                }
            }
            // CraftBukkit end
            chunk.loadNearby(this, this, i, j);
            world.timings.syncChunkLoadTimer.stopTiming(); // Spigot
        }

        return chunk;
    }

    public Chunk getOrCreateChunk(int i, int j) {
        // CraftBukkit start
        Chunk chunk = (Chunk) this.chunks.get(LongHash.toLong(i, j));

        chunk = chunk == null ? (!this.world.ad() && !this.forceChunkLoad ? this.emptyChunk : this.getChunkAt(i, j)) : chunk;

        if (chunk == emptyChunk) return chunk;
        if (i != chunk.locX || j != chunk.locZ) {
            b.error("Chunk (" + chunk.locX + ", " + chunk.locZ + ") stored at  (" + i + ", " + j + ") in world '" + world.getWorld().getName() + "'");
            b.error(chunk.getClass().getName());
            Throwable ex = new Throwable();
            ex.fillInStackTrace();
            ex.printStackTrace();
        }

        return chunk;
        // CraftBukkit end
    }

    public Chunk loadChunk(int i, int j) {
        if (this.chunkLoader == null) {
            return null;
        } else {
            try {
                Chunk chunk = this.chunkLoader.a(this.world, i, j);

                if (chunk != null) {
                    chunk.setLastSaved(this.world.getTime());
                    if (this.chunkProvider != null) {
                        world.timings.syncChunkLoadStructuresTimer.startTiming(); // Spigot
                        this.chunkProvider.recreateStructures(chunk, i, j);
                        world.timings.syncChunkLoadStructuresTimer.stopTiming(); // Spigot
                    }
                }

                return chunk;
            } catch (Exception exception) {
                ChunkProviderServer.b.error("Couldn\'t load chunk", exception);
                return null;
            }
        }
    }

    public void saveChunkNOP(Chunk chunk) {
        if (this.chunkLoader != null) {
            try {
                this.chunkLoader.b(this.world, chunk);
            } catch (Exception exception) {
                ChunkProviderServer.b.error("Couldn\'t save entities", exception);
            }

        }
    }

    public void saveChunk(Chunk chunk) {
        if (this.chunkLoader != null) {
            try {
                chunk.setLastSaved(this.world.getTime());
                this.chunkLoader.a(this.world, chunk);
            } catch (IOException ioexception) {
                ChunkProviderServer.b.error("Couldn\'t save chunk", ioexception);
            } catch (ExceptionWorldConflict exceptionworldconflict) {
                ChunkProviderServer.b.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", exceptionworldconflict);
            }

        }
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        Chunk chunk = this.getOrCreateChunk(i, j);

        if (!chunk.isDone()) {
            chunk.n();
            if (this.chunkProvider != null) {
                this.chunkProvider.getChunkAt(ichunkprovider, i, j);

                // CraftBukkit start
                BlockSand.instaFall = true;
                Random random = new Random();
                random.setSeed(world.getSeed());
                long xRand = random.nextLong() / 2L * 2L + 1L;
                long zRand = random.nextLong() / 2L * 2L + 1L;
                random.setSeed((long) i * xRand + (long) j * zRand ^ world.getSeed());

                org.bukkit.World world = this.world.getWorld();
                if (world != null) {
                    this.world.populating = true;
                    try {
                        for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
                            populator.populate(world, random, chunk.bukkitChunk);
                        }
                    } finally {
                        this.world.populating = false;
                    }
                }
                BlockSand.instaFall = false;
                this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.world.ChunkPopulateEvent(chunk.bukkitChunk));
                // CraftBukkit end
                
                chunk.e();
            }
        }

    }

    public boolean a(IChunkProvider ichunkprovider, Chunk chunk, int i, int j) {
        if (this.chunkProvider != null && this.chunkProvider.a(ichunkprovider, chunk, i, j)) {
            Chunk chunk1 = this.getOrCreateChunk(i, j);

            chunk1.e();
            return true;
        } else {
            return false;
        }
    }

    public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
        int i = 0;

        // CraftBukkit start
        Iterator iterator = this.chunks.values().iterator();
        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();
            // CraftBukkit end

            if (flag) {
                this.saveChunkNOP(chunk);
            }

            if (chunk.a(flag)) {
                this.saveChunk(chunk);
                chunk.f(false);
                ++i;
                if (i == 24 && !flag && false) { // Spigot
                    return false;
                }
            }
        }

        return true;
    }

    public void c() {
        if (this.chunkLoader != null) {
            this.chunkLoader.b();
        }

    }

    public boolean unloadChunks() {
        // CraftBukkit start
        Server server = this.world.getServer();
        // TacoSpigot start - use iterator for unloadQueue
        LongIterator iterator = unloadQueue.iterator();
        for (int i = 0; i < 100 && iterator.hasNext(); ++i) {
            long chunkcoordinates = iterator.nextLong();
            iterator.remove();
            // TacoSpigot end
            Chunk chunk = this.chunks.get(chunkcoordinates);
            if (chunk == null) continue;

            ChunkUnloadEvent event = new ChunkUnloadEvent(chunk.bukkitChunk);
            server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {

                if (chunk != null) {
                    chunk.removeEntities();
                    if (!this.world.savingDisabled) {
                        this.saveChunk(chunk);
                        this.saveChunkNOP(chunk);
                    }
                    this.chunks.remove(chunkcoordinates); // CraftBukkit
                }

                // this.unloadQueue.remove(olong);

                // Update neighbor counts
                for (int x = -2; x < 3; x++) {
                    for (int z = -2; z < 3; z++) {
                        if (x == 0 && z == 0) {
                            continue;
                        }

                        Chunk neighbor = this.getChunkIfLoaded(chunk.locX + x, chunk.locZ + z);
                        if (neighbor != null) {
                            neighbor.setNeighborUnloaded(-x, -z);
                            chunk.setNeighborUnloaded(x, z);
                        }
                    }
                }
            }
            // CraftBukkit end

            if (this.chunkLoader != null) {
                this.chunkLoader.a();
            }
        }

        return this.chunkProvider.unloadChunks();
    }

    public boolean canSave() {
        return !this.world.savingDisabled;
    }

    public String getName() {
        // CraftBukkit - this.chunks.count() -> .size()
        return "ServerChunkCache: " + this.chunks.size() + " Drop: " + this.unloadQueue.size();
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return this.chunkProvider.getMobsFor(enumcreaturetype, blockposition);
    }

    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition) {
        return this.chunkProvider.findNearestMapFeature(world, s, blockposition);
    }

    public int getLoadedChunks() {
        // CraftBukkit - this.chunks.count() -> this.chunks.size()
        return this.chunks.size();
    }

    public void recreateStructures(Chunk chunk, int i, int j) {}

    public Chunk getChunkAt(BlockPosition blockposition) {
        return this.getOrCreateChunk(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }
}
