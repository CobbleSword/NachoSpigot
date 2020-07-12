package net.minecraft.server;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.event.server.MapInitializeEvent;
// CraftBukkit end

public class ItemWorldMap extends ItemWorldMapBase {

    protected ItemWorldMap() {
        this.a(true);
    }

    public WorldMap getSavedMap(ItemStack itemstack, World world) {
        World worldMain = world.getServer().getServer().worlds.get(0); // CraftBukkit - store reference to primary world
        String s = "map_" + itemstack.getData();
        WorldMap worldmap = (WorldMap) worldMain.a(WorldMap.class, s); // CraftBukkit - use primary world for maps

        if (worldmap == null && !world.isClientSide) {
            itemstack.setData(worldMain.b("map")); // CraftBukkit - use primary world for maps
            s = "map_" + itemstack.getData();
            worldmap = new WorldMap(s);
            worldmap.scale = 3;
            worldmap.a((double) world.getWorldData().c(), (double) world.getWorldData().e(), worldmap.scale);
            worldmap.map = (byte) ((WorldServer) world).dimension; // CraftBukkit - fixes Bukkit multiworld maps
            worldmap.c();
            worldMain.a(s, (PersistentBase) worldmap); // CraftBukkit - use primary world for maps

            // CraftBukkit start
            MapInitializeEvent event = new MapInitializeEvent(worldmap.mapView);
            Bukkit.getServer().getPluginManager().callEvent(event);
            // CraftBukkit end
        }

        return worldmap;
    }

    public void a(World world, Entity entity, WorldMap worldmap) {
        // CraftBukkit - world.worldProvider -> ((WorldServer) world)
        if (((WorldServer) world).dimension == worldmap.map && entity instanceof EntityHuman) {
            int i = 1 << worldmap.scale;
            int j = worldmap.centerX;
            int k = worldmap.centerZ;
            int l = MathHelper.floor(entity.locX - (double) j) / i + 64;
            int i1 = MathHelper.floor(entity.locZ - (double) k) / i + 64;
            int j1 = 128 / i;

            if (world.worldProvider.o()) {
                j1 /= 2;
            }

            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = worldmap.a((EntityHuman) entity);

            ++worldmap_worldmaphumantracker.b;
            boolean flag = false;

            for (int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
                if ((k1 & 15) == (worldmap_worldmaphumantracker.b & 15) || flag) {
                    flag = false;
                    double d0 = 0.0D;

                    for (int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                        if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                            int i2 = k1 - l;
                            int j2 = l1 - i1;
                            boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                            int k2 = (j / i + k1 - 64) * i;
                            int l2 = (k / i + l1 - 64) * i;
                            HashMultiset hashmultiset = HashMultiset.create();
                            Chunk chunk = world.getChunkAtWorldCoords(new BlockPosition(k2, 0, l2));

                            if (!chunk.isEmpty()) {
                                int i3 = k2 & 15;
                                int j3 = l2 & 15;
                                int k3 = 0;
                                double d1 = 0.0D;

                                if (world.worldProvider.o()) {
                                    int l3 = k2 + l2 * 231871;

                                    l3 = l3 * l3 * 31287121 + l3 * 11;
                                    if ((l3 >> 20 & 1) == 0) {
                                        hashmultiset.add(Blocks.DIRT.g(Blocks.DIRT.getBlockData().set(BlockDirt.VARIANT, BlockDirt.EnumDirtVariant.DIRT)), 10);
                                    } else {
                                        hashmultiset.add(Blocks.STONE.g(Blocks.STONE.getBlockData().set(BlockStone.VARIANT, BlockStone.EnumStoneVariant.STONE)), 100);
                                    }

                                    d1 = 100.0D;
                                } else {
                                    BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                                    for (int i4 = 0; i4 < i; ++i4) {
                                        for (int j4 = 0; j4 < i; ++j4) {
                                            int k4 = chunk.b(i4 + i3, j4 + j3) + 1;
                                            IBlockData iblockdata = Blocks.AIR.getBlockData();

                                            if (k4 > 1) {
                                                do {
                                                    --k4;
                                                    iblockdata = chunk.getBlockData(blockposition_mutableblockposition.c(i4 + i3, k4, j4 + j3));
                                                } while (iblockdata.getBlock().g(iblockdata) == MaterialMapColor.b && k4 > 0);

                                                if (k4 > 0 && iblockdata.getBlock().getMaterial().isLiquid()) {
                                                    int l4 = k4 - 1;

                                                    Block block;

                                                    do {
                                                        block = chunk.getTypeAbs(i4 + i3, l4--, j4 + j3);
                                                        ++k3;
                                                    } while (l4 > 0 && block.getMaterial().isLiquid());
                                                }
                                            }

                                            d1 += (double) k4 / (double) (i * i);
                                            hashmultiset.add(iblockdata.getBlock().g(iblockdata));
                                        }
                                    }
                                }

                                k3 /= i * i;
                                double d2 = (d1 - d0) * 4.0D / (double) (i + 4) + ((double) (k1 + l1 & 1) - 0.5D) * 0.4D;
                                byte b0 = 1;

                                if (d2 > 0.6D) {
                                    b0 = 2;
                                }

                                if (d2 < -0.6D) {
                                    b0 = 0;
                                }

                                MaterialMapColor materialmapcolor = (MaterialMapColor) Iterables.getFirst(Multisets.copyHighestCountFirst(hashmultiset), MaterialMapColor.b);

                                if (materialmapcolor == MaterialMapColor.n) {
                                    d2 = (double) k3 * 0.1D + (double) (k1 + l1 & 1) * 0.2D;
                                    b0 = 1;
                                    if (d2 < 0.5D) {
                                        b0 = 2;
                                    }

                                    if (d2 > 0.9D) {
                                        b0 = 0;
                                    }
                                }

                                d0 = d1;
                                if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                                    byte b1 = worldmap.colors[k1 + l1 * 128];
                                    byte b2 = (byte) (materialmapcolor.M * 4 + b0);

                                    if (b1 != b2) {
                                        worldmap.colors[k1 + l1 * 128] = b2;
                                        worldmap.flagDirty(k1, l1);
                                        flag = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public void a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        if (!world.isClientSide) {
            WorldMap worldmap = this.getSavedMap(itemstack, world);

            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                worldmap.a(entityhuman, itemstack);
            }

            if (flag) {
                this.a(world, entity, worldmap);
            }

        }
    }

    public Packet c(ItemStack itemstack, World world, EntityHuman entityhuman) {
        return this.getSavedMap(itemstack, world).a(itemstack, world, entityhuman);
    }

    public void d(ItemStack itemstack, World world, EntityHuman entityhuman) {
        if (itemstack.hasTag() && itemstack.getTag().getBoolean("map_is_scaling")) {
            WorldMap worldmap = Items.FILLED_MAP.getSavedMap(itemstack, world);

            world = world.getServer().getServer().worlds.get(0); // CraftBukkit - use primary world for maps

            itemstack.setData(world.b("map"));
            WorldMap worldmap1 = new WorldMap("map_" + itemstack.getData());

            worldmap1.scale = (byte) (worldmap.scale + 1);
            if (worldmap1.scale > 4) {
                worldmap1.scale = 4;
            }

            worldmap1.a((double) worldmap.centerX, (double) worldmap.centerZ, worldmap1.scale);
            worldmap1.map = worldmap.map;
            worldmap1.c();
            world.a("map_" + itemstack.getData(), (PersistentBase) worldmap1);

            // CraftBukkit start
            MapInitializeEvent event = new MapInitializeEvent(worldmap1.mapView);
            Bukkit.getServer().getPluginManager().callEvent(event);
            // CraftBukkit end
        }

    }
}
