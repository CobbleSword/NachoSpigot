package dev.cobblesword.nachospigot.protocol;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.server.*;

public class MinecraftPipeline extends ChannelInitializer<SocketChannel>
{
    private ServerConnection serverConnection;

    public MinecraftPipeline(ServerConnection serverConnection)
    {
        this.serverConnection = serverConnection;
    }

    protected void initChannel(SocketChannel channel) throws Exception
    {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        } catch (ChannelException channelexception) {
            // Ignore
        }

        try {
            channel.config().setOption(ChannelOption.IP_TOS, 0x18);
        } catch (ChannelException e) {
            // Ignore
        }

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
        networkmanager.a(new HandshakeListener(this.serverConnection.f, networkmanager));
    }
}
