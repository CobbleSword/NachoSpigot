package me.elier.nachospigot.config;

import com.google.common.base.Throwables;
import dev.cobblesword.nachospigot.OldNachoConfig;
import dev.cobblesword.nachospigot.commons.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public class NachoConfig {

    private static File CONFIG_FILE;
    private static final String HEADER = "This is the main configuration file for NachoSpigot.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
            + "with caution, and make sure you know what each option does before configuring.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to NachoSpigot,\n"
            + "join us in our Discord.\n"
            + "\n"
            + "Discord: https://discord.gg/SBTEbSx\n"
            + "Github: https://github.com/CobbleSword/NachoSpigot\n";
    static YamlConfiguration config;
    static int version;

    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        File old_config = new File("nacho.json");
        if(old_config.exists()) migrate(old_config);
        config = new YamlConfiguration();
        try {
            System.out.println("Loading NachoSpigot config from " + configFile.getName());
            config.load(CONFIG_FILE);
        } catch (IOException ignored) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load nacho.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        int configVersion = -1; // Update this every new configuration update
        version = getInt("config-version", configVersion);
        set("config-version", configVersion);
        readConfig(NachoConfig.class, null);
    }

    private static void migrate(File old_config) {
        OldNachoConfig nachoJson = FileUtils.toObject(old_config, OldNachoConfig.class);
        try {
            Files.delete(old_config.toPath());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to delete nacho.json during migration to nacho.yml", e);
        }
        // TODO
    }

    static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw Throwables.propagate(ex.getCause());
                    } catch (Exception ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                    }
                }
            }
        }

        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    private static void set(String path, Object val) {
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static float getFloat(String path, float def) {
        config.addDefault(path, def);
        return config.getFloat(path, config.getFloat(path));
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return (List<T>) config.getList(path, config.getList(path));
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    public static boolean saveEmptyScoreboardTeams;

    private static void saveEmptyScoreboardTeams() {
        saveEmptyScoreboardTeams = getBoolean("settings.save-empty-scoreboard-teams", false);
    }
    public static boolean enableVersionCommand;
    public static boolean enablePluginsCommand;
    public static boolean enableReloadCommand;

    private static void commands() {
        enableVersionCommand = getBoolean("settings.commands.enable-version-command", true);
        enablePluginsCommand = getBoolean("settings.commands.enable-plugins-command", true);
        enableReloadCommand = getBoolean("settings.commands.enable-reload-command", true);
    }

    public static boolean useFastOperators;

    private static void useFastOperators() {
        useFastOperators = getBoolean("settings.use-fast-operators", false);
    }
    public static boolean patchProtocolLib;

    private static void patchProtocolLib() {
        patchProtocolLib = getBoolean("settings.patch-protocollib", true);
    }
    public static boolean stopNotifyBungee;

    private static void stopNotifyBungee() {
        stopNotifyBungee = getBoolean("settings.stop-notify-bungee", false);
    }
    public static boolean checkForMalware;

    private static void antiMalware() {
        checkForMalware = getBoolean("settings.anti-malware", false);
    }

    public static boolean kickOnIllegalBehavior;

    private static void kickOnIllegalBehavior() {
        kickOnIllegalBehavior = getBoolean("settings.kick-on-illegal-behavior", true);
    }

    public static boolean shouldTickEnchantmentTables;

    private static void shouldTickEnchantmentTables() {
        shouldTickEnchantmentTables = getBoolean("settings.tick-enchantment-tables", true);
    }

    public static boolean usePandaWire;

    private static void usePandaWire() {
        usePandaWire = getBoolean("settings.panda-wire", true);
    }

    public static boolean constantExplosions;
    public static boolean explosionProtectedRegions;

    private static void explosions() {
        constantExplosions = getBoolean("settings.constant-explosions", false);
        explosionProtectedRegions = getBoolean("settings.explode-protected-regions", true);
    }

    public static boolean fireEntityExplodeEvent;

    private static void fireEntityExplodeEvent() {
        fireEntityExplodeEvent = getBoolean("settings.fire-entity-explode-event", true);
    }

    public static boolean reducedDensityRays;

    private static void reducedDensityRays() {
        reducedDensityRays = getBoolean("settings.reduced-density-rays", true);
    }
    public static int playerTimeStatisticsInterval = 20;

    private static void playerTimeStatisticsInterval() {
        playerTimeStatisticsInterval = getInt("settings.player-time-statistics-interval", 20);
    }

    public static String serverBrandName = "NachoSpigot";

    private static void serverBrandName() {
        serverBrandName = getString("settings.brand-name", "NachoSpigot");
    }

    public static boolean stopDecodingItemStackOnPlace = true;

    private static void stopDecodingItemStackOnPlace() {
        stopDecodingItemStackOnPlace = getBoolean("settings.stop-decoding-itemstack-on-place", true);
    }

    public static boolean enableAntiCrash = true;

    private static void enableAntiCrash() {
        enableAntiCrash = getBoolean("settings.anti-crash", true);
    }

    public static boolean infiniteWaterSources = true;

    private static void infiniteWaterSources() {
        infiniteWaterSources = getBoolean("settings.infinite-water-sources", true); // TODO: move to world config
    }
    public static boolean leavesDecayEvent = true;

    private static void leavesDecayEvent() {
        leavesDecayEvent = getBoolean("settings.leaves-decay-event", true);
    }

    public static boolean enableMobAI = true;
    public static boolean enableMobSound = true;
    public static boolean enableEntityActivation = true;
    public static boolean endermiteSpawning = true;

    private static void entity() {
        enableMobAI = getBoolean("settings.entity.mob-ai", true);
        enableMobSound = getBoolean("settings.entity.mob-sound", true);
        enableEntityActivation = getBoolean("settings.entity.entity-activation", true);
        endermiteSpawning = getBoolean("settings.entity.endermite-spawning", true);
    }

    public static boolean enableLavaToCobblestone = true;

    private static void setEnableLavaToCobblestone() {
        enableLavaToCobblestone = getBoolean("settings.enable-lava-to-cobblestone", true);
    }

    public static boolean firePlayerMoveEvent = true; // Highly recommend disable this for lobby/limbo/minigames servers.

    private static void firePlayerMoveEvent() {
        firePlayerMoveEvent = getBoolean("settings.fire-player-move-event", true);
    }

    public static boolean disablePhysicsPlace = false;
    public static boolean disablePhysicsUpdate = false;

    private static void physics() {
        disablePhysicsPlace = getBoolean("settings.physics.disable-place", false);
        disablePhysicsUpdate = getBoolean("settings.physics.disable-update", false);
    }

    public static boolean doBlocksOperations = true;

    private static void doBlocksOperations() {
        doBlocksOperations = getBoolean("settings.block-operations", true);
    }

    public static boolean doChunkUnload = true;

    private static void doChunkUnload() {
        doChunkUnload = getBoolean("settings.unload-chunks", true);
    }

    public static int chunkThreads = 2; // PaperSpigot - Bumped value
    public static int playersPerThread = 50;

    private static void chunk() {
        chunkThreads = getInt("settings.chunks.threads", 2);
        playersPerThread = getInt("settings.chunks.players-per-thread", 50);
    }

    public static boolean enableTCPNODELAY = true;

    private static void enableTCPNODELAY() {
        enableTCPNODELAY = getBoolean("settings.enable-tcpnodelay", true);
    }

    public static boolean useFixedPoolForTNT = false;
    public static int fixedPoolSize = 500;

    private static void fixedPools() {
        useFixedPoolForTNT = getBoolean("settings.fixed-pools.use-fixed-pools-for-tnt", false);
        fixedPoolSize = getInt("settings.fixed-pools.size", 500);
    }
    public static boolean useFasterCannonTracker = true;

    private static void useFasterCannonTracker() {
        useFasterCannonTracker = getBoolean("settings.faster-cannon-tracker", true);
    }

    public static boolean disableSpongeAbsorption = false;

    private static void disableSpongeAbsorption() {
        disableSpongeAbsorption = getBoolean("settings.disable-sponge-absorption", false);
    }

    public static boolean fixEatWhileRunning = false;

    private static void fixEatWhileRunning() {
        fixEatWhileRunning = getBoolean("settings.fix-eat-while-running", false);
    }
}
