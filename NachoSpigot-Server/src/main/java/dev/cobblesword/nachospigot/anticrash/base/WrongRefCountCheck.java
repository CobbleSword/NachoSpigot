package dev.cobblesword.nachospigot.anticrash.base;

import dev.cobblesword.nachospigot.protocol.PacketListener;
import net.minecraft.server.*;

public class WrongRefCountCheck implements PacketListener {
    @Override
    public boolean onReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        if (packet instanceof PacketPlayInCustomPayload) {
            PacketDataSerializer ab = ((PacketPlayInCustomPayload)packet).b();
            if (ab.refCnt() < 1) {
                playerConnection.getNetworkManager().close(new ChatMessage("Wrong ref count!"));
                return false;
            }
        }
        return true;
    }
}
