package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {

    private static final Logger a = LogManager.getLogger();
    private final WorldServer world;
    private Set<EntityTrackerEntry> trackedEntities = Sets.newHashSet();
    public IntHashMap<EntityTrackerEntry> trackedEntitiesID = new IntHashMap();
    private int entityViewDistance;

    public EntityTracker(WorldServer worldserver) {
        this.world = worldserver;
        this.entityViewDistance = worldserver.getMinecraftServer().getPlayerList().d();
    }

    public void track(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            this.addEntity(entity, 512, 2);
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator<EntityTrackerEntry> iterator = this.trackedEntities.iterator();

            while (iterator.hasNext())
            {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                if (entitytrackerentry.tracker != entityplayer) {
                    entitytrackerentry.updatePlayer(entityplayer);
                }
            }
        } else if (entity instanceof EntityFishingHook) {
            this.addEntity(entity, 64, 5, true);
        } else if (entity instanceof EntityArrow) {
            this.addEntity(entity, 64, 20, false);
        } else if (entity instanceof EntitySmallFireball) {
            this.addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntityFireball) {
            this.addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntitySnowball) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderPearl) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderSignal) {
            this.addEntity(entity, 64, 4, true);
        } else if (entity instanceof EntityEgg) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityPotion) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityThrownExpBottle) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityFireworks) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityItem) {
            this.addEntity(entity, 64, 20, true);
        } else if (entity instanceof EntityMinecartAbstract) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityBoat) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntitySquid) {
            this.addEntity(entity, 64, 3, true);
        } else if (entity instanceof EntityWither) {
            this.addEntity(entity, 80, 3, false);
        } else if (entity instanceof EntityBat) {
            this.addEntity(entity, 80, 3, false);
        } else if (entity instanceof EntityEnderDragon) {
            this.addEntity(entity, 160, 3, true);
        } else if (entity instanceof IAnimal) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityTNTPrimed) {
            this.addEntity(entity, 160, 10, true);
        } else if (entity instanceof EntityFallingBlock) {
            this.addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityHanging) {
            this.addEntity(entity, 160, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityArmorStand) {
            this.addEntity(entity, 160, 3, true);
        } else if (entity instanceof EntityExperienceOrb) {
            this.addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityEnderCrystal) {
            this.addEntity(entity, 256, Integer.MAX_VALUE, false);
        }

    }

    public void addEntity(Entity entity, int i, int j) {
        this.addEntity(entity, i, j, false);
    }

    public void addEntity(Entity entity, int i, final int j, boolean flag)
    {
        org.spigotmc.AsyncCatcher.catchOp( "entity track"); // Spigot
        i = org.spigotmc.TrackingRange.getEntityTrackingRange(entity, i); // Spigot
        if (i > this.entityViewDistance) {
            i = this.entityViewDistance;
        }

        try {
            if (this.trackedEntitiesID.b(entity.getId())) {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, j, flag);

            this.trackedEntities.add(entitytrackerentry);
            this.trackedEntitiesID.a(entity.getId(), entitytrackerentry);
            entitytrackerentry.scanPlayers(this.world.players);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Adding entity to track");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity To Track");

            crashreportsystemdetails.a("Tracking range", (Object) (i + " blocks"));
            final int finalI = i; // CraftBukkit - fix decompile error
            crashreportsystemdetails.a("Update interval", new Callable() {
                public String a() throws Exception {
                    String s = "Once per " + finalI + " ticks"; // CraftBukkit

                    if (finalI == Integer.MAX_VALUE) { // CraftBukkit
                        s = "Maximum (" + s + ")";
                    }

                    return s;
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
            entity.appendEntityCrashDetails(crashreportsystemdetails);
            CrashReportSystemDetails crashreportsystemdetails1 = crashreport.a("Entity That Is Already Tracked");

            this.trackedEntitiesID.get(entity.getId()).tracker.appendEntityCrashDetails(crashreportsystemdetails1);

            try {
                throw new ReportedException(crashreport);
            } catch (ReportedException reportedexception) {
                EntityTracker.a.error("\"Silently\" catching entity tracking error.", reportedexception);
            }
        }

    }

    public void untrackEntity(Entity entity)
    {
        org.spigotmc.AsyncCatcher.catchOp( "entity untrack"); // Spigot
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator<EntityTrackerEntry> iterator = this.trackedEntities.iterator();

            while (iterator.hasNext())
            {
                EntityTrackerEntry entitytrackerentry = iterator.next();

                entitytrackerentry.a(entityplayer);
            }
        }

        EntityTrackerEntry entitytrackerentry1 = this.trackedEntitiesID.d(entity.getId());

        if (entitytrackerentry1 != null)
        {
            this.trackedEntities.remove(entitytrackerentry1);
            entitytrackerentry1.a();
        }

    }

    public void updatePlayers()
    {

        ArrayList<EntityPlayer> playersToUpdate = Lists.newArrayList();
        Iterator<EntityTrackerEntry> iterator = this.trackedEntities.iterator();

        while (iterator.hasNext())
        {
            EntityTrackerEntry entitytrackerentry = iterator.next();

            entitytrackerentry.track(this.world.players); // Updates the location of entities

            if (entitytrackerentry.playerEntitiesUpdated && entitytrackerentry.tracker instanceof EntityPlayer)
            {
                playersToUpdate.add((EntityPlayer) entitytrackerentry.tracker);
            }
        }

        for (int i = 0; i < playersToUpdate.size(); ++i)
        {
            EntityPlayer entityplayer = playersToUpdate.get(i);
            Iterator<EntityTrackerEntry> iterator1 = this.trackedEntities.iterator();// currently all entities in a world
            //TODO: We only need to update local entities ^

            while (iterator1.hasNext())
            {
                EntityTrackerEntry entitytrackerentry1 = iterator1.next();

                if (entitytrackerentry1.tracker != entityplayer)
                {
//                    System.out.println(" I have been called");
                    entitytrackerentry1.updatePlayer(entityplayer);
                }
            }
        }

    }

    //Fired once on login, anytime a potion is applied and on gamemode changes
    // and on option unloads
    public void a(EntityPlayer entityplayer)
    {
        Iterator<EntityTrackerEntry> iterator = this.trackedEntities.iterator();
        while (iterator.hasNext())
        {
            EntityTrackerEntry entitytrackerentry = iterator.next();
            if (entitytrackerentry.tracker == entityplayer)
            {
                entitytrackerentry.scanPlayers(this.world.players);
            }
            else
            {
                entitytrackerentry.updatePlayer(entityplayer);
            }
        }

    }

    // Fired every arm swing and item slot swap (hotbar slot change), Mount, sleep
    public void a(Entity entity, Packet packet)
    {
        EntityTrackerEntry entitytrackerentry = this.trackedEntitiesID.get(entity.getId());

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcast(packet);
        }

    }

    public void sendPacketToEntity(Entity entity, Packet packet)
    {
        EntityTrackerEntry entitytrackerentry = this.trackedEntitiesID.get(entity.getId());

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcastIncludingSelf(packet);
        }

    }

    public void untrackPlayer(EntityPlayer entityplayer)
    {
        Iterator<EntityTrackerEntry> iterator = this.trackedEntities.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.clear(entityplayer);
        }

    }

    public Iterator<EntityTrackerEntry> getEntityTrackerEntries()
    {
        return this.trackedEntities.iterator();
    }

    public void updatePlayerChunk(EntityPlayer entityplayer, Chunk chunk)
    {
        this.a(entityplayer, chunk);
    }

    //Nacho never really  called anymore, We inlined this in PlayerEntity
    public void a(EntityPlayer entityplayer, Chunk chunk)
    {
        Iterator<EntityTrackerEntry> iterator = this.trackedEntities.iterator();

        while (iterator.hasNext())
        {
            EntityTrackerEntry entitytrackerentry = iterator.next();

            if (entitytrackerentry.tracker != entityplayer && entitytrackerentry.tracker.ae == chunk.locX && entitytrackerentry.tracker.ag == chunk.locZ)
            {
                entitytrackerentry.updatePlayer(entityplayer);
            }
        }

    }
}
