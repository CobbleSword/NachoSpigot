package me.suicidalkids.ion.blocks.redstone;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.server.*;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.*;

/*
 * Based on https://gist.github.com/Panda4994/70ed6d39c89396570e062e4404a8d518
 *          https://www.spigotmc.org/resources/pandawire-1-8-8-1-15-2.41991/
 *
 * Mainly based on a decompiled version of PandaWire, with comments and structure
 * from the original copied over. This get rid of any errors the decompiler made.
 *
 * On top of that theres additional optimisations in places.
 */
public class PandaRedstoneWire extends BlockRedstoneWire {
    /*
     * I considered replacing the lists with LinkedHashSets for faster lookup,
     * but an artificial test showed barely any difference in performance
     */
    /** Positions that need to be turned off **/
    private final List<BlockPosition> turnOff = Lists.newArrayList();
    /** Positions that need to be checked to be turned on **/
    private final List<BlockPosition> turnOn = Lists.newArrayList();
    /** Positions of wire that was updated already (Ordering determines update order and is therefore required!) **/
    private final Set<BlockPosition> updatedRedstoneWire = Sets.newLinkedHashSet();

    /** Ordered arrays of the facings; Needed for the update order.
     *  I went with a vertical-first order here, but vertical last would work to.
     *  However it should be avoided to update the vertical axis between the horizontal ones as this would cause unneeded directional behavior. **/
    private static final EnumDirection[] facingsHorizontal = {EnumDirection.WEST, EnumDirection.EAST, EnumDirection.NORTH, EnumDirection.SOUTH};
    private static final EnumDirection[] facingsVertical = {EnumDirection.DOWN, EnumDirection.UP};
    private static final EnumDirection[] facings = ArrayUtils.addAll(facingsVertical, facingsHorizontal);

    /** Offsets for all surrounding blocks that need to receive updates **/
    private static final BaseBlockPosition[] surroundingBlocksOffset;
    static {
        Set<BaseBlockPosition> set = Sets.newLinkedHashSet();
        for (EnumDirection facing : facings) {
            set.add(ReflectUtil.getOfT(facing, BaseBlockPosition.class));
        }

        for (EnumDirection facing1 : facings) {
            BaseBlockPosition v1 = ReflectUtil.getOfT(facing1, BaseBlockPosition.class);

            for (EnumDirection facing2 : facings) {
                BaseBlockPosition v2 = ReflectUtil.getOfT(facing2, BaseBlockPosition.class);
                set.add(new BlockPosition(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ()));
            }
        }

        set.remove(BlockPosition.ZERO);
        surroundingBlocksOffset = set.toArray(new BaseBlockPosition[0]);
    }

    private boolean canProvidePower = true;

    public PandaRedstoneWire() {
        super();
    }

    private void updateSurroundingRedstone(World worldIn, BlockPosition pos, IBlockData iblockdata) {
        // Recalculate the connected wires
        calculateCurrentChanges(worldIn, pos, iblockdata);

        // Set to collect all the updates, to only execute them once. Ordering required.
        Set<BlockPosition> blocksNeedingUpdate = Sets.newLinkedHashSet();

        // Add the needed updates
        for (BlockPosition posi : updatedRedstoneWire) {
            addBlocksNeedingUpdate(worldIn, posi, blocksNeedingUpdate);
        }
        // Add all other updates to keep known behaviors
        // They are added in a backwards order because it preserves a commonly used behavior with the update order
        Iterator<BlockPosition> it = Lists.newLinkedList(updatedRedstoneWire).descendingIterator();
        while (it.hasNext()) {
            addAllSurroundingBlocks(it.next(), blocksNeedingUpdate);
        }
        // Remove updates on the wires as they just were updated
        blocksNeedingUpdate.removeAll(updatedRedstoneWire);
        /* Avoid unnecessary updates on the just updated wires
         * A huge scale test showed about 40% more ticks per second
         * It's probably less in normal usage but likely still worth it
         */
        updatedRedstoneWire.clear();

        // Execute updates
        for (BlockPosition posi : blocksNeedingUpdate) {
            worldIn.d(posi, this);
        }
    }

    /**
     * Turns on or off all connected wires
     *
     * @param worldIn	World
     * @param position	Position of the wire that received the update
     * @param state     Current state of this block
     */
    protected void calculateCurrentChanges(World worldIn, BlockPosition position, IBlockData state) {
        // Turn off all connected wires first if needed
        if (state.getBlock() == this) {
            this.turnOff.add(position);
        } else {
            // In case this wire was removed, check the surrounding wires
            checkSurroundingWires(worldIn, position);
        }

        while (!turnOff.isEmpty()) {
            BlockPosition pos = turnOff.remove(0);
            state = worldIn.getType(pos);
            int oldPower = state.get(POWER);
            this.canProvidePower = false;
            int blockPower = worldIn.A(pos);
            this.canProvidePower = true;
            int wirePower = getSurroundingWirePower(worldIn, pos);
            // Lower the strength as it moved a block
            --wirePower;
            int newPower = Math.max(blockPower, wirePower);

            // Power lowered?
            if (newPower < oldPower) {
                // If it's still powered by a direct source (but weaker) mark for turn on
                if (blockPower > 0 && !turnOn.contains(pos)) {
                    turnOn.add(pos);
                }
                // Set all the way to off for now, because wires that were powered by this need to update first
                setWireState(worldIn, pos, state, 0);
                // Power rose?
            } else if (newPower > oldPower) {
                // Set new Power
                setWireState(worldIn, pos, state, newPower);
            }
            // Check if surrounding wires need to change based on the current/new state and add them to the lists
            checkSurroundingWires(worldIn, pos);
        }

        while (!turnOn.isEmpty()) {
            BlockPosition pos = turnOn.remove(0);
            state = worldIn.getType(pos);
            int oldPower = state.get(POWER);
            this.canProvidePower = false;
            int blockPower = worldIn.A(pos);
            this.canProvidePower = true;
            int wirePower = getSurroundingWirePower(worldIn, pos);
            // Lower the strength as it moved a block
            wirePower--;
            int newPower = Math.max(blockPower, wirePower);

            if (oldPower != newPower) {
                BlockRedstoneEvent event = new BlockRedstoneEvent(
                        worldIn.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()),
                        oldPower,
                        newPower);
                worldIn.getServer().getPluginManager().callEvent(event);
                newPower = event.getNewCurrent();
            }

            if (newPower > oldPower) {
                setWireState(worldIn, pos, state, newPower);
            }
            // Check if surrounding wires need to change based on the current/new state and add them to the lists
            checkSurroundingWires(worldIn, pos);
        }

        turnOff.clear();
    }

    /**
     * Checks if an wire needs to be marked for update depending on the power next to it
     *
     * @author panda
     *
     * @param worldIn       World
     * @param pos		    Position of the wire that might need to change
     * @param otherPower	Power of the wire next to it
     */
    protected void addWireToList(World worldIn, BlockPosition pos, int otherPower) {
        IBlockData state = worldIn.getType(pos);
        if (state.getBlock() == this) {
            int power = state.get(POWER);
            // Could get powered stronger by the neighbor?
            if (power < otherPower - 1 && !turnOn.contains(pos)) {
                // Mark for turn on check.
                turnOn.add(pos);
            }
            // Should have powered the neighbor? Probably was powered by it and is in turn off phase.
            if (power > otherPower && !turnOff.contains(pos)) {
                // Mark for turn off check.
                turnOff.add(pos);
            }
        }
    }

    /**
     * Checks if the wires around need to get updated depending on this wires state.
     * Checks all wires below before the same layer before on top to keep
     * some more rotational symmetry around the y-axis.
     *
     * @author panda
     *
     * @param worldIn		World
     * @param pos			Position of the wire
     */
    protected void checkSurroundingWires(World worldIn, BlockPosition pos) {
        IBlockData state = worldIn.getType(pos);
        int ownPower = 0;
        if (state.getBlock() == Blocks.REDSTONE_WIRE) {
            ownPower = state.get(POWER);
        }
        // Check wires on the same layer first as they appear closer to the wire
        for (EnumDirection facingHorizontal : facingsHorizontal) {
            this.addWireToList(worldIn, pos.shift(facingHorizontal), ownPower);
        }
        for (EnumDirection facingVertical : facingsVertical) {
            BlockPosition offsetPos = pos.shift(facingVertical);
            Block block = worldIn.getType(offsetPos).getBlock();
            boolean solidBlock = block.u();
            for (EnumDirection facingHorizontal : facingsHorizontal) {
                // wire can travel upwards if the block on top doesn't cut the wire (is non-solid)
                // it can travel down if the block below is solid and the block "diagonal" doesn't cut off the wire (is non-solid)
                if (facingVertical == EnumDirection.UP && (!solidBlock || /* This can improve glowstone wiring up to 2.5x */ block == Blocks.GLOWSTONE) || facingVertical == EnumDirection.DOWN && solidBlock && !worldIn.getType(offsetPos.shift(facingHorizontal)).getBlock().isOccluding()) {
                    this.addWireToList(worldIn, offsetPos.shift(facingHorizontal), ownPower);
                }
            }
        }
    }

    /**
     * Gets the maximum power of the surrounding wires
     *
     * @author panda
     *
     * @param worldIn	World
     * @param pos		Position of the asking wire
     * @return			The maximum power of the wires that could power the wire at pos
     */
    private int getSurroundingWirePower(World worldIn, BlockPosition pos) {
        int wirePower = 0;
        for (EnumDirection enumfacing : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            BlockPosition offsetPos = pos.shift(enumfacing);
            IBlockData iblockdata = worldIn.getType(offsetPos);
            boolean occluding = iblockdata.getBlock().isOccluding();
            // Wires on the same layer
            wirePower = this.getPower(iblockdata, wirePower);

            // Block below the wire need to be solid (Upwards diode of slabs/stairs/glowstone) and no block should cut the wire
            if (occluding && !worldIn.getType(pos.up()).getBlock().isOccluding()) {
                wirePower = this.getPower(worldIn, offsetPos.up(), wirePower);
                // Only get from power below if no block is cutting the wire
            } else if (!occluding) {
                wirePower = this.getPower(worldIn, offsetPos.down(), wirePower);
            }
        }
        return wirePower;
    }

    /**
     * Adds all blocks that need to receive an update from a redstone change in this position.
     * This means only blocks that actually could change.
     *
     * @author panda
     *
     * @param worldIn	World
     * @param pos		Position of the wire
     * @param set		Set to add the update positions too
     */
    private void addBlocksNeedingUpdate(World worldIn, BlockPosition pos, Set<BlockPosition> set) {
        Set<EnumDirection> connectedSides = getSidesToPower(worldIn, pos);
        // Add the blocks next to the wire first (closest first order)
        for (EnumDirection facing : facings) {
            BlockPosition offsetPos = pos.shift(facing);
            IBlockData state = worldIn.getType(offsetPos);
            // canConnectTo() is not the nicest solution here as it returns true for e.g. the front of a repeater
            // canBlockBePoweredFromSide catches these cases
            boolean flag = connectedSides.contains(facing.opposite()) || facing == EnumDirection.DOWN;
            if (flag || (facing.k().c() && a(state, facing))) {
                if (canBlockBePoweredFromSide(state, facing, true)) set.add(offsetPos);
            }

            // Later add blocks around the surrounding blocks that get powered
            if (flag && state.getBlock().isOccluding()) {
                for (EnumDirection facing1 : facings) {
                    if (canBlockBePoweredFromSide(worldIn.getType(offsetPos.shift(facing1)), facing1, false)) set.add(offsetPos.shift(facing1));
                }
            }
        }
    }

    /**
     * Checks if a block can get powered from a side.
     * This behavior would better be implemented per block type as follows:
     *  - return false as default. (blocks that are not affected by redstone don't need to be updated, it doesn't really hurt if they are either)
     *  - return true for all blocks that can get powered from all side and change based on it (doors, fence gates, trap doors, note blocks, lamps, dropper, hopper, TNT, rails, possibly more)
     *  - implement own logic for pistons, repeaters, comparators and redstone torches
     *  The current implementation was chosen to keep everything in one class.
     *
     *  Why is this extra check needed?
     *  1. It makes sure that many old behaviors still work (QC + Pistons).
     *  2. It prevents updates from "jumping".
     *     Or rather it prevents this wire to update a block that would get powered by the next one of the same line.
     *     This is to prefer as it makes understanding the update order of the wire really easy. The signal "travels" from the power source.
     *
     * @author panda
     *
     * @param state		State of the block
     * @param side		Side from which it gets powered
     * @param isWire		True if it's powered by a wire directly, False if through a block
     * @return			True if the block can change based on the power level it gets on the given side, false otherwise
     */
    private boolean canBlockBePoweredFromSide(IBlockData state, EnumDirection side, boolean isWire) {
        Block block = state.getBlock();
        if (block == Blocks.AIR) return false;
        if (block instanceof BlockPiston && state.get(BlockPiston.FACING) == side.opposite()) return false;
        if (block instanceof BlockDiodeAbstract && state.get(BlockDiodeAbstract.FACING) != side.opposite())
            return isWire && block instanceof BlockRedstoneComparator && state.get(BlockRedstoneComparator.FACING).k() != side.k() && side.k().c();
        return !(state.getBlock() instanceof BlockRedstoneTorch) || (!isWire && state.get(BlockRedstoneTorch.FACING) == side);
    }

    /**
     * Creates a list of all horizontal sides that can get powered by a wire.
     * The list is ordered the same as the facingsHorizontal.
     *
     * @param worldIn	World
     * @param pos		Position of the wire
     * @return			List of all facings that can get powered by this wire
     */
    private Set<EnumDirection> getSidesToPower(World worldIn, BlockPosition pos) {
        Set<EnumDirection> retval = Sets.newHashSet();
        for (EnumDirection facing : facingsHorizontal) {
            if (this.isPowerSourceAt(worldIn, pos, facing)) {
                retval.add(facing);
            }
        }
        if (retval.isEmpty()) return Sets.newHashSet(facingsHorizontal);
        boolean northsouth = retval.contains(EnumDirection.NORTH) || retval.contains(EnumDirection.SOUTH);
        boolean eastwest = retval.contains(EnumDirection.EAST) || retval.contains(EnumDirection.WEST);
        if (northsouth) {
            retval.remove(EnumDirection.EAST);
            retval.remove(EnumDirection.WEST);
        }
        if (eastwest) {
            retval.remove(EnumDirection.NORTH);
            retval.remove(EnumDirection.SOUTH);
        }
        return retval;
    }

    private boolean canSidePower(World worldIn, BlockPosition pos, EnumDirection side) {
        Set<EnumDirection> retval = Sets.newHashSet();
        for (EnumDirection facing : facingsHorizontal) {
            if (this.isPowerSourceAt(worldIn, pos, facing)) {
                retval.add(facing);
            }
        }
        if (retval.isEmpty()) {
            return side != EnumDirection.DOWN && side != EnumDirection.UP;
        }
        boolean northsouth = retval.contains(EnumDirection.NORTH) || retval.contains(EnumDirection.SOUTH);
        boolean eastwest = retval.contains(EnumDirection.EAST) || retval.contains(EnumDirection.WEST);
        if (northsouth) {
            retval.remove(EnumDirection.EAST);
            retval.remove(EnumDirection.WEST);
        }
        if (eastwest) {
            retval.remove(EnumDirection.NORTH);
            retval.remove(EnumDirection.SOUTH);
        }
        return retval.contains(side);
    }

    /**
     * Adds all surrounding positions to a set.
     * This is the neighbor blocks, as well as their neighbors
     *
     * @param pos
     * @param set
     */
    private void addAllSurroundingBlocks(BlockPosition pos, Set<BlockPosition> set) {
        for (BaseBlockPosition vect : surroundingBlocksOffset) {
            set.add(pos.a(vect));
        }
    }

    /**
     * Sets the block state of a wire with a new power level and marks for updates
     *
     * @author panda
     *
     * @param worldIn	World
     * @param pos		Position at which the state needs to be set
     * @param state		Old state
     * @param power		Power it should get set to
     */
    private void setWireState(World worldIn, BlockPosition pos, IBlockData state, int power) {
        state = state.set(POWER, power);
        worldIn.setTypeAndData(pos, state, 2);
        updatedRedstoneWire.add(pos);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.updateSurroundingRedstone(world, blockposition, world.getType(blockposition));

        for (EnumDirection enumdirection : EnumDirection.values()) {
            world.applyPhysics(blockposition.shift(enumdirection), this);
        }
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        for (EnumDirection enumdirection : EnumDirection.values()) {
            world.applyPhysics(blockposition.shift(enumdirection), this);
        }

        this.updateSurroundingRedstone(world, blockposition, world.getType(blockposition));
    }

    public void doPhysics(World world, BlockPosition blockposition, IBlockData iblockdata, Block block) {
        if (this.canPlace(world, blockposition)) {
            this.updateSurroundingRedstone(world, blockposition, iblockdata);
        } else {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }
    }

    protected final int getPower(IBlockData state, int power) {
        if (state.getBlock() != Blocks.REDSTONE_WIRE) {
            return power;
        }
        int j = state.get(BlockRedstoneWire.POWER);
        return Math.max(j, power);
    }

    public int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection) {
        if (!this.canProvidePower) {
            return 0;
        } else {
            int i = iblockdata.get(BlockRedstoneWire.POWER);
            if (i == 0) { // md_5 change? This isn't in the original.
                return 0;
            } else if (enumdirection == EnumDirection.UP) {
                return i;
            } else {
                return this.canSidePower((World) iblockaccess, blockposition, enumdirection) ? i : 0;
            }
        }
    }

    private boolean isPowerSourceAt(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockpos = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockpos);
        Block block = iblockdata.getBlock();
        boolean flag = block.isOccluding();
        boolean flag1 = iblockaccess.getType(blockposition.up()).getBlock().isOccluding();
        return !flag1 && flag && e(iblockaccess, blockpos.up()) || (a(iblockdata, enumdirection) || (block == Blocks.POWERED_REPEATER && iblockdata.get(BlockDiodeAbstract.FACING) == enumdirection || !flag && e(iblockaccess, blockpos.down())));
    }

}