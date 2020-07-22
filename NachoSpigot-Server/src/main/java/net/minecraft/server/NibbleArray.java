package net.minecraft.server;

public class NibbleArray {

    private final byte[] a;
    private final boolean dummy;

    public NibbleArray()
    {
        this.a = new byte[2048];
        this.dummy = false;
    }

    public NibbleArray(boolean isDummy)
    {
        this.a = new byte[2048];
        this.dummy = true;
    }

    public NibbleArray(byte[] abyte) {
        this.a = abyte;
        if (abyte.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + abyte.length);
        }
        this.dummy = false;
    }

    public int a(int i, int j, int k) {
        return this.a(this.b(i, j, k));
    }

    public void a(int i, int j, int k, int l) {
        this.a(this.b(i, j, k), l);
    }

    private int b(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    public int a(int i) {
        int j = this.c(i);

        return this.a[j] >> ((i & 1) << 2) & 15; // Spigot
    }

    public void a(int i, int j) {
        int k = this.c(i);

//        if (this.b(i)) {
//            this.a[k] = (byte) (this.a[k] & 240 | j & 15);
//        } else {
//            this.a[k] = (byte) (this.a[k] & 15 | (j & 15) << 4);
//        }

        // Spigot start
        int shift = (i & 1) << 2;
        this.a[k] = (byte) (this.a[k] & ~(15 << shift) | (j & 15) << shift);
        // Spigot end

    }

    private boolean b(int i) {
        return (i & 1) == 0;
    }

    private int c(int i) {
        return i >> 1;
    }

    public byte[] a() {
        return this.a;
    }

    public byte[] getBytes() { return this.a; }

    public boolean isDummy() {
        return dummy;
    }
}
