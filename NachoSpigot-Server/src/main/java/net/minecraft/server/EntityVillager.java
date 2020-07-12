package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import org.bukkit.craftbukkit.entity.CraftVillager; // CraftBukkit

public class EntityVillager extends EntityAgeable implements IMerchant, NPC {

    private int profession;
    private boolean bo;
    private boolean bp;
    Village village;
    private EntityHuman tradingPlayer;
    private MerchantRecipeList br;
    private int bs;
    private boolean bt;
    private boolean bu;
    private int riches;
    private String bw;
    private int bx;
    private int by;
    private boolean bz;
    private boolean bA;
    public InventorySubcontainer inventory;
    private static final EntityVillager.IMerchantRecipeOption[][][][] bC = new EntityVillager.IMerchantRecipeOption[][][][] { { { { new EntityVillager.MerchantRecipeOptionBuy(Items.WHEAT, new EntityVillager.MerchantOptionRandomRange(18, 22)), new EntityVillager.MerchantRecipeOptionBuy(Items.POTATO, new EntityVillager.MerchantOptionRandomRange(15, 19)), new EntityVillager.MerchantRecipeOptionBuy(Items.CARROT, new EntityVillager.MerchantOptionRandomRange(15, 19)), new EntityVillager.MerchantRecipeOptionSell(Items.BREAD, new EntityVillager.MerchantOptionRandomRange(-4, -2))}, { new EntityVillager.MerchantRecipeOptionBuy(Item.getItemOf(Blocks.PUMPKIN), new EntityVillager.MerchantOptionRandomRange(8, 13)), new EntityVillager.MerchantRecipeOptionSell(Items.PUMPKIN_PIE, new EntityVillager.MerchantOptionRandomRange(-3, -2))}, { new EntityVillager.MerchantRecipeOptionBuy(Item.getItemOf(Blocks.MELON_BLOCK), new EntityVillager.MerchantOptionRandomRange(7, 12)), new EntityVillager.MerchantRecipeOptionSell(Items.APPLE, new EntityVillager.MerchantOptionRandomRange(-5, -7))}, { new EntityVillager.MerchantRecipeOptionSell(Items.COOKIE, new EntityVillager.MerchantOptionRandomRange(-6, -10)), new EntityVillager.MerchantRecipeOptionSell(Items.CAKE, new EntityVillager.MerchantOptionRandomRange(1, 1))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.STRING, new EntityVillager.MerchantOptionRandomRange(15, 20)), new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionProcess(Items.FISH, new EntityVillager.MerchantOptionRandomRange(6, 6), Items.COOKED_FISH, new EntityVillager.MerchantOptionRandomRange(6, 6))}, { new EntityVillager.MerchantRecipeOptionEnchant(Items.FISHING_ROD, new EntityVillager.MerchantOptionRandomRange(7, 8))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Item.getItemOf(Blocks.WOOL), new EntityVillager.MerchantOptionRandomRange(16, 22)), new EntityVillager.MerchantRecipeOptionSell(Items.SHEARS, new EntityVillager.MerchantOptionRandomRange(3, 4))}, { new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 0), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 1), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 2), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 3), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 4), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 5), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 6), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 7), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 8), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 9), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 10), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 11), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 12), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 13), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 14), new EntityVillager.MerchantOptionRandomRange(1, 2)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Item.getItemOf(Blocks.WOOL), 1, 15), new EntityVillager.MerchantOptionRandomRange(1, 2))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.STRING, new EntityVillager.MerchantOptionRandomRange(15, 20)), new EntityVillager.MerchantRecipeOptionSell(Items.ARROW, new EntityVillager.MerchantOptionRandomRange(-12, -8))}, { new EntityVillager.MerchantRecipeOptionSell(Items.BOW, new EntityVillager.MerchantOptionRandomRange(2, 3)), new EntityVillager.MerchantRecipeOptionProcess(Item.getItemOf(Blocks.GRAVEL), new EntityVillager.MerchantOptionRandomRange(10, 10), Items.FLINT, new EntityVillager.MerchantOptionRandomRange(6, 10))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.PAPER, new EntityVillager.MerchantOptionRandomRange(24, 36)), new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionBuy(Items.BOOK, new EntityVillager.MerchantOptionRandomRange(8, 10)), new EntityVillager.MerchantRecipeOptionSell(Items.COMPASS, new EntityVillager.MerchantOptionRandomRange(10, 12)), new EntityVillager.MerchantRecipeOptionSell(Item.getItemOf(Blocks.BOOKSHELF), new EntityVillager.MerchantOptionRandomRange(3, 4))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.WRITTEN_BOOK, new EntityVillager.MerchantOptionRandomRange(2, 2)), new EntityVillager.MerchantRecipeOptionSell(Items.CLOCK, new EntityVillager.MerchantOptionRandomRange(10, 12)), new EntityVillager.MerchantRecipeOptionSell(Item.getItemOf(Blocks.GLASS), new EntityVillager.MerchantOptionRandomRange(-5, -3))}, { new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionBook()}, { new EntityVillager.MerchantRecipeOptionSell(Items.NAME_TAG, new EntityVillager.MerchantOptionRandomRange(20, 22))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.ROTTEN_FLESH, new EntityVillager.MerchantOptionRandomRange(36, 40)), new EntityVillager.MerchantRecipeOptionBuy(Items.GOLD_INGOT, new EntityVillager.MerchantOptionRandomRange(8, 10))}, { new EntityVillager.MerchantRecipeOptionSell(Items.REDSTONE, new EntityVillager.MerchantOptionRandomRange(-4, -1)), new EntityVillager.MerchantRecipeOptionSell(new ItemStack(Items.DYE, 1, EnumColor.BLUE.getInvColorIndex()), new EntityVillager.MerchantOptionRandomRange(-2, -1))}, { new EntityVillager.MerchantRecipeOptionSell(Items.ENDER_EYE, new EntityVillager.MerchantOptionRandomRange(7, 11)), new EntityVillager.MerchantRecipeOptionSell(Item.getItemOf(Blocks.GLOWSTONE), new EntityVillager.MerchantOptionRandomRange(-3, -1))}, { new EntityVillager.MerchantRecipeOptionSell(Items.EXPERIENCE_BOTTLE, new EntityVillager.MerchantOptionRandomRange(3, 11))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_HELMET, new EntityVillager.MerchantOptionRandomRange(4, 6))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(10, 14))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(16, 19))}, { new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_BOOTS, new EntityVillager.MerchantOptionRandomRange(5, 7)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_LEGGINGS, new EntityVillager.MerchantOptionRandomRange(9, 11)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_HELMET, new EntityVillager.MerchantOptionRandomRange(5, 7)), new EntityVillager.MerchantRecipeOptionSell(Items.CHAINMAIL_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(11, 15))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.IRON_AXE, new EntityVillager.MerchantOptionRandomRange(6, 8))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_SWORD, new EntityVillager.MerchantOptionRandomRange(9, 10))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_SWORD, new EntityVillager.MerchantOptionRandomRange(12, 15)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_AXE, new EntityVillager.MerchantOptionRandomRange(9, 12))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_SHOVEL, new EntityVillager.MerchantOptionRandomRange(5, 7))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.IRON_INGOT, new EntityVillager.MerchantOptionRandomRange(7, 9)), new EntityVillager.MerchantRecipeOptionEnchant(Items.IRON_PICKAXE, new EntityVillager.MerchantOptionRandomRange(9, 11))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.DIAMOND, new EntityVillager.MerchantOptionRandomRange(3, 4)), new EntityVillager.MerchantRecipeOptionEnchant(Items.DIAMOND_PICKAXE, new EntityVillager.MerchantOptionRandomRange(12, 15))}}}, { { { new EntityVillager.MerchantRecipeOptionBuy(Items.PORKCHOP, new EntityVillager.MerchantOptionRandomRange(14, 18)), new EntityVillager.MerchantRecipeOptionBuy(Items.CHICKEN, new EntityVillager.MerchantOptionRandomRange(14, 18))}, { new EntityVillager.MerchantRecipeOptionBuy(Items.COAL, new EntityVillager.MerchantOptionRandomRange(16, 24)), new EntityVillager.MerchantRecipeOptionSell(Items.COOKED_PORKCHOP, new EntityVillager.MerchantOptionRandomRange(-7, -5)), new EntityVillager.MerchantRecipeOptionSell(Items.COOKED_CHICKEN, new EntityVillager.MerchantOptionRandomRange(-8, -6))}}, { { new EntityVillager.MerchantRecipeOptionBuy(Items.LEATHER, new EntityVillager.MerchantOptionRandomRange(9, 12)), new EntityVillager.MerchantRecipeOptionSell(Items.LEATHER_LEGGINGS, new EntityVillager.MerchantOptionRandomRange(2, 4))}, { new EntityVillager.MerchantRecipeOptionEnchant(Items.LEATHER_CHESTPLATE, new EntityVillager.MerchantOptionRandomRange(7, 12))}, { new EntityVillager.MerchantRecipeOptionSell(Items.SADDLE, new EntityVillager.MerchantOptionRandomRange(8, 10))}}}};

    public EntityVillager(World world) {
        this(world, 0);
    }

    public EntityVillager(World world, int i) {
        super(world);
        this.inventory = new InventorySubcontainer("Items", false, 8, (CraftVillager) this.getBukkitEntity()); // CraftBukkit add argument
        this.setProfession(i);
        this.setSize(0.6F, 1.8F);
        ((Navigation) this.getNavigation()).b(true);
        ((Navigation) this.getNavigation()).a(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.goalSelector.a(1, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
        this.goalSelector.a(2, new PathfinderGoalMoveIndoors(this));
        this.goalSelector.a(3, new PathfinderGoalRestrictOpenDoor(this));
        this.goalSelector.a(4, new PathfinderGoalOpenDoor(this, true));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 0.6D));
        this.goalSelector.a(6, new PathfinderGoalMakeLove(this));
        this.goalSelector.a(7, new PathfinderGoalTakeFlower(this));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(9, new PathfinderGoalInteractVillagers(this));
        this.goalSelector.a(9, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.j(true);
    }

    private void cv() {
        if (!this.bA) {
            this.bA = true;
            if (this.isBaby()) {
                this.goalSelector.a(8, new PathfinderGoalPlay(this, 0.32D));
            } else if (this.getProfession() == 0) {
                this.goalSelector.a(6, new PathfinderGoalVillagerFarm(this, 0.6D));
            }

        }
    }

    protected void n() {
        if (this.getProfession() == 0) {
            this.goalSelector.a(8, new PathfinderGoalVillagerFarm(this, 0.6D));
        }

        super.n();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
    }

    protected void E() {
        if (--this.profession <= 0) {
            BlockPosition blockposition = new BlockPosition(this);

            this.world.ae().a(blockposition);
            this.profession = 70 + this.random.nextInt(50);
            this.village = this.world.ae().getClosestVillage(blockposition, 32);
            if (this.village == null) {
                this.cj();
            } else {
                BlockPosition blockposition1 = this.village.a();

                this.a(blockposition1, (int) ((float) this.village.b() * 1.0F));
                if (this.bz) {
                    this.bz = false;
                    this.village.b(5);
                }
            }
        }

        if (!this.co() && this.bs > 0) {
            --this.bs;
            if (this.bs <= 0) {
                if (this.bt) {
                    Iterator iterator = this.br.iterator();

                    while (iterator.hasNext()) {
                        MerchantRecipe merchantrecipe = (MerchantRecipe) iterator.next();

                        if (merchantrecipe.h()) {
                            merchantrecipe.a(this.random.nextInt(6) + this.random.nextInt(6) + 2);
                        }
                    }

                    this.cw();
                    this.bt = false;
                    if (this.village != null && this.bw != null) {
                        this.world.broadcastEntityEffect(this, (byte) 14);
                        this.village.a(this.bw, 1);
                    }
                }

                this.addEffect(new MobEffect(MobEffectList.REGENERATION.id, 200, 0));
            }
        }

        super.E();
    }

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();
        boolean flag = itemstack != null && itemstack.getItem() == Items.SPAWN_EGG;

        if (!flag && this.isAlive() && !this.co() && !this.isBaby()) {
            if (!this.world.isClientSide && (this.br == null || this.br.size() > 0)) {
                this.a_(entityhuman);
                entityhuman.openTrade(this);
            }

            entityhuman.b(StatisticList.F);
            return true;
        } else {
            return super.a(entityhuman);
        }
    }

    protected void h() {
        super.h();
        this.datawatcher.a(16, Integer.valueOf(0));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Profession", this.getProfession());
        nbttagcompound.setInt("Riches", this.riches);
        nbttagcompound.setInt("Career", this.bx);
        nbttagcompound.setInt("CareerLevel", this.by);
        nbttagcompound.setBoolean("Willing", this.bu);
        if (this.br != null) {
            nbttagcompound.set("Offers", this.br.a());
        }

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (itemstack != null) {
                nbttaglist.add(itemstack.save(new NBTTagCompound()));
            }
        }

        nbttagcompound.set("Inventory", nbttaglist);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setProfession(nbttagcompound.getInt("Profession"));
        this.riches = nbttagcompound.getInt("Riches");
        this.bx = nbttagcompound.getInt("Career");
        this.by = nbttagcompound.getInt("CareerLevel");
        this.bu = nbttagcompound.getBoolean("Willing");
        if (nbttagcompound.hasKeyOfType("Offers", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Offers");

            this.br = new MerchantRecipeList(nbttagcompound1);
        }

        NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.createStack(nbttaglist.get(i));

            if (itemstack != null) {
                this.inventory.a(itemstack);
            }
        }

        this.j(true);
        this.cv();
    }

    protected boolean isTypeNotPersistent() {
        return false;
    }

    protected String z() {
        return this.co() ? "mob.villager.haggle" : "mob.villager.idle";
    }

    protected String bo() {
        return "mob.villager.hit";
    }

    protected String bp() {
        return "mob.villager.death";
    }

    public void setProfession(int i) {
        this.datawatcher.watch(16, Integer.valueOf(i));
    }

    public int getProfession() {
        return Math.max(this.datawatcher.getInt(16) % 5, 0);
    }

    public boolean cm() {
        return this.bo;
    }

    public void l(boolean flag) {
        this.bo = flag;
    }

    public void m(boolean flag) {
        this.bp = flag;
    }

    public boolean cn() {
        return this.bp;
    }

    public void b(EntityLiving entityliving) {
        super.b(entityliving);
        if (this.village != null && entityliving != null) {
            this.village.a(entityliving);
            if (entityliving instanceof EntityHuman) {
                byte b0 = -1;

                if (this.isBaby()) {
                    b0 = -3;
                }

                this.village.a(entityliving.getName(), b0);
                if (this.isAlive()) {
                    this.world.broadcastEntityEffect(this, (byte) 13);
                }
            }
        }

    }

    public void die(DamageSource damagesource) {
        if (this.village != null) {
            Entity entity = damagesource.getEntity();

            if (entity != null) {
                if (entity instanceof EntityHuman) {
                    this.village.a(entity.getName(), -2);
                } else if (entity instanceof IMonster) {
                    this.village.h();
                }
            } else {
                EntityHuman entityhuman = this.world.findNearbyPlayer(this, 16.0D);

                if (entityhuman != null) {
                    this.village.h();
                }
            }
        }

        super.die(damagesource);
    }

    public void a_(EntityHuman entityhuman) {
        this.tradingPlayer = entityhuman;
    }

    public EntityHuman v_() {
        return this.tradingPlayer;
    }

    public boolean co() {
        return this.tradingPlayer != null;
    }

    public boolean n(boolean flag) {
        if (!this.bu && flag && this.cr()) {
            boolean flag1 = false;

            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (itemstack != null) {
                    if (itemstack.getItem() == Items.BREAD && itemstack.count >= 3) {
                        flag1 = true;
                        this.inventory.splitStack(i, 3);
                    } else if ((itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT) && itemstack.count >= 12) {
                        flag1 = true;
                        this.inventory.splitStack(i, 12);
                    }
                }

                if (flag1) {
                    this.world.broadcastEntityEffect(this, (byte) 18);
                    this.bu = true;
                    break;
                }
            }
        }

        return this.bu;
    }

    public void o(boolean flag) {
        this.bu = flag;
    }

    public void a(MerchantRecipe merchantrecipe) {
        merchantrecipe.g();
        this.a_ = -this.w();
        this.makeSound("mob.villager.yes", this.bB(), this.bC());
        int i = 3 + this.random.nextInt(4);

        if (merchantrecipe.e() == 1 || this.random.nextInt(5) == 0) {
            this.bs = 40;
            this.bt = true;
            this.bu = true;
            if (this.tradingPlayer != null) {
                this.bw = this.tradingPlayer.getName();
            } else {
                this.bw = null;
            }

            i += 5;
        }

        if (merchantrecipe.getBuyItem1().getItem() == Items.EMERALD) {
            this.riches += merchantrecipe.getBuyItem1().count;
        }

        if (merchantrecipe.j()) {
            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY + 0.5D, this.locZ, i));
        }

    }

    public void a_(ItemStack itemstack) {
        if (!this.world.isClientSide && this.a_ > -this.w() + 20) {
            this.a_ = -this.w();
            if (itemstack != null) {
                this.makeSound("mob.villager.yes", this.bB(), this.bC());
            } else {
                this.makeSound("mob.villager.no", this.bB(), this.bC());
            }
        }

    }

    public MerchantRecipeList getOffers(EntityHuman entityhuman) {
        if (this.br == null) {
            this.cw();
        }

        return this.br;
    }

    private void cw() {
        EntityVillager.IMerchantRecipeOption[][][] aentityvillager_imerchantrecipeoption = EntityVillager.bC[this.getProfession()];

        if (this.bx != 0 && this.by != 0) {
            ++this.by;
        } else {
            this.bx = this.random.nextInt(aentityvillager_imerchantrecipeoption.length) + 1;
            this.by = 1;
        }

        if (this.br == null) {
            this.br = new MerchantRecipeList();
        }

        int i = this.bx - 1;
        int j = this.by - 1;
        EntityVillager.IMerchantRecipeOption[][] aentityvillager_imerchantrecipeoption1 = aentityvillager_imerchantrecipeoption[i];

        if (j >= 0 && j < aentityvillager_imerchantrecipeoption1.length) {
            EntityVillager.IMerchantRecipeOption[] aentityvillager_imerchantrecipeoption2 = aentityvillager_imerchantrecipeoption1[j];
            EntityVillager.IMerchantRecipeOption[] aentityvillager_imerchantrecipeoption3 = aentityvillager_imerchantrecipeoption2;
            int k = aentityvillager_imerchantrecipeoption2.length;

            for (int l = 0; l < k; ++l) {
                EntityVillager.IMerchantRecipeOption entityvillager_imerchantrecipeoption = aentityvillager_imerchantrecipeoption3[l];

                entityvillager_imerchantrecipeoption.a(this.br, this.random);
            }
        }

    }

    public IChatBaseComponent getScoreboardDisplayName() {
        String s = this.getCustomName();

        if (s != null && s.length() > 0) {
            ChatComponentText chatcomponenttext = new ChatComponentText(s);

            chatcomponenttext.getChatModifier().setChatHoverable(this.aQ());
            chatcomponenttext.getChatModifier().setInsertion(this.getUniqueID().toString());
            return chatcomponenttext;
        } else {
            if (this.br == null) {
                this.cw();
            }

            String s1 = null;

            switch (this.getProfession()) {
            case 0:
                if (this.bx == 1) {
                    s1 = "farmer";
                } else if (this.bx == 2) {
                    s1 = "fisherman";
                } else if (this.bx == 3) {
                    s1 = "shepherd";
                } else if (this.bx == 4) {
                    s1 = "fletcher";
                }
                break;

            case 1:
                s1 = "librarian";
                break;

            case 2:
                s1 = "cleric";
                break;

            case 3:
                if (this.bx == 1) {
                    s1 = "armor";
                } else if (this.bx == 2) {
                    s1 = "weapon";
                } else if (this.bx == 3) {
                    s1 = "tool";
                }
                break;

            case 4:
                if (this.bx == 1) {
                    s1 = "butcher";
                } else if (this.bx == 2) {
                    s1 = "leather";
                }
            }

            if (s1 != null) {
                ChatMessage chatmessage = new ChatMessage("entity.Villager." + s1, new Object[0]);

                chatmessage.getChatModifier().setChatHoverable(this.aQ());
                chatmessage.getChatModifier().setInsertion(this.getUniqueID().toString());
                return chatmessage;
            } else {
                return super.getScoreboardDisplayName();
            }
        }
    }

    public float getHeadHeight() {
        float f = 1.62F;

        if (this.isBaby()) {
            f = (float) ((double) f - 0.81D);
        }

        return f;
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, GroupDataEntity groupdataentity) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity);
        this.setProfession(this.world.random.nextInt(5));
        this.cv();
        return groupdataentity;
    }

    public void cp() {
        this.bz = true;
    }

    public EntityVillager b(EntityAgeable entityageable) {
        EntityVillager entityvillager = new EntityVillager(this.world);

        entityvillager.prepare(this.world.E(new BlockPosition(entityvillager)), (GroupDataEntity) null);
        return entityvillager;
    }

    public boolean cb() {
        return false;
    }

    public void onLightningStrike(EntityLightning entitylightning) {
        if (!this.world.isClientSide && !this.dead) {
            EntityWitch entitywitch = new EntityWitch(this.world);

            entitywitch.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            entitywitch.prepare(this.world.E(new BlockPosition(entitywitch)), (GroupDataEntity) null);
            entitywitch.k(this.ce());
            if (this.hasCustomName()) {
                entitywitch.setCustomName(this.getCustomName());
                entitywitch.setCustomNameVisible(this.getCustomNameVisible());
            }

            this.world.addEntity(entitywitch);
            this.die();
        }
    }

    public InventorySubcontainer cq() {
        return this.inventory;
    }

    protected void a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();
        Item item = itemstack.getItem();

        if (this.a(item)) {
            ItemStack itemstack1 = this.inventory.a(itemstack);

            if (itemstack1 == null) {
                entityitem.die();
            } else {
                itemstack.count = itemstack1.count;
            }
        }

    }

    private boolean a(Item item) {
        return item == Items.BREAD || item == Items.POTATO || item == Items.CARROT || item == Items.WHEAT || item == Items.WHEAT_SEEDS;
    }

    public boolean cr() {
        return this.s(1);
    }

    public boolean cs() {
        return this.s(2);
    }

    public boolean ct() {
        boolean flag = this.getProfession() == 0;

        return flag ? !this.s(5) : !this.s(1);
    }

    private boolean s(int i) {
        boolean flag = this.getProfession() == 0;

        for (int j = 0; j < this.inventory.getSize(); ++j) {
            ItemStack itemstack = this.inventory.getItem(j);

            if (itemstack != null) {
                if (itemstack.getItem() == Items.BREAD && itemstack.count >= 3 * i || itemstack.getItem() == Items.POTATO && itemstack.count >= 12 * i || itemstack.getItem() == Items.CARROT && itemstack.count >= 12 * i) {
                    return true;
                }

                if (flag && itemstack.getItem() == Items.WHEAT && itemstack.count >= 9 * i) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean cu() {
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (itemstack != null && (itemstack.getItem() == Items.WHEAT_SEEDS || itemstack.getItem() == Items.POTATO || itemstack.getItem() == Items.CARROT)) {
                return true;
            }
        }

        return false;
    }

    public boolean d(int i, ItemStack itemstack) {
        if (super.d(i, itemstack)) {
            return true;
        } else {
            int j = i - 300;

            if (j >= 0 && j < this.inventory.getSize()) {
                this.inventory.setItem(j, itemstack);
                return true;
            } else {
                return false;
            }
        }
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }

    static class MerchantRecipeOptionProcess implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;
        public ItemStack c;
        public EntityVillager.MerchantOptionRandomRange d;

        public MerchantRecipeOptionProcess(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange, Item item1, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange1) {
            this.a = new ItemStack(item);
            this.b = entityvillager_merchantoptionrandomrange;
            this.c = new ItemStack(item1);
            this.d = entityvillager_merchantoptionrandomrange1;
        }

        public void a(MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            int j = 1;

            if (this.d != null) {
                j = this.d.a(random);
            }

            merchantrecipelist.add(new MerchantRecipe(new ItemStack(this.a.getItem(), i, this.a.getData()), new ItemStack(Items.EMERALD), new ItemStack(this.c.getItem(), j, this.c.getData())));
        }
    }

    static class MerchantRecipeOptionBook implements EntityVillager.IMerchantRecipeOption {

        public MerchantRecipeOptionBook() {}

        public void a(MerchantRecipeList merchantrecipelist, Random random) {
            Enchantment enchantment = Enchantment.b[random.nextInt(Enchantment.b.length)];
            int i = MathHelper.nextInt(random, enchantment.getStartLevel(), enchantment.getMaxLevel());
            ItemStack itemstack = Items.ENCHANTED_BOOK.a(new WeightedRandomEnchant(enchantment, i));
            int j = 2 + random.nextInt(5 + i * 10) + 3 * i;

            if (j > 64) {
                j = 64;
            }

            merchantrecipelist.add(new MerchantRecipe(new ItemStack(Items.BOOK), new ItemStack(Items.EMERALD, j), itemstack));
        }
    }

    static class MerchantRecipeOptionEnchant implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionEnchant(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = new ItemStack(item);
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            ItemStack itemstack = new ItemStack(Items.EMERALD, i, 0);
            ItemStack itemstack1 = new ItemStack(this.a.getItem(), 1, this.a.getData());

            itemstack1 = EnchantmentManager.a(random, itemstack1, 5 + random.nextInt(15));
            merchantrecipelist.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    static class MerchantRecipeOptionSell implements EntityVillager.IMerchantRecipeOption {

        public ItemStack a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionSell(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = new ItemStack(item);
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public MerchantRecipeOptionSell(ItemStack itemstack, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = itemstack;
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            ItemStack itemstack;
            ItemStack itemstack1;

            if (i < 0) {
                itemstack = new ItemStack(Items.EMERALD, 1, 0);
                itemstack1 = new ItemStack(this.a.getItem(), -i, this.a.getData());
            } else {
                itemstack = new ItemStack(Items.EMERALD, i, 0);
                itemstack1 = new ItemStack(this.a.getItem(), 1, this.a.getData());
            }

            merchantrecipelist.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    static class MerchantRecipeOptionBuy implements EntityVillager.IMerchantRecipeOption {

        public Item a;
        public EntityVillager.MerchantOptionRandomRange b;

        public MerchantRecipeOptionBuy(Item item, EntityVillager.MerchantOptionRandomRange entityvillager_merchantoptionrandomrange) {
            this.a = item;
            this.b = entityvillager_merchantoptionrandomrange;
        }

        public void a(MerchantRecipeList merchantrecipelist, Random random) {
            int i = 1;

            if (this.b != null) {
                i = this.b.a(random);
            }

            merchantrecipelist.add(new MerchantRecipe(new ItemStack(this.a, i, 0), Items.EMERALD));
        }
    }

    interface IMerchantRecipeOption {

        void a(MerchantRecipeList merchantrecipelist, Random random);
    }

    static class MerchantOptionRandomRange extends Tuple<Integer, Integer> {

        public MerchantOptionRandomRange(int i, int j) {
            super(Integer.valueOf(i), Integer.valueOf(j));
        }

        public int a(Random random) {
            return ((Integer) this.a()).intValue() >= ((Integer) this.b()).intValue() ? ((Integer) this.a()).intValue() : ((Integer) this.a()).intValue() + random.nextInt(((Integer) this.b()).intValue() - ((Integer) this.a()).intValue() + 1);
        }
    }
}
