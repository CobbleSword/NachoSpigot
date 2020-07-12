package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PathfinderGoalTargetNearestPlayer extends PathfinderGoal {

    private static final Logger a = LogManager.getLogger();
    private EntityInsentient b;
    private final Predicate<Entity> c;
    private final PathfinderGoalNearestAttackableTarget.DistanceComparator d;
    private EntityLiving e;

    public PathfinderGoalTargetNearestPlayer(EntityInsentient entityinsentient) {
        this.b = entityinsentient;
        if (entityinsentient instanceof EntityCreature) {
            PathfinderGoalTargetNearestPlayer.a.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
        }

        this.c = new Predicate() {
            public boolean a(Entity entity) {
                if (!(entity instanceof EntityHuman)) {
                    return false;
                } else if (((EntityHuman) entity).abilities.isInvulnerable) {
                    return false;
                } else {
                    double d0 = PathfinderGoalTargetNearestPlayer.this.f();

                    if (entity.isSneaking()) {
                        d0 *= 0.800000011920929D;
                    }

                    if (entity.isInvisible()) {
                        float f = ((EntityHuman) entity).bY();

                        if (f < 0.1F) {
                            f = 0.1F;
                        }

                        d0 *= (double) (0.7F * f);
                    }

                    return (double) entity.g(PathfinderGoalTargetNearestPlayer.this.b) > d0 ? false : PathfinderGoalTarget.a(PathfinderGoalTargetNearestPlayer.this.b, (EntityLiving) entity, false, true);
                }
            }

            public boolean apply(Object object) {
                return this.a((Entity) object);
            }
        };
        this.d = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entityinsentient);
    }

    public boolean a() {
        double d0 = this.f();
        List list = this.b.world.a(EntityHuman.class, this.b.getBoundingBox().grow(d0, 4.0D, d0), this.c);

        Collections.sort(list, this.d);
        if (list.isEmpty()) {
            return false;
        } else {
            this.e = (EntityLiving) list.get(0);
            return true;
        }
    }

    public boolean b() {
        EntityLiving entityliving = this.b.getGoalTarget();

        if (entityliving == null) {
            return false;
        } else if (!entityliving.isAlive()) {
            return false;
        } else if (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).abilities.isInvulnerable) {
            return false;
        } else {
            ScoreboardTeamBase scoreboardteambase = this.b.getScoreboardTeam();
            ScoreboardTeamBase scoreboardteambase1 = entityliving.getScoreboardTeam();

            if (scoreboardteambase != null && scoreboardteambase1 == scoreboardteambase) {
                return false;
            } else {
                double d0 = this.f();

                return this.b.h(entityliving) > d0 * d0 ? false : !(entityliving instanceof EntityPlayer) || !((EntityPlayer) entityliving).playerInteractManager.isCreative();
            }
        }
    }

    public void c() {
        this.b.setGoalTarget(this.e, org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true); // CraftBukkit - added reason
        super.c();
    }

    public void d() {
        this.b.setGoalTarget((EntityLiving) null);
        super.c();
    }

    protected double f() {
        AttributeInstance attributeinstance = this.b.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);

        return attributeinstance == null ? 16.0D : attributeinstance.getValue();
    }
}
