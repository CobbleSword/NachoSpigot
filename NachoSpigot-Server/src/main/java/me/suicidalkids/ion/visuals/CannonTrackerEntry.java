package me.suicidalkids.ion.visuals;

import net.minecraft.server.*;

import java.util.List;

/*
 * This is a custom entity tracker made for the cannoning entities tnt and sand.
 * The goal behind this is to reduce packets and logic without hiding entities.
 * It may not completely replicate the original behavior, but it should make up
 * for that with its advantages.
 */
public class CannonTrackerEntry extends EntityTrackerEntry {

    private boolean movingX;
    private boolean movingY;
    private boolean movingZ;

    private double updateX;
    private double updateY;
    private double updateZ;

    public CannonTrackerEntry(Entity entity, int i, int j, boolean flag) {
        super(entity, i, j, flag);
        this.movingX = entity.motX != 0.0;
        this.movingY = true;
        this.movingZ = entity.motZ != 0.0;
        this.updateX = entity.locX;
        this.updateY = entity.locY;
        this.updateZ = entity.locZ;
    }

    @Override
    public void track(List<EntityHuman> list) {
        boolean motionX = this.tracker.motX != 0.0;
        boolean motionY = this.tracker.motY != 0.0;
        boolean motionZ = this.tracker.motZ != 0.0;

        // This tracked entities motion has changed or an explosion has occurred, update it!
        if (!this.tracker.ai && motionX == movingX && motionY == movingY && motionZ == movingZ) {
            return;
        }

        // This entity has moved 4 blocks since the last update, search for players
        if (this.tracker.e(updateX, updateY, updateZ) > 16.0D) {
            this.scanPlayers(list);
            this.updateX = this.tracker.locX;
            this.updateY = this.tracker.locY;
            this.updateZ = this.tracker.locZ;
        }

        // Update nearby players, only resynchronise when motion is updated
        if (motionX || motionY || motionZ) {
            this.broadcastUpdate();
        }

        // Keep what of which axis the entity is moving on
        this.tracker.ai = false;
        this.movingX = motionX;
        this.movingY = motionY;
        this.movingZ = motionZ;
    }

    private void broadcastUpdate() {
        DataWatcher datawatcher = this.tracker.getDataWatcher();

        if (datawatcher.a()) {
            this.broadcastIncludingSelf(new PacketPlayOutEntityMetadata(this.tracker.getId(), datawatcher, false));
        }

        // Only update location on movement
        if (this.tracker.lastX != this.tracker.locX || this.tracker.lastY != this.tracker.locY || this.tracker.lastZ != this.tracker.locZ) {
            this.broadcast(new PacketPlayOutEntityTeleport(this.tracker));
        }

        this.broadcast(new PacketPlayOutEntityVelocity(this.tracker));
    }

    @Override
    public void updatePlayer(EntityPlayer entityplayer) {
        // Check configurable distance as a cube then visible distance.
        if (this.c(entityplayer) && this.tracker.h(entityplayer) < 4096.0D) {
            if (this.trackedPlayers.contains(entityplayer) || (!this.e(entityplayer) && !this.tracker.attachedToPlayer)) {
                return;
            }

            entityplayer.removeQueue.remove(Integer.valueOf(this.tracker.getId()));

            this.trackedPlayerMap.put(entityplayer, true); // Paper
            Packet<?> packet = this.c(); // IonSpigot
            if (packet == null) return; // IonSpigot - If it's null don't update the client!

            entityplayer.playerConnection.sendPacket(packet);

            if (this.tracker.getCustomNameVisible()) {
                entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(this.tracker.getId(), this.tracker.getDataWatcher(), true));
            }

            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.tracker.motX, this.tracker.motY, this.tracker.motZ));

            if (this.tracker.vehicle != null) {
                entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle));
            }
        } else if (this.trackedPlayers.contains(entityplayer)) {
            this.trackedPlayers.remove(entityplayer);
            entityplayer.d(this.tracker);
        }
    }

}