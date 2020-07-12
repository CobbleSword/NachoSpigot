package net.techcable.tacospigot;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class ImmutableArrayMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    private final Indexer<K> indexer;
    private final int offset;
    private final Object[] data;
    private final Object[] outlyingData;
    private final Entry<K, V>[] entries;
    private final int[] outlyingIds;

    private final int size;

    @SuppressWarnings("Convert2Lambda") // The comparator is anonomous for performance reasons
    public ImmutableArrayMap(Indexer<K> getId, Map<K, V> map) {
        Preconditions.checkNotNull(getId, "Null getId function");
        Preconditions.checkNotNull(map, "Null map");
        this.indexer = getId;
        this.size = map.size();
        this.keyComparator = new Comparator<K>() {
            @Override
            public int compare(K o1, K o2) {
                return Integer.compare(indexer.getId(o1), indexer.getId(o2));
            }
        };
        @SuppressWarnings("unchecked")
        Entry<K, V>[] entries = new Entry[size];
        Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
        for (int i = 0; i < entries.length; i++) {
            Preconditions.checkArgument(iterator.hasNext(), "Expected %s entries but only got %s", size, i + 1);
            Entry<K, V> entry = iterator.next();
            entries[i] = Maps.immutableEntry(entry.getKey(), entry.getValue());
        }
        Arrays.sort(entries, (entry1, entry2) -> keyComparator.compare(entry1.getKey(), entry2.getKey()));
        Preconditions.checkArgument(!iterator.hasNext(), "Got more than expected %s entries", size);
        int[] ids = Arrays.stream(entries).map(Entry::getKey).mapToInt(indexer::getId).toArray(); // Don't worry, its sorted by key id ;)
        int[] largestRangeOfSequentialValues = calculateLargestRangeOfSequentialValues(ids);
        int minIndex = largestRangeOfSequentialValues == null ? -1 : largestRangeOfSequentialValues[0];
        int maxIndex = largestRangeOfSequentialValues == null ? -1 : largestRangeOfSequentialValues[1];
        int sequentalRangeSize = largestRangeOfSequentialValues == null ? 0 : largestRangeOfSequentialValues[2];
        if (sequentalRangeSize < size / 2) {
            System.err.println("Less than 50% of values are sequential");
            System.err.print(sequentalRangeSize);
            System.err.print(" out of ");
            System.err.println(size);
            System.err.println("Expect reduced performance");
        }
        this.data = new Object[sequentalRangeSize];
        this.outlyingIds = new int[size - sequentalRangeSize];
        this.outlyingData = new Object[size - sequentalRangeSize];
        this.offset = sequentalRangeSize == 0 ? 0 : ids[minIndex];
        this.entries = entries;
        int outlyingIndex = 0;
        for (int i = 0; i < entries.length; i++) {
            Entry<K, V> entry = entries[i];
            K key = entry.getKey();
            V value = entry.getValue();
            int id = indexer.getId(key);
            Preconditions.checkArgument(id >= 0, "Negative id for %s: %s", key, id);
            if (i >= minIndex && i < maxIndex) {
                int index = id - offset;
                data[index] = value;
            } else {
                int index = outlyingIndex++;
                outlyingIds[index] = id;
                outlyingData[index] = value;
            }
        }
    }

    private final Comparator<K> keyComparator;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        int id = indexer.getId((K) key);
        int index = id - offset;
        if (index >= 0 && index < data.length) {
            return (V) data[index];
        }
        int outlyingIndex = Arrays.binarySearch(outlyingIds, id);
        if (outlyingIndex >= 0 && outlyingIndex < outlyingData.length) {
            return (V) outlyingData[outlyingIndex];
        } else {
            return null;
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return Arrays.asList(entries).iterator();
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        for (Entry<K, V> entry : entries) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    private static int[] calculateLargestRangeOfSequentialValues(int[] ids) {
        int largestRangeSize = 0;
        int[] largestRange = new int[3];
        for (int minIndex = 0; minIndex < ids.length; minIndex++) {
            final int min = ids[minIndex];
            int lastNum = min;
            int maxIndex;
            for (maxIndex = minIndex + 1; maxIndex < ids.length; maxIndex++) {
                final int max = ids[maxIndex];
                if (lastNum + 1 != max) break; // The number is not sequential
                lastNum = max;
            }
            int rangeSize = maxIndex - minIndex;
            if (rangeSize > largestRangeSize) {
                largestRange[0] = minIndex;
                largestRange[1] = maxIndex;
                largestRange[2] = rangeSize;
                largestRangeSize = rangeSize;
            }
        }
        return largestRangeSize == 0 ? null : largestRange;
    }
}
