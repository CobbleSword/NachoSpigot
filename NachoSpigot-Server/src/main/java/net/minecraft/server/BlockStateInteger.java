package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class BlockStateInteger extends BlockState<Integer> {

    private final ImmutableSet<Integer> a;
    // TacoSpigot start
    private final int min, max;
    private final int range;

    @Override
    public int getValueId(Integer value) {
        if (value < min) {
            throw new IllegalArgumentException("Too small: " + value);
        } else if (value > max) {
            throw new IllegalArgumentException("Too large: " + value);
        } else {
            return value - min;
        }
    }

    @Override
    public Integer getByValueId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Negative id: " + id);
        } else if (id > range) {
            throw new IllegalArgumentException("Id is out of range: " + id);
        } else {
            return id;
        }
    }
    // TacoSpigot end

    protected BlockStateInteger(String s, int i, int j) {
        super(s, Integer.class);
        // TacoSpigot start
        this.min = i;
        this.max = j;
        this.range = (max - min); // min and max are _both_ inclusive (there's a reason you're not supposed to do this :p)
        // TacoSpigot end
        if (i < 0) {
            throw new IllegalArgumentException("Min value of " + s + " must be 0 or greater");
        } else if (j <= i) {
            throw new IllegalArgumentException("Max value of " + s + " must be greater than min (" + i + ")");
        } else {
            HashSet hashset = Sets.newHashSet();

            for (int k = i; k <= j; ++k) {
                hashset.add(Integer.valueOf(k));
            }

            this.a = ImmutableSet.copyOf(hashset);
        }
    }

    public Collection<Integer> c() {
        return this.a;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            if (!super.equals(object)) {
                return false;
            } else {
                BlockStateInteger blockstateinteger = (BlockStateInteger) object;

                return this.a.equals(blockstateinteger.a);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = super.hashCode();

        i = 31 * i + this.a.hashCode();
        return i;
    }

    public static BlockStateInteger of(String s, int i, int j) {
        return new BlockStateInteger(s, i, j);
    }

    public String a(Integer integer) {
        return integer.toString();
    }

    // TacoSpigot start - fix stupid generic thingies
    /*
    public String a(Comparable comparable) {
        return this.a((Integer) comparable);
    }
    */
    // TacoSpigot end
}
