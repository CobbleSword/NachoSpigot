package net.minecraft.server;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
// TacoSpigot start
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
// TacoSpigot end

public class DataWatcher {

    private final Entity a;
    private boolean b = true;
    // Spigot Start
    // TacoSpigot start - use fastutil instead of trove
    private static final Object2IntMap<Class<?>> classToId = new Object2IntOpenHashMap(10, 0.5f);
    private final Int2ObjectMap dataValues = new Int2ObjectOpenHashMap(10, 0.5f);
    // These exist as an attempt at backwards compatability for (broken) NMS plugins
    private static final Map<Class<?>, Integer> c = classToId;
    private final Map<Integer, DataWatcher.WatchableObject> d = dataValues;
    // TacoSpigot end
    // Spigot End
    private boolean e;
//    private ReadWriteLock f = new ReentrantReadWriteLock(); // Spigot - Remove DataWatcher Locking
    boolean registrationLocked; // Spigot

    public DataWatcher(Entity entity) {
        this.a = entity;
    }

    public <T> void a(int i, T t0) {
        if (this.registrationLocked) throw new IllegalStateException("Registering datawatcher object after entity initialization"); // Spigot
        int integer = classToId.get(t0.getClass()); // Spigot

        if (integer == -1) { // Spigot
            throw new IllegalArgumentException("Unknown data type: " + t0.getClass());
        } else if (i > 31) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 31 + ")");
        } else if (this.dataValues.containsKey(i)) { // Spigot
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        } else {
            DataWatcher.WatchableObject datawatcher_watchableobject = new DataWatcher.WatchableObject(integer, i, t0); // Spigot

//            this.f.writeLock().lock(); // Spigot - not required
            this.dataValues.put(i, datawatcher_watchableobject); // Spigot
//            this.f.writeLock().unlock(); // Spigot - not required
            this.b = false;
        }
    }

    public void add(int i, int j) {
        DataWatcher.WatchableObject datawatcher_watchableobject = new DataWatcher.WatchableObject(j, i, (Object) null);

//        this.f.writeLock().lock(); // Spigot - not required
        this.dataValues.put(i, datawatcher_watchableobject); // Spigot
//        this.f.writeLock().unlock(); // Spigot - not required
        this.b = false;
    }

    public byte getByte(int i) {
        return ((Byte) this.j(i).b()).byteValue();
    }

    public short getShort(int i) {
        return ((Short) this.j(i).b()).shortValue();
    }

    public int getInt(int i) {
        return ((Integer) this.j(i).b()).intValue();
    }

    public float getFloat(int i) {
        return ((Float) this.j(i).b()).floatValue();
    }

    public String getString(int i) {
        return (String) this.j(i).b();
    }

    public ItemStack getItemStack(int i) {
        return (ItemStack) this.j(i).b();
    }

    private DataWatcher.WatchableObject j(int i) {
        // Spigot - not required
        /*
        this.f.readLock().lock();

        DataWatcher.WatchableObject datawatcher_watchableobject;

        try {
            datawatcher_watchableobject = (DataWatcher.WatchableObject) this.dataValues.get(i); // Spigot
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Getting synched entity data");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Synched entity data");

            crashreportsystemdetails.a("Data ID", (Object) Integer.valueOf(i));
            throw new ReportedException(crashreport);
        }

        this.f.readLock().unlock();
        return datawatcher_watchableobject;

         */
        return (WatchableObject) this.dataValues.get(i);
    }

    public Vector3f h(int i) {
        return (Vector3f) this.j(i).b();
    }

    public <T> void watch(int i, T t0) {
        DataWatcher.WatchableObject datawatcher_watchableobject = this.j(i);

        if (ObjectUtils.notEqual(t0, datawatcher_watchableobject.b())) {
            datawatcher_watchableobject.a(t0);
            this.a.i(i);
            datawatcher_watchableobject.a(true);
            this.e = true;
        }

    }

    public void update(int i) {
        this.j(i).d = true;
        this.e = true;
    }

    public boolean a() {
        return this.e;
    }

    public static void a(List<DataWatcher.WatchableObject> list, PacketDataSerializer serializer) throws IOException {
        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                DataWatcher.WatchableObject datawatcher_watchableobject = (DataWatcher.WatchableObject) iterator.next();

                a(serializer, datawatcher_watchableobject);
            }
        }

        serializer.writeByte(127);
    }

    public List<DataWatcher.WatchableObject> b() {
        ArrayList arraylist = null;

        if (this.e) {
//            this.f.readLock().lock(); // Spigot - not required
            Iterator iterator = this.dataValues.values().iterator(); // Spigot // TacoSpigot

            while (iterator.hasNext()) {
                DataWatcher.WatchableObject datawatcher_watchableobject = (DataWatcher.WatchableObject) iterator.next();

                if (datawatcher_watchableobject.d()) {
                    datawatcher_watchableobject.a(false);
                    if (arraylist == null) {
                        arraylist = Lists.newArrayList();
                    }

                   // Spigot start - copy ItemStacks to prevent ConcurrentModificationExceptions
                    if ( datawatcher_watchableobject.b() instanceof ItemStack )
                    {
                        datawatcher_watchableobject = new WatchableObject(
                                datawatcher_watchableobject.c(),
                                datawatcher_watchableobject.a(),
                                ( (ItemStack) datawatcher_watchableobject.b() ).cloneItemStack()
                        );
                    }
                    // Spigot end

                    arraylist.add(datawatcher_watchableobject);
                }
            }

//            this.f.readLock().unlock(); // Spigot - not required
        }

        this.e = false;
        return arraylist;
    }

    public void a(PacketDataSerializer serializer) throws IOException {
//        this.f.readLock().lock(); // Spigot - not required
        Iterator iterator = this.dataValues.values().iterator(); // Spigot // TacoSpigot

        while (iterator.hasNext()) {
            DataWatcher.WatchableObject datawatcher_watchableobject = (DataWatcher.WatchableObject) iterator.next();

            a(serializer, datawatcher_watchableobject);
        }

//        this.f.readLock().unlock(); // Spigot - not required
        serializer.writeByte(127);
    }

    public List<DataWatcher.WatchableObject> c() {
        ArrayList arraylist = Lists.newArrayList(); // Spigot

//        this.f.readLock().lock(); // Spigot - not required

        arraylist.addAll(this.dataValues.values()); // Spigot // TacoSpigot
        // Spigot start - copy ItemStacks to prevent ConcurrentModificationExceptions
        for ( int i = 0; i < arraylist.size(); i++ )
        {
            WatchableObject watchableobject = (WatchableObject) arraylist.get( i );
            if ( watchableobject.b() instanceof ItemStack )
            {
                watchableobject = new WatchableObject(
                        watchableobject.c(),
                        watchableobject.a(),
                        ( (ItemStack) watchableobject.b() ).cloneItemStack()
                );
                arraylist.set( i, watchableobject );
            }
        }
        // Spigot end

//        this.f.readLock().unlock(); // Spigot - not required
        return arraylist;
    }

    private static void a(PacketDataSerializer serializer, DataWatcher.WatchableObject datawatcher_watchableobject) throws IOException {
        int i = (datawatcher_watchableobject.c() << 5 | datawatcher_watchableobject.a() & 31) & 255;

        serializer.writeByte(i);
        switch (datawatcher_watchableobject.c()) {
        case 0:
            serializer.writeByte(((Byte) datawatcher_watchableobject.b()).byteValue());
            break;

        case 1:
            serializer.writeShort(((Short) datawatcher_watchableobject.b()).shortValue());
            break;

        case 2:
            serializer.writeInt(((Integer) datawatcher_watchableobject.b()).intValue());
            break;

        case 3:
            serializer.writeFloat(((Float) datawatcher_watchableobject.b()).floatValue());
            break;

        case 4:
            serializer.a((String) datawatcher_watchableobject.b());
            break;

        case 5:
            ItemStack itemstack = (ItemStack) datawatcher_watchableobject.b();

            serializer.a(itemstack);
            break;

        case 6:
            BlockPosition blockposition = (BlockPosition) datawatcher_watchableobject.b();

            serializer.writeInt(blockposition.getX());
            serializer.writeInt(blockposition.getY());
            serializer.writeInt(blockposition.getZ());
            break;

        case 7:
            Vector3f vector3f = (Vector3f) datawatcher_watchableobject.b();

            serializer.writeFloat(vector3f.getX());
            serializer.writeFloat(vector3f.getY());
            serializer.writeFloat(vector3f.getZ());
        }

    }

    public static List<DataWatcher.WatchableObject> b(PacketDataSerializer serializer) throws IOException {
        ArrayList arraylist = null;

        for (byte b0 = serializer.readByte(); b0 != 127; b0 = serializer.readByte()) {
            if (arraylist == null) {
                arraylist = Lists.newArrayList();
            }

            int i = (b0 & 224) >> 5;
            int j = b0 & 31;
            DataWatcher.WatchableObject datawatcher_watchableobject = null;

            switch (i) {
            case 0:
                datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, Byte.valueOf(serializer.readByte()));
                break;

            case 1:
                datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, Short.valueOf(serializer.readShort()));
                break;

            case 2:
                datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, Integer.valueOf(serializer.readInt()));
                break;

            case 3:
                datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, Float.valueOf(serializer.readFloat()));
                break;

            case 4:
                datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, serializer.readUtf(32767)); // Nacho - deobfuscate readUtf
                break;

            case 5:
                datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, serializer.decodeItemStack());
                break;

            case 6:
                int k = serializer.readInt();
                int l = serializer.readInt();
                int i1 = serializer.readInt();

                datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, new BlockPosition(k, l, i1));
                break;

            case 7:
                float f = serializer.readFloat();
                float f1 = serializer.readFloat();
                float f2 = serializer.readFloat();

                datawatcher_watchableobject = new DataWatcher.WatchableObject(i, j, new Vector3f(f, f1, f2));
            }

            arraylist.add(datawatcher_watchableobject);
        }

        return arraylist;
    }

    public boolean d() {
        return this.b;
    }

    public void e() {
        this.e = false;
    }

    static {
        // Spigot Start - remove valueOf
        classToId.put(Byte.class, 0);
        classToId.put(Short.class, 1);
        classToId.put(Integer.class, 2);
        classToId.put(Float.class, 3);
        classToId.put(String.class, 4);
        classToId.put(ItemStack.class, 5);
        classToId.put(BlockPosition.class, 6);
        classToId.put(Vector3f.class, 7);
        // Spigot End
    }

    public static class WatchableObject {

        private final int a;
        private final int b;
        private Object c;
        private boolean d;

        public WatchableObject(int i, int j, Object object) {
            this.b = j;
            this.c = object;
            this.a = i;
            this.d = true;
        }

        public int a() {
            return this.b;
        }

        public void a(Object object) {
            this.c = object;
        }

        public Object b() {
            return this.c;
        }

        public int c() {
            return this.a;
        }

        public boolean d() {
            return this.d;
        }

        public void a(boolean flag) {
            this.d = flag;
        }
    }
}
