package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

// CraftBukkit start
import java.net.InetAddress;
import java.util.Map;
// CraftBukkit end

public class HandshakeListener implements PacketHandshakingInListener {

    private static final com.google.gson.Gson gson = new com.google.gson.Gson(); // Spigot
    // CraftBukkit start - add fields
    private static final Map<InetAddress, Long> throttleTracker = new new Object2LongOpenHashMap<>();
    private static int throttleCounter = 0;
    // CraftBukkit end

    private final MinecraftServer a;
    private final NetworkManager b;

    public HandshakeListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.a = minecraftserver;
        this.b = networkmanager;
    }

    public void a(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
        switch (packethandshakinginsetprotocol.a()) {
            case LOGIN: {
                this.b.a(EnumProtocol.LOGIN);
                ChatComponentText text;

                // CraftBukkit start - Connection throttle
                try {
                    long currentTime = System.currentTimeMillis();
                    long connectionThrottle = MinecraftServer.getServer().server.getConnectionThrottle();
                    InetAddress address = ((java.net.InetSocketAddress) this.b.getSocketAddress()).getAddress();

                    synchronized (throttleTracker) {
                        if (throttleTracker.containsKey(address) && !"127.0.0.1".equals(address.getHostAddress()) && currentTime - throttleTracker.get(address) < connectionThrottle) {
                            throttleTracker.put(address, currentTime);
                            text = new ChatComponentText("Connection throttled! Please wait before reconnecting.");
                            this.b.handle(new PacketLoginOutDisconnect(text));
                            this.b.close(text);
                            return;
                        }

                        throttleTracker.put(address, currentTime);
                        throttleCounter++;
                        if (throttleCounter > 200) {
                            throttleCounter = 0;

                            // Cleanup stale entries
                            throttleTracker.entrySet().removeIf(entry -> entry.getValue() > connectionThrottle);
                        }
                    }
                } catch (Throwable t) {
                    org.apache.logging.log4j.LogManager.getLogger().debug("Failed to check connection throttle", t);
                }
                // CraftBukkit end

                if (packethandshakinginsetprotocol.b() > 47) {
                    text = new ChatComponentText(java.text.MessageFormat.format(org.spigotmc.SpigotConfig.outdatedServerMessage, "1.8.8")); // Spigot
                    this.b.handle(new PacketLoginOutDisconnect(text));
                    this.b.close(text);
                } else if (packethandshakinginsetprotocol.b() < 47) {
                    text = new ChatComponentText(java.text.MessageFormat.format(org.spigotmc.SpigotConfig.outdatedClientMessage, "1.8.8")); // Spigot
                    this.b.handle(new PacketLoginOutDisconnect(text));
                    this.b.close(text);
                } else {
                    this.b.a(new LoginListener(this.a, this.b));
                    // Spigot Start
                    if (org.spigotmc.SpigotConfig.bungee) {
                        String[] split = packethandshakinginsetprotocol.hostname.split("\00");
                        if (split.length == 3 || split.length == 4) {
                            packethandshakinginsetprotocol.hostname = split[0];
                            b.l = new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) b.getSocketAddress()).getPort());
                            b.spoofedUUID = com.mojang.util.UUIDTypeAdapter.fromString(split[2]);
                        } else {
                            text = new ChatComponentText("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
                            this.b.handle(new PacketLoginOutDisconnect(text));
                            this.b.close(text);
                            return;
                        }
                        if (split.length == 4) {
                            b.spoofedProfile = gson.fromJson(split[3], com.mojang.authlib.properties.Property[].class);
                        }
                    }
                    // Spigot End
                    ((LoginListener) this.b.getPacketListener()).hostname = packethandshakinginsetprotocol.hostname + ":" + packethandshakinginsetprotocol.port; // CraftBukkit - set hostname
                }
                break;
            }
            case STATUS: {
                this.b.a(EnumProtocol.STATUS);
                this.b.a(new PacketStatusListener(this.a, this.b));
                break;
            }
            default:
                throw new UnsupportedOperationException("Invalid intention " + packethandshakinginsetprotocol.a());
        }
    }

    @Override
    public void a(IChatBaseComponent iChatBaseComponent) {
        // empty?
    }
}
