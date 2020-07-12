package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTeleportEvent;
// CraftBukkit end

public class EntityEnderman extends EntityMonster {

    private static final UUID a = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier b = (new AttributeModifier(EntityEnderman.a, "Attacking speed boost", 0.15000000596046448D, 0)).a(false);
    private static final Set<Block> c = Sets.newIdentityHashSet();
    private boolean bm;

    public EntityEnderman(World world) {
        super(world);
        this.setSize(0.6F, 2.9F);
        this.S = 1.0F;
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(10, new EntityEnderman.PathfinderGoalEndermanPlaceBlock(this));
        this.goalSelector.a(11, new EntityEnderman.PathfinderGoalEndermanPickupBlock(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(2, new EntityEnderman.PathfinderGoalPlayerWhoLookedAtTarget(this));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityEndermite.class, 10, true, false, new Predicate() {
            public boolean a(EntityEndermite entityendermite) {
                return entityendermite.n();
            }

            public boolean apply(Object object) {
                return this.a((EntityEndermite) object);
            }
        }));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(7.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(64.0D);
    }

    protected void h() {
        super.h();
        this.datawatcher.a(16, new Short((short) 0));
        this.datawatcher.a(17, new Byte((byte) 0));
        this.datawatcher.a(18, new Byte((byte) 0));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        IBlockData iblockdata = this.getCarried();

        nbttagcompound.setShort("carried", (short) Block.getId(iblockdata.getBlock()));
        nbttagcompound.setShort("carriedData", (short) iblockdata.getBlock().toLegacyData(iblockdata));
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        IBlockData iblockdata;

        if (nbttagcompound.hasKeyOfType("carried", 8)) {
            iblockdata = Block.getByName(nbttagcompound.getString("carried")).fromLegacyData(nbttagcompound.getShort("carriedData") & '\uffff');
        } else {
            iblockdata = Block.getById(nbttagcompound.getShort("carried")).fromLegacyData(nbttagcompound.getShort("carriedData") & '\uffff');
        }

        this.setCarried(iblockdata);
    }

    private boolean c(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.armor[3];

        if (itemstack != null && itemstack.getItem() == Item.getItemOf(Blocks.PUMPKIN)) {
            return false;
        } else {
            Vec3D vec3d = entityhuman.d(1.0F).a();
            Vec3D vec3d1 = new Vec3D(this.locX - entityhuman.locX, this.getBoundingBox().b + (double) (this.length / 2.0F) - (entityhuman.locY + (double) entityhuman.getHeadHeight()), this.locZ - entityhuman.locZ);
            double d0 = vec3d1.b();

            vec3d1 = vec3d1.a();
            double d1 = vec3d.b(vec3d1);

            return d1 > 1.0D - 0.025D / d0 ? entityhuman.hasLineOfSight(this) : false;
        }
    }

    public float getHeadHeight() {
        return 2.55F;
    }

    public void m() {
        if (this.world.isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(EnumParticle.PORTAL, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width, this.locY + this.random.nextDouble() * (double) this.length - 0.25D, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width, (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D, new int[0]);
            }
        }

        this.aY = false;
        super.m();
    }

    protected void E() {
        if (this.U()) {
            this.damageEntity(DamageSource.DROWN, 1.0F);
        }

        if (this.co() && !this.bm && this.random.nextInt(100) == 0) {
            this.a(false);
        }

        if (this.world.w()) {
            float f = this.c(1.0F);

            if (f > 0.5F && this.world.i(new BlockPosition(this)) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setGoalTarget((EntityLiving) null);
                this.a(false);
                this.bm = false;
                this.n();
            }
        }

        super.E();
    }

    protected boolean n() {
        double d0 = this.locX + (this.random.nextDouble() - 0.5D) * 64.0D;
        double d1 = this.locY + (double) (this.random.nextInt(64) - 32);
        double d2 = this.locZ + (this.random.nextDouble() - 0.5D) * 64.0D;

        return this.k(d0, d1, d2);
    }

    protected boolean b(Entity entity) {
        Vec3D vec3d = new Vec3D(this.locX - entity.locX, this.getBoundingBox().b + (double) (this.length / 2.0F) - entity.locY + (double) entity.getHeadHeight(), this.locZ - entity.locZ);

        vec3d = vec3d.a();
        double d0 = 16.0D;
        double d1 = this.locX + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.a * d0;
        double d2 = this.locY + (double) (this.random.nextInt(16) - 8) - vec3d.b * d0;
        double d3 = this.locZ + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.c * d0;

        return this.k(d1, d2, d3);
    }

    protected boolean k(double d0, double d1, double d2) {
        double d3 = this.locX;
        double d4 = this.locY;
        double d5 = this.locZ;

        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        boolean flag = false;
        BlockPosition blockposition = new BlockPosition(this.locX, this.locY, this.locZ);

        if (this.world.isLoaded(blockposition)) {
            boolean flag1 = false;

            while (!flag1 && blockposition.getY() > 0) {
                BlockPosition blockposition1 = blockposition.down();
                Block block = this.world.getType(blockposition1).getBlock();

                if (block.getMaterial().isSolid()) {
                    flag1 = true;
                } else {
                    --this.locY;
                    blockposition = blockposition1;
                }
            }

            if (flag1) {
                // CraftBukkit start - Teleport event
                // super.enderTeleportTo(this.locX, this.locY, this.locZ);
                EntityTeleportEvent teleport = new EntityTeleportEvent(this.getBukkitEntity(), new Location(this.world.getWorld(), d3, d4, d5), new Location(this.world.getWorld(), this.locX, this.locY, this.locZ));
                this.world.getServer().getPluginManager().callEvent(teleport);
                if (teleport.isCancelled()) {
                    return false;
                }

                Location to = teleport.getTo();
                this.enderTeleportTo(to.getX(), to.getY(), to.getZ());
                // CraftBukkit end
                if (this.world.getCubes(this, this.getBoundingBox()).isEmpty() && !this.world.containsLiquid(this.getBoundingBox())) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            this.setPosition(d3, d4, d5);
            return false;
        } else {
            short short0 = 128;

            for (int i = 0; i < short0; ++i) {
                double d6 = (double) i / ((double) short0 - 1.0D);
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (this.locX - d3) * d6 + (this.random.nextDouble() - 0.5D) * (double) this.width * 2.0D;
                double d8 = d4 + (this.locY - d4) * d6 + this.random.nextDouble() * (double) this.length;
                double d9 = d5 + (this.locZ - d5) * d6 + (this.random.nextDouble() - 0.5D) * (double) this.width * 2.0D;

                this.world.addParticle(EnumParticle.PORTAL, d7, d8, d9, (double) f, (double) f1, (double) f2, new int[0]);
            }

            this.world.makeSound(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
            this.makeSound("mob.endermen.portal", 1.0F, 1.0F);
            return true;
        }
    }

    protected String z() {
        return this.co() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    protected String bo() {
        return "mob.endermen.hit";
    }

    protected String bp() {
        return "mob.endermen.death";
    }

    protected Item getLoot() {
        return Items.ENDER_PEARL;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        Item item = this.getLoot();

        if (item != null) {
            int j = this.random.nextInt(2 + i);

            for (int k = 0; k < j; ++k) {
                this.a(item, 1);
            }
        }

        // PaperSpigot start - Drop the block the entity is holding when it dies
        Item carriedItem = Item.getItemOf(getCarried().getBlock());
        if (carriedItem != null) {
            this.a(carriedItem, 1);
        }
        // PaperSpigot end

    }

    public void setCarried(IBlockData iblockdata) {
        this.datawatcher.watch(16, Short.valueOf((short) (Block.getCombinedId(iblockdata) & '\uffff')));
    }

    public IBlockData getCarried() {
        return Block.getByCombinedId(this.datawatcher.getShort(16) & '\uffff');
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (damagesource.getEntity() == null || !(damagesource.getEntity() instanceof EntityEndermite)) {
                if (!this.world.isClientSide) {
                    this.a(true);
                }

                if (damagesource instanceof EntityDamageSource && damagesource.getEntity() instanceof EntityHuman) {
                    if (damagesource.getEntity() instanceof EntityPlayer && ((EntityPlayer) damagesource.getEntity()).playerInteractManager.isCreative()) {
                        this.a(false);
                    } else {
                        this.bm = true;
                    }
                }

                if (damagesource instanceof EntityDamageSourceIndirect) {
                    this.bm = false;

                    for (int i = 0; i < 64; ++i) {
                        if (this.n()) {
                            return true;
                        }
                    }

                    return false;
                }
            }

            boolean flag = super.damageEntity(damagesource, f);

            if (damagesource.ignoresArmor() && this.random.nextInt(10) != 0) {
                this.n();
            }

            return flag;
        }
    }

    public boolean co() {
        return this.datawatcher.getByte(18) > 0;
    }

    public void a(boolean flag) {
        this.datawatcher.watch(18, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    static {
        EntityEnderman.c.add(Blocks.GRASS);
        EntityEnderman.c.add(Blocks.DIRT);
        EntityEnderman.c.add(Blocks.SAND);
        EntityEnderman.c.add(Blocks.GRAVEL);
        EntityEnderman.c.add(Blocks.YELLOW_FLOWER);
        EntityEnderman.c.add(Blocks.RED_FLOWER);
        EntityEnderman.c.add(Blocks.BROWN_MUSHROOM);
        EntityEnderman.c.add(Blocks.RED_MUSHROOM);
        EntityEnderman.c.add(Blocks.TNT);
        EntityEnderman.c.add(Blocks.CACTUS);
        EntityEnderman.c.add(Blocks.CLAY);
        EntityEnderman.c.add(Blocks.PUMPKIN);
        EntityEnderman.c.add(Blocks.MELON_BLOCK);
        EntityEnderman.c.add(Blocks.MYCELIUM);
    }

    static class PathfinderGoalEndermanPickupBlock extends PathfinderGoal {

        private EntityEnderman enderman;

        public PathfinderGoalEndermanPickupBlock(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
        }

        public boolean a() {
            return !this.enderman.world.getGameRules().getBoolean("mobGriefing") ? false : (this.enderman.getCarried().getBlock().getMaterial() != Material.AIR ? false : this.enderman.bc().nextInt(20) == 0);
        }

        public void e() {
            Random random = this.enderman.bc();
            World world = this.enderman.world;
            int i = MathHelper.floor(this.enderman.locX - 2.0D + random.nextDouble() * 4.0D);
            int j = MathHelper.floor(this.enderman.locY + random.nextDouble() * 3.0D);
            int k = MathHelper.floor(this.enderman.locZ - 2.0D + random.nextDouble() * 4.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (EntityEnderman.c.contains(block)) {
                // CraftBukkit start - Pickup event
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.enderman, this.enderman.world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), org.bukkit.Material.AIR).isCancelled()) {
                    this.enderman.setCarried(iblockdata);
                    world.setTypeUpdate(blockposition, Blocks.AIR.getBlockData());
                }
                // CraftBukkit end
            }

        }
    }

    static class PathfinderGoalEndermanPlaceBlock extends PathfinderGoal {

        private EntityEnderman a;

        public PathfinderGoalEndermanPlaceBlock(EntityEnderman entityenderman) {
            this.a = entityenderman;
        }

        public boolean a() {
            return !this.a.world.getGameRules().getBoolean("mobGriefing") ? false : (this.a.getCarried().getBlock().getMaterial() == Material.AIR ? false : this.a.bc().nextInt(2000) == 0);
        }

        public void e() {
            Random random = this.a.bc();
            World world = this.a.world;
            int i = MathHelper.floor(this.a.locX - 1.0D + random.nextDouble() * 2.0D);
            int j = MathHelper.floor(this.a.locY + random.nextDouble() * 2.0D);
            int k = MathHelper.floor(this.a.locZ - 1.0D + random.nextDouble() * 2.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            Block block = world.getType(blockposition).getBlock();
            Block block1 = world.getType(blockposition.down()).getBlock();

            if (this.a(world, blockposition, this.a.getCarried().getBlock(), block, block1)) {
                // CraftBukkit start - Place event
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.a, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this.a.getCarried().getBlock(), this.a.getCarried().getBlock().toLegacyData(this.a.getCarried())).isCancelled()) {
                world.setTypeAndData(blockposition, this.a.getCarried(), 3);
                this.a.setCarried(Blocks.AIR.getBlockData());
                }
                // CraftBukkit end
            }

        }

        private boolean a(World world, BlockPosition blockposition, Block block, Block block1, Block block2) {
            return !block.canPlace(world, blockposition) ? false : (block1.getMaterial() != Material.AIR ? false : (block2.getMaterial() == Material.AIR ? false : block2.d()));
        }
    }

    static class PathfinderGoalPlayerWhoLookedAtTarget extends PathfinderGoalNearestAttackableTarget {

        private EntityHuman g;
        private int h;
        private int i;
        private EntityEnderman j;

        public PathfinderGoalPlayerWhoLookedAtTarget(EntityEnderman entityenderman) {
            super(entityenderman, EntityHuman.class, true);
            this.j = entityenderman;
        }

        public boolean a() {
            double d0 = this.f();
            List list = this.e.world.a(EntityHuman.class, this.e.getBoundingBox().grow(d0, 4.0D, d0), this.c);

            Collections.sort(list, this.b);
            if (list.isEmpty()) {
                return false;
            } else {
                this.g = (EntityHuman) list.get(0);
                return true;
            }
        }

        public void c() {
            this.h = 5;
            this.i = 0;
        }

        public void d() {
            this.g = null;
            this.j.a(false);
            AttributeInstance attributeinstance = this.j.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

            attributeinstance.c(EntityEnderman.b);
            super.d();
        }

        public boolean b() {
            if (this.g != null) {
                if (!this.j.c(this.g)) {
                    return false;
                } else {
                    this.j.bm = true;
                    this.j.a(this.g, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return super.b();
            }
        }

        public void e() {
            if (this.g != null) {
                if (--this.h <= 0) {
                    this.d = this.g;
                    this.g = null;
                    super.c();
                    this.j.makeSound("mob.endermen.stare", 1.0F, 1.0F);
                    this.j.a(true);
                    AttributeInstance attributeinstance = this.j.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

                    attributeinstance.b(EntityEnderman.b);
                }
            } else {
                if (this.d != null) {
                    if (this.d instanceof EntityHuman && this.j.c((EntityHuman) this.d)) {
                        if (this.d.h(this.j) < 16.0D) {
                            this.j.n();
                        }

                        this.i = 0;
                    } else if (this.d.h(this.j) > 256.0D && this.i++ >= 30 && this.j.b((Entity) this.d)) {
                        this.i = 0;
                    }
                }

                super.e();
            }

        }
    }
}
