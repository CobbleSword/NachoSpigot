package org.bukkit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CoalTypeTest {
    @Test
    public void getByData() {
        for (CoalType coalType : CoalType.values()) {
            assertThat(CoalType.getByData(coalType.getData()), is(coalType));
        }
    }
}
