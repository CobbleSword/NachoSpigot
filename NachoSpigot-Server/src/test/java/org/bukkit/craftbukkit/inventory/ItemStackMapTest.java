package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Joiner;
import org.bukkit.Material;
import org.bukkit.inventory.meta.MapMeta;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class ItemStackMapTest extends ItemStackTest {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, Material.MAP);
    }

    static List<Object[]> operators() {
        return CompoundOperator.compound(
            Joiner.on('+'),
            NAME_PARAMETER,
            Long.parseLong("10", 2),
            ItemStackLoreEnchantmentTest.operators(),
            Arrays.asList(
                new Object[] {
                        (Operator) cleanStack -> {
                            MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                            meta.setScaling(true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                            meta.setScaling(false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Scale vs. Unscale"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                            meta.setScaling(true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Scale vs. Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                            meta.setScaling(false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Unscale vs. Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                            meta.setScaling(true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Scale vs. None"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                            meta.setScaling(false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Unscale vs. None"
                }
            )
        );
    }
}
