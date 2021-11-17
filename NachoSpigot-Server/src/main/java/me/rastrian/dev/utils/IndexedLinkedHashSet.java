package me.rastrian.dev.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class IndexedLinkedHashSet<E> implements Set<E> {

    private final ArrayList<E> list = new ArrayList<>();
    private final HashSet<E> set = new HashSet<>();

    public boolean add(E e) {
        if (set.add(e)) {
            return list.add(e);
        }
        return false;
    }

    public boolean remove(Object o) {
        if (set.remove(o)) {
            return list.remove(o);
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    public void clear() {
        set.clear();
        list.clear();
    }

    public E get(int index) {
        return list.get(index);
    }

    public boolean removeAll(Collection<?> c) {
        if (set.removeAll(c)) {
            return list.removeAll(c);
        }
        return true;
    }

    public boolean retainAll(Collection<?> c) {
        if (set.retainAll(c)) {
            return list.retainAll(c);
        }
        return false;
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }
    
    public int indexOf(Object o) {
        return list.indexOf(o);
    }
}