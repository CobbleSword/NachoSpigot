package dev.cobblesword.nachospigot.commons;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.server.BlockJukeBox.TileEntityRecordPlayer;
import net.minecraft.server.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;


/**
 * Optimized world tile entity list implementation, provides
 * an iterator of tile entities that need to be ticked based
 * on the world time to reduce needed iteration/checks.
 * @author Rastrian
 */
public final class OptimizedWorldTileEntitySet extends AbstractSet<TileEntity> {

    /**
     * Map of tile classes with modified tick intervals.
     */
    private static final Object2LongMap<Class<? extends TileEntity>> CUSTOM_TICK_INTERVALS =
            new Object2LongOpenHashMap<Class<? extends TileEntity>>() {{
                // Entities with empty tick# methods.
                this.put(TileEntityCommand.class, -1L);
                this.put(TileEntityComparator.class, -1L);
                this.put(TileEntityDispenser.class, -1L);
                this.put(TileEntityDropper.class, -1L);
                this.put(TileEntityEnderPortal.class, -1L);
                this.put(TileEntityFlowerPot.class, -1L);
                this.put(TileEntityNote.class, -1L);
                this.put(TileEntityRecordPlayer.class, -1L);
                this.put(TileEntitySign.class, -1L);
                this.put(TileEntitySkull.class, -1L);

                // Entities that we have modified to have empty tick# methods.
                this.put(TileEntityEnderChest.class, -1L);
                this.put(TileEntityChest.class, -1L);

                // Entities with vanilla controlled tick id checks inside
                // the tick# methods, helps to schedule only when required.
                this.put(TileEntityBeacon.class, 80L);
                this.put(TileEntityLightDetector.class, 20L);

                // Entities that do a scanPlayer# lookup, helps to slow down.
                this.put(TileEntityEnchantTable.class, 20L);
                this.put(TileEntityMobSpawner.class, 20L);
            }};

    /**
     * Multimap of all registered tile entities.
     */
    private final Multimap<Class<? extends TileEntity>, TileEntity> registeredTiles = HashMultimap.create();

    @Override
    public int size() {
        return this.registeredTiles.size();
    }

    @Override
    public boolean add(TileEntity tile) {
        if (tile == null) {
            return false;
        }

        if (this.registeredTiles.containsValue(tile)) {
            return false;
        }

        return this.registeredTiles.put(tile.getClass(), tile);
    }

    @Override
    public boolean remove(Object object) {
        if (object == null) {
            return false;
        }

        return this.registeredTiles.remove(object.getClass(), object);
    }

    @Override
    public boolean contains(Object object) {
        if (object == null) {
            return false;
        }

        return this.registeredTiles.containsEntry(object.getClass(), object);
    }

    @Override
    public void clear() {
        this.registeredTiles.clear();
    }

    @Override
    public @NotNull Iterator<TileEntity> iterator() {
        return this.registeredTiles.values().iterator();
    }

    public Iterator<TileEntity> tickIterator(long worldTime) {
        LinkedList<Iterator<TileEntity>> tileIterators = new LinkedList<>();
        for (Class<? extends TileEntity> tileClassToTick : this.getTileClassesToTick(worldTime)) {
            Collection<TileEntity> tileBucket = this.registeredTiles.get(tileClassToTick);
            if (tileBucket != null) {
                tileIterators.add(tileBucket.iterator());
            }
        }

        return Iterators.concat(tileIterators.iterator());
    }

    private List<Class<? extends TileEntity>> getTileClassesToTick(long worldTime) {
        List<Class<? extends TileEntity>> tilesToTick = new LinkedList<>();
        for (Class<? extends TileEntity> registeredTileClass : this.registeredTiles.keySet()) {
            long customTickInterval = OptimizedWorldTileEntitySet.CUSTOM_TICK_INTERVALS.getLong(registeredTileClass);
            if (customTickInterval != 0) { // Troves non-existent value is 0.
                if (customTickInterval > 0 && (worldTime == 0 || worldTime % customTickInterval == 0)) {
                    tilesToTick.add(registeredTileClass);
                }
                continue;
            }
            tilesToTick.add(registeredTileClass);
        }
        return tilesToTick;
    }
}