package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import org.bukkit.event.entity.EntityInteractEvent; // CraftBukkit

public class BlockPressurePlateBinary extends BlockPressurePlateAbstract {

    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    private final BlockPressurePlateBinary.EnumMobType b;

    protected BlockPressurePlateBinary(Material material, BlockPressurePlateBinary.EnumMobType blockpressureplatebinary_enummobtype) {
        super(material);
        this.j(this.blockStateList.getBlockData().set(BlockPressurePlateBinary.POWERED, Boolean.valueOf(false)));
        this.b = blockpressureplatebinary_enummobtype;
    }

    protected int e(IBlockData iblockdata) {
        return ((Boolean) iblockdata.get(BlockPressurePlateBinary.POWERED)).booleanValue() ? 15 : 0;
    }

    protected IBlockData a(IBlockData iblockdata, int i) {
        return iblockdata.set(BlockPressurePlateBinary.POWERED, Boolean.valueOf(i > 0));
    }

    protected int f(World world, BlockPosition blockposition) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox(blockposition);
        List list;

        switch (BlockPressurePlateBinary.SyntheticClass_1.a[this.b.ordinal()]) {
        case 1:
            list = world.getEntities((Entity) null, axisalignedbb);
            break;

        case 2:
            list = world.a(EntityLiving.class, axisalignedbb);
            break;

        default:
            return 0;
        }

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                // CraftBukkit start - Call interact event when turning on a pressure plate
                if (this.e(world.getType(blockposition)) == 0) {
                    org.bukkit.World bworld = world.getWorld();
                    org.bukkit.plugin.PluginManager manager = world.getServer().getPluginManager();
                    org.bukkit.event.Cancellable cancellable;

                    if (entity instanceof EntityHuman) {
                        cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent((EntityHuman) entity, org.bukkit.event.block.Action.PHYSICAL, blockposition, null, null);
                    } else {
                        cancellable = new EntityInteractEvent(entity.getBukkitEntity(), bworld.getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                        manager.callEvent((EntityInteractEvent) cancellable);
                    }

                    // We only want to block turning the plate on if all events are cancelled
                    if (cancellable.isCancelled()) {
                        continue;
                    }
                }
                // CraftBukkit end

                if (!entity.aI()) {
                    return 15;
                }
            }
        }

        return 0;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockPressurePlateBinary.POWERED, Boolean.valueOf(i == 1));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Boolean) iblockdata.get(BlockPressurePlateBinary.POWERED)).booleanValue() ? 1 : 0;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockPressurePlateBinary.POWERED});
    }

    static class SyntheticClass_1 {

        static final int[] a = new int[BlockPressurePlateBinary.EnumMobType.values().length];

        static {
            try {
                BlockPressurePlateBinary.SyntheticClass_1.a[BlockPressurePlateBinary.EnumMobType.EVERYTHING.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                BlockPressurePlateBinary.SyntheticClass_1.a[BlockPressurePlateBinary.EnumMobType.MOBS.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

        }
    }

    public static enum EnumMobType {

        EVERYTHING, MOBS;

        private EnumMobType() {}
    }
}
