package org.bukkit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DifficultyTest {
    @Test
    public void getByValue() {
        for (Difficulty difficulty : Difficulty.values()) {
            assertThat(Difficulty.getByValue(difficulty.getValue()), is(difficulty));
        }
    }
}
