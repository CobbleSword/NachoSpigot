package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;
// CraftBukkit end

public abstract class EntityInsentient extends EntityLiving {

    public int a_;
    protected int b_;
    private ControllerLook lookController;
    protected ControllerMove moveController;
    protected ControllerJump g;
    private EntityAIBodyControl b;
    protected NavigationAbstract navigation;
    public PathfinderGoalSelector goalSelector;
    public PathfinderGoalSelector targetSelector;
    private EntityLiving goalTarget;
    private EntitySenses bk;
    private ItemStack[] equipment = new ItemStack[5];
    public float[] dropChances = new float[5];
    public boolean canPickUpLoot;
    public boolean persistent;
    private boolean bo;
    private Entity bp;
    private NBTTagCompound bq;
    public PathfinderGoalFloat goalFloat; // PaperSpigot

    public EntityInsentient(World world) {
        super(world);
        this.goalSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.targetSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.lookController = new ControllerLook(this);
        this.moveController = new ControllerMove(this);
        this.g = new ControllerJump(this);
        this.b = new EntityAIBodyControl(this);
        this.navigation = this.b(world);
        this.bk = new EntitySenses(this);

        for (int i = 0; i < this.dropChances.length; ++i) {
            this.dropChances[i] = 0.085F;
        }

        // CraftBukkit start - default persistance to type's persistance value
        this.persistent = !isTypeNotPersistent();
        // CraftBukkit end
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.FOLLOW_RANGE).setValue(16.0D);
    }

    protected NavigationAbstract b(World world) {
        return new Navigation(this, world);
    }

    public ControllerLook getControllerLook() {
        return this.lookController;
    }

    public ControllerMove getControllerMove() {
        return this.moveController;
    }

    public ControllerJump getControllerJump() {
        return this.g;
    }

    public NavigationAbstract getNavigation() {
        return this.navigation;
    }

    public EntitySenses getEntitySenses() {
        return this.bk;
    }

    public EntityLiving getGoalTarget() {
        return this.goalTarget;
    }

    public void setGoalTarget(EntityLiving entityliving) {
        // CraftBukkit start - fire event
        setGoalTarget(entityliving, EntityTargetEvent.TargetReason.UNKNOWN, true);
    }

    public void setGoalTarget(EntityLiving entityliving, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (getGoalTarget() == entityliving) return;
        if (fireEvent) {
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN && getGoalTarget() != null && entityliving == null) {
                reason = getGoalTarget().isAlive() ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
            }
            if (reason == EntityTargetEvent.TargetReason.UNKNOWN) {
                world.getServer().getLogger().log(java.util.logging.Level.WARNING, "Unknown target reason, please report on the issue tracker", new Exception());
            }
            CraftLivingEntity ctarget = null;
            if (entityliving != null) {
                ctarget = (CraftLivingEntity) entityliving.getBukkitEntity();
            }
            EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.getBukkitEntity(), ctarget, reason);
            world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            if (event.getTarget() != null) {
                entityliving = ((CraftLivingEntity) event.getTarget()).getHandle();
            } else {
                entityliving = null;
            }
        }
        this.goalTarget = entityliving;
        // CraftBukkit end
    }

    public boolean a(Class<? extends EntityLiving> oclass) {
        return oclass != EntityGhast.class;
    }

    public void v() {}

    protected void h() {
        super.h();
        this.datawatcher.a(15, Byte.valueOf((byte) 0));
    }

    public int w() {
        return 80;
    }

    public void x() {
        String s = this.z();

        if (s != null) {
            this.makeSound(s, this.bB(), this.bC());
        }

    }

    public void K() {
        super.K();
        this.world.methodProfiler.a("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.a_++) {
            this.a_ = -this.w();
            this.x();
        }

        this.world.methodProfiler.b();
    }

    protected int getExpValue(EntityHuman entityhuman) {
        if (this.b_ > 0) {
            int i = this.b_;
            ItemStack[] aitemstack = this.getEquipment();

            for (int j = 0; j < aitemstack.length; ++j) {
                if (aitemstack[j] != null && this.dropChances[j] <= 1.0F) {
                    i += 1 + this.random.nextInt(3);
                }
            }

            return i;
        } else {
            return this.b_;
        }
    }

    public void y() {
        if (this.world.isClientSide) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = 10.0D;

                this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width - d0 * d3, this.locY + (double) (this.random.nextFloat() * this.length) - d1 * d3, this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width - d2 * d3, d0, d1, d2, new int[0]);
            }
        } else {
            this.world.broadcastEntityEffect(this, (byte) 20);
        }

    }

    public void t_() {
        super.t_();
        if (!this.world.isClientSide) {
            this.ca();
        }

    }

    protected float h(float f, float f1) {
        this.b.a();
        return f1;
    }

    protected String z() {
        return null;
    }

    protected Item getLoot() {
        return null;
    }

    protected ItemStack headDrop = null; // CraftBukkit
    protected void dropDeathLoot(boolean flag, int i) {
        Item item = this.getLoot();

        if (item != null) {
            int j = this.random.nextInt(3);

            if (i > 0) {
                j += this.random.nextInt(i + 1);
            }

            for (int k = 0; k < j; ++k) {
                this.a(item, 1);
            }
        }

        // CraftBukkit start
        if (headDrop != null) {
            this.a(headDrop, 0.0F);
            headDrop = null;
        }
        // CraftBukkit end
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("CanPickUpLoot", this.bY());
        nbttagcompound.setBoolean("PersistenceRequired", this.persistent);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (int i = 0; i < this.equipment.length; ++i) {
            nbttagcompound1 = new NBTTagCompound();
            if (this.equipment[i] != null) {
                this.equipment[i].save(nbttagcompound1);
            }

            nbttaglist.add(nbttagcompound1);
        }

        nbttagcompound.set("Equipment", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        for (int j = 0; j < this.dropChances.length; ++j) {
            nbttaglist1.add(new NBTTagFloat(this.dropChances[j]));
        }

        nbttagcompound.set("DropChances", nbttaglist1);
        nbttagcompound.setBoolean("Leashed", this.bo);
        if (this.bp != null) {
            nbttagcompound1 = new NBTTagCompound();
            if (this.bp instanceof EntityLiving) {
                nbttagcompound1.setLong("UUIDMost", this.bp.getUniqueID().getMostSignificantBits());
                nbttagcompound1.setLong("UUIDLeast", this.bp.getUniqueID().getLeastSignificantBits());
            } else if (this.bp instanceof EntityHanging) {
                BlockPosition blockposition = ((EntityHanging) this.bp).getBlockPosition();

                nbttagcompound1.setInt("X", blockposition.getX());
                nbttagcompound1.setInt("Y", blockposition.getY());
                nbttagcompound1.setInt("Z", blockposition.getZ());
            }

            nbttagcompound.set("Leash", nbttagcompound1);
        }

        if (this.ce()) {
            nbttagcompound.setBoolean("NoAI", this.ce());
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        
        // CraftBukkit start - If looting or persistence is false only use it if it was set after we started using it
        if (nbttagcompound.hasKeyOfType("CanPickUpLoot", 1)) {
            boolean data = nbttagcompound.getBoolean("CanPickUpLoot");
            if (isLevelAtLeast(nbttagcompound, 1) || data) {
                this.j(data);
            }
        }

        boolean data = nbttagcompound.getBoolean("PersistenceRequired");
        if (isLevelAtLeast(nbttagcompound, 1) || data) {
            this.persistent = data;
        }
        // CraftBukkit end
        NBTTagList nbttaglist;
        int i;

        if (nbttagcompound.hasKeyOfType("Equipment", 9)) {
            nbttaglist = nbttagcompound.getList("Equipment", 10);

            for (i = 0; i < this.equipment.length; ++i) {
                this.equipment[i] = ItemStack.createStack(nbttaglist.get(i));
            }
        }

        if (nbttagcompound.hasKeyOfType("DropChances", 9)) {
            nbttaglist = nbttagcompound.getList("DropChances", 5);

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.dropChances[i] = nbttaglist.e(i);
            }
        }

        this.bo = nbttagcompound.getBoolean("Leashed");
        if (this.bo && nbttagcompound.hasKeyOfType("Leash", 10)) {
            this.bq = nbttagcompound.getCompound("Leash");
        }

        this.k(nbttagcompound.getBoolean("NoAI"));
    }

    public void n(float f) {
        this.ba = f;
    }

    public void k(float f) {
        super.k(f);
        this.n(f);
    }

    public void m() {
        super.m();
        this.world.methodProfiler.a("looting");
        if (!this.world.isClientSide && this.bY() && !this.aP && this.world.getGameRules().getBoolean("mobGriefing")) {
            List list = this.world.a(EntityItem.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (!entityitem.dead && entityitem.getItemStack() != null && !entityitem.s()) {
                    this.a(entityitem);
                }
            }
        }

        this.world.methodProfiler.b();
    }

    protected void a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        int i = c(itemstack);

        if (i > -1) {
            boolean flag = true;
            ItemStack itemstack1 = this.getEquipment(i);

            if (itemstack1 != null) {
                if (i == 0) {
                    if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword)) {
                        flag = true;
                    } else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword) {
                        ItemSword itemsword = (ItemSword) itemstack.getItem();
                        ItemSword itemsword1 = (ItemSword) itemstack1.getItem();

                        if (itemsword.g() == itemsword1.g()) {
                            flag = itemstack.getData() > itemstack1.getData() || itemstack.hasTag() && !itemstack1.hasTag();
                        } else {
                            flag = itemsword.g() > itemsword1.g();
                        }
                    } else if (itemstack.getItem() instanceof ItemBow && itemstack1.getItem() instanceof ItemBow) {
                        flag = itemstack.hasTag() && !itemstack1.hasTag();
                    } else {
                        flag = false;
                    }
                } else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor)) {
                    flag = true;
                } else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor) {
                    ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
                    ItemArmor itemarmor1 = (ItemArmor) itemstack1.getItem();

                    if (itemarmor.c == itemarmor1.c) {
                        flag = itemstack.getData() > itemstack1.getData() || itemstack.hasTag() && !itemstack1.hasTag();
                    } else {
                        flag = itemarmor.c > itemarmor1.c;
                    }
                } else {
                    flag = false;
                }
            }

            if (flag && this.a(itemstack)) {
                if (itemstack1 != null && this.random.nextFloat() - 0.1F < this.dropChances[i]) {
                    this.a(itemstack1, 0.0F);
                }

                if (itemstack.getItem() == Items.DIAMOND && entityitem.n() != null) {
                    EntityHuman entityhuman = this.world.a(entityitem.n());

                    if (entityhuman != null) {
                        entityhuman.b((Statistic) AchievementList.x);
                    }
                }

                this.setEquipment(i, itemstack);
                this.dropChances[i] = 2.0F;
                this.persistent = true;
                this.receive(entityitem, 1);
                entityitem.die();
            }
        }

    }

    protected boolean a(ItemStack itemstack) {
        return true;
    }

    protected boolean isTypeNotPersistent() {
        return true;
    }

    protected void D() {
        if (this.persistent) {
            this.ticksFarFromPlayer = 0;
        } else {
            EntityHuman entityhuman = this.world.findNearbyPlayerWhoAffectsSpawning(this, -1.0D); // PaperSpigot - Affects Spawning API

            if (entityhuman != null) {
                double d0 = entityhuman.locX - this.locX;
                double d1 = entityhuman.locY - this.locY;
                double d2 = entityhuman.locZ - this.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > this.world.paperSpigotConfig.hardDespawnDistance) { // CraftBukkit - remove isTypeNotPersistent() check // PaperSpigot - custom despawn distances
                    this.die();
                }

                if (this.ticksFarFromPlayer > 600 && this.random.nextInt(800) == 0 && d3 > this.world.paperSpigotConfig.softDespawnDistance) { // CraftBukkit - remove isTypeNotPersistent() check // PaperSpigot - custom despawn distance
                    this.die();
                } else if (d3 < this.world.paperSpigotConfig.softDespawnDistance) { // PaperSpigot - custom despawn distances
                    this.ticksFarFromPlayer = 0;
                }
            }

        }
    }

    protected final void doTick() {
        ++this.ticksFarFromPlayer;
        this.world.methodProfiler.a("checkDespawn");
        this.D();
        this.world.methodProfiler.b();
        // Spigot Start
        if ( this.fromMobSpawner )
        {
            // PaperSpigot start - Allow nerfed mobs to jump
            if (goalFloat != null) {
                if (goalFloat.a()) goalFloat.e();
                this.g.b();
            }
            // PaperSpigot end
            return;
        }
        // Spigot End
        this.world.methodProfiler.a("sensing");
        this.bk.a();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("targetSelector");
        this.targetSelector.a();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("goalSelector");
        this.goalSelector.a();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("navigation");
        this.navigation.k();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("mob tick");
        this.E();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("controls");
        this.world.methodProfiler.a("move");
        this.moveController.c();
        this.world.methodProfiler.c("look");
        this.lookController.a();
        this.world.methodProfiler.c("jump");
        this.g.b();
        this.world.methodProfiler.b();
        this.world.methodProfiler.b();
    }

    protected void E() {}

    public int bQ() {
        return 40;
    }

    public void a(Entity entity, float f, float f1) {
        double d0 = entity.locX - this.locX;
        double d1 = entity.locZ - this.locZ;
        double d2;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            d2 = entityliving.locY + (double) entityliving.getHeadHeight() - (this.locY + (double) this.getHeadHeight());
        } else {
            d2 = (entity.getBoundingBox().b + entity.getBoundingBox().e) / 2.0D - (this.locY + (double) this.getHeadHeight());
        }

        double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float) (MathHelper.b(d1, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
        float f3 = (float) (-(MathHelper.b(d2, d3) * 180.0D / 3.1415927410125732D));

        this.pitch = this.b(this.pitch, f3, f1);
        this.yaw = this.b(this.yaw, f2, f);
    }

    private float b(float f, float f1, float f2) {
        float f3 = MathHelper.g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    public boolean bR() {
        return true;
    }

    public boolean canSpawn() {
        return this.world.a(this.getBoundingBox(), (Entity) this) && this.world.getCubes(this, this.getBoundingBox()).isEmpty() && !this.world.containsLiquid(this.getBoundingBox());
    }

    public int bV() {
        return 4;
    }

    public int aE() {
        if (this.getGoalTarget() == null) {
            return 3;
        } else {
            int i = (int) (this.getHealth() - this.getMaxHealth() * 0.33F);

            i -= (3 - this.world.getDifficulty().a()) * 4;
            if (i < 0) {
                i = 0;
            }

            return i + 3;
        }
    }

    public ItemStack bA() {
        return this.equipment[0];
    }

    public ItemStack getEquipment(int i) {
        return this.equipment[i];
    }

    public ItemStack q(int i) {
        return this.equipment[i + 1];
    }

    public void setEquipment(int i, ItemStack itemstack) {
        this.equipment[i] = itemstack;
    }

    public ItemStack[] getEquipment() {
        return this.equipment;
    }

    protected void dropEquipment(boolean flag, int i) {
        for (int j = 0; j < this.getEquipment().length; ++j) {
            ItemStack itemstack = this.getEquipment(j);
            boolean flag1 = this.dropChances[j] > 1.0F;

            if (itemstack != null && (flag || flag1) && this.random.nextFloat() - (float) i * 0.01F < this.dropChances[j]) {
                if (!flag1 && itemstack.e()) {
                    int k = Math.max(itemstack.j() - 25, 1);
                    int l = itemstack.j() - this.random.nextInt(this.random.nextInt(k) + 1);

                    if (l > k) {
                        l = k;
                    }

                    if (l < 1) {
                        l = 1;
                    }

                    itemstack.setData(l);
                }

                this.a(itemstack, 0.0F);
            }
        }

    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        if (this.random.nextFloat() < 0.15F * difficultydamagescaler.c()) {
            int i = this.random.nextInt(2);
            float f = this.world.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            for (int j = 3; j >= 0; --j) {
                ItemStack itemstack = this.q(j);

                if (j < 3 && this.random.nextFloat() < f) {
                    break;
                }

                if (itemstack == null) {
                    Item item = a(j + 1, i);

                    if (item != null) {
                        this.setEquipment(j + 1, new ItemStack(item));
                    }
                }
            }
        }

    }

    public static int c(ItemStack itemstack) {
        if (itemstack.getItem() != Item.getItemOf(Blocks.PUMPKIN) && itemstack.getItem() != Items.SKULL) {
            if (itemstack.getItem() instanceof ItemArmor) {
                switch (((ItemArmor) itemstack.getItem()).b) {
                case 0:
                    return 4;

                case 1:
                    return 3;

                case 2:
                    return 2;

                case 3:
                    return 1;
                }
            }

            return 0;
        } else {
            return 4;
        }
    }

    public static Item a(int i, int j) {
        switch (i) {
        case 4:
            if (j == 0) {
                return Items.LEATHER_HELMET;
            } else if (j == 1) {
                return Items.GOLDEN_HELMET;
            } else if (j == 2) {
                return Items.CHAINMAIL_HELMET;
            } else if (j == 3) {
                return Items.IRON_HELMET;
            } else if (j == 4) {
                return Items.DIAMOND_HELMET;
            }

        case 3:
            if (j == 0) {
                return Items.LEATHER_CHESTPLATE;
            } else if (j == 1) {
                return Items.GOLDEN_CHESTPLATE;
            } else if (j == 2) {
                return Items.CHAINMAIL_CHESTPLATE;
            } else if (j == 3) {
                return Items.IRON_CHESTPLATE;
            } else if (j == 4) {
                return Items.DIAMOND_CHESTPLATE;
            }

        case 2:
            if (j == 0) {
                return Items.LEATHER_LEGGINGS;
            } else if (j == 1) {
                return Items.GOLDEN_LEGGINGS;
            } else if (j == 2) {
                return Items.CHAINMAIL_LEGGINGS;
            } else if (j == 3) {
                return Items.IRON_LEGGINGS;
            } else if (j == 4) {
                return Items.DIAMOND_LEGGINGS;
            }

        case 1:
            if (j == 0) {
                return Items.LEATHER_BOOTS;
            } else if (j == 1) {
                return Items.GOLDEN_BOOTS;
            } else if (j == 2) {
                return Items.CHAINMAIL_BOOTS;
            } else if (j == 3) {
                return Items.IRON_BOOTS;
            } else if (j == 4) {
                return Items.DIAMOND_BOOTS;
            }

        default:
            return null;
        }
    }

    protected void b(DifficultyDamageScaler difficultydamagescaler) {
        float f = difficultydamagescaler.c();

        if (this.bA() != null && this.random.nextFloat() < 0.25F * f) {
            EnchantmentManager.a(this.random, this.bA(), (int) (5.0F + f * (float) this.random.nextInt(18)));
        }

        for (int i = 0; i < 4; ++i) {
            ItemStack itemstack = this.q(i);

            if (itemstack != null && this.random.nextFloat() < 0.5F * f) {
                EnchantmentManager.a(this.random, itemstack, (int) (5.0F + f * (float) this.random.nextInt(18)));
            }
        }

    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).b(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, 1));
        return groupdataentity;
    }

    public boolean bW() {
        return false;
    }

    public void bX() {
        this.persistent = true;
    }

    public void a(int i, float f) {
        this.dropChances[i] = f;
    }

    public boolean bY() {
        return this.canPickUpLoot;
    }

    public void j(boolean flag) {
        this.canPickUpLoot = flag;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    public final boolean e(EntityHuman entityhuman) {
        if (this.cc() && this.getLeashHolder() == entityhuman) {
            // CraftBukkit start - fire PlayerUnleashEntityEvent
            if (CraftEventFactory.callPlayerUnleashEntityEvent(this, entityhuman).isCancelled()) {
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, this, this.getLeashHolder()));
                return false;
            }
            // CraftBukkit end
            this.unleash(true, !entityhuman.abilities.canInstantlyBuild);
            return true;
        } else {
            ItemStack itemstack = entityhuman.inventory.getItemInHand();

            if (itemstack != null && itemstack.getItem() == Items.LEAD && this.cb()) {
                if (!(this instanceof EntityTameableAnimal) || !((EntityTameableAnimal) this).isTamed()) {
                    // CraftBukkit start - fire PlayerLeashEntityEvent
                    if (CraftEventFactory.callPlayerLeashEntityEvent(this, entityhuman, entityhuman).isCancelled()) {
                        ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, this, this.getLeashHolder()));
                        return false;
                    }
                    // CraftBukkit end
                    this.setLeashHolder(entityhuman, true);
                    --itemstack.count;
                    return true;
                }

                if (((EntityTameableAnimal) this).e((EntityLiving) entityhuman)) {
                    // CraftBukkit start - fire PlayerLeashEntityEvent
                    if (CraftEventFactory.callPlayerLeashEntityEvent(this, entityhuman, entityhuman).isCancelled()) {
                        ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, this, this.getLeashHolder()));
                        return false;
                    }
                    // CraftBukkit end
                    this.setLeashHolder(entityhuman, true);
                    --itemstack.count;
                    return true;
                }
            }

            return this.a(entityhuman) ? true : super.e(entityhuman);
        }
    }

    protected boolean a(EntityHuman entityhuman) {
        return false;
    }

    protected void ca() {
        if (this.bq != null) {
            this.n();
        }

        if (this.bo) {
            if (!this.isAlive()) {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.PLAYER_UNLEASH)); // CraftBukkit
                this.unleash(true, true);
            }

            if (this.bp == null || this.bp.dead) {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.HOLDER_GONE)); // CraftBukkit
                this.unleash(true, true);
            }
        }
    }

    public void unleash(boolean flag, boolean flag1) {
        if (this.bo) {
            this.bo = false;
            this.bp = null;
            if (!this.world.isClientSide && flag1) {
                this.a(Items.LEAD, 1);
            }

            if (!this.world.isClientSide && flag && this.world instanceof WorldServer) {
                ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutAttachEntity(1, this, (Entity) null)));
            }
        }

    }

    public boolean cb() {
        return !this.cc() && !(this instanceof IMonster);
    }

    public boolean cc() {
        return this.bo;
    }

    public Entity getLeashHolder() {
        return this.bp;
    }

    public void setLeashHolder(Entity entity, boolean flag) {
        this.bo = true;
        this.bp = entity;
        if (!this.world.isClientSide && flag && this.world instanceof WorldServer) {
            ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutAttachEntity(1, this, this.bp)));
        }

    }

    private void n() {
        if (this.bo && this.bq != null) {
            if (this.bq.hasKeyOfType("UUIDMost", 4) && this.bq.hasKeyOfType("UUIDLeast", 4)) {
                UUID uuid = new UUID(this.bq.getLong("UUIDMost"), this.bq.getLong("UUIDLeast"));
                List list = this.world.a(EntityLiving.class, this.getBoundingBox().grow(10.0D, 10.0D, 10.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityLiving entityliving = (EntityLiving) iterator.next();

                    if (entityliving.getUniqueID().equals(uuid)) {
                        this.bp = entityliving;
                        break;
                    }
                }
            } else if (this.bq.hasKeyOfType("X", 99) && this.bq.hasKeyOfType("Y", 99) && this.bq.hasKeyOfType("Z", 99)) {
                BlockPosition blockposition = new BlockPosition(this.bq.getInt("X"), this.bq.getInt("Y"), this.bq.getInt("Z"));
                EntityLeash entityleash = EntityLeash.b(this.world, blockposition);

                if (entityleash == null) {
                    entityleash = EntityLeash.a(this.world, blockposition);
                }

                this.bp = entityleash;
            } else {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), UnleashReason.UNKNOWN)); // CraftBukkit
                this.unleash(false, true);
            }
        }

        this.bq = null;
    }

    public boolean d(int i, ItemStack itemstack) {
        int j;

        if (i == 99) {
            j = 0;
        } else {
            j = i - 100 + 1;
            if (j < 0 || j >= this.equipment.length) {
                return false;
            }
        }

        if (itemstack != null && c(itemstack) != j && (j != 4 || !(itemstack.getItem() instanceof ItemBlock))) {
            return false;
        } else {
            this.setEquipment(j, itemstack);
            return true;
        }
    }

    public boolean bM() {
        return super.bM() && !this.ce();
    }

    public void k(boolean flag) {
        this.datawatcher.watch(15, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    public boolean ce() {
        return this.datawatcher.getByte(15) != 0;
    }

    public static enum EnumEntityPositionType {

        ON_GROUND, IN_AIR, IN_WATER;

        private EnumEntityPositionType() {}
    }
}
