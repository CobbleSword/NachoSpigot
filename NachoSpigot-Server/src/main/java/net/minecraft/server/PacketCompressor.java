package net.minecraft.server;

import com.velocitypowered.natives.compression.VelocityCompressor; // Paper
import com.velocitypowered.natives.util.MoreByteBufUtils; // Paper
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class PacketCompressor extends MessageToByteEncoder<ByteBuf> {
    private final byte[] encodeBuf; // Paper
    private final Deflater deflater;
    private final com.velocitypowered.natives.compression.VelocityCompressor compressor; // Paper
    private int threshold;

    public PacketCompressor(int compressionThreshold) {
        // Paper start
        this(null, compressionThreshold);
    }
    public PacketCompressor(VelocityCompressor compressor, int compressionThreshold) {
        this.threshold = compressionThreshold;
        if (compressor == null) {
            this.encodeBuf = new byte[8192];
            this.deflater = new Deflater();
        } else {
            this.encodeBuf = null;
            this.deflater = null;
        }
        this.compressor = compressor;
        // Paper end
    }

    protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
        int var4 = var2.readableBytes();
        PacketDataSerializer var5 = new PacketDataSerializer(var3);
        if (var4 < this.threshold) {
            var5.writeVarInt(0); // Nacho - deobfuscate writeVarInt
            var5.writeBytes(var2);
        } else {
            // Paper start
            if (this.deflater != null) {
                byte[] var6 = new byte[var4];
                var2.readBytes(var6);
                var5.writeVarInt(var6.length); // Nacho - deobfuscate writeVarInt
                this.deflater.setInput(var6, 0, var4);
                this.deflater.finish();

                while (!this.deflater.finished()) {
                    int var7 = this.deflater.deflate(this.encodeBuf);
                    var5.writeBytes(this.encodeBuf, 0, var7);
                }

                this.deflater.reset();
                return;
            }

            var5.writeVarInt(var4);
            ByteBuf compatibleIn = MoreByteBufUtils.ensureCompatible(var1.alloc(), this.compressor, var2);
            try {
                this.compressor.deflate(compatibleIn, var3);
            } finally {
                compatibleIn.release();
            }
            // Paper end
        }
    }

    // Paper start
    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception{
        if (this.compressor != null) {
            // We allocate bytes to be compressed plus 1 byte. This covers two cases:
            //
            // - Compression
            //    According to https://github.com/ebiggers/libdeflate/blob/master/libdeflate.h#L103,
            //    if the data compresses well (and we do not have some pathological case) then the maximum
            //    size the compressed size will ever be is the input size minus one.
            // - Uncompressed
            //    This is fairly obvious - we will then have one more than the uncompressed size.
            int initialBufferSize = msg.readableBytes() + 1;
            return com.velocitypowered.natives.util.MoreByteBufUtils.preferredBuffer(ctx.alloc(), this.compressor, initialBufferSize);
        }

        return super.allocateBuffer(ctx, msg, preferDirect);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (this.compressor != null) {
            this.compressor.close();
        }
    }
    // Paper end

    // Nacho start - deobfuscate
    public void setThreshold(int threshold) {
        this.threshold = threshold;
        // Nacho end
    }
}
