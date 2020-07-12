package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnchantmentManager {

    private static final Random a = new Random();
    private static final EnchantmentManager.EnchantmentModifierProtection b = new EnchantmentManager.EnchantmentModifierProtection((EnchantmentManager.SyntheticClass_1) null);
    private static final EnchantmentManager.EnchantmentModifierDamage c = new EnchantmentManager.EnchantmentModifierDamage((EnchantmentManager.SyntheticClass_1) null);
    private static final EnchantmentManager.EnchantmentModifierThorns d = new EnchantmentManager.EnchantmentModifierThorns((EnchantmentManager.SyntheticClass_1) null);
    private static final EnchantmentManager.EnchantmentModifierArthropods e = new EnchantmentManager.EnchantmentModifierArthropods((EnchantmentManager.SyntheticClass_1) null);

    public static int getEnchantmentLevel(int i, ItemStack itemstack) {
        if (itemstack == null) {
            return 0;
        } else {
            NBTTagList nbttaglist = itemstack.getEnchantments();

            if (nbttaglist == null) {
                return 0;
            } else {
                for (int j = 0; j < nbttaglist.size(); ++j) {
                    short short0 = nbttaglist.get(j).getShort("id");
                    short short1 = nbttaglist.get(j).getShort("lvl");

                    if (short0 == i) {
                        return short1;
                    }
                }

                return 0;
            }
        }
    }

    public static Map<Integer, Integer> a(ItemStack itemstack) {
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        NBTTagList nbttaglist = itemstack.getItem() == Items.ENCHANTED_BOOK ? Items.ENCHANTED_BOOK.h(itemstack) : itemstack.getEnchantments();

        if (nbttaglist != null) {
            for (int i = 0; i < nbttaglist.size(); ++i) {
                short short0 = nbttaglist.get(i).getShort("id");
                short short1 = nbttaglist.get(i).getShort("lvl");

                linkedhashmap.put(Integer.valueOf(short0), Integer.valueOf(short1));
            }
        }

        return linkedhashmap;
    }

    public static void a(Map<Integer, Integer> map, ItemStack itemstack) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            int i = ((Integer) iterator.next()).intValue();
            Enchantment enchantment = Enchantment.getById(i);

            if (enchantment != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setShort("id", (short) i);
                nbttagcompound.setShort("lvl", (short) ((Integer) map.get(Integer.valueOf(i))).intValue());
                nbttaglist.add(nbttagcompound);
                if (itemstack.getItem() == Items.ENCHANTED_BOOK) {
                    Items.ENCHANTED_BOOK.a(itemstack, new WeightedRandomEnchant(enchantment, ((Integer) map.get(Integer.valueOf(i))).intValue()));
                }
            }
        }

        if (nbttaglist.size() > 0) {
            if (itemstack.getItem() != Items.ENCHANTED_BOOK) {
                itemstack.a("ench", (NBTBase) nbttaglist);
            }
        } else if (itemstack.hasTag()) {
            itemstack.getTag().remove("ench");
        }

    }

    public static int a(int i, ItemStack[] aitemstack) {
        if (aitemstack == null) {
            return 0;
        } else {
            int j = 0;
            ItemStack[] aitemstack1 = aitemstack;
            int k = aitemstack.length;

            for (int l = 0; l < k; ++l) {
                ItemStack itemstack = aitemstack1[l];
                int i1 = getEnchantmentLevel(i, itemstack);

                if (i1 > j) {
                    j = i1;
                }
            }

            return j;
        }
    }

    private static void a(EnchantmentManager.EnchantmentModifier enchantmentmanager_enchantmentmodifier, ItemStack itemstack) {
        if (itemstack != null) {
            NBTTagList nbttaglist = itemstack.getEnchantments();

            if (nbttaglist != null) {
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    short short0 = nbttaglist.get(i).getShort("id");
                    short short1 = nbttaglist.get(i).getShort("lvl");

                    if (Enchantment.getById(short0) != null) {
                        enchantmentmanager_enchantmentmodifier.a(Enchantment.getById(short0), short1);
                    }
                }

            }
        }
    }

    private static void a(EnchantmentManager.EnchantmentModifier enchantmentmanager_enchantmentmodifier, ItemStack[] aitemstack) {
        ItemStack[] aitemstack1 = aitemstack;
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack1[j];

            a(enchantmentmanager_enchantmentmodifier, itemstack);
        }

    }

    public static int a(ItemStack[] aitemstack, DamageSource damagesource) {
        EnchantmentManager.b.a = 0;
        EnchantmentManager.b.b = damagesource;
        a((EnchantmentManager.EnchantmentModifier) EnchantmentManager.b, aitemstack);
        if (EnchantmentManager.b.a > 25) {
            EnchantmentManager.b.a = 25;
        } else if (EnchantmentManager.b.a < 0) {
            EnchantmentManager.b.a = 0;
        }

        return (EnchantmentManager.b.a + 1 >> 1) + EnchantmentManager.a.nextInt((EnchantmentManager.b.a >> 1) + 1);
    }

    public static float a(ItemStack itemstack, EnumMonsterType enummonstertype) {
        EnchantmentManager.c.a = 0.0F;
        EnchantmentManager.c.b = enummonstertype;
        a((EnchantmentManager.EnchantmentModifier) EnchantmentManager.c, itemstack);
        return EnchantmentManager.c.a;
    }

    public static void a(EntityLiving entityliving, Entity entity) {
        EnchantmentManager.d.b = entity;
        EnchantmentManager.d.a = entityliving;
        if (entityliving != null) {
            a((EnchantmentManager.EnchantmentModifier) EnchantmentManager.d, entityliving.getEquipment());
        }

        if (entity instanceof EntityHuman) {
            a((EnchantmentManager.EnchantmentModifier) EnchantmentManager.d, entityliving.bA());
        }

    }

    public static void b(EntityLiving entityliving, Entity entity) {
        EnchantmentManager.e.a = entityliving;
        EnchantmentManager.e.b = entity;
        if (entityliving != null) {
            a((EnchantmentManager.EnchantmentModifier) EnchantmentManager.e, entityliving.getEquipment());
        }

        if (entityliving instanceof EntityHuman) {
            a((EnchantmentManager.EnchantmentModifier) EnchantmentManager.e, entityliving.bA());
        }

    }

    public static int a(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantment.KNOCKBACK.id, entityliving.bA());
    }

    public static int getFireAspectEnchantmentLevel(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantment.FIRE_ASPECT.id, entityliving.bA());
    }

    public static int getOxygenEnchantmentLevel(Entity entity) {
        return a(Enchantment.OXYGEN.id, entity.getEquipment());
    }

    public static int b(Entity entity) {
        return a(Enchantment.DEPTH_STRIDER.id, entity.getEquipment());
    }

    public static int getDigSpeedEnchantmentLevel(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantment.DIG_SPEED.id, entityliving.bA());
    }

    public static boolean hasSilkTouchEnchantment(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantment.SILK_TOUCH.id, entityliving.bA()) > 0;
    }

    public static int getBonusBlockLootEnchantmentLevel(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS.id, entityliving.bA());
    }

    public static int g(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantment.LUCK.id, entityliving.bA());
    }

    public static int h(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantment.LURE.id, entityliving.bA());
    }

    public static int getBonusMonsterLootEnchantmentLevel(EntityLiving entityliving) {
        return getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS.id, entityliving.bA());
    }

    public static boolean j(EntityLiving entityliving) {
        return a(Enchantment.WATER_WORKER.id, entityliving.getEquipment()) > 0;
    }

    public static ItemStack a(Enchantment enchantment, EntityLiving entityliving) {
        ItemStack[] aitemstack = entityliving.getEquipment();
        int i = aitemstack.length;

        for (int j = 0; j < i; ++j) {
            ItemStack itemstack = aitemstack[j];

            if (itemstack != null && getEnchantmentLevel(enchantment.id, itemstack) > 0) {
                return itemstack;
            }
        }

        return null;
    }

    public static int a(Random random, int i, int j, ItemStack itemstack) {
        Item item = itemstack.getItem();
        int k = item.b();

        if (k <= 0) {
            return 0;
        } else {
            if (j > 15) {
                j = 15;
            }

            int l = random.nextInt(8) + 1 + (j >> 1) + random.nextInt(j + 1);

            return i == 0 ? Math.max(l / 3, 1) : (i == 1 ? l * 2 / 3 + 1 : Math.max(l, j * 2));
        }
    }

    public static ItemStack a(Random random, ItemStack itemstack, int i) {
        List list = b(random, itemstack, i);
        boolean flag = itemstack.getItem() == Items.BOOK;

        if (flag) {
            itemstack.setItem(Items.ENCHANTED_BOOK);
        }

        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) iterator.next();

                if (flag) {
                    Items.ENCHANTED_BOOK.a(itemstack, weightedrandomenchant);
                } else {
                    itemstack.addEnchantment(weightedrandomenchant.enchantment, weightedrandomenchant.level);
                }
            }
        }

        return itemstack;
    }

    public static List<WeightedRandomEnchant> b(Random random, ItemStack itemstack, int i) {
        Item item = itemstack.getItem();
        int j = item.b();

        if (j <= 0) {
            return null;
        } else {
            j /= 2;
            j = 1 + random.nextInt((j >> 1) + 1) + random.nextInt((j >> 1) + 1);
            int k = j + i;
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            int l = (int) ((float) k * (1.0F + f) + 0.5F);

            if (l < 1) {
                l = 1;
            }

            ArrayList arraylist = null;
            Map map = b(l, itemstack);

            if (map != null && !map.isEmpty()) {
                WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) WeightedRandom.a(random, map.values());

                if (weightedrandomenchant != null) {
                    arraylist = Lists.newArrayList();
                    arraylist.add(weightedrandomenchant);

                    for (int i1 = l; random.nextInt(50) <= i1; i1 >>= 1) {
                        Iterator iterator = map.keySet().iterator();

                        while (iterator.hasNext()) {
                            Integer integer = (Integer) iterator.next();
                            boolean flag = true;
                            Iterator iterator1 = arraylist.iterator();

                            while (true) {
                                if (iterator1.hasNext()) {
                                    WeightedRandomEnchant weightedrandomenchant1 = (WeightedRandomEnchant) iterator1.next();

                                    if (weightedrandomenchant1.enchantment.a(Enchantment.getById(integer.intValue()))) {
                                        continue;
                                    }

                                    flag = false;
                                }

                                if (!flag) {
                                    iterator.remove();
                                }
                                break;
                            }
                        }

                        if (!map.isEmpty()) {
                            WeightedRandomEnchant weightedrandomenchant2 = (WeightedRandomEnchant) WeightedRandom.a(random, map.values());

                            arraylist.add(weightedrandomenchant2);
                        }
                    }
                }
            }

            return arraylist;
        }
    }

    public static Map<Integer, WeightedRandomEnchant> b(int i, ItemStack itemstack) {
        Item item = itemstack.getItem();
        HashMap hashmap = null;
        boolean flag = itemstack.getItem() == Items.BOOK;
        Enchantment[] aenchantment = Enchantment.b;
        int j = aenchantment.length;

        for (int k = 0; k < j; ++k) {
            Enchantment enchantment = aenchantment[k];

            if (enchantment != null && (enchantment.slot.canEnchant(item) || flag)) {
                for (int l = enchantment.getStartLevel(); l <= enchantment.getMaxLevel(); ++l) {
                    if (i >= enchantment.a(l) && i <= enchantment.b(l)) {
                        if (hashmap == null) {
                            hashmap = Maps.newHashMap();
                        }

                        hashmap.put(Integer.valueOf(enchantment.id), new WeightedRandomEnchant(enchantment, l));
                    }
                }
            }
        }

        return hashmap;
    }

    static class SyntheticClass_1 {    }

    static final class EnchantmentModifierArthropods implements EnchantmentManager.EnchantmentModifier {

        public EntityLiving a;
        public Entity b;

        private EnchantmentModifierArthropods() {}

        public void a(Enchantment enchantment, int i) {
            enchantment.a(this.a, this.b, i);
        }

        EnchantmentModifierArthropods(EnchantmentManager.SyntheticClass_1 enchantmentmanager_syntheticclass_1) {
            this();
        }
    }

    static final class EnchantmentModifierThorns implements EnchantmentManager.EnchantmentModifier {

        public EntityLiving a;
        public Entity b;

        private EnchantmentModifierThorns() {}

        public void a(Enchantment enchantment, int i) {
            enchantment.b(this.a, this.b, i);
        }

        EnchantmentModifierThorns(EnchantmentManager.SyntheticClass_1 enchantmentmanager_syntheticclass_1) {
            this();
        }
    }

    static final class EnchantmentModifierDamage implements EnchantmentManager.EnchantmentModifier {

        public float a;
        public EnumMonsterType b;

        private EnchantmentModifierDamage() {}

        public void a(Enchantment enchantment, int i) {
            this.a += enchantment.a(i, this.b);
        }

        EnchantmentModifierDamage(EnchantmentManager.SyntheticClass_1 enchantmentmanager_syntheticclass_1) {
            this();
        }
    }

    static final class EnchantmentModifierProtection implements EnchantmentManager.EnchantmentModifier {

        public int a;
        public DamageSource b;

        private EnchantmentModifierProtection() {}

        public void a(Enchantment enchantment, int i) {
            this.a += enchantment.a(i, this.b);
        }

        EnchantmentModifierProtection(EnchantmentManager.SyntheticClass_1 enchantmentmanager_syntheticclass_1) {
            this();
        }
    }

    interface EnchantmentModifier {

        void a(Enchantment enchantment, int i);
    }
}
