package net.minecraft.server;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.plugin.AuthorNagException;
// CraftBukkit end

public class LoginListener implements PacketLoginInListener, IUpdatePlayerListBox {

    // Paper start - Cache authenticator threads
    private static final AtomicInteger threadId = new AtomicInteger(0);
    private static final java.util.concurrent.ExecutorService authenticatorPool = java.util.concurrent.Executors.newCachedThreadPool(
            r -> { // Yatopia start - make sure produced threads are daemon ones
                Thread thread = new Thread(r, "User Authenticator #" + threadId.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            } // Yatopia end
    );
    // Paper end

    private static final AtomicInteger b = new AtomicInteger(0);
    private static final Logger c = LogManager.getLogger();
    private static final Random random = new Random();
    private final byte[] e = new byte[4];
    private final MinecraftServer server;
    public final NetworkManager networkManager;
    private LoginListener.EnumProtocolState g;
    private int h;
    private GameProfile i;
    private final String j;
    private SecretKey loginKey;
    private EntityPlayer l;
    public String hostname = ""; // CraftBukkit - add field

    public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.g = LoginListener.EnumProtocolState.HELLO;
        this.j = "";
        this.server = minecraftserver;
        this.networkManager = networkmanager;
        LoginListener.random.nextBytes(this.e);
    }

    public void c() {
        if (this.g == LoginListener.EnumProtocolState.READY_TO_ACCEPT) {
            this.b();
        } else if (this.g == LoginListener.EnumProtocolState.e) {
            EntityPlayer entityplayer = this.server.getPlayerList().a(this.i.getId());

            if (entityplayer == null) {
                this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                this.server.getPlayerList().a(this.networkManager, this.l);
                this.l = null;
            }
        }

        if (this.h++ == 600) {
            this.disconnect("Took too long to log in");
        }

    }

    public void disconnect(String s) {
        if (s == null) {
            new AuthorNagException("Kick message was set to null, causing an exception!").printStackTrace();
            s = "Kicked by plugin";
        }

        try {
            LoginListener.c.info("Disconnecting " + this.d() + ": " + s);
            ChatComponentText chatcomponenttext = new ChatComponentText(s);

            this.networkManager.handle(new PacketLoginOutDisconnect(chatcomponenttext));
            this.networkManager.close(chatcomponenttext);
        } catch (Exception exception) {
            LoginListener.c.error("Error whilst disconnecting player", exception);
        }

    }

    // Spigot start
    public void initUUID() {
        UUID uuid;
        if (networkManager.spoofedUUID != null) {
            uuid = networkManager.spoofedUUID;
        } else {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.i.getName()).getBytes(Charsets.UTF_8));
        }
        this.i = new GameProfile(uuid, this.i.getName());
        if (networkManager.spoofedProfile != null) {
            for (com.mojang.authlib.properties.Property property : networkManager.spoofedProfile) {
                this.i.getProperties().put(property.getName(), property);
            }
        }
    }
    // Spigot end

    public void b() {
        // Spigot start - Moved to initUUID
        /*
        if (!this.i.isComplete()) {
            this.i = this.a(this.i);
        }
        */
        // Spigot end

        // CraftBukkit start - fire PlayerLoginEvent
        EntityPlayer s = this.server.getPlayerList().attemptLogin(this, this.i, hostname);

        if (s == null) {
            // this.disconnect(s);
            // CraftBukkit end
        } else {
            this.g = LoginListener.EnumProtocolState.ACCEPTED;
            if (this.server.aK() >= 0 && !this.networkManager.c()) {
                this.networkManager.a(new PacketLoginOutSetCompression(this.server.aK()), new ChannelFutureListener() {
                    public void a(ChannelFuture channelfuture) {
                        LoginListener.this.networkManager.setupCompression(LoginListener.this.server.aK()); // Nacho - deobfuscate setupCompression
                    }

                    public void operationComplete(ChannelFuture future) { // CraftBukkit - fix decompile error
                        this.a(future);
                    }
                });
            }

            this.networkManager.handle(new PacketLoginOutSuccess(this.i));
            EntityPlayer entityplayer = this.server.getPlayerList().a(this.i.getId());

            if (entityplayer != null) {
                this.g = LoginListener.EnumProtocolState.e;
                this.l = this.server.getPlayerList().processLogin(this.i, s); // CraftBukkit - add player reference
            } else {
                this.server.getPlayerList().a(this.networkManager, this.server.getPlayerList().processLogin(this.i, s)); // CraftBukkit - add player reference
            }
        }

    }

    public void a(IChatBaseComponent ichatbasecomponent) {
        LoginListener.c.info(this.d() + " lost connection: " + ichatbasecomponent.c());
    }

    public String d() {
        String socketAddress = networkManager == null ? "null" : (networkManager.getSocketAddress() == null ? "null" : networkManager.getSocketAddress().toString());
        return this.i != null ? this.i.toString() + " (" + socketAddress + ")" : socketAddress;
    }

    public void a(PacketLoginInStart packetlogininstart) {
        Validate.validState(this.g == LoginListener.EnumProtocolState.HELLO, "Unexpected hello packet");
        this.i = packetlogininstart.a();
        if (this.server.getOnlineMode() && !this.networkManager.c()) {
            this.g = LoginListener.EnumProtocolState.KEY;
            this.networkManager.handle(new PacketLoginOutEncryptionBegin(this.j, this.server.Q().getPublic(), this.e));
        } else {
            // Spigot start
            // Paper start - Cache authenticator threads
            authenticatorPool.execute(() -> {
                try {
                    initUUID();
                    new LoginHandler().fireEvents();
                } catch (Exception ex) {
                    disconnect("Failed to verify username!");
                    server.server.getLogger().log(Level.WARNING, "Exception verifying " + i.getName(), ex);
                }
            });
            // Paper end
            // Spigot end
        }

    }

    public void a(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
        Validate.validState(this.g == LoginListener.EnumProtocolState.KEY, "Unexpected key packet");
        PrivateKey privatekey = this.server.Q().getPrivate();

        if (!Arrays.equals(this.e, packetlogininencryptionbegin.b(privatekey))) {
            throw new IllegalStateException("Invalid nonce!");
        } else {
            try {
                this.loginKey = packetlogininencryptionbegin.a(privatekey);
                this.g = LoginListener.EnumProtocolState.AUTHENTICATING;
                this.networkManager.setupEncryption(this.loginKey);
            } catch (Exception ex) {
                throw new IllegalStateException("Protocol error", ex);
            }
            // Paper start - Cache authenticator threads
            authenticatorPool.execute(() -> {
                GameProfile gameprofile = LoginListener.this.i;
                try {
                    String s = (new BigInteger(MinecraftEncryption.a(LoginListener.this.j, LoginListener.this.server.Q().getPublic(), LoginListener.this.loginKey))).toString(16);
                    LoginListener.this.i = LoginListener.this.server.aD().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s);
                    if (LoginListener.this.i != null) {
                        // CraftBukkit start - fire PlayerPreLoginEvent
                        if (!networkManager.g()) return;
                        new LoginHandler().fireEvents();
                    } else if (LoginListener.this.server.T()) {
                        LoginListener.c.warn("Failed to verify username but will let them in anyway!");
                        LoginListener.this.i = LoginListener.this.a(gameprofile);
                        LoginListener.this.g = EnumProtocolState.READY_TO_ACCEPT;
                    } else {
                        LoginListener.this.disconnect("Failed to verify username!");
                        LoginListener.c.error("Username '" + gameprofile.getName() + "' tried to join with an invalid session"); // CraftBukkit - fix null pointer
                    }
                } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                    if (LoginListener.this.server.T()) {
                        LoginListener.c.warn("Authentication servers are down but will let them in anyway!");
                        LoginListener.this.i = LoginListener.this.a(gameprofile);
                        LoginListener.this.g = EnumProtocolState.READY_TO_ACCEPT;
                    } else {
                        LoginListener.this.disconnect("Authentication servers are down. Please try again later, sorry!");
                        LoginListener.c.error("Couldn't verify username because servers are unavailable");
                    }
                    // CraftBukkit start - catch all exceptions
                } catch (Exception exception) {
                    disconnect("Failed to verify username!");
                    server.server.getLogger().log(Level.WARNING, "Exception verifying " + gameprofile.getName(), exception);
                    // CraftBukkit end
                }

            });
        }
    }

    // Spigot start
    public class LoginHandler {

        public void fireEvents() throws Exception {
            String playerName = i.getName();
            java.net.InetAddress address = ((java.net.InetSocketAddress) networkManager.getSocketAddress()).getAddress();
            java.util.UUID uniqueId = i.getId();
            final org.bukkit.craftbukkit.CraftServer server = LoginListener.this.server.server;

            AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
            server.getPluginManager().callEvent(asyncEvent);

            if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
                final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
                if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
                    event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
                }
                Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
                    @Override
                    protected PlayerPreLoginEvent.Result evaluate() {
                        server.getPluginManager().callEvent(event);
                        return event.getResult();
                    }
                };

                LoginListener.this.server.processQueue.add(waitable);
                if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                    disconnect(event.getKickMessage());
                    return;
                }
            } else {
                if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                    disconnect(asyncEvent.getKickMessage());
                    return;
                }
            }
            // CraftBukkit end
            LoginListener.c.info("UUID of player " + LoginListener.this.i.getName() + " is " + LoginListener.this.i.getId());
            LoginListener.this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
        }
    }
    // Spigot end

    protected GameProfile a(GameProfile gameprofile) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameprofile.getName()).getBytes(Charsets.UTF_8));

        return new GameProfile(uuid, gameprofile.getName());
    }

    static enum EnumProtocolState {

        HELLO, KEY, AUTHENTICATING, READY_TO_ACCEPT, e, ACCEPTED;

        private EnumProtocolState() {
        }
    }
}
