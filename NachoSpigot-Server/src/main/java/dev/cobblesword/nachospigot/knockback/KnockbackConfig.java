package dev.cobblesword.nachospigot.knockback;

import com.google.common.base.Throwables;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KnockbackConfig {
    private static final Logger LOGGER = LogManager.getLogger(KnockbackConfig.class);
    private static File CONFIG_FILE;
    //private static final YamlCommenter c = new YamlCommenter();
    private static final String HEADER = "This is the knockback configuration file for NachoSpigot.\n"
            + "As you can see, there isn't much to configure. To add/remove/edit a knockback profile\n"
            + "you must use the /kb command. Note that this will impact gameplay so use caution and \n"
            + "make sure you know what each option does before configuring.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to NachoSpigot,\n"
            + "feel free to join us in our Discord.\n"
            + "\n"
            + "Discord: https://discord.gg/SBTEbSx\n";
    static YamlConfiguration config;

    private static KnockbackProfile currentKb;
    private static Set<KnockbackProfile> kbProfiles = new HashSet<>();

    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        try {
            KnockbackConfig.LOGGER.info("Loading NachoSpigot knockback config from " + configFile.getName());
            config.load(CONFIG_FILE);
        } catch (IOException ignored) {
        } catch (InvalidConfigurationException ex) {
            LOGGER.log(Level.ERROR, "Could not load knockback.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().copyDefaults(true);
        config.options().header(HEADER);

        final KnockbackProfile defaultProfile = new CraftKnockbackProfile("Default");

        kbProfiles = new HashSet<>();
        kbProfiles.add(defaultProfile);

        for (String key : getKeys("knockback.profiles")) {
            final String path = "knockback.profiles." + key;
            CraftKnockbackProfile profile = (CraftKnockbackProfile) getKbProfileByName(key);
            if (profile == null) {
                profile = new CraftKnockbackProfile(key);
                kbProfiles.add(profile);
            }
            profile.setStopSprint(getBoolean(path + ".stop-sprint", true));
            profile.setFrictionHorizontal(getDouble(path + ".friction-horizontal", 0.5D));
            profile.setFrictionVertical(getDouble(path + ".friction-vertical", 0.5D));
            profile.setHorizontal(getDouble(path + ".horizontal", 0.4D));
            profile.setVertical(getDouble(path + ".vertical", 0.4D));
            profile.setVerticalMax(getDouble(path + ".vertical-max", 0.4D));
            profile.setVerticalMin(getDouble(path + ".vertical-min", -1.0D));
            profile.setExtraHorizontal(getDouble(path + ".extra-horizontal", 0.5D));
            profile.setExtraVertical(getDouble(path + ".extra-vertical", 0.05D));


            profile.setRodHorizontal(getDouble(path + ".projectiles.rod.horizontal", 0.4D));
            profile.setRodVertical(getDouble(path + ".projectiles.rod.vertical", 0.4D));
            profile.setArrowHorizontal(getDouble(path + ".projectiles.arrow.horizontal", 0.4D));
            profile.setArrowVertical(getDouble(path + ".projectiles.arrow.vertical", 0.4D));
            profile.setPearlHorizontal(getDouble(path + ".projectiles.pearl.horizontal", 0.4D));
            profile.setPearlVertical(getDouble(path + ".projectiles.pearl.vertical", 0.4D));
            profile.setSnowballHorizontal(getDouble(path + ".projectiles.snowball.horizontal", 0.4D));
            profile.setSnowballVertical(getDouble(path + ".projectiles.snowball.vertical", 0.4D));
            profile.setEggHorizontal(getDouble(path + ".projectiles.egg.horizontal", 0.4D));
            profile.setEggVertical(getDouble(path + ".projectiles.egg.vertical", 0.4D));
        }
        currentKb = getKbProfileByName(getString("knockback.current", "default"));
        if (currentKb == null) {
            currentKb = defaultProfile;
        }
        save();
    }

    public static KnockbackProfile getCurrentKb() {
        return currentKb;
    }

    public static void setCurrentKb(KnockbackProfile kb) {
        currentKb = kb;
    }

    public static KnockbackProfile getKbProfileByName(String name) {
        for (KnockbackProfile profile : kbProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }

    public static Set<KnockbackProfile> getKbProfiles() {
        return kbProfiles;
    }

    public static void save() {
        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, "Could not save " + CONFIG_FILE, ex);
        }
    }

    public static void set(String path, Object val) {
        config.set(path, val);

        save();
    }

    public static Set<String> getKeys(String path) {
        if (!config.isConfigurationSection(path)) {
            config.createSection(path);
            return new HashSet<>();
        }

        return config.getConfigurationSection(path).getKeys(false);
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
}
