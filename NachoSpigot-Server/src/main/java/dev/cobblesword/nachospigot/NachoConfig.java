package dev.cobblesword.nachospigot;

public class NachoConfig {
    public boolean saveEmptyScoreboardTeams = false;
    public boolean enableVersionCommand = true;
    public boolean enablePluginsCommand = true;
    public boolean enableReloadCommand = true;
    public boolean useFastOperators = false;
    public boolean patchProtocolLib = true;
    public boolean stopNotifyBungee = false;
    public boolean checkForMalware = false;
    public boolean shouldTickEnchantmentTables = true;
    public boolean usePandaWire = true;
    public boolean constantExplosions = false;
    public boolean explosionProtectedRegions = true;
    public int playerTimeStatisticsInterval = 20;
    public String serverBrandName = "NachoSpigot";
    public boolean enableAntiCrash = true;
    public boolean infiniteWaterSources = true;
    public boolean leavesDecayEvent = true;
    public boolean enableMobAI = true;
    public boolean enableMobSound = true;
    public boolean enableEntityActivation = true;
    public boolean enableLavaToCobblestone = true;
    public boolean firePlayerMoveEvent = true; // Highly recommend disable this for lobby/limbo/minigames servers.
    public boolean endermiteSpawning = true;
    public boolean disablePhysicsPlace = false;
    public boolean disablePhysicsUpdate = false;
    public boolean doBlocksOperations = true;
    public boolean doChunkUnload = true;
    public int chunkThreads = 2; // PaperSpigot - Bumped value
    public int playersPerThread = 50;
    public boolean enableTCPNODELAY = true;
    public int nettyBufferSize = 65535;
}
