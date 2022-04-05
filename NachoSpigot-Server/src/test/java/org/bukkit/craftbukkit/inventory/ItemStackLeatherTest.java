package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Joiner;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class ItemStackLeatherTest extends ItemStackTest {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS);
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
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            meta.setColor(Color.FUCHSIA);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Color vs Null"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            meta.setColor(Color.GRAY);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Color vs Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            meta.setColor(Color.MAROON);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                            meta.setColor(Color.ORANGE);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Color vs Other"
                }
            )
        );
    }
}
