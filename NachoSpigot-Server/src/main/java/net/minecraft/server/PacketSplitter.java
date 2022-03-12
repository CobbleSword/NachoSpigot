package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

/*
[Nacho-0025] Optimize Packet Splitter
Every packet we recieve, we are forced to
create PacketDataSerializer to decode 3 bytes of data
then we just release the object....

why don't we just move that function over here?

Now, we this handler is stateless we can easily share it across all handlers,
using less PacketSplitter objects, willn't affect much tho
 */
public class PacketSplitter extends ByteToMessageDecoder {

    // private final byte[] lenBuf = new byte[3]; // Paper // Nacho

    public PacketSplitter()
    {
    }

    // [Vanilla Copy] Pulled from PacketDataSerializer:e()
    public int readVarInt(ByteBuf byteBuf)
    {
        byte b0;
        int i = 0;
        int j = 0;
        do {
            b0 = byteBuf.readByte();
            i |= (b0 & Byte.MAX_VALUE) << j++ * 7;
            if (j > 5)
                throw new RuntimeException("VarInt too big");
        } while ((b0 & 0x80) == 128);
        return i;
    }

    /*
    [Nacho-0025]
    We re wrote this method to use 1 less object per packet
     */
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) return;
        int origReaderIndex = in.readerIndex();

        for (int i = 0; i < 3; i++)
        {
            if (!in.isReadable())
            {
                in.readerIndex(origReaderIndex);
                return;
            }

            byte read = in.readByte();
            if (read >= 0) {
                in.readerIndex(origReaderIndex);
                int length = readVarInt(in);
                if (length == 0) return;

                if (in.readableBytes() < length)
                {
                    in.readerIndex(origReaderIndex);
                    return;
                }

                out.add(in.readRetainedSlice(length));
                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
