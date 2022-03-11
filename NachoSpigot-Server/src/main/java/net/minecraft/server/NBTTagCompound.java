package net.minecraft.server;

import dev.cobblesword.nachospigot.commons.Constants;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

public class NBTTagCompound extends NBTBase {
    private it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<String, NBTBase> map = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(8, 0.8f); // Paper - reduce memory footprint of NBTTagCompound

    public NBTTagCompound() {
    }

    public NBTTagCompound(it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<String, NBTBase> map)
    {
        this.map = map;
    }

    void write(DataOutput var1) throws IOException {
        Iterator var2 = this.map.keySet().iterator();

        while(var2.hasNext()) {
            String var3 = (String)var2.next();
            NBTBase var4 = (NBTBase)this.map.get(var3);
            a(var3, var4, var1);
        }

        var1.writeByte(0);
    }

    void load(DataInput var1, int var2, NBTReadLimiter var3) throws IOException {
        var3.a(384L);
        if (var2 > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.map.clear();

            byte var4;
            while((var4 = a(var1, var3)) != 0) {
                String var5 = b(var1, var3);
                var3.a((long)(224 + 16 * var5.length()));
                NBTBase var6 = a(var4, var5, var1, var2 + 1, var3);
                if (this.map.put(var5, var6) != null) {
                    var3.a(288L);
                }
            }

        }
    }

    public Set<String> c() {
        return this.map.keySet();
    }

    public byte getTypeId() {
        return 10;
    }

    public void set(String var1, NBTBase var2) {
        this.map.put(var1, var2);
    }

    public void setByte(String var1, byte var2) {
        this.map.put(var1, new NBTTagByte(var2));
    }

    public void setShort(String var1, short var2) {
        this.map.put(var1, new NBTTagShort(var2));
    }

    public void setInt(String var1, int var2) {
        this.map.put(var1, new NBTTagInt(var2));
    }

    public void setLong(String var1, long var2) {
        this.map.put(var1, new NBTTagLong(var2));
    }

    public void setFloat(String var1, float var2) {
        this.map.put(var1, new NBTTagFloat(var2));
    }

    public void setDouble(String var1, double var2) {
        this.map.put(var1, new NBTTagDouble(var2));
    }

    public void setString(String var1, String var2) {
        this.map.put(var1, new NBTTagString(var2));
    }

    public void setByteArray(String var1, byte[] var2) {
        this.map.put(var1, new NBTTagByteArray(var2));
    }

    public void setIntArray(String var1, int[] var2) {
        this.map.put(var1, new NBTTagIntArray(var2));
    }

    public void setBoolean(String var1, boolean var2) {
        this.setByte(var1, (byte)(var2 ? 1 : 0));
    }

    public NBTBase get(String var1) {
        return (NBTBase)this.map.get(var1);
    }

    public byte b(String var1) {
        NBTBase var2 = (NBTBase)this.map.get(var1);
        return var2 != null ? var2.getTypeId() : 0;
    }

    public boolean hasKey(String var1) {
        return this.map.containsKey(var1);
    }

    public boolean hasKeyOfType(String var1, int var2) {
        byte var3 = this.b(var1);
        if (var3 == var2) {
            return true;
        } else if (var2 != 99) {
            if (var3 > 0) {
            }

            return false;
        } else {
            return var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4 || var3 == 5 || var3 == 6;
        }
    }

    public byte getByte(String var1) {
        try {
            return !this.hasKeyOfType(var1, 99) ? 0 : ((NBTNumber)this.map.get(var1)).f();
        } catch (ClassCastException var3) {
            return 0;
        }
    }

    public short getShort(String var1) {
        try {
            return !this.hasKeyOfType(var1, 99) ? 0 : ((NBTNumber)this.map.get(var1)).e();
        } catch (ClassCastException var3) {
            return 0;
        }
    }

    public int getInt(String var1) {
        try {
            return !this.hasKeyOfType(var1, 99) ? 0 : ((NBTNumber)this.map.get(var1)).d();
        } catch (ClassCastException var3) {
            return 0;
        }
    }

    public long getLong(String var1) {
        try {
            return !this.hasKeyOfType(var1, 99) ? 0L : ((NBTNumber)this.map.get(var1)).c();
        } catch (ClassCastException var3) {
            return 0L;
        }
    }

    public float getFloat(String var1) {
        try {
            return !this.hasKeyOfType(var1, 99) ? 0.0F : ((NBTNumber)this.map.get(var1)).h();
        } catch (ClassCastException var3) {
            return 0.0F;
        }
    }

    public double getDouble(String var1) {
        try {
            return !this.hasKeyOfType(var1, 99) ? 0.0D : ((NBTNumber)this.map.get(var1)).g();
        } catch (ClassCastException var3) {
            return 0.0D;
        }
    }

    public String getString(String var1) {
        try {
            return !this.hasKeyOfType(var1, 8) ? "" : ((NBTBase)this.map.get(var1)).a_();
        } catch (ClassCastException var3) {
            return "";
        }
    }

    public byte[] getByteArray(String var1) {
        try {
            return !this.hasKeyOfType(var1, 7) ? new byte[0] : ((NBTTagByteArray)this.map.get(var1)).c();
        } catch (ClassCastException var3) {
            throw new ReportedException(this.a(var1, 7, var3));
        }
    }

    public int[] getIntArray(String var1) {
        try {
            return !this.hasKeyOfType(var1, 11) ? Constants.EMPTY_ARRAY : ((NBTTagIntArray)this.map.get(var1)).c();
        } catch (ClassCastException var3) {
            throw new ReportedException(this.a(var1, 11, var3));
        }
    }

    public NBTTagCompound getCompound(String var1) {
        try {
            return !this.hasKeyOfType(var1, 10) ? new NBTTagCompound() : (NBTTagCompound)this.map.get(var1);
        } catch (ClassCastException var3) {
            throw new ReportedException(this.a(var1, 10, var3));
        }
    }

    public NBTTagList getList(String var1, int var2) {
        try {
            if (this.b(var1) != 9) {
                return new NBTTagList();
            } else {
                NBTTagList var3 = (NBTTagList)this.map.get(var1);
                return var3.size() > 0 && var3.f() != var2 ? new NBTTagList() : var3;
            }
        } catch (ClassCastException var4) {
            throw new ReportedException(this.a(var1, 9, var4));
        }
    }

    public boolean getBoolean(String var1) {
        return this.getByte(var1) != 0;
    }

    public void remove(String var1) {
        this.map.remove(var1);
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder("{");

        Entry var3;
        for(Iterator var2 = this.map.entrySet().iterator(); var2.hasNext(); var1.append((String)var3.getKey()).append(':').append(var3.getValue())) {
            var3 = (Entry)var2.next();
            if (var1.length() != 1) {
                var1.append(',');
            }
        }

        return var1.append('}').toString();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    private CrashReport a(final String var1, final int var2, ClassCastException var3) {
        CrashReport var4 = CrashReport.a(var3, "Reading NBT data");
        CrashReportSystemDetails var5 = var4.a("Corrupt NBT tag", 1);
        var5.a("Tag type found", new Callable<String>() {
            public String call() throws Exception {
                return NBTBase.a[((NBTBase)NBTTagCompound.this.map.get(var1)).getTypeId()];
            }
        });
        var5.a("Tag type expected", new Callable<String>() {
            public String call() throws Exception {
                return NBTBase.a[var2];
            }
        });
        var5.a("Tag name", var1);
        return var4;
    }

    public NBTBase clone() {
        it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<String, NBTBase> ret = new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(this.map.size(), 0.8f);
        Iterator<Map.Entry<String, NBTBase>> iterator = (this.map instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap) ? ((it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap)this.map).object2ObjectEntrySet().fastIterator() : this.map.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, NBTBase> entry = iterator.next();
            ret.put(entry.getKey(), entry.getValue().clone());
        }
        return new NBTTagCompound(ret);
    }

    public boolean equals(Object var1) {
        if (super.equals(var1)) {
            NBTTagCompound var2 = (NBTTagCompound)var1;
            return this.map.entrySet().equals(var2.map.entrySet());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.map.hashCode();
    }

    private static void a(String var0, NBTBase var1, DataOutput var2) throws IOException {
        var2.writeByte(var1.getTypeId());
        if (var1.getTypeId() != 0) {
            var2.writeUTF(var0);
            var1.write(var2);
        }
    }

    private static byte a(DataInput var0, NBTReadLimiter var1) throws IOException {
        return var0.readByte();
    }

    private static String b(DataInput var0, NBTReadLimiter var1) throws IOException {
        return var0.readUTF();
    }

    static NBTBase a(byte var0, String var1, DataInput var2, int var3, NBTReadLimiter var4) throws IOException {
        NBTBase var5 = NBTBase.createTag(var0);

        try {
            var5.load(var2, var3, var4);
            return var5;
        } catch (IOException var9) {
            CrashReport var7 = CrashReport.a(var9, "Loading NBT data");
            CrashReportSystemDetails var8 = var7.a("NBT Tag");
            var8.a("Tag name", var1);
            var8.a("Tag type", var0);
            throw new ReportedException(var7);
        }
    }

    public void a(NBTTagCompound var1) {
        Iterator var2 = var1.map.keySet().iterator();

        while(var2.hasNext()) {
            String var3 = (String)var2.next();
            NBTBase var4 = (NBTBase)var1.map.get(var3);
            if (var4.getTypeId() == 10) {
                if (this.hasKeyOfType(var3, 10)) {
                    NBTTagCompound var5 = this.getCompound(var3);
                    var5.a((NBTTagCompound)var4);
                } else {
                    this.set(var3, var4.clone());
                }
            } else {
                this.set(var3, var4.clone());
            }
        }

    }
}
