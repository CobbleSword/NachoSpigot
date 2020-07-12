package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;

// CraftBukkit start
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Painting;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
// CraftBukkit end

public abstract class EntityHanging extends Entity {

    private int c;
    public BlockPosition blockPosition;
    public EnumDirection direction;

    public EntityHanging(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
    }

    public EntityHanging(World world, BlockPosition blockposition) {
        this(world);
        this.blockPosition = blockposition;
    }

    protected void h() {}

    public void setDirection(EnumDirection enumdirection) {
        Validate.notNull(enumdirection);
        Validate.isTrue(enumdirection.k().c());
        this.direction = enumdirection;
        this.lastYaw = this.yaw = (float) (this.direction.b() * 90);
        this.updateBoundingBox();
    }

    /* CraftBukkit start - bounding box calculation made static (for spawn usage)

        l is from function l()
        m is from function m()

        Placing here as it's more likely to be noticed as something which needs to be updated
        then something in a CraftBukkit file.
     */
    public static AxisAlignedBB calculateBoundingBox(BlockPosition blockPosition, EnumDirection direction, int width, int height) {
            double d0 = (double) blockPosition.getX() + 0.5D;
            double d1 = (double) blockPosition.getY() + 0.5D;
            double d2 = (double) blockPosition.getZ() + 0.5D;
            double d3 = 0.46875D;
            double d4 = width % 32 == 0 ? 0.5D : 0.0D;
            double d5 = height % 32 == 0 ? 0.5D : 0.0D;

            d0 -= (double) direction.getAdjacentX() * 0.46875D;
            d2 -= (double) direction.getAdjacentZ() * 0.46875D;
            d1 += d5;
            EnumDirection enumdirection = direction.f();

            d0 += d4 * (double) enumdirection.getAdjacentX();
            d2 += d4 * (double) enumdirection.getAdjacentZ();
            double d6 = (double) width;
            double d7 = (double) height;
            double d8 = (double) width;

            if (direction.k() == EnumDirection.EnumAxis.Z) {
                d8 = 1.0D;
            } else {
                d6 = 1.0D;
            }

            d6 /= 32.0D;
            d7 /= 32.0D;
            d8 /= 32.0D;
            return new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8);
    }

    private void updateBoundingBox() {
        if (this.direction != null) {
            // CraftBukkit start code moved in to calculateBoundingBox
            AxisAlignedBB bb = calculateBoundingBox(this.blockPosition, this.direction, this.l(), this.m());
            this.locX = (bb.a + bb.d) / 2.0D;
            this.locY = (bb.b + bb.e) / 2.0D;
            this.locZ = (bb.c + bb.f) / 2.0D;
            this.a(bb);
            // CraftBukkit end
        }
    }

    private double a(int i) {
        return i % 32 == 0 ? 0.5D : 0.0D;
    }

    public void t_() {
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        if (this.c++ == this.world.spigotConfig.hangingTickFrequency && !this.world.isClientSide) {
            this.c = 0;
            if (!this.dead && !this.survives()) {
                // CraftBukkit start - fire break events
                Material material = this.world.getType(new BlockPosition(this)).getBlock().getMaterial();
                HangingBreakEvent.RemoveCause cause;

                if (!material.equals(Material.AIR)) {
                    // TODO: This feels insufficient to catch 100% of suffocation cases
                    cause = HangingBreakEvent.RemoveCause.OBSTRUCTION;
                } else {
                    cause = HangingBreakEvent.RemoveCause.PHYSICS;
                }

                HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), cause);
                this.world.getServer().getPluginManager().callEvent(event);

                PaintingBreakEvent paintingEvent = null;
                if (this instanceof EntityPainting) {
                    // Fire old painting event until it can be removed
                    paintingEvent = new PaintingBreakEvent((Painting) this.getBukkitEntity(), PaintingBreakEvent.RemoveCause.valueOf(cause.name()));
                    paintingEvent.setCancelled(event.isCancelled());
                    this.world.getServer().getPluginManager().callEvent(paintingEvent);
                }

                if (dead || event.isCancelled() || (paintingEvent != null && paintingEvent.isCancelled())) {
                    return;
                }
                // CraftBukkit end
                this.die();
                this.b((Entity) null);
            }
        }

    }

    public boolean survives() {
        if (!this.world.getCubes(this, this.getBoundingBox()).isEmpty()) {
            return false;
        } else {
            int i = Math.max(1, this.l() / 16);
            int j = Math.max(1, this.m() / 16);
            BlockPosition blockposition = this.blockPosition.shift(this.direction.opposite());
            EnumDirection enumdirection = this.direction.f();

            for (int k = 0; k < i; ++k) {
                for (int l = 0; l < j; ++l) {
                    BlockPosition blockposition1 = blockposition.shift(enumdirection, k).up(l);
                    Block block = this.world.getType(blockposition1).getBlock();

                    if (!block.getMaterial().isBuildable() && !BlockDiodeAbstract.d(block)) {
                        return false;
                    }
                }
            }

            List list = this.world.getEntities(this, this.getBoundingBox());
            Iterator iterator = list.iterator();

            Entity entity;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entity = (Entity) iterator.next();
            } while (!(entity instanceof EntityHanging));

            return false;
        }
    }

    public boolean ad() {
        return true;
    }

    public boolean l(Entity entity) {
        return entity instanceof EntityHuman ? this.damageEntity(DamageSource.playerAttack((EntityHuman) entity), 0.0F) : false;
    }

    public EnumDirection getDirection() {
        return this.direction;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (!this.dead && !this.world.isClientSide) {
                // CraftBukkit start - fire break events
                HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.DEFAULT);
                PaintingBreakEvent paintingEvent = null;
                if (damagesource.getEntity() != null) {
                    event = new org.bukkit.event.hanging.HangingBreakByEntityEvent((Hanging) this.getBukkitEntity(), damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity());

                    if (this instanceof EntityPainting) {
                        // Fire old painting event until it can be removed
                        paintingEvent = new org.bukkit.event.painting.PaintingBreakByEntityEvent((Painting) this.getBukkitEntity(), damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity());
                    }
                } else if (damagesource.isExplosion()) {
                    event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.EXPLOSION);
                }

                this.world.getServer().getPluginManager().callEvent(event);

                if (paintingEvent != null) {
                    paintingEvent.setCancelled(event.isCancelled());
                    this.world.getServer().getPluginManager().callEvent(paintingEvent);
                }

                if (this.dead || event.isCancelled() || (paintingEvent != null && paintingEvent.isCancelled())) {
                    return true;
                }
                // CraftBukkit end

                this.die();
                this.ac();
                this.b(damagesource.getEntity());
            }

            return true;
        }
    }

    public void move(double d0, double d1, double d2) {
        if (!this.world.isClientSide && !this.dead && d0 * d0 + d1 * d1 + d2 * d2 > 0.0D) {
            if (this.dead) return; // CraftBukkit

            // CraftBukkit start - fire break events
            // TODO - Does this need its own cause? Seems to only be triggered by pistons
            HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.PHYSICS);
            this.world.getServer().getPluginManager().callEvent(event);

            if (this.dead || event.isCancelled()) {
                return;
            }
            // CraftBukkit end

            this.die();
            this.b((Entity) null);
        }

    }

    public void g(double d0, double d1, double d2) {
        if (false && !this.world.isClientSide && !this.dead && d0 * d0 + d1 * d1 + d2 * d2 > 0.0D) { // CraftBukkit - not needed
            this.die();
            this.b((Entity) null);
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Facing", (byte) this.direction.b());
        nbttagcompound.setInt("TileX", this.getBlockPosition().getX());
        nbttagcompound.setInt("TileY", this.getBlockPosition().getY());
        nbttagcompound.setInt("TileZ", this.getBlockPosition().getZ());
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.blockPosition = new BlockPosition(nbttagcompound.getInt("TileX"), nbttagcompound.getInt("TileY"), nbttagcompound.getInt("TileZ"));
        EnumDirection enumdirection;

        if (nbttagcompound.hasKeyOfType("Direction", 99)) {
            enumdirection = EnumDirection.fromType2(nbttagcompound.getByte("Direction"));
            this.blockPosition = this.blockPosition.shift(enumdirection);
        } else if (nbttagcompound.hasKeyOfType("Facing", 99)) {
            enumdirection = EnumDirection.fromType2(nbttagcompound.getByte("Facing"));
        } else {
            enumdirection = EnumDirection.fromType2(nbttagcompound.getByte("Dir"));
        }

        this.setDirection(enumdirection);
    }

    public abstract int l();

    public abstract int m();

    public abstract void b(Entity entity);

    protected boolean af() {
        return false;
    }

    public void setPosition(double d0, double d1, double d2) {
        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        BlockPosition blockposition = this.blockPosition;

        this.blockPosition = new BlockPosition(d0, d1, d2);
        if (!this.blockPosition.equals(blockposition)) {
            this.updateBoundingBox();
            this.ai = true;
        }

    }

    public BlockPosition getBlockPosition() {
        return this.blockPosition;
    }
}
