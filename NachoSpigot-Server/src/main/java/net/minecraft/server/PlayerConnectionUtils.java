package net.minecraft.server;

public class PlayerConnectionUtils {
    public static <T extends PacketListener> void ensureMainThread(final Packet<T> packet, final T listener, IAsyncTaskHandler handler) throws CancelledPacketHandleException {
        if (!packet.instant() && !handler.isMainThread()) {
            handler.postToMainThread(() -> packet.a(listener));
            throw CancelledPacketHandleException.INSTANCE;
        }
    }
}
