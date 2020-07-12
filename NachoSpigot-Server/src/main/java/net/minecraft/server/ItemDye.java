package net.minecraft.server;

import org.bukkit.event.entity.SheepDyeWoolEvent; // CraftBukkit

public class ItemDye extends Item {

    public static final int[] a = new int[] { 1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

    public ItemDye() {
        this.a(true);
        this.setMaxDurability(0);
        this.a(CreativeModeTab.l);
    }

    public String e_(ItemStack itemstack) {
        int i = itemstack.getData();

        return super.getName() + "." + EnumColor.fromInvColorIndex(i).d();
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
            return false;
        } else {
            EnumColor enumcolor = EnumColor.fromInvColorIndex(itemstack.getData());

            if (enumcolor == EnumColor.WHITE) {
                if (a(itemstack, world, blockposition)) {
                    if (!world.isClientSide) {
                        world.triggerEffect(2005, blockposition, 0);
                    }

                    return true;
                }
            } else if (enumcolor == EnumColor.BROWN) {
                IBlockData iblockdata = world.getType(blockposition);
                Block block = iblockdata.getBlock();

                if (block == Blocks.LOG && iblockdata.get(BlockWood.VARIANT) == BlockWood.EnumLogVariant.JUNGLE) {
                    if (enumdirection == EnumDirection.DOWN) {
                        return false;
                    }

                    if (enumdirection == EnumDirection.UP) {
                        return false;
                    }

                    blockposition = blockposition.shift(enumdirection);
                    if (world.isEmpty(blockposition)) {
                        IBlockData iblockdata1 = Blocks.COCOA.getPlacedState(world, blockposition, enumdirection, f, f1, f2, 0, entityhuman);

                        world.setTypeAndData(blockposition, iblockdata1, 2);
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            --itemstack.count;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public static boolean a(ItemStack itemstack, World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() instanceof IBlockFragilePlantElement) {
            IBlockFragilePlantElement iblockfragileplantelement = (IBlockFragilePlantElement) iblockdata.getBlock();

            if (iblockfragileplantelement.a(world, blockposition, iblockdata, world.isClientSide)) {
                if (!world.isClientSide) {
                    if (iblockfragileplantelement.a(world, world.random, blockposition, iblockdata)) {
                        iblockfragileplantelement.b(world, world.random, blockposition, iblockdata);
                    }

                    --itemstack.count;
                }

                return true;
            }
        }

        return false;
    }

    public boolean a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving) {
        if (entityliving instanceof EntitySheep) {
            EntitySheep entitysheep = (EntitySheep) entityliving;
            EnumColor enumcolor = EnumColor.fromInvColorIndex(itemstack.getData());

            if (!entitysheep.isSheared() && entitysheep.getColor() != enumcolor) {
                // CraftBukkit start
                byte bColor = (byte) enumcolor.getColorIndex();
                SheepDyeWoolEvent event = new SheepDyeWoolEvent((org.bukkit.entity.Sheep) entitysheep.getBukkitEntity(), org.bukkit.DyeColor.getByData(bColor));
                entitysheep.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return false;
                }

                enumcolor = EnumColor.fromColorIndex((byte) event.getColor().getWoolData());
                // CraftBukkit end
                entitysheep.setColor(enumcolor);
                --itemstack.count;
            }

            return true;
        } else {
            return false;
        }
    }
}
