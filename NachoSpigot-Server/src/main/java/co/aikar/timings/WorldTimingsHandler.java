package co.aikar.timings;

import net.minecraft.server.World;

/**
 * Set of timers per world, to track world specific timings.
 */
public class WorldTimingsHandler {
    public final Timing mobSpawn;
    public final Timing doChunkUnload;
    public final Timing doPortalForcer;
    public final Timing scheduledBlocks;
    public final Timing scheduledBlocksCleanup;
    public final Timing scheduledBlocksTicking;
    public final Timing chunkTicks;
    public final Timing chunkTicksBlocks;
    public final Timing doVillages;
    public final Timing doChunkMap;
    public final Timing doChunkGC;
    public final Timing doSounds;
    public final Timing entityRemoval;
    public final Timing entityTick;
    public final Timing tileEntityTick;
    public final Timing tileEntityPending;
    public final Timing tracker;
    public final Timing doTick;
    public final Timing tickEntities;

    public final Timing syncChunkLoadTimer;
    public final Timing syncChunkLoadDataTimer;
    public final Timing syncChunkLoadStructuresTimer;
    public final Timing syncChunkLoadEntitiesTimer;
    public final Timing syncChunkLoadTileEntitiesTimer;
    public final Timing syncChunkLoadTileTicksTimer;
    public final Timing syncChunkLoadPostTimer;

    public WorldTimingsHandler(World server) {
        String name = server.worldData.getName() +" - ";

        mobSpawn = Timings.ofSafe(name + "mobSpawn");
        doChunkUnload = Timings.ofSafe(name + "doChunkUnload");
        scheduledBlocks = Timings.ofSafe(name + "Scheduled Blocks");
        scheduledBlocksCleanup = Timings.ofSafe(name + "Scheduled Blocks - Cleanup");
        scheduledBlocksTicking = Timings.ofSafe(name + "Scheduled Blocks - Ticking");
        chunkTicks = Timings.ofSafe(name + "Chunk Ticks");
        chunkTicksBlocks = Timings.ofSafe(name + "Chunk Ticks - Blocks");
        doVillages = Timings.ofSafe(name + "doVillages");
        doChunkMap = Timings.ofSafe(name + "doChunkMap");
        doSounds = Timings.ofSafe(name + "doSounds");
        doChunkGC = Timings.ofSafe(name + "doChunkGC");
        doPortalForcer = Timings.ofSafe(name + "doPortalForcer");
        entityTick = Timings.ofSafe(name + "entityTick");
        entityRemoval = Timings.ofSafe(name + "entityRemoval");
        tileEntityTick = Timings.ofSafe(name + "tileEntityTick");
        tileEntityPending = Timings.ofSafe(name + "tileEntityPending");

        syncChunkLoadTimer = Timings.ofSafe(name + "syncChunkLoad");
        syncChunkLoadDataTimer = Timings.ofSafe(name + "syncChunkLoad - Data");
        syncChunkLoadStructuresTimer = Timings.ofSafe(name + "chunkLoad - Structures");
        syncChunkLoadEntitiesTimer = Timings.ofSafe(name + "chunkLoad - Entities");
        syncChunkLoadTileEntitiesTimer = Timings.ofSafe(name + "chunkLoad - TileEntities");
        syncChunkLoadTileTicksTimer = Timings.ofSafe(name + "chunkLoad - TileTicks");
        syncChunkLoadPostTimer = Timings.ofSafe(name + "chunkLoad - Post");

        tracker = Timings.ofSafe(name + "tracker");
        doTick = Timings.ofSafe(name + "doTick");
        tickEntities = Timings.ofSafe(name + "tickEntities");
    }
}
