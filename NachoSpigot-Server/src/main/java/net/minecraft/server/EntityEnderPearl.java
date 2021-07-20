package net.minecraft.server;

// CraftBukkit start
import dev.cobblesword.nachospigot.commons.Constants;
import org.bukkit.Bukkit;
import org.github.paperspigot.PaperSpigotConfig;
import net.minecraft.server.BlockPosition;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerTeleportEvent;

import dev.cobblesword.nachospigot.Nacho;

// CraftBukkit end

public class EntityEnderPearl extends EntityProjectile {

    private EntityLiving c;

    public EntityEnderPearl(World world) {
        super(world);
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(world, entityliving);
        this.c = entityliving;
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        EntityLiving entityliving = this.getShooter();

        if (movingobjectposition.entity != null) {
            if (movingobjectposition.entity == this.c) {
                return;
            }

            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, entityliving), 0.0F);
        }

        // PaperSpigot start - Remove entities in unloaded chunks
        if (this.inUnloadedChunk && world.paperSpigotConfig.removeUnloadedEnderPearls) {
            this.die();
        }
        // PaperSpigot end

        // FlamePaper start - 0117-Pearl-through-blocks
        BlockPosition blockPosition = movingobjectposition.a();
        
        if (blockPosition != null) {
            IBlockData blockData = world.getType(blockPosition);
            Block block = blockData.getBlock();
            boolean collides = 
                PaperSpigotConfig.pearlPassthroughTripwire && block == Blocks.TRIPWIRE
                || PaperSpigotConfig.pearlPassthroughCobweb && block == Blocks.WEB
                || PaperSpigotConfig.pearlPassthroughBed && block == Blocks.BED
                || PaperSpigotConfig.pearlPassthroughFenceGate && (block == Blocks.FENCE_GATE || block == Blocks.SPRUCE_FENCE_GATE || block == Blocks.BIRCH_FENCE_GATE || block == Blocks.JUNGLE_FENCE_GATE || block == Blocks.DARK_OAK_FENCE_GATE || block == Blocks.ACACIA_FENCE_GATE) && ((Boolean) blockData.get(BlockFenceGate.OPEN)).booleanValue()
                || PaperSpigotConfig.pearlPassthroughSlab && (block == Blocks.STONE_SLAB || block == Blocks.WOODEN_SLAB || block == Blocks.STONE_SLAB2);
        
            if (collides) {
                return;
            }
        }
        // FlamePaper end

        for (int i = 0; i < 32; ++i) {
            this.world.addParticle(EnumParticle.PORTAL, this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian(), Constants.EMPTY_ARRAY);
        }

        if (!this.world.isClientSide) {
            if (entityliving instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entityliving;

                if (entityplayer.playerConnection.a().isConnected() && entityplayer.world == this.world && !entityplayer.isSleeping()) {
                    // CraftBukkit start - Fire PlayerTeleportEvent
                    org.bukkit.craftbukkit.entity.CraftPlayer player = entityplayer.getBukkitEntity();
                    org.bukkit.Location location = getBukkitEntity().getLocation();
                    location.setPitch(player.getLocation().getPitch());
                    location.setYaw(player.getLocation().getYaw());

                    PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                    Bukkit.getPluginManager().callEvent(teleEvent);

                    if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                        if ((this.random.nextFloat() < 0.05F) && (this.world.getGameRules().getBoolean("doMobSpawning")) && (Nacho.get().getConfig().endermiteSpawning)) {
                            EntityEndermite entityendermite = new EntityEndermite(this.world);

                            entityendermite.a(true);
                            entityendermite.setPositionRotation(entityliving.locX, entityliving.locY, entityliving.locZ, entityliving.yaw, entityliving.pitch);
                            this.world.addEntity(entityendermite);
                        }

                        if (entityliving.au()) {
                            entityliving.mount((Entity) null);
                        }

                        entityplayer.playerConnection.teleport(teleEvent.getTo());
                        entityliving.fallDistance = 0.0F;
                        CraftEventFactory.entityDamage = this;
                        entityliving.damageEntity(DamageSource.FALL, 5.0F);
                        CraftEventFactory.entityDamage = null;
                    }
                    // CraftBukkit end
                }
            } else if (entityliving != null) {
                entityliving.enderTeleportTo(this.locX, this.locY, this.locZ);
                entityliving.fallDistance = 0.0F;
            }

            this.die();
        }

    }

    public void t_() {
        EntityLiving entityliving = this.getShooter();

        if (entityliving != null && entityliving instanceof EntityHuman && !entityliving.isAlive()) {
            this.die();
        } else {
            super.t_();
        }

    }
}
