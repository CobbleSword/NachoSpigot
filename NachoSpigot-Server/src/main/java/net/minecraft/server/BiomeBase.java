package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BiomeBase {

    private static final Logger aD = LogManager.getLogger();
    protected static final BiomeTemperature a = new BiomeTemperature(0.1F, 0.2F);
    protected static final BiomeTemperature b = new BiomeTemperature(-0.5F, 0.0F);
    protected static final BiomeTemperature c = new BiomeTemperature(-1.0F, 0.1F);
    protected static final BiomeTemperature d = new BiomeTemperature(-1.8F, 0.1F);
    protected static final BiomeTemperature e = new BiomeTemperature(0.125F, 0.05F);
    protected static final BiomeTemperature f = new BiomeTemperature(0.2F, 0.2F);
    protected static final BiomeTemperature g = new BiomeTemperature(0.45F, 0.3F);
    protected static final BiomeTemperature h = new BiomeTemperature(1.5F, 0.025F);
    protected static final BiomeTemperature i = new BiomeTemperature(1.0F, 0.5F);
    protected static final BiomeTemperature j = new BiomeTemperature(0.0F, 0.025F);
    protected static final BiomeTemperature k = new BiomeTemperature(0.1F, 0.8F);
    protected static final BiomeTemperature l = new BiomeTemperature(0.2F, 0.3F);
    protected static final BiomeTemperature m = new BiomeTemperature(-0.2F, 0.1F);
    private static final BiomeBase[] biomes = new BiomeBase[256];
    public static final Set<BiomeBase> n = Sets.newHashSet();
    public static final Map<String, BiomeBase> o = Maps.newHashMap();
    public static final BiomeBase OCEAN = (new BiomeOcean(0)).b(112).a("Ocean").a(BiomeBase.c);
    public static final BiomeBase PLAINS = (new BiomePlains(1)).b(9286496).a("Plains");
    public static final BiomeBase DESERT = (new BiomeDesert(2)).b(16421912).a("Desert").b().a(2.0F, 0.0F).a(BiomeBase.e);
    public static final BiomeBase EXTREME_HILLS = (new BiomeBigHills(3, false)).b(6316128).a("Extreme Hills").a(BiomeBase.i).a(0.2F, 0.3F);
    public static final BiomeBase FOREST = (new BiomeForest(4, 0)).b(353825).a("Forest");
    public static final BiomeBase TAIGA = (new BiomeTaiga(5, 0)).b(747097).a("Taiga").a(5159473).a(0.25F, 0.8F).a(BiomeBase.f);
    public static final BiomeBase SWAMPLAND = (new BiomeSwamp(6)).b(522674).a("Swampland").a(9154376).a(BiomeBase.m).a(0.8F, 0.9F);
    public static final BiomeBase RIVER = (new BiomeRiver(7)).b(255).a("River").a(BiomeBase.b);
    public static final BiomeBase HELL = (new BiomeHell(8)).b(16711680).a("Hell").b().a(2.0F, 0.0F);
    public static final BiomeBase SKY = (new BiomeTheEnd(9)).b(8421631).a("The End").b();
    public static final BiomeBase FROZEN_OCEAN = (new BiomeOcean(10)).b(9474208).a("FrozenOcean").c().a(BiomeBase.c).a(0.0F, 0.5F);
    public static final BiomeBase FROZEN_RIVER = (new BiomeRiver(11)).b(10526975).a("FrozenRiver").c().a(BiomeBase.b).a(0.0F, 0.5F);
    public static final BiomeBase ICE_PLAINS = (new BiomeIcePlains(12, false)).b(16777215).a("Ice Plains").c().a(0.0F, 0.5F).a(BiomeBase.e);
    public static final BiomeBase ICE_MOUNTAINS = (new BiomeIcePlains(13, false)).b(10526880).a("Ice Mountains").c().a(BiomeBase.g).a(0.0F, 0.5F);
    public static final BiomeBase MUSHROOM_ISLAND = (new BiomeMushrooms(14)).b(16711935).a("MushroomIsland").a(0.9F, 1.0F).a(BiomeBase.l);
    public static final BiomeBase MUSHROOM_SHORE = (new BiomeMushrooms(15)).b(10486015).a("MushroomIslandShore").a(0.9F, 1.0F).a(BiomeBase.j);
    public static final BiomeBase BEACH = (new BiomeBeach(16)).b(16440917).a("Beach").a(0.8F, 0.4F).a(BiomeBase.j);
    public static final BiomeBase DESERT_HILLS = (new BiomeDesert(17)).b(13786898).a("DesertHills").b().a(2.0F, 0.0F).a(BiomeBase.g);
    public static final BiomeBase FOREST_HILLS = (new BiomeForest(18, 0)).b(2250012).a("ForestHills").a(BiomeBase.g);
    public static final BiomeBase TAIGA_HILLS = (new BiomeTaiga(19, 0)).b(1456435).a("TaigaHills").a(5159473).a(0.25F, 0.8F).a(BiomeBase.g);
    public static final BiomeBase SMALL_MOUNTAINS = (new BiomeBigHills(20, true)).b(7501978).a("Extreme Hills Edge").a(BiomeBase.i.a()).a(0.2F, 0.3F);
    public static final BiomeBase JUNGLE = (new BiomeJungle(21, false)).b(5470985).a("Jungle").a(5470985).a(0.95F, 0.9F);
    public static final BiomeBase JUNGLE_HILLS = (new BiomeJungle(22, false)).b(2900485).a("JungleHills").a(5470985).a(0.95F, 0.9F).a(BiomeBase.g);
    public static final BiomeBase JUNGLE_EDGE = (new BiomeJungle(23, true)).b(6458135).a("JungleEdge").a(5470985).a(0.95F, 0.8F);
    public static final BiomeBase DEEP_OCEAN = (new BiomeOcean(24)).b(48).a("Deep Ocean").a(BiomeBase.d);
    public static final BiomeBase STONE_BEACH = (new BiomeStoneBeach(25)).b(10658436).a("Stone Beach").a(0.2F, 0.3F).a(BiomeBase.k);
    public static final BiomeBase COLD_BEACH = (new BiomeBeach(26)).b(16445632).a("Cold Beach").a(0.05F, 0.3F).a(BiomeBase.j).c();
    public static final BiomeBase BIRCH_FOREST = (new BiomeForest(27, 2)).a("Birch Forest").b(3175492);
    public static final BiomeBase BIRCH_FOREST_HILLS = (new BiomeForest(28, 2)).a("Birch Forest Hills").b(2055986).a(BiomeBase.g);
    public static final BiomeBase ROOFED_FOREST = (new BiomeForest(29, 3)).b(4215066).a("Roofed Forest");
    public static final BiomeBase COLD_TAIGA = (new BiomeTaiga(30, 0)).b(3233098).a("Cold Taiga").a(5159473).c().a(-0.5F, 0.4F).a(BiomeBase.f).c(16777215);
    public static final BiomeBase COLD_TAIGA_HILLS = (new BiomeTaiga(31, 0)).b(2375478).a("Cold Taiga Hills").a(5159473).c().a(-0.5F, 0.4F).a(BiomeBase.g).c(16777215);
    public static final BiomeBase MEGA_TAIGA = (new BiomeTaiga(32, 1)).b(5858897).a("Mega Taiga").a(5159473).a(0.3F, 0.8F).a(BiomeBase.f);
    public static final BiomeBase MEGA_TAIGA_HILLS = (new BiomeTaiga(33, 1)).b(4542270).a("Mega Taiga Hills").a(5159473).a(0.3F, 0.8F).a(BiomeBase.g);
    public static final BiomeBase EXTREME_HILLS_PLUS = (new BiomeBigHills(34, true)).b(5271632).a("Extreme Hills+").a(BiomeBase.i).a(0.2F, 0.3F);
    public static final BiomeBase SAVANNA = (new BiomeSavanna(35)).b(12431967).a("Savanna").a(1.2F, 0.0F).b().a(BiomeBase.e);
    public static final BiomeBase SAVANNA_PLATEAU = (new BiomeSavanna(36)).b(10984804).a("Savanna Plateau").a(1.0F, 0.0F).b().a(BiomeBase.h);
    public static final BiomeBase MESA = (new BiomeMesa(37, false, false)).b(14238997).a("Mesa");
    public static final BiomeBase MESA_PLATEAU_F = (new BiomeMesa(38, false, true)).b(11573093).a("Mesa Plateau F").a(BiomeBase.h);
    public static final BiomeBase MESA_PLATEAU = (new BiomeMesa(39, false, false)).b(13274213).a("Mesa Plateau").a(BiomeBase.h);
    public static final BiomeBase ad = BiomeBase.OCEAN;
    protected static final NoiseGenerator3 ae;
    protected static final NoiseGenerator3 af;
    protected static final WorldGenTallPlant ag;
    public String ah;
    public int ai;
    public int aj;
    public IBlockData ak;
    public IBlockData al;
    public int am;
    public float an;
    public float ao;
    public float temperature;
    public float humidity;
    public int ar;
    public BiomeDecorator as;
    protected List<BiomeMeta> at;
    protected List<BiomeMeta> au;
    protected List<BiomeMeta> av;
    protected List<BiomeMeta> aw;
    protected boolean ax;
    protected boolean ay;
    public final int id;
    protected WorldGenTrees aA;
    protected WorldGenBigTree aB;
    protected WorldGenSwampTree aC;

    protected BiomeBase(int i) {
        this.ak = Blocks.GRASS.getBlockData();
        this.al = Blocks.DIRT.getBlockData();
        this.am = 5169201;
        this.an = BiomeBase.a.a;
        this.ao = BiomeBase.a.b;
        this.temperature = 0.5F;
        this.humidity = 0.5F;
        this.ar = 16777215;
        this.at = Lists.newArrayList();
        this.au = Lists.newArrayList();
        this.av = Lists.newArrayList();
        this.aw = Lists.newArrayList();
        this.ay = true;
        this.aA = new WorldGenTrees(false);
        this.aB = new WorldGenBigTree(false);
        this.aC = new WorldGenSwampTree();
        this.id = i;
        BiomeBase.biomes[i] = this;
        this.as = this.a();
        this.au.add(new BiomeMeta(EntitySheep.class, 12, 4, 4));
        this.au.add(new BiomeMeta(EntityRabbit.class, 10, 3, 3));
        this.au.add(new BiomeMeta(EntityPig.class, 10, 4, 4));
        this.au.add(new BiomeMeta(EntityChicken.class, 10, 4, 4));
        this.au.add(new BiomeMeta(EntityCow.class, 8, 4, 4));
        this.at.add(new BiomeMeta(EntitySpider.class, 100, 4, 4));
        this.at.add(new BiomeMeta(EntityZombie.class, 100, 4, 4));
        this.at.add(new BiomeMeta(EntitySkeleton.class, 100, 4, 4));
        this.at.add(new BiomeMeta(EntityCreeper.class, 100, 4, 4));
        this.at.add(new BiomeMeta(EntitySlime.class, 100, 4, 4));
        this.at.add(new BiomeMeta(EntityEnderman.class, 10, 1, 4));
        this.at.add(new BiomeMeta(EntityWitch.class, 5, 1, 1));
        this.av.add(new BiomeMeta(EntitySquid.class, 10, 4, 4));
        this.aw.add(new BiomeMeta(EntityBat.class, 10, 8, 8));
    }

    protected BiomeDecorator a() {
        return new BiomeDecorator();
    }

    protected BiomeBase a(float f, float f1) {
        if (f > 0.1F && f < 0.2F) {
            throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
        } else {
            this.temperature = f;
            this.humidity = f1;
            return this;
        }
    }

    protected final BiomeBase a(BiomeTemperature biomebase_biometemperature) {
        this.an = biomebase_biometemperature.a;
        this.ao = biomebase_biometemperature.b;
        return this;
    }

    protected BiomeBase b() {
        this.ay = false;
        return this;
    }

    public WorldGenTreeAbstract a(Random random) {
        return (WorldGenTreeAbstract) (random.nextInt(10) == 0 ? this.aB : this.aA);
    }

    public WorldGenerator b(Random random) {
        return new WorldGenGrass(BlockLongGrass.EnumTallGrassType.GRASS);
    }

    public BlockFlowers.EnumFlowerVarient a(Random random, BlockPosition blockposition) {
        return random.nextInt(3) > 0 ? BlockFlowers.EnumFlowerVarient.DANDELION : BlockFlowers.EnumFlowerVarient.POPPY;
    }

    protected BiomeBase c() {
        this.ax = true;
        return this;
    }

    protected BiomeBase a(String s) {
        this.ah = s;
        return this;
    }

    protected BiomeBase a(int i) {
        this.am = i;
        return this;
    }

    protected BiomeBase b(int i) {
        this.a(i, false);
        return this;
    }

    protected BiomeBase c(int i) {
        this.aj = i;
        return this;
    }

    protected BiomeBase a(int i, boolean flag) {
        this.ai = i;
        if (flag) {
            this.aj = (i & 16711422) >> 1;
        } else {
            this.aj = i;
        }

        return this;
    }

    public List<BiomeMeta> getMobs(EnumCreatureType enumcreaturetype) {
        switch (SyntheticClass_1.switchMap[enumcreaturetype.ordinal()]) {
        case 1:
            return this.at;

        case 2:
            return this.au;

        case 3:
            return this.av;

        case 4:
            return this.aw;

        default:
            return Collections.emptyList();
        }
    }

    public boolean d() {
        return this.j();
    }

    public boolean e() {
        return this.j() ? false : this.ay;
    }

    public boolean f() {
        return this.humidity > 0.85F;
    }

    public float g() {
        return 0.1F;
    }

    public final int h() {
        return (int) (this.humidity * 65536.0F);
    }

    public final float a(BlockPosition blockposition) {
        if (blockposition.getY() > 64) {
            float f = (float) (BiomeBase.ae.a((double) blockposition.getX() * 1.0D / 8.0D, (double) blockposition.getZ() * 1.0D / 8.0D) * 4.0D);

            return this.temperature - (f + (float) blockposition.getY() - 64.0F) * 0.05F / 30.0F;
        } else {
            return this.temperature;
        }
    }

    public void a(World world, Random random, BlockPosition blockposition) {
        this.as.a(world, random, this, blockposition);
    }

    public boolean j() {
        return this.ax;
    }

    public void a(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        this.b(world, random, chunksnapshot, i, j, d0);
    }

    public final void b(World world, Random random, ChunkSnapshot chunksnapshot, int i, int j, double d0) {
        int k = world.F();
        IBlockData iblockdata = this.ak;
        IBlockData iblockdata1 = this.al;
        int l = -1;
        int i1 = (int) (d0 / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int j1 = i & 15;
        int k1 = j & 15;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l1 = 255; l1 >= 0; --l1) {
            if (l1 <= (world.paperSpigotConfig.generateFlatBedrock ? 0 : random.nextInt(5))) { // PaperSpigot - Configurable flat bedrock
                chunksnapshot.a(k1, l1, j1, Blocks.BEDROCK.getBlockData());
            } else {
                IBlockData iblockdata2 = chunksnapshot.a(k1, l1, j1);

                if (iblockdata2.getBlock().getMaterial() == Material.AIR) {
                    l = -1;
                } else if (iblockdata2.getBlock() == Blocks.STONE) {
                    if (l == -1) {
                        if (i1 <= 0) {
                            iblockdata = null;
                            iblockdata1 = Blocks.STONE.getBlockData();
                        } else if (l1 >= k - 4 && l1 <= k + 1) {
                            iblockdata = this.ak;
                            iblockdata1 = this.al;
                        }

                        if (l1 < k && (iblockdata == null || iblockdata.getBlock().getMaterial() == Material.AIR)) {
                            if (this.a((BlockPosition) blockposition_mutableblockposition.c(i, l1, j)) < 0.15F) {
                                iblockdata = Blocks.ICE.getBlockData();
                            } else {
                                iblockdata = Blocks.WATER.getBlockData();
                            }
                        }

                        l = i1;
                        if (l1 >= k - 1) {
                            chunksnapshot.a(k1, l1, j1, iblockdata);
                        } else if (l1 < k - 7 - i1) {
                            iblockdata = null;
                            iblockdata1 = Blocks.STONE.getBlockData();
                            chunksnapshot.a(k1, l1, j1, Blocks.GRAVEL.getBlockData());
                        } else {
                            chunksnapshot.a(k1, l1, j1, iblockdata1);
                        }
                    } else if (l > 0) {
                        --l;
                        chunksnapshot.a(k1, l1, j1, iblockdata1);
                        if (l == 0 && iblockdata1.getBlock() == Blocks.SAND) {
                            l = random.nextInt(4) + Math.max(0, l1 - 63);
                            iblockdata1 = iblockdata1.get(BlockSand.VARIANT) == BlockSand.EnumSandVariant.RED_SAND ? Blocks.RED_SANDSTONE.getBlockData() : Blocks.SANDSTONE.getBlockData();
                        }
                    }
                }
            }
        }

    }

    protected BiomeBase k() {
        return this.d(this.id + 128);
    }

    protected BiomeBase d(int i) {
        return new BiomeBaseSub(i, this);
    }

    public Class<? extends BiomeBase> l() {
        return this.getClass();
    }

    public boolean a(BiomeBase biomebase) {
        return biomebase == this ? true : (biomebase == null ? false : this.l() == biomebase.l());
    }

    public EnumTemperature m() {
        return (double) this.temperature < 0.2D ? EnumTemperature.COLD : ((double) this.temperature < 1.0D ? EnumTemperature.MEDIUM : EnumTemperature.WARM);
    }

    public static BiomeBase[] getBiomes() {
        return BiomeBase.biomes;
    }

    public static BiomeBase getBiome(int i) {
        return getBiome(i, (BiomeBase) null);
    }

    public static BiomeBase getBiome(int i, BiomeBase biomebase) {
        if (i >= 0 && i <= BiomeBase.biomes.length) {
            BiomeBase biomebase1 = BiomeBase.biomes[i];

            return biomebase1 == null ? biomebase : biomebase1;
        } else {
            BiomeBase.aD.warn("Biome ID is out of bounds: " + i + ", defaulting to 0 (Ocean)");
            return BiomeBase.OCEAN;
        }
    }

    static {
        BiomeBase.PLAINS.k();
        BiomeBase.DESERT.k();
        BiomeBase.FOREST.k();
        BiomeBase.TAIGA.k();
        BiomeBase.SWAMPLAND.k();
        BiomeBase.ICE_PLAINS.k();
        BiomeBase.JUNGLE.k();
        BiomeBase.JUNGLE_EDGE.k();
        BiomeBase.COLD_TAIGA.k();
        BiomeBase.SAVANNA.k();
        BiomeBase.SAVANNA_PLATEAU.k();
        BiomeBase.MESA.k();
        BiomeBase.MESA_PLATEAU_F.k();
        BiomeBase.MESA_PLATEAU.k();
        BiomeBase.BIRCH_FOREST.k();
        BiomeBase.BIRCH_FOREST_HILLS.k();
        BiomeBase.ROOFED_FOREST.k();
        BiomeBase.MEGA_TAIGA.k();
        BiomeBase.EXTREME_HILLS.k();
        BiomeBase.EXTREME_HILLS_PLUS.k();
        BiomeBase.MEGA_TAIGA.d(BiomeBase.MEGA_TAIGA_HILLS.id + 128).a("Redwood Taiga Hills M");
        BiomeBase[] abiomebase = BiomeBase.biomes;
        int i = abiomebase.length;

        for (int j = 0; j < i; ++j) {
            BiomeBase biomebase = abiomebase[j];

            if (biomebase != null) {
                if (BiomeBase.o.containsKey(biomebase.ah)) {
                    throw new Error("Biome \"" + biomebase.ah + "\" is defined as both ID " + ((BiomeBase) BiomeBase.o.get(biomebase.ah)).id + " and " + biomebase.id);
                }

                BiomeBase.o.put(biomebase.ah, biomebase);
                if (biomebase.id < 128) {
                    BiomeBase.n.add(biomebase);
                }
            }
        }

        BiomeBase.n.remove(BiomeBase.HELL);
        BiomeBase.n.remove(BiomeBase.SKY);
        BiomeBase.n.remove(BiomeBase.FROZEN_OCEAN);
        BiomeBase.n.remove(BiomeBase.SMALL_MOUNTAINS);
        ae = new NoiseGenerator3(new Random(1234L), 1);
        af = new NoiseGenerator3(new Random(2345L), 1);
        ag = new WorldGenTallPlant();
    }

    static class SyntheticClass_1 {

        static final int[] switchMap = new int[EnumCreatureType.values().length];

        static {
            try {
                SyntheticClass_1.switchMap[EnumCreatureType.MONSTER.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                SyntheticClass_1.switchMap[EnumCreatureType.CREATURE.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            try {
                SyntheticClass_1.switchMap[EnumCreatureType.WATER_CREATURE.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                SyntheticClass_1.switchMap[EnumCreatureType.AMBIENT.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

        }
    }

    public static class BiomeMeta extends WeightedRandom.WeightedRandomChoice {

        public Class<? extends EntityInsentient> b;
        public int c;
        public int d;

        public BiomeMeta(Class<? extends EntityInsentient> oclass, int i, int j, int k) {
            super(i);
            this.b = oclass;
            this.c = j;
            this.d = k;
        }

        public String toString() {
            return this.b.getSimpleName() + "*(" + this.c + "-" + this.d + "):" + this.a;
        }
    }

    public static class BiomeTemperature {

        public float a;
        public float b;

        public BiomeTemperature(float f, float f1) {
            this.a = f;
            this.b = f1;
        }

        public BiomeTemperature a() {
            return new BiomeTemperature(this.a * 0.8F, this.b * 0.6F);
        }
    }

    public static enum EnumTemperature {

        OCEAN, COLD, MEDIUM, WARM;

        private EnumTemperature() {}
    }
}
