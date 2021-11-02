package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class BlockSponge extends Block {

    public static final BlockStateBoolean WET = BlockStateBoolean.of("wet");

    protected BlockSponge() {
        super(Material.SPONGE);
        this.j(this.blockStateList.getBlockData().set(BlockSponge.WET, false));
        this.a(CreativeModeTab.b);
    }

    public String getName() {
        return LocaleI18n.get(this.a() + ".dry.name");
    }

    public int getDropData(IBlockData iblockdata) {
        return iblockdata.get(BlockSponge.WET) ? 1 : 0;
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.nachoSpigotConfig.disableSpongeAbsorption) {
          this.e(world, blockposition, iblockdata);
        }
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!world.nachoSpigotConfig.disableSpongeAbsorption) {
          this.e(world, blockposition, iblockdata);
          super.doPhysics(world, blockposition, iblockdata, block);
        }
        
    }

    protected void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.nachoSpigotConfig.disableSpongeAbsorption && !(Boolean) iblockdata.get(BlockSponge.WET) && this.e(world, blockposition)) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockSponge.WET, true), 2);
            world.triggerEffect(2001, blockposition, Block.getId(Blocks.WATER));
        }

    }

    private boolean e(World world, BlockPosition blockposition) {
        LinkedList linkedlist = Lists.newLinkedList();
        ArrayList arraylist = Lists.newArrayList();

        linkedlist.add(new Tuple(blockposition, Integer.valueOf(0)));
        int i = 0;

        BlockPosition blockposition1;

        while (!linkedlist.isEmpty()) {
            Tuple tuple = (Tuple) linkedlist.poll();

            blockposition1 = (BlockPosition) tuple.a();
            int j = ((Integer) tuple.b()).intValue();
            EnumDirection[] aenumdirection = EnumDirection.values();
            int k = aenumdirection.length;

            for (int l = 0; l < k; ++l) {
                EnumDirection enumdirection = aenumdirection[l];
                BlockPosition blockposition2 = blockposition1.shift(enumdirection);

                if (world.getType(blockposition2).getBlock().getMaterial() == Material.WATER) {
                    world.setTypeAndData(blockposition2, Blocks.AIR.getBlockData(), 2);
                    arraylist.add(blockposition2);
                    ++i;
                    if (j < 6) {
                        linkedlist.add(new Tuple(blockposition2, Integer.valueOf(j + 1)));
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            blockposition1 = (BlockPosition) iterator.next();
            world.applyPhysics(blockposition1, Blocks.AIR);
        }

        return i > 0;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockSponge.WET, Boolean.valueOf((i & 1) == 1));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Boolean) iblockdata.get(BlockSponge.WET)).booleanValue() ? 1 : 0;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockSponge.WET);
    }
}
