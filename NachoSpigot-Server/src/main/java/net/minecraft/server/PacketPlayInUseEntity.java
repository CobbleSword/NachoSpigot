package net.minecraft.server;

import me.elier.nachospigot.config.NachoConfig;

import java.io.IOException;

public class PacketPlayInUseEntity implements Packet<PacketListenerPlayIn> {
    private int a;
    private PacketPlayInUseEntity.EnumEntityUseAction action;
    private Vec3D c;

    public PacketPlayInUseEntity() {
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.readVarInt(); // Nacho - deobfuscate readVarInt
        this.action = var1.a(PacketPlayInUseEntity.EnumEntityUseAction.class);
        if (this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
            this.c = new Vec3D(var1.readFloat(), var1.readFloat(), var1.readFloat());
        }

    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.writeVarInt(this.a); // Nacho - deobfuscate writeVarInt
        var1.a(this.action);
        if (this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
            var1.writeFloat((float)this.c.a);
            var1.writeFloat((float)this.c.b);
            var1.writeFloat((float)this.c.c);
        }

    }

    public void a(PacketListenerPlayIn var1) {
        var1.a(this);
    }

    public Entity a(World var1) {
        return var1.a(this.a);
    }

    public PacketPlayInUseEntity.EnumEntityUseAction a() {
        return this.action;
    }

    public Vec3D b() {
        return this.c;
    }

    public enum EnumEntityUseAction {
        INTERACT,
        ATTACK,
        INTERACT_AT;

        EnumEntityUseAction() {
        }
    }

    @Override
    public boolean instant() {
        return NachoConfig.instantPlayInUseEntity;
    }
}
