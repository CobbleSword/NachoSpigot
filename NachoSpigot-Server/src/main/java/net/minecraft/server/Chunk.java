package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger; // PaperSpigot

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists; // CraftBukkit
import org.bukkit.Bukkit; // CraftBukkit

public class Chunk {

    private static final Logger c = LogManager.getLogger();
    private final ChunkSection[] sections;
    private final byte[] e;
    private final int[] f;
    private final boolean[] g;
    private boolean h;
    public final World world;
    public final int[] heightMap;
    public final int locX;
    public final int locZ;
    private boolean k;
    public final Map<BlockPosition, TileEntity> tileEntities;
    public final List<Entity>[] entitySlices; // Spigot
    // PaperSpigot start - track the number of minecarts and items
    // Keep this synced with entitySlices.add() and entitySlices.remove()
    private final int[] itemCounts = new int[16];
    private final int[] inventoryEntityCounts = new int[16];
    // PaperSpigot end
    private boolean done;
    private boolean lit;
    private boolean p;
    private boolean q;
    private boolean r;
    private long lastSaved;
    private int t;
    private long u;
    private int v;
    private ConcurrentLinkedQueue<BlockPosition> w;
    protected gnu.trove.map.hash.TObjectIntHashMap<Class> entityCount = new gnu.trove.map.hash.TObjectIntHashMap<Class>(); // Spigot
    // PaperSpigot start - Asynchronous light updates
    public AtomicInteger pendingLightUpdates = new AtomicInteger();
    public long lightUpdateTime;
    // PaperSpigot end

    // PaperSpigot start - ChunkMap caching
    private PacketPlayOutMapChunk.ChunkMap chunkMap;
    private int emptySectionBits;

    public PacketPlayOutMapChunk.ChunkMap getChunkMap(boolean groundUpContinuous, int primaryBitMask) {
        if (!world.paperSpigotConfig.cacheChunkMaps || !groundUpContinuous || (primaryBitMask != 0 && primaryBitMask != '\uffff')) {
            return PacketPlayOutMapChunk.a(this, groundUpContinuous, !world.worldProvider.o(), primaryBitMask);
        }

        if (primaryBitMask == 0) {
            PacketPlayOutMapChunk.ChunkMap chunkMap = new PacketPlayOutMapChunk.ChunkMap();
            chunkMap.a = new byte[0];
            return chunkMap;
        }

        boolean isDirty = false;
        for (int i = 0; i < sections.length; ++i) {
            ChunkSection section = sections[i];
            if (section == null) {
                if ((emptySectionBits & (1 << i)) == 0) {
                    isDirty = true;
                    emptySectionBits |= (1 << i);
                }
            } else {
                if ((emptySectionBits & (1 << i)) == 1) {
                    isDirty = true;
                    emptySectionBits &= ~(1 << i);
                    section.isDirty = false;
                } else if (section.isDirty) {
                    isDirty = true;
                    section.isDirty = false;
                }
            }
        }

        if (isDirty || chunkMap == null) {
            chunkMap = PacketPlayOutMapChunk.a(this, true, !world.worldProvider.o(), '\uffff');
        }

        return chunkMap;
    }
    // PaperSpigot end

    // CraftBukkit start - Neighbor loaded cache for chunk lighting and entity ticking
    private int neighbors = 0x1 << 12;

    public boolean areNeighborsLoaded(final int radius) {
        switch (radius) {
            case 2:
                return this.neighbors == Integer.MAX_VALUE >> 6;
            case 1:
                final int mask =
                        //       x        z   offset          x        z   offset          x         z   offset
                        (0x1 << (1 * 5 +  1 + 12)) | (0x1 << (0 * 5 +  1 + 12)) | (0x1 << (-1 * 5 +  1 + 12)) |
                        (0x1 << (1 * 5 +  0 + 12)) | (0x1 << (0 * 5 +  0 + 12)) | (0x1 << (-1 * 5 +  0 + 12)) |
                        (0x1 << (1 * 5 + -1 + 12)) | (0x1 << (0 * 5 + -1 + 12)) | (0x1 << (-1 * 5 + -1 + 12));
                return (this.neighbors & mask) == mask;
            default:
                throw new UnsupportedOperationException(String.valueOf(radius));
        }
    }

    public void setNeighborLoaded(final int x, final int z) {
        this.neighbors |= 0x1 << (x * 5 + 12 + z);
    }

    public void setNeighborUnloaded(final int x, final int z) {
        this.neighbors &= ~(0x1 << (x * 5 + 12 + z));
    }
    // CraftBukkit end

    public Chunk(World world, int i, int j) {
        this.sections = new ChunkSection[16];
        this.e = new byte[256];
        this.f = new int[256];
        this.g = new boolean[256];
        this.tileEntities = Maps.newHashMap();
        this.v = 4096;
        this.w = Queues.newConcurrentLinkedQueue();
        this.entitySlices = (List[]) (new List[16]); // Spigot
        this.world = world;
        this.locX = i;
        this.locZ = j;
        this.heightMap = new int[256];

        for (int k = 0; k < this.entitySlices.length; ++k) {
            this.entitySlices[k] = new org.bukkit.craftbukkit.util.UnsafeList(); // Spigot
        }

        Arrays.fill(this.f, -999);
        Arrays.fill(this.e, (byte) -1);

        // CraftBukkit start
        if (!(this instanceof EmptyChunk)) {
            this.bukkitChunk = new org.bukkit.craftbukkit.CraftChunk(this);
        }
    }

    public org.bukkit.Chunk bukkitChunk;
    public boolean mustSave;
    // CraftBukkit end

    public Chunk(World world, ChunkSnapshot chunksnapshot, int i, int j) {
        this(world, i, j);
        short short0 = 256;
        boolean flag = !world.worldProvider.o();

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                for (int i1 = 0; i1 < short0; ++i1) {
                    int j1 = k * short0 * 16 | l * short0 | i1;
                    IBlockData iblockdata = chunksnapshot.a(j1);

                    if (iblockdata.getBlock().getMaterial() != Material.AIR) {
                        int k1 = i1 >> 4;

                        if (this.sections[k1] == null) {
                            this.sections[k1] = new ChunkSection(k1 << 4, flag);
                        }

                        this.sections[k1].setType(k, i1 & 15, l, iblockdata);
                    }
                }
            }
        }

    }

    public boolean a(int i, int j) {
        return i == this.locX && j == this.locZ;
    }

    public int f(BlockPosition blockposition) {
        return this.b(blockposition.getX() & 15, blockposition.getZ() & 15);
    }

    public int b(int i, int j) {
        return this.heightMap[j << 4 | i];
    }

    public int g() {
        for (int i = this.sections.length - 1; i >= 0; --i) {
            if (this.sections[i] != null) {
                return this.sections[i].getYPosition();
            }
        }

        return 0;
    }

    public ChunkSection[] getSections() {
        return this.sections;
    }

    public void initLighting() {
        int i = this.g();

        this.t = Integer.MAX_VALUE;

        for (int j = 0; j < 16; ++j) {
            int k = 0;

            while (k < 16) {
                this.f[j + (k << 4)] = -999;
                int l = i + 16;

                while (true) {
                    if (l > 0) {
                        if (this.e(j, l - 1, k) == 0) {
                            --l;
                            continue;
                        }

                        this.heightMap[k << 4 | j] = l;
                        if (l < this.t) {
                            this.t = l;
                        }
                    }

                    if (!this.world.worldProvider.o()) {
                        l = 15;
                        int i1 = i + 16 - 1;

                        do {
                            int j1 = this.e(j, i1, k);

                            if (j1 == 0 && l != 15) {
                                j1 = 1;
                            }

                            l -= j1;
                            if (l > 0) {
                                ChunkSection chunksection = this.sections[i1 >> 4];

                                if (chunksection != null) {
                                    chunksection.a(j, i1 & 15, k, l);
                                    this.world.n(new BlockPosition((this.locX << 4) + j, i1, (this.locZ << 4) + k));
                                }
                            }

                            --i1;
                        } while (i1 > 0 && l > 0);
                    }

                    ++k;
                    break;
                }
            }
        }

        this.q = true;
    }

    private void d(int i, int j) {
        this.g[i + j * 16] = true;
        this.k = true;
    }

    private void h(boolean flag) {
        this.world.methodProfiler.a("recheckGaps");
        if (this.world.areChunksLoaded(new BlockPosition(this.locX * 16 + 8, 0, this.locZ * 16 + 8), 16)) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (this.g[i + j * 16]) {
                        this.g[i + j * 16] = false;
                        int k = this.b(i, j);
                        int l = this.locX * 16 + i;
                        int i1 = this.locZ * 16 + j;
                        int j1 = Integer.MAX_VALUE;

                        Iterator iterator;
                        EnumDirection enumdirection;

                        for (iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator(); iterator.hasNext(); j1 = Math.min(j1, this.world.b(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ()))) {
                            enumdirection = (EnumDirection) iterator.next();
                        }

                        this.c(l, i1, j1);
                        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                        while (iterator.hasNext()) {
                            enumdirection = (EnumDirection) iterator.next();
                            this.c(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ(), k);
                        }

                        if (flag) {
                            this.world.methodProfiler.b();
                            return;
                        }
                    }
                }
            }

            this.k = false;
        }

        this.world.methodProfiler.b();
    }

    private void c(int i, int j, int k) {
        int l = this.world.getHighestBlockYAt(new BlockPosition(i, 0, j)).getY();

        if (l > k) {
            this.a(i, j, k, l + 1);
        } else if (l < k) {
            this.a(i, j, l, k + 1);
        }

    }

    private void a(int i, int j, int k, int l) {
        if (l > k && this.world.areChunksLoaded(new BlockPosition(i, 0, j), 16)) {
            for (int i1 = k; i1 < l; ++i1) {
                this.world.updateLight(EnumSkyBlock.SKY, new BlockPosition(i, i1, j)); // PaperSpigot - Asynchronous lighting updates
            }

            this.q = true;
        }

    }

    private void d(int i, int j, int k) {
        int l = this.heightMap[k << 4 | i] & 255;
        int i1 = l;

        if (j > l) {
            i1 = j;
        }

        while (i1 > 0 && this.e(i, i1 - 1, k) == 0) {
            --i1;
        }

        if (i1 != l) {
            this.world.a(i + this.locX * 16, k + this.locZ * 16, i1, l);
            this.heightMap[k << 4 | i] = i1;
            int j1 = this.locX * 16 + i;
            int k1 = this.locZ * 16 + k;
            int l1;
            int i2;

            if (!this.world.worldProvider.o()) {
                ChunkSection chunksection;

                if (i1 < l) {
                    for (l1 = i1; l1 < l; ++l1) {
                        chunksection = this.sections[l1 >> 4];
                        if (chunksection != null) {
                            chunksection.a(i, l1 & 15, k, 15);
                            this.world.n(new BlockPosition((this.locX << 4) + i, l1, (this.locZ << 4) + k));
                        }
                    }
                } else {
                    for (l1 = l; l1 < i1; ++l1) {
                        chunksection = this.sections[l1 >> 4];
                        if (chunksection != null) {
                            chunksection.a(i, l1 & 15, k, 0);
                            this.world.n(new BlockPosition((this.locX << 4) + i, l1, (this.locZ << 4) + k));
                        }
                    }
                }

                l1 = 15;

                while (i1 > 0 && l1 > 0) {
                    --i1;
                    i2 = this.e(i, i1, k);
                    if (i2 == 0) {
                        i2 = 1;
                    }

                    l1 -= i2;
                    if (l1 < 0) {
                        l1 = 0;
                    }

                    ChunkSection chunksection1 = this.sections[i1 >> 4];

                    if (chunksection1 != null) {
                        chunksection1.a(i, i1 & 15, k, l1);
                    }
                }
            }

            l1 = this.heightMap[k << 4 | i];
            i2 = l;
            int j2 = l1;

            if (l1 < l) {
                i2 = l1;
                j2 = l;
            }

            if (l1 < this.t) {
                this.t = l1;
            }

            if (!this.world.worldProvider.o()) {
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    this.a(j1 + enumdirection.getAdjacentX(), k1 + enumdirection.getAdjacentZ(), i2, j2);
                }

                this.a(j1, k1, i2, j2);
            }

            this.q = true;
        }
    }

    public int b(BlockPosition blockposition) {
        return this.getType(blockposition).p();
    }

    private int e(int i, int j, int k) {
        return this.getType(i, j, k).p();
    }

    private Block getType(int i, int j, int k) {
        Block block = Blocks.AIR;

        if (j >= 0 && j >> 4 < this.sections.length) {
            ChunkSection chunksection = this.sections[j >> 4];

            if (chunksection != null) {
                try {
                    block = chunksection.b(i, j & 15, k);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.a(throwable, "Getting block");

                    throw new ReportedException(crashreport);
                }
            }
        }

        return block;
    }

    public Block getTypeAbs(final int i, final int j, final int k) {
        try {
            return this.getType(i & 15, j, k & 15);
        } catch (ReportedException reportedexception) {
            CrashReportSystemDetails crashreportsystemdetails = reportedexception.a().a("Block being got");

            crashreportsystemdetails.a("Location", new Callable() {
                public String a() throws Exception {
                    return CrashReportSystemDetails.a(new BlockPosition(Chunk.this.locX * 16 + i, j, Chunk.this.locZ * 16 + k));
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
            throw reportedexception;
        }
    }

    public Block getType(final BlockPosition blockposition) {
        try {
            return this.getType(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
        } catch (ReportedException reportedexception) {
            CrashReportSystemDetails crashreportsystemdetails = reportedexception.a().a("Block being got");

            crashreportsystemdetails.a("Location", new Callable() {
                public String a() throws Exception {
                    return CrashReportSystemDetails.a(blockposition);
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
            throw reportedexception;
        }
    }

    // PaperSpigot start - Optimize getBlockData
    public IBlockData getBlockData(final BlockPosition blockposition) {
        if (blockposition.getY() >= 0 && blockposition.getY() >> 4 < this.sections.length) {
            ChunkSection chunksection = this.sections[blockposition.getY() >> 4];
            if (chunksection != null) {
                return chunksection.getType(blockposition.getX() & 15, blockposition.getY() & 15, blockposition.getZ() & 15);
            }
        }
        return Blocks.AIR.getBlockData();
    }
    public IBlockData getBlockDataSlow(final BlockPosition blockposition) {
        // PaperSpigot end
        if (this.world.G() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            IBlockData iblockdata = null;

            if (blockposition.getY() == 60) {
                iblockdata = Blocks.BARRIER.getBlockData();
            }

            if (blockposition.getY() == 70) {
                iblockdata = ChunkProviderDebug.b(blockposition.getX(), blockposition.getZ());
            }

            return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
        } else {
            try {
                if (blockposition.getY() >= 0 && blockposition.getY() >> 4 < this.sections.length) {
                    ChunkSection chunksection = this.sections[blockposition.getY() >> 4];

                    if (chunksection != null) {
                        int i = blockposition.getX() & 15;
                        int j = blockposition.getY() & 15;
                        int k = blockposition.getZ() & 15;

                        return chunksection.getType(i, j, k);
                    }
                }

                return Blocks.AIR.getBlockData();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Getting block state");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

                crashreportsystemdetails.a("Location", new Callable() {
                    public String a() throws Exception {
                        return CrashReportSystemDetails.a(blockposition);
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    private int g(int i, int j, int k) {
        if (j >> 4 >= this.sections.length) {
            return 0;
        } else {
            ChunkSection chunksection = this.sections[j >> 4];

            return chunksection != null ? chunksection.c(i, j & 15, k) : 0;
        }
    }

    public int c(BlockPosition blockposition) {
        return this.g(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
    }

    public IBlockData a(BlockPosition blockposition, IBlockData iblockdata) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        int l = k << 4 | i;

        if (j >= this.f[l] - 1) {
            this.f[l] = -999;
        }

        int i1 = this.heightMap[l];
        IBlockData iblockdata1 = this.getBlockData(blockposition);

        if (iblockdata1 == iblockdata) {
            return null;
        } else {
            Block block = iblockdata.getBlock();
            Block block1 = iblockdata1.getBlock();
            ChunkSection chunksection = this.sections[j >> 4];
            boolean flag = false;

            if (chunksection == null) {
                if (block == Blocks.AIR) {
                    return null;
                }

                chunksection = this.sections[j >> 4] = new ChunkSection(j >> 4 << 4, !this.world.worldProvider.o());
                flag = j >= i1;
            }

            chunksection.setType(i, j & 15, k, iblockdata);
            if (block1 != block) {
                if (!this.world.isClientSide) {
                    block1.remove(this.world, blockposition, iblockdata1);
                } else if (block1 instanceof IContainer) {
                    this.world.t(blockposition);
                }
            }

            if (chunksection.b(i, j & 15, k) != block) {
                return null;
            } else {
                if (flag) {
                    this.initLighting();
                } else {
                    int j1 = block.p();
                    int k1 = block1.p();

                    if (j1 > 0) {
                        if (j >= i1) {
                            this.d(i, j + 1, k);
                        }
                    } else if (j == i1 - 1) {
                        this.d(i, j, k);
                    }

                    if (j1 != k1 && (j1 < k1 || this.getBrightness(EnumSkyBlock.SKY, blockposition) > 0 || this.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 0)) {
                        this.d(i, k);
                    }
                }

                TileEntity tileentity;

                if (block1 instanceof IContainer) {
                    tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
                    if (tileentity != null) {
                        tileentity.E();
                    }
                }

                // CraftBukkit - Don't place while processing the BlockPlaceEvent, unless it's a BlockContainer. Prevents blocks such as TNT from activating when cancelled.
                if (!this.world.isClientSide && block1 != block  && (!this.world.captureBlockStates || block instanceof BlockContainer)) {
                    block.onPlace(this.world, blockposition, iblockdata);
                }

                if (block instanceof IContainer) {
                    tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
                    if (tileentity == null) {
                        tileentity = ((IContainer) block).a(this.world, block.toLegacyData(iblockdata));
                        this.world.setTileEntity(blockposition, tileentity);
                    }

                    if (tileentity != null) {
                        tileentity.E();
                    }
                }

                this.q = true;
                return iblockdata1;
            }
        }
    }

    public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        ChunkSection chunksection = this.sections[j >> 4];

        return chunksection == null ? (this.d(blockposition) ? enumskyblock.c : 0) : (enumskyblock == EnumSkyBlock.SKY ? (this.world.worldProvider.o() ? 0 : chunksection.d(i, j & 15, k)) : (enumskyblock == EnumSkyBlock.BLOCK ? chunksection.e(i, j & 15, k) : enumskyblock.c));
    }

    public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        ChunkSection chunksection = this.sections[k >> 4];

        if (chunksection == null) {
            chunksection = this.sections[k >> 4] = new ChunkSection(k >> 4 << 4, !this.world.worldProvider.o());
            this.initLighting();
        }

        this.q = true;
        if (enumskyblock == EnumSkyBlock.SKY) {
            if (!this.world.worldProvider.o()) {
                chunksection.a(j, k & 15, l, i);
            }
        } else if (enumskyblock == EnumSkyBlock.BLOCK) {
            chunksection.b(j, k & 15, l, i);
        }

    }

    public int a(BlockPosition blockposition, int i) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        ChunkSection chunksection = this.sections[k >> 4];

        if (chunksection == null) {
            return !this.world.worldProvider.o() && i < EnumSkyBlock.SKY.c ? EnumSkyBlock.SKY.c - i : 0;
        } else {
            int i1 = this.world.worldProvider.o() ? 0 : chunksection.d(j, k & 15, l);

            i1 -= i;
            int j1 = chunksection.e(j, k & 15, l);

            if (j1 > i1) {
                i1 = j1;
            }

            return i1;
        }
    }

    public void a(Entity entity) {
        this.r = true;
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);

        if (i != this.locX || j != this.locZ) {
            // CraftBukkit start
            Bukkit.getLogger().warning("Wrong location for " + entity + " in world '" + world.getWorld().getName() + "'!");
            // Chunk.c.warn("Wrong location! (" + i + ", " + j + ") should be (" + this.locX + ", " + this.locZ + "), " + entity, new Object[] { entity});
            Bukkit.getLogger().warning("Entity is at " + entity.locX + "," + entity.locZ + " (chunk " + i + "," + j + ") but was stored in chunk " + this.locX + "," + this.locZ);
            // CraftBukkit end
            entity.die();
        }

        int k = MathHelper.floor(entity.locY / 16.0D);

        if (k < 0) {
            k = 0;
        }

        if (k >= this.entitySlices.length) {
            k = this.entitySlices.length - 1;
        }

        entity.ad = true;
        entity.ae = this.locX;
        entity.af = k;
        entity.ag = this.locZ;
        this.entitySlices[k].add(entity);
        // PaperSpigot start - update counts
        if (entity instanceof EntityItem) {
            itemCounts[k]++;
        } else if (entity instanceof IInventory) {
            inventoryEntityCounts[k]++;
        }
        // PaperSpigot end
        // Spigot start - increment creature type count
        // Keep this synced up with World.a(Class)
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;
            if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
                return;
            }
        }
        for ( EnumCreatureType creatureType : EnumCreatureType.values() )
        {
            if ( creatureType.a().isAssignableFrom( entity.getClass() ) )
            {
                this.entityCount.adjustOrPutValue( creatureType.a(), 1, 1 );
            }
        }
        // Spigot end
    }

    public void b(Entity entity) {
        this.a(entity, entity.af);
    }

    public void a(Entity entity, int i) {
        if (i < 0) {
            i = 0;
        }

        if (i >= this.entitySlices.length) {
            i = this.entitySlices.length - 1;
        }

        if (!this.entitySlices[i].remove(entity)) return; // TacoSpigot
        // PaperSpigot start - update counts
        if (entity instanceof EntityItem) {
            itemCounts[i]--;
        } else if (entity instanceof IInventory) {
            inventoryEntityCounts[i]--;
        }
        // PaperSpigot end
        // Spigot start - decrement creature type count
        // Keep this synced up with World.a(Class)
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;
            if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
                return;
            }
        }
        for ( EnumCreatureType creatureType : EnumCreatureType.values() )
        {
            if ( creatureType.a().isAssignableFrom( entity.getClass() ) )
            {
                this.entityCount.adjustValue( creatureType.a(), -1 );
            }
        }
        // Spigot end
    }

    public boolean d(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;

        return j >= this.heightMap[k << 4 | i];
    }

    private TileEntity i(BlockPosition blockposition) {
        Block block = this.getType(blockposition);

        return !block.isTileEntity() ? null : ((IContainer) block).a(this.world, this.c(blockposition));
    }

    public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        // CraftBukkit start
        TileEntity tileentity = null;
        if (world.captureBlockStates) {
            tileentity = world.capturedTileEntities.get(blockposition);
        }
        if (tileentity == null) {
            tileentity = (TileEntity) this.tileEntities.get(blockposition);
        }
        // CraftBukkit end

        if (tileentity == null) {
            if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.IMMEDIATE) {
                tileentity = this.i(blockposition);
                this.world.setTileEntity(blockposition, tileentity);
            } else if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.QUEUED) {
                this.w.add(blockposition);
            }
        } else if (tileentity.x()) {
            this.tileEntities.remove(blockposition);
            return null;
        }

        return tileentity;
    }

    public void a(TileEntity tileentity) {
        this.a(tileentity.getPosition(), tileentity);
        if (this.h) {
            this.world.a(tileentity);
        }

    }

    public void a(BlockPosition blockposition, TileEntity tileentity) {
        tileentity.a(this.world);
        tileentity.a(blockposition);
        if (this.getType(blockposition) instanceof IContainer) {
            if (this.tileEntities.containsKey(blockposition)) {
                ((TileEntity) this.tileEntities.get(blockposition)).y();
            }

            tileentity.D();
            this.tileEntities.put(blockposition, tileentity);
            // CraftBukkit start
            // PaperSpigot start - Remove invalid mob spawner tile entities
        } else if (this.world.paperSpigotConfig.removeInvalidMobSpawnerTEs && tileentity instanceof TileEntityMobSpawner &&
                org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(getType(blockposition)) != org.bukkit.Material.MOB_SPAWNER) {
            this.tileEntities.remove(blockposition);
            // PaperSpigot end
        } else {
            System.out.println("Attempted to place a tile entity (" + tileentity + ") at " + tileentity.position.getX() + "," + tileentity.position.getY() + "," + tileentity.position.getZ()
                + " (" + org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(getType(blockposition)) + ") where there was no entity tile!");
            System.out.println("Chunk coordinates: " + (this.locX * 16) + "," + (this.locZ * 16));
            new Exception().printStackTrace();
            // CraftBukkit end
        }
    }

    public void e(BlockPosition blockposition) {
        if (this.h) {
            TileEntity tileentity = (TileEntity) this.tileEntities.remove(blockposition);

            if (tileentity != null) {
                tileentity.y();
            }
        }

    }

    public void addEntities() {
        this.h = true;
        this.world.a(this.tileEntities.values());

        for (int i = 0; i < this.entitySlices.length; ++i) {
            Iterator iterator = this.entitySlices[i].iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                entity.ah();
            }

            this.world.b((Collection) this.entitySlices[i]);
        }

    }

    public void removeEntities() {
        this.h = false;
        Iterator iterator = this.tileEntities.values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();
            // Spigot Start
            if ( tileentity instanceof IInventory )
            {
                for ( org.bukkit.entity.HumanEntity h : Lists.<org.bukkit.entity.HumanEntity>newArrayList((List<org.bukkit.entity.HumanEntity>) ( (IInventory) tileentity ).getViewers() ) )
                {
                    if ( h instanceof org.bukkit.craftbukkit.entity.CraftHumanEntity )
                    {
                       ( (org.bukkit.craftbukkit.entity.CraftHumanEntity) h).getHandle().closeInventory();
                    }
                }
            }
            // Spigot End

            this.world.b(tileentity);
        }

        for (int i = 0; i < this.entitySlices.length; ++i) {
            // CraftBukkit start
            List<Entity> newList = Lists.newArrayList(this.entitySlices[i]);
            java.util.Iterator<Entity> iter = newList.iterator();
            while (iter.hasNext()) {
                Entity entity = iter.next();
                // Spigot Start
                if ( entity instanceof IInventory )
                {
                    for ( org.bukkit.entity.HumanEntity h : Lists.<org.bukkit.entity.HumanEntity>newArrayList( (List<org.bukkit.entity.HumanEntity>) ( (IInventory) entity ).getViewers() ) )
                    {
                        if ( h instanceof org.bukkit.craftbukkit.entity.CraftHumanEntity )
                        {
                           ( (org.bukkit.craftbukkit.entity.CraftHumanEntity) h).getHandle().closeInventory();
                        }
                    }
                }
                // Spigot End

                // Do not pass along players, as doing so can get them stuck outside of time.
                // (which for example disables inventory icon updates and prevents block breaking)
                if (entity instanceof EntityPlayer) {
                    iter.remove();
                }
            }

            this.world.c((Collection) newList);
            // CraftBukkit end
        }

    }

    public void e() {
        this.q = true;
    }

    public void a(Entity entity, AxisAlignedBB axisalignedbb, List<Entity> list, Predicate<? super Entity> predicate) {
        int i = MathHelper.floor((axisalignedbb.b - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.e + 2.0D) / 16.0D);

        i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

        for (int k = i; k <= j; ++k) {
            if (!this.entitySlices[k].isEmpty()) {
                Iterator iterator = this.entitySlices[k].iterator();
                // PaperSpigot start - Don't search for inventories if we have none, and that is all we want
                /*
                 * We check if they want inventories by seeing if it is the static `IEntitySelector.c`
                 *
                 * Make sure the inventory selector stays in sync.
                 * It should be the one that checks `var1 instanceof IInventory && var1.isAlive()`
                 */
                if (predicate == IEntitySelector.c && inventoryEntityCounts[k] <= 0) continue;
                // PaperSpigot end
                while (iterator.hasNext()) {
                    Entity entity1 = (Entity) iterator.next();

                    if (entity1.getBoundingBox().b(axisalignedbb) && entity1 != entity) {
                        if (predicate == null || predicate.apply(entity1)) {
                            list.add(entity1);
                        }

                        Entity[] aentity = entity1.aB();

                        if (aentity != null) {
                            for (int l = 0; l < aentity.length; ++l) {
                                entity1 = aentity[l];
                                if (entity1 != entity && entity1.getBoundingBox().b(axisalignedbb) && (predicate == null || predicate.apply(entity1))) {
                                    list.add(entity1);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public <T extends Entity> void a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, List<T> list, Predicate<? super T> predicate) {
        int i = MathHelper.floor((axisalignedbb.b - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.e + 2.0D) / 16.0D);

        i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

        // PaperSpigot start
        int[] counts;
        if (ItemStack.class.isAssignableFrom(oclass)) {
            counts = itemCounts;
        } else if (IInventory.class.isAssignableFrom(oclass)) {
            counts = inventoryEntityCounts;
        } else {
            counts = null;
        }
        // PaperSpigot end
        for (int k = i; k <= j; ++k) {
            if (counts != null && counts[k] <= 0) continue; // PaperSpigot - Don't check a chunk if it doesn't have the type we are looking for
            Iterator iterator = this.entitySlices[k].iterator(); // Spigot

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (oclass.isInstance(entity) && entity.getBoundingBox().b(axisalignedbb) && (predicate == null || predicate.apply((T) entity))) { // CraftBukkit - fix decompile error // Spigot
                    list.add((T) entity); // Fix decompile error
                }
            }
        }

    }

    public boolean a(boolean flag) {
        if (flag) {
            if (this.r && this.world.getTime() != this.lastSaved || this.q) {
                return true;
            }
        } else if (this.r && this.world.getTime() >= this.lastSaved + MinecraftServer.getServer().autosavePeriod * 4) { // Spigot - Only save if we've passed 2 auto save intervals without modification
            return true;
        }

        return this.q;
    }

    public Random a(long i) {
        return new Random(this.world.getSeed() + (long) (this.locX * this.locX * 4987142) + (long) (this.locX * 5947611) + (long) (this.locZ * this.locZ) * 4392871L + (long) (this.locZ * 389711) ^ i);
    }

    public boolean isEmpty() {
        return false;
    }

    public void loadNearby(IChunkProvider ichunkprovider, IChunkProvider ichunkprovider1, int i, int j) {
        world.timings.syncChunkLoadPostTimer.startTiming(); // Spigot
        boolean flag = ichunkprovider.isChunkLoaded(i, j - 1);
        boolean flag1 = ichunkprovider.isChunkLoaded(i + 1, j);
        boolean flag2 = ichunkprovider.isChunkLoaded(i, j + 1);
        boolean flag3 = ichunkprovider.isChunkLoaded(i - 1, j);
        boolean flag4 = ichunkprovider.isChunkLoaded(i - 1, j - 1);
        boolean flag5 = ichunkprovider.isChunkLoaded(i + 1, j + 1);
        boolean flag6 = ichunkprovider.isChunkLoaded(i - 1, j + 1);
        boolean flag7 = ichunkprovider.isChunkLoaded(i + 1, j - 1);

        if (flag1 && flag2 && flag5) {
            if (!this.done) {
                ichunkprovider.getChunkAt(ichunkprovider1, i, j);
            } else {
                ichunkprovider.a(ichunkprovider1, this, i, j);
            }
        }

        Chunk chunk;

        if (flag3 && flag2 && flag6) {
            chunk = ichunkprovider.getOrCreateChunk(i - 1, j);
            if (!chunk.done) {
                ichunkprovider.getChunkAt(ichunkprovider1, i - 1, j);
            } else {
                ichunkprovider.a(ichunkprovider1, chunk, i - 1, j);
            }
        }

        if (flag && flag1 && flag7) {
            chunk = ichunkprovider.getOrCreateChunk(i, j - 1);
            if (!chunk.done) {
                ichunkprovider.getChunkAt(ichunkprovider1, i, j - 1);
            } else {
                ichunkprovider.a(ichunkprovider1, chunk, i, j - 1);
            }
        }

        if (flag4 && flag && flag3) {
            chunk = ichunkprovider.getOrCreateChunk(i - 1, j - 1);
            if (!chunk.done) {
                ichunkprovider.getChunkAt(ichunkprovider1, i - 1, j - 1);
            } else {
                ichunkprovider.a(ichunkprovider1, chunk, i - 1, j - 1);
            }
        }

        world.timings.syncChunkLoadPostTimer.stopTiming(); // Spigot
    }

    public BlockPosition h(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getZ() & 15;
        int k = i | j << 4;
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), this.f[k], blockposition.getZ());

        if (blockposition1.getY() == -999) {
            int l = this.g() + 15;

            blockposition1 = new BlockPosition(blockposition.getX(), l, blockposition.getZ());
            int i1 = -1;

            while (blockposition1.getY() > 0 && i1 == -1) {
                Block block = this.getType(blockposition1);
                Material material = block.getMaterial();

                if (!material.isSolid() && !material.isLiquid()) {
                    blockposition1 = blockposition1.down();
                } else {
                    i1 = blockposition1.getY() + 1;
                }
            }

            this.f[k] = i1;
        }

        return new BlockPosition(blockposition.getX(), this.f[k], blockposition.getZ());
    }

    public void b(boolean flag) {
        if (this.k && !this.world.worldProvider.o() && !flag) {
            this.recheckGaps(this.world.isClientSide); // PaperSpigot - Asynchronous lighting updates
        }

        this.p = true;
        if (!this.lit && this.done && this.world.spigotConfig.randomLightUpdates) { // Spigot - also use random light updates setting to determine if we should relight
            this.n();
        }

        while (!this.w.isEmpty()) {
            BlockPosition blockposition = (BlockPosition) this.w.poll();

            if (this.a(blockposition, Chunk.EnumTileEntityState.CHECK) == null && this.getType(blockposition).isTileEntity()) {
                TileEntity tileentity = this.i(blockposition);

                this.world.setTileEntity(blockposition, tileentity);
                this.world.b(blockposition, blockposition);
            }
        }

    }

    /**
     * PaperSpigot - Recheck gaps asynchronously.
     */
    public void recheckGaps(final boolean isClientSide) {
        if (!world.paperSpigotConfig.useAsyncLighting) {
            this.h(isClientSide);
            return;
        }

        world.lightingExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Chunk.this.h(isClientSide);
            }
        });
    }

    public boolean isReady() {
        // Spigot Start
        /*
         * As of 1.7, Mojang added a check to make sure that only chunks which have been lit are sent to the client.
         * Unfortunately this interferes with our modified chunk ticking algorithm, which will only tick chunks distant from the player on a very infrequent basis.
         * We cannot unfortunately do this lighting stage during chunk gen as it appears to put a lot more noticeable load on the server, than when it is done at play time.
         * For now at least we will simply send all chunks, in accordance with pre 1.7 behaviour.
         */
        return true;
        // Spigot End
    }

    public ChunkCoordIntPair j() {
        return new ChunkCoordIntPair(this.locX, this.locZ);
    }

    public boolean c(int i, int j) {
        if (i < 0) {
            i = 0;
        }

        if (j >= 256) {
            j = 255;
        }

        for (int k = i; k <= j; k += 16) {
            ChunkSection chunksection = this.sections[k >> 4];

            if (chunksection != null && !chunksection.a()) {
                return false;
            }
        }

        return true;
    }

    public void a(ChunkSection[] achunksection) {
        if (this.sections.length != achunksection.length) {
            Chunk.c.warn("Could not set level chunk sections, array length is " + achunksection.length + " instead of " + this.sections.length);
        } else {
            for (int i = 0; i < this.sections.length; ++i) {
                this.sections[i] = achunksection[i];
            }

        }
    }

    public BiomeBase getBiome(BlockPosition blockposition, WorldChunkManager worldchunkmanager) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getZ() & 15;
        int k = this.e[j << 4 | i] & 255;
        BiomeBase biomebase;

        if (k == 255) {
            biomebase = worldchunkmanager.getBiome(blockposition, BiomeBase.PLAINS);
            k = biomebase.id;
            this.e[j << 4 | i] = (byte) (k & 255);
        }

        biomebase = BiomeBase.getBiome(k);
        return biomebase == null ? BiomeBase.PLAINS : biomebase;
    }

    public byte[] getBiomeIndex() {
        return this.e;
    }

    public void a(byte[] abyte) {
        if (this.e.length != abyte.length) {
            Chunk.c.warn("Could not set level chunk biomes, array length is " + abyte.length + " instead of " + this.e.length);
        } else {
            for (int i = 0; i < this.e.length; ++i) {
                this.e[i] = abyte[i];
            }

        }
    }

    public void l() {
        this.v = 0;
    }

    public void m() {
        BlockPosition blockposition = new BlockPosition(this.locX << 4, 0, this.locZ << 4);

        for (int i = 0; i < 8; ++i) {
            if (this.v >= 4096) {
                return;
            }

            int j = this.v % 16;
            int k = this.v / 16 % 16;
            int l = this.v / 256;

            ++this.v;

            for (int i1 = 0; i1 < 16; ++i1) {
                BlockPosition blockposition1 = blockposition.a(k, (j << 4) + i1, l);
                boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;

                if (this.sections[j] == null && flag || this.sections[j] != null && this.sections[j].b(k, i1, l).getMaterial() == Material.AIR) {
                    EnumDirection[] aenumdirection = EnumDirection.values();
                    int j1 = aenumdirection.length;

                    for (int k1 = 0; k1 < j1; ++k1) {
                        EnumDirection enumdirection = aenumdirection[k1];
                        BlockPosition blockposition2 = blockposition1.shift(enumdirection);

                        if (this.world.getType(blockposition2).getBlock().r() > 0) {
                            this.world.x(blockposition2);
                        }
                    }

                    this.world.x(blockposition1);
                }
            }
        }

    }

    public void n() {
        this.done = true;
        this.lit = true;
        BlockPosition blockposition = new BlockPosition(this.locX << 4, 0, this.locZ << 4);

        if (!this.world.worldProvider.o()) {
            if (this.world.areChunksLoadedBetween(blockposition.a(-1, 0, -1), blockposition.a(16, this.world.F(), 16))) {
                label42:
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (!this.e(i, j)) {
                            this.lit = false;
                            break label42;
                        }
                    }
                }

                if (this.lit) {
                    Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator.hasNext()) {
                        EnumDirection enumdirection = (EnumDirection) iterator.next();
                        int k = enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE ? 16 : 1;

                        this.world.getChunkAtWorldCoords(blockposition.shift(enumdirection, k)).a(enumdirection.opposite());
                    }

                    this.y();
                }
            } else {
                this.lit = false;
            }
        }

    }

    private void y() {
        for (int i = 0; i < this.g.length; ++i) {
            this.g[i] = true;
        }

        this.h(false);
    }

    private void a(EnumDirection enumdirection) {
        if (this.done) {
            int i;

            if (enumdirection == EnumDirection.EAST) {
                for (i = 0; i < 16; ++i) {
                    this.e(15, i);
                }
            } else if (enumdirection == EnumDirection.WEST) {
                for (i = 0; i < 16; ++i) {
                    this.e(0, i);
                }
            } else if (enumdirection == EnumDirection.SOUTH) {
                for (i = 0; i < 16; ++i) {
                    this.e(i, 15);
                }
            } else if (enumdirection == EnumDirection.NORTH) {
                for (i = 0; i < 16; ++i) {
                    this.e(i, 0);
                }
            }

        }
    }

    private boolean e(int i, int j) {
        int k = this.g();
        boolean flag = false;
        boolean flag1 = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition((this.locX << 4) + i, 0, (this.locZ << 4) + j);

        int l;

        for (l = k + 16 - 1; l > this.world.F() || l > 0 && !flag1; --l) {
            blockposition_mutableblockposition.c(blockposition_mutableblockposition.getX(), l, blockposition_mutableblockposition.getZ());
            int i1 = this.b((BlockPosition) blockposition_mutableblockposition);

            if (i1 == 255 && blockposition_mutableblockposition.getY() < this.world.F()) {
                flag1 = true;
            }

            if (!flag && i1 > 0) {
                flag = true;
            } else if (flag && i1 == 0 && !this.world.x(blockposition_mutableblockposition)) {
                return false;
            }
        }

        for (l = blockposition_mutableblockposition.getY(); l > 0; --l) {
            blockposition_mutableblockposition.c(blockposition_mutableblockposition.getX(), l, blockposition_mutableblockposition.getZ());
            if (this.getType(blockposition_mutableblockposition).r() > 0) {
                this.world.x(blockposition_mutableblockposition);
            }
        }

        return true;
    }

    public boolean o() {
        return this.h;
    }

    public World getWorld() {
        return this.world;
    }

    public int[] q() {
        return this.heightMap;
    }

    public void a(int[] aint) {
        if (this.heightMap.length != aint.length) {
            Chunk.c.warn("Could not set level chunk heightmap, array length is " + aint.length + " instead of " + this.heightMap.length);
        } else {
            for (int i = 0; i < this.heightMap.length; ++i) {
                this.heightMap[i] = aint[i];
            }

        }
    }

    public Map<BlockPosition, TileEntity> getTileEntities() {
        return this.tileEntities;
    }

    public List<Entity>[] getEntitySlices() {
        return this.entitySlices;
    }

    public boolean isDone() {
        return this.done;
    }

    public void d(boolean flag) {
        this.done = flag;
    }

    public boolean u() {
        return this.lit;
    }

    public void e(boolean flag) {
        this.lit = flag;
    }

    public void f(boolean flag) {
        this.q = flag;
    }

    public void g(boolean flag) {
        this.r = flag;
    }

    public void setLastSaved(long i) {
        this.lastSaved = i;
    }

    public int v() {
        return this.t;
    }

    public long w() {
        return this.u;
    }

    public void c(long i) {
        this.u = i;
    }

    public static enum EnumTileEntityState {

        IMMEDIATE, QUEUED, CHECK;

        private EnumTileEntityState() {}
    }
}
