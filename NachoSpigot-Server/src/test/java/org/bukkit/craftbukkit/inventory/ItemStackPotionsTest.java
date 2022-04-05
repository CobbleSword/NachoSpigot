package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Joiner;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class ItemStackPotionsTest extends ItemStackTest {

    @Parameters(name="[{index}]:{" + NAME_PARAMETER + "}")
    public static List<Object[]> data() {
        return StackProvider.compound(operators(), "%s %s", NAME_PARAMETER, Material.POTION);
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
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.CONFUSION.createEffect(1, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> cleanStack,
                    "Potion vs Null"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.HARM.createEffect(2, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Potion vs Blank"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.SLOW_DIGGING.createEffect(1, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.FAST_DIGGING.createEffect(1, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Potion vs Harder"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.JUMP.createEffect(1, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.JUMP.createEffect(1, 1), false);
                            meta.addCustomEffect(PotionEffectType.REGENERATION.createEffect(1, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Potion vs Better"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.SPEED.createEffect(10, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.SPEED.createEffect(5, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Potion vs Faster"
                },
                new Object[] {
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(1, 1), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                        (Operator) cleanStack -> {
                            final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                            meta.addCustomEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(1, 2), false);
                            cleanStack.setItemMeta(meta);
                            return cleanStack;
                        },
                    "Potion vs Stronger"
                }
            )
        );
    }
}
