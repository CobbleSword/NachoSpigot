package net.minecraft.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.WorldSettings.EnumGamemode;

public class PacketPlayOutPlayerInfo implements Packet<PacketListenerPlayOut> {
    private PacketPlayOutPlayerInfo.EnumPlayerInfoAction a;
    private final List<PacketPlayOutPlayerInfo.PlayerInfoData> b = Lists.newArrayList();

    public PacketPlayOutPlayerInfo() {
    }

    public PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction var1, EntityPlayer... var2) {
        this.a = var1;
        EntityPlayer[] var3 = var2;
        int var4 = var2.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            EntityPlayer var6 = var3[var5];
            this.b.add(new PacketPlayOutPlayerInfo.PlayerInfoData(var6.getProfile(), var6.ping, var6.playerInteractManager.getGameMode(), var6.getPlayerListName()));
        }

    }

    public PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction var1, Iterable<EntityPlayer> var2) {
        this.a = var1;
        Iterator var3 = var2.iterator();

        while(var3.hasNext()) {
            EntityPlayer var4 = (EntityPlayer)var3.next();
            this.b.add(new PacketPlayOutPlayerInfo.PlayerInfoData(var4.getProfile(), var4.ping, var4.playerInteractManager.getGameMode(), var4.getPlayerListName()));
        }

    }

    public PacketPlayOutPlayerInfo.EnumPlayerInfoAction getPlayerInfoAction()
    {
        return this.a;
    }

    public void setPlayerInfoAction(PacketPlayOutPlayerInfo.EnumPlayerInfoAction action)
    {
        this.a = action;
    }

    public List<PlayerInfoData> getPlayersInfo()
    {
        return b;
    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction)var1.a(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class);
        int var2 = var1.readVarInt();

        for(int var3 = 0; var3 < var2; ++var3) {
            GameProfile var4 = null;
            int var5 = 0;
            EnumGamemode var6 = null;
            IChatBaseComponent var7 = null;
            switch(this.a) {
            case ADD_PLAYER:
                var4 = new GameProfile(var1.readUUID(), var1.readUtf(16)); // Nacho - deobfuscate readUtf
                int var8 = var1.readVarInt();
                int var9 = 0;

                for(; var9 < var8; ++var9) {
                    String var10 = var1.readUtf(32767); // Nacho - deobfuscate readUtf
                    String var11 = var1.readUtf(32767); // Nacho - deobfuscate readUtf
                    if (var1.readBoolean()) {
                        var4.getProperties().put(var10, new Property(var10, var11, var1.readUtf(32767))); // Nacho - deobfuscate readUtf
                    } else {
                        var4.getProperties().put(var10, new Property(var10, var11));
                    }
                }

                var6 = EnumGamemode.getById(var1.readVarInt());
                var5 = var1.readVarInt();
                if (var1.readBoolean()) {
                    var7 = var1.d();
                }
                break;
            case UPDATE_GAME_MODE:
                var4 = new GameProfile(var1.readUUID(), (String)null); // Nacho - deobfuscate readUUID
                var6 = EnumGamemode.getById(var1.readVarInt());
                break;
            case UPDATE_LATENCY:
                var4 = new GameProfile(var1.readUUID(), (String)null); // Nacho - deobfuscate readUUID
                var5 = var1.readVarInt();
                break;
            case UPDATE_DISPLAY_NAME:
                var4 = new GameProfile(var1.readUUID(), (String)null); // Nacho - deobfuscate readUUID
                if (var1.readBoolean()) {
                    var7 = var1.d();
                }
                break;
            case REMOVE_PLAYER:
                var4 = new GameProfile(var1.readUUID(), (String)null); // Nacho - deobfuscate readUUID
            }

            this.b.add(new PacketPlayOutPlayerInfo.PlayerInfoData(var4, var5, var6, var7));
        }

    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.a(this.a);
        var1.writeVarInt(this.b.size()); // Nacho - deobfuscate writeVarInt
        Iterator var2 = this.b.iterator();

        while(true) {
            while(var2.hasNext()) {
                PacketPlayOutPlayerInfo.PlayerInfoData var3 = (PacketPlayOutPlayerInfo.PlayerInfoData)var2.next();
                switch(this.a) {
                case ADD_PLAYER:
                    var1.a(var3.a().getId());
                    var1.a(var3.a().getName());
                    var1.writeVarInt(var3.a().getProperties().size()); // Nacho - deobfuscate writeVarInt
                    Iterator var4 = var3.a().getProperties().values().iterator();

                    while(var4.hasNext()) {
                        Property var5 = (Property)var4.next();
                        var1.a(var5.getName());
                        var1.a(var5.getValue());
                        if (var5.hasSignature()) {
                            var1.writeBoolean(true);
                            var1.a(var5.getSignature());
                        } else {
                            var1.writeBoolean(false);
                        }
                    }

                    var1.writeVarInt(var3.c().getId()); // Nacho - deobfuscate writeVarInt
                    var1.writeVarInt(var3.b()); // Nacho - deobfuscate writeVarInt
                    if (var3.d() == null) {
                        var1.writeBoolean(false);
                    } else {
                        var1.writeBoolean(true);
                        var1.a(var3.d());
                    }
                    break;
                case UPDATE_GAME_MODE:
                    var1.a(var3.a().getId());
                    var1.writeVarInt(var3.c().getId()); // Nacho - deobfuscate writeVarInt
                    break;
                case UPDATE_LATENCY:
                    var1.a(var3.a().getId());
                    var1.writeVarInt(var3.b()); // Nacho - deobfuscate writeVarInt
                    break;
                case UPDATE_DISPLAY_NAME:
                    var1.a(var3.a().getId());
                    if (var3.d() == null) {
                        var1.writeBoolean(false);
                    } else {
                        var1.writeBoolean(true);
                        var1.a(var3.d());
                    }
                    break;
                case REMOVE_PLAYER:
                    var1.a(var3.a().getId());
                }
            }

            return;
        }
    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", this.a).add("entries", this.b).toString();
    }

    public class PlayerInfoData {
        private final int b;
        private final EnumGamemode c;
        private final GameProfile d;
        private final IChatBaseComponent e;

        public PlayerInfoData(GameProfile var2, int var3, EnumGamemode var4, IChatBaseComponent var5) {
            this.d = var2;
            this.b = var3;
            this.c = var4;
            this.e = var5;
        }

        public GameProfile a() {
            return this.d;
        }

        public int b() {
            return this.b;
        }

        public EnumGamemode c() {
            return this.c;
        }

        public IChatBaseComponent d() {
            return this.e;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.b).add("gameMode", this.c).add("profile", this.d).add("displayName", this.e == null ? null : ChatSerializer.a(this.e)).toString();
        }
    }

    public static enum EnumPlayerInfoAction {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;

        private EnumPlayerInfoAction() {
        }
    }
}
