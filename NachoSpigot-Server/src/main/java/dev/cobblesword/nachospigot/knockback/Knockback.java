package dev.cobblesword.nachospigot.knockback;

import dev.cobblesword.nachospigot.commons.FileUtils;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.defaults.nacho.KnockbackCommand;

import java.io.File;
import java.util.Collections;

public class Knockback {

    private static Knockback INSTANCE;

    private static final File CONFIG_FILE = new File("knockback.json");
    private KnockbackConfig config;

    public Knockback() {
        INSTANCE = this;
        this.config = new KnockbackConfig();
        while (!CONFIG_FILE.exists()) FileUtils.toFile(this.config, CONFIG_FILE);
        this.config = FileUtils.toObject(CONFIG_FILE, KnockbackConfig.class);
        this.saveConfig();
    }

    public void registerCommands() {
        KnockbackCommand knockbackCommand = new KnockbackCommand("knockback", Collections.singletonList("kb"));
        MinecraftServer.getServer().server.getCommandMap().register(knockbackCommand.getName(), "ns", knockbackCommand);
    }

    public void reloadConfig() {
        this.config = FileUtils.toObject(CONFIG_FILE, KnockbackConfig.class);
    }

    public void saveConfig() {
        FileUtils.toFile(this.config, CONFIG_FILE);
    }

    public static Knockback get() {
        return INSTANCE == null ? new Knockback() : INSTANCE;
    }

    public KnockbackConfig getConfig()
    {
        return config;
    }
}