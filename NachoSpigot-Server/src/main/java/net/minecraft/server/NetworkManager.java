package net.minecraft.server;

import com.velocitypowered.natives.compression.VelocityCompressor; // Paper
import com.velocitypowered.natives.util.Natives; // Paper
import dev.cobblesword.nachospigot.Nacho; // Nacho
import dev.cobblesword.nachospigot.exception.ExploitException; // Nacho
import com.google.common.collect.Queues;
import io.netty.channel.*;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import dev.cobblesword.nachospigot.commons.minecraft.CryptException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker ROOT_MARKER = MarkerManager.getMarker("NETWORK");
    public static final Marker PACKET_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.ROOT_MARKER);
    public static final AttributeKey<EnumProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
    public static final AttributeKey<EnumProtocol> c = ATTRIBUTE_PROTOCOL;

    private final EnumProtocolDirection h;
    private final Queue<NetworkManager.QueuedPacket> i = Queues.newConcurrentLinkedQueue();
    private final ReentrantReadWriteLock j = new ReentrantReadWriteLock();
    public Channel channel;
    // Spigot Start // PAIL
    public SocketAddress l;
    public java.util.UUID spoofedUUID;
    public com.mojang.authlib.properties.Property[] spoofedProfile;
    public boolean preparing = true;
    // Spigot End
    private PacketListener m;
    private IChatBaseComponent n;
    private boolean encrypted; // Nacho - deobfuscate
    private boolean isDisconnectionHandled; // Nacho - deobfuscate

    // Tuinity start - allow controlled flushing
    volatile boolean canFlush = true;
    private final java.util.concurrent.atomic.AtomicInteger packetWrites = new java.util.concurrent.atomic.AtomicInteger();
    private int flushPacketsStart;
    private final Object flushLock = new Object();

    void disableAutomaticFlush() {
        synchronized (this.flushLock) {
            this.flushPacketsStart = this.packetWrites.get(); // must be volatile and before canFlush = false
            this.canFlush = false;
        }
    }

    void enableAutomaticFlush() {
        synchronized (this.flushLock)
        {
            this.canFlush = true;
            if (this.packetWrites.get() != this.flushPacketsStart) { // must be after canFlush = true
                this.flush(); // only make the flush call if we need to
            }
        }
    }

    private void flush()
    {
        if (this.channel.eventLoop().inEventLoop()) {
            this.channel.flush();
        } //[Nacho-Spigot] Fixed RejectedExecutionException: event executor terminated by BeyazPolis
    }
    // Tuinity end - allow controlled flushing

    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        this.h = enumprotocoldirection;
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.l = this.channel.remoteAddress();
        // Spigot Start
        this.preparing = false;
        // Spigot End

        try {
            this.setProtocol(EnumProtocol.HANDSHAKING);
        } catch (Throwable throwable) {
            NetworkManager.LOGGER.fatal(throwable);
        }

    }

    public void setProtocol(EnumProtocol protocol) {
        a(protocol);
    }

    public void a(EnumProtocol protocol) {
        this.channel.attr(NetworkManager.ATTRIBUTE_PROTOCOL).set(protocol);
        this.channel.config().setAutoRead(true);
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        ChatMessage chatmessage;

        if(throwable instanceof DecoderException) {
            DecoderException decoderException = ((DecoderException) throwable);
            if(decoderException.getCause() instanceof ExploitException) {
                Bukkit.getLogger().warning("Server crash detected...");
                if(this.getPacketListener() != null && this.getPacketListener() instanceof PlayerConnection) {
                    PlayerConnection playerConnection = (PlayerConnection) this.getPacketListener();
                    CraftPlayer player = playerConnection.getPlayer();
                    if(player != null) {
                        Bukkit.getLogger().warning(player.getName() + " has tried to crash the server... " + decoderException.getCause());
                    }
                }
            }
        }

        if (throwable instanceof TimeoutException) {
            chatmessage = new ChatMessage("disconnect.timeout");
        } else {
            chatmessage = new ChatMessage("disconnect.genericReason", "Internal Exception: " + throwable);
        }

        this.close(chatmessage);
//        if (MinecraftServer.getServer().isDebugging()) throwable.printStackTrace(); // Spigot
    }

    protected void a(ChannelHandlerContext ctx, Packet packet) throws Exception {
        if (this.channel.isOpen()) {
            if (this.m instanceof PlayerConnection) {
                try {
                    for (dev.cobblesword.nachospigot.protocol.PacketListener packetListener : Nacho.get().getPacketListeners()) {
                        if(!packetListener.onReceivedPacket((PlayerConnection) this.m, packet))
                            return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                packet.a(this.m);//packet.handle(PlayerConnection)
            } catch (CancelledPacketHandleException cancelledpackethandleexception) {
                ;
            }
        }
    }

    public void a(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener");
//        NetworkManager.g.debug("Set listener of {} to {}", new Object[] { this, packetlistener});
        this.m = packetlistener;
    }

    //sendPacket
    public void handle(Packet packet) {
        if (this.isConnected()) {
            this.sendPacketQueue();
            this.dispatchPacket(packet, null, Boolean.TRUE);
        } else {
            this.j.writeLock().lock();

            try {
                this.i.add(new NetworkManager.QueuedPacket(packet));
            } finally {
                this.j.writeLock().unlock();
            }
        }

    }

    //sendPacket
    public void a(Packet packet, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners) {
        if (this.isConnected()) {
            this.sendPacketQueue();
            this.dispatchPacket(packet, ArrayUtils.insert(0, listeners, listener), Boolean.TRUE);
        } else {
            this.j.writeLock().lock();

            try {
                this.i.add(new NetworkManager.QueuedPacket(packet, ArrayUtils.insert(0, listeners, listener)));
            } finally {
                this.j.writeLock().unlock();
            }
        }

    }

    // Paper / Nacho start
    public EntityPlayer getPlayer() {
        if (getPacketListener() instanceof PlayerConnection) {
            return ((PlayerConnection) getPacketListener()).player;
        } else {
            return null;
        }
    }
    // Paper / Nacho end

    public void dispatchPacket(final Packet<?> packet, final GenericFutureListener<? extends Future<? super Void>>[] listeners, Boolean flushConditional) {
        this.packetWrites.getAndIncrement(); // must be before using canFlush
        boolean effectiveFlush = flushConditional == null ? this.canFlush : flushConditional;
        final boolean flush = effectiveFlush || packet instanceof PacketPlayOutKeepAlive || packet instanceof PacketPlayOutKickDisconnect; // no delay for certain packets
        final EnumProtocol enumprotocol = EnumProtocol.getProtocolForPacket(packet);
        final EnumProtocol enumprotocol1 = this.channel.attr(NetworkManager.ATTRIBUTE_PROTOCOL).get();
        if (enumprotocol1 != enumprotocol) {
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (enumprotocol != enumprotocol1) {
                this.setProtocol(enumprotocol);
            }
            ChannelFuture channelfuture = flush ? this.channel.writeAndFlush(packet) : this.channel.write(packet);
            if (listeners != null) {
                channelfuture.addListeners(listeners);
            }
            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else {
            // Tuinity start - optimise packets that are not flushed
            Runnable choice1 = null;
            AbstractEventExecutor.LazyRunnable choice2 = null;
            // note: since the type is not dynamic here, we need to actually copy the old executor code
            // into two branches. On conflict, just re-copy - no changes were made inside the executor code.
            if (flush) {
                choice1 = () -> {
                    if (enumprotocol != enumprotocol1) {
                        this.setProtocol(enumprotocol);
                    }
                    try {
                        ChannelFuture channelfuture1 = (flush) ? this.channel.writeAndFlush(packet) : this.channel.write(packet); // Tuinity - add flush parameter
                        if (listeners != null) {
                            channelfuture1.addListeners(listeners);
                        }
                        channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    } catch (Exception e) {
                        LOGGER.error("NetworkException: " + getPlayer(), e);
                        close(new ChatMessage("disconnect.genericReason", "Internal Exception: " + e.getMessage()));;
                    }
                };
            } else {
                // explicitly declare a variable to make the lambda use the type
                choice2 = () -> {
                    if (enumprotocol != enumprotocol1) {
                        this.setProtocol(enumprotocol);
                    }
                    try {
                        // Nacho - why not remove the check below if the check is done above? just code duplication...
                        // even IntelliJ screamed at me for doing leaving it like that :shrug:
                        ChannelFuture channelfuture1 = /* (flush) ? this.channel.writeAndFlush(packet) :  */this.channel.write(packet); // Nacho - see above // Tuinity - add flush parameter
                        if (listeners != null) {
                            channelfuture1.addListeners(listeners);
                        }
                        channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    } catch (Exception e) {
                        LOGGER.error("NetworkException: " + getPlayer(), e);
                        close(new ChatMessage("disconnect.genericReason", "Internal Exception: " + e.getMessage()));;
                    }
                };
            }
            this.channel.eventLoop().execute(choice1 != null ? choice1 : choice2);
            // Tuinity end - optimise packets that are not flushed
        }
    }

    private void a(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
        this.dispatchPacket(packet, agenericfuturelistener, Boolean.TRUE);
    }

    private void sendPacketQueue() {
        if(this.i.isEmpty()) return; // [Nacho-0019] :: Avoid lock every packet send
        if (this.channel != null && this.channel.isOpen()) {
            this.j.readLock().lock();
            boolean needsFlush = this.canFlush;
            boolean hasWrotePacket = false;
            try {
                Iterator<QueuedPacket> iterator = this.i.iterator();
                while (iterator.hasNext()) {
                    QueuedPacket queued = iterator.next();
                    Packet packet = queued.a;
                    if (hasWrotePacket && (needsFlush || this.canFlush)) flush();
                    iterator.remove();
                    this.dispatchPacket(packet, queued.b, (!iterator.hasNext() && (needsFlush || this.canFlush)) ? Boolean.TRUE : Boolean.FALSE);
                    hasWrotePacket = true;
                }
            } finally {
                this.j.readLock().unlock();
            }
        }
    }

    private void m()
    {
        this.sendPacketQueue();
    }

    public void tick() {
        this.sendPacketQueue();
        if (this.m instanceof IUpdatePlayerListBox) {
            ((IUpdatePlayerListBox) this.m).c();
        }

        this.channel.flush();
    }

    public void a() {
        this.tick();
    }

    public SocketAddress getSocketAddress() {
        return this.l;
    }

    public void close(IChatBaseComponent ichatbasecomponent) {
        this.i.clear(); // FlamePaper - Minetick fix memory leaks
        // Spigot Start
        this.preparing = false;
        // Spigot End
        if (this.channel.isOpen()) {
            this.channel.close(); // We can't wait as this may be called from an event loop.
            this.n = ichatbasecomponent;
        }
    }

    public boolean c() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    // Paper start
    /*
    public void setEncryptionKey(SecretKey secretkey) {
        this.o = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
    }*/

    public void setupEncryption(javax.crypto.SecretKey key) throws CryptException {
        if (!this.encrypted) { // Nacho - deobfuscate encrypted
            try {
                com.velocitypowered.natives.encryption.VelocityCipher decryption = com.velocitypowered.natives.util.Natives.cipher.get().forDecryption(key);
                com.velocitypowered.natives.encryption.VelocityCipher encryption = com.velocitypowered.natives.util.Natives.cipher.get().forEncryption(key);

                this.encrypted = true; // Nacho - deobfuscate encrypted
                this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(decryption));
                this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(encryption));
            } catch (java.security.GeneralSecurityException e) {
                throw new CryptException(e);
            }
        }
    }
    // Paper end

    public boolean isConnected()
    {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean g() {
        return this.isConnected();
    }

    public boolean h() {
        return this.channel == null;
    }

    public PacketListener getPacketListener() {
        return this.m;
    }

    public IChatBaseComponent j() {
        return this.n;
    }

    public void k() {
        this.channel.config().setAutoRead(false);
    }

    public void setupCompression(int compressionThreshold) { // Nacho - deobfuscate
        if (compressionThreshold >= 0) {
            VelocityCompressor compressor = Natives.compress.get().create(-1); // Paper
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor) this.channel.pipeline().get("decompress")).setThreshold(compressionThreshold); // Nacho - deobfuscate setThreshold
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(compressor, compressionThreshold)); // Paper
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor) this.channel.pipeline().get("decompress")).setThreshold(compressionThreshold); // Nacho - deobfuscate setThreshold
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(compressor, compressionThreshold)); // Paper
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void handleDisconnection()
    {
        if (this.channel != null && !this.channel.isOpen())
        {
            if (!this.isDisconnectionHandled) // Nacho - deobfuscate isDisconnectionHandled
            {
                this.isDisconnectionHandled = true; // Nacho - deobfuscate isDisconnectionHandled
                if (this.j() != null) {
                    this.getPacketListener().a(this.j());
                } else if (this.getPacketListener() != null) {
                    this.getPacketListener().a(new ChatComponentText("Disconnected"));
                }
                this.i.clear(); // Free up packet queue.
            } else {
                NetworkManager.LOGGER.warn("handleDisconnection() called twice");
            }

        }
    }

    public void l()
    {
        this.handleDisconnection();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception { // CraftBukkit - fix decompile error
        // this.a(channelhandlercontext, object);
        // FlamePaper - Check if channel is opened before reading packet
        if (g()) {
            this.a(channelhandlercontext, object);
        }
    }

    static class QueuedPacket {
        private final Packet a; //packet
        private final GenericFutureListener<? extends Future<? super Void>>[] b; //listener

        @SafeVarargs
        public QueuedPacket(Packet packet, GenericFutureListener<? extends Future<? super Void>> ...listeners) {
            this.a = packet;
            this.b = listeners;
        }
    }

    // Spigot Start
    public SocketAddress getRawAddress() {
        return this.channel.remoteAddress();
    }
    // Spigot End
}
