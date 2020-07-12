package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockRedstoneLamp extends Block {

    private final boolean a;

    public BlockRedstoneLamp(boolean flag) {
        super(Material.BUILDABLE_GLASS);
        this.a = flag;
        if (flag) {
            this.a(1.0F);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            if (this.a && !world.isBlockIndirectlyPowered(blockposition)) {
                world.setTypeAndData(blockposition, Blocks.REDSTONE_LAMP.getBlockData(), 2);
            } else if (!this.a && world.isBlockIndirectlyPowered(blockposition)) {
                // CraftBukkit start
                if (CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 0, 15).getNewCurrent() != 15) {
                    return;
                }
                // CraftBukkit end
                world.setTypeAndData(blockposition, Blocks.LIT_REDSTONE_LAMP.getBlockData(), 2);
            }

        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!world.isClientSide) {
            if (this.a && !world.isBlockIndirectlyPowered(blockposition)) {
                world.a(blockposition, (Block) this, 4);
            } else if (!this.a && world.isBlockIndirectlyPowered(blockposition)) {
                // CraftBukkit start
                if (CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 0, 15).getNewCurrent() != 15) {
                    return;
                }
                // CraftBukkit end
                world.setTypeAndData(blockposition, Blocks.LIT_REDSTONE_LAMP.getBlockData(), 2);
            }

        }
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide) {
            if (this.a && !world.isBlockIndirectlyPowered(blockposition)) {
                // CraftBukkit start
                if (CraftEventFactory.callRedstoneChange(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 15, 0).getNewCurrent() != 0) {
                    return;
                }
                // CraftBukkit end
                world.setTypeAndData(blockposition, Blocks.REDSTONE_LAMP.getBlockData(), 2);
            }

        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.REDSTONE_LAMP);
    }

    protected ItemStack i(IBlockData iblockdata) {
        return new ItemStack(Blocks.REDSTONE_LAMP);
    }
}
