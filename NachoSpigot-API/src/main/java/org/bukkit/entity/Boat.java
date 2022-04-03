package org.bukkit.entity;

/**
 * Represents a boat entity.
 */
public interface Boat extends Vehicle {

    /**
     * Gets the maximum speed of a boat. The speed is unrelated to the
     * velocity.
     *
     * @return The max speed.
     */
    double getMaxSpeed();

    /**
     * Sets the maximum speed of a boat. Must be nonnegative. Default is 0.4D.
     *
     * @param speed The max speed.
     */
    void setMaxSpeed(double speed);

    /**
     * Gets the deceleration rate (newSpeed = curSpeed * rate) of occupied
     * boats. The default is 0.2.
     *
     * @return The rate of deceleration
     */
    double getOccupiedDeceleration();

    /**
     * Sets the deceleration rate (newSpeed = curSpeed * rate) of occupied
     * boats. Setting this to a higher value allows for quicker acceleration.
     * The default is 0.2.
     *
     * @param rate deceleration rate
     */
    void setOccupiedDeceleration(double rate);

    /**
     * Gets the deceleration rate (newSpeed = curSpeed * rate) of unoccupied
     * boats. The default is -1. Values below 0 indicate that no additional
     * deceleration is imposed.
     *
     * @return The rate of deceleration
     */
    double getUnoccupiedDeceleration();

    /**
     * Sets the deceleration rate (newSpeed = curSpeed * rate) of unoccupied
     * boats. Setting this to a higher value allows for quicker deceleration
     * of boats when a player disembarks. The default is -1. Values below 0
     * indicate that no additional deceleration is imposed.
     *
     * @param rate deceleration rate
     */
    void setUnoccupiedDeceleration(double rate);

    /**
     * Get whether boats can work on land.
     *
     * @return whether boats can work on land
     */
    boolean getWorkOnLand();

    /**
     * Set whether boats can work on land.
     *
     * @param workOnLand whether boats can work on land
     */
    void setWorkOnLand(boolean workOnLand);
}
