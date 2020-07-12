package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegionLoader implements IChunkLoader, IAsyncChunkSaver {

    private static final Logger a = LogManager.getLogger();
    private Map<ChunkCoordIntPair, NBTTagCompound> b = new ConcurrentHashMap();
    private Set<ChunkCoordIntPair> c = Collections.newSetFromMap(new ConcurrentHashMap());
    private final File d;
    private boolean e = false;

    public ChunkRegionLoader(File file) {
        this.d = file;
    }

    // CraftBukkit start
    public boolean chunkExists(World world, int i, int j) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);

        if (this.c.contains(chunkcoordintpair)) {
            if (this.b.containsKey(chunkcoordintpair)) {
                return true;
            }
        }

        final RegionFile region = RegionFileCache.a(this.d, i, j, false); // PaperSpigot
        return region != null && region.chunkExists(i & 31, j & 31); // PaperSpigot
    }
    // CraftBukkit end

    // CraftBukkit start - Add async variant, provide compatibility
    public Chunk a(World world, int i, int j) throws IOException {
        world.timings.syncChunkLoadDataTimer.startTiming(); // Spigot
        Object[] data = loadChunk(world, i, j);
        world.timings.syncChunkLoadDataTimer.stopTiming(); // Spigot
        if (data != null) {
            Chunk chunk = (Chunk) data[0];
            NBTTagCompound nbttagcompound = (NBTTagCompound) data[1];
            loadEntities(chunk, nbttagcompound.getCompound("Level"), world);
            return chunk;
        }

        return null;
    }

    public Object[] loadChunk(World world, int i, int j) throws IOException {
        // CraftBukkit end
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        NBTTagCompound nbttagcompound = (NBTTagCompound) this.b.get(chunkcoordintpair);

        if (nbttagcompound == null) {
            DataInputStream datainputstream = RegionFileCache.c(this.d, i, j);

            if (datainputstream == null) {
                return null;
            }

            nbttagcompound = NBTCompressedStreamTools.a(datainputstream);
        }

        return this.a(world, i, j, nbttagcompound);
    }

    protected Object[] a(World world, int i, int j, NBTTagCompound nbttagcompound) { // CraftBukkit - return Chunk -> Object[]
        if (!nbttagcompound.hasKeyOfType("Level", 10)) {
            ChunkRegionLoader.a.error("Chunk file at " + i + "," + j + " is missing level data, skipping");
            return null;
        } else {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");

            if (!nbttagcompound1.hasKeyOfType("Sections", 9)) {
                ChunkRegionLoader.a.error("Chunk file at " + i + "," + j + " is missing block data, skipping");
                return null;
            } else {
                Chunk chunk = this.a(world, nbttagcompound1);

                if (!chunk.a(i, j)) {
                    ChunkRegionLoader.a.error("Chunk file at " + i + "," + j + " is in the wrong location; relocating. (Expected " + i + ", " + j + ", got " + chunk.locX + ", " + chunk.locZ + ")");
                    nbttagcompound1.setInt("xPos", i);
                    nbttagcompound1.setInt("zPos", j);

                    // CraftBukkit start - Have to move tile entities since we don't load them at this stage
                    NBTTagList tileEntities = nbttagcompound.getCompound("Level").getList("TileEntities", 10);
                    if (tileEntities != null) {
                        for (int te = 0; te < tileEntities.size(); te++) {
                            NBTTagCompound tileEntity = (NBTTagCompound) tileEntities.get(te);
                            int x = tileEntity.getInt("x") - chunk.locX * 16;
                            int z = tileEntity.getInt("z") - chunk.locZ * 16;
                            tileEntity.setInt("x", i * 16 + x);
                            tileEntity.setInt("z", j * 16 + z);
                        }
                    }
                    // CraftBukkit end
                    chunk = this.a(world, nbttagcompound1);
                }

                // CraftBukkit start
                Object[] data = new Object[2];
                data[0] = chunk;
                data[1] = nbttagcompound;
                return data;
                // CraftBukkit end
            }
        }
    }

    public void a(World world, Chunk chunk) throws IOException, ExceptionWorldConflict {
        world.checkSession();

        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound.set("Level", nbttagcompound1);
            this.a(chunk, world, nbttagcompound1);
            this.a(chunk.j(), nbttagcompound);
        } catch (Exception exception) {
            ChunkRegionLoader.a.error("Failed to save chunk", exception);
        }

    }

    protected void a(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        if (!this.c.contains(chunkcoordintpair)) {
            this.b.put(chunkcoordintpair, nbttagcompound);
        }

        FileIOThread.a().a(this);
    }

    public boolean c() {
        if (this.b.isEmpty()) {
            if (this.e) {
                ChunkRegionLoader.a.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[] { this.d.getName()});
            }

            return false;
        } else {
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) this.b.keySet().iterator().next();

            boolean flag;

            try {
                this.c.add(chunkcoordintpair);
                NBTTagCompound nbttagcompound = (NBTTagCompound) this.b.remove(chunkcoordintpair);

                if (nbttagcompound != null) {
                    try {
                        this.b(chunkcoordintpair, nbttagcompound);
                    } catch (Exception exception) {
                        ChunkRegionLoader.a.error("Failed to save chunk", exception);
                    }
                }

                flag = true;
            } finally {
                this.c.remove(chunkcoordintpair);
            }

            return flag;
        }
    }

    private void b(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) throws IOException {
        DataOutputStream dataoutputstream = RegionFileCache.d(this.d, chunkcoordintpair.x, chunkcoordintpair.z);

        NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) dataoutputstream);
        dataoutputstream.close();
    }

    public void b(World world, Chunk chunk) throws IOException {}

    public void a() {}

    public void b() {
        try {
            this.e = true;

            while (true) {
                if (this.c()) {
                    continue;
                }
            }
        } finally {
            this.e = false;
        }

    }

    private void a(Chunk chunk, World world, NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("V", (byte) 1);
        nbttagcompound.setInt("xPos", chunk.locX);
        nbttagcompound.setInt("zPos", chunk.locZ);
        nbttagcompound.setLong("LastUpdate", world.getTime());
        nbttagcompound.setIntArray("HeightMap", chunk.q());
        nbttagcompound.setBoolean("TerrainPopulated", chunk.isDone());
        nbttagcompound.setBoolean("LightPopulated", chunk.u());
        nbttagcompound.setLong("InhabitedTime", chunk.w());
        ChunkSection[] achunksection = chunk.getSections();
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = !world.worldProvider.o();
        ChunkSection[] achunksection1 = achunksection;
        int i = achunksection.length;

        NBTTagCompound nbttagcompound1;

        for (int j = 0; j < i; ++j) {
            ChunkSection chunksection = achunksection1[j];

            if (chunksection != null) {
                nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Y", (byte) (chunksection.getYPosition() >> 4 & 255));
                byte[] abyte = new byte[chunksection.getIdArray().length];
                NibbleArray nibblearray = new NibbleArray();
                NibbleArray nibblearray1 = null;

                for (int k = 0; k < chunksection.getIdArray().length; ++k) {
                    char c0 = chunksection.getIdArray()[k];
                    int l = k & 15;
                    int i1 = k >> 8 & 15;
                    int j1 = k >> 4 & 15;

                    if (c0 >> 12 != 0) {
                        if (nibblearray1 == null) {
                            nibblearray1 = new NibbleArray();
                        }

                        nibblearray1.a(l, i1, j1, c0 >> 12);
                    }

                    abyte[k] = (byte) (c0 >> 4 & 255);
                    nibblearray.a(l, i1, j1, c0 & 15);
                }

                nbttagcompound1.setByteArray("Blocks", abyte);
                nbttagcompound1.setByteArray("Data", nibblearray.a());
                if (nibblearray1 != null) {
                    nbttagcompound1.setByteArray("Add", nibblearray1.a());
                }

                nbttagcompound1.setByteArray("BlockLight", chunksection.getEmittedLightArray().a());
                if (flag) {
                    nbttagcompound1.setByteArray("SkyLight", chunksection.getSkyLightArray().a());
                } else {
                    nbttagcompound1.setByteArray("SkyLight", new byte[chunksection.getEmittedLightArray().a().length]);
                }

                nbttaglist.add(nbttagcompound1);
            }
        }

        nbttagcompound.set("Sections", nbttaglist);
        nbttagcompound.setByteArray("Biomes", chunk.getBiomeIndex());
        chunk.g(false);
        NBTTagList nbttaglist1 = new NBTTagList();

        Iterator iterator;

        for (i = 0; i < chunk.getEntitySlices().length; ++i) {
            iterator = chunk.getEntitySlices()[i].iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                nbttagcompound1 = new NBTTagCompound();
                if (entity.d(nbttagcompound1)) {
                    chunk.g(true);
                    nbttaglist1.add(nbttagcompound1);
                }
            }
        }

        nbttagcompound.set("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();

        iterator = chunk.getTileEntities().values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            tileentity.b(nbttagcompound1);
            nbttaglist2.add(nbttagcompound1);
        }

        nbttagcompound.set("TileEntities", nbttaglist2);
        List list = world.a(chunk, false);

        if (list != null) {
            long k1 = world.getTime();
            NBTTagList nbttaglist3 = new NBTTagList();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                NextTickListEntry nextticklistentry = (NextTickListEntry) iterator1.next();
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.c(nextticklistentry.a());

                nbttagcompound2.setString("i", minecraftkey == null ? "" : minecraftkey.toString());
                nbttagcompound2.setInt("x", nextticklistentry.a.getX());
                nbttagcompound2.setInt("y", nextticklistentry.a.getY());
                nbttagcompound2.setInt("z", nextticklistentry.a.getZ());
                nbttagcompound2.setInt("t", (int) (nextticklistentry.b - k1));
                nbttagcompound2.setInt("p", nextticklistentry.c);
                nbttaglist3.add(nbttagcompound2);
            }

            nbttagcompound.set("TileTicks", nbttaglist3);
        }

    }

    private Chunk a(World world, NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("xPos");
        int j = nbttagcompound.getInt("zPos");
        Chunk chunk = new Chunk(world, i, j);

        chunk.a(nbttagcompound.getIntArray("HeightMap"));
        chunk.d(nbttagcompound.getBoolean("TerrainPopulated"));
        chunk.e(nbttagcompound.getBoolean("LightPopulated"));
        chunk.c(nbttagcompound.getLong("InhabitedTime"));
        NBTTagList nbttaglist = nbttagcompound.getList("Sections", 10);
        byte b0 = 16;
        ChunkSection[] achunksection = new ChunkSection[b0];
        boolean flag = !world.worldProvider.o();

        for (int k = 0; k < nbttaglist.size(); ++k) {
            NBTTagCompound nbttagcompound1 = nbttaglist.get(k);
            byte b1 = nbttagcompound1.getByte("Y");
            ChunkSection chunksection = new ChunkSection(b1 << 4, flag);
            byte[] abyte = nbttagcompound1.getByteArray("Blocks");
            NibbleArray nibblearray = new NibbleArray(nbttagcompound1.getByteArray("Data"));
            NibbleArray nibblearray1 = nbttagcompound1.hasKeyOfType("Add", 7) ? new NibbleArray(nbttagcompound1.getByteArray("Add")) : null;
            char[] achar = new char[abyte.length];

            for (int l = 0; l < achar.length; ++l) {
                int i1 = l & 15;
                int j1 = l >> 8 & 15;
                int k1 = l >> 4 & 15;
                int l1 = nibblearray1 != null ? nibblearray1.a(i1, j1, k1) : 0;

                // CraftBukkit start - fix broken blocks
                // achar[l] = (char) (l1 << 12 | (abyte[l] & 255) << 4 | nibblearray.a(i1, j1, k1));

                int ex =  l1;
                int id = (abyte[l] & 255);
                int data = nibblearray.a(i1, j1, k1);
                int packed = ex << 12 | id << 4 | data;
                if (Block.d.a(packed) == null) {
                    Block block = Block.getById(ex << 8 | id);
                    if (block != null) {
                        try {
                            data = block.toLegacyData(block.fromLegacyData(data));
                        } catch (Exception ignored) {
                            data = block.toLegacyData(block.getBlockData());
                        }
                        packed = ex << 12 | id << 4 | data;
                    }
                }
                achar[l] = (char) packed;
                // CraftBukkit end
            }

            chunksection.a(achar);
            chunksection.a(new NibbleArray(nbttagcompound1.getByteArray("BlockLight")));
            if (flag) {
                chunksection.b(new NibbleArray(nbttagcompound1.getByteArray("SkyLight")));
            }

            chunksection.recalcBlockCounts();
            achunksection[b1] = chunksection;
        }

        chunk.a(achunksection);
        if (nbttagcompound.hasKeyOfType("Biomes", 7)) {
            chunk.a(nbttagcompound.getByteArray("Biomes"));
        }

        // CraftBukkit start - End this method here and split off entity loading to another method
        return chunk;
    }

    public void loadEntities(Chunk chunk, NBTTagCompound nbttagcompound, World world) {
        // CraftBukkit end
        world.timings.syncChunkLoadEntitiesTimer.startTiming(); // Spigot
        NBTTagList nbttaglist1 = nbttagcompound.getList("Entities", 10);

        if (nbttaglist1 != null) {
            for (int i2 = 0; i2 < nbttaglist1.size(); ++i2) {
                NBTTagCompound nbttagcompound2 = nbttaglist1.get(i2);
                Entity entity = EntityTypes.a(nbttagcompound2, world);

                chunk.g(true);
                if (entity != null) {
                    chunk.a(entity);
                    Entity entity1 = entity;

                    for (NBTTagCompound nbttagcompound3 = nbttagcompound2; nbttagcompound3.hasKeyOfType("Riding", 10); nbttagcompound3 = nbttagcompound3.getCompound("Riding")) {
                        Entity entity2 = EntityTypes.a(nbttagcompound3.getCompound("Riding"), world);

                        if (entity2 != null) {
                            chunk.a(entity2);
                            entity1.mount(entity2);
                        }

                        entity1 = entity2;
                    }
                }
            }
        }
        world.timings.syncChunkLoadEntitiesTimer.stopTiming(); // Spigot
        world.timings.syncChunkLoadTileEntitiesTimer.startTiming(); // Spigot
        NBTTagList nbttaglist2 = nbttagcompound.getList("TileEntities", 10);

        if (nbttaglist2 != null) {
            for (int j2 = 0; j2 < nbttaglist2.size(); ++j2) {
                NBTTagCompound nbttagcompound4 = nbttaglist2.get(j2);
                TileEntity tileentity = TileEntity.c(nbttagcompound4);

                if (tileentity != null) {
                    chunk.a(tileentity);
                }
            }
        }
        world.timings.syncChunkLoadTileEntitiesTimer.stopTiming(); // Spigot
        world.timings.syncChunkLoadTileTicksTimer.startTiming(); // Spigot

        if (nbttagcompound.hasKeyOfType("TileTicks", 9)) {
            NBTTagList nbttaglist3 = nbttagcompound.getList("TileTicks", 10);

            if (nbttaglist3 != null) {
                for (int k2 = 0; k2 < nbttaglist3.size(); ++k2) {
                    NBTTagCompound nbttagcompound5 = nbttaglist3.get(k2);
                    Block block;

                    if (nbttagcompound5.hasKeyOfType("i", 8)) {
                        block = Block.getByName(nbttagcompound5.getString("i"));
                    } else {
                        block = Block.getById(nbttagcompound5.getInt("i"));
                    }

                    world.b(new BlockPosition(nbttagcompound5.getInt("x"), nbttagcompound5.getInt("y"), nbttagcompound5.getInt("z")), block, nbttagcompound5.getInt("t"), nbttagcompound5.getInt("p"));
                }
            }
        }
        world.timings.syncChunkLoadTileTicksTimer.stopTiming(); // Spigot

        // return chunk; // CraftBukkit
    }
}
