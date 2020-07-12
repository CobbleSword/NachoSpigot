package net.minecraft.server;

import java.util.Random;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockCommand extends BlockContainer {

    public static final BlockStateBoolean TRIGGERED = BlockStateBoolean.of("triggered");

    public BlockCommand() {
        super(Material.ORE, MaterialMapColor.q);
        this.j(this.blockStateList.getBlockData().set(BlockCommand.TRIGGERED, Boolean.valueOf(false)));
    }

    public TileEntity a(World world, int i) {
        return new TileEntityCommand();
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (!world.isClientSide) {
            boolean flag = world.isBlockIndirectlyPowered(blockposition);
            boolean flag1 = ((Boolean) iblockdata.get(BlockCommand.TRIGGERED)).booleanValue();

            // CraftBukkit start
            org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            int old = flag1 ? 15 : 0;
            int current = flag ? 15 : 0;

            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, old, current);
            world.getServer().getPluginManager().callEvent(eventRedstone);
            // CraftBukkit end

            if (eventRedstone.getNewCurrent() > 0 && !(eventRedstone.getOldCurrent() > 0)) { // CraftBukkit
                world.setTypeAndData(blockposition, iblockdata.set(BlockCommand.TRIGGERED, Boolean.valueOf(true)), 4);
                world.a(blockposition, (Block) this, this.a(world));
            } else if (!(eventRedstone.getNewCurrent() > 0) && eventRedstone.getOldCurrent() > 0) { // CraftBukkit
                world.setTypeAndData(blockposition, iblockdata.set(BlockCommand.TRIGGERED, Boolean.valueOf(false)), 4);
            }
        }

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityCommand) {
            ((TileEntityCommand) tileentity).getCommandBlock().a(world);
            world.updateAdjacentComparators(blockposition, this);
        }

    }

    public int a(World world) {
        return 1;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumDirection enumdirection, float f, float f1, float f2) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityCommand ? ((TileEntityCommand) tileentity).getCommandBlock().a(entityhuman) : false;
    }

    public boolean isComplexRedstone() {
        return true;
    }

    public int l(World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityCommand ? ((TileEntityCommand) tileentity).getCommandBlock().j() : 0;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityCommand) {
            CommandBlockListenerAbstract commandblocklistenerabstract = ((TileEntityCommand) tileentity).getCommandBlock();

            if (itemstack.hasName()) {
                commandblocklistenerabstract.setName(itemstack.getName());
            }

            if (!world.isClientSide) {
                commandblocklistenerabstract.a(world.getGameRules().getBoolean("sendCommandFeedback"));
            }

        }
    }

    public int a(Random random) {
        return 0;
    }

    public int b() {
        return 3;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCommand.TRIGGERED, Boolean.valueOf((i & 1) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        int i = 0;

        if (((Boolean) iblockdata.get(BlockCommand.TRIGGERED)).booleanValue()) {
            i |= 1;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockCommand.TRIGGERED});
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockCommand.TRIGGERED, Boolean.valueOf(false));
    }
}
