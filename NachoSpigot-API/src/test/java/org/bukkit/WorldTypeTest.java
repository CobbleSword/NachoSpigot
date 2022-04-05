package org.bukkit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WorldTypeTest {
    @Test
    public void getByName() {
        for (WorldType worldType : WorldType.values()) {
            assertThat(WorldType.getByName(worldType.getName()), is(worldType));
        }
    }
}
