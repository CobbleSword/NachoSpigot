package net.minecraft.server;

import java.io.IOException;

public interface Packet<T extends PacketListener> {
    /**
     * Reads the raw packet data from the data stream.
     */
    void a(PacketDataSerializer serializer) throws IOException;

    /**
     * Writes the raw packet data to the data stream.
     */
    void b(PacketDataSerializer serializer) throws IOException;

    /**
     * Passes this packet on to the NetworkManager for processing.
     */
    void a(T t0);
}
