package net.minecraft.server;

import com.velocitypowered.natives.compression.VelocityCompressor; // Paper
import com.velocitypowered.natives.util.MoreByteBufUtils; // Paper
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class PacketDecompressor extends ByteToMessageDecoder {
    private final Inflater inflater;
    private final com.velocitypowered.natives.compression.VelocityCompressor compressor; // Paper
    private int threshold;

    public PacketDecompressor(int compressionThreshold) {
        this(null, compressionThreshold);
    }
    public PacketDecompressor(VelocityCompressor compressor, int compressionThreshold) {
        this.threshold = compressionThreshold;
        this.inflater = compressor == null ? new Inflater() : null;
        this.compressor = compressor;
    }

    protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
        if (var2.readableBytes() != 0) {
            PacketDataSerializer var4 = new PacketDataSerializer(var2);
            int var5 = var4.readVarInt(); // Nacho - deobfuscate readVarInt
            if (var5 == 0) {
                var3.add(var4.readBytes(var4.readableBytes()));
            } else {
                if (var5 < this.threshold) {
                    throw new DecoderException("Badly compressed packet - size of " + var5 + " is below server threshold of " + this.threshold);
                }

                if (var5 > 2097152) {
                    throw new DecoderException("Badly compressed packet - size of " + var5 + " is larger than protocol maximum of " + 2097152);
                }
                // Paper start
                if(this.inflater != null) {
                    byte[] var6 = new byte[var4.readableBytes()];
                    var4.readBytes(var6);
                    this.inflater.setInput(var6);
                    byte[] var7 = new byte[var5];
                    this.inflater.inflate(var7);
                    var3.add(Unpooled.wrappedBuffer(var7));
                    this.inflater.reset();
                    return;
                }
                int claimedUncompressedSize = var5; // OBFHELPER
                ByteBuf compatibleIn = MoreByteBufUtils.ensureCompatible(var1.alloc(), this.compressor, var2);
                ByteBuf uncompressed = MoreByteBufUtils.preferredBuffer(var1.alloc(), this.compressor, claimedUncompressedSize);
                try {
                    this.compressor.inflate(compatibleIn, uncompressed, claimedUncompressedSize);
                    var3.add(uncompressed);
                    var2.clear();
                } catch (Exception e) {
                    uncompressed.release();
                    throw e;
                } finally {
                    compatibleIn.release();
                }
                // Paper end
            }
        }
    }

    // Paper start
    @Override
    public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
        if (this.compressor != null) {
            this.compressor.close();
        }
    }
    // Paper end

    public void setThreshold(int var1) { // Nacho - deobfuscate
        this.threshold = var1;
    }
}