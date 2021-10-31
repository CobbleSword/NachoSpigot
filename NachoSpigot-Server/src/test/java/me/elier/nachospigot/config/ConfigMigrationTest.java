package me.elier.nachospigot.config;

import dev.cobblesword.nachospigot.OldNachoConfig;
import dev.cobblesword.nachospigot.commons.FileUtils;
import org.junit.Test;

import java.io.File;

public class ConfigMigrationTest {

    @Test
    public void migrateFullConfig() {
        FileUtils.toFile(new OldNachoConfig(), new File("nacho.json"));
        NachoConfig.init(new File("nacho.yml"));
    }
}
