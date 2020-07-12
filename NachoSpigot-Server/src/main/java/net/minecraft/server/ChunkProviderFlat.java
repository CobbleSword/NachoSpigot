package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChunkProviderFlat implements IChunkProvider {

    private World a;
    private Random b;
    private final IBlockData[] c = new IBlockData[256];
    private final WorldGenFlatInfo d;
    private final List<StructureGenerator> e = Lists.newArrayList();
    private final boolean f;
    private final boolean g;
    private WorldGenLakes h;
    private WorldGenLakes i;

    public ChunkProviderFlat(World world, long i, boolean flag, String s) {
        this.a = world;
        this.b = new Random(i);
        this.d = WorldGenFlatInfo.a(s);
        if (flag) {
            Map map = this.d.b();

            if (map.containsKey("village") && world.paperSpigotConfig.generateVillage) { // PaperSpigot
                Map map1 = (Map) map.get("village");

                if (!map1.containsKey("size")) {
                    map1.put("size", "1");
                }

                this.e.add(new WorldGenVillage(map1));
            }

            if (map.containsKey("biome_1") && world.paperSpigotConfig.generateTemple) { // PaperSpigot
                this.e.add(new WorldGenLargeFeature((Map) map.get("biome_1")));
            }

            if (map.containsKey("mineshaft") && world.paperSpigotConfig.generateMineshaft) { // PaperSpigot
                this.e.add(new WorldGenMineshaft((Map) map.get("mineshaft")));
            }

            if (map.containsKey("stronghold") && world.paperSpigotConfig.generateStronghold) { // PaperSpigot
                this.e.add(new WorldGenStronghold((Map) map.get("stronghold")));
            }

            if (map.containsKey("oceanmonument") && world.paperSpigotConfig.generateMonument) { // PaperSpigot
                this.e.add(new WorldGenMonument((Map) map.get("oceanmonument")));
            }
        }

        if (this.d.b().containsKey("lake")) {
            this.h = new WorldGenLakes(Blocks.WATER);
        }

        if (this.d.b().containsKey("lava_lake")) {
            this.i = new WorldGenLakes(Blocks.LAVA);
        }

        this.g = world.paperSpigotConfig.generateDungeon && this.d.b().containsKey("dungeon");  // PaperSpigot
        int j = 0;
        int k = 0;
        boolean flag1 = true;
        Iterator iterator = this.d.c().iterator();

        while (iterator.hasNext()) {
            WorldGenFlatLayerInfo worldgenflatlayerinfo = (WorldGenFlatLayerInfo) iterator.next();

            for (int l = worldgenflatlayerinfo.d(); l < worldgenflatlayerinfo.d() + worldgenflatlayerinfo.b(); ++l) {
                IBlockData iblockdata = worldgenflatlayerinfo.c();

                if (iblockdata.getBlock() != Blocks.AIR) {
                    flag1 = false;
                    this.c[l] = iblockdata;
                }
            }

            if (worldgenflatlayerinfo.c().getBlock() == Blocks.AIR) {
                k += worldgenflatlayerinfo.b();
            } else {
                j += worldgenflatlayerinfo.b() + k;
                k = 0;
            }
        }

        world.b(j);
        this.f = flag1 ? false : this.d.b().containsKey("decoration");
    }

    public Chunk getOrCreateChunk(int i, int j) {
        ChunkSnapshot chunksnapshot = new ChunkSnapshot();

        int k;

        for (int l = 0; l < this.c.length; ++l) {
            IBlockData iblockdata = this.c[l];

            if (iblockdata != null) {
                for (int i1 = 0; i1 < 16; ++i1) {
                    for (k = 0; k < 16; ++k) {
                        chunksnapshot.a(i1, l, k, iblockdata);
                    }
                }
            }
        }

        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            WorldGenBase worldgenbase = (WorldGenBase) iterator.next();

            worldgenbase.a(this, this.a, i, j, chunksnapshot);
        }

        Chunk chunk = new Chunk(this.a, chunksnapshot, i, j);
        BiomeBase[] abiomebase = this.a.getWorldChunkManager().getBiomeBlock((BiomeBase[]) null, i * 16, j * 16, 16, 16);
        byte[] abyte = chunk.getBiomeIndex();

        for (k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) abiomebase[k].id;
        }

        chunk.initLighting();
        return chunk;
    }

    public boolean isChunkLoaded(int i, int j) {
        return true;
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        int k = i * 16;
        int l = j * 16;
        BlockPosition blockposition = new BlockPosition(k, 0, l);
        BiomeBase biomebase = this.a.getBiome(new BlockPosition(k + 16, 0, l + 16));
        boolean flag = false;

        this.b.setSeed(this.a.getSeed());
        long i1 = this.b.nextLong() / 2L * 2L + 1L;
        long j1 = this.b.nextLong() / 2L * 2L + 1L;

        this.b.setSeed((long) i * i1 + (long) j * j1 ^ this.a.getSeed());
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            StructureGenerator structuregenerator = (StructureGenerator) iterator.next();
            boolean flag1 = structuregenerator.a(this.a, this.b, chunkcoordintpair);

            if (structuregenerator instanceof WorldGenVillage) {
                flag |= flag1;
            }
        }

        if (this.h != null && !flag && this.b.nextInt(4) == 0) {
            this.h.generate(this.a, this.b, blockposition.a(this.b.nextInt(16) + 8, this.b.nextInt(256), this.b.nextInt(16) + 8));
        }

        if (this.i != null && !flag && this.b.nextInt(8) == 0) {
            BlockPosition blockposition1 = blockposition.a(this.b.nextInt(16) + 8, this.b.nextInt(this.b.nextInt(248) + 8), this.b.nextInt(16) + 8);

            if (blockposition1.getY() < this.a.F() || this.b.nextInt(10) == 0) {
                this.i.generate(this.a, this.b, blockposition1);
            }
        }

        if (this.g) {
            for (int k1 = 0; k1 < 8; ++k1) {
                (new WorldGenDungeons()).generate(this.a, this.b, blockposition.a(this.b.nextInt(16) + 8, this.b.nextInt(256), this.b.nextInt(16) + 8));
            }
        }

        if (this.f) {
            biomebase.a(this.a, this.b, blockposition);
        }

    }

    public boolean a(IChunkProvider ichunkprovider, Chunk chunk, int i, int j) {
        return false;
    }

    public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
        return true;
    }

    public void c() {}

    public boolean unloadChunks() {
        return false;
    }

    public boolean canSave() {
        return true;
    }

    public String getName() {
        return "FlatLevelSource";
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        BiomeBase biomebase = this.a.getBiome(blockposition);

        return biomebase.getMobs(enumcreaturetype);
    }

    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition) {
        if ("Stronghold".equals(s)) {
            Iterator iterator = this.e.iterator();

            while (iterator.hasNext()) {
                StructureGenerator structuregenerator = (StructureGenerator) iterator.next();

                if (structuregenerator instanceof WorldGenStronghold) {
                    return structuregenerator.getNearestGeneratedFeature(world, blockposition);
                }
            }
        }

        return null;
    }

    public int getLoadedChunks() {
        return 0;
    }

    public void recreateStructures(Chunk chunk, int i, int j) {
        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            StructureGenerator structuregenerator = (StructureGenerator) iterator.next();

            structuregenerator.a(this, this.a, i, j, (ChunkSnapshot) null);
        }

    }

    public Chunk getChunkAt(BlockPosition blockposition) {
        return this.getOrCreateChunk(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }
}
