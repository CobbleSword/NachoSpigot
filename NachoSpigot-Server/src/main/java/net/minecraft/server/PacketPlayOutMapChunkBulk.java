package net.minecraft.server;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.List;

public class PacketPlayOutMapChunkBulk implements Packet<PacketListenerPlayOut> {

    private int[] a; public int[] getXArray() { return this.a; }
    private int[] b; public int[] getZArray() { return this.b; }
    private PacketPlayOutMapChunk.ChunkMap[] c;
    private boolean d;
    private World world; // Spigot

    public PacketPlayOutMapChunkBulk() {}

    public PacketPlayOutMapChunkBulk(List<Chunk> list) {
        int i = list.size();

        this.a = new int[i];
        this.b = new int[i];
        this.c = new PacketPlayOutMapChunk.ChunkMap[i];
        this.d = !list.get(0).getWorld().worldProvider.o();

        for (int j = 0; j < i; ++j) {
            Chunk chunk = list.get(j);
            PacketPlayOutMapChunk.ChunkMap map = chunk.getChunkMap(true, '\uffff'); // PaperSpigot

            this.a[j] = chunk.locX;
            this.b[j] = chunk.locZ;
            this.c[j] = map;
        }
        
        world = list.get(0).getWorld(); // Spigot
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.d = serializer.readBoolean();
        int i = serializer.readVarInt();

        this.a = new int[i];
        this.b = new int[i];
        this.c = new PacketPlayOutMapChunk.ChunkMap[i];

        int j;

        for (j = 0; j < i; ++j) {
            this.a[j] = serializer.readInt();
            this.b[j] = serializer.readInt();
            this.c[j] = new PacketPlayOutMapChunk.ChunkMap();
            this.c[j].b = serializer.readShort() & '\uffff';
            this.c[j].a = new byte[PacketPlayOutMapChunk.a(Integer.bitCount(this.c[j].b), this.d, true)];
        }

        for (j = 0; j < i; ++j) {
            serializer.readBytes(this.c[j].a);
        }

    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.writeBoolean(this.d);
        serializer.writeVarInt(this.c.length); // Nacho - deobfuscate writeVarInt

        int i;

        for (i = 0; i < this.a.length; ++i) {
            serializer.writeInt(this.a[i]);
            serializer.writeInt(this.b[i]);
            serializer.writeShort((short) (this.c[i].b & '\uffff'));
        }

        for (i = 0; i < this.a.length; ++i) {
            // Nacho - Spigot your AsyncCatcher is trash do it in a proper way please.
            if (Bukkit.isPrimaryThread()) {
                world.spigotConfig.antiXrayInstance.obfuscateSync(this.a[i], this.b[i], this.c[i].b, this.c[i].a, world); // Spigot
            }
            serializer.writeBytes(this.c[i].a);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
