package org.bukkit;

import net.minecraft.server.WorldType;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class WorldTypeTest {
    @Test
    public void testTypes() {
        for (WorldType type : WorldType.types) {
            if (type == null) continue;
            if (type == WorldType.DEBUG_ALL_BLOCK_STATES) continue; // Doesn't work anyway

            assertThat(type.name() + " has no Bukkit world", org.bukkit.WorldType.getByName(type.name()), is(not(nullValue())));
        }
    }
}
