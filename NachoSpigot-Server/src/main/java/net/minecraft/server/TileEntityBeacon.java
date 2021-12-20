package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

// PaperSpigot start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.destroystokyo.paper.event.block.BeaconEffectEvent;
// PaperSpigot end

import java.util.List;

public class TileEntityBeacon extends TileEntityContainer implements IUpdatePlayerListBox, IInventory {

    public static final MobEffectList[][] a = new MobEffectList[][]{{MobEffectList.FASTER_MOVEMENT, MobEffectList.FASTER_DIG}, {MobEffectList.RESISTANCE, MobEffectList.JUMP}, {MobEffectList.INCREASE_DAMAGE}, {MobEffectList.REGENERATION}};
    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    // private final List<TileEntityBeacon.BeaconColorTracker> f = Lists.newArrayList();
    private boolean i;
    private int j = -1;
    private int k;
    private int l;
    private ItemStack inventorySlot;
    private String n;
    // CraftBukkit start - add fields and methods
    private int maxStack = MAX_STACK;
	
    public TileEntityBeacon() {
    }

    // NextSpigot - Start
    public boolean isEnabled() {
        return i;
    }

    public void setEnabled(boolean state) {
        this.i = state;
    }

    public int getLevel() {
        return j;
    }

    public void setLevel(int newLevel) {
        this.j = newLevel;
    }
    // NextSpigot - end

    public ItemStack[] getContents() {
        return new ItemStack[]{this.inventorySlot};
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    // CraftBukkit end

    public void c() {
        if (this.world.getTime() % 80L == 0L) {
            this.m();
        }

    }

    public void m() {
        this.B();
        this.A();
    }

    private void A() {
        if (isEnabled() && getLevel() > 0 && !this.world.isClientSide && this.k > 0) {
            double radius = getLevel() * 10 + 10;
            byte b0 = 0;

            if (getLevel() >= 4 && this.k == this.l) {
                b0 = 1;
            }

            int posX = this.position.getX();
            int posY = this.position.getY();
            int posZ = this.position.getZ();
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(posX, posY, posZ, posX + 1, posY + 1, posZ + 1)
                    .grow(radius, radius, radius).a(0.0D, this.world.getHeight(), 0.0D);

            List<EntityHuman> list = this.world.a(EntityHuman.class, axisalignedbb);

            // PaperSpigot start
            org.bukkit.block.Block block = world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
            PotionEffect primaryEffect = new PotionEffect(PotionEffectType.getById(this.k), 180, b0, true, true);
            // PaperSpigot end

            for (EntityHuman entityhuman : list) {
                // PaperSpigot start - BeaconEffectEvent
                BeaconEffectEvent event = new BeaconEffectEvent(block, primaryEffect, (Player) entityhuman.getBukkitEntity(), true);
                if (CraftEventFactory.callEvent(event).isCancelled()) continue;

                PotionEffect effect = event.getEffect();
                entityhuman.addEffect(new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()));
                // PaperSpigot end
            }

            if (getLevel() >= 4 && this.k != this.l && this.l > 0) {
                PotionEffect secondaryEffect = new PotionEffect(PotionEffectType.getById(this.l), 180, 0, true, true); // PaperSpigot

                for (EntityHuman entityhuman : list) {
                    // PaperSpigot start - BeaconEffectEvent
                    BeaconEffectEvent event = new BeaconEffectEvent(block, secondaryEffect, (Player) entityhuman.getBukkitEntity(), false);
                    if (CraftEventFactory.callEvent(event).isCancelled()) continue;

                    PotionEffect effect = event.getEffect();
                    entityhuman.addEffect(new MobEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()));
                    // PaperSpigot end
                }
            }
        }

    }

    private void B() {
        int prevLevel = getLevel();
        int posX = this.position.getX();
        int posY = this.position.getY();
        int posZ = this.position.getZ();
        setLevel(0);
        setEnabled(true);

        BlockPosition.MutableBlockPosition mutableBlockPosition = new BlockPosition.MutableBlockPosition();
        for (int y = posY + 1; y < 256; ++y) {
            Block block = this.world
                    .getType(mutableBlockPosition.setValues(posX, y, posZ)) // Nacho - deobfuscate setValues
                    .getBlock();
            if (block != Blocks.STAINED_GLASS && block != Blocks.STAINED_GLASS_PANE
                    && block.p() >= 15 && block != Blocks.BEDROCK) {
                setEnabled(false);
                break;
			}
        }

        if (isEnabled()) {
            for (int layer = 1; layer <= 4; setLevel(layer++)) {
                int layerY = posY - layer;

                if (layerY < 0) {
                    break;
                }

                boolean hasLayer = true;
                for (int layerX = posX - layer; layerX <= posX + layer && hasLayer; ++layerX) {
                    for (int layerZ = posZ - layer; layerZ <= posZ + layer; ++layerZ) {
                        Block block = this.world.getType(new BlockPosition(layerX, layerY, layerZ)).getBlock();

                        if (block != Blocks.EMERALD_BLOCK && block != Blocks.GOLD_BLOCK && block != Blocks.DIAMOND_BLOCK && block != Blocks.IRON_BLOCK) {
                            hasLayer = false;
                            break;
                        }
                    }
                }

                if (!hasLayer) {
                    break;
                }
            }

            if (getLevel() == 0) {
                setEnabled(false);
            }
        }

        if (!this.world.isClientSide && getLevel() == 4 && prevLevel < getLevel()) {
            AxisAlignedBB bb = new AxisAlignedBB(posX, posY, posZ, posX, posY - 4, posZ)
                    .grow(10.0D, 5.0D, 10.0D);

                for (EntityHuman entityhuman : this.world.a(EntityHuman.class, bb)) {
                entityhuman.b(AchievementList.K);
            }
        }

    }

    public Packet getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.b(nbttagcompound);
        return new PacketPlayOutTileEntityData(this.position, 3, nbttagcompound);
    }

    private int h(int i) {
        if (i >= 0 && i < MobEffectList.byId.length && MobEffectList.byId[i] != null) {
            MobEffectList mobeffectlist = MobEffectList.byId[i];

            return mobeffectlist != MobEffectList.FASTER_MOVEMENT && mobeffectlist != MobEffectList.FASTER_DIG && mobeffectlist != MobEffectList.RESISTANCE && mobeffectlist != MobEffectList.JUMP && mobeffectlist != MobEffectList.INCREASE_DAMAGE && mobeffectlist != MobEffectList.REGENERATION ? 0 : i;
        } else {
            return 0;
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.k = this.h(nbttagcompound.getInt("Primary"));
        this.l = this.h(nbttagcompound.getInt("Secondary"));
        setLevel(nbttagcompound.getInt("Levels"));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Primary", this.k);
        nbttagcompound.setInt("Secondary", this.l);
        nbttagcompound.setInt("Levels", getLevel());
    }

    public int getSize() {
        return 1;
    }

    public ItemStack getItem(int i) {
        return i == 0 ? this.inventorySlot : null;
    }

    public ItemStack splitStack(int i, int j) {
        if (i == 0 && this.inventorySlot != null) {
            if (j >= this.inventorySlot.count) {
                ItemStack itemstack = this.inventorySlot;

                this.inventorySlot = null;
                return itemstack;
            } else {
                this.inventorySlot.count -= j;
                return new ItemStack(this.inventorySlot.getItem(), j, this.inventorySlot.getData());
            }
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (i == 0 && this.inventorySlot != null) {
            ItemStack itemstack = this.inventorySlot;

            this.inventorySlot = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i == 0) {
            this.inventorySlot = itemstack;
        }

    }

    public String getName() {
        return this.hasCustomName() ? this.n : "container.beacon";
    }

    public boolean hasCustomName() {
        return this.n != null && this.n.length() > 0;
    }

    public void a(String s) {
        this.n = s;
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }
	
	public void setMaxStackSize(int size) {
        maxStack = size;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.e((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {
    }

    public void closeContainer(EntityHuman entityhuman) {
	}

    public boolean b(int i, ItemStack itemstack) {
        return itemstack.getItem() == Items.EMERALD || itemstack.getItem() == Items.DIAMOND || itemstack.getItem() == Items.GOLD_INGOT || itemstack.getItem() == Items.IRON_INGOT;
    }

    public String getContainerName() {
        return "minecraft:beacon";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerBeacon(playerinventory, this);
    }

    public int getProperty(int i) {
        switch (i) {
        case 0:
            return getLevel();

        case 1:
            return this.k;

        case 2:
            return this.l;

        default:
            return 0;
        }
    }

    public void b(int i, int j) {
        switch (i) {
        case 0:
                setLevel(j);
                break;

        case 1:
                this.k = this.h(j);
                break;

        case 2:
                this.l = this.h(j);
        }

    }

    public int g() {
        return 3;
    }

    public void l() {
        this.inventorySlot = null;
    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.m();
            return true;
        } else {
            return super.c(i, j);
        }
    }

//    public static class BeaconColorTracker {
//
//        private final float[] a;
//        private int b;
//
//        public BeaconColorTracker(float[] afloat) {
//            this.a = afloat;
//            this.b = 1;
//        }
//
//        protected void a() {
//            ++this.b;
//        }
//
//        public float[] b() {
//            return this.a;
//        }
//    }
}
