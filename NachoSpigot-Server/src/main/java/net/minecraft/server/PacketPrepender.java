package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {
    public PacketPrepender() {
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        a(channelHandlerContext, byteBuf, byteBuf2);
    }

    protected void a(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
        int var4 = var2.readableBytes();
        int var5 = PacketDataSerializer.a(var4);
        if (var5 > 3) {
            throw new IllegalArgumentException("unable to fit " + var4 + " into " + 3);
        } else {
            PacketDataSerializer var6 = new PacketDataSerializer(var3);
            var6.ensureWritable(var5 + var4);
            var6.b(var4);
            var6.writeBytes(var2, var2.readerIndex(), var4);
        }
    }
}