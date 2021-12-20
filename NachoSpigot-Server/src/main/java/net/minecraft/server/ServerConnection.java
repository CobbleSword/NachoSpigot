package net.minecraft.server;

import com.google.common.collect.Lists;
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

    public static EventLoopGroup boss, worker;

    public final MinecraftServer server;
    public volatile boolean started;

    private final List<ChannelFuture> listeningChannels = Collections.synchronizedList(Lists.newArrayList());

    private final List<NetworkManager> connectedChannels = Collections.synchronizedList(Lists.newArrayList());

    // Paper start - prevent blocking on adding a new network manager while the server is ticking
    public final java.util.Queue<NetworkManager> pending = new java.util.concurrent.ConcurrentLinkedQueue<>();
    private void addPending() {
        NetworkManager manager;
        while ((manager = pending.poll()) != null) {
            this.connectedChannels.add(manager); // Nacho - deobfuscate connectedChannels
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
        synchronized (this.listeningChannels) { // Nacho - deobfuscate listeningChannels
            Class<? extends ServerChannel> channel = null;
            final int workerThreadCount = Runtime.getRuntime().availableProcessors();

            {
                switch (eventGroupType) {
                    default:
                    case DEFAULT: {
                        LOGGER.info("Finding best event group type using fall-through");
                    }

                    case EPOLL: {
                        if (Epoll.isAvailable()) {
                            boss = new EpollEventLoopGroup(0);
                            worker = new EpollEventLoopGroup(workerThreadCount);

                            channel = EpollServerSocketChannel.class;

                            LOGGER.info("Using epoll");

                            break;
                        }
                    }
                    case KQUEUE: {
                        if (KQueue.isAvailable()) {
                            boss = new KQueueEventLoopGroup(0);
                            worker = new KQueueEventLoopGroup(workerThreadCount);

                            channel = KQueueServerSocketChannel.class;

                            LOGGER.info("Using kqueue");

                            break;
                        }
                    }
                    case NIO: {
                        boss = new NioEventLoopGroup(0);
                        worker = new NioEventLoopGroup(workerThreadCount);

                        channel = NioServerSocketChannel.class;

                        LOGGER.info("Using NIO");

                        break;
                    }
                }
            }

            // Paper/Nacho start - indicate Velocity natives in use
            LOGGER.info("Nacho: Using " + Natives.compress.getLoadedVariant() + " compression from Velocity.");
            LOGGER.info("Nacho: Using " + Natives.cipher.getLoadedVariant() + " cipher from Velocity.");
            // Paper/Nacho end

            this.listeningChannels.add(((new ServerBootstrap() // Nacho - deobfuscate listeningChannels
                    .channel(channel))
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, SERVER_WRITE_MARK)
                    .childHandler(new MinecraftPipeline(this))
                    .group(boss, worker)
                    .localAddress(ip, port))
                    .bind()
                    .syncUninterruptibly());
        }
    }

    public void stopServer() throws InterruptedException {
        this.started = false;
        LOGGER.info("Shutting down event loops");
        for (ChannelFuture future : this.listeningChannels) { // Nacho - deobfuscate listeningChannels
            try {
                future.channel().close().sync();
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        }

    }

    public void c() {
        synchronized (this.connectedChannels) { // Nacho - deobfuscate connectedChannels
            // Spigot Start
            this.addPending(); // Paper
            // This prevents players from 'gaming' the server, and strategically relogging to increase their position in the tick order
            if ( org.spigotmc.SpigotConfig.playerShuffle > 0 && MinecraftServer.currentTick % org.spigotmc.SpigotConfig.playerShuffle == 0 ) {
                Collections.shuffle( this.connectedChannels); // Nacho - deobfuscate connectedChannels
            }
            // Spigot End
            Iterator<NetworkManager> iterator = this.connectedChannels.iterator(); // Nacho - deobfuscate connectedChannels

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
