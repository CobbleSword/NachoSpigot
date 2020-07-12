package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
// CraftBukkit end

public class ItemMinecart extends Item {

    private static final IDispenseBehavior a = new DispenseBehaviorItem() {
        private final DispenseBehaviorItem b = new DispenseBehaviorItem();

        public ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
            EnumDirection enumdirection = BlockDispenser.b(isourceblock.f());
            World world = isourceblock.getWorld();
            double d0 = isourceblock.getX() + (double) enumdirection.getAdjacentX() * 1.125D;
            double d1 = Math.floor(isourceblock.getY()) + (double) enumdirection.getAdjacentY();
            double d2 = isourceblock.getZ() + (double) enumdirection.getAdjacentZ() * 1.125D;
            BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
            IBlockData iblockdata = world.getType(blockposition);
            BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = iblockdata.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(((BlockMinecartTrackAbstract) iblockdata.getBlock()).n()) : BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
            double d3;

            if (BlockMinecartTrackAbstract.d(iblockdata)) {
                if (blockminecarttrackabstract_enumtrackposition.c()) {
                    d3 = 0.6D;
                } else {
                    d3 = 0.1D;
                }
            } else {
                if (iblockdata.getBlock().getMaterial() != Material.AIR || !BlockMinecartTrackAbstract.d(world.getType(blockposition.down()))) {
                    return this.b.a(isourceblock, itemstack);
                }

                IBlockData iblockdata1 = world.getType(blockposition.down());
                BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition1 = iblockdata1.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata1.get(((BlockMinecartTrackAbstract) iblockdata1.getBlock()).n()) : BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;

                if (enumdirection != EnumDirection.DOWN && blockminecarttrackabstract_enumtrackposition1.c()) {
                    d3 = -0.4D;
                } else {
                    d3 = -0.9D;
                }
            }

            // CraftBukkit start
            // EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.a(world, d0, d1 + d3, d2, ((ItemMinecart) itemstack.getItem()).b);
            ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
            org.bukkit.block.Block block2 = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
            CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

            BlockDispenseEvent event = new BlockDispenseEvent(block2, craftItem.clone(), new org.bukkit.util.Vector(d0, d1 + d3, d2));
            if (!BlockDispenser.eventFired) {
                world.getServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled()) {
                itemstack.count++;
                return itemstack;
            }

            if (!event.getItem().equals(craftItem)) {
                itemstack.count++;
                // Chain to handler for new item
                ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                    idispensebehavior.a(isourceblock, eventStack);
                    return itemstack;
                }
            }

            itemstack1 = CraftItemStack.asNMSCopy(event.getItem());
            EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.a(world, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), ((ItemMinecart) itemstack1.getItem()).b);

            if (itemstack.hasName()) {
                entityminecartabstract.setCustomName(itemstack.getName());
            }

            world.addEntity(entityminecartabstract);
            // itemstack.a(1); // CraftBukkit - handled during event processing
            // CraftBukkit end
            return itemstack;
        }

        protected void a(ISourceBlock isourceblock) {
            isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
        }
    };
    private final EntityMinecartAbstract.EnumMinecartType b;

    public ItemMinecart(EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype) {
        this.maxStackSize = 1;
        this.b = entityminecartabstract_enumminecarttype;
        this.a(CreativeModeTab.e);
        BlockDispenser.REGISTRY.a(this, ItemMinecart.a);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);

        if (BlockMinecartTrackAbstract.d(iblockdata)) {
            if (!world.isClientSide) {
                BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = iblockdata.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(((BlockMinecartTrackAbstract) iblockdata.getBlock()).n()) : BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
                double d0 = 0.0D;

                if (blockminecarttrackabstract_enumtrackposition.c()) {
                    d0 = 0.5D;
                }

                // CraftBukkit start - Minecarts
                org.bukkit.event.player.PlayerInteractEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent(entityhuman, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK, blockposition, enumdirection, itemstack);

                if (event.isCancelled()) {
                    return false;
                }
                // CraftBukkit end

                EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.a(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.0625D + d0, (double) blockposition.getZ() + 0.5D, this.b);

                if (itemstack.hasName()) {
                    entityminecartabstract.setCustomName(itemstack.getName());
                }

                world.addEntity(entityminecartabstract);
            }

            --itemstack.count;
            return true;
        } else {
            return false;
        }
    }
}
