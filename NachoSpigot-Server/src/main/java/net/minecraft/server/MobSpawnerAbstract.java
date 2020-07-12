package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;

import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
// CraftBukkit end

public abstract class MobSpawnerAbstract {

    public int spawnDelay = 20;
    private String mobName = "Pig";
    private final List<MobSpawnerAbstract.a> mobs = Lists.newArrayList();
    private MobSpawnerAbstract.a spawnData;
    private double e;
    private double f;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    private Entity j;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;
    private int tickDelay = 0; // PaperSpigot

    public MobSpawnerAbstract() {}

    public String getMobName() {
        if (this.i() == null) {
            // CraftBukkit start - fix NPE
            if (this.mobName == null) {
                this.mobName = "Pig";
            }
            // CraftBukkit end
            if (this.mobName != null && this.mobName.equals("Minecart")) {
                this.mobName = "MinecartRideable";
            }

            return this.mobName;
        } else {
            return this.i().d;
        }
    }

    public void setMobName(String s) {
        this.mobName = s;
    }

    private boolean g() {
        BlockPosition blockposition = this.b();

        return this.a().isPlayerNearbyWhoAffectsSpawning((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, (double) this.requiredPlayerRange); // PaperSpigot - Affects Spawning API
    }

    public void c() {
        // PaperSpigot start - Configurable mob spawner tick rate
        if (spawnDelay > 0 && --tickDelay > 0) return;
        tickDelay = this.a().paperSpigotConfig.mobSpawnerTickRate;
        // PaperSpigot end
        if (this.g()) {
            BlockPosition blockposition = this.b();
            double d0;

            if (this.a().isClientSide) {
                double d1 = (double) ((float) blockposition.getX() + this.a().random.nextFloat());
                double d2 = (double) ((float) blockposition.getY() + this.a().random.nextFloat());

                d0 = (double) ((float) blockposition.getZ() + this.a().random.nextFloat());
                this.a().addParticle(EnumParticle.SMOKE_NORMAL, d1, d2, d0, 0.0D, 0.0D, 0.0D, new int[0]);
                this.a().addParticle(EnumParticle.FLAME, d1, d2, d0, 0.0D, 0.0D, 0.0D, new int[0]);
                if (this.spawnDelay > 0) {
                    this.spawnDelay -= tickDelay; // PaperSpigot
                }

                this.f = this.e;
                this.e = (this.e + (double) (1000.0F / ((float) this.spawnDelay + 200.0F))) % 360.0D;
            } else {
                if (this.spawnDelay < -tickDelay) { // PaperSpigot
                    this.h();
                }

                if (this.spawnDelay > 0) {
                    this.spawnDelay -= tickDelay; // PaperSpigot
                    return;
                }

                boolean flag = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    // TacoSpigot start
                    SpawnerPreSpawnEvent event = new SpawnerPreSpawnEvent( new Location(this.a().getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ()), EntityType.fromName(this.getMobName()));
                    this.a().getServer().getPluginManager().callEvent(event);

                    if (event.isCancelled()) {
                        flag = true;
                        break;
                    }
                    // TacoSpigot end

                    Entity entity = EntityTypes.createEntityByName(this.getMobName(), this.a());

                    if (entity == null) {
                        return;
                    }

                    int j = this.a().a(entity.getClass(), (new AxisAlignedBB((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1))).grow((double) this.spawnRange, (double) this.spawnRange, (double) this.spawnRange)).size();

                    if (j >= this.maxNearbyEntities) {
                        this.h();
                        return;
                    }

                    d0 = (double) blockposition.getX() + (this.a().random.nextDouble() - this.a().random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    double d3 = (double) (blockposition.getY() + this.a().random.nextInt(3) - 1);
                    double d4 = (double) blockposition.getZ() + (this.a().random.nextDouble() - this.a().random.nextDouble()) * (double) this.spawnRange + 0.5D;
                    EntityInsentient entityinsentient = entity instanceof EntityInsentient ? (EntityInsentient) entity : null;

                    entity.setPositionRotation(d0, d3, d4, this.a().random.nextFloat() * 360.0F, 0.0F);
                    if (entityinsentient == null || entityinsentient.bR() && entityinsentient.canSpawn()) {
                        this.a(entity, true);
                        this.a().triggerEffect(2004, blockposition, 0);
                        if (entityinsentient != null) {
                            entityinsentient.y();
                        }

                        flag = true;
                    }
                }

                if (flag) {
                    this.h();
                }
            }

        }
    }

    private Entity a(Entity entity, boolean flag) {
        if (this.i() != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entity.d(nbttagcompound);
            Iterator iterator = this.i().c.c().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                NBTBase nbtbase = this.i().c.get(s);

                nbttagcompound.set(s, nbtbase.clone());
            }

            entity.f(nbttagcompound);
            if (entity.world != null && flag) {
                // CraftBukkit start - call SpawnerSpawnEvent, abort if cancelled
                SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, this.b().getX(), this.b().getY(), this.b().getZ());
                if (!event.isCancelled()) {
                    entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
                    // Spigot Start
                    if ( entity.world.spigotConfig.nerfSpawnerMobs )
                    {
                        entity.fromMobSpawner = true;
                    }
                    // Spigot End
                }
                // CraftBukkit end
            }

            NBTTagCompound nbttagcompound1;

            for (Entity entity1 = entity; nbttagcompound.hasKeyOfType("Riding", 10); nbttagcompound = nbttagcompound1) {
                nbttagcompound1 = nbttagcompound.getCompound("Riding");
                Entity entity2 = EntityTypes.createEntityByName(nbttagcompound1.getString("id"), entity.world);

                if (entity2 != null) {
                    NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                    entity2.d(nbttagcompound2);
                    Iterator iterator1 = nbttagcompound1.c().iterator();

                    while (iterator1.hasNext()) {
                        String s1 = (String) iterator1.next();
                        NBTBase nbtbase1 = nbttagcompound1.get(s1);

                        nbttagcompound2.set(s1, nbtbase1.clone());
                    }

                    entity2.f(nbttagcompound2);
                    entity2.setPositionRotation(entity1.locX, entity1.locY, entity1.locZ, entity1.yaw, entity1.pitch);
                    // CraftBukkit start - call SpawnerSpawnEvent, skip if cancelled
                    SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity2, this.b().getX(), this.b().getY(), this.b().getZ());
                    if (event.isCancelled()) {
                        continue;
                    }
                    if (entity.world != null && flag) {
                        entity.world.addEntity(entity2, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
                    }

                    entity1.mount(entity2);
                }

                entity1 = entity2;
            }
        } else if (entity instanceof EntityLiving && entity.world != null && flag) {
            if (entity instanceof EntityInsentient) {
                ((EntityInsentient) entity).prepare(entity.world.E(new BlockPosition(entity)), (GroupDataEntity) null);
            }
            // Spigot start - call SpawnerSpawnEvent, abort if cancelled
            SpawnerSpawnEvent event = CraftEventFactory.callSpawnerSpawnEvent(entity, this.b().getX(), this.b().getY(), this.b().getZ());
            if (!event.isCancelled()) {
                entity.world.addEntity(entity, CreatureSpawnEvent.SpawnReason.SPAWNER); // CraftBukkit
                // Spigot Start
                if ( entity.world.spigotConfig.nerfSpawnerMobs )
                {
                    entity.fromMobSpawner = true;
                }
                // Spigot End
            }
            // Spigot end
        }

        return entity;
    }

    private void h() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;

            this.spawnDelay = this.minSpawnDelay + this.a().random.nextInt(i);
        }

        if (this.mobs.size() > 0) {
            this.a((MobSpawnerAbstract.a) WeightedRandom.a(this.a().random, this.mobs));
        }

        this.a(1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.mobName = nbttagcompound.getString("EntityId");
        this.spawnDelay = nbttagcompound.getShort("Delay");
        this.mobs.clear();
        if (nbttagcompound.hasKeyOfType("SpawnPotentials", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("SpawnPotentials", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.mobs.add(new MobSpawnerAbstract.a(nbttaglist.get(i)));
            }
        }

        if (nbttagcompound.hasKeyOfType("SpawnData", 10)) {
            this.a(new MobSpawnerAbstract.a(nbttagcompound.getCompound("SpawnData"), this.mobName));
        } else {
            this.a((MobSpawnerAbstract.a) null);
        }

        if (nbttagcompound.hasKeyOfType("MinSpawnDelay", 99)) {
            this.minSpawnDelay = nbttagcompound.getShort("MinSpawnDelay");
            this.maxSpawnDelay = nbttagcompound.getShort("MaxSpawnDelay");
            this.spawnCount = nbttagcompound.getShort("SpawnCount");
        }

        if (nbttagcompound.hasKeyOfType("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = nbttagcompound.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = nbttagcompound.getShort("RequiredPlayerRange");
        }

        if (nbttagcompound.hasKeyOfType("SpawnRange", 99)) {
            this.spawnRange = nbttagcompound.getShort("SpawnRange");
        }

        if (this.a() != null) {
            this.j = null;
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        String s = this.getMobName();

        if (!UtilColor.b(s)) {
            nbttagcompound.setString("EntityId", s);
            nbttagcompound.setShort("Delay", (short) this.spawnDelay);
            nbttagcompound.setShort("MinSpawnDelay", (short) this.minSpawnDelay);
            nbttagcompound.setShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
            nbttagcompound.setShort("SpawnCount", (short) this.spawnCount);
            nbttagcompound.setShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
            nbttagcompound.setShort("RequiredPlayerRange", (short) this.requiredPlayerRange);
            nbttagcompound.setShort("SpawnRange", (short) this.spawnRange);
            if (this.i() != null) {
                nbttagcompound.set("SpawnData", this.i().c.clone());
            }

            if (this.i() != null || this.mobs.size() > 0) {
                NBTTagList nbttaglist = new NBTTagList();

                if (this.mobs.size() > 0) {
                    Iterator iterator = this.mobs.iterator();

                    while (iterator.hasNext()) {
                        MobSpawnerAbstract.a mobspawnerabstract_a = (MobSpawnerAbstract.a) iterator.next();

                        nbttaglist.add(mobspawnerabstract_a.a());
                    }
                } else {
                    nbttaglist.add(this.i().a());
                }

                nbttagcompound.set("SpawnPotentials", nbttaglist);
            }

        }
    }

    public boolean b(int i) {
        if (i == 1 && this.a().isClientSide) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }

    private MobSpawnerAbstract.a i() {
        return this.spawnData;
    }

    public void a(MobSpawnerAbstract.a mobspawnerabstract_a) {
        this.spawnData = mobspawnerabstract_a;
    }

    public abstract void a(int i);

    public abstract World a();

    public abstract BlockPosition b();

    public class a extends WeightedRandom.WeightedRandomChoice {

        private final NBTTagCompound c;
        private final String d;

        public a(NBTTagCompound nbttagcompound) {
            this(nbttagcompound.getCompound("Properties"), nbttagcompound.getString("Type"), nbttagcompound.getInt("Weight"));
        }

        public a(NBTTagCompound nbttagcompound, String s) {
            this(nbttagcompound, s, 1);
        }

        private a(NBTTagCompound nbttagcompound, String s, int i) {
            super(i);
            if (s.equals("Minecart")) {
                if (nbttagcompound != null) {
                    s = EntityMinecartAbstract.EnumMinecartType.a(nbttagcompound.getInt("Type")).b();
                } else {
                    s = "MinecartRideable";
                }
            }

            this.c = nbttagcompound;
            this.d = s;
        }

        public NBTTagCompound a() {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            nbttagcompound.set("Properties", this.c);
            nbttagcompound.setString("Type", this.d);
            nbttagcompound.setInt("Weight", this.a);
            return nbttagcompound;
        }
    }
}
