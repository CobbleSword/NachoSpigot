package dev.cobblesword.nachospigot;

import dev.cobblesword.nachospigot.commons.FileUtils;

import java.io.File;

public class Nacho
{
    private static Nacho INSTANCE;
    private static final File CONFIG_FILE = new File("nacho.json");
    private NachoConfig config;

    public Nacho()
    {
        INSTANCE = this;

        this.config = new NachoConfig();
        while (!CONFIG_FILE.exists())
            FileUtils.toFile(this.config, CONFIG_FILE);
        this.config = FileUtils.toObject(CONFIG_FILE, NachoConfig.class);
    }

    public void reloadConfig()
    {
        this.config = FileUtils.toObject(CONFIG_FILE, NachoConfig.class);
    }

    public static Nacho get()
    {
        return INSTANCE;
    }

    public NachoConfig getConfig()
    {
        return config;
    }
}
