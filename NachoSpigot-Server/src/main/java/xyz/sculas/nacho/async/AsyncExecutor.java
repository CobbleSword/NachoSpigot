package xyz.sculas.nacho.async;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AsyncExecutor {
    public static ThreadPoolExecutor EXPLOSIONS_EXECUTOR;
    public static ThreadPoolExecutor TRACKER_EXECUTOR;

    public static void initExplosions(boolean fixed, int size) {
        if (fixed)
            EXPLOSIONS_EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(size);
        else
            EXPLOSIONS_EXECUTOR = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public static void initTracker() {
        TRACKER_EXECUTOR = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public static void stopExecutor() {
        if (EXPLOSIONS_EXECUTOR != null) EXPLOSIONS_EXECUTOR.shutdown();
        if (TRACKER_EXECUTOR != null) TRACKER_EXECUTOR.shutdown();
    }
}