package net.minecraft.server;

import java.io.IOException;

public interface Packet<T extends PacketListener> {
    /**
     * Reads the raw packet data from the data stream.
     */
    default void readPacketData(PacketDataSerializer serializer) throws IOException {} // Nacho - OBFHELPER
    default void a(PacketDataSerializer serializer) throws IOException { readPacketData(serializer); }

    /**
     * Writes the raw packet data to the data stream.
     */
    default void writePacketData(PacketDataSerializer serializer) throws IOException {}; // Nacho - OBFHELPER
    default void b(PacketDataSerializer serializer) throws IOException { writePacketData(serializer); }

    /**
     * Passes this packet on to the NetHandler for processing.
     */
    default void processPacket(T t0) {}; // Nacho - OBFHELPER
    default void a(T handler) { processPacket(handler); }
}
