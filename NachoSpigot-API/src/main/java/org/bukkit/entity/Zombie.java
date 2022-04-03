package org.bukkit.entity;

/**
 * Represents a Zombie.
 */
public interface Zombie extends Monster {

    /**
     * Gets whether the zombie is a baby
     *
     * @return Whether the zombie is a baby
     */
    boolean isBaby();

    /**
     * Sets whether the zombie is a baby
     *
     * @param flag Whether the zombie is a baby
     */
    void setBaby(boolean flag);

    /**
     * Gets whether the zombie is a villager
     *
     * @return Whether the zombie is a villager
     */
    boolean isVillager();

    /**
     * Sets whether the zombie is a villager
     *
     * @param flag Whether the zombie is a villager
     */
    void setVillager(boolean flag);
}
