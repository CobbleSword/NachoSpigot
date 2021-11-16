package me.elier.nachospigot.config;

import com.google.common.base.Throwables;
import dev.cobblesword.nachospigot.OldNachoConfig;
import dev.cobblesword.nachospigot.commons.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.sugarcanemc.sugarcane.util.yaml.YamlCommenter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.List;

public class NachoConfig {

    private static final Logger LOGGER = LogManager.getLogger(NachoConfig.class);
    private static File CONFIG_FILE;
    protected static final YamlCommenter c = new YamlCommenter();
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
        config = new YamlConfiguration();
        try {
            System.out.println("Loading NachoSpigot config from " + configFile.getName());
            config.load(CONFIG_FILE);
        } catch (IOException ignored) {
        } catch (InvalidConfigurationException ex) {
            LOGGER.log(Level.ERROR, "Could not load nacho.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().copyDefaults(true);
        File old_config = new File("nacho.json");
        if(old_config.exists()) migrate(old_config);

        int configVersion = 4; // Update this every new configuration update
        version = getInt("config-version", configVersion);
        set("config-version", configVersion);
        c.setHeader(HEADER);
        c.addComment("config-version", "Configuration version, do NOT modify this!");
        readConfig(NachoConfig.class, null);
    }

    private static void migrate(File old_config) {
        OldNachoConfig nachoJson = FileUtils.toObject(old_config, OldNachoConfig.class);
        if(nachoJson == null) {old_config.delete(); return;}
        set("settings.save-empty-scoreboard-teams", nachoJson.saveEmptyScoreboardTeams);
        set("settings.commands.enable-version-command", nachoJson.enableVersionCommand);
        set("settings.commands.enable-plugins-command", nachoJson.enablePluginsCommand);
        set("settings.commands.enable-reload-command", nachoJson.enableReloadCommand);
        set("settings.fast-operators", nachoJson.useFastOperators);
        set("settings.patch-protocollib", nachoJson.patchProtocolLib);
        set("settings.stop-notify-bungee", nachoJson.stopNotifyBungee);
        set("settings.anti-malware", nachoJson.checkForMalware);
        set("settings.kick-on-illegal-behavior", nachoJson.kickOnIllegalBehavior);
        set("world-settings.default.tick-enchantment-tables", nachoJson.shouldTickEnchantmentTables);
        set("settings.panda-wire", nachoJson.usePandaWire);
        set("world-settings.default.explosions.constant-radius", nachoJson.constantExplosions);
        set("world-settings.default.explosions.explode-protected-regions", nachoJson.explosionProtectedRegions);
        set("settings.event.fire-entity-explode-event", nachoJson.fireEntityExplodeEvent);
        set("world-settings.default.explosions.reduced-density-rays", nachoJson.reducedDensityRays);
        set("settings.player-time-statistics-interval", nachoJson.playerTimeStatisticsInterval);
        set("settings.brand-name", nachoJson.serverBrandName);
        set("settings.stop-decoding-itemstack-on-place", nachoJson.stopDecodingItemStackOnPlace);
        set("settings.anti-crash", nachoJson.enableAntiCrash);
        set("world-settings.default.infinite-water-sources", nachoJson.infiniteWaterSources);
        set("settings.event.fire-leaf-decay-event", nachoJson.leavesDecayEvent);
        set("world-settings.default.entity.mob-ai", nachoJson.enableMobAI);
        set("world-settings.default.entity.mob-sound", nachoJson.enableMobSound);
        set("world-settings.default.entity.entity-activation", nachoJson.enableEntityActivation);
        set("world-settings.default.entity.endermite-spawning", nachoJson.endermiteSpawning);
        set("world-settings.default.enable-lava-to-cobblestone", nachoJson.enableLavaToCobblestone);
        set("settings.event.fire-player-move-event", nachoJson.firePlayerMoveEvent);
        set("world-settings.default.physics.disable-place", nachoJson.disablePhysicsPlace);
        set("world-settings.default.physics.disable-update", nachoJson.disablePhysicsUpdate);
        set("world-settings.default.block-operations", nachoJson.doBlocksOperations);
        set("world-settings.default.unload-chunks", nachoJson.doChunkUnload);
        set("settings.chunk.threads", nachoJson.chunkThreads);
        set("settings.chunk.players-per-thread", nachoJson.playersPerThread);
        set("settings.use-tcp-nodelay", nachoJson.enableTCPNODELAY);
        set("settings.fixed-pools.use-fixed-pools-for-explosions", nachoJson.useFixedPoolForTNT);
        set("settings.fixed-pools.size", nachoJson.fixedPoolSize);
        set("settings.faster-cannon-tracker", nachoJson.useFasterCannonTracker);
        set("world-settings.default.disable-sponge-absorption", nachoJson.disableSpongeAbsorption);
        set("settings.fix-eat-while-running", nachoJson.fixEatWhileRunning);
        set("settings.hide-projectiles-from-hidden-players", nachoJson.hideProjectilesFromHiddenPlayers);
        set("settings.instant-use-entity", nachoJson.hideProjectilesFromHiddenPlayers);
        old_config.delete();
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
                        LOGGER.log(Level.ERROR, "Error invoking " + method, ex);
                    }
                }
            }
        }

        try {
            config.save(CONFIG_FILE);
            //c.saveComments(CONFIG_FILE);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, "Could not save " + CONFIG_FILE, ex);
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
        return config.getList(path, config.getList(path));
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    public static boolean saveEmptyScoreboardTeams;

    private static void saveEmptyScoreboardTeams() {
        saveEmptyScoreboardTeams = getBoolean("settings.save-empty-scoreboard-teams", false);
        c.addComment("settings.save-empty-scoreboard-teams", "Toggles whether or not the server should save empty scoreboard teams");
    }
    public static boolean enableVersionCommand;
    public static boolean enablePluginsCommand;
    public static boolean enableReloadCommand;

    private static void commands() {
        enableVersionCommand = getBoolean("settings.commands.enable-version-command", true);
        c.addComment("settings.commands.enable-version-command", "Toggles the /version command");
        enablePluginsCommand = getBoolean("settings.commands.enable-plugins-command", true);
        c.addComment("settings.commands.enable-plugins-command", "Toggles the /plugins command");
        enableReloadCommand = getBoolean("settings.commands.enable-reload-command", true);
        c.addComment("settings.commands.enable-reload-command", "Toggles the /reload command");
    }

    public static boolean useFastOperators;

    private static void useFastOperators() {
        useFastOperators = getBoolean("settings.fast-operators", false);
        c.addComment("settings.fast-operators", "Enables Fast Operators, which uses a faster method for managing operators");
    }
    public static boolean patchProtocolLib;

    private static void patchProtocolLib() {
        patchProtocolLib = getBoolean("settings.patch-protocollib", true);
        c.addComment("settings.patch-protocollib", "Enables the ProtocolLib runtime patch (not required on ProtocolLib version 4.7+)");
    }
    public static boolean stopNotifyBungee;

    private static void stopNotifyBungee() {
        stopNotifyBungee = getBoolean("settings.stop-notify-bungee", false);
        c.addComment("settings.stop-notify-bungee", "Disables the firewall check when running BungeeCord");
    }
    public static boolean checkForMalware;

    private static void antiMalware() {
        checkForMalware = getBoolean("settings.anti-malware", false);
        c.addComment("settings.anti-malware", "Enables the built-in anti malware feature");
    }

    public static boolean kickOnIllegalBehavior;

    private static void kickOnIllegalBehavior() {
        kickOnIllegalBehavior = getBoolean("settings.kick-on-illegal-behavior", true);
        c.addComment("settings.kick-on-illegal-behavior", "Kicks players if they try to do an illegal action (e.g. using a creative mode action while not in creative mode.)");
    }

    public static boolean usePandaWire;

    private static void usePandaWire() {
        usePandaWire = getBoolean("settings.panda-wire", true);
        c.addComment("settings.panda-wire", "Optimizes redstone wires.");
    }

    public static boolean fireEntityExplodeEvent;
    public static boolean firePlayerMoveEvent;
    public static boolean leavesDecayEvent;

    private static void fireEntityExplodeEvent() {
        fireEntityExplodeEvent = getBoolean("settings.event.fire-entity-explode-event", true);
        c.addComment("settings.event.fire-entity-explode-event", "Toggles the entity explode event");
        firePlayerMoveEvent = getBoolean("settings.event.fire-player-move-event", true);
        c.addComment("settings.event.fire-player-move-event", "Toggles the player move event");
        leavesDecayEvent = getBoolean("settings.event.fire-leaf-decay-event", true);
        c.addComment("settings.event.fire-leaf-decay-event", "Toggles the leaf decay event");
    }

    public static int playerTimeStatisticsInterval;

    private static void playerTimeStatisticsInterval() {
        playerTimeStatisticsInterval = getInt("settings.player-time-statistics-interval", 20);
        c.addComment("settings.player-time-statistics-interval", "Changes when statistics are ticked (e.g. 20 would be every 20th tick)");
    }

    public static String serverBrandName;

    private static void serverBrandName() {
        serverBrandName = getString("settings.brand-name", "NachoSpigot");
        c.addComment("settings.brand-name", "Changes the brand name of the server.\nThis will show in statistics, server lists, client crashes,\n and in the client debug screen. (accessed by pressing F3)");
    }

    public static boolean stopDecodingItemStackOnPlace;

    private static void stopDecodingItemStackOnPlace() {
        stopDecodingItemStackOnPlace = getBoolean("settings.stop-decoding-itemstack-on-place", true);
        c.addComment("settings.stop-decoding-itemstack-on-place", "Disables decoding itemstacks when not needed");
    }

    public static boolean enableAntiCrash;

    private static void enableAntiCrash() {
        enableAntiCrash = getBoolean("settings.anti-crash", true);
        c.addComment("settings.anti-crash", "Kicks players if they try to do an action that would/might crash the server");
    }

    public static int chunkThreads; // PaperSpigot - Bumped value
    public static int playersPerThread;

    private static void chunk() {
        chunkThreads = getInt("settings.chunk.threads", 2);
        c.addComment("settings.chunk.threads", "The amount of threads used for chunks");
        playersPerThread = getInt("settings.chunk.players-per-thread", 50);
        c.addComment("settings.chunk.players-per-thread", "The amount of players for each thread");
    }

    public static boolean enableTCPNODELAY;

    private static void enableTCPNODELAY() {
        enableTCPNODELAY = getBoolean("settings.use-tcp-nodelay", true);
        c.addComment("settings.use-tcp-nodelay", "Enables the TCP_NODELAY socket option");
    }

    public static boolean useFixedPoolForTNT;
    public static int fixedPoolSize;

    private static void fixedPools() {
        useFixedPoolForTNT = getBoolean("settings.fixed-pools.use-fixed-pools-for-explosions", false);
        c.addComment("settings.fixed-pools.use-fixed-pools-for-explosions", "Enables fixed thread pool for explosions");
        fixedPoolSize = getInt("settings.fixed-pools.size", 500);
        c.addComment("settings.fixed-pools.size", "The size for the fixed thread pool for explosions.");
    }
    public static boolean useFasterCannonTracker;

    private static void useFasterCannonTracker() {
        useFasterCannonTracker = getBoolean("settings.faster-cannon-tracker", true);
        c.addComment("settings.faster-cannon-tracker", "Enables a faster cannon entity tracker");
    }

    public static boolean disableTracking;

    private static void disableTracking() {
        disableTracking = getBoolean("settings.disable-tracking", false);
        c.addComment("settings.disable-tracking", "Disable entity tracking");
    }

    public static int trackingThreads;

    private static void trackingThreads() {
        trackingThreads = getInt("settings.tracking-threads", 4);
        c.addComment("settings.tracking-threads", "Entity Tracking Threads");
    }

    public static boolean fixEatWhileRunning;

    private static void fixEatWhileRunning() {
        fixEatWhileRunning = getBoolean("settings.fix-eat-while-running", false);
        c.addComment("settings.fix-eat-while-running", "Fixes the eating while running bug");
    }

    public static boolean hideProjectilesFromHiddenPlayers;

    private static void hideProjectilesFromHiddenPlayers() {
        hideProjectilesFromHiddenPlayers = getBoolean("settings.hide-projectiles-from-hidden-players", false);
        c.addComment("settings.hide-projectiles-from-hidden-players", "Hides projectiles from hidden players");
    }

    public static boolean antiEnderPearlGlitch;
    
    private static void antiEnderPearlGlitch() {
        antiEnderPearlGlitch = getBoolean("settings.anti-enderpearl-glitch", false);
        c.addComment("settings.anti-enderpearl-glitch", "Enables anti enderpearl glitch");
    }

    public static boolean disabledFallBlockAnimation;

    private static void disableFallAnimation() {
        disabledFallBlockAnimation = getBoolean("settings.disabled-block-fall-animation", false);
        c.addComment("settings.disabled-block-fall-animation", "Disables the fall animation for blocks");
    }

    public static boolean disableInfiniSleeperThreadUsage;

    private static void disableInfiniSleeperThreadUsage() {
        disableInfiniSleeperThreadUsage = getBoolean("settings.disable-infinisleeper-thread-usage", false);
        c.addComment("settings.disable-infinisleeper-thread-usage", "Disable infinisleeper thread usage, just enable this if you know what are you doing.");
    }

    public static boolean enableFastMath;

    private static void enableFastMath() {
        enableFastMath = getBoolean("settings.enable-fastmath", false);
        c.addComment("settings.enable-fastmath", "Enable Fast Math usage.");
    }

    public static boolean enableFastMathCosSin;

    private static void enableFastMathCosSin() {
        enableFastMathCosSin = getBoolean("settings.enable-fastmath-cos-sin", false);
        c.addComment("settings.enable-fastmath-cos-sin", "Enable Fast Math usage with cos() and sin() methods, this may break anticheats and server-side calculations.");
    }

    public static int tileEntityTickingTime;

    private static void tileEntityTickingTime() {
        tileEntityTickingTime = getInt("settings.tile-entity-ticking-time", 20);
        c.addComment("settings.tile-entity-ticking-time", "Ticking time (20 ticks per second) for usage on tile entity operations.");
    }

    public static int itemDirtyTicks;

    private static void itemDirtyTicks() {
        itemDirtyTicks = getInt("settings.item-dirty-ticks", 20);
        c.addComment("settings.item-dirty-ticks", "Controls the interval for the item-dirty check. Minecraft checks an item every tick to see if it was changed. This can be expensive because it also needs to check all NBT data. Spigot only checks for basic count/data/type data and does a deep check every 20 ticks by default.");
    }

    public static boolean enableTcpFastOpen;

    private static void enableTcpFastOpen() {
        enableTcpFastOpen = getBoolean("settings.use-tcp-fastopen", true);
        c.addComment("settings.use-tcp-fastopen", "Enables the TCP_FASTOPEN socket option");
    }

    public static int modeTcpFastOpen;

    private static void modeTcpFastOpen() {
        modeTcpFastOpen = getInt("settings.tcp-fastopen-mode", 1);
        c.addComment("settings.use-tcp-fastopen", "Options: 0 - Disabled.; 1 - TFO is enabled for outgoing connections (clients).; 2 - TFO is enabled for incoming connections (servers).; 3 - TFO is enabled for both clients and servers.");
    }

    public static boolean enableProtocolLibShim;

    private static void enableProtocolLibShim() {
        enableProtocolLibShim = getBoolean("settings.enable-protocollib-shim", true);
        c.addComment("settings.enable-protocollib-shim", "Enable ProtocolLib network shim. Allows ProtocolLib to work, but requires extra memory. Disable this if you don't use ProtocolLib!");
    }

    public static boolean instantPlayInUseEntity;

    private static void instantPlayInUseEntity() {
        instantPlayInUseEntity = getBoolean("settings.instant-interaction", false);
        c.addComment("settings.instant-interaction", "Disables delay of all interactions");
    }
}
