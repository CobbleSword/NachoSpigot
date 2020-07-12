package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
// CraftBukkit end

public class EntityCow extends EntityAnimal {

    public EntityCow(World world) {
        super(world);
        this.setSize(0.9F, 1.3F);
        ((Navigation) this.getNavigation()).a(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, Items.WHEAT, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    protected String z() {
        return "mob.cow.say";
    }

    protected String bo() {
        return "mob.cow.hurt";
    }

    protected String bp() {
        return "mob.cow.hurt";
    }

    protected void a(BlockPosition blockposition, Block block) {
        this.makeSound("mob.cow.step", 0.15F, 1.0F);
    }

    protected float bB() {
        return 0.4F;
    }

    protected Item getLoot() {
        return Items.LEATHER;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        int j = this.random.nextInt(3) + this.random.nextInt(1 + i);

        int k;

        for (k = 0; k < j; ++k) {
            this.a(Items.LEATHER, 1);
        }

        j = this.random.nextInt(3) + 1 + this.random.nextInt(1 + i);

        for (k = 0; k < j; ++k) {
            if (this.isBurning()) {
                this.a(Items.COOKED_BEEF, 1);
            } else {
                this.a(Items.BEEF, 1);
            }
        }

    }

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (itemstack != null && itemstack.getItem() == Items.BUCKET && !entityhuman.abilities.canInstantlyBuild && !this.isBaby()) {
            // CraftBukkit start - Got milk?
            org.bukkit.Location loc = this.getBukkitEntity().getLocation();
            org.bukkit.event.player.PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(entityhuman, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), null, itemstack, Items.MILK_BUCKET);

            if (event.isCancelled()) {
                return false;
            }

            ItemStack result = CraftItemStack.asNMSCopy(event.getItemStack());
            if (--itemstack.count <= 0) {
                entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, result);
            } else if (!entityhuman.inventory.pickup(result)) {
                entityhuman.drop(result, false);
            }
            // CraftBukkit end

            return true;
        } else {
            return super.a(entityhuman);
        }
    }

    public EntityCow b(EntityAgeable entityageable) {
        return new EntityCow(this.world);
    }

    public float getHeadHeight() {
        return this.length;
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
