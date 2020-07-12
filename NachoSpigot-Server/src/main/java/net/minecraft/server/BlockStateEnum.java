package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class BlockStateEnum<T extends Enum<T> & INamable> extends BlockState<T> {

    private final ImmutableSet<T> a;
    private final Map<String, T> b = Maps.newHashMap();

    protected BlockStateEnum(String s, Class<T> oclass, Collection<T> collection) {
        super(s, oclass);
        this.a = ImmutableSet.copyOf(collection);
        Iterator<T> iterator = collection.iterator(); // TacoSpigot - generic iterator

        while (iterator.hasNext()) {
            T oenum = iterator.next(); // TacoSpigot - generics
            String s1 = ((INamable) oenum).getName();

            if (this.b.containsKey(s1)) {
                throw new IllegalArgumentException("Multiple values have the same name \'" + s1 + "\'");
            }

            this.b.put(s1, oenum);
        }

    }

    public Collection<T> c() {
        return this.a;
    }
    public String a(T t0) {
        return ((INamable) t0).getName();
    }

    // TacoSpigot start
    @Override
    public int
getValueId(T value) {
        return value.ordinal();
    }

    @Override
    public T getByValueId(int id) {
        T[] values = this.b().getEnumConstants();
        if (id >= 0 && id < values.length) {
            return values[id];
        } else {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
    }
    // TacoSpigot end

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> of(String s, Class<T> oclass) {
        return a(s, oclass, Predicates.alwaysTrue());
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> a(String s, Class<T> oclass, Predicate<T> predicate) {
        return a(s, oclass, Collections2.filter(Lists.newArrayList(oclass.getEnumConstants()), predicate));
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> of(String s, Class<T> oclass, T... at) {
        return a(s, oclass, (Collection) Lists.newArrayList(at));
    }

    public static <T extends Enum<T> & INamable> BlockStateEnum<T> a(String s, Class<T> oclass, Collection<T> collection) {
        return new BlockStateEnum(s, oclass, collection);
    }

    // TacoSpigot start - fix stupid generic thingies
    /*
    public String a(Comparable comparable) {
        return this.a((Enum) comparable);
    }
    */
    // TacoSpigot end
}
