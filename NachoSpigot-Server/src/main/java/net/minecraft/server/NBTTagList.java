package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTBase {

    private static final Logger b = LogManager.getLogger();
    private List<NBTBase> list = Lists.newArrayList();
    private byte type = 0;

    public NBTTagList() {}

    void write(DataOutput dataoutput) throws IOException {
        if (!this.list.isEmpty()) {
            this.type = ((NBTBase) this.list.get(0)).getTypeId();
        } else {
            this.type = 0;
        }

        dataoutput.writeByte(this.type);
        dataoutput.writeInt(this.list.size());

        for (int i = 0; i < this.list.size(); ++i) {
            ((NBTBase) this.list.get(i)).write(dataoutput);
        }

    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(296L);
        if (i > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.type = datainput.readByte();
            int j = datainput.readInt();
            nbtreadlimiter.a(j * 8); // CraftBukkit

            if (this.type == 0 && j > 0) {
                throw new RuntimeException("Missing type on ListTag");
            } else {
                nbtreadlimiter.a(32L * (long) j);
                this.list = Lists.newArrayListWithCapacity(j);

                for (int k = 0; k < j; ++k) {
                    NBTBase nbtbase = NBTBase.createTag(this.type);

                    nbtbase.load(datainput, i + 1, nbtreadlimiter);
                    this.list.add(nbtbase);
                }

            }
        }
    }

    public byte getTypeId() {
        return (byte) 9;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[");

        for (int i = 0; i < this.list.size(); ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(i).append(':').append(this.list.get(i));
        }

        return stringbuilder.append(']').toString();
    }

    public void add(NBTBase nbtbase) {
        if (nbtbase.getTypeId() == 0) {
            NBTTagList.b.warn("Invalid TagEnd added to ListTag");
        } else {
            if (this.type == 0) {
                this.type = nbtbase.getTypeId();
            } else if (this.type != nbtbase.getTypeId()) {
                NBTTagList.b.warn("Adding mismatching tag types to tag list");
                return;
            }

            this.list.add(nbtbase);
        }
    }

    public void a(int i, NBTBase nbtbase) {
        if (nbtbase.getTypeId() == 0) {
            NBTTagList.b.warn("Invalid TagEnd added to ListTag");
        } else if (i >= 0 && i < this.list.size()) {
            if (this.type == 0) {
                this.type = nbtbase.getTypeId();
            } else if (this.type != nbtbase.getTypeId()) {
                NBTTagList.b.warn("Adding mismatching tag types to tag list");
                return;
            }

            this.list.set(i, nbtbase);
        } else {
            NBTTagList.b.warn("index out of bounds to set tag in tag list");
        }
    }

    public NBTBase a(int i) {
        return (NBTBase) this.list.remove(i);
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public NBTTagCompound get(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            return nbtbase.getTypeId() == 10 ? (NBTTagCompound) nbtbase : new NBTTagCompound();
        } else {
            return new NBTTagCompound();
        }
    }

    public int[] c(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            return nbtbase.getTypeId() == 11 ? ((NBTTagIntArray) nbtbase).c() : new int[0];
        } else {
            return new int[0];
        }
    }

    public double d(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            return nbtbase.getTypeId() == 6 ? ((NBTTagDouble) nbtbase).g() : 0.0D;
        } else {
            return 0.0D;
        }
    }

    public float e(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            return nbtbase.getTypeId() == 5 ? ((NBTTagFloat) nbtbase).h() : 0.0F;
        } else {
            return 0.0F;
        }
    }

    public String getString(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            return nbtbase.getTypeId() == 8 ? nbtbase.a_() : nbtbase.toString();
        } else {
            return "";
        }
    }

    public NBTBase g(int i) {
        return (NBTBase) (i >= 0 && i < this.list.size() ? (NBTBase) this.list.get(i) : new NBTTagEnd());
    }

    public int size() {
        return this.list.size();
    }

    public NBTBase clone() {
        NBTTagList nbttaglist = new NBTTagList();

        nbttaglist.type = this.type;
        Iterator iterator = this.list.iterator();

        while (iterator.hasNext()) {
            NBTBase nbtbase = (NBTBase) iterator.next();
            NBTBase nbtbase1 = nbtbase.clone();

            nbttaglist.list.add(nbtbase1);
        }

        return nbttaglist;
    }

    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagList nbttaglist = (NBTTagList) object;

            if (this.type == nbttaglist.type) {
                return this.list.equals(nbttaglist.list);
            }
        }

        return false;
    }

    public int hashCode() {
        return super.hashCode() ^ this.list.hashCode();
    }

    public int f() {
        return this.type;
    }
}
