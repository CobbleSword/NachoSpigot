package dev.cobblesword.nachospigot.knockback;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class KnockbackConfig {
    private File configFile = new File("knockback.yml");
    private YamlConfiguration config;

    private KnockbackProfile currentKb;
    private Set<KnockbackProfile> kbProfiles = new HashSet<>();

    public KnockbackConfig() {
        init();
        try {
            this.config.save(this.configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + this.configFile, ex);
        }
    }

    private void init() {
        if (!this.configFile.exists()) {
            try {
                this.configFile.createNewFile();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        this.config.options().copyDefaults(true);

        final KnockbackProfile defaultProfile = new CraftKnockbackProfile("Default");

        this.kbProfiles = new HashSet<>();
        this.kbProfiles.add(defaultProfile);

        for (String key : this.getKeys("knockback.profiles")) {
            final String path = "knockback.profiles." + key;
            CraftKnockbackProfile profile = (CraftKnockbackProfile) getKbProfileByName(key);
            if (profile == null) {
                profile = new CraftKnockbackProfile(key);
                this.kbProfiles.add(profile);
            }
            profile.setStopSprint(this.getBoolean(path + ".stop-sprint", true));
            profile.setFrictionHorizontal(this.getDouble(path + ".friction-horizontal", 0.5D));
            profile.setFrictionVertical(this.getDouble(path + ".friction-vertical", 0.5D));
            profile.setHorizontal(this.getDouble(path + ".horizontal", 0.4D));
            profile.setVertical(this.getDouble(path + ".vertical", 0.4D));
            profile.setVerticalMax(this.getDouble(path + ".vertical-max", 0.4D));
            profile.setVerticalMin(this.getDouble(path + ".vertical-min", -1.0D));
            profile.setExtraHorizontal(this.getDouble(path + ".extra-horizontal", 0.5D));
            profile.setExtraVertical(this.getDouble(path + ".extra-vertical", 0.05D));


            profile.setRodHorizontal(this.getDouble(path + ".projectiles.rod.horizontal", 0.4D));
            profile.setRodVertical(this.getDouble(path + ".projectiles.rod.vertical", 0.4D));
            profile.setArrowHorizontal(this.getDouble(path + ".projectiles.arrow.horizontal", 0.4D));
            profile.setArrowVertical(this.getDouble(path + ".projectiles.arrow.vertical", 0.4D));
            profile.setPearlHorizontal(this.getDouble(path + ".projectiles.pearl.horizontal", 0.4D));
            profile.setPearlVertical(this.getDouble(path + ".projectiles.pearl.vertical", 0.4D));
            profile.setSnowballHorizontal(this.getDouble(path + ".projectiles.snowball.horizontal", 0.4D));
            profile.setSnowballVertical(this.getDouble(path + ".projectiles.snowball.vertical", 0.4D));
            profile.setEggHorizontal(this.getDouble(path + ".projectiles.egg.horizontal", 0.4D));
            profile.setEggHorizontal(this.getDouble(path + ".projectiles.egg.vertical", 0.4D));
        }
        this.currentKb = this.getKbProfileByName(this.getString("knockback.current", "default"));
        if (this.currentKb == null) {
            this.currentKb = defaultProfile;
        }
    }

    public KnockbackProfile getCurrentKb() {
        return this.currentKb;
    }

    public void setCurrentKb(KnockbackProfile kb) {
        this.currentKb = kb;
    }

    public KnockbackProfile getKbProfileByName(String name) {
        for (KnockbackProfile profile : this.kbProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }

    public Set<KnockbackProfile> getKbProfiles() {
        return this.kbProfiles;
    }

    public void save() {
        try {
            this.config.save(this.configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object val) {
        this.config.set(path, val);

        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys(String path) {
        if (!this.config.isConfigurationSection(path)) {
            this.config.createSection(path);
            return new HashSet<>();
        }

        return this.config.getConfigurationSection(path).getKeys(false);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, this.config.getBoolean(path));
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, this.config.getDouble(path));
    }

    public float getFloat(String path, float def) {
        return (float) this.getDouble(path, def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return config.getInt(path, this.config.getInt(path));
    }

    public <T> List getList(String path, T def) {
        this.config.addDefault(path, def);
        return this.config.getList(path, this.config.getList(path));
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, this.config.getString(path));
    }
}
