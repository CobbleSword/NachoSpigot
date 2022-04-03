package org.bukkit;

import java.io.File;
import org.bukkit.generator.ChunkGenerator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.util.Vector;

/**
 * Represents a world, which may contain entities, chunks and blocks
 */
public interface World extends PluginMessageRecipient, Metadatable {

    /**
     * Gets the {@link Block} at the given coordinates
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Block at the given coordinates
     * @see #getBlockTypeIdAt(int, int, int) Returns the current type ID of
     *     the block
     */
    Block getBlockAt(int x, int y, int z);

    /**
     * Gets the {@link Block} at the given {@link Location}
     *
     * @param location Location of the block
     * @return Block at the given location
     * @see #getBlockTypeIdAt(org.bukkit.Location) Returns the current type ID
     *     of the block
     */
    Block getBlockAt(Location location);

    /**
     * Gets the block type ID at the given coordinates
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Type ID of the block at the given coordinates
     * @see #getBlockAt(int, int, int) Returns a live Block object at the
     *     given location
     * @deprecated Magic value
     */
    @Deprecated
    int getBlockTypeIdAt(int x, int y, int z);

    /**
     * Gets the block type ID at the given {@link Location}
     *
     * @param location Location of the block
     * @return Type ID of the block at the given location
     * @see #getBlockAt(org.bukkit.Location) Returns a live Block object at
     *     the given location
     * @deprecated Magic value
     */
    @Deprecated
    int getBlockTypeIdAt(Location location);

    /**
     * Gets the highest non-air coordinate at the given coordinates
     *
     * @param x X-coordinate of the blocks
     * @param z Z-coordinate of the blocks
     * @return Y-coordinate of the highest non-air block
     */
    int getHighestBlockYAt(int x, int z);

    /**
     * Gets the highest non-air coordinate at the given {@link Location}
     *
     * @param location Location of the blocks
     * @return Y-coordinate of the highest non-air block
     */
    int getHighestBlockYAt(Location location);

    /**
     * Gets the highest non-empty block at the given coordinates
     *
     * @param x X-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Highest non-empty block
     */
    Block getHighestBlockAt(int x, int z);

    /**
     * Gets the highest non-empty block at the given coordinates
     *
     * @param location Coordinates to get the highest block
     * @return Highest non-empty block
     */
    Block getHighestBlockAt(Location location);

    /**
     * Gets the {@link Chunk} at the given coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Chunk at the given coordinates
     */
    Chunk getChunkAt(int x, int z);

    /**
     * Gets the {@link Chunk} at the given {@link Location}
     *
     * @param location Location of the chunk
     * @return Chunk at the given location
     */
    Chunk getChunkAt(Location location);

    /**
     * Gets the {@link Chunk} that contains the given {@link Block}
     *
     * @param block Block to get the containing chunk from
     * @return The chunk that contains the given block
     */
    Chunk getChunkAt(Block block);

    // PaperSpigot start - Async chunk load API
    interface ChunkLoadCallback {
        void onLoad(Chunk chunk);
    }
    void getChunkAtAsync(int x, int z, ChunkLoadCallback cb);
    void getChunkAtAsync(Location location, ChunkLoadCallback cb);
    void getChunkAtAsync(Block block, ChunkLoadCallback cb);
    // PaperSpigot end

    /**
     * Checks if the specified {@link Chunk} is loaded
     *
     * @param chunk The chunk to check
     * @return true if the chunk is loaded, otherwise false
     */
    boolean isChunkLoaded(Chunk chunk);

    /**
     * Gets an array of all loaded {@link Chunk}s
     *
     * @return Chunk[] containing all loaded chunks
     */
    Chunk[] getLoadedChunks();

    /**
     * Loads the specified {@link Chunk}
     *
     * @param chunk The chunk to load
     */
    void loadChunk(Chunk chunk);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is loaded
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is loaded, otherwise false
     */
    boolean isChunkLoaded(int x, int z);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is loaded and
     * in use by one or more players
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is loaded and in use by one or more players,
     *     otherwise false
     */
    boolean isChunkInUse(int x, int z);

    /**
     * Loads the {@link Chunk} at the specified coordinates
     * <p>
     * If the chunk does not exist, it will be generated.
     * <p>
     * This method is analogous to {@link #loadChunk(int, int, boolean)} where
     * generate is true.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     */
    void loadChunk(int x, int z);

    /**
     * Loads the {@link Chunk} at the specified coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param generate Whether or not to generate a chunk if it doesn't
     *     already exist
     * @return true if the chunk has loaded successfully, otherwise false
     */
    boolean loadChunk(int x, int z, boolean generate);

    /**
     * Safely unloads and saves the {@link Chunk} at the specified coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean,
     * boolean)} where safe and saveis true
     *
     * @param chunk the chunk to unload
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    boolean unloadChunk(Chunk chunk);

    /**
     * Safely unloads and saves the {@link Chunk} at the specified coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean,
     * boolean)} where safe and saveis true
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    boolean unloadChunk(int x, int z);

    /**
     * Safely unloads and optionally saves the {@link Chunk} at the specified
     * coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean,
     * boolean)} where save is true
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param save Whether or not to save the chunk
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    boolean unloadChunk(int x, int z, boolean save);

    /**
     * Unloads and optionally saves the {@link Chunk} at the specified
     * coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param save Controls whether the chunk is saved
     * @param safe Controls whether to unload the chunk when players are
     *     nearby
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    boolean unloadChunk(int x, int z, boolean save, boolean safe);

    /**
     * Safely queues the {@link Chunk} at the specified coordinates for
     * unloading
     * <p>
     * This method is analogous to {@link #unloadChunkRequest(int, int,
     * boolean)} where safe is true
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true is the queue attempt was successful, otherwise false
     */
    boolean unloadChunkRequest(int x, int z);

    /**
     * Queues the {@link Chunk} at the specified coordinates for unloading
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param safe Controls whether to queue the chunk when players are nearby
     * @return Whether the chunk was actually queued
     */
    boolean unloadChunkRequest(int x, int z, boolean safe);

    /**
     * Regenerates the {@link Chunk} at the specified coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Whether the chunk was actually regenerated
     */
    boolean regenerateChunk(int x, int z);

    /**
     * Resends the {@link Chunk} to all clients
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Whether the chunk was actually refreshed
     * 
     * @deprecated This method is not guaranteed to work suitably across all client implementations.
     */
    @Deprecated
    boolean refreshChunk(int x, int z);

    /**
     * Drops an item at the specified {@link Location}
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @return ItemDrop entity created as a result of this method
     */
    Item dropItem(Location location, ItemStack item);

    /**
     * Drops an item at the specified {@link Location} with a random offset
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @return ItemDrop entity created as a result of this method
     */
    Item dropItemNaturally(Location location, ItemStack item);

    /**
     * Creates an {@link Arrow} entity at the given {@link Location}
     *
     * @param location Location to spawn the arrow
     * @param direction Direction to shoot the arrow in
     * @param speed Speed of the arrow. A recommend speed is 0.6
     * @param spread Spread of the arrow. A recommend spread is 12
     * @return Arrow entity spawned as a result of this method
     */
    Arrow spawnArrow(Location location, Vector direction, float speed, float spread);

    /**
     * Creates a tree at the given {@link Location}
     *
     * @param location Location to spawn the tree
     * @param type Type of the tree to create
     * @return true if the tree was created successfully, otherwise false
     */
    boolean generateTree(Location location, TreeType type);

    /**
     * Creates a tree at the given {@link Location}
     *
     * @param loc Location to spawn the tree
     * @param type Type of the tree to create
     * @param delegate A class to call for each block changed as a result of
     *     this method
     * @return true if the tree was created successfully, otherwise false
     */
    boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate);

    /**
     * Creates a entity at the given {@link Location}
     *
     * @param loc The location to spawn the entity
     * @param type The entity to spawn
     * @return Resulting Entity of this method, or null if it was unsuccessful
     */
    Entity spawnEntity(Location loc, EntityType type);

    /**
     * Creates a creature at the given {@link Location}
     *
     * @param loc The location to spawn the creature
     * @param type The creature to spawn
     * @return Resulting LivingEntity of this method, or null if it was
     *     unsuccessful
     * @deprecated Has issues spawning non LivingEntities. Use {@link
     *     #spawnEntity(Location, EntityType) spawnEntity} instead.
     */
    @Deprecated
    LivingEntity spawnCreature(Location loc, EntityType type);

    /**
     * Creates a creature at the given {@link Location}
     *
     * @param loc The location to spawn the creature
     * @param type The creature to spawn
     * @return Resulting LivingEntity of this method, or null if it was
     *     unsuccessful
     */
    @Deprecated
    LivingEntity spawnCreature(Location loc, CreatureType type);

    /**
     * Strikes lightning at the given {@link Location}
     *
     * @param loc The location to strike lightning
     * @return The lightning entity.
     */
    LightningStrike strikeLightning(Location loc);

    /**
     * Strikes lightning at the given {@link Location} without doing damage
     *
     * @param loc The location to strike lightning
     * @return The lightning entity.
     */
    LightningStrike strikeLightningEffect(Location loc);

    /**
     * Get a list of all entities in this World
     *
     * @return A List of all Entities currently residing in this world
     */
    List<Entity> getEntities();

    /**
     * Get a list of all living entities in this World
     *
     * @return A List of all LivingEntities currently residing in this world
     */
    List<LivingEntity> getLivingEntities();

    /**
     * Get a collection of all entities in this World matching the given
     * class/interface
     *
     * @param <T> an entity subclass
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match the given class/interface
     */
    @Deprecated
    <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes);

    /**
     * Get a collection of all entities in this World matching the given
     * class/interface
     * 
     * @param <T> an entity subclass
     * @param cls The class representing the type of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match the given class/interface
     */
    <T extends Entity> Collection<T> getEntitiesByClass(Class<T> cls);

    /**
     * Get a collection of all entities in this World matching any of the
     * given classes/interfaces
     *
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match one or more of the given classes/interfaces
     */
    Collection<Entity> getEntitiesByClasses(Class<?>... classes);

    /**
     * Get a list of all players in this World
     *
     * @return A list of all Players currently residing in this world
     */
    List<Player> getPlayers();

    /**
     * Returns a list of entities within a bounding box centered around a Location.
     *
     * Some implementations may impose artificial restrictions on the size of the search bounding box.
     *
     * @param location The center of the bounding box
     * @param x 1/2 the size of the box along x axis
     * @param y 1/2 the size of the box along y axis
     * @param z 1/2 the size of the box along z axis
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    Collection<Entity> getNearbyEntities(Location location, double x, double y, double z);

    /**
     * Gets the unique name of this world
     *
     * @return Name of this world
     */
    String getName();

    /**
     * Gets the Unique ID of this world
     *
     * @return Unique ID of this world.
     */
    UUID getUID();

    /**
     * Gets the default spawn {@link Location} of this world
     *
     * @return The spawn location of this world
     */
    Location getSpawnLocation();

    /**
     * Sets the spawn location of the world
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return True if it was successfully set.
     */
    boolean setSpawnLocation(int x, int y, int z);

    /**
     * Gets the relative in-game time of this world.
     * <p>
     * The relative time is analogous to hours * 1000
     *
     * @return The current relative time
     * @see #getFullTime() Returns an absolute time of this world
     */
    long getTime();

    /**
     * Sets the relative in-game time on the server.
     * <p>
     * The relative time is analogous to hours * 1000
     * <p>
     * Note that setting the relative time below the current relative time
     * will actually move the clock forward a day. If you require to rewind
     * time, please see {@link #setFullTime(long)}
     *
     * @param time The new relative time to set the in-game time to (in
     *     hours*1000)
     * @see #setFullTime(long) Sets the absolute time of this world
     */
    void setTime(long time);

    /**
     * Gets the full in-game time on this world
     *
     * @return The current absolute time
     * @see #getTime() Returns a relative time of this world
     */
    long getFullTime();

    /**
     * Sets the in-game time on the server
     * <p>
     * Note that this sets the full time of the world, which may cause adverse
     * effects such as breaking redstone clocks and any scheduled events
     *
     * @param time The new absolute time to set this world to
     * @see #setTime(long) Sets the relative time of this world
     */
    void setFullTime(long time);

    /**
     * Returns whether the world has an ongoing storm.
     *
     * @return Whether there is an ongoing storm
     */
    boolean hasStorm();

    /**
     * Set whether there is a storm. A duration will be set for the new
     * current conditions.
     *
     * @param hasStorm Whether there is rain and snow
     */
    void setStorm(boolean hasStorm);

    /**
     * Get the remaining time in ticks of the current conditions.
     *
     * @return Time in ticks
     */
    int getWeatherDuration();

    /**
     * Set the remaining time in ticks of the current conditions.
     *
     * @param duration Time in ticks
     */
    void setWeatherDuration(int duration);

    /**
     * Returns whether there is thunder.
     *
     * @return Whether there is thunder
     */
    boolean isThundering();

    /**
     * Set whether it is thundering.
     *
     * @param thundering Whether it is thundering
     */
    void setThundering(boolean thundering);

    /**
     * Get the thundering duration.
     *
     * @return Duration in ticks
     */
    int getThunderDuration();

    /**
     * Set the thundering duration.
     *
     * @param duration Duration in ticks
     */
    void setThunderDuration(int duration);

    /**
     * Creates explosion at given coordinates with given power
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(double x, double y, double z, float power);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(double x, double y, double z, float power, boolean setFire);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks);

    /**
     * Creates explosion at given coordinates with given power
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(Location loc, float power);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire.
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    boolean createExplosion(Location loc, float power, boolean setFire);

    /**
     * Gets the {@link Environment} type of this world
     *
     * @return This worlds Environment type
     */
    Environment getEnvironment();

    /**
     * Gets the Seed for this world.
     *
     * @return This worlds Seed
     */
    long getSeed();

    /**
     * Gets the current PVP setting for this world.
     *
     * @return True if PVP is enabled
     */
    boolean getPVP();

    /**
     * Sets the PVP setting for this world.
     *
     * @param pvp True/False whether PVP should be Enabled.
     */
    void setPVP(boolean pvp);

    /**
     * Gets the chunk generator for this world
     *
     * @return ChunkGenerator associated with this world
     */
    ChunkGenerator getGenerator();

    /**
     * Saves world to disk
     */
    void save();

    /**
     * Gets a list of all applied {@link BlockPopulator}s for this World
     *
     * @return List containing any or none BlockPopulators
     */
    List<BlockPopulator> getPopulators();

    /**
     * Spawn an entity of a specific class at the given {@link Location}
     *
     * @param location the {@link Location} to spawn the entity at
     * @param clazz the class of the {@link Entity} to spawn
     * @param <T> the class of the {@link Entity} to spawn
     * @return an instance of the spawned {@link Entity}
     * @throws IllegalArgumentException if either parameter is null or the
     *     {@link Entity} requested cannot be spawned
     */
    <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of
     * the specified {@link Material}. The material dictates what is falling.
     * When the FallingBlock hits the ground, it will place that block.
     * <p>
     * The Material must be a block type, check with {@link Material#isBlock()
     * material.isBlock()}. The Material may not be air.
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param material The block {@link Material} type
     * @param data The block data
     * @return The spawned {@link FallingBlock} instance
     * @throws IllegalArgumentException if {@link Location} or {@link
     *     Material} are null or {@link Material} is not a block
     * @deprecated Magic value
     */
    @Deprecated
    FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of
     * the specified blockId (converted to {@link Material})
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param blockId The id of the intended material
     * @param blockData The block data
     * @return The spawned FallingBlock instance
     * @throws IllegalArgumentException if location is null, or blockId is
     *     invalid
     * @see #spawnFallingBlock(org.bukkit.Location, org.bukkit.Material, byte)
     * @deprecated Magic value
     */
    @Deprecated
    FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException;

    /**
     * Plays an effect to all players within a default radius around a given
     * location.
     *
     * @param location the {@link Location} around which players must be to
     *     hear the sound
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     */
    void playEffect(Location location, Effect effect, int data);

    /**
     * Plays an effect to all players within a given radius around a location.
     *
     * @param location the {@link Location} around which players must be to
     *     hear the effect
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     * @param radius the radius around the location
     */
    void playEffect(Location location, Effect effect, int data, int radius);

    /**
     * Plays an effect to all players within a default radius around a given
     * location.
     *
     * @param <T> data dependant on the type of effect
     * @param location the {@link Location} around which players must be to
     *     hear the sound
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     */
    <T> void playEffect(Location location, Effect effect, T data);

    /**
     * Plays an effect to all players within a given radius around a location.
     *
     * @param <T> data dependant on the type of effect
     * @param location the {@link Location} around which players must be to
     *     hear the effect
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     * @param radius the radius around the location
     */
    <T> void playEffect(Location location, Effect effect, T data, int radius);

    /**
     * Get empty chunk snapshot (equivalent to all air blocks), optionally
     * including valid biome data. Used for representing an ungenerated chunk,
     * or for fetching only biome data without loading a chunk.
     *
     * @param x - chunk x coordinate
     * @param z - chunk z coordinate
     * @param includeBiome - if true, snapshot includes per-coordinate biome
     *     type
     * @param includeBiomeTempRain - if true, snapshot includes per-coordinate
     *     raw biome temperature and rainfall
     * @return The empty snapshot.
     */
    ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain);

    /**
     * Sets the spawn flags for this.
     *
     * @param allowMonsters - if true, monsters are allowed to spawn in this
     *     world.
     * @param allowAnimals - if true, animals are allowed to spawn in this
     *     world.
     */
    void setSpawnFlags(boolean allowMonsters, boolean allowAnimals);

    /**
     * Gets whether animals can spawn in this world.
     *
     * @return whether animals can spawn in this world.
     */
    boolean getAllowAnimals();

    /**
     * Gets whether monsters can spawn in this world.
     *
     * @return whether monsters can spawn in this world.
     */
    boolean getAllowMonsters();

    /**
     * Gets the biome for the given block coordinates.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Biome of the requested block
     */
    Biome getBiome(int x, int z);

    /**
     * Sets the biome for the given block coordinates
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @param bio new Biome type for this block
     */
    void setBiome(int x, int z, Biome bio);

    /**
     * Gets the temperature for the given block coordinates.
     * <p>
     * It is safe to run this method when the block does not exist, it will
     * not create the block.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Temperature of the requested block
     */
    double getTemperature(int x, int z);

    /**
     * Gets the humidity for the given block coordinates.
     * <p>
     * It is safe to run this method when the block does not exist, it will
     * not create the block.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Humidity of the requested block
     */
    double getHumidity(int x, int z);

    /**
     * Gets the maximum height of this world.
     * <p>
     * If the max height is 100, there are only blocks from y=0 to y=99.
     *
     * @return Maximum height of the world
     */
    int getMaxHeight();

    /**
     * Gets the sea level for this world.
     * <p>
     * This is often half of {@link #getMaxHeight()}
     *
     * @return Sea level
     */
    int getSeaLevel();

    /**
     * Gets whether the world's spawn area should be kept loaded into memory
     * or not.
     *
     * @return true if the world's spawn area will be kept loaded into memory.
     */
    boolean getKeepSpawnInMemory();

    /**
     * Sets whether the world's spawn area should be kept loaded into memory
     * or not.
     *
     * @param keepLoaded if true then the world's spawn area will be kept
     *     loaded into memory.
     */
    void setKeepSpawnInMemory(boolean keepLoaded);

    /**
     * Gets whether or not the world will automatically save
     *
     * @return true if the world will automatically save, otherwise false
     */
    boolean isAutoSave();

    /**
     * Sets whether or not the world will automatically save
     *
     * @param value true if the world should automatically save, otherwise
     *     false
     */
    void setAutoSave(boolean value);

    /**
     * Sets the Difficulty of the world.
     *
     * @param difficulty the new difficulty you want to set the world to
     */
    void setDifficulty(Difficulty difficulty);

    /**
     * Gets the Difficulty of the world.
     *
     * @return The difficulty of the world.
     */
    Difficulty getDifficulty();

    /**
     * Gets the folder of this world on disk.
     *
     * @return The folder of this world.
     */
    File getWorldFolder();

    /**
     * Gets the type of this world.
     *
     * @return Type of this world.
     */
    WorldType getWorldType();

    /**
     * Gets whether or not structures are being generated.
     *
     * @return True if structures are being generated.
     */
    boolean canGenerateStructures();

    /**
     * Gets the world's ticks per animal spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn animals.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn animals in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn animals
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, animal spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 400.
     *
     * @return The world's ticks per animal spawns value
     */
    long getTicksPerAnimalSpawns();

    /**
     * Sets the world's ticks per animal spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn animals.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn animals in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn animals
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, animal spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 400.
     *
     * @param ticksPerAnimalSpawns the ticks per animal spawns value you want
     *     to set the world to
     */
    void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns);

    /**
     * Gets the world's ticks per monster spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn monsters.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn monsters in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn monsters
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, monsters spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 1.
     *
     * @return The world's ticks per monster spawns value
     */
    long getTicksPerMonsterSpawns();

    /**
     * Sets the world's ticks per monster spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn monsters.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn monsters in
     *     this world on every tick.
     * <li>A value of 400 will mean the server will attempt to spawn monsters
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, monsters spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 1.
     *
     * @param ticksPerMonsterSpawns the ticks per monster spawns value you
     *     want to set the world to
     */
    void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns);

    /**
     * Gets limit for number of monsters that can spawn in a chunk in this
     * world
     *
     * @return The monster spawn limit
     */
    int getMonsterSpawnLimit();

    /**
     * Sets the limit for number of monsters that can spawn in a chunk in this
     * world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     * 
     * @param limit the new mob limit
     */
    void setMonsterSpawnLimit(int limit);

    /**
     * Gets the limit for number of animals that can spawn in a chunk in this
     * world
     *
     * @return The animal spawn limit
     */
    int getAnimalSpawnLimit();

    /**
     * Sets the limit for number of animals that can spawn in a chunk in this
     * world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     * 
     * @param limit the new mob limit
     */
    void setAnimalSpawnLimit(int limit);

    /**
     * Gets the limit for number of water animals that can spawn in a chunk in
     * this world
     *
     * @return The water animal spawn limit
     */
    int getWaterAnimalSpawnLimit();

    /**
     * Sets the limit for number of water animals that can spawn in a chunk in
     * this world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     * 
     * @param limit the new mob limit
     */
    void setWaterAnimalSpawnLimit(int limit);

    /**
     * Gets the limit for number of ambient mobs that can spawn in a chunk in
     * this world
     *
     * @return The ambient spawn limit
     */
    int getAmbientSpawnLimit();

    /**
     * Sets the limit for number of ambient mobs that can spawn in a chunk in
     * this world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     * 
     * @param limit the new mob limit
     */
    void setAmbientSpawnLimit(int limit);

    /**
     * Play a Sound at the provided Location in the World
     * <p>
     * This function will fail silently if Location or Sound are null.
     *
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(Location location, Sound sound, float volume, float pitch);

    /**
     * Get existing rules
     *
     * @return An array of rules
     */
    String[] getGameRules();

    /**
     * Gets the current state of the specified rule
     * <p>
     * Will return null if rule passed is null
     *
     * @param rule Rule to look up value of
     * @return String value of rule
     */
    String getGameRuleValue(String rule);

    /**
     * Set the specified gamerule to specified value.
     * <p>
     * The rule may attempt to validate the value passed, will return true if
     * value was set.
     * <p>
     * If rule is null, the function will return false.
     *
     * @param rule Rule to set
     * @param value Value to set rule to
     * @return True if rule was set
     */
    boolean setGameRuleValue(String rule, String value);

    /**
     * Checks if string is a valid game rule
     *
     * @param rule Rule to check
     * @return True if rule exists
     */
    boolean isGameRule(String rule);

    // Spigot start
    class Spigot
    {

        /**
         * Plays an effect to all players within a default radius around a given
         * location.
         *
         * @param location the {@link Location} around which players must be to
         * see the effect
         * @param effect the {@link Effect}
         * @throws IllegalArgumentException if the location or effect is null.
         * It also throws when the effect requires a material or a material data
         */
        public void playEffect(Location location, Effect effect)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        /**
         * Plays an effect to all players within a default radius around a given
         * location. The effect will use the provided material (and material
         * data if required). The particle's position on the client will be the
         * given location, adjusted on each axis by a normal distribution with
         * mean 0 and standard deviation given in the offset parameters, each
         * particle has independently calculated offsets. The effect will have
         * the given speed and particle count if the effect is a particle. Some
         * effect will create multiple particles.
         *
         * @param location the {@link Location} around which players must be to
         * see the effect
         * @param effect effect the {@link Effect}
         * @param id the item/block/data id for the effect
         * @param data the data value of the block/item for the effect
         * @param offsetX the amount to be randomly offset by in the X axis
         * @param offsetY the amount to be randomly offset by in the Y axis
         * @param offsetZ the amount to be randomly offset by in the Z axis
         * @param speed the speed of the particles
         * @param particleCount the number of particles
         * @param radius the radius around the location
         */
        public void playEffect(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int particleCount, int radius)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
        
        /**
         * Strikes lightning at the given {@link Location} and possibly without sound
         *
         * @param loc The location to strike lightning
         * @param isSilent Whether this strike makes no sound
         * @return The lightning entity.
         */        
        public LightningStrike strikeLightning(Location loc, boolean isSilent)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
        
        /**
         * Strikes lightning at the given {@link Location} without doing damage and possibly without sound
         *
         * @param loc The location to strike lightning
         * @param isSilent Whether this strike makes no sound
         * @return The lightning entity.
         */
        public LightningStrike strikeLightningEffect(Location loc, boolean isSilent)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    Spigot spigot();
    // Spigot end

    /**
     * Gets the world border for this world.
     *
     * @return The world border for this world.
     */
    WorldBorder getWorldBorder();

    /**
     * Represents various map environment types that a world may be
     */
    enum Environment {

        /**
         * Represents the "normal"/"surface world" map
         */
        NORMAL(0),
        /**
         * Represents a nether based map ("hell")
         */
        NETHER(-1),
        /**
         * Represents the "end" map
         */
        THE_END(1);

        private final int id;
        private static final Map<Integer, Environment> lookup = new HashMap<>();

        Environment(int id) {
            this.id = id;
        }

        /**
         * Gets the dimension ID of this environment
         *
         * @return dimension ID
         * @deprecated Magic value
         */
        @Deprecated
        public int getId() {
            return id;
        }

        /**
         * Get an environment by ID
         *
         * @param id The ID of the environment
         * @return The environment
         * @deprecated Magic value
         */
        @Deprecated
        public static Environment getEnvironment(int id) {
            return lookup.get(id);
        }

        static {
            for (Environment env : values()) {
                lookup.put(env.getId(), env);
            }
        }
    }
}
