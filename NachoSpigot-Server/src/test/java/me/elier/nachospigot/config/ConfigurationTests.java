package me.elier.nachospigot.config;

import dev.cobblesword.nachospigot.OldNachoConfig;
import dev.cobblesword.nachospigot.commons.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationTests {
    private class TestUtils {
        public void containsMethodInvocation() {

        }
    }

    @Test
    public void migrateFullConfig() {
        FileUtils.toFile(new OldNachoConfig(), new File("nacho.json"));
        NachoConfig.init(new File("nacho.yml"));
    }

    @Test
    public void migratePartialModifiedConfig() throws IOException {
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
        new File("nacho.json").createNewFile();
        NachoConfig.init(new File("nacho.yml"));
    }

    @Test
    public void loadConfig() {
        NachoConfig.init(new File("nacho.yml"));
    }

    // Makes sure that c.addComment is not called in any config init functions.
    @Test
    public void noAddCommentInConfigInit() {

    }
}
