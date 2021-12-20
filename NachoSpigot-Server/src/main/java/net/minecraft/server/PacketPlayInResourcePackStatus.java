package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInResourcePackStatus implements Packet<PacketListenerPlayIn> {

    public String a; // TacoSpigot - make public
    public PacketPlayInResourcePackStatus.EnumResourcePackStatus b; // PAIL: private -> public, rename: status

    public PacketPlayInResourcePackStatus() {}

    public void a(PacketDataSerializer serializer) throws IOException {
        this.a = serializer.readUtf(40); // Nacho - deobfuscate readUtf
        this.b = (PacketPlayInResourcePackStatus.EnumResourcePackStatus) serializer.a(PacketPlayInResourcePackStatus.EnumResourcePackStatus.class);
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.a(this.a);
        serializer.a((Enum) this.b);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public static enum EnumResourcePackStatus {

        SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED;

        private EnumResourcePackStatus() {}
    }
}
