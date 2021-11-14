package net.minecraft.server;

public class PlayerConnectionUtils {
    public static <T extends PacketListener> void ensureMainThread(final Packet<T> packet, final T listener, IAsyncTaskHandler iasynctaskhandler) throws CancelledPacketHandleException {
        if (!packet.instant() && !iasynctaskhandler.isMainThread()) {
            iasynctaskhandler.postToMainThread(new Runnable() {
                public void run() {
                    packet.a(listener);
                }
            });
            throw CancelledPacketHandleException.INSTANCE;
        }
    }
}
