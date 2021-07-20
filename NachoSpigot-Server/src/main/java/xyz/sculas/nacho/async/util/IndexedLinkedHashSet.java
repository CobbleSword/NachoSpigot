package xyz.sculas.nacho.async.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class IndexedLinkedHashSet<E> implements Set<E> {
    private final ArrayList<E> list = new ArrayList<>();
    private final HashSet<E> set = new HashSet<>();

    public boolean add(E e) {
        if (this.set.add(e)) {
            return this.list.add(e);
        }
        return false;
    }

    public boolean remove(Object o) {
        if (this.set.remove(o)) {
            return this.list.remove(o);
        }
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        return this.set.containsAll(c);
    }

    public void clear() {
        this.set.clear();
        this.list.clear();
    }

    public E get(int index) {
        return this.list.get(index);
    }

    public boolean removeAll(Collection<?> c) {
        if (this.set.removeAll(c)) {
            return this.list.removeAll(c);
        }
        return true;
    }

    public boolean retainAll(Collection<?> c) {
        if (this.set.retainAll(c)) {
            return this.list.retainAll(c);
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

    public int size() {
        return this.set.size();
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public boolean contains(Object o) {
        return this.set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.list.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.list.toArray(a);
    }

    public int indexOf(Object o) {
        return this.list.indexOf(o);
    }
}