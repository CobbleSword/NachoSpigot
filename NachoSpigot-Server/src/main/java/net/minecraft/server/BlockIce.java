package net.minecraft.server;

import java.util.Random;

public class BlockIce extends BlockHalfTransparent {

    public BlockIce() {
        super(Material.ICE, false);
        this.frictionFactor = 0.98F;
        this.a(true);
        this.a(CreativeModeTab.b);
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, TileEntity tileentity) {
        entityhuman.b(StatisticList.MINE_BLOCK_COUNT[Block.getId(this)]);
        entityhuman.applyExhaustion(0.025F);
        if (this.I() && EnchantmentManager.hasSilkTouchEnchantment(entityhuman)) {
            ItemStack itemstack = this.i(iblockdata);

            if (itemstack != null) {
                a(world, blockposition, itemstack);
            }
        } else {
            if (world.worldProvider.n()) {
                world.setAir(blockposition);
                return;
            }

            int i = EnchantmentManager.getBonusBlockLootEnchantmentLevel(entityhuman);

            this.b(world, blockposition, iblockdata, i);
            Material material = world.getType(blockposition.down()).getBlock().getMaterial();

            if (material.isSolid() || material.isLiquid()) {
                world.setTypeUpdate(blockposition, Blocks.FLOWING_WATER.getBlockData());
            }
        }

    }

    public int a(Random random) {
        return 0;
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (world.b(EnumSkyBlock.BLOCK, blockposition) > 11 - this.p()) {
            // CraftBukkit start
            if (org.bukkit.craftbukkit.event.CraftEventFactory.callBlockFadeEvent(world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), world.worldProvider.n() ? Blocks.AIR : Blocks.WATER).isCancelled()) {
                return;
            }
            // CraftBukkit end
            
            if (world.worldProvider.n()) {
                world.setAir(blockposition);
            } else {
                this.b(world, blockposition, world.getType(blockposition), 0);
                world.setTypeUpdate(blockposition, Blocks.WATER.getBlockData());
            }
        }
    }

    public int k() {
        return 0;
    }
}
