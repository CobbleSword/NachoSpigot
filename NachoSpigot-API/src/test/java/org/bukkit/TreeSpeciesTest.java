package org.bukkit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TreeSpeciesTest {
    @Test
    public void getByData() {
        for (TreeSpecies treeSpecies : TreeSpecies.values()) {
            assertThat(TreeSpecies.getByData(treeSpecies.getData()), is(treeSpecies));
        }
    }
}
