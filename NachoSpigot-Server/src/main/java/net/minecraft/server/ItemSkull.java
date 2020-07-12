package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class ItemSkull extends Item {

    private static final String[] a = new String[] { "skeleton", "wither", "zombie", "char", "creeper"};

    public ItemSkull() {
        this.a(CreativeModeTab.c);
        this.setMaxDurability(0);
        this.a(true);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        if (enumdirection == EnumDirection.DOWN) {
            return false;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();
            boolean flag = block.a(world, blockposition);

            if (!flag) {
                if (!world.getType(blockposition).getBlock().getMaterial().isBuildable()) {
                    return false;
                }

                blockposition = blockposition.shift(enumdirection);
            }

            if (!entityhuman.a(blockposition, enumdirection, itemstack)) {
                return false;
            } else if (!Blocks.SKULL.canPlace(world, blockposition)) {
                return false;
            } else {
                if (!world.isClientSide) {
                    // Spigot Start
                    if ( !Blocks.SKULL.canPlace( world, blockposition ) )
                    {
                        return false;
                    }
                    // Spigot End
                    world.setTypeAndData(blockposition, Blocks.SKULL.getBlockData().set(BlockSkull.FACING, enumdirection), 3);
                    int i = 0;

                    if (enumdirection == EnumDirection.UP) {
                        i = MathHelper.floor((double) (entityhuman.yaw * 16.0F / 360.0F) + 0.5D) & 15;
                    }

                    TileEntity tileentity = world.getTileEntity(blockposition);

                    if (tileentity instanceof TileEntitySkull) {
                        TileEntitySkull tileentityskull = (TileEntitySkull) tileentity;

                        if (itemstack.getData() == 3) {
                            GameProfile gameprofile = null;

                            if (itemstack.hasTag()) {
                                NBTTagCompound nbttagcompound = itemstack.getTag();

                                if (nbttagcompound.hasKeyOfType("SkullOwner", 10)) {
                                    gameprofile = GameProfileSerializer.deserialize(nbttagcompound.getCompound("SkullOwner"));
                                } else if (nbttagcompound.hasKeyOfType("SkullOwner", 8) && nbttagcompound.getString("SkullOwner").length() > 0) {
                                    gameprofile = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));
                                }
                            }

                            tileentityskull.setGameProfile(gameprofile);
                        } else {
                            tileentityskull.setSkullType(itemstack.getData());
                        }

                        tileentityskull.setRotation(i);
                        Blocks.SKULL.a(world, blockposition, tileentityskull);
                    }

                    --itemstack.count;
                }

                return true;
            }
        }
    }

    public int filterData(int i) {
        return i;
    }

    public String e_(ItemStack itemstack) {
        int i = itemstack.getData();

        if (i < 0 || i >= ItemSkull.a.length) {
            i = 0;
        }

        return super.getName() + "." + ItemSkull.a[i];
    }

    public String a(ItemStack itemstack) {
        if (itemstack.getData() == 3 && itemstack.hasTag()) {
            if (itemstack.getTag().hasKeyOfType("SkullOwner", 8)) {
                return LocaleI18n.a("item.skull.player.name", new Object[] { itemstack.getTag().getString("SkullOwner")});
            }

            if (itemstack.getTag().hasKeyOfType("SkullOwner", 10)) {
                NBTTagCompound nbttagcompound = itemstack.getTag().getCompound("SkullOwner");

                if (nbttagcompound.hasKeyOfType("Name", 8)) {
                    return LocaleI18n.a("item.skull.player.name", new Object[] { nbttagcompound.getString("Name")});
                }
            }
        }

        return super.a(itemstack);
    }

    public boolean a(final NBTTagCompound nbttagcompound) { // Spigot - make final
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("SkullOwner", 8) && nbttagcompound.getString("SkullOwner").length() > 0) {
            GameProfile gameprofile = new GameProfile((UUID) null, nbttagcompound.getString("SkullOwner"));

            // Spigot start
            TileEntitySkull.b(gameprofile, new com.google.common.base.Predicate<GameProfile>() {

                @Override
                public boolean apply(GameProfile gameprofile) {
                    nbttagcompound.set("SkullOwner", GameProfileSerializer.serialize(new NBTTagCompound(), gameprofile));                    
                    return false;
                }
            });
            // Spigot end
            return true;
        } else {
            return false;
        }
    }
}
