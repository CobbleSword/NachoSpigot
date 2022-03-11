package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/*
[Nacho-0026] Optimize Packet Prepender
Don't create PacketDataSerializer and use byteBuf more
And use only 1 instance of all connections

 */
@ChannelHandler.Sharable
public class PacketPrepender extends MessageToMessageEncoder<ByteBuf>
{
    public static final PacketPrepender INSTANCE = new PacketPrepender();

    public PacketPrepender() {
    }

    public static void writeVarInt(ByteBuf buf, int value)
    {
        while (true)
        {
            if ((value & 0xFFFFFF80) == 0) {
                buf.writeByte(value);
                return;
            }

            buf.writeByte(value & 0x7F | 0x80);
            value >>>= 7;
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        ByteBuf lengthBuf = ctx.alloc().buffer(5);
        writeVarInt(lengthBuf, in.readableBytes());
        out.add(lengthBuf);
        out.add(in.retain());
    }

    /*
    protected void a(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception
    {
        int var4 = in.readableBytes();
        int var5 = PacketDataSerializer.a(var4);
        if (var5 > 3) {
            throw new IllegalArgumentException("unable to fit " + var4 + " into " + 3);
        }
        else
        {
            PacketDataSerializer var6 = new PacketDataSerializer(out);
            var6.ensureWritable(var5 + var4);
            var6.b(var4);
            var6.writeBytes(in, in.readerIndex(), var4);
        }
    }

     */
}