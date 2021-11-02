package me.elier.nachospigot.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class NachoWorldConfig {

    private final String worldName;
    private final YamlConfiguration config;
    private boolean verbose;

    public NachoWorldConfig(String worldName) {
        this.worldName = worldName;
        this.config = NachoConfig.config;
        init();
    }

    public void init() {
        this.verbose = getBoolean("verbose", false);

        log("-------- World Settings For [\" + worldName + \"] --------");
        NachoConfig.readConfig(NachoWorldConfig.class, this);
    }

    private void log(String s) {
        if(verbose) {
            Bukkit.getLogger().info(s);
        }
    }

    private void set(String path, Object val) {
        config.set("world-settings.default." + path, val);
    }

    private boolean getBoolean(String path, boolean def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getBoolean("world-settings." + worldName + "." + path, config.getBoolean("world-settings.default." + path));
    }

    private double getDouble(String path, double def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getDouble("world-settings." + worldName + "." + path, config.getDouble("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getInt("world-settings." + worldName + "." + path, config.getInt("world-settings.default." + path));
    }

    private float getFloat(String path, float def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getFloat("world-settings." + worldName + "." + path, config.getFloat("world-settings.default." + path));
    }

    private <T> List getList(String path, T def) {
        config.addDefault("world-settings.default." + path, def);
        return (List<T>) config.getList("world-settings." + worldName + "." + path, config.getList("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getString("world-settings." + worldName + "." + path, config.getString("world-settings.default." + path));
    }

    private void addComment(String path, String comment) {
        NachoConfig.c.addComment("world-settings.default." + path, comment);
    }

    public boolean disableSpongeAbsorption;

    private void disableSpongeAbsorption() {
        disableSpongeAbsorption = getBoolean("disable-sponge-absorption", false);
        addComment("disable-sponge-absorption", "Disables sponge absorption");
    }

    public boolean doChunkUnload;

    private void doChunkUnload() {
        doChunkUnload = getBoolean("unload-chunks", true);
        addComment("unload-chunks", "Enable unloading chunks");
    }

    public boolean doBlocksOperations;

    private void doBlocksOperations() {
        doBlocksOperations = getBoolean("block-operations", true);
        addComment("block-operations", "Enable block operations");
    }

    public boolean disablePhysicsPlace;
    public boolean disablePhysicsUpdate;

    private void physics() {
        disablePhysicsPlace = getBoolean("physics.disable-place", false);
        addComment("physics.disable-place", "Disables physics place");
        disablePhysicsUpdate = getBoolean("settings.physics.disable-update", false);
        addComment("physics.disable-update", "Disables physics update");
    }

    public boolean enableLavaToCobblestone;

    private void setEnableLavaToCobblestone() {
        enableLavaToCobblestone = getBoolean("enable-lava-to-cobblestone", true);
        addComment("enable-lava-to-cobblestone", "Enables lava converting to cobblestone.");
    }

    public boolean enableMobAI;
    public boolean enableMobSound;
    public boolean enableEntityActivation;
    public boolean endermiteSpawning;

    private void entity() {
        enableMobAI = getBoolean("entity.mob-ai", true);
        addComment("entity.mob-ai", "Enables mob AI");
        enableMobSound = getBoolean("entity.mob-sound", true);
        addComment("entity.mob-sound", "Enables mob sound");
        enableEntityActivation = getBoolean("entity.entity-activation", true);
        addComment("entity.entity-activation", "Enables active ticks for entities");
        endermiteSpawning = getBoolean("entity.endermite-spawning", true);
        addComment("entity.endermite-spawning", "Enables endermite spawning.");
    }

    public boolean infiniteWaterSources;

    private void infiniteWaterSources() {
        infiniteWaterSources = getBoolean("infinite-water-sources", true);
        addComment("infinite-water-sources", "Enables infinite water sources");
    }

    public boolean constantExplosions;
    public boolean explosionProtectedRegions;
    public boolean reducedDensityRays;

    private void explosions() {
        constantExplosions = getBoolean("explosions.constant-radius", false);
        addComment("explosions.constant-explosions", "Changes the radius of explosions to be constant.");
        explosionProtectedRegions = getBoolean("explosions.explode-protected-regions", true);
        addComment("explosions.explode-protected-regions", "Toggles whether explosions should explode protected regions");
        reducedDensityRays = getBoolean("explosions.reduced-density-rays", true);
        addComment("explosions.reduced-density-rays", "Toggles whether the server should use reduced rays when calculating density");
    }

    public boolean shouldTickEnchantmentTables;

    private void shouldTickEnchantmentTables() {
        shouldTickEnchantmentTables = getBoolean("tick-enchantment-tables", true);
        addComment("tick-enchantment-tables", "Toggles whether enchantment tables should be ticked");
    }
}
