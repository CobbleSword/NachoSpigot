package org.bukkit.entity;

import org.bukkit.DyeColor;

/**
 * Represents a Wolf
 */
public interface Wolf extends Animals, Tameable {

    /**
     * Checks if this wolf is angry
     *
     * @return Anger true if angry
     */
    boolean isAngry();

    /**
     * Sets the anger of this wolf.
     * <p>
     * An angry wolf can not be fed or tamed, and will actively look for
     * targets to attack.
     *
     * @param angry true if angry
     */
    void setAngry(boolean angry);

    /**
     * Checks if this wolf is sitting
     *
     * @return true if sitting
     */
    boolean isSitting();

    /**
     * Sets if this wolf is sitting.
     * <p>
     * Will remove any path that the wolf was following beforehand.
     *
     * @param sitting true if sitting
     */
    void setSitting(boolean sitting);

    /**
     * Get the collar color of this wolf
     *
     * @return the color of the collar
     */
    DyeColor getCollarColor();

    /**
     * Set the collar color of this wolf
     *
     * @param color the color to apply
     */
    void setCollarColor(DyeColor color);
}
