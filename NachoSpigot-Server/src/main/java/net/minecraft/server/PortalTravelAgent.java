package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.util.Vector;
// CraftBukkit end

public class PortalTravelAgent {

    private final WorldServer a;
    private final Random b;
    private final LongHashMap<PortalTravelAgent.ChunkCoordinatesPortal> c = new LongHashMap();
    private final List<Long> d = Lists.newArrayList();

    public PortalTravelAgent(WorldServer worldserver) {
        this.a = worldserver;
        this.b = new Random(worldserver.getSeed());
    }

    public void a(Entity entity, float f) {
        if (this.a.worldProvider.getDimension() != 1) {
            if (!this.b(entity, f)) {
                this.a(entity);
                this.b(entity, f);
            }
        } else {
            int i = MathHelper.floor(entity.locX);
            int j = MathHelper.floor(entity.locY) - 1;
            int k = MathHelper.floor(entity.locZ);
            // CraftBukkit start - Modularize end portal creation
            BlockPosition created = this.createEndPortal(entity.locX, entity.locY, entity.locZ);
            entity.setPositionRotation((double) created.getX(), (double) created.getY(), (double) created.getZ(), entity.yaw, 0.0F);
            entity.motX = entity.motY = entity.motZ = 0.0D;
        }
    }

    // Split out from original a(Entity, double, double, double, float) method in order to enable being called from createPortal
    private BlockPosition createEndPortal(double x, double y, double z) {
            int i = MathHelper.floor(x);
            int j = MathHelper.floor(y) - 1;
            int k = MathHelper.floor(z);
            // CraftBukkit end
            byte b0 = 1;
            byte b1 = 0;

            for (int l = -2; l <= 2; ++l) {
                for (int i1 = -2; i1 <= 2; ++i1) {
                    for (int j1 = -1; j1 < 3; ++j1) {
                        int k1 = i + i1 * b0 + l * b1;
                        int l1 = j + j1;
                        int i2 = k + i1 * b1 - l * b0;
                        boolean flag = j1 < 0;

                        this.a.setTypeUpdate(new BlockPosition(k1, l1, i2), flag ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData());
                    }
                }
            }

        // CraftBukkit start
        return new BlockPosition(i, k, k);
    }

    // use logic based on creation to verify end portal
    private BlockPosition findEndPortal(BlockPosition portal) {
        int i = portal.getX();
        int j = portal.getY() - 1;
        int k = portal.getZ();
        byte b0 = 1;
        byte b1 = 0;

        for (int l = -2; l <= 2; ++l) {
            for (int i1 = -2; i1 <= 2; ++i1) {
                for (int j1 = -1; j1 < 3; ++j1) {
                    int k1 = i + i1 * b0 + l * b1;
                    int l1 = j + j1;
                    int i2 = k + i1 * b1 - l * b0;
                    boolean flag = j1 < 0;

                    if (this.a.getType(new BlockPosition(k1, l1, i2)).getBlock() != (flag ? Blocks.OBSIDIAN : Blocks.AIR)) {
                        return null;
                    }
                }
            }
        }
        return new BlockPosition(i, j, k);
    }
    // CraftBukkit end

    public boolean b(Entity entity, float f) {
        // CraftBukkit start - Modularize portal search process and entity teleportation
        BlockPosition found = this.findPortal(entity.locX, entity.locY, entity.locZ, 128);
        if (found == null) {
            return false;
        }

        Location exit = new Location(this.a.getWorld(), found.getX(), found.getY(), found.getZ(), f, entity.pitch);
        Vector velocity = entity.getBukkitEntity().getVelocity();
        this.adjustExit(entity, exit, velocity);
        entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
        if (entity.motX != velocity.getX() || entity.motY != velocity.getY() || entity.motZ != velocity.getZ()) {
            entity.getBukkitEntity().setVelocity(velocity);
        }
        return true;
    }

    public BlockPosition findPortal(double x, double y, double z, int searchRadius) { // Paper - actually use search radius
        if (this.a.getWorld().getEnvironment() == org.bukkit.World.Environment.THE_END) {
            return this.findEndPortal(this.a.worldProvider.h());
        }
        // CraftBukkit end
        double d0 = -1.0D;
        // CraftBukkit start
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(z);
        // CraftBukkit end
        boolean flag1 = true;
        Object object = BlockPosition.ZERO;
        long k = ChunkCoordIntPair.a(i, j);

        if (this.c.contains(k)) {
            PortalTravelAgent.ChunkCoordinatesPortal portaltravelagent_chunkcoordinatesportal = (PortalTravelAgent.ChunkCoordinatesPortal) this.c.getEntry(k);

            d0 = 0.0D;
            object = portaltravelagent_chunkcoordinatesportal;
            portaltravelagent_chunkcoordinatesportal.c = this.a.getTime();
            flag1 = false;
        } else {
            BlockPosition blockposition = new BlockPosition(x, y, z);

            for (int l = -searchRadius; l <= searchRadius; ++l) {   // Paper - actually use search radius
                BlockPosition blockposition1;

                for (int i1 = -searchRadius; i1 <= searchRadius; ++i1) {    // Paper - actually use search radius
                    for (BlockPosition blockposition2 = blockposition.a(l, this.a.V() - 1 - blockposition.getY(), i1); blockposition2.getY() >= 0; blockposition2 = blockposition1) {
                        blockposition1 = blockposition2.down();
                        if (this.a.getType(blockposition2).getBlock() == Blocks.PORTAL) {
                            while (this.a.getType(blockposition1 = blockposition2.down()).getBlock() == Blocks.PORTAL) {
                                blockposition2 = blockposition1;
                            }

                            double d1 = blockposition2.i(blockposition);

                            if (d0 < 0.0D || d1 < d0) {
                                d0 = d1;
                                object = blockposition2;
                            }
                        }
                    }
                }
            }
        }

        if (d0 >= 0.0D) {
            if (flag1) {
                this.c.put(k, new PortalTravelAgent.ChunkCoordinatesPortal((BlockPosition) object, this.a.getTime()));
                this.d.add(Long.valueOf(k));
            }
            // CraftBukkit start - Move entity teleportation logic into exit
            return (BlockPosition) object;
        } else {
            return null;
        }
    }

    // Entity repositioning logic split out from original b method and combined with repositioning logic for The End from original a method
    public void adjustExit(Entity entity, Location position, Vector velocity) {
        Location from = position.clone();
        Vector before = velocity.clone();
        BlockPosition object = new BlockPosition(position.getBlockX(), position.getBlockY(), position.getBlockZ());
        float f = position.getYaw();

        if (this.a.getWorld().getEnvironment() == org.bukkit.World.Environment.THE_END || entity.getBukkitEntity().getWorld().getEnvironment() == org.bukkit.World.Environment.THE_END || entity.aG() == null) {
            // entity.setPositionRotation((double) i, (double) j, (double) k, entity.yaw, 0.0F);
            // entity.motX = entity.motY = entity.motZ = 0.0D;
            position.setPitch(0.0F);
            velocity.setX(0);
            velocity.setY(0);
            velocity.setZ(0);
        } else {
            // CraftBukkit end

            double d2 = (double) ((BlockPosition) object).getX() + 0.5D;
            double d3 = (double) ((BlockPosition) object).getY() + 0.5D;
            double d4 = (double) ((BlockPosition) object).getZ() + 0.5D;
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = Blocks.PORTAL.f(this.a, (BlockPosition) object);
            boolean flag2 = shapedetector_shapedetectorcollection.b().e().c() == EnumDirection.EnumAxisDirection.NEGATIVE;
            double d5 = shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X ? (double) shapedetector_shapedetectorcollection.a().getZ() : (double) shapedetector_shapedetectorcollection.a().getX();

            d3 = (double) (shapedetector_shapedetectorcollection.a().getY() + 1) - entity.aG().b * (double) shapedetector_shapedetectorcollection.e();
            if (flag2) {
                ++d5;
            }

            if (shapedetector_shapedetectorcollection.b().k() == EnumDirection.EnumAxis.X) {
                d4 = d5 + (1.0D - entity.aG().a) * (double) shapedetector_shapedetectorcollection.d() * (double) shapedetector_shapedetectorcollection.b().e().c().a();
            } else {
                d2 = d5 + (1.0D - entity.aG().a) * (double) shapedetector_shapedetectorcollection.d() * (double) shapedetector_shapedetectorcollection.b().e().c().a();
            }

            float f1 = 0.0F;
            float f2 = 0.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;

            if (shapedetector_shapedetectorcollection.b().opposite() == entity.aH()) {
                f1 = 1.0F;
                f2 = 1.0F;
            } else if (shapedetector_shapedetectorcollection.b().opposite() == entity.aH().opposite()) {
                f1 = -1.0F;
                f2 = -1.0F;
            } else if (shapedetector_shapedetectorcollection.b().opposite() == entity.aH().e()) {
                f3 = 1.0F;
                f4 = -1.0F;
            } else {
                f3 = -1.0F;
                f4 = 1.0F;
            }
            
            // CraftBukkit start
            double d6 = velocity.getX();
            double d7 = velocity.getZ();
            // CraftBukkit end

            // CraftBukkit start - Adjust position and velocity instances instead of entity
            velocity.setX(d6 * (double) f1 + d7 * (double) f4);
            velocity.setZ(d6 * (double) f3 + d7 * (double) f2);
            f = f - (float) (entity.aH().opposite().b() * 90) + (float) (shapedetector_shapedetectorcollection.b().b() * 90);
            // entity.setPositionRotation(d2, d3, d4, entity.yaw, entity.pitch);
            position.setX(d2);
            position.setY(d3);
            position.setZ(d4);
            position.setYaw(f);
        }
        EntityPortalExitEvent event = new EntityPortalExitEvent(entity.getBukkitEntity(), from, position, before, velocity);
        this.a.getServer().getPluginManager().callEvent(event);
        Location to = event.getTo();
        if (event.isCancelled() || to == null || !entity.isAlive()) {
            position.setX(from.getX());
            position.setY(from.getY());
            position.setZ(from.getZ());
            position.setYaw(from.getYaw());
            position.setPitch(from.getPitch());
            velocity.copy(before);
        } else {
            position.setX(to.getX());
            position.setY(to.getY());
            position.setZ(to.getZ());
            position.setYaw(to.getYaw());
            position.setPitch(to.getPitch());
            velocity.copy(event.getAfter()); // event.getAfter() will never be null, as setAfter() will cause an NPE if null is passed in
        }
        // CraftBukkit end
    }

    public boolean a(Entity entity) {
        // CraftBukkit start - Allow for portal creation to be based on coordinates instead of entity
        return this.createPortal(entity.locX, entity.locY, entity.locZ, 16);
    }

    public boolean createPortal(double x, double y, double z, int b0) {
        if (this.a.getWorld().getEnvironment() == org.bukkit.World.Environment.THE_END) {
            createEndPortal(x, y, z);
            return true;
        }
        // CraftBukkit end
        double d0 = -1.0D;
        // CraftBukkit start
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);
        // CraftBukkit end
        int l = i;
        int i1 = j;
        int j1 = k;
        int k1 = 0;
        int l1 = this.b.nextInt(4);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        int i2;
        double d1;
        int j2;
        double d2;
        int k2;
        int l2;
        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        double d3;
        double d4;

        for (i2 = i - b0; i2 <= i + b0; ++i2) {
            d1 = (double) i2 + 0.5D - x; // CraftBukkit

            for (j2 = k - b0; j2 <= k + b0; ++j2) {
                d2 = (double) j2 + 0.5D - z; // CraftBukkit

                label271:
                for (k2 = this.a.V() - 1; k2 >= 0; --k2) {
                    if (this.a.isEmpty(blockposition_mutableblockposition.c(i2, k2, j2))) {
                        while (k2 > 0 && this.a.isEmpty(blockposition_mutableblockposition.c(i2, k2 - 1, j2))) {
                            --k2;
                        }

                        for (l2 = l1; l2 < l1 + 4; ++l2) {
                            i3 = l2 % 2;
                            j3 = 1 - i3;
                            if (l2 % 4 >= 2) {
                                i3 = -i3;
                                j3 = -j3;
                            }

                            for (k3 = 0; k3 < 3; ++k3) {
                                for (l3 = 0; l3 < 4; ++l3) {
                                    for (i4 = -1; i4 < 4; ++i4) {
                                        j4 = i2 + (l3 - 1) * i3 + k3 * j3;
                                        k4 = k2 + i4;
                                        int l4 = j2 + (l3 - 1) * j3 - k3 * i3;

                                        blockposition_mutableblockposition.c(j4, k4, l4);
                                        if (i4 < 0 && !this.a.getType(blockposition_mutableblockposition).getBlock().getMaterial().isBuildable() || i4 >= 0 && !this.a.isEmpty(blockposition_mutableblockposition)) {
                                            continue label271;
                                        }
                                    }
                                }
                            }

                            d3 = (double) k2 + 0.5D - y; // CraftBukkit
                            d4 = d1 * d1 + d3 * d3 + d2 * d2;
                            if (d0 < 0.0D || d4 < d0) {
                                d0 = d4;
                                l = i2;
                                i1 = k2;
                                j1 = j2;
                                k1 = l2 % 4;
                            }
                        }
                    }
                }
            }
        }

        if (d0 < 0.0D) {
            for (i2 = i - b0; i2 <= i + b0; ++i2) {
                d1 = (double) i2 + 0.5D - x; // CraftBukkit

                for (j2 = k - b0; j2 <= k + b0; ++j2) {
                    d2 = (double) j2 + 0.5D - z; // CraftBukkit

                    label219:
                    for (k2 = this.a.V() - 1; k2 >= 0; --k2) {
                        if (this.a.isEmpty(blockposition_mutableblockposition.c(i2, k2, j2))) {
                            while (k2 > 0 && this.a.isEmpty(blockposition_mutableblockposition.c(i2, k2 - 1, j2))) {
                                --k2;
                            }

                            for (l2 = l1; l2 < l1 + 2; ++l2) {
                                i3 = l2 % 2;
                                j3 = 1 - i3;

                                for (k3 = 0; k3 < 4; ++k3) {
                                    for (l3 = -1; l3 < 4; ++l3) {
                                        i4 = i2 + (k3 - 1) * i3;
                                        j4 = k2 + l3;
                                        k4 = j2 + (k3 - 1) * j3;
                                        blockposition_mutableblockposition.c(i4, j4, k4);
                                        if (l3 < 0 && !this.a.getType(blockposition_mutableblockposition).getBlock().getMaterial().isBuildable() || l3 >= 0 && !this.a.isEmpty(blockposition_mutableblockposition)) {
                                            continue label219;
                                        }
                                    }
                                }

                                d3 = (double) k2 + 0.5D - y; // CraftBukkit
                                d4 = d1 * d1 + d3 * d3 + d2 * d2;
                                if (d0 < 0.0D || d4 < d0) {
                                    d0 = d4;
                                    l = i2;
                                    i1 = k2;
                                    j1 = j2;
                                    k1 = l2 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        int i5 = l;
        int j5 = i1;

        j2 = j1;
        int k5 = k1 % 2;
        int l5 = 1 - k5;

        if (k1 % 4 >= 2) {
            k5 = -k5;
            l5 = -l5;
        }

        if (d0 < 0.0D) {
            i1 = MathHelper.clamp(i1, 70, this.a.V() - 10);
            j5 = i1;

            for (k2 = -1; k2 <= 1; ++k2) {
                for (l2 = 1; l2 < 3; ++l2) {
                    for (i3 = -1; i3 < 3; ++i3) {
                        j3 = i5 + (l2 - 1) * k5 + k2 * l5;
                        k3 = j5 + i3;
                        l3 = j2 + (l2 - 1) * l5 - k2 * k5;
                        boolean flag = i3 < 0;

                        this.a.setTypeUpdate(new BlockPosition(j3, k3, l3), flag ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData());
                    }
                }
            }
        }

        IBlockData iblockdata = Blocks.PORTAL.getBlockData().set(BlockPortal.AXIS, k5 != 0 ? EnumDirection.EnumAxis.X : EnumDirection.EnumAxis.Z);

        for (l2 = 0; l2 < 4; ++l2) {
            for (i3 = 0; i3 < 4; ++i3) {
                for (j3 = -1; j3 < 4; ++j3) {
                    k3 = i5 + (i3 - 1) * k5;
                    l3 = j5 + j3;
                    i4 = j2 + (i3 - 1) * l5;
                    boolean flag1 = i3 == 0 || i3 == 3 || j3 == -1 || j3 == 3;

                    this.a.setTypeAndData(new BlockPosition(k3, l3, i4), flag1 ? Blocks.OBSIDIAN.getBlockData() : iblockdata, 2);
                }
            }

            for (i3 = 0; i3 < 4; ++i3) {
                for (j3 = -1; j3 < 4; ++j3) {
                    k3 = i5 + (i3 - 1) * k5;
                    l3 = j5 + j3;
                    i4 = j2 + (i3 - 1) * l5;
                    BlockPosition blockposition = new BlockPosition(k3, l3, i4);

                    this.a.applyPhysics(blockposition, this.a.getType(blockposition).getBlock());
                }
            }
        }

        return true;
    }

    public void a(long i) {
        if (i % 100L == 0L) {
            Iterator iterator = this.d.iterator();
            long j = i - 300L;

            while (iterator.hasNext()) {
                Long olong = (Long) iterator.next();
                PortalTravelAgent.ChunkCoordinatesPortal portaltravelagent_chunkcoordinatesportal = (PortalTravelAgent.ChunkCoordinatesPortal) this.c.getEntry(olong.longValue());

                if (portaltravelagent_chunkcoordinatesportal == null || portaltravelagent_chunkcoordinatesportal.c < j) {
                    iterator.remove();
                    this.c.remove(olong.longValue());
                }
            }
        }

    }

    public class ChunkCoordinatesPortal extends BlockPosition {

        public long c;

        public ChunkCoordinatesPortal(BlockPosition blockposition, long i) {
            super(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            this.c = i;
        }
    }
}
