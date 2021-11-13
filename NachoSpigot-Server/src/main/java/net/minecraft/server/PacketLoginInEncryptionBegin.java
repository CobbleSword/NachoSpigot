package net.minecraft.server;

import java.io.IOException;
import java.security.PrivateKey;
import javax.crypto.SecretKey;

public class PacketLoginInEncryptionBegin implements Packet<PacketLoginInListener> {

    private byte[] a = new byte[0];
    private byte[] b = new byte[0];

    public PacketLoginInEncryptionBegin() {}

    public void a(PacketDataSerializer serializer) throws IOException {
        // TacoSpigot start - limit to 256 bytes
        this.a = serializer.readByteArray(256);
        this.b = serializer.readByteArray(256);
        // TacoSpigot end
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.a(this.a);
        serializer.a(this.b);
    }

    public void a(PacketLoginInListener packetlogininlistener) {
        packetlogininlistener.a(this);
    }

    public SecretKey a(PrivateKey privatekey) {
        return MinecraftEncryption.a(privatekey, this.a);
    }

    public byte[] b(PrivateKey privatekey) {
        return privatekey == null ? this.b : MinecraftEncryption.b(privatekey, this.b);
    }

    // TacoSpigot start - fernflower is gud at generics
    /*
    public void a(PacketListener packetlistener) {
        this.a((PacketLoginInListener) packetlistener);
    }
    */
    // TacoSpigot end
}
