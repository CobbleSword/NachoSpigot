package net.minecraft.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

public enum EnumProtocol {

    HANDSHAKING(-1) {
        {
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketHandshakingInSetProtocol.class);
        }
    }, PLAY(0) {
        {
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutKeepAlive.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutLogin.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutChat.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateTime.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityEquipment.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnPosition.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateHealth.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutRespawn.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPosition.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutHeldItemSlot.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBed.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAnimation.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutNamedEntitySpawn.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCollect.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntity.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityLiving.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityPainting.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityExperienceOrb.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityVelocity.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityDestroy.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutRelEntityMove.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutEntityLook.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityTeleport.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityHeadRotation.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityStatus.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAttachEntity.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityMetadata.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityEffect.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutRemoveEntityEffect.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutExperience.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateAttributes.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMapChunk.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMultiBlockChange.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockChange.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockAction.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockBreakAnimation.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMapChunkBulk.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutExplosion.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldEvent.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutNamedSoundEffect.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldParticles.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutGameStateChange.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityWeather.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutOpenWindow.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCloseWindow.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSetSlot.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWindowItems.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWindowData.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTransaction.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateSign.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMap.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTileEntityData.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutOpenSignEditor.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutStatistic.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPlayerInfo.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAbilities.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTabComplete.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardObjective.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardScore.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardDisplayObjective.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardTeam.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCustomPayload.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutKickDisconnect.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutServerDifficulty.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCombatEvent.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCamera.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldBorder.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTitle.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSetCompression.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPlayerListHeaderFooter.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutResourcePackSend.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateEntityNBT.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInKeepAlive.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInChat.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInUseEntity.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInPosition.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInLook.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInPositionLook.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInBlockDig.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInBlockPlace.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInHeldItemSlot.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInArmAnimation.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInEntityAction.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInSteerVehicle.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInCloseWindow.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInWindowClick.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInTransaction.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInSetCreativeSlot.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInEnchantItem.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInUpdateSign.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInAbilities.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInTabComplete.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInSettings.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInClientCommand.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInCustomPayload.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInSpectate.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInResourcePackStatus.class);
        }
    }, STATUS(1) {
        {
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketStatusInStart.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketStatusOutServerInfo.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketStatusInPing.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketStatusOutPong.class);
        }
    }, LOGIN(2) {
        {
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutDisconnect.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutEncryptionBegin.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutSuccess.class);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutSetCompression.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketLoginInStart.class);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketLoginInEncryptionBegin.class);
        }
    };

    private static final int handshakeId = -1;
    private static final int loginId = 2;

    private static final EnumProtocol[] g = new EnumProtocol[loginId - handshakeId + 1]; // 4
    private static final Map<Class<? extends Packet<?>>, EnumProtocol> protocolMap = Maps.newHashMap();
    private final int protocolId;
    private final Map<EnumProtocolDirection, BiMap<Integer, Class<? extends Packet<?>>>> packetMap;

    EnumProtocol(int protocolId) {
        this.packetMap = Maps.newEnumMap(EnumProtocolDirection.class);
        this.protocolId = protocolId;
    }

    protected void registerPacket(EnumProtocolDirection dir, Class<? extends Packet<?>> packet) {
        BiMap<Integer, Class<? extends Packet<?>>> map = this.packetMap.computeIfAbsent(dir, k -> HashBiMap.create());

        if (map.containsValue(packet)) {
            String s = dir + " packet " + packet + " is already known to ID " + map.inverse().get(packet);
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else {
            map.put(map.size(), packet);
        }
    }

    public Integer a(EnumProtocolDirection direction, Packet<?> packet) {
        return this.packetMap.get(direction).inverse().get(packet.getClass());
    }

    public Packet<?> a(EnumProtocolDirection direction, int i) throws IllegalAccessException, InstantiationException {
        Class<?> packetClass = (Class<?>) ((BiMap<?, ?>) this.packetMap.get(direction)).get(i);
        return packetClass == null ? null : (Packet<?>) packetClass.newInstance();
    }

    public int getProtocolId() {
        return this.protocolId;
    }

    public static EnumProtocol a(int i) {
        return i >= handshakeId && i <= loginId ? g[i - handshakeId] : null;
    }

    public static EnumProtocol getProtocolForPacket(Packet<?> packet) {
        return protocolMap.get(packet.getClass());
    }

    static {
        for (EnumProtocol protocol : values()) {
            int bound = protocol.getProtocolId();
            if (bound < handshakeId || bound > loginId) {
                throw new Error("Invalid protocol ID " + bound);
            }
            g[bound - handshakeId] = protocol;
            for (EnumProtocolDirection direction : protocol.packetMap.keySet()) {
                Class<? extends Packet<?>> packetClass;
                for (Iterator<Class<? extends Packet<?>>> packets = protocol.packetMap.get(direction).values().iterator(); packets.hasNext(); protocolMap.put(packetClass, protocol)) {
                    packetClass = packets.next();
                    if (protocolMap.containsKey(packetClass) && protocolMap.get(packetClass) != protocol) {
                        throw new Error("Packet " + packetClass + " is already assigned to protocol " + protocolMap.get(packetClass) + " - can't reassign to " + protocol);
                    }
                    try {
                        packetClass.newInstance();
                    } catch (Throwable throwable) {
                        throw new Error("Packet " + packetClass + " fails instantiation checks! " + packetClass);
                    }
                }
            }
        }
    }
}
