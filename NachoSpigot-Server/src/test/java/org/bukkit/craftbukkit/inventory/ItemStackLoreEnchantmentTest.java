package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Joiner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(Parameterized.class)
public class ItemStackLoreEnchantmentTest extends ItemStackTest {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, ItemStackTest.COMPOUND_MATERIALS);
    }

    static List<Object[]> operators() {
        return CompoundOperator.compound(
            Joiner.on('+'),
            NAME_PARAMETER,
            ~0L,
            Arrays.asList(
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.setLore(Arrays.asList("First Lore", "Second Lore"));
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Lore vs Null"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.setLore(Collections.singletonList("Some lore"));
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Lore vs Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.setLore(Arrays.asList("Some more lore", "Another lore"));
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.setLore(Collections.singletonList("Some more lore"));
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Lore vs Other"
                }
            ),
            Arrays.asList(
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.setDisplayName("TestItemName");
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Name vs Null"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.setDisplayName("AnotherItemName");
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Name vs Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.setDisplayName("The original ItemName");
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.setDisplayName("The other name");
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Name vs Other"
                }
            ),
            Arrays.asList(
                new Object[] {
                        (Operator) cleanStack -> {
                            cleanStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "EnchantStack vs Null"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            cleanStack.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "EnchantStack vs Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            cleanStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            cleanStack.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
                            return cleanStack;
                        },
                    "EnchantStack vs OtherEnchantStack"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.addEnchant(Enchantment.DURABILITY, 1, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Enchant vs Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Enchant vs Null"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.addEnchant(Enchantment.PROTECTION_FIRE, 1, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            meta.addEnchant(Enchantment.PROTECTION_FIRE, 2, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Enchant vs Other"
                }
            ),
            Arrays.asList(
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            ((Repairable) meta).setRepairCost(42);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Repair vs Null"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            ((Repairable) meta).setRepairCost(36);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Repair vs Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            ((Repairable) meta).setRepairCost(89);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            ItemMeta meta = cleanStack.getItemMeta();
                            ((Repairable) meta).setRepairCost(88);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Repair vs Other"
                }
            )
        );
    }
}
