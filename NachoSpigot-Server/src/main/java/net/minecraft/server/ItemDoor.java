package net.minecraft.server;

public class ItemDoor extends Item {

    private Block a;

    public ItemDoor(Block block) {
        this.a = block;
        this.a(CreativeModeTab.d);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        if (enumdirection != EnumDirection.UP) {
            return false;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (!block.a(world, blockposition)) {
                blockposition = blockposition.shift(enumdirection);
            }

            if (!entityhuman.a(blockposition, enumdirection, itemstack)) {
                return false;
            } else if (!this.a.canPlace(world, blockposition)) {
                return false;
            } else {
                a(world, blockposition, EnumDirection.fromAngle((double) entityhuman.yaw), this.a);
                --itemstack.count;
                return true;
            }
        }
    }

    public static void a(World world, BlockPosition blockposition, EnumDirection enumdirection, Block block) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection.e());
        BlockPosition blockposition2 = blockposition.shift(enumdirection.f());
        int i = (world.getType(blockposition2).getBlock().isOccluding() ? 1 : 0) + (world.getType(blockposition2.up()).getBlock().isOccluding() ? 1 : 0);
        int j = (world.getType(blockposition1).getBlock().isOccluding() ? 1 : 0) + (world.getType(blockposition1.up()).getBlock().isOccluding() ? 1 : 0);
        boolean flag = world.getType(blockposition2).getBlock() == block || world.getType(blockposition2.up()).getBlock() == block;
        boolean flag1 = world.getType(blockposition1).getBlock() == block || world.getType(blockposition1.up()).getBlock() == block;
        boolean flag2 = false;

        if (flag && !flag1 || j > i) {
            flag2 = true;
        }

        BlockPosition blockposition3 = blockposition.up();
        IBlockData iblockdata = block.getBlockData().set(BlockDoor.FACING, enumdirection).set(BlockDoor.HINGE, flag2 ? BlockDoor.EnumDoorHinge.RIGHT : BlockDoor.EnumDoorHinge.LEFT);

        // Spigot start - update physics after the block multi place event
        world.setTypeAndData(blockposition, iblockdata.set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 3);
        world.setTypeAndData(blockposition3, iblockdata.set(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 3);
        // world.applyPhysics(blockposition, block);
        // world.applyPhysics(blockposition3, block);
        // Spigot end
    }
}
