package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class PacketSplitter extends ByteToMessageDecoder {

    private final byte[] lenBuf = new byte[3]; // Paper

    public PacketSplitter() {
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        // Paper start - if channel is not active just discard the packet
        if (!channelhandlercontext.channel().isActive()) {
            bytebuf.skipBytes(bytebuf.readableBytes());
            return;
        }
        bytebuf.markReaderIndex();

        // Paper start - reuse temporary length buffer
        byte[] abyte = lenBuf;
        java.util.Arrays.fill(abyte, (byte) 0);
        // Paper end

        for(int var5 = 0; var5 < abyte.length; ++var5) {
            if (!bytebuf.isReadable()) {
                bytebuf.resetReaderIndex();
                return;
            }

            abyte[var5] = bytebuf.readByte();
            if (abyte[var5] >= 0) {
                PacketDataSerializer var6 = new PacketDataSerializer(Unpooled.wrappedBuffer(abyte));

                try {
                    int var7 = var6.e();
                    if (bytebuf.readableBytes() < var7) {
                        bytebuf.resetReaderIndex();
                        return;
                    }

                    list.add(bytebuf.readBytes(var7));
                } finally {
                    var6.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
