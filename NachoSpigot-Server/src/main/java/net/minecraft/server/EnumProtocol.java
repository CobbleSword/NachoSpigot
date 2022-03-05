package net.minecraft.server;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Supplier;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.elier.nachospigot.config.NachoConfig;

public enum EnumProtocol {

    HANDSHAKING(-1) {
        {
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketHandshakingInSetProtocol.class, PacketHandshakingInSetProtocol::new);
        }
    }, PLAY(0) {
        {
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutKeepAlive.class, PacketPlayOutKeepAlive::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutLogin.class, PacketPlayOutLogin::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutChat.class, PacketPlayOutChat::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateTime.class, PacketPlayOutUpdateTime::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityEquipment.class, PacketPlayOutEntityEquipment::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnPosition.class, PacketPlayOutSpawnPosition::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateHealth.class, PacketPlayOutUpdateHealth::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutRespawn.class, PacketPlayOutRespawn::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPosition.class, PacketPlayOutPosition::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutHeldItemSlot.class, PacketPlayOutHeldItemSlot::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBed.class, PacketPlayOutBed::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAnimation.class, PacketPlayOutAnimation::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutNamedEntitySpawn.class, PacketPlayOutNamedEntitySpawn::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCollect.class, PacketPlayOutCollect::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntity.class, PacketPlayOutSpawnEntity::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityLiving.class, PacketPlayOutSpawnEntityLiving::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityPainting.class, PacketPlayOutSpawnEntityPainting::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityExperienceOrb.class, PacketPlayOutSpawnEntityExperienceOrb::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityVelocity.class, PacketPlayOutEntityVelocity::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityDestroy.class, PacketPlayOutEntityDestroy::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.class, PacketPlayOutEntity::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutRelEntityMove.class, PacketPlayOutEntity.PacketPlayOutRelEntityMove::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutEntityLook.class, PacketPlayOutEntity.PacketPlayOutEntityLook::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook.class, PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityTeleport.class, PacketPlayOutEntityTeleport::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityHeadRotation.class, PacketPlayOutEntityHeadRotation::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityStatus.class, PacketPlayOutEntityStatus::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAttachEntity.class, PacketPlayOutAttachEntity::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityMetadata.class, PacketPlayOutEntityMetadata::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityEffect.class, PacketPlayOutEntityEffect::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutRemoveEntityEffect.class, PacketPlayOutRemoveEntityEffect::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutExperience.class, PacketPlayOutExperience::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateAttributes.class, PacketPlayOutUpdateAttributes::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMapChunk.class, PacketPlayOutMapChunk::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMultiBlockChange.class, PacketPlayOutMultiBlockChange::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockChange.class, PacketPlayOutBlockChange::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockAction.class, PacketPlayOutBlockAction::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockBreakAnimation.class, PacketPlayOutBlockBreakAnimation::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMapChunkBulk.class, PacketPlayOutMapChunkBulk::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutExplosion.class, PacketPlayOutExplosion::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldEvent.class, PacketPlayOutWorldEvent::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutNamedSoundEffect.class, PacketPlayOutNamedSoundEffect::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldParticles.class, PacketPlayOutWorldParticles::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutGameStateChange.class, PacketPlayOutGameStateChange::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityWeather.class, PacketPlayOutSpawnEntityWeather::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutOpenWindow.class, PacketPlayOutOpenWindow::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCloseWindow.class, PacketPlayOutCloseWindow::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSetSlot.class, PacketPlayOutSetSlot::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWindowItems.class, PacketPlayOutWindowItems::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWindowData.class, PacketPlayOutWindowData::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTransaction.class, PacketPlayOutTransaction::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateSign.class, PacketPlayOutUpdateSign::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMap.class, PacketPlayOutMap::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTileEntityData.class, PacketPlayOutTileEntityData::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutOpenSignEditor.class, PacketPlayOutOpenSignEditor::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutStatistic.class, PacketPlayOutStatistic::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPlayerInfo.class, PacketPlayOutPlayerInfo::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAbilities.class, PacketPlayOutAbilities::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTabComplete.class, PacketPlayOutTabComplete::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardObjective.class, PacketPlayOutScoreboardObjective::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardScore.class, PacketPlayOutScoreboardScore::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardDisplayObjective.class, PacketPlayOutScoreboardDisplayObjective::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardTeam.class, PacketPlayOutScoreboardTeam::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCustomPayload.class, PacketPlayOutCustomPayload::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutKickDisconnect.class, PacketPlayOutKickDisconnect::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutServerDifficulty.class, PacketPlayOutServerDifficulty::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCombatEvent.class, PacketPlayOutCombatEvent::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCamera.class, PacketPlayOutCamera::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldBorder.class, PacketPlayOutWorldBorder::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTitle.class, PacketPlayOutTitle::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSetCompression.class, PacketPlayOutSetCompression::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPlayerListHeaderFooter.class, PacketPlayOutPlayerListHeaderFooter::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutResourcePackSend.class, PacketPlayOutResourcePackSend::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateEntityNBT.class, PacketPlayOutUpdateEntityNBT::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInKeepAlive.class, PacketPlayInKeepAlive::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInChat.class, PacketPlayInChat::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInUseEntity.class, PacketPlayInUseEntity::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.class, PacketPlayInFlying::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInPosition.class, PacketPlayInFlying.PacketPlayInPosition::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInLook.class, PacketPlayInFlying.PacketPlayInLook::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInPositionLook.class, PacketPlayInFlying.PacketPlayInPositionLook::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInBlockDig.class, PacketPlayInBlockDig::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInBlockPlace.class, PacketPlayInBlockPlace::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInHeldItemSlot.class, PacketPlayInHeldItemSlot::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInArmAnimation.class, PacketPlayInArmAnimation::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInEntityAction.class, PacketPlayInEntityAction::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInSteerVehicle.class, PacketPlayInSteerVehicle::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInCloseWindow.class, PacketPlayInCloseWindow::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInWindowClick.class, PacketPlayInWindowClick::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInTransaction.class, PacketPlayInTransaction::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInSetCreativeSlot.class, PacketPlayInSetCreativeSlot::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInEnchantItem.class, PacketPlayInEnchantItem::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInUpdateSign.class, PacketPlayInUpdateSign::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInAbilities.class, PacketPlayInAbilities::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInTabComplete.class, PacketPlayInTabComplete::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInSettings.class, PacketPlayInSettings::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInClientCommand.class, PacketPlayInClientCommand::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInCustomPayload.class, PacketPlayInCustomPayload::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInSpectate.class, PacketPlayInSpectate::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketPlayInResourcePackStatus.class, PacketPlayInResourcePackStatus::new);
        }
    }, STATUS(1) {
        {
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketStatusInStart.class, PacketStatusInStart::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketStatusOutServerInfo.class, PacketStatusOutServerInfo::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketStatusInPing.class, PacketStatusInPing::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketStatusOutPong.class, PacketStatusOutPong::new);
        }
    }, LOGIN(2) {
        {
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutDisconnect.class, PacketLoginOutDisconnect::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutEncryptionBegin.class, PacketLoginOutEncryptionBegin::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutSuccess.class, PacketLoginOutSuccess::new);
            this.registerPacket(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutSetCompression.class, PacketLoginOutSetCompression::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketLoginInStart.class, PacketLoginInStart::new);
            this.registerPacket(EnumProtocolDirection.SERVERBOUND, PacketLoginInEncryptionBegin.class, PacketLoginInEncryptionBegin::new);
        }
    };

    private static final int handshakeId = -1;
    private static final int loginId = 2;

    private static final EnumProtocol[] STATES = new EnumProtocol[loginId - handshakeId + 1]; // 4

    private final Map<EnumProtocolDirection, BiMap<Integer, Class<? extends Packet<?>>>> _protocolPacketShim = Maps.newEnumMap(EnumProtocolDirection.class);

    private static final Map<Class<? extends Packet<?>>, EnumProtocol> packetClass2State = Maps.newHashMap();

    private final Object2IntMap<Class<? extends Packet<?>>> packetClassToId = new Object2IntOpenHashMap<>(16, 0.5f);
    private final Map<EnumProtocolDirection, IntObjectMap<Supplier<Packet<?>>>> packetMap = Maps.newEnumMap(EnumProtocolDirection.class);

    private final int stateId;

    EnumProtocol(int stateId) {
        this.stateId = stateId;
    }

    public int getStateId() {
        return this.stateId;
    }

    protected void registerPacket(EnumProtocolDirection dir, Class<? extends Packet<?>> clazz, Supplier<Packet<?>> packet) {
        IntObjectMap<Supplier<Packet<?>>> map = this.packetMap.computeIfAbsent(dir, k -> new IntObjectHashMap<>(16, 0.5f));
        int packetId = map.size(); // think of this as an incrementing integer
        this.packetClassToId.put(clazz, packetId);
        map.put(packetId, packet);

        if (NachoConfig.enableProtocolShim) {
            this._protocolRegisterShim(dir, clazz);
        }
    }

    private void _protocolRegisterShim(EnumProtocolDirection dir, Class<? extends Packet<?>> clazz) {
        BiMap<Integer, Class<? extends Packet<?>>> map = this._protocolPacketShim.computeIfAbsent(dir, k -> HashBiMap.create());
        map.put(map.size(), clazz);
    }

    public Packet<?> createPacket(EnumProtocolDirection direction, int packetId) {
        Supplier<Packet<?>> packet = this.packetMap.get(direction).get(packetId);
        return packet == null ? null : packet.get();
    }

    public Integer getPacketIdForPacket(Packet<?> packet) {
        return this.packetClassToId.getInt(packet.getClass());
    }

    public static EnumProtocol getProtocolForPacket(Packet<?> packet) {
        return packetClass2State.get(packet.getClass());
    }

    /**
     * @param state the intention from the packet
     * @return the packets for the intention if valid, else null
     */
    public static EnumProtocol isValidIntention(int state) {
        return state >= handshakeId && state <= loginId ? STATES[state - handshakeId] : null;
    }

    static {
        for (EnumProtocol state : values()) {
            int id = state.getStateId();
            if (id < handshakeId || id > loginId) {
                throw new Error("Invalid protocol ID " + id);
            }
            STATES[id - handshakeId] = state;
            for (Class<? extends Packet<?>> packetClass : state.packetClassToId.keySet()) {
                packetClass2State.put(packetClass, state);
            }
        }
    }
    
    // Method for plugins to use
    public Integer getPacketIdForPacket(EnumProtocolDirection dir, Packet<?> packet) {
        return this.getPacketIdForPacket(packet);
    }
}
