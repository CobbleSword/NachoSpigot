package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Joiner;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class ItemStackEnchantStorageTest extends ItemStackTest {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, Material.ENCHANTED_BOOK);
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
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                            meta.addStoredEnchant(Enchantment.DURABILITY, 1, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Enchantable vs Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                            meta.addStoredEnchant(Enchantment.KNOCKBACK, 1, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Enchantable vs Null"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                            meta.addStoredEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                            meta.addStoredEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
                            meta.addStoredEnchant(Enchantment.FIRE_ASPECT, 1, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Enchantable vs More"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                            meta.addStoredEnchant(Enchantment.PROTECTION_FIRE, 1, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                            meta.addEnchant(Enchantment.PROTECTION_FIRE, 2, true);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Enchantable vs Other"
                }
            )
        );
    }
}
