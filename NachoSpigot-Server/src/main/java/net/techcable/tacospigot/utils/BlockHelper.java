package net.techcable.tacospigot.utils;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;

import net.minecraft.server.Block;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.World;

public class BlockHelper {
    private BlockHelper() {}

    public static void forEachAdjacentBlock(World w, BlockPosition position, int radius, BiConsumer<World, BlockPosition> action) {
        isAllAdjacentBlocksFillPredicate(w, position, radius, (world, adjacentPos) -> {
            action.accept(world, adjacentPos);
            return true;
        });
    }

    public static boolean isAllAdjacentBlocksLoaded(World world, BlockPosition pos, int radius) {
        return isAllAdjacentBlocksFillPredicate(world, pos, radius, World::isLoaded);
    }

    public static boolean isAllAdjacentBlocksFillPredicate(World world, BlockPosition pos, int radius, BiPredicate<World, BlockPosition> predicate) {
        // Make sure to keep this below the inline threshold!!!
        int startX = pos.getX() - radius;
        int endX = pos.getX() + radius;
        int startY = Math.max(0, pos.getY() - radius);
        int endY = Math.min(255, pos.getY() + radius);
        int startZ = pos.getZ() - radius;
        int endZ = pos.getZ() + radius;
        BlockPosition.MutableBlockPosition adjacent = new BlockPosition.MutableBlockPosition();
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    adjacent.setValues(x, y, z);
                    if (!predicate.test(world, adjacent)) return false;
                }
            }
        }
        return true;
    }

    private static final Block[] blockById = new Block[256];

    static {
        for (int i = 0; i < blockById.length; i++) {
            blockById[i] = Block.getById(i);
        }
    }

    public static Block getBlock(int id) {
        return id < 256 ? blockById[id] : Block.getById(id);
    }
}
