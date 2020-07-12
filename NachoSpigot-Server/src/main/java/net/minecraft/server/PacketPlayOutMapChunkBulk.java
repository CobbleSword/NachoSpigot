package net.minecraft.server;

import java.io.IOException;
import java.util.List;

public class PacketPlayOutMapChunkBulk implements Packet<PacketListenerPlayOut> {

    private int[] a;
    private int[] b;
    private PacketPlayOutMapChunk.ChunkMap[] c;
    private boolean d;
    private World world; // Spigot

    public PacketPlayOutMapChunkBulk() {}

    public PacketPlayOutMapChunkBulk(List<Chunk> list) {
        int i = list.size();

        this.a = new int[i];
        this.b = new int[i];
        this.c = new PacketPlayOutMapChunk.ChunkMap[i];
        this.d = !((Chunk) list.get(0)).getWorld().worldProvider.o();

        for (int j = 0; j < i; ++j) {
            Chunk chunk = (Chunk) list.get(j);
            PacketPlayOutMapChunk.ChunkMap packetplayoutmapchunk_chunkmap = chunk.getChunkMap(true, '\uffff'); // PaperSpigot

            this.a[j] = chunk.locX;
            this.b[j] = chunk.locZ;
            this.c[j] = packetplayoutmapchunk_chunkmap;
        }
        
        world = ((Chunk) list.get(0)).getWorld(); // Spigot
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.d = packetdataserializer.readBoolean();
        int i = packetdataserializer.e();

        this.a = new int[i];
        this.b = new int[i];
        this.c = new PacketPlayOutMapChunk.ChunkMap[i];

        int j;

        for (j = 0; j < i; ++j) {
            this.a[j] = packetdataserializer.readInt();
            this.b[j] = packetdataserializer.readInt();
            this.c[j] = new PacketPlayOutMapChunk.ChunkMap();
            this.c[j].b = packetdataserializer.readShort() & '\uffff';
            this.c[j].a = new byte[PacketPlayOutMapChunk.a(Integer.bitCount(this.c[j].b), this.d, true)];
        }

        for (j = 0; j < i; ++j) {
            packetdataserializer.readBytes(this.c[j].a);
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeBoolean(this.d);
        packetdataserializer.b(this.c.length);

        int i;

        for (i = 0; i < this.a.length; ++i) {
            packetdataserializer.writeInt(this.a[i]);
            packetdataserializer.writeInt(this.b[i]);
            packetdataserializer.writeShort((short) (this.c[i].b & '\uffff'));
        }

        for (i = 0; i < this.a.length; ++i) {
            world.spigotConfig.antiXrayInstance.obfuscate(this.a[i], this.b[i], this.c[i].b, this.c[i].a, world); // Spigot
            packetdataserializer.writeBytes(this.c[i].a);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
