package org.bukkit.block;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;

/**
 * Represents a creature spawner.
 */
public interface CreatureSpawner extends BlockState {

    /**
     * Get the spawner's creature type.
     *
     * @return The creature type.
     * @deprecated In favour of {@link #getSpawnedType()}.
     */
    @Deprecated
    CreatureType getCreatureType();

    /**
     * Get the spawner's creature type.
     *
     * @return The creature type.
     */
    EntityType getSpawnedType();

    /**
     * Set the spawner's creature type.
     *
     * @param creatureType The creature type.
     */
    void setSpawnedType(EntityType creatureType);

    /**
     * Set the spawner creature type.
     *
     * @param creatureType The creature type.
     * @deprecated In favour of {@link #setSpawnedType(EntityType)}.
     */
    @Deprecated
    void setCreatureType(CreatureType creatureType);

    /**
     * Get the spawner's creature type.
     *
     * @return The creature type's name.
     * @deprecated Use {@link #getCreatureTypeName()}.
     */
    @Deprecated
    String getCreatureTypeId();

    /**
     * Set the spawner mob type.
     *
     * @param creatureType The creature type's name.
     */
    void setCreatureTypeByName(String creatureType);

    /**
     * Get the spawner's creature type.
     *
     * @return The creature type's name.
     */
    String getCreatureTypeName();

    /**
     * Set the spawner mob type.
     *
     * @param creatureType The creature type's name.
     * @deprecated Use {@link #setCreatureTypeByName(String)}.
     */
    @Deprecated
    void setCreatureTypeId(String creatureType);

    /**
     * Get the spawner's delay.
     *
     * @return The delay.
     */
    int getDelay();

    /**
     * Set the spawner's delay.
     *
     * @param delay The delay.
     */
    void setDelay(int delay);
}
