package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityLeash extends EntityHanging {

    public EntityLeash(World world) {
        super(world);
    }

    public EntityLeash(World world, BlockPosition blockposition) {
        super(world, blockposition);
        this.setPosition((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D);
        float f = 0.125F;
        float f1 = 0.1875F;
        float f2 = 0.25F;

        this.a(new AxisAlignedBB(this.locX - 0.1875D, this.locY - 0.25D + 0.125D, this.locZ - 0.1875D, this.locX + 0.1875D, this.locY + 0.25D + 0.125D, this.locZ + 0.1875D));
    }

    protected void h() {
        super.h();
    }

    public void setDirection(EnumDirection enumdirection) {}

    public int l() {
        return 9;
    }

    public int m() {
        return 9;
    }

    public float getHeadHeight() {
        return -0.0625F;
    }

    public void b(Entity entity) {}

    public boolean d(NBTTagCompound nbttagcompound) {
        return false;
    }

    public void b(NBTTagCompound nbttagcompound) {}

    public void a(NBTTagCompound nbttagcompound) {}

    public boolean e(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.bA();
        boolean flag = false;
        double d0;
        List list;
        Iterator iterator;
        EntityInsentient entityinsentient;

        if (itemstack != null && itemstack.getItem() == Items.LEAD && !this.world.isClientSide) {
            d0 = 7.0D;
            list = this.world.a(EntityInsentient.class, new AxisAlignedBB(this.locX - d0, this.locY - d0, this.locZ - d0, this.locX + d0, this.locY + d0, this.locZ + d0));
            iterator = list.iterator();

            while (iterator.hasNext()) {
                entityinsentient = (EntityInsentient) iterator.next();
                if (entityinsentient.cc() && entityinsentient.getLeashHolder() == entityhuman) {
                    // CraftBukkit start
                    if (CraftEventFactory.callPlayerLeashEntityEvent(entityinsentient, this, entityhuman).isCancelled()) {
                        ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, entityinsentient, entityinsentient.getLeashHolder()));
                        continue;
                    }
                    // CraftBukkit end
                    entityinsentient.setLeashHolder(this, true);
                    flag = true;
                }
            }
        }

        if (!this.world.isClientSide && !flag) {
            // CraftBukkit start - Move below
            // this.die();
            boolean die = true;
            // CraftBukkit end
            if (true || entityhuman.abilities.canInstantlyBuild) { // CraftBukkit - Process for non-creative as well
                d0 = 7.0D;
                list = this.world.a(EntityInsentient.class, new AxisAlignedBB(this.locX - d0, this.locY - d0, this.locZ - d0, this.locX + d0, this.locY + d0, this.locZ + d0));
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityinsentient = (EntityInsentient) iterator.next();
                    if (entityinsentient.cc() && entityinsentient.getLeashHolder() == this) {
                        // CraftBukkit start
                        if (CraftEventFactory.callPlayerUnleashEntityEvent(entityinsentient, entityhuman).isCancelled()) {
                            die = false;
                            continue;
                        }
                        entityinsentient.unleash(true, !entityhuman.abilities.canInstantlyBuild); // false -> survival mode boolean
                        // CraftBukkit end
                    }
                }
            }
            // CraftBukkit start
            if (die) {
                this.die();
            }
            // CraftBukkit end
        }

        return true;
    }

    public boolean survives() {
        return this.world.getType(this.blockPosition).getBlock() instanceof BlockFence;
    }

    public static EntityLeash a(World world, BlockPosition blockposition) {
        EntityLeash entityleash = new EntityLeash(world, blockposition);

        entityleash.attachedToPlayer = true;
        world.addEntity(entityleash);
        return entityleash;
    }

    public static EntityLeash b(World world, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        List list = world.a(EntityLeash.class, new AxisAlignedBB((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D));
        Iterator iterator = list.iterator();

        EntityLeash entityleash;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entityleash = (EntityLeash) iterator.next();
        } while (!entityleash.getBlockPosition().equals(blockposition));

        return entityleash;
    }
}
