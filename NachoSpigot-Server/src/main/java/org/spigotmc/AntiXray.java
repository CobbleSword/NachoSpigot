package org.spigotmc;

import gnu.trove.set.TByteSet;
import gnu.trove.set.hash.TByteHashSet;
import net.minecraft.server.Block;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Blocks;
import net.minecraft.server.ChunkSection;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import co.aikar.timings.SpigotTimings;

// PaperSpigot start
import java.util.HashSet;
import java.util.Set;
// PaperSpigot end
// TacoSpigot start
import net.minecraft.server.Chunk;
import net.techcable.tacospigot.utils.BlockHelper;
// TacoSpigot end

public class AntiXray
{

    // Used to keep track of which blocks to obfuscate
    private final boolean[] obfuscateBlocks = new boolean[ Short.MAX_VALUE ];
    // Used to select a random replacement ore
    private final byte[] replacementOres;
    // PaperSpigot start
    public boolean queueUpdates = true;
    public final Set<BlockPosition> pendingUpdates = new HashSet<BlockPosition>();
    // PaperSpigot end

    public AntiXray(SpigotWorldConfig config)
    {
        // Set all listed blocks as true to be obfuscated
        for ( int id : ( config.engineMode == 1 ) ? config.hiddenBlocks : config.replaceBlocks )
        {
            obfuscateBlocks[id] = true;
        }

        // For every block
        TByteSet blocks = new TByteHashSet();
        for ( Integer i : config.hiddenBlocks )
        {
            Block block = Block.getById( i );
            // Check it exists and is not a tile entity
            if ( block != null && !block.isTileEntity() )
            {
                // Add it to the set of replacement blocks
                blocks.add( (byte) (int) i );
            }
        }
        // Bake it to a flat array of replacements
        replacementOres = blocks.toArray();
    }

    /**
     * PaperSpigot - Flush queued block updates for world.
     */
    public void flushUpdates(World world)
    {
        if ( world.spigotConfig.antiXray && !pendingUpdates.isEmpty() )
        {
            queueUpdates = false;

            for ( BlockPosition position : pendingUpdates )
            {
                updateNearbyBlocks( world, position );
            }

            pendingUpdates.clear();
            queueUpdates = true;
        }
    }

    /**
     * Starts the timings handler, then updates all blocks within the set radius
     * of the given coordinate, revealing them if they are hidden ores.
     */
    public void updateNearbyBlocks(World world, BlockPosition position)
    {
        if ( world.spigotConfig.antiXray )
        {
            // PaperSpigot start
            if ( queueUpdates )
            {
                pendingUpdates.add( position );
                return;
            }
            // PaperSpigot end
            SpigotTimings.antiXrayUpdateTimer.startTiming();
            updateNearbyBlocks( world, position, 2, false ); // 2 is the radius, we shouldn't change it as that would make it exponentially slower
            SpigotTimings.antiXrayUpdateTimer.stopTiming();
        }
    }

    /**
     * Starts the timings handler, and then removes all non exposed ores from
     * the chunk buffer.
     */
    public void obfuscateSync(int chunkX, int chunkY, int bitmask, byte[] buffer, World world)
    {
        if ( world.spigotConfig.antiXray )
        {
            SpigotTimings.antiXrayObfuscateTimer.startTiming();
            obfuscate( chunkX, chunkY, bitmask, buffer, world );
            SpigotTimings.antiXrayObfuscateTimer.stopTiming();
        }
    }

    /**
     * Removes all non exposed ores from the chunk buffer.
     */
    public void obfuscate(int chunkX, int chunkY, int bitmask, byte[] buffer, World world)
    {
        // If the world is marked as obfuscated
        if ( world.spigotConfig.antiXray )
        {
            // Initial radius to search around for air
            int initialRadius = 1;
            // Which block in the buffer we are looking at, anywhere from 0 to 16^4
            int index = 0;
            // The iterator marking which random ore we should use next
            int randomOre = 0;

            // Chunk corner X and Z blocks
            int startX = chunkX << 4;
            int startZ = chunkY << 4;

            byte replaceWithTypeId;
            switch ( world.getWorld().getEnvironment() )
            {
                case NETHER:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.NETHERRACK);
                    break;
                case THE_END:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.END_STONE);
                    break;
                default:
                    replaceWithTypeId = (byte) CraftMagicNumbers.getId(Blocks.STONE);
                    break;
            }

            BlockPosition.MutableBlockPosition pos = new BlockPosition.MutableBlockPosition(); // TacoSpigot - preallocate MutableBlockPosition
            // Chunks can have up to 16 sections
            for ( int i = 0; i < 16; i++ )
            {
                // If the bitmask indicates this chunk is sent...
                if ( ( bitmask & 1 << i ) != 0 )
                {
                    // Work through all blocks in the chunk, y,z,x
                    for ( int y = 0; y < 16; y++ )
                    {
                        for ( int z = 0; z < 16; z++ )
                        {
                            for ( int x = 0; x < 16; x++ )
                            {
                                // For some reason we can get too far ahead of ourselves (concurrent modification on bulk chunks?) so if we do, just abort and move on
                                if ( index >= buffer.length )
                                {
                                    index++;
                                    continue;
                                }
                                // Grab the block ID in the buffer.
                                // TODO: extended IDs are not yet supported
                                int blockId = (buffer[index << 1] & 0xFF)
                                        | ((buffer[(index << 1) + 1] & 0xFF) << 8);
                                blockId >>>= 4;
                                // Check if the block should be obfuscated
                                if ( obfuscateBlocks[blockId] )
                                {
                                    // The world isn't loaded, bail out
                                    // TacoSpigot start
                                    pos.setValues(startX + x, ( i << 4 ) + y, startZ + z);
                                    if ( !isLoaded( world, /* new BlockPosition( startX + x, ( i << 4 ) + y, startZ + z ) */ pos, initialRadius ) )
                                    {
                                        // TacoSpigot end
                                        index++;
                                        continue;
                                    }
                                    // On the otherhand, if radius is 0, or the nearby blocks are all non air, we can obfuscate
                                    if ( !hasTransparentBlockAdjacent( world, /* new BlockPosition( startX + x, ( i << 4 ) + y, startZ + z ) */ pos, initialRadius ) ) // TacoSpigot - use prexisting MutableBlockPosition
                                    {
                                        int newId = blockId;
                                        switch ( world.spigotConfig.engineMode )
                                        {
                                            case 1:
                                                // Replace with replacement material
                                                newId = replaceWithTypeId & 0xFF;
                                                break;
                                            case 2:
                                                // Replace with random ore.
                                                if ( randomOre >= replacementOres.length )
                                                {
                                                    randomOre = 0;
                                                }
                                                newId = replacementOres[randomOre++] & 0xFF;
                                                break;
                                        }
                                        newId <<= 4;
                                        buffer[index << 1] = (byte) (newId & 0xFF);
                                        buffer[(index << 1) + 1] = (byte) ((newId >> 8) & 0xFF);
                                    }
                                }

                                index++;
                            }
                        }
                    }
                }
            }
        }
    }

    // TacoSpigot start
    private void updateNearbyBlocks(World world, final BlockPosition startPos, int radius, boolean updateSelf) {
        int startX = startPos.getX() - radius;
        int endX = startPos.getX() + radius;
        int startY = Math.max(0, startPos.getY() - radius);
        int endY = Math.min(255, startPos.getY() + radius);
        int startZ = startPos.getZ() - radius;
        int endZ = startPos.getZ() + radius;
        BlockPosition.MutableBlockPosition adjacent = new BlockPosition.MutableBlockPosition();
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    adjacent.setValues(x, y, z);
                    if (!updateSelf && x == startPos.getX() & y == startPos.getY() & z == startPos.getZ()) continue;
                    if (world.isLoaded(adjacent)) updateBlock(world, adjacent);
                }
            }
        }
    }

    private void updateBlock(World world, BlockPosition position)
    {
        // If the block in question is loaded
        if ( true ) // TacoSpigot - caller checked
        {
            // Get block id
            Block block = getType(world, position); // TacoSpigot - directly access the underlying data

            // See if it needs update
            if ( obfuscateBlocks[Block.getId( block )] ) // TacoSpigot - always update
            {
                // Send the update
                world.notify( position );
            }

            // TacoSpigot start
            /*
            // Check other blocks for updates
            if ( radius > 0 )
            {
                updateNearbyBlocks( world, position.east(), radius - 1, true );
                updateNearbyBlocks( world, position.west(), radius - 1, true );
                updateNearbyBlocks( world, position.up(), radius - 1, true );
                updateNearbyBlocks( world, position.down(), radius - 1, true );
                updateNearbyBlocks( world, position.south(), radius - 1, true );
                updateNearbyBlocks( world, position.north(), radius - 1, true );
            }
            */
            // TacoSpigot end
        }
    }

    private static boolean isLoaded(World world, BlockPosition position, int radius)
    {
        // TacoSpigot start
        return BlockHelper.isAllAdjacentBlocksLoaded(world, position, radius);
        // TacoSpigot end
    }

    // TacoSpigot start
    private static boolean hasTransparentBlockAdjacent(World w, BlockPosition startPos, int radius)
    {
        // !(solid blocks all around)
        return !BlockHelper.isAllAdjacentBlocksFillPredicate(w, startPos, radius, (world, position) -> {
            Block block = getType(world, position);
            return isSolidBlock(block);
        }); /* isSolidBlock */
        // TacoSpigot end
    }

    private static boolean isSolidBlock(Block block) {
        // Mob spawners are treated as solid blocks as far as the
        // game is concerned for lighting and other tasks but for
        // rendering they can be seen through therefor we special
        // case them so that the antixray doesn't show the fake
        // blocks around them.
        return block.isOccluding() && block != Blocks.MOB_SPAWNER && block != Blocks.BARRIER;
    }

    // TacoSpigot start
    public static Block getType(World world, BlockPosition pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        Chunk chunk = world.getChunkIfLoaded(x >> 4, z >> 4);
        if (chunk == null) return Blocks.AIR;
        int sectionId = y >> 4;
        if (sectionId < 0 || sectionId > 15) return Blocks.AIR;
        ChunkSection section = chunk.getSections()[sectionId];
        if (section == null) return Blocks.AIR; // Handle empty chunks
        x &= 0xF;
        y &= 0xF;
        z &= 0xF;
        int combinedId = section.getIdArray()[(y << 8) | (z << 4) | x];
        int blockId = combinedId >> 4;
        return BlockHelper.getBlock(blockId);
    }
    // TacoSpigot end
}
