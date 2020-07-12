package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import java.util.HashMap;
// CraftBukkit end

public class PlayerChunkMap {

    private static final Logger a = LogManager.getLogger();
    private final WorldServer world;
    private final List<EntityPlayer> managedPlayers = Lists.newArrayList();
    private final LongHashMap<PlayerChunkMap.PlayerChunk> d = new LongHashMap();
    private final Queue<PlayerChunkMap.PlayerChunk> e = new java.util.concurrent.ConcurrentLinkedQueue<PlayerChunkMap.PlayerChunk>(); // CraftBukkit ArrayList -> ConcurrentLinkedQueue
    private final Queue<PlayerChunkMap.PlayerChunk> f = new java.util.concurrent.ConcurrentLinkedQueue<PlayerChunkMap.PlayerChunk>(); // CraftBukkit ArrayList -> ConcurrentLinkedQueue
    private int g;
    private long h;
    private final int[][] i = new int[][] { { 1, 0}, { 0, 1}, { -1, 0}, { 0, -1}};
    private boolean wasNotEmpty; // CraftBukkit - add field

    public PlayerChunkMap(WorldServer worldserver, int viewDistance /* Spigot */) {
        this.world = worldserver;
        this.a(viewDistance); // Spigot
    }

    public WorldServer a() {
        return this.world;
    }

    public void flush() {
        long i = this.world.getTime();
        int j;
        PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk;

        if (i - this.h > 8000L) {
            this.h = i;

            // CraftBukkit start - Use iterator
            java.util.Iterator iterator = this.f.iterator();
            while (iterator.hasNext()) {
                playerchunkmap_playerchunk = (PlayerChunk) iterator.next();
                playerchunkmap_playerchunk.b();
                playerchunkmap_playerchunk.a();
            }
        } else {
            java.util.Iterator iterator = this.e.iterator();
            while (iterator.hasNext()) {
                playerchunkmap_playerchunk = (PlayerChunk) iterator.next();
                playerchunkmap_playerchunk.b();
                iterator.remove();
                // CraftBukkit end
            }
        }

        // this.e.clear(); //CraftBukkit - Removals are already covered
        if (this.managedPlayers.isEmpty()) {
            if (!wasNotEmpty) return; // CraftBukkit - Only do unload when we go from non-empty to empty
            WorldProvider worldprovider = this.world.worldProvider;

            if (!worldprovider.e()) {
                this.world.chunkProviderServer.b();
            }
            // CraftBukkit start
            wasNotEmpty = false;
        } else {
            wasNotEmpty = true;
        }
        // CraftBukkit end

    }

    public boolean a(int i, int j) {
        long k = (long) i + 2147483647L | (long) j + 2147483647L << 32;

        return this.d.getEntry(k) != null;
    }

    private PlayerChunkMap.PlayerChunk a(int i, int j, boolean flag) {
        long k = (long) i + 2147483647L | (long) j + 2147483647L << 32;
        PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = (PlayerChunkMap.PlayerChunk) this.d.getEntry(k);

        if (playerchunkmap_playerchunk == null && flag) {
            playerchunkmap_playerchunk = new PlayerChunkMap.PlayerChunk(i, j);
            this.d.put(k, playerchunkmap_playerchunk);
            this.f.add(playerchunkmap_playerchunk);
        }

        return playerchunkmap_playerchunk;
    }

    // CraftBukkit start - add method
    public final boolean isChunkInUse(int x, int z) {
        PlayerChunk pi = a(x, z, false);
        if (pi != null) {
            return (pi.b.size() > 0);
        }
        return false;
    }
    // CraftBukkit end

    public void flagDirty(BlockPosition blockposition) {
        int i = blockposition.getX() >> 4;
        int j = blockposition.getZ() >> 4;
        PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(i, j, false);

        if (playerchunkmap_playerchunk != null) {
            playerchunkmap_playerchunk.a(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
        }

    }

    public void addPlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;

        entityplayer.d = entityplayer.locX;
        entityplayer.e = entityplayer.locZ;

        // CraftBukkit start - Load nearby chunks first
        List<ChunkCoordIntPair> chunkList = new LinkedList<ChunkCoordIntPair>();

        // PaperSpigot start - Player view distance API
        for (int k = i - entityplayer.viewDistance; k <= i + entityplayer.viewDistance; ++k) {
            for (int l = j - entityplayer.viewDistance; l <= j + entityplayer.viewDistance; ++l) {
        // PaperSpigot end
                chunkList.add(new ChunkCoordIntPair(k, l));
            }
        }
        
        Collections.sort(chunkList, new ChunkCoordComparator(entityplayer));
        for (ChunkCoordIntPair pair : chunkList) {
            this.a(pair.x, pair.z, true).a(entityplayer);
        }
        // CraftBukkit end

        this.managedPlayers.add(entityplayer);
        this.b(entityplayer);
    }

    public void b(EntityPlayer entityplayer) {
        ArrayList arraylist = Lists.newArrayList(entityplayer.chunkCoordIntPairQueue);
        int i = 0;
        int j = entityplayer.viewDistance; // PaperSpigot - Player view distance API
        int k = (int) entityplayer.locX >> 4;
        int l = (int) entityplayer.locZ >> 4;
        int i1 = 0;
        int j1 = 0;
        ChunkCoordIntPair chunkcoordintpair = this.a(k, l, true).location;

        entityplayer.chunkCoordIntPairQueue.clear();
        if (arraylist.contains(chunkcoordintpair)) {
            entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
        }

        int k1;

        for (k1 = 1; k1 <= j * 2; ++k1) {
            for (int l1 = 0; l1 < 2; ++l1) {
                int[] aint = this.i[i++ % 4];

                for (int i2 = 0; i2 < k1; ++i2) {
                    i1 += aint[0];
                    j1 += aint[1];
                    chunkcoordintpair = this.a(k + i1, l + j1, true).location;
                    if (arraylist.contains(chunkcoordintpair)) {
                        entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
                    }
                }
            }
        }

        i %= 4;

        for (k1 = 0; k1 < j * 2; ++k1) {
            i1 += this.i[i][0];
            j1 += this.i[i][1];
            chunkcoordintpair = this.a(k + i1, l + j1, true).location;
            if (arraylist.contains(chunkcoordintpair)) {
                entityplayer.chunkCoordIntPairQueue.add(chunkcoordintpair);
            }
        }

    }

    public void removePlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.d >> 4;
        int j = (int) entityplayer.e >> 4;

        // PaperSpigot start - Player view distance API
        for (int k = i - entityplayer.viewDistance; k <= i + entityplayer.viewDistance; ++k) {
            for (int l = j - entityplayer.viewDistance; l <= j + entityplayer.viewDistance; ++l) {
        // PaperSpigot end
                PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(k, l, false);

                if (playerchunkmap_playerchunk != null) {
                    playerchunkmap_playerchunk.b(entityplayer);
                }
            }
        }

        this.managedPlayers.remove(entityplayer);
    }

    private boolean a(int i, int j, int k, int l, int i1) {
        int j1 = i - k;
        int k1 = j - l;

        return j1 >= -i1 && j1 <= i1 ? k1 >= -i1 && k1 <= i1 : false;
    }

    public void movePlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;
        double d0 = entityplayer.d - entityplayer.locX;
        double d1 = entityplayer.e - entityplayer.locZ;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D) {
            int k = (int) entityplayer.d >> 4;
            int l = (int) entityplayer.e >> 4;
            int i1 = entityplayer.viewDistance; // PaperSpigot - Player view distance API
            int j1 = i - k;
            int k1 = j - l;
            List<ChunkCoordIntPair> chunksToLoad = new LinkedList<ChunkCoordIntPair>(); // CraftBukkit

            if (j1 != 0 || k1 != 0) {
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2) {
                        if (!this.a(l1, i2, k, l, i1)) {
                            chunksToLoad.add(new ChunkCoordIntPair(l1, i2)); // CraftBukkit
                        }

                        if (!this.a(l1 - j1, i2 - k1, i, j, i1)) {
                            PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(l1 - j1, i2 - k1, false);

                            if (playerchunkmap_playerchunk != null) {
                                playerchunkmap_playerchunk.b(entityplayer);
                            }
                        }
                    }
                }

                this.b(entityplayer);
                entityplayer.d = entityplayer.locX;
                entityplayer.e = entityplayer.locZ;

                // CraftBukkit start - send nearest chunks first
                Collections.sort(chunksToLoad, new ChunkCoordComparator(entityplayer));
                for (ChunkCoordIntPair pair : chunksToLoad) {
                    this.a(pair.x, pair.z, true).a(entityplayer);
                }

                if (j1 > 1 || j1 < -1 || k1 > 1 || k1 < -1) {
                    Collections.sort(entityplayer.chunkCoordIntPairQueue, new ChunkCoordComparator(entityplayer));
                }
                // CraftBukkit end
            }
        }
    }

    public boolean a(EntityPlayer entityplayer, int i, int j) {
        PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(i, j, false);

        return playerchunkmap_playerchunk != null && playerchunkmap_playerchunk.b.contains(entityplayer) && !entityplayer.chunkCoordIntPairQueue.contains(playerchunkmap_playerchunk.location);
    }

    public void a(int i) {
        i = MathHelper.clamp(i, 3, 32);
        if (i != this.g) {
            int j = i - this.g;
            ArrayList arraylist = Lists.newArrayList(this.managedPlayers);
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();
                int k = (int) entityplayer.locX >> 4;
                int l = (int) entityplayer.locZ >> 4;
                int i1;
                int j1;

                if (j > 0) {
                    for (i1 = k - i; i1 <= k + i; ++i1) {
                        for (j1 = l - i; j1 <= l + i; ++j1) {
                            PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(i1, j1, true);

                            if (!playerchunkmap_playerchunk.b.contains(entityplayer)) {
                                playerchunkmap_playerchunk.a(entityplayer);
                            }
                        }
                    }
                } else {
                    for (i1 = k - this.g; i1 <= k + this.g; ++i1) {
                        for (j1 = l - this.g; j1 <= l + this.g; ++j1) {
                            if (!this.a(i1, j1, k, l, i)) {
                                this.a(i1, j1, true).b(entityplayer);
                            }
                        }
                    }
                }
            }

            this.g = i;
        }
    }

    // PaperSpigot start - Player view distance API
    public void updateViewDistance(EntityPlayer player, int viewDistance) {
        viewDistance = MathHelper.clamp(viewDistance, 3, 32);
        if (viewDistance != player.viewDistance) {
            int cx = (int) player.locX >> 4;
            int cz = (int) player.locZ >> 4;

            if (viewDistance - player.viewDistance > 0) {
                for (int x = cx - viewDistance; x <= cx + viewDistance; ++x) {
                    for (int z = cz - viewDistance; z <= cz + viewDistance; ++z) {
                        PlayerChunkMap.PlayerChunk playerchunkmap_playerchunk = this.a(x, z, true);

                        if (!playerchunkmap_playerchunk.b.contains(player)) {
                            playerchunkmap_playerchunk.a(player);
                        }
                    }
                }
            } else {
                for (int x = cx - player.viewDistance; x <= cx + player.viewDistance; ++x) {
                    for (int z = cz - player.viewDistance; z <= cz + player.viewDistance; ++z) {
                        if (!this.a(x, z, cx, cz, viewDistance)) {
                            this.a(x, z, true).b(player);
                        }
                    }
                }
            }

            player.viewDistance = viewDistance;
        }
    }
    // PaperSpigot end

    public static int getFurthestViewableBlock(int i) {
        return i * 16 - 16;
    }

    class PlayerChunk {

        private final List<EntityPlayer> b = Lists.newArrayList();
        private final ChunkCoordIntPair location;
        private short[] dirtyBlocks = new short[64];
        private int dirtyCount;
        private int f;
        private long g;

        // CraftBukkit start - add fields
        private final HashMap<EntityPlayer, Runnable> players = new HashMap<EntityPlayer, Runnable>();
        private boolean loaded = false;
        private Runnable loadedRunnable = new Runnable() {
            public void run() {
                PlayerChunk.this.loaded = true;
            }
        };
        // CraftBukkit end

        public PlayerChunk(int i, int j) {
            this.location = new ChunkCoordIntPair(i, j);
            PlayerChunkMap.this.a().chunkProviderServer.getChunkAt(i, j, loadedRunnable); // CraftBukkit
        }

        public void a(final EntityPlayer entityplayer) {  // CraftBukkit - added final to argument
            if (this.b.contains(entityplayer)) {
                PlayerChunkMap.a.debug("Failed to add player. {} already is in chunk {}, {}", new Object[] { entityplayer, Integer.valueOf(this.location.x), Integer.valueOf(this.location.z)});
            } else {
                if (this.b.isEmpty()) {
                    this.g = PlayerChunkMap.this.world.getTime();
                }

                this.b.add(entityplayer);
                // CraftBukkit start - use async chunk io
                Runnable playerRunnable;
                if (this.loaded) {
                    playerRunnable = null;
                    entityplayer.chunkCoordIntPairQueue.add(this.location);
                } else {
                    playerRunnable = new Runnable() {
                        public void run() {
                            entityplayer.chunkCoordIntPairQueue.add(PlayerChunk.this.location);
                        }
                    };
                    PlayerChunkMap.this.a().chunkProviderServer.getChunkAt(this.location.x, this.location.z, playerRunnable);
                }

                this.players.put(entityplayer, playerRunnable);
                // CraftBukkit end
            }
        }

        public void b(EntityPlayer entityplayer) {
            if (this.b.contains(entityplayer)) {
                // CraftBukkit start - If we haven't loaded yet don't load the chunk just so we can clean it up
                if (!this.loaded) {
                    ChunkIOExecutor.dropQueuedChunkLoad(PlayerChunkMap.this.a(), this.location.x, this.location.z, this.players.get(entityplayer));
                    this.b.remove(entityplayer);
                    this.players.remove(entityplayer);

                    if (this.b.isEmpty()) {
                        ChunkIOExecutor.dropQueuedChunkLoad(PlayerChunkMap.this.a(), this.location.x, this.location.z, this.loadedRunnable);
                        long i = (long) this.location.x + 2147483647L | (long) this.location.z + 2147483647L << 32;
                        PlayerChunkMap.this.d.remove(i);
                        PlayerChunkMap.this.f.remove(this);
                    }

                    return;
                }
                // CraftBukkit end
                Chunk chunk = PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z);

                if (chunk.isReady()) {
                    entityplayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, 0));
                }

                this.players.remove(entityplayer); // CraftBukkit
                this.b.remove(entityplayer);
                entityplayer.chunkCoordIntPairQueue.remove(this.location);
                if (this.b.isEmpty()) {
                    long i = (long) this.location.x + 2147483647L | (long) this.location.z + 2147483647L << 32;

                    this.a(chunk);
                    PlayerChunkMap.this.d.remove(i);
                    PlayerChunkMap.this.f.remove(this);
                    if (this.dirtyCount > 0) {
                        PlayerChunkMap.this.e.remove(this);
                    }

                    PlayerChunkMap.this.a().chunkProviderServer.queueUnload(this.location.x, this.location.z);
                }

            }
        }

        public void a() {
            this.a(PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z));
        }

        private void a(Chunk chunk) {
            chunk.c(chunk.w() + PlayerChunkMap.this.world.getTime() - this.g);
            this.g = PlayerChunkMap.this.world.getTime();
        }

        public void a(int i, int j, int k) {
            if (this.dirtyCount == 0) {
                PlayerChunkMap.this.e.add(this);
            }

            this.f |= 1 << (j >> 4);
            if (this.dirtyCount < 64) {
                short short0 = (short) (i << 12 | k << 8 | j);

                for (int l = 0; l < this.dirtyCount; ++l) {
                    if (this.dirtyBlocks[l] == short0) {
                        return;
                    }
                }

                this.dirtyBlocks[this.dirtyCount++] = short0;
            }

        }

        public void a(Packet packet) {
            for (int i = 0; i < this.b.size(); ++i) {
                EntityPlayer entityplayer = (EntityPlayer) this.b.get(i);

                if (!entityplayer.chunkCoordIntPairQueue.contains(this.location)) {
                    entityplayer.playerConnection.sendPacket(packet);
                }
            }

        }

        public void b() {
            if (this.dirtyCount != 0) {
                int i;
                int j;
                int k;

                if (this.dirtyCount == 1) {
                    i = (this.dirtyBlocks[0] >> 12 & 15) + this.location.x * 16;
                    j = this.dirtyBlocks[0] & 255;
                    k = (this.dirtyBlocks[0] >> 8 & 15) + this.location.z * 16;
                    BlockPosition blockposition = new BlockPosition(i, j, k);

                    this.a((Packet) (new PacketPlayOutBlockChange(PlayerChunkMap.this.world, blockposition)));
                    if (PlayerChunkMap.this.world.getType(blockposition).getBlock().isTileEntity()) {
                        this.a(PlayerChunkMap.this.world.getTileEntity(blockposition));
                    }
                } else {
                    int l;

                    if (this.dirtyCount == 64) {
                        i = this.location.x * 16;
                        j = this.location.z * 16;
                        this.a((Packet) (new PacketPlayOutMapChunk(PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z), false, this.f)));

                        for (k = 0; k < 16; ++k) {
                            if ((this.f & 1 << k) != 0) {
                                l = k << 4;
                                List list = PlayerChunkMap.this.world.getTileEntities(i, l, j, i + 16, l + 16, j + 16);

                                for (int i1 = 0; i1 < list.size(); ++i1) {
                                    this.a((TileEntity) list.get(i1));
                                }
                            }
                        }
                    } else {
                        this.a((Packet) (new PacketPlayOutMultiBlockChange(this.dirtyCount, this.dirtyBlocks, PlayerChunkMap.this.world.getChunkAt(this.location.x, this.location.z))));

                        for (i = 0; i < this.dirtyCount; ++i) {
                            j = (this.dirtyBlocks[i] >> 12 & 15) + this.location.x * 16;
                            k = this.dirtyBlocks[i] & 255;
                            l = (this.dirtyBlocks[i] >> 8 & 15) + this.location.z * 16;
                            BlockPosition blockposition1 = new BlockPosition(j, k, l);

                            if (PlayerChunkMap.this.world.getType(blockposition1).getBlock().isTileEntity()) {
                                this.a(PlayerChunkMap.this.world.getTileEntity(blockposition1));
                            }
                        }
                    }
                }

                this.dirtyCount = 0;
                this.f = 0;
            }
        }

        private void a(TileEntity tileentity) {
            if (tileentity != null) {
                Packet packet = tileentity.getUpdatePacket();

                if (packet != null) {
                    this.a(packet);
                }
            }

        }
    }

    // CraftBukkit start - Sorter to load nearby chunks first
    private static class ChunkCoordComparator implements java.util.Comparator<ChunkCoordIntPair> {
        private int x;
        private int z;

        public ChunkCoordComparator (EntityPlayer entityplayer) {
            x = (int) entityplayer.locX >> 4;
            z = (int) entityplayer.locZ >> 4;
        }

        public int compare(ChunkCoordIntPair a, ChunkCoordIntPair b) {
            if (a.equals(b)) {
                return 0;
            }

            // Subtract current position to set center point
            int ax = a.x - this.x;
            int az = a.z - this.z;
            int bx = b.x - this.x;
            int bz = b.z - this.z;

            int result = ((ax - bx) * (ax + bx)) + ((az - bz) * (az + bz));
            if (result != 0) {
                return result;
            }

            if (ax < 0) {
                if (bx < 0) {
                    return bz - az;
                } else {
                    return -1;
                }
            } else {
                if (bx < 0) {
                    return 1;
                } else {
                    return az - bz;
                }
            }
        }
    }
    // CraftBukkit end
}
