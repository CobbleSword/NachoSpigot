package dev.cobblesword.nachospigot.anticrash.base;

import dev.cobblesword.nachospigot.protocol.PacketListener;
import net.minecraft.server.*;

public class WrongReadableBytesCheck implements PacketListener {
    @Override
    public boolean onReceivedPacket(PlayerConnection playerConnection, Packet packet) {
        if (packet instanceof PacketPlayInCustomPayload) {
            PacketDataSerializer ab = ((PacketPlayInCustomPayload)packet).b();
            if (ab.readableBytes() > 16000) {
                playerConnection.getNetworkManager().close(new ChatMessage("Readable bytes exceeds limit!"));
                return false;
            }
        }
        return true;
    }
}
