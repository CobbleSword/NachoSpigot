package org.bukkit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EffectTest {
    @Test
    public void getById() {
        for (Effect effect : Effect.values()) {
            if (effect.getType() != Effect.Type.PARTICLE) {
                assertThat(Effect.getById(effect.getId()), is(effect));
            } else {
                assertThat(Effect.getByName(effect.getName()), is(effect));
            }
        }
    }
}
