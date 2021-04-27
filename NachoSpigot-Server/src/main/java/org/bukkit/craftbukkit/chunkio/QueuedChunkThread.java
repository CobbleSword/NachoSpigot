package org.bukkit.craftbukkit.chunkio;

import java.util.concurrent.LinkedBlockingDeque;

public class QueuedChunkThread implements Runnable {
    public LinkedBlockingDeque<QueuedChunkPacket> chunks = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        while (true) {
            this.emptyQueue();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
    }

    private void emptyQueue() {
        while (true) {
            QueuedChunkPacket packet = this.chunks.poll();
            if (packet == null) break;
            packet.run();
        }
    }
}