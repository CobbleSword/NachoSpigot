package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.Material;
// CraftBukkit end

public class PathfinderGoalEatTile extends PathfinderGoal {

    private static final Predicate<IBlockData> b = BlockStatePredicate.a((Block) Blocks.TALLGRASS).a(BlockLongGrass.TYPE, Predicates.equalTo(BlockLongGrass.EnumTallGrassType.GRASS));
    private EntityInsentient c;
    private World d;
    int a;

    public PathfinderGoalEatTile(EntityInsentient entityinsentient) {
        this.c = entityinsentient;
        this.d = entityinsentient.world;
        this.a(7);
    }

    public boolean a() {
        if (this.c.bc().nextInt(this.c.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition(this.c.locX, this.c.locY, this.c.locZ);

            return PathfinderGoalEatTile.b.apply(this.d.getType(blockposition)) ? true : this.d.getType(blockposition.down()).getBlock() == Blocks.GRASS;
        }
    }

    public void c() {
        this.a = 40;
        this.d.broadcastEntityEffect(this.c, (byte) 10);
        this.c.getNavigation().n();
    }

    public void d() {
        this.a = 0;
    }

    public boolean b() {
        return this.a > 0;
    }

    public int f() {
        return this.a;
    }

    public void e() {
        this.a = Math.max(0, this.a - 1);
        if (this.a == 4) {
            BlockPosition blockposition = new BlockPosition(this.c.locX, this.c.locY, this.c.locZ);

            if (PathfinderGoalEatTile.b.apply(this.d.getType(blockposition))) {
                // CraftBukkit
                if (!CraftEventFactory.callEntityChangeBlockEvent(this.c, this.c.world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), Material.AIR, !this.d.getGameRules().getBoolean("mobGriefing")).isCancelled()) {
                    this.d.setAir(blockposition, false);
                }

                this.c.v();
            } else {
                BlockPosition blockposition1 = blockposition.down();

                if (this.d.getType(blockposition1).getBlock() == Blocks.GRASS) {
                    // CraftBukkit
                    if (!CraftEventFactory.callEntityChangeBlockEvent(this.c, this.c.world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), Material.AIR, !this.d.getGameRules().getBoolean("mobGriefing")).isCancelled()) {
                        this.d.triggerEffect(2001, blockposition1, Block.getId(Blocks.GRASS));
                        this.d.setTypeAndData(blockposition1, Blocks.DIRT.getBlockData(), 2);
                    }

                    this.c.v();
                }
            }

        }
    }
}
