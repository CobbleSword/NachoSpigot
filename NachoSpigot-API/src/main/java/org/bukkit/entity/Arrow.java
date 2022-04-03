package org.bukkit.entity;

/**
 * Represents an arrow.
 */
public interface Arrow extends Projectile {

    /**
     * Gets the knockback strength for an arrow, which is the
     * {@link org.bukkit.enchantments.Enchantment#KNOCKBACK KnockBack} level
     * of the bow that shot it.
     *
     * @return the knockback strength value
     */
    int getKnockbackStrength();

    /**
     * Sets the knockback strength for an arrow.
     *
     * @param knockbackStrength the knockback strength value
     */
    void setKnockbackStrength(int knockbackStrength);

    /**
     * Gets whether this arrow is critical.
     * <p>
     * Critical arrows have increased damage and cause particle effects.
     * <p>
     * Critical arrows generally occur when a player fully draws a bow before
     * firing.
     *
     * @return true if it is critical
     */
    boolean isCritical();

    /**
     * Sets whether or not this arrow should be critical.
     *
     * @param critical whether or not it should be critical
     */
    void setCritical(boolean critical);

    class Spigot extends Entity.Spigot
    {

        public double getDamage()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        public void setDamage(double damage)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    Spigot spigot();
}
