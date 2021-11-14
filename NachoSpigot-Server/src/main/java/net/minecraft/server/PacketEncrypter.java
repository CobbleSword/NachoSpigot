package net.minecraft.server;

import com.velocitypowered.natives.encryption.VelocityCipher; // Paper
import com.velocitypowered.natives.util.MoreByteBufUtils; // Paper
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder; // Paper

import java.util.List; // Paper

public class PacketEncrypter extends MessageToMessageEncoder<ByteBuf> { // Paper - change superclass
    private final VelocityCipher cipher; // Paper

    public PacketEncrypter(VelocityCipher ciper) { // Paper
        this.cipher = ciper; // Paper
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception { // Paper
        // Paper start
        ByteBuf compatible = MoreByteBufUtils.ensureCompatible(channelHandlerContext.alloc(), cipher, byteBuf);
        try {
            cipher.process(compatible);
            list.add(compatible);
        } catch (Exception e) {
            compatible.release(); // compatible will never be used if we throw an exception
            throw e;
        }
        // Paper end
    }

    // Paper start
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        cipher.close();
    }
    // Paper end
}