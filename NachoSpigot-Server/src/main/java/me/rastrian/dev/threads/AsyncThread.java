package me.rastrian.dev.threads;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import me.elier.nachospigot.config.NachoConfig;
import me.rastrian.dev.threads.netty.Spigot404Write;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.openhft.affinity.AffinityLock;
import net.openhft.affinity.AffinityStrategies;

public abstract class AsyncThread {
    private static final long SEC_IN_NANO = 1000000000;
    private static final int TPS = NachoConfig.combatThreadTPS;
    private static final long TICK_TIME = SEC_IN_NANO / TPS;
    
    private int currentTick = 0;
    private final RollingAverage tps1 = new RollingAverage(60);
    private final RollingAverage tps5 = new RollingAverage(300);
    private final RollingAverage tps15 = new RollingAverage(900);

    protected Queue<Runnable> packets = new ConcurrentLinkedQueue<Runnable>();
    private boolean running = true;
    private Thread t;

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
            long wait = TICK_TIME - (curTime - lastTick) - catchupTime;
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
            if ( ++this.currentTick % TPS == 0 )
            {
                final long diff = curTime - lastTick;
                double currentTps = 1E9 / diff * TPS;
                tps1.add(currentTps, diff);
                tps5.add(currentTps, diff);
                tps15.add(currentTps, diff);
            }
            this.run();
            lastTick = curTime;
        }
    }

    public abstract void run();

    public double[] getTPS() {
        return new double[] {
            this.tps1.getAverage(),
            this.tps5.getAverage(),
            this.tps15.getAverage()
        };
    }

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

    public static class RollingAverage {
        private final int size;
        private long time;
        private double total;
        private int index = 0;
        private final double[] samples;
        private final long[] times;

        RollingAverage(int size) {
            this.size = size;
            this.time = size * SEC_IN_NANO;
            this.total = TPS * SEC_IN_NANO * size;
            this.samples = new double[size];
            this.times = new long[size];
            for (int i = 0; i < size; i++) {
                this.samples[i] = TPS;
                this.times[i] = SEC_IN_NANO;
            }
        }

        public void add(double x, long t) {
            time -= times[index];
            total -= samples[index] * times[index];
            samples[index] = x;
            times[index] = t;
            time += t;
            total += x * t;
            if (++index == size) {
                index = 0;
            }
        }

        public double getAverage() {
            return total / time;
        }
    }
}