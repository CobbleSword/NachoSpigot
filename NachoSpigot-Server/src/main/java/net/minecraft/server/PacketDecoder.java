package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketDecoder extends ByteToMessageDecoder {
    private static final Logger a = LogManager.getLogger();
    private static final Marker b;
    private final EnumProtocolDirection c;

    public PacketDecoder(EnumProtocolDirection var1) {
        this.c = var1;
    }

    protected void decode(ChannelHandlerContext ctx , ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) return;

        PacketDataSerializer packetDataHelper = new PacketDataSerializer(in);
        int packetId = packetDataHelper.readVarInt();
        Packet<?> packet = ctx.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get().createPacket(this.c, packetId);
        if (packet == null)
            throw new IOException("Bad packet id " + packetId);

        packet.a(packetDataHelper);

        if (packetDataHelper.isReadable())
            throw new IOException("Packet " + ctx.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get().getStateId() + "/" + packetId + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetDataHelper.readableBytes() + " bytes extra whilst reading packet " + packetId);
        out.add(packet);
    }

    static {
        b = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.PACKET_MARKER);
    }
}