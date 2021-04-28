package net.minecraft.server;

public class ItemBlock extends Item {

    protected final Block a;

    public ItemBlock(Block block) {
        this.a = block;
    }

    public ItemBlock b(String s) {
        super.c(s);
        return this;
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (!block.a(world, blockposition)) {
            blockposition = blockposition.shift(enumdirection);
        }

        if (itemstack.count == 0) {
            return false;
        } else if (!entityhuman.a(blockposition, enumdirection, itemstack)) {
            return false;
        } else if (world.a(this.a, blockposition, false, enumdirection, entityhuman, itemstack)) { // PaperSpigot - Pass entityhuman instead of null
            int i = this.filterData(itemstack.getData());
            IBlockData iblockdata1 = this.a.getPlacedState(world, blockposition, enumdirection, f, f1, f2, i, entityhuman);

            if (world.setTypeAndData(blockposition, iblockdata1, 3)) {
                iblockdata1 = world.getType(blockposition);
                if (iblockdata1.getBlock() == this.a) {
                    a(world, entityhuman, blockposition, itemstack);
                    this.a.postPlace(world, blockposition, iblockdata1, entityhuman, itemstack);
                }

                world.makeSound((double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 0.5F), (double) ((float) blockposition.getZ() + 0.5F), this.a.stepSound.getPlaceSound(), (this.a.stepSound.getVolume1() + 1.0F) / 2.0F, this.a.stepSound.getVolume2() * 0.8F);
                --itemstack.count;
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean a(World world, EntityHuman entityhuman, BlockPosition blockposition, ItemStack itemstack) {
        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (minecraftserver == null) {
            return false;
        } else {
            if (itemstack.hasTag() && itemstack.getTag().hasKeyOfType("BlockEntityTag", 10)) {
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity != null) {
                    if (!world.isClientSide && tileentity.F() && !minecraftserver.getPlayerList().isOp(entityhuman.getProfile())) {
                        return false;
                    }

                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttagcompound.clone();

                    tileentity.b(nbttagcompound);
                    NBTTagCompound nbttagcompound2 = (NBTTagCompound) itemstack.getTag().get("BlockEntityTag");

                    nbttagcompound.a(nbttagcompound2);
                    nbttagcompound.setInt("x", blockposition.getX());
                    nbttagcompound.setInt("y", blockposition.getY());
                    nbttagcompound.setInt("z", blockposition.getZ());
                    if (!nbttagcompound.equals(nbttagcompound1)) {
                        tileentity.a(nbttagcompound);
                        tileentity.update();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public String e_(ItemStack itemstack) {
        return this.a.a();
    }

    public String getName() {
        return this.a.a();
    }

    public Block d() {
        return this.a;
    }

    public Item c(String s) {
        return this.b(s);
    }
}
