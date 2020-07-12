package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

public class FileIOThread implements Runnable {

    private static final FileIOThread a = new FileIOThread();
    private List<IAsyncChunkSaver> b = Collections.synchronizedList(Lists.<IAsyncChunkSaver>newArrayList());
    private volatile long c;
    private volatile long d;
    private volatile boolean e;

    private FileIOThread() {
        Thread thread = new Thread(this, "File IO Thread");

        thread.setPriority(1);
        thread.start();
    }

    public static FileIOThread a() {
        return FileIOThread.a;
    }

    public void run() {
        while (true) {
            this.c();
        }
    }

    private void c() {
        for (int i = 0; i < this.b.size(); ++i) {
            IAsyncChunkSaver iasyncchunksaver = (IAsyncChunkSaver) this.b.get(i);
            boolean flag = iasyncchunksaver.c();

            if (!flag) {
                this.b.remove(i--);
                ++this.d;
            }

            /* // Spigot start - don't sleep in between chunks so we unload faster.
            try {
                Thread.sleep(this.e ? 0L : 10L);
            } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            } */ // Spigot end
        }

        if (this.b.isEmpty()) {
            try {
                Thread.sleep(25L);
            } catch (InterruptedException interruptedexception1) {
                interruptedexception1.printStackTrace();
            }
        }

    }

    public void a(IAsyncChunkSaver iasyncchunksaver) {
        if (!this.b.contains(iasyncchunksaver)) {
            ++this.c;
            this.b.add(iasyncchunksaver);
        }
    }

    public void b() throws InterruptedException {
        this.e = true;

        while (this.c != this.d) {
            Thread.sleep(10L);
        }

        this.e = false;
    }
}
