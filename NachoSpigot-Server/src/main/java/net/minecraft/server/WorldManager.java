package net.minecraft.server;

public class WorldManager implements IWorldAccess {

    private MinecraftServer a;
    private WorldServer world;

    public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
        this.a = minecraftserver;
        this.world = worldserver;
    }

    public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {}

    public void a(Entity entity) {
        this.world.getTracker().track(entity);
    }

    public void b(Entity entity) {
        this.world.getTracker().untrackEntity(entity);
        this.world.getScoreboard().a(entity);
    }

    public void a(String s, double d0, double d1, double d2, float f, float f1) {
        // CraftBukkit - this.world.dimension
        this.a.getPlayerList().sendPacketNearby(d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, this.world.dimension, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
    }

    public void a(EntityHuman entityhuman, String s, double d0, double d1, double d2, float f, float f1) {
        if (s.equals("random.drink") || s.contains("step") || s.contains("player") || s.equals("random.eat")) {
            this.a.getPlayerList().sendPacketNearby(entityhuman, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, this.world.dimension, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
        } else {
            this.a.getPlayerList().sendPacketNearbyIncludingSelf(entityhuman, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, this.world.dimension, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
        }
    }

    public void a(int i, int j, int k, int l, int i1, int j1) {}

    public void a(BlockPosition blockposition) {
        this.world.getPlayerChunkMap().flagDirty(blockposition);
    }

    public void b(BlockPosition blockposition) {}

    public void a(String s, BlockPosition blockposition) {}

    public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
        if (i == 2001) {
            this.a.getPlayerList().sendPacketNearby(entityhuman, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 64.0D, this.world.dimension, new PacketPlayOutWorldEvent(i, blockposition, j, false));
        } else {
            this.a.getPlayerList().sendPacketNearbyIncludingSelf(entityhuman, blockposition.getX(),
                    blockposition.getY(), blockposition.getZ(), 64.0D, this.world.dimension, new
                    PacketPlayOutWorldEvent(i, blockposition, j, false));
        }
    }

    @Override
    public void sendPlayWorldPacket(EntityHuman entityhuman, int i, int blockPosition_x, int blockPosition_y, int blockPosition_z, int j)
    {
        this.a.getPlayerList().sendPacketNearby(entityhuman, (double) blockPosition_x, (double) blockPosition_y, (double) blockPosition_z, 64.0D, this.world, new Int3PacketPlayOutWorldEvent(i, blockPosition_x, blockPosition_y, blockPosition_z, j, false));
    }

    public void a(int i, BlockPosition blockposition, int j) {
        this.a.getPlayerList().sendAll(new PacketPlayOutWorldEvent(i, blockposition, j, true));
    }

    public void b(int i, BlockPosition blockposition, int j) {
        // CraftBukkit start
        EntityHuman entityhuman = null;
        Entity entity = world.a(i); // PAIL Rename getEntity
        if (entity instanceof EntityHuman) entityhuman = (EntityHuman) entity;
        // CraftBukkit end

        java.util.List<? extends EntityHuman> list = entity != null ? entity.world.players : this.a.getPlayerList().v();
        PacketPlayOutBlockBreakAnimation packet = null; // SportPaper - cache packet

        for (EntityHuman human : list) {
            if (!(human instanceof EntityPlayer)) continue;
            EntityPlayer entityplayer = (EntityPlayer) human;

            if (entityplayer.world == this.world && entityplayer.getId() != i) {
                double d0 = (double) blockposition.getX() - entityplayer.locX;
                double d1 = (double) blockposition.getY() - entityplayer.locY;
                double d2 = (double) blockposition.getZ() - entityplayer.locZ;

                // CraftBukkit start
                if (entityhuman instanceof EntityPlayer && !entityplayer.getBukkitEntity().canSee(((EntityPlayer) entityhuman).getBukkitEntity())) {
                    continue;
                }
                // CraftBukkit end

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
                    // SportPaper start
                    if (packet == null) packet = new PacketPlayOutBlockBreakAnimation(i, blockposition, j);
                    entityplayer.playerConnection.sendPacket(packet);
                    // SportPaper end
                }
            }
        }

    }
}
