package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.velocitypowered.natives.util.Natives; // Paper
import dev.cobblesword.nachospigot.protocol.MinecraftPipeline; // Nacho
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConnection {

    public enum EventGroupType {
        EPOLL,
        KQUEUE,
        NIO,
        DEFAULT
    }

    private static final WriteBufferWaterMark SERVER_WRITE_MARK = new WriteBufferWaterMark(1 << 20, 1 << 21);

    private static final Logger LOGGER = LogManager.getLogger();

    private final EventGroupType eventGroupType;

    public static LazyInitVar<EventLoopGroup> a, b; // a = boss, b = worker


    public static final LazyInitVar<DefaultEventLoopGroup> c = new LazyInitVar<>(() ->
            new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Server IO #%d").setDaemon(true).build())
    );

    public final MinecraftServer server;
    public volatile boolean started;

    private final List<ChannelFuture> g = Collections.synchronizedList(Lists.newArrayList());
    public List<ChannelFuture> getListeningChannels() { return this.g; } // OBFHELPER

    private final List<NetworkManager> h = Collections.synchronizedList(Lists.newArrayList());
    public List<NetworkManager> getConnectedChannels() { return this.h; } // OBFHELPER

    // Paper start - prevent blocking on adding a new network manager while the server is ticking
    public final java.util.Queue<NetworkManager> pending = new java.util.concurrent.ConcurrentLinkedQueue<>();
    private void addPending() {
        NetworkManager manager;
        while ((manager = pending.poll()) != null) {
            this.getConnectedChannels().add(manager);
        }
    }
    // Paper end


    public ServerConnection(MinecraftServer server) {
        this.server = server;
        this.started = true;

        if (server.ai()) /* use-native-transport */ {
            if (Epoll.isAvailable()) {
                this.eventGroupType = EventGroupType.EPOLL;
                return;
            } else if (KQueue.isAvailable()) {
                this.eventGroupType = EventGroupType.KQUEUE;
                return;
            }
        }

        this.eventGroupType = server.getTransport();
    }

    public void a(InetAddress ip, int port) throws IOException {
        synchronized (this.getListeningChannels()) {
            Class<? extends ServerChannel> channel = null;
            final int workerThreadCount = Runtime.getRuntime().availableProcessors();

            {
                // First time using fall-through, lol
                switch (eventGroupType) {
                    default:
                    case DEFAULT: {
                        LOGGER.info("Finding best event group type using fall-through");
                    }

                    case EPOLL: {
                        if (Epoll.isAvailable()) {
                            a = new LazyInitVar<>(() -> new EpollEventLoopGroup(2));
                            b = new LazyInitVar<>(() -> new EpollEventLoopGroup(workerThreadCount));

                            channel = EpollServerSocketChannel.class;

                            LOGGER.info("Using epoll");

                            break;
                        }
                    }
                    case KQUEUE: {
                        if (KQueue.isAvailable()) {
                            a = new LazyInitVar<>(() -> new KQueueEventLoopGroup(2));
                            b = new LazyInitVar<>(() -> new KQueueEventLoopGroup(workerThreadCount));

                            channel = KQueueServerSocketChannel.class;

                            LOGGER.info("Using kqueue");

                            break;
                        }
                    }
                    case NIO: {
                        a = new LazyInitVar<>(() -> new NioEventLoopGroup(2));
                        b = new LazyInitVar<>(() -> new NioEventLoopGroup(workerThreadCount));

                        channel = NioServerSocketChannel.class;

                        LOGGER.info("Using NIO");

                        break;
                    }
                }
            }

            // Paper start - indicate Velocity natives in use
            LOGGER.info("Nacho: Using " + Natives.compress.getLoadedVariant() + " compression from Velocity.");
            LOGGER.info("Nacho: Using " + Natives.cipher.getLoadedVariant() + " cipher from Velocity.");
            // Paper end

            this.getListeningChannels().add(((new ServerBootstrap()
                    .channel(channel))
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, SERVER_WRITE_MARK)
                    .childHandler(new MinecraftPipeline(this))
                    .group(a.get(), b.get())
                    .localAddress(ip, port))
                    .bind()
                    .syncUninterruptibly());
        }
    }

    public void stopServer() throws InterruptedException {
        this.started = false;
        LOGGER.info("Shutting down event loops");
        for (ChannelFuture channelfuture : this.getListeningChannels()) {
            try {
                channelfuture.channel().close().sync();
            } finally {
                a.get().shutdownGracefully();
                b.get().shutdownGracefully();
                c.get().shutdownGracefully();
            }
        }

    }

    public void c() {
        synchronized (this.getConnectedChannels()) {
            // Spigot Start
            this.addPending(); // Paper
            // This prevents players from 'gaming' the server, and strategically relogging to increase their position in the tick order
            if ( org.spigotmc.SpigotConfig.playerShuffle > 0 && MinecraftServer.currentTick % org.spigotmc.SpigotConfig.playerShuffle == 0 ) {
                Collections.shuffle( this.h );
            }
            // Spigot End
            Iterator<NetworkManager> iterator = this.h.iterator();

            while (iterator.hasNext()) {
                final NetworkManager networkmanager = iterator.next();

                if (!networkmanager.h()) {
                    if (!networkmanager.isConnected()) {
                        // Spigot Start
                        // Fix a race condition where a NetworkManager could be unregistered just before connection.
                        if (networkmanager.preparing) continue;
                        // Spigot End
                        iterator.remove();
                        networkmanager.l();
                    } else {
                        try {
                            networkmanager.tick();
                        } catch (Exception exception) {
                            if (networkmanager.c()) {
                                CrashReport crashreport = CrashReport.a(exception, "Ticking memory connection");
                                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Ticking connection");

                                crashreportsystemdetails.a("Connection", networkmanager::toString);
                                throw new ReportedException(crashreport);
                            }

                            ServerConnection.LOGGER.warn("Failed to handle packet for " + networkmanager.getSocketAddress(), exception);
                            final ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");

                            networkmanager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), (GenericFutureListener) future -> networkmanager.close(chatcomponenttext), new GenericFutureListener[0]);
                            networkmanager.k();
                        }
                    }
                }
            }

        }
    }

    public MinecraftServer d() {
        return this.server;
    }
}
