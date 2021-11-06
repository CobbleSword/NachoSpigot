package me.elier.nachospigot.config;

import dev.cobblesword.nachospigot.OldNachoConfig;
import dev.cobblesword.nachospigot.commons.FileUtils;
import org.bukkit.support.DummyServer;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationTests {

    @Test
    public void migrateFullConfig() {
        DummyServer.setup();
        FileUtils.toFile(new OldNachoConfig(), new File("nacho.json"));
        NachoConfig.init(new File("nacho.yml"));
    }

    @Test
    public void migratePartialModifiedConfig() throws IOException {
        DummyServer.setup();
        File file = new File("nacho.json");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("{ \"usePandaWire\": false }");
        writer.close();
        NachoConfig.init(new File("nacho.yml"));
        assert !NachoConfig.usePandaWire;
    }

    @Test
    public void migrateEmptyConfig() throws IOException {
        DummyServer.setup();
        new File("nacho.json").createNewFile();
        NachoConfig.init(new File("nacho.yml"));
    }

    @Test
    public void loadConfig() {
        DummyServer.setup();
        NachoConfig.init(new File("nacho.yml"));
    }
}
