package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WorldGenRegistration {

    public static void a() {
        WorldGenFactory.a(WorldGenRegistration.WorldGenPyramidPiece.class, "TeDP");
        WorldGenFactory.a(WorldGenRegistration.WorldGenJungleTemple.class, "TeJP");
        WorldGenFactory.a(WorldGenRegistration.WorldGenWitchHut.class, "TeSH");
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                WorldGenRegistration.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                WorldGenRegistration.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

        }
    }

    public static class WorldGenWitchHut extends WorldGenRegistration.WorldGenScatteredPiece {

        private boolean e;

        public WorldGenWitchHut() {}

        public WorldGenWitchHut(Random random, int i, int j) {
            super(random, i, 64, j, 7, 7, 9);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("Witch", this.e);
        }

        protected void b(NBTTagCompound nbttagcompound) {
            super.b(nbttagcompound);
            this.e = nbttagcompound.getBoolean("Witch");
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (!this.a(world, structureboundingbox, 0)) {
                return false;
            } else {
                this.a(world, structureboundingbox, 1, 1, 1, 5, 1, 7, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 1, 4, 2, 5, 4, 7, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 2, 1, 0, 4, 1, 0, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 2, 2, 2, 3, 3, 2, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 1, 2, 3, 1, 3, 6, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 5, 2, 3, 5, 3, 6, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 2, 2, 7, 4, 3, 7, Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), Blocks.PLANKS.fromLegacyData(BlockWood.EnumLogVariant.SPRUCE.a()), false);
                this.a(world, structureboundingbox, 1, 0, 2, 1, 3, 2, Blocks.LOG.getBlockData(), Blocks.LOG.getBlockData(), false);
                this.a(world, structureboundingbox, 5, 0, 2, 5, 3, 2, Blocks.LOG.getBlockData(), Blocks.LOG.getBlockData(), false);
                this.a(world, structureboundingbox, 1, 0, 7, 1, 3, 7, Blocks.LOG.getBlockData(), Blocks.LOG.getBlockData(), false);
                this.a(world, structureboundingbox, 5, 0, 7, 5, 3, 7, Blocks.LOG.getBlockData(), Blocks.LOG.getBlockData(), false);
                this.a(world, Blocks.FENCE.getBlockData(), 2, 3, 2, structureboundingbox);
                this.a(world, Blocks.FENCE.getBlockData(), 3, 3, 7, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 1, 3, 4, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 5, 3, 4, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 5, 3, 5, structureboundingbox);
                this.a(world, Blocks.FLOWER_POT.getBlockData().set(BlockFlowerPot.CONTENTS, BlockFlowerPot.EnumFlowerPotContents.MUSHROOM_RED), 1, 3, 5, structureboundingbox);
                this.a(world, Blocks.CRAFTING_TABLE.getBlockData(), 3, 2, 6, structureboundingbox);
                this.a(world, Blocks.cauldron.getBlockData(), 4, 2, 6, structureboundingbox);
                this.a(world, Blocks.FENCE.getBlockData(), 1, 2, 1, structureboundingbox);
                this.a(world, Blocks.FENCE.getBlockData(), 5, 2, 1, structureboundingbox);
                int i = this.a(Blocks.OAK_STAIRS, 3);
                int j = this.a(Blocks.OAK_STAIRS, 1);
                int k = this.a(Blocks.OAK_STAIRS, 0);
                int l = this.a(Blocks.OAK_STAIRS, 2);

                this.a(world, structureboundingbox, 0, 4, 1, 6, 4, 1, Blocks.SPRUCE_STAIRS.fromLegacyData(i), Blocks.SPRUCE_STAIRS.fromLegacyData(i), false);
                this.a(world, structureboundingbox, 0, 4, 2, 0, 4, 7, Blocks.SPRUCE_STAIRS.fromLegacyData(k), Blocks.SPRUCE_STAIRS.fromLegacyData(k), false);
                this.a(world, structureboundingbox, 6, 4, 2, 6, 4, 7, Blocks.SPRUCE_STAIRS.fromLegacyData(j), Blocks.SPRUCE_STAIRS.fromLegacyData(j), false);
                this.a(world, structureboundingbox, 0, 4, 8, 6, 4, 8, Blocks.SPRUCE_STAIRS.fromLegacyData(l), Blocks.SPRUCE_STAIRS.fromLegacyData(l), false);

                int i1;
                int j1;

                for (i1 = 2; i1 <= 7; i1 += 5) {
                    for (j1 = 1; j1 <= 5; j1 += 4) {
                        this.b(world, Blocks.LOG.getBlockData(), j1, -1, i1, structureboundingbox);
                    }
                }

                if (!this.e) {
                    i1 = this.a(2, 5);
                    j1 = this.d(2);
                    int k1 = this.b(2, 5);

                    if (structureboundingbox.b((BaseBlockPosition) (new BlockPosition(i1, j1, k1)))) {
                        this.e = true;
                        EntityWitch entitywitch = new EntityWitch(world);

                        entitywitch.setPositionRotation((double) i1 + 0.5D, (double) j1, (double) k1 + 0.5D, 0.0F, 0.0F);
                        entitywitch.prepare(world.E(new BlockPosition(i1, j1, k1)), (GroupDataEntity) null);
                        world.addEntity(entitywitch, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CHUNK_GEN); // CraftBukkit - add SpawnReason
                    }
                }

                return true;
            }
        }
    }

    public static class WorldGenJungleTemple extends WorldGenRegistration.WorldGenScatteredPiece {

        private boolean e;
        private boolean f;
        private boolean g;
        private boolean h;
        private static final List<StructurePieceTreasure> i = Lists.newArrayList(new StructurePieceTreasure[] { new StructurePieceTreasure(Items.DIAMOND, 0, 1, 3, 3), new StructurePieceTreasure(Items.IRON_INGOT, 0, 1, 5, 10), new StructurePieceTreasure(Items.GOLD_INGOT, 0, 2, 7, 15), new StructurePieceTreasure(Items.EMERALD, 0, 1, 3, 2), new StructurePieceTreasure(Items.BONE, 0, 4, 6, 20), new StructurePieceTreasure(Items.ROTTEN_FLESH, 0, 3, 7, 16), new StructurePieceTreasure(Items.SADDLE, 0, 1, 1, 3), new StructurePieceTreasure(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1), new StructurePieceTreasure(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1), new StructurePieceTreasure(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)});
        private static final List<StructurePieceTreasure> j = Lists.newArrayList(new StructurePieceTreasure[] { new StructurePieceTreasure(Items.ARROW, 0, 2, 7, 30)});
        private static WorldGenRegistration.WorldGenJungleTemple.WorldGenJungleTemple$WorldGenJungleTemplePiece k = new WorldGenRegistration.WorldGenJungleTemple.WorldGenJungleTemple$WorldGenJungleTemplePiece((WorldGenRegistration.SyntheticClass_1) null);

        public WorldGenJungleTemple() {}

        public WorldGenJungleTemple(Random random, int i, int j) {
            super(random, i, 64, j, 12, 10, 15);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("placedMainChest", this.e);
            nbttagcompound.setBoolean("placedHiddenChest", this.f);
            nbttagcompound.setBoolean("placedTrap1", this.g);
            nbttagcompound.setBoolean("placedTrap2", this.h);
        }

        protected void b(NBTTagCompound nbttagcompound) {
            super.b(nbttagcompound);
            this.e = nbttagcompound.getBoolean("placedMainChest");
            this.f = nbttagcompound.getBoolean("placedHiddenChest");
            this.g = nbttagcompound.getBoolean("placedTrap1");
            this.h = nbttagcompound.getBoolean("placedTrap2");
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            if (!this.a(world, structureboundingbox, 0)) {
                return false;
            } else {
                int i = this.a(Blocks.STONE_STAIRS, 3);
                int j = this.a(Blocks.STONE_STAIRS, 2);
                int k = this.a(Blocks.STONE_STAIRS, 0);
                int l = this.a(Blocks.STONE_STAIRS, 1);

                this.a(world, structureboundingbox, 0, -4, 0, this.a - 1, 0, this.c - 1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 2, 1, 2, 9, 2, 2, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 2, 1, 12, 9, 2, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 2, 1, 3, 2, 2, 11, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 9, 1, 3, 9, 2, 11, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 1, 3, 1, 10, 6, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 1, 3, 13, 10, 6, 13, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 1, 3, 2, 1, 6, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 10, 3, 2, 10, 6, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 2, 3, 2, 9, 3, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 2, 6, 2, 9, 6, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 3, 7, 3, 8, 7, 11, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 4, 8, 4, 7, 8, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 3, 1, 3, 8, 2, 11);
                this.a(world, structureboundingbox, 4, 3, 6, 7, 3, 9);
                this.a(world, structureboundingbox, 2, 4, 2, 9, 5, 12);
                this.a(world, structureboundingbox, 4, 6, 5, 7, 6, 9);
                this.a(world, structureboundingbox, 5, 7, 6, 6, 7, 8);
                this.a(world, structureboundingbox, 5, 1, 2, 6, 2, 2);
                this.a(world, structureboundingbox, 5, 2, 12, 6, 2, 12);
                this.a(world, structureboundingbox, 5, 5, 1, 6, 5, 1);
                this.a(world, structureboundingbox, 5, 5, 13, 6, 5, 13);
                this.a(world, Blocks.AIR.getBlockData(), 1, 5, 5, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 10, 5, 5, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 1, 5, 9, structureboundingbox);
                this.a(world, Blocks.AIR.getBlockData(), 10, 5, 9, structureboundingbox);

                int i1;

                for (i1 = 0; i1 <= 14; i1 += 14) {
                    this.a(world, structureboundingbox, 2, 4, i1, 2, 5, i1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                    this.a(world, structureboundingbox, 4, 4, i1, 4, 5, i1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                    this.a(world, structureboundingbox, 7, 4, i1, 7, 5, i1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                    this.a(world, structureboundingbox, 9, 4, i1, 9, 5, i1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                }

                this.a(world, structureboundingbox, 5, 6, 0, 6, 6, 0, false, random, WorldGenRegistration.WorldGenJungleTemple.k);

                for (i1 = 0; i1 <= 11; i1 += 11) {
                    for (int j1 = 2; j1 <= 12; j1 += 2) {
                        this.a(world, structureboundingbox, i1, 4, j1, i1, 5, j1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                    }

                    this.a(world, structureboundingbox, i1, 6, 5, i1, 6, 5, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                    this.a(world, structureboundingbox, i1, 6, 9, i1, 6, 9, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                }

                this.a(world, structureboundingbox, 2, 7, 2, 2, 9, 2, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 9, 7, 2, 9, 9, 2, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 2, 7, 12, 2, 9, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 9, 7, 12, 9, 9, 12, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 4, 9, 4, 4, 9, 4, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 7, 9, 4, 7, 9, 4, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 4, 9, 10, 4, 9, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 7, 9, 10, 7, 9, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 5, 9, 7, 6, 9, 7, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 5, 9, 6, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 6, 9, 6, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(j), 5, 9, 8, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(j), 6, 9, 8, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 4, 0, 0, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 5, 0, 0, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 6, 0, 0, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 7, 0, 0, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 4, 1, 8, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 4, 2, 9, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 4, 3, 10, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 7, 1, 8, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 7, 2, 9, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(i), 7, 3, 10, structureboundingbox);
                this.a(world, structureboundingbox, 4, 1, 9, 4, 1, 9, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 7, 1, 9, 7, 1, 9, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 4, 1, 10, 7, 2, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 5, 4, 5, 6, 4, 5, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(k), 4, 4, 5, structureboundingbox);
                this.a(world, Blocks.STONE_STAIRS.fromLegacyData(l), 7, 4, 5, structureboundingbox);

                for (i1 = 0; i1 < 4; ++i1) {
                    this.a(world, Blocks.STONE_STAIRS.fromLegacyData(j), 5, 0 - i1, 6 + i1, structureboundingbox);
                    this.a(world, Blocks.STONE_STAIRS.fromLegacyData(j), 6, 0 - i1, 6 + i1, structureboundingbox);
                    this.a(world, structureboundingbox, 5, 0 - i1, 7 + i1, 6, 0 - i1, 9 + i1);
                }

                this.a(world, structureboundingbox, 1, -3, 12, 10, -1, 13);
                this.a(world, structureboundingbox, 1, -3, 1, 3, -1, 13);
                this.a(world, structureboundingbox, 1, -3, 1, 9, -1, 5);

                for (i1 = 1; i1 <= 13; i1 += 2) {
                    this.a(world, structureboundingbox, 1, -3, i1, 1, -2, i1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                }

                for (i1 = 2; i1 <= 12; i1 += 2) {
                    this.a(world, structureboundingbox, 1, -1, i1, 3, -1, i1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                }

                this.a(world, structureboundingbox, 2, -2, 1, 5, -2, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 7, -2, 1, 9, -2, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 6, -3, 1, 6, -3, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 6, -1, 1, 6, -1, 1, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, Blocks.TRIPWIRE_HOOK.fromLegacyData(this.a(Blocks.TRIPWIRE_HOOK, EnumDirection.EAST.b())).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(true)), 1, -3, 8, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE_HOOK.fromLegacyData(this.a(Blocks.TRIPWIRE_HOOK, EnumDirection.WEST.b())).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(true)), 4, -3, 8, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 2, -3, 8, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 3, -3, 8, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 7, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 6, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 5, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 4, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 3, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 2, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 5, -3, 1, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 4, -3, 1, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 3, -3, 1, structureboundingbox);
                if (!this.g) {
                    this.g = this.a(world, structureboundingbox, random, 3, -2, 1, EnumDirection.NORTH.a(), WorldGenRegistration.WorldGenJungleTemple.j, 2);
                }

                this.a(world, Blocks.VINE.fromLegacyData(15), 3, -2, 2, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE_HOOK.fromLegacyData(this.a(Blocks.TRIPWIRE_HOOK, EnumDirection.NORTH.b())).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(true)), 7, -3, 1, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE_HOOK.fromLegacyData(this.a(Blocks.TRIPWIRE_HOOK, EnumDirection.SOUTH.b())).set(BlockTripwireHook.ATTACHED, Boolean.valueOf(true)), 7, -3, 5, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 7, -3, 2, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 7, -3, 3, structureboundingbox);
                this.a(world, Blocks.TRIPWIRE.getBlockData().set(BlockTripwire.ATTACHED, Boolean.valueOf(true)), 7, -3, 4, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 8, -3, 6, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 9, -3, 6, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 9, -3, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 9, -3, 4, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 9, -2, 4, structureboundingbox);
                if (!this.h) {
                    this.h = this.a(world, structureboundingbox, random, 9, -2, 3, EnumDirection.WEST.a(), WorldGenRegistration.WorldGenJungleTemple.j, 2);
                }

                this.a(world, Blocks.VINE.fromLegacyData(15), 8, -1, 3, structureboundingbox);
                this.a(world, Blocks.VINE.fromLegacyData(15), 8, -2, 3, structureboundingbox);
                if (!this.e) {
                    this.e = this.a(world, structureboundingbox, random, 8, -3, 3, StructurePieceTreasure.a(WorldGenRegistration.WorldGenJungleTemple.i, new StructurePieceTreasure[] { Items.ENCHANTED_BOOK.b(random)}), 2 + random.nextInt(5));
                }

                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 9, -3, 2, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 8, -3, 1, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 4, -3, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 5, -2, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 5, -1, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 6, -3, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 7, -2, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 7, -1, 5, structureboundingbox);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 8, -3, 5, structureboundingbox);
                this.a(world, structureboundingbox, 9, -1, 1, 9, -1, 5, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 8, -3, 8, 10, -1, 10);
                this.a(world, Blocks.STONEBRICK.fromLegacyData(BlockSmoothBrick.P), 8, -2, 11, structureboundingbox);
                this.a(world, Blocks.STONEBRICK.fromLegacyData(BlockSmoothBrick.P), 9, -2, 11, structureboundingbox);
                this.a(world, Blocks.STONEBRICK.fromLegacyData(BlockSmoothBrick.P), 10, -2, 11, structureboundingbox);
                this.a(world, Blocks.LEVER.fromLegacyData(BlockLever.a(EnumDirection.fromType1(this.a(Blocks.LEVER, EnumDirection.NORTH.a())))), 8, -2, 12, structureboundingbox);
                this.a(world, Blocks.LEVER.fromLegacyData(BlockLever.a(EnumDirection.fromType1(this.a(Blocks.LEVER, EnumDirection.NORTH.a())))), 9, -2, 12, structureboundingbox);
                this.a(world, Blocks.LEVER.fromLegacyData(BlockLever.a(EnumDirection.fromType1(this.a(Blocks.LEVER, EnumDirection.NORTH.a())))), 10, -2, 12, structureboundingbox);
                this.a(world, structureboundingbox, 8, -3, 8, 8, -3, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, structureboundingbox, 10, -3, 8, 10, -3, 10, false, random, WorldGenRegistration.WorldGenJungleTemple.k);
                this.a(world, Blocks.MOSSY_COBBLESTONE.getBlockData(), 10, -2, 9, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 8, -2, 9, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 8, -2, 10, structureboundingbox);
                this.a(world, Blocks.REDSTONE_WIRE.getBlockData(), 10, -1, 9, structureboundingbox);
                this.a(world, Blocks.STICKY_PISTON.fromLegacyData(EnumDirection.UP.a()), 9, -2, 8, structureboundingbox);
                this.a(world, Blocks.STICKY_PISTON.fromLegacyData(this.a(Blocks.STICKY_PISTON, EnumDirection.WEST.a())), 10, -2, 8, structureboundingbox);
                this.a(world, Blocks.STICKY_PISTON.fromLegacyData(this.a(Blocks.STICKY_PISTON, EnumDirection.WEST.a())), 10, -1, 8, structureboundingbox);
                this.a(world, Blocks.UNPOWERED_REPEATER.fromLegacyData(this.a(Blocks.UNPOWERED_REPEATER, EnumDirection.NORTH.b())), 10, -2, 10, structureboundingbox);
                if (!this.f) {
                    this.f = this.a(world, structureboundingbox, random, 9, -3, 10, StructurePieceTreasure.a(WorldGenRegistration.WorldGenJungleTemple.i, new StructurePieceTreasure[] { Items.ENCHANTED_BOOK.b(random)}), 2 + random.nextInt(5));
                }

                return true;
            }
        }

        static class WorldGenJungleTemple$WorldGenJungleTemplePiece extends StructurePiece.StructurePieceBlockSelector {

            private WorldGenJungleTemple$WorldGenJungleTemplePiece() {}

            public void a(Random random, int i, int j, int k, boolean flag) {
                if (random.nextFloat() < 0.4F) {
                    this.a = Blocks.COBBLESTONE.getBlockData();
                } else {
                    this.a = Blocks.MOSSY_COBBLESTONE.getBlockData();
                }

            }

            WorldGenJungleTemple$WorldGenJungleTemplePiece(WorldGenRegistration.SyntheticClass_1 worldgenregistration_syntheticclass_1) {
                this();
            }
        }
    }

    public static class WorldGenPyramidPiece extends WorldGenRegistration.WorldGenScatteredPiece {

        private boolean[] e = new boolean[4];
        private static final List<StructurePieceTreasure> f = Lists.newArrayList(new StructurePieceTreasure[] { new StructurePieceTreasure(Items.DIAMOND, 0, 1, 3, 3), new StructurePieceTreasure(Items.IRON_INGOT, 0, 1, 5, 10), new StructurePieceTreasure(Items.GOLD_INGOT, 0, 2, 7, 15), new StructurePieceTreasure(Items.EMERALD, 0, 1, 3, 2), new StructurePieceTreasure(Items.BONE, 0, 4, 6, 20), new StructurePieceTreasure(Items.ROTTEN_FLESH, 0, 3, 7, 16), new StructurePieceTreasure(Items.SADDLE, 0, 1, 1, 3), new StructurePieceTreasure(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1), new StructurePieceTreasure(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1), new StructurePieceTreasure(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)});

        public WorldGenPyramidPiece() {}

        public WorldGenPyramidPiece(Random random, int i, int j) {
            super(random, i, 64, j, 21, 15, 21);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setBoolean("hasPlacedChest0", this.e[0]);
            nbttagcompound.setBoolean("hasPlacedChest1", this.e[1]);
            nbttagcompound.setBoolean("hasPlacedChest2", this.e[2]);
            nbttagcompound.setBoolean("hasPlacedChest3", this.e[3]);
        }

        protected void b(NBTTagCompound nbttagcompound) {
            super.b(nbttagcompound);
            this.e[0] = nbttagcompound.getBoolean("hasPlacedChest0");
            this.e[1] = nbttagcompound.getBoolean("hasPlacedChest1");
            this.e[2] = nbttagcompound.getBoolean("hasPlacedChest2");
            this.e[3] = nbttagcompound.getBoolean("hasPlacedChest3");
        }

        public boolean a(World world, Random random, StructureBoundingBox structureboundingbox) {
            this.a(world, structureboundingbox, 0, -4, 0, this.a - 1, 0, this.c - 1, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);

            int i;

            for (i = 1; i <= 9; ++i) {
                this.a(world, structureboundingbox, i, i, i, this.a - 1 - i, i, this.c - 1 - i, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
                this.a(world, structureboundingbox, i + 1, i, i + 1, this.a - 2 - i, i, this.c - 2 - i, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            }

            int j;

            for (i = 0; i < this.a; ++i) {
                for (j = 0; j < this.c; ++j) {
                    byte b0 = -5;

                    this.b(world, Blocks.SANDSTONE.getBlockData(), i, b0, j, structureboundingbox);
                }
            }

            i = this.a(Blocks.SANDSTONE_STAIRS, 3);
            j = this.a(Blocks.SANDSTONE_STAIRS, 2);
            int k = this.a(Blocks.SANDSTONE_STAIRS, 0);
            int l = this.a(Blocks.SANDSTONE_STAIRS, 1);
            int i1 = ~EnumColor.ORANGE.getInvColorIndex() & 15;
            int j1 = ~EnumColor.BLUE.getInvColorIndex() & 15;

            this.a(world, structureboundingbox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(i), 2, 10, 0, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(j), 2, 10, 4, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(k), 0, 10, 2, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(l), 4, 10, 2, structureboundingbox);
            this.a(world, structureboundingbox, this.a - 5, 0, 0, this.a - 1, 9, 4, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 4, 10, 1, this.a - 2, 10, 3, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(i), this.a - 3, 10, 0, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(j), this.a - 3, 10, 4, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(k), this.a - 5, 10, 2, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(l), this.a - 1, 10, 2, structureboundingbox);
            this.a(world, structureboundingbox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 9, 1, 0, 11, 3, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 9, 1, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 9, 2, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 9, 3, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 10, 3, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 11, 3, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 11, 2, 1, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 11, 1, 1, structureboundingbox);
            this.a(world, structureboundingbox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 4, 1, 2, 8, 2, 2, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 12, 1, 2, 16, 2, 2, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 5, 4, 5, this.a - 6, 4, this.c - 6, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 9, 4, 9, 11, 4, 11, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, 8, 1, 8, 8, 3, 8, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 12, 1, 8, 12, 3, 8, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 8, 1, 12, 8, 3, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 12, 1, 12, 12, 3, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 5, 1, 5, this.a - 2, 4, 11, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 7, 7, 9, this.a - 7, 7, 11, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 5, 5, 9, 5, 7, 11, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, this.a - 6, 5, 9, this.a - 6, 7, 11, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, Blocks.AIR.getBlockData(), 5, 5, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 5, 6, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 6, 6, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), this.a - 6, 5, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), this.a - 6, 6, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), this.a - 7, 6, 10, structureboundingbox);
            this.a(world, structureboundingbox, 2, 4, 4, 2, 6, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 3, 4, 4, this.a - 3, 6, 4, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(i), 2, 4, 5, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(i), 2, 3, 4, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(i), this.a - 3, 4, 5, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(i), this.a - 3, 3, 4, structureboundingbox);
            this.a(world, structureboundingbox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 3, 1, 3, this.a - 2, 2, 3, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, Blocks.SANDSTONE_STAIRS.getBlockData(), 1, 1, 2, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.getBlockData(), this.a - 2, 1, 2, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SAND.a()), 1, 2, 2, structureboundingbox);
            this.a(world, Blocks.STONE_SLAB.fromLegacyData(BlockDoubleStepAbstract.EnumStoneSlabVariant.SAND.a()), this.a - 2, 2, 2, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(l), 2, 1, 2, structureboundingbox);
            this.a(world, Blocks.SANDSTONE_STAIRS.fromLegacyData(k), this.a - 3, 1, 2, structureboundingbox);
            this.a(world, structureboundingbox, 4, 3, 5, 4, 3, 18, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 5, 3, 5, this.a - 5, 3, 17, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 3, 1, 5, 4, 2, 16, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, structureboundingbox, this.a - 6, 1, 5, this.a - 5, 2, 16, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);

            int k1;

            for (k1 = 5; k1 <= 17; k1 += 2) {
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 4, 1, k1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 4, 2, k1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), this.a - 5, 1, k1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), this.a - 5, 2, k1, structureboundingbox);
            }

            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 10, 0, 7, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 10, 0, 8, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 9, 0, 9, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 11, 0, 9, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 8, 0, 10, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 12, 0, 10, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 7, 0, 10, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 13, 0, 10, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 9, 0, 11, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 11, 0, 11, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 10, 0, 12, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 10, 0, 13, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(j1), 10, 0, 10, structureboundingbox);

            for (k1 = 0; k1 <= this.a - 1; k1 += this.a - 1) {
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 2, 1, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 2, 2, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 2, 3, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 3, 1, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 3, 2, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 3, 3, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 4, 1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), k1, 4, 2, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 4, 3, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 5, 1, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 5, 2, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 5, 3, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 6, 1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), k1, 6, 2, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 6, 3, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 7, 1, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 7, 2, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 7, 3, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 8, 1, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 8, 2, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 8, 3, structureboundingbox);
            }

            for (k1 = 2; k1 <= this.a - 3; k1 += this.a - 3 - 2) {
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1 - 1, 2, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 2, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1 + 1, 2, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1 - 1, 3, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 3, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1 + 1, 3, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1 - 1, 4, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), k1, 4, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1 + 1, 4, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1 - 1, 5, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 5, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1 + 1, 5, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1 - 1, 6, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), k1, 6, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1 + 1, 6, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1 - 1, 7, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1, 7, 0, structureboundingbox);
                this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), k1 + 1, 7, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1 - 1, 8, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1, 8, 0, structureboundingbox);
                this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), k1 + 1, 8, 0, structureboundingbox);
            }

            this.a(world, structureboundingbox, 8, 4, 0, 12, 6, 0, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, Blocks.AIR.getBlockData(), 8, 6, 0, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 12, 6, 0, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 9, 5, 0, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 10, 5, 0, structureboundingbox);
            this.a(world, Blocks.STAINED_HARDENED_CLAY.fromLegacyData(i1), 11, 5, 0, structureboundingbox);
            this.a(world, structureboundingbox, 8, -14, 8, 12, -11, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 8, -10, 8, 12, -10, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), false);
            this.a(world, structureboundingbox, 8, -9, 8, 12, -9, 12, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), false);
            this.a(world, structureboundingbox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getBlockData(), Blocks.SANDSTONE.getBlockData(), false);
            this.a(world, structureboundingbox, 9, -11, 9, 11, -1, 11, Blocks.AIR.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, Blocks.STONE_PRESSURE_PLATE.getBlockData(), 10, -11, 10, structureboundingbox);
            this.a(world, structureboundingbox, 9, -13, 9, 11, -13, 11, Blocks.TNT.getBlockData(), Blocks.AIR.getBlockData(), false);
            this.a(world, Blocks.AIR.getBlockData(), 8, -11, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 8, -10, 10, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 7, -10, 10, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 7, -11, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 12, -11, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 12, -10, 10, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 13, -10, 10, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 13, -11, 10, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 10, -11, 8, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 10, -10, 8, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 10, -10, 7, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 10, -11, 7, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 10, -11, 12, structureboundingbox);
            this.a(world, Blocks.AIR.getBlockData(), 10, -10, 12, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.CHISELED.a()), 10, -10, 13, structureboundingbox);
            this.a(world, Blocks.SANDSTONE.fromLegacyData(BlockSandStone.EnumSandstoneVariant.SMOOTH.a()), 10, -11, 13, structureboundingbox);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                if (!this.e[enumdirection.b()]) {
                    int l1 = enumdirection.getAdjacentX() * 2;
                    int i2 = enumdirection.getAdjacentZ() * 2;

                    this.e[enumdirection.b()] = this.a(world, structureboundingbox, random, 10 + l1, -11, 10 + i2, StructurePieceTreasure.a(WorldGenRegistration.WorldGenPyramidPiece.f, new StructurePieceTreasure[] { Items.ENCHANTED_BOOK.b(random)}), 2 + random.nextInt(5));
                }
            }

            return true;
        }
    }

    abstract static class WorldGenScatteredPiece extends StructurePiece {

        protected int a;
        protected int b;
        protected int c;
        protected int d = -1;

        public WorldGenScatteredPiece() {}

        protected WorldGenScatteredPiece(Random random, int i, int j, int k, int l, int i1, int j1) {
            super(0);
            this.a = l;
            this.b = i1;
            this.c = j1;
            this.m = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
            switch (WorldGenRegistration.SyntheticClass_1.a[this.m.ordinal()]) {
            case 1:
            case 2:
                this.l = new StructureBoundingBox(i, j, k, i + l - 1, j + i1 - 1, k + j1 - 1);
                break;

            default:
                this.l = new StructureBoundingBox(i, j, k, i + j1 - 1, j + i1 - 1, k + l - 1);
            }

        }

        protected void a(NBTTagCompound nbttagcompound) {
            nbttagcompound.setInt("Width", this.a);
            nbttagcompound.setInt("Height", this.b);
            nbttagcompound.setInt("Depth", this.c);
            nbttagcompound.setInt("HPos", this.d);
        }

        protected void b(NBTTagCompound nbttagcompound) {
            this.a = nbttagcompound.getInt("Width");
            this.b = nbttagcompound.getInt("Height");
            this.c = nbttagcompound.getInt("Depth");
            this.d = nbttagcompound.getInt("HPos");
        }

        protected boolean a(World world, StructureBoundingBox structureboundingbox, int i) {
            if (this.d >= 0) {
                return true;
            } else {
                int j = 0;
                int k = 0;
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int l = this.l.c; l <= this.l.f; ++l) {
                    for (int i1 = this.l.a; i1 <= this.l.d; ++i1) {
                        blockposition_mutableblockposition.c(i1, 64, l);
                        if (structureboundingbox.b((BaseBlockPosition) blockposition_mutableblockposition)) {
                            j += Math.max(world.r(blockposition_mutableblockposition).getY(), world.worldProvider.getSeaLevel());
                            ++k;
                        }
                    }
                }

                if (k == 0) {
                    return false;
                } else {
                    this.d = j / k;
                    this.l.a(0, this.d - this.l.b + i, 0);
                    return true;
                }
            }
        }
    }
}
