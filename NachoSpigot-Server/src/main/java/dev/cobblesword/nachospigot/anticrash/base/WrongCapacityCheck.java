package dev.cobblesword.nachospigot.anticrash.base;

import dev.cobblesword.nachospigot.protocol.PacketListener;
import net.minecraft.server.*;

public class WrongCapacityCheck implements PacketListener {
    @Override
    public boolean onReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        if (packet instanceof PacketPlayInCustomPayload) {
            PacketDataSerializer ab = ((PacketPlayInCustomPayload)packet).b();
            // ty Lew_x :)
            if (ab.capacity() > 4800 || ab.capacity() < 1) {
                playerConnection.getNetworkManager().close(new ChatMessage("Wrong capacity!"));
                return false;
            }
        }
        return true;
    }
}
