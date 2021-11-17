package me.rastrian.dev.threads;

public class HitDetection extends AsyncThread {
    public HitDetection() {
        super("Async Hit Detection");
    }

    @Override
    public void run() {
        while (this.packets.size() > 0) {
            (this.packets.poll()).run();
        }
    }
}