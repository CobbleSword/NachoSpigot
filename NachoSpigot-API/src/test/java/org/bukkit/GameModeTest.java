package org.bukkit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GameModeTest {
    @Test
    public void getByValue() {
        for (GameMode gameMode : GameMode.values()) {
            assertThat(GameMode.getByValue(gameMode.getValue()), is(gameMode));
        }
    }
}
