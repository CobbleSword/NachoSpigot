package org.bukkit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InstrumentTest {
    @Test
    public void getByType() {
        for (Instrument instrument : Instrument.values()) {
            assertThat(Instrument.getByType(instrument.getType()), is(instrument));
        }
    }
}
