package net.minecraft.server;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// TacoSpigot start
import com.google.common.collect.Table;

import net.techcable.tacospigot.BlockStateRegistry;
import net.techcable.tacospigot.ImmutableArrayMap;
import net.techcable.tacospigot.ImmutableArrayTable;
import net.techcable.tacospigot.TacoSpigotConfig;
// TacoSpigot end

public class BlockStateList {

    private static final Joiner a = Joiner.on(", ");
    private static final Function<IBlockState, String> b = new Function() {
        public String a(IBlockState iblockstate) {
            return iblockstate == null ? "<NULL>" : iblockstate.a();
        }

        public Object apply(Object object) {
            return this.a((IBlockState) object);
        }
    };
    private final Block c;
    private final ImmutableList<IBlockState> d;
    private final ImmutableList<IBlockData> e;

    public BlockStateList(Block block, IBlockState... aiblockstate) {
        this.c = block;
        Arrays.sort(aiblockstate, new Comparator() {
            public int a(IBlockState iblockstate, IBlockState iblockstate1) {
                return iblockstate.a().compareTo(iblockstate1.a());
            }

            public int compare(Object object, Object object1) {
                return this.a((IBlockState) object, (IBlockState) object1);
            }
        });
        for (IBlockState state : aiblockstate) state.tryInitId(); // TacoSpigot
        this.d = ImmutableList.copyOf(aiblockstate);
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        ArrayList arraylist = Lists.newArrayList();
        Iterable iterable = IteratorUtils.a(this.e());
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            List list = (List) iterator.next();
            Map map = MapGeneratorUtils.b(this.d, list);
            BlockStateList.BlockData blockstatelist_blockdata = new BlockStateList.BlockData(block, ImmutableMap.copyOf(map), null);

            linkedhashmap.put(map, blockstatelist_blockdata);
            arraylist.add(blockstatelist_blockdata);
        }

        iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            BlockStateList.BlockData blockstatelist_blockdata1 = (BlockStateList.BlockData) iterator.next();

            blockstatelist_blockdata1.a((Map) linkedhashmap);
        }

        this.e = ImmutableList.copyOf(arraylist);
    }

    public ImmutableList<IBlockData> a() {
        return this.e;
    }

    private List<Iterable<Comparable>> e() {
        ArrayList arraylist = Lists.newArrayList();

        for (int i = 0; i < this.d.size(); ++i) {
            arraylist.add(((IBlockState) this.d.get(i)).c());
        }

        return arraylist;
    }

    public IBlockData getBlockData() {
        return (IBlockData) this.e.get(0);
    }

    public Block getBlock() {
        return this.c;
    }

    public Collection<IBlockState> d() {
        return this.d;
    }

    public String toString() {
        return Objects.toStringHelper(this).add("block", Block.REGISTRY.c(this.c)).add("properties", Iterables.transform(this.d, BlockStateList.b)).toString();
    }

    static class BlockData extends BlockDataAbstract {

        private final Block a;
        // TacoSpigot start
        private final ImmutableMap<IBlockState, Comparable> bAsImmutableMap;
        private final Map<IBlockState, Comparable> b;
        private Table<IBlockState, Comparable, IBlockData> c;
        // TacoSpigot end

        private BlockData(Block block, ImmutableMap<IBlockState, Comparable> immutablemap) {
            this.a = block;
            // TacoSpigot start
            this.bAsImmutableMap = immutablemap;
            if (TacoSpigotConfig.useArraysForBlockStates) {
                b = new ImmutableArrayMap<>(IBlockState.INDEXER, immutablemap);
            } else {
                b = immutablemap;
            }
            // TacoSpigot end
        }

        public Collection<IBlockState> a() {
            return Collections.unmodifiableCollection(this.b.keySet());
        }

        public <T extends Comparable<T>> T get(IBlockState<T> iblockstate) {
            // TacoSpigot start - runtime check -> assertion
            assert this.b.containsKey(iblockstate) : "Cannot get property " + iblockstate + " as it does not exist in " + this.a.P();
            Object value = this.b.get(iblockstate);
            assert value == bAsImmutableMap.get(iblockstate) : "Array map gave data " + String.valueOf(value) + " and regular map gave data " + String.valueOf(bAsImmutableMap.get(iblockstate));
            assert value != null : "Null value for state " + iblockstate + " and data " + this;
            assert iblockstate.b().isInstance(value) : "Value " + value + " for state " + iblockstate + " and data " + this + " not instanceof " + iblockstate.b().getTypeName();
            return (T) value;
            // TacoSpigot end
        }

        public <T extends Comparable<T>, V extends T> IBlockData set(IBlockState<T> iblockstate, V v0) {
            // TacoSpigot start - runtime check -> assertion
            assert iblockstate != null : "Null block state";
            assert v0 != null : "Null value for block state " + iblockstate;
            assert this.b.containsKey(iblockstate) : "Cannot set property " + iblockstate + " as it does not exist in " + this.a.P();
            assert  iblockstate.c().contains(v0) : "Cannot set property " + iblockstate + " to " + v0 + " on block " + Block.REGISTRY.c(this.a) + ", it is not an allowed value";
            IBlockData data = (IBlockData) (this.b.get(iblockstate) == v0 ? this : (IBlockData) this.c.get(iblockstate, v0));
            assert data != null : "No block data with property " + iblockstate + " and value " + v0 + " for block data " + this;
            return data;
            // TacoSpigot end
        }

        public ImmutableMap<IBlockState, Comparable> b() {
            return this.bAsImmutableMap; // TacoSpigot
        }

        public Block getBlock() {
            return this.a;
        }

        public boolean equals(Object object) {
            return this == object;
        }

        public int hashCode() {
            return this.b.hashCode();
        }

        public void a(Map<Map<IBlockState, Comparable>, BlockStateList.BlockData> map) {
            if (this.c != null) {
                throw new IllegalStateException();
            } else {
                HashBasedTable hashbasedtable = HashBasedTable.create();
                Iterator iterator = this.b.keySet().iterator();

                while (iterator.hasNext()) {
                    IBlockState iblockstate = (IBlockState) iterator.next();
                    Iterator iterator1 = iblockstate.c().iterator();

                    while (iterator1.hasNext()) {
                        Comparable comparable = (Comparable) iterator1.next();

                        if (comparable != this.get(iblockstate)) { // TacoSpigot - use this.get(iblockstate) instead of this.b.get(iblockstate)
                            assert map.get(this.b(iblockstate, comparable)) != null : "Map doesn't contain block data with state " + iblockstate + " and comparable " + comparable + b(iblockstate, comparable); // TacoSpigot - assert present
                            hashbasedtable.put(iblockstate, comparable, map.get(this.b(iblockstate, comparable)));
                        }
                    }
                }

                // TacoSpigot start
                if (TacoSpigotConfig.useArraysForBlockStates) {
                    this.c = new ImmutableArrayTable<IBlockState, Comparable, IBlockData> (
                            IBlockState.INDEXER,
                            (IBlockState state, Comparable value) -> state.getValueId(value),
                            hashbasedtable
                    );
                } else {
                    this.c = ImmutableTable.copyOf(hashbasedtable);
                }
                // TacoSpigot end
            }
        }

        private Map<IBlockState, Comparable> b(IBlockState iblockstate, Comparable comparable) {
            HashMap hashmap = Maps.newHashMap(this.b);

            hashmap.put(iblockstate, comparable);
            return hashmap;
        }

        BlockData(Block block, ImmutableMap immutablemap, Object object) {
            this(block, immutablemap);
        }
    }
}
