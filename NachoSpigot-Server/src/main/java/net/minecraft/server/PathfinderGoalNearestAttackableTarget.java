package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PathfinderGoalNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalTarget {

    protected final Class<T> a;
    private final int g;
    protected final PathfinderGoalNearestAttackableTarget.DistanceComparator b;
    protected Predicate c;
    protected EntityLiving d;

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, boolean flag) {
        this(entitycreature, oclass, flag, false);
    }

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, boolean flag, boolean flag1) {
        this(entitycreature, oclass, 10, flag, flag1, (Predicate) null);
    }

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, int i, boolean flag, boolean flag1, final Predicate<? super T> predicate) {
        super(entitycreature, flag, flag1);
        this.a = oclass;
        this.g = i;
        this.b = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entitycreature);
        this.a(1);
        this.c = new Predicate() {
            public boolean a(T t0) {
                if (predicate != null && !predicate.apply(t0)) {
                    return false;
                } else {
                    if (t0 instanceof EntityHuman) {
                        double d0 = PathfinderGoalNearestAttackableTarget.this.f();

                        if (t0.isSneaking()) {
                            d0 *= 0.800000011920929D;
                        }

                        if (t0.isInvisible()) {
                            float f = ((EntityHuman) t0).bY();

                            if (f < 0.1F) {
                                f = 0.1F;
                            }

                            d0 *= (double) (0.7F * f);
                        }

                        if ((double) t0.g(PathfinderGoalNearestAttackableTarget.this.e) > d0) {
                            return false;
                        }
                    }

                    return PathfinderGoalNearestAttackableTarget.this.a(t0, false);
                }
            }

            public boolean apply(Object object) {
                return this.a((T) object); // CraftBukkit - fix decompile error
            }
        };
    }

    public boolean a() {
        if (this.g > 0 && this.e.bc().nextInt(this.g) != 0) {
            return false;
        } else {
            double d0 = this.f();
            List list = this.e.world.a(this.a, this.e.getBoundingBox().grow(d0, 4.0D, d0), Predicates.and((Predicate) this.c, (Predicate) IEntitySelector.d)); // TacoSpigot - the eclipse compiler can't understand this, so make it generic

            Collections.sort(list, this.b);
            if (list.isEmpty()) {
                return false;
            } else {
                this.d = (EntityLiving) list.get(0);
                return true;
            }
        }
    }

    public void c() {
        this.e.setGoalTarget(this.d, d instanceof EntityPlayer ? org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_PLAYER : org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true); // Craftbukkit - reason
        super.c();
    }

    public static class DistanceComparator implements Comparator<Entity> {

        private final Entity a;

        public DistanceComparator(Entity entity) {
            this.a = entity;
        }

        public int a(Entity entity, Entity entity1) {
            double d0 = this.a.h(entity);
            double d1 = this.a.h(entity1);

            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }

        public int compare(Entity object, Entity object1) { // CraftBukkit - fix decompile error
            return this.a((Entity) object, (Entity) object1);
        }
    }
}
