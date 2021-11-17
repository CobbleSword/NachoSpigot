package me.rastrian.dev.threads;

public class Knockback extends AsyncThread {
    public Knockback() {
        super("Async Knockback Thread");
    }

    @Override
    public void run() {
        while (this.packets.size() > 0) {
            (this.packets.poll()).run();
        }
    }
}