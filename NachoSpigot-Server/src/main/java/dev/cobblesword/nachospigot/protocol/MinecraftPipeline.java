package dev.cobblesword.nachospigot.protocol;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.server.*;
import dev.cobblesword.nachospigot.Nacho;

public class MinecraftPipeline extends ChannelInitializer<SocketChannel>
{
    private final ServerConnection serverConnection;

    public MinecraftPipeline(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    protected void initChannel(SocketChannel channel) {
        try {
            ChannelConfig config = channel.config();
            config.setOption(ChannelOption.TCP_NODELAY, Nacho.get().getConfig().enableTCPNODELAY);
            config.setOption(ChannelOption.IP_TOS, 0x18); // [Nacho-0027] :: Optimize networking
            config.setOption(ChannelOption.SO_KEEPALIVE, true);
            config.setAllocator(ByteBufAllocator.DEFAULT);
        } catch (Exception ignored) {}

        channel.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(30))
                .addLast("legacy_query", new LegacyPingHandler(serverConnection))
                .addLast("splitter", new PacketSplitter())
                .addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND))
                .addLast("prepender", PacketPrepender.INSTANCE)
                .addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));
        NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);
        this.serverConnection.pending.add(networkmanager); // Paper
        channel.pipeline().addLast("packet_handler", networkmanager);
        networkmanager.a(new HandshakeListener(this.serverConnection.server, networkmanager));
    }
}
