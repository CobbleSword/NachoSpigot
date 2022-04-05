package org.bukkit;

import org.bukkit.craftbukkit.CraftSound;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class SoundTest {

    @Test
    public void testGetSound() {
        for (Sound sound : Sound.values()) {
            assertThat(sound.name(), CraftSound.getSound(sound), is(not(nullValue())));
        }
    }
}
