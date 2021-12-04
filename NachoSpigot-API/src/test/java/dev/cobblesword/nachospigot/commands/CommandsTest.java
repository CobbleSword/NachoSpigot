package dev.cobblesword.nachospigot.commands;

import org.bukkit.Server;
import org.bukkit.TestServer;
import org.junit.Test;

/**
 * @author Elierrr
 */
public class CommandsTest {

    @Test
    public void isHelpEnabled() {
        Server instance = TestServer.getInstance();
        assert instance.getCommandMap().getCommand("help") == null;
    }

    @Test
    public void isReloadEnabled() {
        Server instance = TestServer.getInstance();
        assert instance.getCommandMap().getCommand("reload") == null;
    }

    @Test
    public void isPluginsEnabled() {
        Server instance = TestServer.getInstance();
        assert instance.getCommandMap().getCommand("plugins") == null;
    }
}
