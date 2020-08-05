package net.minecraft.server;

import co.aikar.timings.SpigotTimings;
import co.aikar.timings.Timing;
import dev.cobblesword.nachospigot.Nacho;
import dev.cobblesword.nachospigot.exception.ExploitException;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker ROOT_MARKER = MarkerManager.getMarker("NETWORK");
    public static final Marker PACKET_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.ROOT_MARKER);
    public static final AttributeKey<EnumProtocol> ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
    public static final AttributeKey<EnumProtocol> c = ATTRIBUTE_PROTOCOL;
    public static final LazyInitVar NETWORK_WORKER_GROUP = new LazyInitVar()
    {
        protected NioEventLoopGroup a() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar NETWORK_EPOLL_WORKER_GROUP = new LazyInitVar() {
        protected EpollEventLoopGroup a() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar LOCAL_WORKER_GROUP = new LazyInitVar() {
        protected LocalEventLoopGroup a() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };

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
    private boolean o; public boolean isEncrypted() { return this.o; } // Nacho - OBFHELPER
    private boolean p; public boolean isDisconnectionHandled() { return this.p; } // Nacho - OBFHELPER
    public void setDisconnectionHandled(boolean handled) { this.p = handled;} // Nacho - OBFHELPER

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

    private final void flush()
    {
        if (this.channel.eventLoop().inEventLoop()) {
                this.channel.flush();
            } else {
                this.channel.eventLoop().execute(() -> {
                    this.channel.flush();
                });
            }
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
            this.a(EnumProtocol.HANDSHAKING);
        } catch (Throwable throwable) {
            NetworkManager.LOGGER.fatal(throwable);
        }

    }

    public void a(EnumProtocol enumprotocol) {
        this.channel.attr(NetworkManager.ATTRIBUTE_PROTOCOL).set(enumprotocol);
        this.channel.config().setAutoRead(true);
//        NetworkManager.g.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }

    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        ChatMessage chatmessage;

        if(throwable instanceof DecoderException)
        {
            DecoderException decoderException = ((DecoderException) throwable);
            if(decoderException.getCause() instanceof ExploitException)
            {
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
            chatmessage = new ChatMessage("disconnect.timeout", new Object[0]);
        } else {
            chatmessage = new ChatMessage("disconnect.genericReason", new Object[] { "Internal Exception: " + throwable});
        }

        this.close(chatmessage);
//        if (MinecraftServer.getServer().isDebugging()) throwable.printStackTrace(); // Spigot
    }

    protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) throws Exception {
        if (this.channel.isOpen())
        {
            if (this.m instanceof PlayerConnection) {
                try {
                    for (dev.cobblesword.nachospigot.protocol.PacketListener packetListener : Nacho.get().getPacketListeners())
                    {
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
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
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
                this.i.add(new NetworkManager.QueuedPacket(packet, (GenericFutureListener[]) null));
            } finally {
                this.j.writeLock().unlock();
            }
        }

    }

    //sendPacket
    public void a(Packet packet, GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        if (this.isConnected()) {
            this.sendPacketQueue();
            this.dispatchPacket(packet, (GenericFutureListener[]) ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener), Boolean.TRUE);
        } else {
            this.j.writeLock().lock();

            try {
                this.i.add(new NetworkManager.QueuedPacket(packet, (GenericFutureListener[]) ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener)));
            } finally {
                this.j.writeLock().unlock();
            }
        }

    }

    //
    public void dispatchPacket(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener, Boolean flushConditional)
    {
        this.packetWrites.getAndIncrement(); // must be befeore using canFlush
        boolean effectiveFlush = flushConditional == null ? this.canFlush : flushConditional.booleanValue();
        final boolean flush = effectiveFlush || packet instanceof PacketPlayOutKeepAlive || packet instanceof PacketPlayOutKickDisconnect; // no delay for certain packets

        final EnumProtocol enumprotocol = EnumProtocol.a(packet);
        final EnumProtocol enumprotocol1 = this.channel.attr(NetworkManager.ATTRIBUTE_PROTOCOL).get();

        if (enumprotocol1 != enumprotocol) {
//            NetworkManager.g.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop())
        {
            if (enumprotocol != enumprotocol1)
            {
                this.a(enumprotocol);
            }

//            ChannelFuture channelfuture = this.channel.writeAndFlush(packet);
            ChannelFuture channelfuture = flush ? this.channel.writeAndFlush(packet) : this.channel.write(packet);
            if (agenericfuturelistener != null)
            {
                channelfuture.addListeners(agenericfuturelistener);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else
        {
            this.channel.eventLoop().execute(() ->
            {
                if (enumprotocol != enumprotocol1) {
                    NetworkManager.this.a(enumprotocol);
                }

                ChannelFuture channelfuture = flush ? this.channel.writeAndFlush(packet) : this.channel.write(packet);
                //ChannelFuture channelfuture = NetworkManager.this.channel.writeAndFlush(packet);

                if (agenericfuturelistener != null) {
                    channelfuture.addListeners(agenericfuturelistener);
                }

                channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    private void a(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener)
    {
        this.dispatchPacket(packet, agenericfuturelistener, Boolean.TRUE);
    }

    private void sendPacketQueue()
    {
            if(this.i.isEmpty()) return; // [Nacho-0019] :: Avoid lock every packet send
            if (this.channel != null && this.channel.isOpen()) {
            this.j.readLock().lock();
            boolean needsFlush = this.canFlush;
            boolean hasWrotePacket = false;

            try
            {
                Iterator<QueuedPacket> iterator = this.i.iterator();
                while (iterator.hasNext()) {
                    QueuedPacket queued = iterator.next();
                    Packet packet = queued.a;
                    if (hasWrotePacket && (needsFlush || this.canFlush))
                    {
                        flush();
                    }
                    iterator.remove();
                    this.dispatchPacket(packet, queued.b, (!iterator.hasNext() && (needsFlush || this.canFlush)) ? Boolean.TRUE : Boolean.FALSE);
                    hasWrotePacket = true;
                }
            }
            finally
            {
                this.j.readLock().unlock();
            }
        }
    }

    private void m()
    {
        this.sendPacketQueue();
    }

    public void tick()
    {
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

    public void close(IChatBaseComponent ichatbasecomponent)
    {
        // Spigot Start
        this.preparing = false;
        // Spigot End
        if (this.channel.isOpen())
        {
            this.channel.close(); // We can't wait as this may be called from an event loop.
            this.n = ichatbasecomponent;
        }
    }

    public boolean c() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public void a(SecretKey secretkey)
    {
        this.o = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
    }

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

    public void a(int i)
    {
        if (i >= 0)
        {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor) this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(i));
            }

            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor) this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(i));
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
            if (!this.isDisconnectionHandled())
            {
                this.setDisconnectionHandled(true);
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
    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception
    { // CraftBukkit - fix decompile error
        this.a(channelhandlercontext, object);
    }

    static class QueuedPacket
    {
        private final Packet a; //packet
        private final GenericFutureListener<? extends Future<? super Void>>[] b; //listener

        public QueuedPacket(Packet packet, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
            this.a = packet;
            this.b = agenericfuturelistener;
        }
    }

    // Spigot Start
    public SocketAddress getRawAddress()
    {
        return this.channel.remoteAddress();
    }
    // Spigot End
}
