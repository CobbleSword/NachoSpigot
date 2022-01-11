package dev.cobblesword.nachospigot;

import com.google.common.util.concurrent.MoreExecutors;
import org.bukkit.support.DummyServer;
import org.junit.Test;

public class GuavaInjectorTests {
    @Test
    public void sameThreadExecutor() throws ReflectiveOperationException {
        DummyServer.setup();
        GuavaInjector.load();
        MoreExecutors.class.getDeclaredMethod("sameThreadExecutor").invoke(null);
    }
}
