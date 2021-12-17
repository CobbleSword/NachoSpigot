package me.elier.nachospigot.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

@SuppressWarnings("unused")
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
        return config.getList("world-settings." + worldName + "." + path, config.getList("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        config.addDefault("world-settings.default." + path, def);
        return config.getString("world-settings." + worldName + "." + path, config.getString("world-settings.default." + path));
    }

    private static void addComment(String path, String comment) {
        NachoConfig.c.addComment("world-settings.default." + path, comment);
    }

    static void loadComments() {
        addComment("disable-sponge-absorption", "Disables sponge absorption");
        addComment("unload-chunks", "Enable unloading chunks");
        addComment("block-operations", "Enable block operations");
        addComment("physics.disable-place", "Disables physics place");
        addComment("physics.disable-update", "Disables physics update");
        addComment("enable-lava-to-cobblestone", "Enables lava converting to cobblestone.");
        addComment("entity.mob-ai", "Enables mob AI");
        addComment("entity.mob-sound", "Enables mob sound");
        addComment("entity.entity-activation", "Enables active ticks for entities");
        addComment("entity.endermite-spawning", "Enables endermite spawning.");
        addComment("infinite-water-sources", "Enables infinite water sources");
        addComment("explosions.constant-radius", "Changes the radius of explosions to be constant.");
        addComment("explosions.reduced-density-rays", "Toggles whether the server should use reduced rays when calculating density");
        addComment("tick-enchantment-tables", "Toggles whether enchantment tables should be ticked");
    }

    public boolean disableSpongeAbsorption;

    private void disableSpongeAbsorption() {
        disableSpongeAbsorption = getBoolean("disable-sponge-absorption", false);
    }

    public boolean doChunkUnload;

    private void doChunkUnload() {
        doChunkUnload = getBoolean("unload-chunks", true);
    }

    public boolean doBlocksOperations;

    private void doBlocksOperations() {
        doBlocksOperations = getBoolean("block-operations", true);
    }

    public boolean disablePhysicsPlace;
    public boolean disablePhysicsUpdate;

    private void physics() {
        disablePhysicsPlace = getBoolean("physics.disable-place", false);
        disablePhysicsUpdate = getBoolean("physics.disable-update", false);
    }

    public boolean enableLavaToCobblestone;

    private void setEnableLavaToCobblestone() {
        enableLavaToCobblestone = getBoolean("enable-lava-to-cobblestone", true);
    }

    public boolean enableMobAI;
    public boolean enableMobSound;
    public boolean enableEntityActivation;
    public boolean endermiteSpawning;

    private void entity() {
        enableMobAI = getBoolean("entity.mob-ai", true);
        enableMobSound = getBoolean("entity.mob-sound", true);
        enableEntityActivation = getBoolean("entity.entity-activation", true);
        endermiteSpawning = getBoolean("entity.endermite-spawning", true);
    }

    public boolean infiniteWaterSources;

    private void infiniteWaterSources() {
        infiniteWaterSources = getBoolean("infinite-water-sources", true);
    }

    public boolean constantExplosions;
    public boolean reducedDensityRays;

    private void explosions() {
        constantExplosions = getBoolean("explosions.constant-radius", false);
        reducedDensityRays = getBoolean("explosions.reduced-density-rays", true);
    }

    public boolean shouldTickEnchantmentTables;

    private void shouldTickEnchantmentTables() {
        shouldTickEnchantmentTables = getBoolean("tick-enchantment-tables", true);
    }
}
