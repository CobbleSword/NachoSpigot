package org.bukkit.entity;

/**
 * Represents a wither skull {@link Fireball}.
 */
public interface WitherSkull extends Fireball {

    /**
     * Sets the charged status of the wither skull.
     *
     * @param charged whether it should be charged
     */
    void setCharged(boolean charged);

    /**
     * Gets whether or not the wither skull is charged.
     *
     * @return whether the wither skull is charged
     */
    boolean isCharged();
}
