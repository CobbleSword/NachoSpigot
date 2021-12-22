package net.minecraft.server;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.bukkit.craftbukkit.inventory.CraftItemStack; // CraftBukkit
// TacoSpigot start
import net.techcable.tacospigot.CompatHacks;
// TacoSpigot end

public class PacketDataSerializer extends ByteBuf {

    private final ByteBuf a;

    // TacoSpigot start
    private final boolean allowLargePackets;
    public PacketDataSerializer(ByteBuf bytebuf) {
        /*
         * By default, we limit the size of the received byte array to Short.MAX_VALUE, which is 31 KB.
         * However, we make an exception when ProtocolSupport is installed, to allow 1.7 clients to work,
         * and limit them to 31 MEGABYTES as they seem to need to send larger packets sometimes.
         * Although a 31 MB limit leaves the server slightly vulnerable,
         * it's still much better than the old system of having no limit,
         * which would leave the server vulnerable to packets up to 2 GIGABYTES in size.
         */
        this.allowLargePackets = CompatHacks.hasProtocolSupport();
        // TacoSpigot end
        this.a = bytebuf;
    }

    public static int a(int i) {
        for (int j = 1; j < 5; ++j) {
            if ((i & -1 << j * 7) == 0) {
                return j;
            }
        }

        return 5;
    }

    public void a(byte[] abyte) {
        this.writeVarInt(abyte.length); // Nacho - deobfuscate writeVarInt
        this.writeBytes(abyte);
    }

    // TacoSpigot start
    private static final int DEFAULT_LIMIT = Short.MAX_VALUE;
    private static final int LARGE_PACKET_LIMIT = Short.MAX_VALUE * 1024;
    public byte[] a() {
        // TacoSpigot start
        int limit = allowLargePackets ? LARGE_PACKET_LIMIT : DEFAULT_LIMIT;
        return readByteArray(limit);
    }

    public byte[] readByteArray(int limit) {
        int len = this.readVarInt();
        if (len > limit) throw new DecoderException("The received a byte array longer than allowed " + len + " > " + limit);
        byte[] abyte = new byte[len];
        // TacoSpigot end
        this.readBytes(abyte);
        return abyte;
    }

    public BlockPosition c() {
        return BlockPosition.fromLong(this.readLong());
    }

    public void a(BlockPosition blockposition) {
        this.writeLong(blockposition.asLong());
    }

    public IChatBaseComponent d() throws IOException {
        return IChatBaseComponent.ChatSerializer.a(this.readUtf(32767)); // Nacho - deobfuscate readUtf
    }

    public void a(IChatBaseComponent ichatbasecomponent) throws IOException {
        this.a(IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
    }

    public <T extends Enum<T>> T a(Class<T> oclass) {
        return ((T[]) oclass.getEnumConstants())[this.readVarInt()]; // CraftBukkit - fix decompile error
    }

    public void a(Enum<?> oenum) {
        this.writeVarInt(oenum.ordinal()); // Nacho - deobfuscate writeVarInt
    }

    public int readVarInt() { // Nacho - deobfuscate
        byte b0;
        int i = 0;
        int j = 0;
        do {
            b0 = readByte();
            i |= (b0 & Byte.MAX_VALUE) << j++ * 7;
            if (j > 5)
                throw new RuntimeException("VarInt too big");
        } while ((b0 & 0x80) == 128);
        return i;
    }

    public long f() {
        byte b0;
        long i = 0L;
        int j = 0;
        do {
            b0 = readByte();
            i |= (long) (b0 & Byte.MAX_VALUE) << j++ * 7;
            if (j > 10)
                throw new RuntimeException("VarLong too big");
        } while ((b0 & 0x80) == 128);
        return i;
    }

    public void a(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID() { // Nacho - deobfuscate
        return new UUID(this.readLong(), this.readLong());
    }

    public void writeVarInt(int i) { // Nacho - deobfuscate
        while ((i & -128) != 0) {
            this.writeByte(i & 127 | 128);
            i >>>= 7;
        }

        this.writeByte(i);
    }

    public void b(long i) {
        while ((i & -128L) != 0L) {
            this.writeByte((int) (i & 127L) | 128);
            i >>>= 7;
        }

        this.writeByte((int) i);
    }

    public void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            this.writeByte(0);
        } else {
            try {
                NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) (new ByteBufOutputStream(this)));
            } catch (Exception ioexception) { // CraftBukkit - IOException -> Exception
                throw new EncoderException(ioexception);
            }
        }

    }

    public NBTTagCompound h() throws IOException
    {

        int i = this.readerIndex();
        byte b0 = this.readByte();

        if (b0 == 0) {
            return null;
        } else {
            this.readerIndex(i);
            try {
                return NBTCompressedStreamTools.a((DataInput) (new ByteBufInputStream(this)), new NBTReadLimiter(50000L));
            } catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }

    public void a(ItemStack itemstack) {
        if (itemstack == null || itemstack.getItem() == null) { // CraftBukkit - NPE fix itemstack.getItem()
            this.writeShort(-1);
        } else {
            this.writeShort(Item.getId(itemstack.getItem()));
            this.writeByte(itemstack.count);
            this.writeShort(itemstack.getData());
            NBTTagCompound nbttagcompound = null;

            if (itemstack.getItem().usesDurability() || itemstack.getItem().p()) {
                // Spigot start - filter
                itemstack = itemstack.cloneItemStack();
                CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
                // Spigot end
                nbttagcompound = itemstack.getTag();
            }

            this.a(nbttagcompound);
        }

    }

    public ItemStack i() throws IOException
    {
        return this.decodeItemStack();
    }

    public ItemStack decodeItemStack() throws IOException
    {
        ItemStack itemstack = null;
        short itemId = this.readShort();

        if (itemId >= 0)
        {
            byte amount = this.readByte();
            short data = this.readShort();

            itemstack = new ItemStack(Item.getById(itemId), amount, data);
            itemstack.setTag(this.h());
            // CraftBukkit start
            if (itemstack.getTag() != null)
            {
                CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
            }
            // CraftBukkit end
        }
        return itemstack;
    }

    public String readUtf(int i) { // Nacho - deobfuscate
        int j = this.readVarInt();
        if (j > i * 4)
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + (i * 4) + ")");
        if (j < 0)
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        String s = toString(readerIndex(), j, StandardCharsets.UTF_8);
        readerIndex(readerIndex() + j);
        if (s.length() > i)
            throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")");
        // Nacho end
        return s;
    }

    public PacketDataSerializer a(String s) {
        byte[] abyte = s.getBytes(Charsets.UTF_8);

        if (abyte.length > 32767) {
            throw new EncoderException("String too big (was " + s.length() + " bytes encoded, max " + 32767 + ")");
        } else {
            this.writeVarInt(abyte.length); // Nacho - deobfuscate writeVarInt
            this.writeBytes(abyte);
            return this;
        }
    }

    public int capacity() {
        return this.a.capacity();
    }

    public ByteBuf capacity(int i) {
        return this.a.capacity(i);
    }

    public int maxCapacity() {
        return this.a.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.a.alloc();
    }

    public ByteOrder order() {
        return this.a.order();
    }

    public ByteBuf order(ByteOrder byteorder) {
        return this.a.order(byteorder);
    }

    public ByteBuf unwrap() {
        return this.a.unwrap();
    }

    public boolean isDirect() {
        return this.a.isDirect();
    }

    public boolean isReadOnly() {
        return this.a.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.a.asReadOnly();
    }

    public int readerIndex() {
        return this.a.readerIndex();
    }

    public ByteBuf readerIndex(int i) {
        return this.a.readerIndex(i);
    }

    public int writerIndex() {
        return this.a.writerIndex();
    }

    public ByteBuf writerIndex(int i) {
        return this.a.writerIndex(i);
    }

    public ByteBuf setIndex(int i, int j) {
        return this.a.setIndex(i, j);
    }

    public int readableBytes() {
        return this.a.readableBytes();
    }

    public int writableBytes() {
        return this.a.writableBytes();
    }

    public int maxWritableBytes() {
        return this.a.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.a.isReadable();
    }

    public boolean isReadable(int i) {
        return this.a.isReadable(i);
    }

    public boolean isWritable() {
        return this.a.isWritable();
    }

    public boolean isWritable(int i) {
        return this.a.isWritable(i);
    }

    public ByteBuf clear() {
        return this.a.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.a.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.a.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.a.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.a.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.a.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.a.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int i) {
        return this.a.ensureWritable(i);
    }

    public int ensureWritable(int i, boolean flag) {
        return this.a.ensureWritable(i, flag);
    }

    public boolean getBoolean(int i) {
        return this.a.getBoolean(i);
    }

    public byte getByte(int i) {
        return this.a.getByte(i);
    }

    public short getUnsignedByte(int i) {
        return this.a.getUnsignedByte(i);
    }

    public short getShort(int i) {
        return this.a.getShort(i);
    }

    public short getShortLE(int i) {
        return this.a.getShortLE(i);
    }

    public int getUnsignedShort(int i) {
        return this.a.getUnsignedShort(i);
    }

    public int getUnsignedShortLE(int i) {
        return this.a.getUnsignedShortLE(i);
    }

    public int getMedium(int i) {
        return this.a.getMedium(i);
    }

    public int getMediumLE(int i) {
        return this.a.getMediumLE(i);
    }

    public int getUnsignedMedium(int i) {
        return this.a.getUnsignedMedium(i);
    }

    public int getUnsignedMediumLE(int i) {
        return this.a.getUnsignedMediumLE(i);
    }

    public int getInt(int i) {
        return this.a.getInt(i);
    }

    public int getIntLE(int i) {
        return this.a.getIntLE(i);
    }

    public long getUnsignedInt(int i) {
        return this.a.getUnsignedInt(i);
    }

    public long getUnsignedIntLE(int i) {
        return this.a.getUnsignedIntLE(i);
    }

    public long getLongLE(int i) {
        return this.a.getLongLE(i);
    }

    public long getLong(int i) {
        return this.a.getLong(i);
    }

    public char getChar(int i) {
        return this.a.getChar(i);
    }

    public float getFloat(int i) {
        return this.a.getFloat(i);
    }

    public double getDouble(int i) {
        return this.a.getDouble(i);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf) {
        return this.a.getBytes(i, bytebuf);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j) {
        return this.a.getBytes(i, bytebuf, j);
    }

    public ByteBuf getBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.a.getBytes(i, bytebuf, j, k);
    }

    public ByteBuf getBytes(int i, byte[] abyte) {
        return this.a.getBytes(i, abyte);
    }

    public ByteBuf getBytes(int i, byte[] abyte, int j, int k) {
        return this.a.getBytes(i, abyte, j, k);
    }

    public ByteBuf getBytes(int i, ByteBuffer bytebuffer) {
        return this.a.getBytes(i, bytebuffer);
    }

    public ByteBuf getBytes(int i, OutputStream outputstream, int j) throws IOException {
        return this.a.getBytes(i, outputstream, j);
    }

    public int getBytes(int i, GatheringByteChannel gatheringbytechannel, int j) throws IOException {
        return this.a.getBytes(i, gatheringbytechannel, j);
    }

    @Override
    public int getBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
        return this.a.getBytes(i, fileChannel, l, i1);
    }

    @Override
    public CharSequence getCharSequence(int i, int j, Charset charset) {
        return this.a.getCharSequence(i, j, charset);
    }

    public ByteBuf setBoolean(int i, boolean flag) {
        return this.a.setBoolean(i, flag);
    }

    public ByteBuf setByte(int i, int j) {
        return this.a.setByte(i, j);
    }

    public ByteBuf setShort(int i, int j) {
        return this.a.setShort(i, j);
    }

    @Override
    public ByteBuf setShortLE(int i, int j) {
        return this.a.setShortLE(i, j);
    }

    public ByteBuf setMedium(int i, int j) {
        return this.a.setMedium(i, j);
    }

    @Override
    public ByteBuf setMediumLE(int i, int j) {
        return this.a.setMediumLE(i, j);
    }

    public ByteBuf setInt(int i, int j) {
        return this.a.setInt(i, j);
    }

    @Override
    public ByteBuf setIntLE(int i, int j) {
        return this.a.setIntLE(i, j);
    }

    public ByteBuf setLong(int i, long j) {
        return this.a.setLong(i, j);
    }

    @Override
    public ByteBuf setLongLE(int i, long j) {
        return this.a.setLongLE(i, j);
    }

    public ByteBuf setChar(int i, int j) {
        return this.a.setChar(i, j);
    }

    public ByteBuf setFloat(int i, float f) {
        return this.a.setFloat(i, f);
    }

    public ByteBuf setDouble(int i, double d0) {
        return this.a.setDouble(i, d0);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf) {
        return this.a.setBytes(i, bytebuf);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j) {
        return this.a.setBytes(i, bytebuf, j);
    }

    public ByteBuf setBytes(int i, ByteBuf bytebuf, int j, int k) {
        return this.a.setBytes(i, bytebuf, j, k);
    }

    public ByteBuf setBytes(int i, byte[] abyte) {
        return this.a.setBytes(i, abyte);
    }

    public ByteBuf setBytes(int i, byte[] abyte, int j, int k) {
        return this.a.setBytes(i, abyte, j, k);
    }

    public ByteBuf setBytes(int i, ByteBuffer bytebuffer) {
        return this.a.setBytes(i, bytebuffer);
    }

    public int setBytes(int i, InputStream inputstream, int j) throws IOException {
        return this.a.setBytes(i, inputstream, j);
    }

    public int setBytes(int i, ScatteringByteChannel scatteringbytechannel, int j) throws IOException {
        return this.a.setBytes(i, scatteringbytechannel, j);
    }

    @Override
    public int setBytes(int i, FileChannel filechannel, long j, int k) throws IOException {
        return this.a.setBytes(i, filechannel, j, k);
    }

    public ByteBuf setZero(int i, int j) {
        return this.a.setZero(i, j);
    }

    @Override
    public int setCharSequence(int i, CharSequence charsequence, Charset charset) {
        return this.a.setCharSequence(i, charsequence, charset);
    }

    public boolean readBoolean() {
        return this.a.readBoolean();
    }

    public byte readByte() {
        return this.a.readByte();
    }

    public short readUnsignedByte() {
        return this.a.readUnsignedByte();
    }

    public short readShort() {
        return this.a.readShort();
    }

    @Override
    public short readShortLE() {
        return this.a.readShortLE();
    }

    public int readUnsignedShort() {
        return this.a.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.a.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.a.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.a.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.a.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.a.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.a.readInt();
    }

    @Override
    public int readIntLE() {
        return this.a.readIntLE();
    }

    public long readUnsignedInt() {
        return this.a.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.a.readUnsignedIntLE();
    }

    public long readLong() {
        return this.a.readLong();
    }

    @Override
    public long readLongLE() {
        return this.a.readLongLE();
    }

    public char readChar() {
        return this.a.readChar();
    }

    public float readFloat() {
        return this.a.readFloat();
    }

    public double readDouble() {
        return this.a.readDouble();
    }

    public ByteBuf readBytes(int i) {
        return this.a.readBytes(i);
    }

    public ByteBuf readSlice(int i) {
        return this.a.readSlice(i);
    }

    @Override
    public ByteBuf readRetainedSlice(int i) {
        return this.a.readRetainedSlice(i);
    }


    public ByteBuf readBytes(ByteBuf bytebuf) {
        return this.a.readBytes(bytebuf);
    }

    public ByteBuf readBytes(ByteBuf bytebuf, int i) {
        return this.a.readBytes(bytebuf, i);
    }

    public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
        return this.a.readBytes(bytebuf, i, j);
    }

    public ByteBuf readBytes(byte[] abyte) {
        return this.a.readBytes(abyte);
    }

    public ByteBuf readBytes(byte[] abyte, int i, int j) {
        return this.a.readBytes(abyte, i, j);
    }

    public ByteBuf readBytes(ByteBuffer bytebuffer) {
        return this.a.readBytes(bytebuffer);
    }

    public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
        return this.a.readBytes(outputstream, i);
    }

    public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
        return this.a.readBytes(gatheringbytechannel, i);
    }

    @Override
    public CharSequence readCharSequence(int i, Charset charset) {
        return this.a.readCharSequence(i, charset);
    }

    @Override
    public int readBytes(FileChannel filechannel, long i, int j) throws IOException {
        return this.a.readBytes(filechannel, i, j);
    }

    public ByteBuf skipBytes(int i) {
        return this.a.skipBytes(i);
    }

    public ByteBuf writeBoolean(boolean flag) {
        return this.a.writeBoolean(flag);
    }

    public ByteBuf writeByte(int i) {
        return this.a.writeByte(i);
    }

    public ByteBuf writeShort(int i) {
        return this.a.writeShort(i);
    }

    @Override
    public ByteBuf writeShortLE(int i) {
        return this.a.writeShortLE(i);
    }

    public ByteBuf writeMedium(int i) {
        return this.a.writeMedium(i);
    }

    @Override
    public ByteBuf writeMediumLE(int i) {
        return this.a.writeMediumLE(i);
    }

    public ByteBuf writeInt(int i) {
        return this.a.writeInt(i);
    }

    @Override
    public ByteBuf writeIntLE(int i) {
        return this.a.writeIntLE(i);
    }

    public ByteBuf writeLong(long i) {
        return this.a.writeLong(i);
    }

    @Override
    public ByteBuf writeLongLE(long i) {
        return this.a.writeLongLE(i);
    }

    public ByteBuf writeChar(int i) {
        return this.a.writeChar(i);
    }

    public ByteBuf writeFloat(float f) {
        return this.a.writeFloat(f);
    }

    public ByteBuf writeDouble(double d0) {
        return this.a.writeDouble(d0);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf) {
        return this.a.writeBytes(bytebuf);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
        return this.a.writeBytes(bytebuf, i);
    }

    public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
        return this.a.writeBytes(bytebuf, i, j);
    }

    public ByteBuf writeBytes(byte[] abyte) {
        return this.a.writeBytes(abyte);
    }

    public ByteBuf writeBytes(byte[] abyte, int i, int j) {
        return this.a.writeBytes(abyte, i, j);
    }

    public ByteBuf writeBytes(ByteBuffer bytebuffer) {
        return this.a.writeBytes(bytebuffer);
    }

    public int writeBytes(InputStream inputstream, int i) throws IOException {
        return this.a.writeBytes(inputstream, i);
    }

    public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
        return this.a.writeBytes(scatteringbytechannel, i);
    }

    public int writeBytes(FileChannel filechannel, long i, int j) throws IOException {
        return this.a.writeBytes(filechannel, i, j);
    }

    public ByteBuf writeZero(int i) {
        return this.a.writeZero(i);
    }

    public int writeCharSequence(CharSequence charsequence, Charset charset) {
        return this.a.writeCharSequence(charsequence, charset);
    }

    public int indexOf(int i, int j, byte b0) {
        return this.a.indexOf(i, j, b0);
    }

    public int bytesBefore(byte b0) {
        return this.a.bytesBefore(b0);
    }

    public int bytesBefore(int i, byte b0) {
        return this.a.bytesBefore(i, b0);
    }

    public int bytesBefore(int i, int j, byte b0) {
        return this.a.bytesBefore(i, j, b0);
    }

    public int forEachByte(int i, int j, ByteProcessor byteprocessor) {
        return this.a.forEachByte(i, j, byteprocessor);
    }

    public int forEachByte(ByteProcessor byteprocessor) {
        return this.a.forEachByte(byteprocessor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor byteprocessor) {
        return this.a.forEachByteDesc(byteprocessor);
    }

    public int forEachByteDesc(int i, int j, ByteProcessor byteprocessor) {
        return this.a.forEachByteDesc(i, j, byteprocessor);
    }

    public int forEachByte(ByteBufProcessor bytebufprocessor) {
        return this.a.forEachByte(bytebufprocessor);
    }

    public int forEachByte(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.a.forEachByte(i, j, bytebufprocessor);
    }

    public int forEachByteDesc(ByteBufProcessor bytebufprocessor) {
        return this.a.forEachByteDesc(bytebufprocessor);
    }

    public int forEachByteDesc(int i, int j, ByteBufProcessor bytebufprocessor) {
        return this.a.forEachByteDesc(i, j, bytebufprocessor);
    }

    public ByteBuf copy() {
        return this.a.copy();
    }

    public ByteBuf copy(int i, int j) {
        return this.a.copy(i, j);
    }

    public ByteBuf slice() {
        return this.a.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.a.retainedSlice();
    }

    public ByteBuf slice(int i, int j) {
        return this.a.slice(i, j);
    }

    @Override
    public ByteBuf retainedSlice(int i, int i1) {
        return this.a.readRetainedSlice(i);
    }

    public ByteBuf duplicate() {
        return this.a.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.a.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.a.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.a.nioBuffer();
    }

    public ByteBuffer nioBuffer(int i, int j) {
        return this.a.nioBuffer(i, j);
    }

    public ByteBuffer internalNioBuffer(int i, int j) {
        return this.a.internalNioBuffer(i, j);
    }

    public ByteBuffer[] nioBuffers() {
        return this.a.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int i, int j) {
        return this.a.nioBuffers(i, j);
    }

    public boolean hasArray() {
        return this.a.hasArray();
    }

    public byte[] array() {
        return this.a.array();
    }

    public int arrayOffset() {
        return this.a.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.a.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.a.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.a.toString(charset);
    }

    public String toString(int i, int j, Charset charset) {
        return this.a.toString(i, j, charset);
    }

    public int hashCode() {
        return this.a.hashCode();
    }

    public boolean equals(Object object) {
        return this.a.equals(object);
    }

    public int compareTo(ByteBuf bytebuf) {
        return this.a.compareTo(bytebuf);
    }

    public String toString() {
        return this.a.toString();
    }

    public ByteBuf retain(int i) {
        return this.a.retain(i);
    }

    public ByteBuf retain() {
        return this.a.retain();
    }

    public ByteBuf touch() {
        return this.a.touch();
    }

    public ByteBuf touch(Object o) {
        return this.a.touch(o);
    }

    public int refCnt() {
        return this.a.refCnt();
    }

    public boolean release() {
        return this.a.release();
    }

    public boolean release(int i) {
        return this.a.release(i);
    }
}
