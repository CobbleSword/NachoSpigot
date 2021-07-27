package io.papermc.paper.util.maplist;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * list with O(1) remove & contains
 * @author Spottedleaf
 */
@SuppressWarnings("unchecked")
public final class ObjectMapList<T> extends AbstractReferenceList<T> implements Set<T> {

    protected final Int2IntOpenHashMap objectToIndex;

    protected static final Object[] EMPTY_LIST = new Object[0];
    protected T[] elements = (T[]) EMPTY_LIST;
    protected int count;

    public ObjectMapList() {
        this(2, 0.8f);
    }

    public ObjectMapList(int expectedSize, float loadFactor) {
        this.objectToIndex = new Int2IntOpenHashMap(expectedSize, loadFactor);
        this.objectToIndex.defaultReturnValue(Integer.MIN_VALUE);
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public int indexOf(Object object) {
        return this.objectToIndex.get(object.hashCode());
    }

    @Override
    public int lastIndexOf(Object object) {
        return super.indexOf(object);
    }

    @Override
    public boolean remove(final Object object) {
        final int index = this.objectToIndex.remove(object.hashCode());
        if (index == Integer.MIN_VALUE) {
            return false;
        }

        // move the obj at the end to this index
        final int endIndex = --this.count;
        final T end = this.elements[endIndex];
        if (index != endIndex) {
            // not empty after this call
            this.objectToIndex.put(end.hashCode(), index); // update index
        }
        this.elements[index] = end;
        this.elements[endIndex] = null;
        return true;
    }

    @Override
    public boolean add(final T object) {
        final int count = this.count;
        final int currIndex = this.objectToIndex.putIfAbsent(object.hashCode(), count);

        if (currIndex != Integer.MIN_VALUE) {
            return false; // already in this list
        }

        T[] list = this.elements;
        if (list.length == count) {
            // resize required
            list = this.elements = Arrays.copyOf(list, (int)Math.max(4L, (long) count << 1)); // overflow results in negative
        }

        list[count] = object;
        this.count = count + 1;
        return true;
    }

    @Override
    public void add(final int index, final T object) {
        final int currIndex = this.objectToIndex.putIfAbsent(object.hashCode(), index);

        if (currIndex != Integer.MIN_VALUE) {
            return; // already in this list
        }

        int count = this.count;
        T[] list = this.elements;
        if (list.length == count) {
            // resize required
            list = this.elements = Arrays.copyOf(list, (int) Math.max(4L, (long) count << 1)); // overflow results in negative
        }

        System.arraycopy(list, index, list, index + 1, count - index);
        list[index] = object;
        this.count = count + 1;
    }

    @Override
    public T get(int index) {
        return this.elements[index];
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    public T[] getRawData() {
        return this.elements;
    }

    @Override
    public void clear() {
        this.objectToIndex.clear();
        Arrays.fill(this.elements, 0, this.count, null);
        this.count = 0;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.elements, this.count);
    }

    @Override
    public ObjectSpliterator<T> spliterator() {
        return super.spliterator();
    }

    @Override
    public ObjectListIterator<T> iterator() {
        return new Iterator(0);
    }

    private class Iterator implements ObjectListIterator<T> {

        T lastRet;
        int current;

        Iterator(int index) {
            current = index;
        }

        @Override
        public int nextIndex() {
            return this.current + 1;
        }

        @Override
        public int previousIndex() {
            return this.current - 1;
        }

        @Override
        public boolean hasNext() {
            return this.current < ObjectMapList.this.count;
        }

        @Override
        public boolean hasPrevious() {
            return this.current > 0;
        }

        @Override
        public T next() {
            if (this.current >= ObjectMapList.this.count) {
                throw new NoSuchElementException();
            }
            return this.lastRet = ObjectMapList.this.elements[this.current++];
        }

        @Override
        public T previous() {
            if (this.current < 0) {
                throw new NoSuchElementException();
            }
            return this.lastRet = ObjectMapList.this.elements[--this.current];
        }

        @Override
        public void remove() {
            final T lastRet = this.lastRet;

            if (lastRet == null) {
                throw new IllegalStateException();
            }
            this.lastRet = null;

            ObjectMapList.this.remove(lastRet);
            --this.current;
        }
    }
}