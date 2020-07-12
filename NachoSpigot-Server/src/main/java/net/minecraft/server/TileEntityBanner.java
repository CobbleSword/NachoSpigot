package net.minecraft.server;

import java.util.List;

public class TileEntityBanner extends TileEntity {

    public int color;
    public NBTTagList patterns;
    private boolean g;
    private List<TileEntityBanner.EnumBannerPatternType> h;
    private List<EnumColor> i;
    private String j;

    public TileEntityBanner() {}

    public void a(ItemStack itemstack) {
        this.patterns = null;
        if (itemstack.hasTag() && itemstack.getTag().hasKeyOfType("BlockEntityTag", 10)) {
            NBTTagCompound nbttagcompound = itemstack.getTag().getCompound("BlockEntityTag");

            if (nbttagcompound.hasKey("Patterns")) {
                this.patterns = (NBTTagList) nbttagcompound.getList("Patterns", 10).clone();
                // CraftBukkit start
                while (this.patterns.size() > 20) {
                    this.patterns.a(20); // PAIL Rename remove
                }
                // CraftBukkit end
            }

            if (nbttagcompound.hasKeyOfType("Base", 99)) {
                this.color = nbttagcompound.getInt("Base");
            } else {
                this.color = itemstack.getData() & 15;
            }
        } else {
            this.color = itemstack.getData() & 15;
        }

        this.h = null;
        this.i = null;
        this.j = "";
        this.g = true;
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        a(nbttagcompound, this.color, this.patterns);
    }

    public static void a(NBTTagCompound nbttagcompound, int i, NBTTagList nbttaglist) {
        nbttagcompound.setInt("Base", i);
        if (nbttaglist != null) {
            nbttagcompound.set("Patterns", nbttaglist);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.color = nbttagcompound.getInt("Base");
        this.patterns = nbttagcompound.getList("Patterns", 10);
        // CraftBukkit start
        while (this.patterns.size() > 20) {
            this.patterns.a(20); // PAIL Rename remove
        }
        // CraftBukkit end
        this.h = null;
        this.i = null;
        this.j = null;
        this.g = true;
    }

    public Packet getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.b(nbttagcompound);
        return new PacketPlayOutTileEntityData(this.position, 6, nbttagcompound);
    }

    public int b() {
        return this.color;
    }

    public static int b(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag", false);

        return nbttagcompound != null && nbttagcompound.hasKey("Base") ? nbttagcompound.getInt("Base") : itemstack.getData();
    }

    public static int c(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag", false);

        return nbttagcompound != null && nbttagcompound.hasKey("Patterns") ? nbttagcompound.getList("Patterns", 10).size() : 0;
    }

    public NBTTagList d() {
        return this.patterns;
    }

    public static void e(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag", false);

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Patterns", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);

            if (nbttaglist.size() > 0) {
                nbttaglist.a(nbttaglist.size() - 1);
                if (nbttaglist.isEmpty()) {
                    itemstack.getTag().remove("BlockEntityTag");
                    if (itemstack.getTag().isEmpty()) {
                        itemstack.setTag((NBTTagCompound) null);
                    }
                }

            }
        }
    }

    public static enum EnumBannerPatternType {

        BASE("base", "b"), SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "), SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"), SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "), SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "), STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"), STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "), STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "), STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"), STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "), STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "), STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"), STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "), STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "), CROSS("cross", "cr", "# #", " # ", "# #"), STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "), TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"), TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "), TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "), TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "), DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "), DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"), DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "), DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "), CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "), RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "), HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "), HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "), HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"), HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"), BORDER("border", "bo", "###", "# #", "###"), CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)), CREEPER("creeper", "cre", new ItemStack(Items.SKULL, 1, 4)), GRADIENT("gradient", "gra", "# #", " # ", " # "), GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"), BRICKS("bricks", "bri", new ItemStack(Blocks.BRICK_BLOCK)), SKULL("skull", "sku", new ItemStack(Items.SKULL, 1, 1)), FLOWER("flower", "flo", new ItemStack(Blocks.RED_FLOWER, 1, BlockFlowers.EnumFlowerVarient.OXEYE_DAISY.b())), MOJANG("mojang", "moj", new ItemStack(Items.GOLDEN_APPLE, 1, 1));

        private String N;
        private String O;
        private String[] P;
        private ItemStack Q;

        private EnumBannerPatternType(String s, String s1) {
            this.P = new String[3];
            this.N = s;
            this.O = s1;
        }

        private EnumBannerPatternType(String s, String s1, ItemStack itemstack) {
            this(s, s1);
            this.Q = itemstack;
        }

        private EnumBannerPatternType(String s, String s1, String s2, String s3, String s4) {
            this(s, s1);
            this.P[0] = s2;
            this.P[1] = s3;
            this.P[2] = s4;
        }

        public String b() {
            return this.O;
        }

        public String[] c() {
            return this.P;
        }

        public boolean d() {
            return this.Q != null || this.P[0] != null;
        }

        public boolean e() {
            return this.Q != null;
        }

        public ItemStack f() {
            return this.Q;
        }
    }
}
