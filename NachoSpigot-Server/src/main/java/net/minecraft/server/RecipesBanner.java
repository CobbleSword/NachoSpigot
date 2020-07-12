package net.minecraft.server;

public class RecipesBanner {

    public RecipesBanner() {}

    void a(CraftingManager craftingmanager) {
        EnumColor[] aenumcolor = EnumColor.values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            craftingmanager.registerShapedRecipe(new ItemStack(Items.BANNER, 1, enumcolor.getInvColorIndex()), new Object[] { "###", "###", " | ", Character.valueOf('#'), new ItemStack(Blocks.WOOL, 1, enumcolor.getColorIndex()), Character.valueOf('|'), Items.STICK});
        }

        craftingmanager.a(new RecipesBanner.DuplicateRecipe((RecipesBanner.SyntheticClass_1) null));
        craftingmanager.a(new RecipesBanner.AddRecipe((RecipesBanner.SyntheticClass_1) null));
    }

    static class SyntheticClass_1 {    }

    static class AddRecipe extends ShapelessRecipes implements IRecipe { // CraftBukkit - added extends

        // CraftBukkit start - Delegate to new parent class with bogus info
        private AddRecipe() {
            super(new ItemStack(Items.BANNER, 0, 0), java.util.Arrays.asList(new ItemStack(Items.BANNER)));
        }
        // CraftBukkit end

        public boolean a(InventoryCrafting inventorycrafting, World world) {
            boolean flag = false;

            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                if (itemstack != null && itemstack.getItem() == Items.BANNER) {
                    if (flag) {
                        return false;
                    }

                    if (TileEntityBanner.c(itemstack) >= 6) {
                        return false;
                    }

                    flag = true;
                }
            }

            if (!flag) {
                return false;
            } else {
                return this.c(inventorycrafting) != null;
            }
        }

        public ItemStack craftItem(InventoryCrafting inventorycrafting) {
            ItemStack itemstack = null;

            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack1 = inventorycrafting.getItem(i);

                if (itemstack1 != null && itemstack1.getItem() == Items.BANNER) {
                    itemstack = itemstack1.cloneItemStack();
                    itemstack.count = 1;
                    break;
                }
            }

            TileEntityBanner.EnumBannerPatternType tileentitybanner_enumbannerpatterntype = this.c(inventorycrafting);

            if (tileentitybanner_enumbannerpatterntype != null) {
                int j = 0;

                ItemStack itemstack2;

                for (int k = 0; k < inventorycrafting.getSize(); ++k) {
                    itemstack2 = inventorycrafting.getItem(k);
                    if (itemstack2 != null && itemstack2.getItem() == Items.DYE) {
                        j = itemstack2.getData();
                        break;
                    }
                }

                NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag", true);

                itemstack2 = null;
                NBTTagList nbttaglist;

                if (nbttagcompound.hasKeyOfType("Patterns", 9)) {
                    nbttaglist = nbttagcompound.getList("Patterns", 10);
                } else {
                    nbttaglist = new NBTTagList();
                    nbttagcompound.set("Patterns", nbttaglist);
                }

                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setString("Pattern", tileentitybanner_enumbannerpatterntype.b());
                nbttagcompound1.setInt("Color", j);
                nbttaglist.add(nbttagcompound1);
            }

            return itemstack;
        }

        public int a() {
            return 10;
        }

        public ItemStack b() {
            return null;
        }

        public ItemStack[] b(InventoryCrafting inventorycrafting) {
            ItemStack[] aitemstack = new ItemStack[inventorycrafting.getSize()];

            for (int i = 0; i < aitemstack.length; ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                if (itemstack != null && itemstack.getItem().r()) {
                    aitemstack[i] = new ItemStack(itemstack.getItem().q());
                }
            }

            return aitemstack;
        }

        private TileEntityBanner.EnumBannerPatternType c(InventoryCrafting inventorycrafting) {
            TileEntityBanner.EnumBannerPatternType[] atileentitybanner_enumbannerpatterntype = TileEntityBanner.EnumBannerPatternType.values();
            int i = atileentitybanner_enumbannerpatterntype.length;

            for (int j = 0; j < i; ++j) {
                TileEntityBanner.EnumBannerPatternType tileentitybanner_enumbannerpatterntype = atileentitybanner_enumbannerpatterntype[j];

                if (tileentitybanner_enumbannerpatterntype.d()) {
                    boolean flag = true;
                    int k;

                    if (tileentitybanner_enumbannerpatterntype.e()) {
                        boolean flag1 = false;
                        boolean flag2 = false;

                        for (k = 0; k < inventorycrafting.getSize() && flag; ++k) {
                            ItemStack itemstack = inventorycrafting.getItem(k);

                            if (itemstack != null && itemstack.getItem() != Items.BANNER) {
                                if (itemstack.getItem() == Items.DYE) {
                                    if (flag2) {
                                        flag = false;
                                        break;
                                    }

                                    flag2 = true;
                                } else {
                                    if (flag1 || !itemstack.doMaterialsMatch(tileentitybanner_enumbannerpatterntype.f())) {
                                        flag = false;
                                        break;
                                    }

                                    flag1 = true;
                                }
                            }
                        }

                        if (!flag1) {
                            flag = false;
                        }
                    } else if (inventorycrafting.getSize() != tileentitybanner_enumbannerpatterntype.c().length * tileentitybanner_enumbannerpatterntype.c()[0].length()) {
                        flag = false;
                    } else {
                        int l = -1;

                        for (int i1 = 0; i1 < inventorycrafting.getSize() && flag; ++i1) {
                            k = i1 / 3;
                            int j1 = i1 % 3;
                            ItemStack itemstack1 = inventorycrafting.getItem(i1);

                            if (itemstack1 != null && itemstack1.getItem() != Items.BANNER) {
                                if (itemstack1.getItem() != Items.DYE) {
                                    flag = false;
                                    break;
                                }

                                if (l != -1 && l != itemstack1.getData()) {
                                    flag = false;
                                    break;
                                }

                                if (tileentitybanner_enumbannerpatterntype.c()[k].charAt(j1) == 32) {
                                    flag = false;
                                    break;
                                }

                                l = itemstack1.getData();
                            } else if (tileentitybanner_enumbannerpatterntype.c()[k].charAt(j1) != 32) {
                                flag = false;
                                break;
                            }
                        }
                    }

                    if (flag) {
                        return tileentitybanner_enumbannerpatterntype;
                    }
                }
            }

            return null;
        }

        AddRecipe(RecipesBanner.SyntheticClass_1 recipesbanner_syntheticclass_1) {
            this();
        }
    }

    static class DuplicateRecipe extends ShapelessRecipes implements IRecipe { // CraftBukkit - added extends

        // CraftBukkit start - Delegate to new parent class with bogus info
        private DuplicateRecipe() {
            super(new ItemStack(Items.BANNER, 0, 0), java.util.Arrays.asList(new ItemStack(Items.DYE, 0, 5)));
        }
        // CraftBukkit end

        public boolean a(InventoryCrafting inventorycrafting, World world) {
            ItemStack itemstack = null;
            ItemStack itemstack1 = null;

            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack2 = inventorycrafting.getItem(i);

                if (itemstack2 != null) {
                    if (itemstack2.getItem() != Items.BANNER) {
                        return false;
                    }

                    if (itemstack != null && itemstack1 != null) {
                        return false;
                    }

                    int j = TileEntityBanner.b(itemstack2);
                    boolean flag = TileEntityBanner.c(itemstack2) > 0;

                    if (itemstack != null) {
                        if (flag) {
                            return false;
                        }

                        if (j != TileEntityBanner.b(itemstack)) {
                            return false;
                        }

                        itemstack1 = itemstack2;
                    } else if (itemstack1 != null) {
                        if (!flag) {
                            return false;
                        }

                        if (j != TileEntityBanner.b(itemstack1)) {
                            return false;
                        }

                        itemstack = itemstack2;
                    } else if (flag) {
                        itemstack = itemstack2;
                    } else {
                        itemstack1 = itemstack2;
                    }
                }
            }

            return itemstack != null && itemstack1 != null;
        }

        public ItemStack craftItem(InventoryCrafting inventorycrafting) {
            for (int i = 0; i < inventorycrafting.getSize(); ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                if (itemstack != null && TileEntityBanner.c(itemstack) > 0) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.count = 1;
                    return itemstack1;
                }
            }

            return null;
        }

        public int a() {
            return 2;
        }

        public ItemStack b() {
            return null;
        }

        public ItemStack[] b(InventoryCrafting inventorycrafting) {
            ItemStack[] aitemstack = new ItemStack[inventorycrafting.getSize()];

            for (int i = 0; i < aitemstack.length; ++i) {
                ItemStack itemstack = inventorycrafting.getItem(i);

                if (itemstack != null) {
                    if (itemstack.getItem().r()) {
                        aitemstack[i] = new ItemStack(itemstack.getItem().q());
                    } else if (itemstack.hasTag() && TileEntityBanner.c(itemstack) > 0) {
                        aitemstack[i] = itemstack.cloneItemStack();
                        aitemstack[i].count = 1;
                    }
                }
            }

            return aitemstack;
        }

        DuplicateRecipe(RecipesBanner.SyntheticClass_1 recipesbanner_syntheticclass_1) {
            this();
        }
    }
}
