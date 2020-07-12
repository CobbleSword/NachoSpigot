package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {

    private int[] data;

    NBTTagIntArray() {}

    public NBTTagIntArray(int[] aint) {
        this.data = aint;
    }

    void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.data.length);

        for (int i = 0; i < this.data.length; ++i) {
            dataoutput.writeInt(this.data[i]);
        }

    }

    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(192L);
        int j = datainput.readInt();
       com.google.common.base.Preconditions.checkArgument( j < 1 << 24);

        nbtreadlimiter.a((long) (32 * j));
        this.data = new int[j];

        for (int k = 0; k < j; ++k) {
            this.data[k] = datainput.readInt();
        }

    }

    public byte getTypeId() {
        return (byte) 11;
    }

    public String toString() {
        String s = "[";
        int[] aint = this.data;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint[j];

            s = s + k + ",";
        }

        return s + "]";
    }

    public NBTBase clone() {
        int[] aint = new int[this.data.length];

        System.arraycopy(this.data, 0, aint, 0, this.data.length);
        return new NBTTagIntArray(aint);
    }

    public boolean equals(Object object) {
        return super.equals(object) ? Arrays.equals(this.data, ((NBTTagIntArray) object).data) : false;
    }

    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.data);
    }

    public int[] c() {
        return this.data;
    }
}
