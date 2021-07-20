//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.google.common.collect.Lists;
import dev.cobblesword.nachospigot.Nacho;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class BlockSponge extends Block {
  public static final BlockStateBoolean WET = BlockStateBoolean.of("wet");

  protected BlockSponge() {
    super(Material.SPONGE);
    this.j(this.blockStateList.getBlockData().set(WET, false));
    this.a(CreativeModeTab.b);
  }

  public String getName() {
    return LocaleI18n.get(this.a() + ".dry.name");
  }

  public int getDropData(IBlockData var1) {
    return (Boolean)var1.get(WET) ? 1 : 0;
  }

  public void onPlace(World var1, BlockPosition var2, IBlockData var3) {
    if (!Nacho.get().getConfig().disableSpongeAbsorption) {
      this.e(var1, var2, var3);
    }
  }

  public void doPhysics(World var1, BlockPosition var2, IBlockData var3, Block var4) {
    if (!Nacho.get().getConfig().disableSpongeAbsorption) {
      this.e(var1, var2, var3);
      super.doPhysics(var1, var2, var3, var4);
    }
  }

  protected void e(World var1, BlockPosition var2, IBlockData var3) {
    if (!Nacho.get().getConfig().disableSpongeAbsorption && !(Boolean)var3.get(WET) && this.e(var1, var2)) {
      var1.setTypeAndData(var2, var3.set(WET, true), 2);
      var1.triggerEffect(2001, var2, Block.getId(Blocks.WATER));
    }

  }

  private boolean e(World var1, BlockPosition var2) {
    LinkedList var3 = Lists.newLinkedList();
    ArrayList var4 = Lists.newArrayList();
    var3.add(new Tuple(var2, 0));
    int var5 = 0;

    BlockPosition var7;
    while(!var3.isEmpty()) {
      Tuple var6 = (Tuple)var3.poll();
      var7 = (BlockPosition)var6.a();
      int var8 = (Integer)var6.b();
      EnumDirection[] var9 = EnumDirection.values();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
        EnumDirection var12 = var9[var11];
        BlockPosition var13 = var7.shift(var12);
        if (var1.getType(var13).getBlock().getMaterial() == Material.WATER) {
          var1.setTypeAndData(var13, Blocks.AIR.getBlockData(), 2);
          var4.add(var13);
          ++var5;
          if (var8 < 6) {
            var3.add(new Tuple(var13, var8 + 1));
          }
        }
      }

      if (var5 > 64) {
        break;
      }
    }

    Iterator var14 = var4.iterator();

    while(var14.hasNext()) {
      var7 = (BlockPosition)var14.next();
      var1.applyPhysics(var7, Blocks.AIR);
    }

    return var5 > 0;
  }

  public IBlockData fromLegacyData(int var1) {
    return this.getBlockData().set(WET, (var1 & 1) == 1);
  }

  public int toLegacyData(IBlockData var1) {
    return (Boolean)var1.get(WET) ? 1 : 0;
  }

  protected BlockStateList getStateList() {
    return new BlockStateList(this, new IBlockState[]{WET});
  }
}