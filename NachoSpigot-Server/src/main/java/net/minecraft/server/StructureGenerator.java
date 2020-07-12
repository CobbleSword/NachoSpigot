package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

public abstract class StructureGenerator extends WorldGenBase {

    private PersistentStructure d;
    protected Map<Long, StructureStart> e = Maps.newHashMap();

    public StructureGenerator() {}

    public abstract String a();

    protected final void a(World world, final int i, final int j, int k, int l, ChunkSnapshot chunksnapshot) {
        this.a(world);
        if (!this.e.containsKey(Long.valueOf(ChunkCoordIntPair.a(i, j)))) {
            this.b.nextInt();

            try {
                if (this.a(i, j)) {
                    StructureStart structurestart = this.b(i, j);

                    this.e.put(Long.valueOf(ChunkCoordIntPair.a(i, j)), structurestart);
                    this.a(i, j, structurestart);
                }

            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception preparing structure feature");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Feature being prepared");

                crashreportsystemdetails.a("Is feature chunk", new Callable() {
                    public String a() throws Exception {
                        return StructureGenerator.this.a(i, j) ? "True" : "False";
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                crashreportsystemdetails.a("Chunk location", (Object) String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j)}));
                crashreportsystemdetails.a("Chunk pos hash", new Callable() {
                    public String a() throws Exception {
                        return String.valueOf(ChunkCoordIntPair.a(i, j));
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                crashreportsystemdetails.a("Structure type", new Callable() {
                    public String a() throws Exception {
                        return StructureGenerator.this.getClass().getCanonicalName();
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public boolean a(World world, Random random, ChunkCoordIntPair chunkcoordintpair) {
        this.a(world);
        int i = (chunkcoordintpair.x << 4) + 8;
        int j = (chunkcoordintpair.z << 4) + 8;
        boolean flag = false;
        Iterator iterator = this.e.values().iterator();

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.d() && structurestart.a(chunkcoordintpair) && structurestart.a().a(i, j, i + 15, j + 15)) {
                structurestart.a(world, random, new StructureBoundingBox(i, j, i + 15, j + 15));
                structurestart.b(chunkcoordintpair);
                flag = true;
                this.a(structurestart.e(), structurestart.f(), structurestart);
            }
        }

        return flag;
    }

    public boolean b(BlockPosition blockposition) {
        if (this.c == null) return false; // PaperSpigot
        this.a(this.c);
        return this.c(blockposition) != null;
    }

    protected StructureStart c(BlockPosition blockposition) {
        Iterator iterator = this.e.values().iterator();

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.d() && structurestart.a().b((BaseBlockPosition) blockposition)) {
                Iterator iterator1 = structurestart.b().iterator();

                while (iterator1.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator1.next();

                    if (structurepiece.c().b((BaseBlockPosition) blockposition)) {
                        return structurestart;
                    }
                }
            }
        }

        return null;
    }

    public boolean a(World world, BlockPosition blockposition) {
        if (this.c == null) return false; // PaperSpigot
        this.a(world);
        Iterator iterator = this.e.values().iterator();

        StructureStart structurestart;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            structurestart = (StructureStart) iterator.next();
        } while (!structurestart.d() || !structurestart.a().b((BaseBlockPosition) blockposition));

        return true;
    }

    public BlockPosition getNearestGeneratedFeature(World world, BlockPosition blockposition) {
        this.c = world;
        this.a(world);
        this.b.setSeed(world.getSeed());
        long i = this.b.nextLong();
        long j = this.b.nextLong();
        long k = (long) (blockposition.getX() >> 4) * i;
        long l = (long) (blockposition.getZ() >> 4) * j;

        this.b.setSeed(k ^ l ^ world.getSeed());
        this.a(world, blockposition.getX() >> 4, blockposition.getZ() >> 4, 0, 0, (ChunkSnapshot) null);
        double d0 = Double.MAX_VALUE;
        BlockPosition blockposition1 = null;
        Iterator iterator = this.e.values().iterator();

        BlockPosition blockposition2;
        double d1;

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.d()) {
                StructurePiece structurepiece = (StructurePiece) structurestart.b().get(0);

                blockposition2 = structurepiece.a();
                d1 = blockposition2.i(blockposition);
                if (d1 < d0) {
                    d0 = d1;
                    blockposition1 = blockposition2;
                }
            }
        }

        if (blockposition1 != null) {
            return blockposition1;
        } else {
            List list = this.z_();

            if (list != null) {
                BlockPosition blockposition3 = null;
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    blockposition2 = (BlockPosition) iterator1.next();
                    d1 = blockposition2.i(blockposition);
                    if (d1 < d0) {
                        d0 = d1;
                        blockposition3 = blockposition2;
                    }
                }

                return blockposition3;
            } else {
                return null;
            }
        }
    }

    protected List<BlockPosition> z_() {
        return null;
    }

    private void a(World world) {
        if (this.d == null) {
            // Spigot Start
            if ( world.spigotConfig.saveStructureInfo && !this.a().equals( "Mineshaft" ) )
            {
            this.d = (PersistentStructure) world.a(PersistentStructure.class, this.a());
            } else
            {
                this.d = new PersistentStructure( this.a() );
            }
            // Spigot End
            if (this.d == null) {
                this.d = new PersistentStructure(this.a());
                world.a(this.a(), (PersistentBase) this.d);
            } else {
                NBTTagCompound nbttagcompound = this.d.a();
                Iterator iterator = nbttagcompound.c().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    NBTBase nbtbase = nbttagcompound.get(s);

                    if (nbtbase.getTypeId() == 10) {
                        NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;

                        if (nbttagcompound1.hasKey("ChunkX") && nbttagcompound1.hasKey("ChunkZ")) {
                            int i = nbttagcompound1.getInt("ChunkX");
                            int j = nbttagcompound1.getInt("ChunkZ");
                            StructureStart structurestart = WorldGenFactory.a(nbttagcompound1, world);

                            if (structurestart != null) {
                                this.e.put(Long.valueOf(ChunkCoordIntPair.a(i, j)), structurestart);
                            }
                        }
                    }
                }
            }
        }

    }

    private void a(int i, int j, StructureStart structurestart) {
        this.d.a(structurestart.a(i, j), i, j);
        this.d.c();
    }

    protected abstract boolean a(int i, int j);

    protected abstract StructureStart b(int i, int j);
}
