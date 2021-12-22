package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInChat implements Packet<PacketListenerPlayIn> {

    private String a;

    public String getMessage()
    {
        return this.a;
    }

    public void setMessage(String message)
    {
        this.a = message;
    }

    public PacketPlayInChat() {}

    public PacketPlayInChat(String s) {
        if (s.length() > 100) {
            s = s.substring(0, 100);
        }

        this.a = s;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.a = serializer.readUtf(100); // Nacho - deobfuscate readUtf
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.a(this.a);
    }

    // Spigot Start
    private static final java.util.concurrent.ExecutorService executors = java.util.concurrent.Executors.newCachedThreadPool(
            new com.google.common.util.concurrent.ThreadFactoryBuilder().setDaemon( true ).setNameFormat( "Async Chat Thread - #%d" ).build() );
    public void a(final PacketListenerPlayIn packetlistenerplayin) {
        if ( !a.startsWith("/") )
        {
            executors.submit( new Runnable()
            {

                @Override
                public void run()
                {
                    packetlistenerplayin.a( PacketPlayInChat.this );
                }
            } );
            return;
        }
        // Spigot End
        packetlistenerplayin.a(this);
    }

    public String a() {
        return this.a;
    }
}
