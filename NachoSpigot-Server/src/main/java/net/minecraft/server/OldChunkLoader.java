package net.minecraft.server;

public class OldChunkLoader {

    public static OldChunkLoader.OldChunk a(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("xPos");
        int j = nbttagcompound.getInt("zPos");
        OldChunkLoader.OldChunk oldchunkloader_oldchunk = new OldChunkLoader.OldChunk(i, j);

        oldchunkloader_oldchunk.g = nbttagcompound.getByteArray("Blocks");
        oldchunkloader_oldchunk.f = new OldNibbleArray(nbttagcompound.getByteArray("Data"), 7);
        oldchunkloader_oldchunk.e = new OldNibbleArray(nbttagcompound.getByteArray("SkyLight"), 7);
        oldchunkloader_oldchunk.d = new OldNibbleArray(nbttagcompound.getByteArray("BlockLight"), 7);
        oldchunkloader_oldchunk.c = nbttagcompound.getByteArray("HeightMap");
        oldchunkloader_oldchunk.b = nbttagcompound.getBoolean("TerrainPopulated");
        oldchunkloader_oldchunk.h = nbttagcompound.getList("Entities", 10);
        oldchunkloader_oldchunk.i = nbttagcompound.getList("TileEntities", 10);
        oldchunkloader_oldchunk.j = nbttagcompound.getList("TileTicks", 10);

        try {
            oldchunkloader_oldchunk.a = nbttagcompound.getLong("LastUpdate");
        } catch (ClassCastException classcastexception) {
            oldchunkloader_oldchunk.a = (long) nbttagcompound.getInt("LastUpdate");
        }

        return oldchunkloader_oldchunk;
    }

    public static void a(OldChunkLoader.OldChunk oldchunkloader_oldchunk, NBTTagCompound nbttagcompound, WorldChunkManager worldchunkmanager) {
        nbttagcompound.setInt("xPos", oldchunkloader_oldchunk.k);
        nbttagcompound.setInt("zPos", oldchunkloader_oldchunk.l);
        nbttagcompound.setLong("LastUpdate", oldchunkloader_oldchunk.a);
        int[] aint = new int[oldchunkloader_oldchunk.c.length];

        for (int i = 0; i < oldchunkloader_oldchunk.c.length; ++i) {
            aint[i] = oldchunkloader_oldchunk.c[i];
        }

        nbttagcompound.setIntArray("HeightMap", aint);
        nbttagcompound.setBoolean("TerrainPopulated", oldchunkloader_oldchunk.b);
        NBTTagList nbttaglist = new NBTTagList();

        int j;
        int k;

        for (int l = 0; l < 8; ++l) {
            boolean flag = true;

            for (j = 0; j < 16 && flag; ++j) {
                k = 0;

                while (k < 16 && flag) {
                    int i1 = 0;

                    while (true) {
                        if (i1 < 16) {
                            int j1 = j << 11 | i1 << 7 | k + (l << 4);
                            byte b0 = oldchunkloader_oldchunk.g[j1];

                            if (b0 == 0) {
                                ++i1;
                                continue;
                            }

                            flag = false;
                        }

                        ++k;
                        break;
                    }
                }
            }

            if (!flag) {
                byte[] abyte = new byte[4096];
                NibbleArray nibblearray = new NibbleArray();
                NibbleArray nibblearray1 = new NibbleArray();
                NibbleArray nibblearray2 = new NibbleArray();

                for (int k1 = 0; k1 < 16; ++k1) {
                    for (int l1 = 0; l1 < 16; ++l1) {
                        for (int i2 = 0; i2 < 16; ++i2) {
                            int j2 = k1 << 11 | i2 << 7 | l1 + (l << 4);
                            byte b1 = oldchunkloader_oldchunk.g[j2];

                            abyte[l1 << 8 | i2 << 4 | k1] = (byte) (b1 & 255);
                            nibblearray.a(k1, l1, i2, oldchunkloader_oldchunk.f.a(k1, l1 + (l << 4), i2));
                            nibblearray1.a(k1, l1, i2, oldchunkloader_oldchunk.e.a(k1, l1 + (l << 4), i2));
                            nibblearray2.a(k1, l1, i2, oldchunkloader_oldchunk.d.a(k1, l1 + (l << 4), i2));
                        }
                    }
                }

                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setByte("Y", (byte) (l & 255));
                nbttagcompound1.setByteArray("Blocks", abyte);
                nbttagcompound1.setByteArray("Data", nibblearray.a());
                nbttagcompound1.setByteArray("SkyLight", nibblearray1.a());
                nbttagcompound1.setByteArray("BlockLight", nibblearray2.a());
                nbttaglist.add(nbttagcompound1);
            }
        }

        nbttagcompound.set("Sections", nbttaglist);
        byte[] abyte1 = new byte[256];
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (j = 0; j < 16; ++j) {
            for (k = 0; k < 16; ++k) {
                blockposition_mutableblockposition.c(oldchunkloader_oldchunk.k << 4 | j, 0, oldchunkloader_oldchunk.l << 4 | k);
                abyte1[k << 4 | j] = (byte) (worldchunkmanager.getBiome(blockposition_mutableblockposition, BiomeBase.ad).id & 255);
            }
        }

        nbttagcompound.setByteArray("Biomes", abyte1);
        nbttagcompound.set("Entities", oldchunkloader_oldchunk.h);
        nbttagcompound.set("TileEntities", oldchunkloader_oldchunk.i);
        if (oldchunkloader_oldchunk.j != null) {
            nbttagcompound.set("TileTicks", oldchunkloader_oldchunk.j);
        }

    }

    public static class OldChunk {

        public long a;
        public boolean b;
        public byte[] c;
        public OldNibbleArray d;
        public OldNibbleArray e;
        public OldNibbleArray f;
        public byte[] g;
        public NBTTagList h;
        public NBTTagList i;
        public NBTTagList j;
        public final int k;
        public final int l;

        public OldChunk(int i, int j) {
            this.k = i;
            this.l = j;
        }
    }
}
