package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.craftbukkit.event.CraftEventFactory;

import java.util.*;

public class CraftingManager {

    private static final CraftingManager a = new CraftingManager();

    public List<IRecipe> recipes = new ObjectArrayList<>();

    // CraftBukkit start
    public IRecipe lastRecipe;
    public org.bukkit.inventory.InventoryView lastCraftView;
    // CraftBukkit end

    public static CraftingManager getInstance() {
        return CraftingManager.a;
    }

    public CraftingManager() {
        (new RecipesTools()).a(this);
        (new RecipesWeapons()).a(this);
        (new RecipeIngots()).a(this);
        (new RecipesFood()).a(this);
        (new RecipesCrafting()).a(this);
        (new RecipesArmor()).a(this);
        (new RecipesDyes()).a(this);
        this.recipes.add(new RecipeArmorDye());
        this.recipes.add(new RecipeBookClone());
        this.recipes.add(new RecipeMapClone());
        this.recipes.add(new RecipeMapExtend());
        this.recipes.add(new RecipeFireworks());
        this.recipes.add(new RecipeRepair());
        (new RecipesBanner()).a(this);
        this.registerShapedRecipe(new ItemStack(Items.PAPER, 3), "###", '#', Items.REEDS);
        this.registerShapelessRecipe(new ItemStack(Items.BOOK, 1), Items.PAPER, Items.PAPER, Items.PAPER, Items.LEATHER);
        this.registerShapelessRecipe(new ItemStack(Items.WRITABLE_BOOK, 1), Items.BOOK, new ItemStack(Items.DYE, 1, EnumColor.BLACK.getInvColorIndex()), Items.FEATHER);
        this.registerShapedRecipe(new ItemStack(Blocks.FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.OAK.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.BIRCH_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.BIRCH.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.SPRUCE_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.SPRUCE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.JUNGLE_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.JUNGLE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.ACACIA_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + BlockWood.EnumLogVariant.ACACIA.a() - 4));
        this.registerShapedRecipe(new ItemStack(Blocks.DARK_OAK_FENCE, 3), "W#W", "W#W", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + BlockWood.EnumLogVariant.DARK_OAK.a() - 4));
        this.registerShapedRecipe(new ItemStack(Blocks.COBBLESTONE_WALL, 6, BlockCobbleWall.EnumCobbleVariant.NORMAL.a()), "###", "###", '#', Blocks.COBBLESTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.COBBLESTONE_WALL, 6, BlockCobbleWall.EnumCobbleVariant.MOSSY.a()), "###", "###", '#', Blocks.MOSSY_COBBLESTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK_FENCE, 6), "###", "###", '#', Blocks.NETHER_BRICK);
        this.registerShapedRecipe(new ItemStack(Blocks.FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.OAK.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.BIRCH_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.BIRCH.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.SPRUCE_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.SPRUCE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.JUNGLE_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.JUNGLE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.ACACIA_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + BlockWood.EnumLogVariant.ACACIA.a() - 4));
        this.registerShapedRecipe(new ItemStack(Blocks.DARK_OAK_FENCE_GATE, 1), "#W#", "#W#", '#', Items.STICK, 'W', new ItemStack(Blocks.PLANKS, 1, 4 + BlockWood.EnumLogVariant.DARK_OAK.a() - 4));
        this.registerShapedRecipe(new ItemStack(Blocks.JUKEBOX, 1), "###", "#X#", "###", '#', Blocks.PLANKS, 'X', Items.DIAMOND);
        this.registerShapedRecipe(new ItemStack(Items.LEAD, 2), "~~ ", "~O ", "  ~", '~', Items.STRING, 'O', Items.SLIME);
        this.registerShapedRecipe(new ItemStack(Blocks.NOTEBLOCK, 1), "###", "#X#", "###", '#', Blocks.PLANKS, 'X', Items.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.BOOKSHELF, 1), "###", "XXX", "###", '#', Blocks.PLANKS, 'X', Items.BOOK);
        this.registerShapedRecipe(new ItemStack(Blocks.SNOW, 1), "##", "##", '#', Items.SNOWBALL);
        this.registerShapedRecipe(new ItemStack(Blocks.SNOW_LAYER, 6), "###", '#', Blocks.SNOW);
        this.registerShapedRecipe(new ItemStack(Blocks.CLAY, 1), "##", "##", '#', Items.CLAY_BALL);
        this.registerShapedRecipe(new ItemStack(Blocks.BRICK_BLOCK, 1), "##", "##", '#', Items.BRICK);
        this.registerShapedRecipe(new ItemStack(Blocks.GLOWSTONE, 1), "##", "##", '#', Items.GLOWSTONE_DUST);
        this.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1), "##", "##", '#', Items.QUARTZ);
        this.registerShapedRecipe(new ItemStack(Blocks.WOOL, 1), "##", "##", '#', Items.STRING);
        this.registerShapedRecipe(new ItemStack(Blocks.TNT, 1), "X#X", "#X#", "X#X", 'X', Items.GUNPOWDER, '#', Blocks.SAND);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockDoubleStepAbstract.EnumStoneSlabVariant.COBBLESTONE.a()), "###", '#', Blocks.COBBLESTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockDoubleStepAbstract.EnumStoneSlabVariant.STONE.a()), "###", '#', new ItemStack(Blocks.STONE, BlockStone.EnumStoneVariant.STONE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockDoubleStepAbstract.EnumStoneSlabVariant.SAND.a()), "###", '#', Blocks.SANDSTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockDoubleStepAbstract.EnumStoneSlabVariant.BRICK.a()), "###", '#', Blocks.BRICK_BLOCK);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockDoubleStepAbstract.EnumStoneSlabVariant.SMOOTHBRICK.a()), "###", '#', Blocks.STONEBRICK);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockDoubleStepAbstract.EnumStoneSlabVariant.NETHERBRICK.a()), "###", '#', Blocks.NETHER_BRICK);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB, 6, BlockDoubleStepAbstract.EnumStoneSlabVariant.QUARTZ.a()), "###", '#', Blocks.QUARTZ_BLOCK);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_SLAB2, 6, BlockDoubleStoneStepAbstract.EnumStoneSlab2Variant.RED_SANDSTONE.a()), "###", '#', Blocks.RED_SANDSTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, 0), "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.OAK.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, BlockWood.EnumLogVariant.BIRCH.a()), "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.BIRCH.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, BlockWood.EnumLogVariant.SPRUCE.a()), "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.SPRUCE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, BlockWood.EnumLogVariant.JUNGLE.a()), "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.JUNGLE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, 4 + BlockWood.EnumLogVariant.ACACIA.a() - 4), "###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + BlockWood.EnumLogVariant.ACACIA.a() - 4));
        this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_SLAB, 6, 4 + BlockWood.EnumLogVariant.DARK_OAK.a() - 4), "###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + BlockWood.EnumLogVariant.DARK_OAK.a() - 4));
        this.registerShapedRecipe(new ItemStack(Blocks.LADDER, 3), "# #", "###", "# #", '#', Items.STICK);
        this.registerShapedRecipe(new ItemStack(Items.WOODEN_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.OAK.a()));
        this.registerShapedRecipe(new ItemStack(Items.SPRUCE_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.SPRUCE.a()));
        this.registerShapedRecipe(new ItemStack(Items.BIRCH_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.BIRCH.a()));
        this.registerShapedRecipe(new ItemStack(Items.JUNGLE_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.JUNGLE.a()));
        this.registerShapedRecipe(new ItemStack(Items.ACACIA_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.ACACIA.a()));
        this.registerShapedRecipe(new ItemStack(Items.DARK_OAK_DOOR, 3), "##", "##", "##", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.DARK_OAK.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.TRAPDOOR, 2), "###", "###", '#', Blocks.PLANKS);
        this.registerShapedRecipe(new ItemStack(Items.IRON_DOOR, 3), "##", "##", "##", '#', Items.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Blocks.IRON_TRAPDOOR, 1), "##", "##", '#', Items.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Items.SIGN, 3), "###", "###", " X ", '#', Blocks.PLANKS, 'X', Items.STICK);
        this.registerShapedRecipe(new ItemStack(Items.CAKE, 1), "AAA", "BEB", "CCC", 'A', Items.MILK_BUCKET, 'B', Items.SUGAR, 'C', Items.WHEAT, 'E', Items.EGG);
        this.registerShapedRecipe(new ItemStack(Items.SUGAR, 1), "#", '#', Items.REEDS);
        this.registerShapedRecipe(new ItemStack(Blocks.PLANKS, 4, BlockWood.EnumLogVariant.OAK.a()), "#", '#', new ItemStack(Blocks.LOG, 1, BlockWood.EnumLogVariant.OAK.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.PLANKS, 4, BlockWood.EnumLogVariant.SPRUCE.a()), "#", '#', new ItemStack(Blocks.LOG, 1, BlockWood.EnumLogVariant.SPRUCE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.PLANKS, 4, BlockWood.EnumLogVariant.BIRCH.a()), "#", '#', new ItemStack(Blocks.LOG, 1, BlockWood.EnumLogVariant.BIRCH.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.PLANKS, 4, BlockWood.EnumLogVariant.JUNGLE.a()), "#", '#', new ItemStack(Blocks.LOG, 1, BlockWood.EnumLogVariant.JUNGLE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.PLANKS, 4, 4 + BlockWood.EnumLogVariant.ACACIA.a() - 4), "#", '#', new ItemStack(Blocks.LOG2, 1, BlockWood.EnumLogVariant.ACACIA.a() - 4));
        this.registerShapedRecipe(new ItemStack(Blocks.PLANKS, 4, 4 + BlockWood.EnumLogVariant.DARK_OAK.a() - 4), "#", '#', new ItemStack(Blocks.LOG2, 1, BlockWood.EnumLogVariant.DARK_OAK.a() - 4));
        this.registerShapedRecipe(new ItemStack(Items.STICK, 4), "#", "#", '#', Blocks.PLANKS);
        this.registerShapedRecipe(new ItemStack(Blocks.TORCH, 4), "X", "#", 'X', Items.COAL, '#', Items.STICK);
        this.registerShapedRecipe(new ItemStack(Blocks.TORCH, 4), "X", "#", 'X', new ItemStack(Items.COAL, 1, 1), '#', Items.STICK);
        this.registerShapedRecipe(new ItemStack(Items.BOWL, 4), "# #", " # ", '#', Blocks.PLANKS);
        this.registerShapedRecipe(new ItemStack(Items.GLASS_BOTTLE, 3), "# #", " # ", '#', Blocks.GLASS);
        this.registerShapedRecipe(new ItemStack(Blocks.RAIL, 16), "X X", "X#X", "X X", 'X', Items.IRON_INGOT, '#', Items.STICK);
        this.registerShapedRecipe(new ItemStack(Blocks.GOLDEN_RAIL, 6), "X X", "X#X", "XRX", 'X', Items.GOLD_INGOT, 'R', Items.REDSTONE, '#', Items.STICK);
        this.registerShapedRecipe(new ItemStack(Blocks.ACTIVATOR_RAIL, 6), "XSX", "X#X", "XSX", 'X', Items.IRON_INGOT, '#', Blocks.REDSTONE_TORCH, 'S', Items.STICK);
        this.registerShapedRecipe(new ItemStack(Blocks.DETECTOR_RAIL, 6), "X X", "X#X", "XRX", 'X', Items.IRON_INGOT, 'R', Items.REDSTONE, '#', Blocks.STONE_PRESSURE_PLATE);
        this.registerShapedRecipe(new ItemStack(Items.MINECART, 1), "# #", "###", '#', Items.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Items.CAULDRON, 1), "# #", "# #", "###", '#', Items.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Items.BREWING_STAND, 1), " B ", "###", '#', Blocks.COBBLESTONE, 'B', Items.BLAZE_ROD);
        this.registerShapedRecipe(new ItemStack(Blocks.LIT_PUMPKIN, 1), "A", "B", 'A', Blocks.PUMPKIN, 'B', Blocks.TORCH);
        this.registerShapedRecipe(new ItemStack(Items.CHEST_MINECART, 1), "A", "B", 'A', Blocks.CHEST, 'B', Items.MINECART);
        this.registerShapedRecipe(new ItemStack(Items.FURNACE_MINECART, 1), "A", "B", 'A', Blocks.FURNACE, 'B', Items.MINECART);
        this.registerShapedRecipe(new ItemStack(Items.TNT_MINECART, 1), "A", "B", 'A', Blocks.TNT, 'B', Items.MINECART);
        this.registerShapedRecipe(new ItemStack(Items.HOPPER_MINECART, 1), "A", "B", 'A', Blocks.HOPPER, 'B', Items.MINECART);
        this.registerShapedRecipe(new ItemStack(Items.BOAT, 1), "# #", "###", '#', Blocks.PLANKS);
        this.registerShapedRecipe(new ItemStack(Items.BUCKET, 1), "# #", " # ", '#', Items.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Items.FLOWER_POT, 1), "# #", " # ", '#', Items.BRICK);
        this.registerShapelessRecipe(new ItemStack(Items.FLINT_AND_STEEL, 1), new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Items.FLINT, 1));
        this.registerShapedRecipe(new ItemStack(Items.BREAD, 1), "###", '#', Items.WHEAT);
        this.registerShapedRecipe(new ItemStack(Blocks.OAK_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.OAK.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.BIRCH_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.BIRCH.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.SPRUCE_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.SPRUCE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.JUNGLE_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, BlockWood.EnumLogVariant.JUNGLE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.ACACIA_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + BlockWood.EnumLogVariant.ACACIA.a() - 4));
        this.registerShapedRecipe(new ItemStack(Blocks.DARK_OAK_STAIRS, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.PLANKS, 1, 4 + BlockWood.EnumLogVariant.DARK_OAK.a() - 4));
        this.registerShapedRecipe(new ItemStack(Items.FISHING_ROD, 1), "  #", " #X", "# X", '#', Items.STICK, 'X', Items.STRING);
        this.registerShapedRecipe(new ItemStack(Items.CARROT_ON_A_STICK, 1), "# ", " X", '#', Items.FISHING_ROD, 'X', Items.CARROT);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.COBBLESTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.BRICK_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.BRICK_BLOCK);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_BRICK_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.STONEBRICK);
        this.registerShapedRecipe(new ItemStack(Blocks.NETHER_BRICK_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.NETHER_BRICK);
        this.registerShapedRecipe(new ItemStack(Blocks.SANDSTONE_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.SANDSTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.RED_SANDSTONE_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.RED_SANDSTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.QUARTZ_STAIRS, 4), "#  ", "## ", "###", '#', Blocks.QUARTZ_BLOCK);
        this.registerShapedRecipe(new ItemStack(Items.PAINTING, 1), "###", "#X#", "###", '#', Items.STICK, 'X', Blocks.WOOL);
        this.registerShapedRecipe(new ItemStack(Items.ITEM_FRAME, 1), "###", "#X#", "###", '#', Items.STICK, 'X', Items.LEATHER);
        this.registerShapedRecipe(new ItemStack(Items.GOLDEN_APPLE, 1, 0), "###", "#X#", "###", '#', Items.GOLD_INGOT, 'X', Items.APPLE);
        this.registerShapedRecipe(new ItemStack(Items.GOLDEN_APPLE, 1, 1), "###", "#X#", "###", '#', Blocks.GOLD_BLOCK, 'X', Items.APPLE);
        this.registerShapedRecipe(new ItemStack(Items.GOLDEN_CARROT, 1, 0), "###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Items.CARROT);
        this.registerShapedRecipe(new ItemStack(Items.SPECKLED_MELON, 1), "###", "#X#", "###", '#', Items.GOLD_NUGGET, 'X', Items.MELON);
        this.registerShapedRecipe(new ItemStack(Blocks.LEVER, 1), "X", "#", '#', Blocks.COBBLESTONE, 'X', Items.STICK);
        this.registerShapedRecipe(new ItemStack(Blocks.TRIPWIRE_HOOK, 2), "I", "S", "#", '#', Blocks.PLANKS, 'S', Items.STICK, 'I', Items.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Blocks.REDSTONE_TORCH, 1), "X", "#", '#', Items.STICK, 'X', Items.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Items.REPEATER, 1), "#X#", "III", '#', Blocks.REDSTONE_TORCH, 'X', Items.REDSTONE, 'I', new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.STONE.a()));
        this.registerShapedRecipe(new ItemStack(Items.COMPARATOR, 1), " # ", "#X#", "III", '#', Blocks.REDSTONE_TORCH, 'X', Items.QUARTZ, 'I', new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.STONE.a()));
        this.registerShapedRecipe(new ItemStack(Items.CLOCK, 1), " # ", "#X#", " # ", '#', Items.GOLD_INGOT, 'X', Items.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Items.COMPASS, 1), " # ", "#X#", " # ", '#', Items.IRON_INGOT, 'X', Items.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Items.MAP, 1), "###", "#X#", "###", '#', Items.PAPER, 'X', Items.COMPASS);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_BUTTON, 1), "#", '#', new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.STONE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_BUTTON, 1), "#", '#', Blocks.PLANKS);
        this.registerShapedRecipe(new ItemStack(Blocks.STONE_PRESSURE_PLATE, 1), "##", '#', new ItemStack(Blocks.STONE, 1, BlockStone.EnumStoneVariant.STONE.a()));
        this.registerShapedRecipe(new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1), "##", '#', Blocks.PLANKS);
        this.registerShapedRecipe(new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 1), "##", '#', Items.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 1), "##", '#', Items.GOLD_INGOT);
        this.registerShapedRecipe(new ItemStack(Blocks.DISPENSER, 1), "###", "#X#", "#R#", '#', Blocks.COBBLESTONE, 'X', Items.BOW, 'R', Items.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.DROPPER, 1), "###", "# #", "#R#", '#', Blocks.COBBLESTONE, 'R', Items.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Blocks.PISTON, 1), "TTT", "#X#", "#R#", '#', Blocks.COBBLESTONE, 'X', Items.IRON_INGOT, 'R', Items.REDSTONE, 'T', Blocks.PLANKS);
        this.registerShapedRecipe(new ItemStack(Blocks.STICKY_PISTON, 1), "S", "P", 'S', Items.SLIME, 'P', Blocks.PISTON);
        this.registerShapedRecipe(new ItemStack(Items.BED, 1), "###", "XXX", '#', Blocks.WOOL, 'X', Blocks.PLANKS);
        this.registerShapedRecipe(new ItemStack(Blocks.ENCHANTING_TABLE, 1), " B ", "D#D", "###", '#', Blocks.OBSIDIAN, 'B', Items.BOOK, 'D', Items.DIAMOND);
        this.registerShapedRecipe(new ItemStack(Blocks.ANVIL, 1), "III", " i ", "iii", 'I', Blocks.IRON_BLOCK, 'i', Items.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Items.LEATHER), "##", "##", '#', Items.RABBIT_HIDE);
        this.registerShapelessRecipe(new ItemStack(Items.ENDER_EYE, 1), Items.ENDER_PEARL, Items.BLAZE_POWDER);
        this.registerShapelessRecipe(new ItemStack(Items.FIRE_CHARGE, 3), Items.GUNPOWDER, Items.BLAZE_POWDER, Items.COAL);
        this.registerShapelessRecipe(new ItemStack(Items.FIRE_CHARGE, 3), Items.GUNPOWDER, Items.BLAZE_POWDER, new ItemStack(Items.COAL, 1, 1));
        this.registerShapedRecipe(new ItemStack(Blocks.DAYLIGHT_DETECTOR), "GGG", "QQQ", "WWW", 'G', Blocks.GLASS, 'Q', Items.QUARTZ, 'W', Blocks.WOODEN_SLAB);
        this.registerShapedRecipe(new ItemStack(Blocks.HOPPER), "I I", "ICI", " I ", 'I', Items.IRON_INGOT, 'C', Blocks.CHEST);
        this.registerShapedRecipe(new ItemStack(Items.ARMOR_STAND, 1), "///", " / ", "/_/", '/', Items.STICK, '_', new ItemStack(Blocks.STONE_SLAB, 1, BlockDoubleStepAbstract.EnumStoneSlabVariant.STONE.a()));
        sort();
    }

    // CraftBukkit start
    public void sort() {
        this.recipes.sort((r1, r2) -> r1 instanceof ShapelessRecipes && r2 instanceof ShapedRecipes ? 1 : (r2 instanceof ShapelessRecipes && r1 instanceof ShapedRecipes ? -1 : (Integer.compare(r2.a(), r1.a()))));
    }

    public ShapedRecipes registerShapedRecipe(ItemStack itemstack, Object... aobject) {
        StringBuilder s = new StringBuilder();
        int i = 0;
        int j = 0;
        int k = 0;

        if (aobject[i] instanceof String[]) {
            String[] astring = (String[]) aobject[i++];

            for (String s1 : astring) {
                ++k;
                j = s1.length();
                s.append(s1);
            }
        } else {
            while (aobject[i] instanceof String) {
                String s2 = (String) aobject[i++];

                ++k;
                j = s2.length();
                s.append(s2);
            }
        }

        HashMap<Character, ItemStack> hashmap = Maps.newHashMap();

        for (; i < aobject.length; i += 2) {
            Character character = (Character) aobject[i];
            ItemStack itemstack1 = null;

            if (aobject[i + 1] instanceof Item) {
                itemstack1 = new ItemStack((Item) aobject[i + 1]);
            } else if (aobject[i + 1] instanceof Block) {
                itemstack1 = new ItemStack((Block) aobject[i + 1], 1, 32767);
            } else if (aobject[i + 1] instanceof ItemStack) {
                itemstack1 = (ItemStack) aobject[i + 1];
            }

            hashmap.put(character, itemstack1);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1) {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(c0)) {
                aitemstack[i1] = hashmap.get(c0).cloneItemStack();
            } else {
                aitemstack[i1] = null;
            }
        }

        ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, itemstack);

        this.recipes.add(shapedrecipes);
        return shapedrecipes;
    }

    public void registerShapelessRecipe(ItemStack itemstack, Object... aobject) {
        ArrayList<ItemStack> arraylist = Lists.newArrayList();
        int i = aobject.length;

        for (Object object : aobject) {
            if (object instanceof ItemStack) {
                arraylist.add(((ItemStack) object).cloneItemStack());
            } else if (object instanceof Item) {
                arraylist.add(new ItemStack((Item) object));
            } else {
                if (!(object instanceof Block)) {
                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                }

                arraylist.add(new ItemStack((Block) object));
            }
        }

        this.recipes.add(new ShapelessRecipes(itemstack, arraylist));
    }

    public void a(IRecipe irecipe) {
        this.recipes.add(irecipe);
    }

    public ItemStack craft(InventoryCrafting inventorycrafting, World world) {

        /*Iterator<IRecipe> iterator = this.recipes.iterator();
        IRecipe irecipe;
        do {
            if (!iterator.hasNext()) {
                inventorycrafting.currentRecipe = null; // CraftBukkit - Clear recipe when no recipe is found
                return null;
            }

            irecipe = iterator.next();
        } while (!irecipe.a(inventorycrafting, world));*/

        // Yatopia start
        IRecipe recipe = null;

        for (IRecipe possible : this.getRecipes()) {
            if (possible.a(inventorycrafting, world)) {
                recipe = possible;
                break;
            }
        }
        // Yatopia end

        // CraftBukkit start - INVENTORY_PRE_CRAFT event
        inventorycrafting.currentRecipe = recipe;

        if (recipe == null) {
            return null; // CraftBukkit - Clear recipe when no recipe is found
        }

        ItemStack result = recipe.craftItem(inventorycrafting);
        return CraftEventFactory.callPreCraftEvent(inventorycrafting, result, lastCraftView, false);

        // CraftBukkit end
    }

    public ItemStack[] b(InventoryCrafting inventorycrafting, World world) {
        for (IRecipe irecipe : this.recipes) {
            if (irecipe.a(inventorycrafting, world)) {
                return irecipe.b(inventorycrafting);
            }
        }

        ItemStack[] aitemstack = new ItemStack[inventorycrafting.getSize()];

        for (int i = 0; i < aitemstack.length; ++i) {
            aitemstack[i] = inventorycrafting.getItem(i);
        }

        return aitemstack;
    }

    public List<IRecipe> getRecipes() {
        return this.recipes;
    }
}
