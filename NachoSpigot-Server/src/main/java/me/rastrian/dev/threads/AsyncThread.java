package me.rastrian.dev.threads;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.elier.nachospigot.config.NachoConfig;
import me.rastrian.dev.threads.netty.Spigot404Write;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.openhft.affinity.AffinityLock;
import net.openhft.affinity.AffinityStrategies;

public abstract class AsyncThread {
    private boolean running = true;
    private int TICK_TIME = 1000000000 / NachoConfig.threadTPS;
    private Thread t;
    protected Queue<Runnable> packets = new ConcurrentLinkedQueue<Runnable>();

    public AsyncThread(String s) {
        try (final AffinityLock al = AffinityLock.acquireLock();){
            System.out.println("Lock found.");
            this.t = new Thread(new Runnable(){

                @Override
                public void run() {
                    try (AffinityLock al2 = al.acquireLock(AffinityStrategies.SAME_SOCKET, AffinityStrategies.ANY);){
                        System.out.println("Thread " + AsyncThread.this.t.getId() + " locked");
                        AsyncThread.this.loop();
                    }
                }
            });
            this.t.start();
        }
    }

    public void loop() {
        long lastTick = System.nanoTime();
        long catchupTime = 0L;
        while (this.running) {
            long curTime = System.nanoTime();
            long wait = (long)this.TICK_TIME - (curTime - lastTick) - catchupTime;
            if (wait > 0L) {
                try {
                    Thread.sleep(wait / 1000000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catchupTime = 0L;
                continue;
            }
            catchupTime = Math.min(1000000000L, Math.abs(wait));
            this.run();
            lastTick = curTime;
        }
    }

    public abstract void run();

    public void addPacket(final Packet<?>  packet, final NetworkManager manager, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
        this.packets.add(new Runnable(){

            @Override
            public void run() {
                Spigot404Write.writeThenFlush(manager.channel, packet, agenericfuturelistener);
            }
        });
    }

    public Thread getThread() {
        return this.t;
    }

    public static class RunnableItem {
        private Channel channel;
        private Packet<?>  packet;

        public RunnableItem(Channel m, Packet<?>  p) {
            this.channel = m;
            this.packet = p;
        }

        public Packet<?> getPacket() {
            return this.packet;
        }

        public Channel getChannel() {
            return this.channel;
        }
    }
}