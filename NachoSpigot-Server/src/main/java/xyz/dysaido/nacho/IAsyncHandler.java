package xyz.dysaido.nacho;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public abstract class IAsyncHandler<R extends Runnable> implements Executor {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String name;
    private final Queue<R> pendingRunnables = new ConcurrentLinkedQueue<>();
    private int terminateCount;

    protected IAsyncHandler(String name) {
        this.name = name;
    }

    protected abstract R packUpRunnable(Runnable runnable);

    protected abstract boolean shouldRun(R task);

    public boolean isMainThread() {
        return Thread.currentThread() == this.getMainThread();
    }

    protected abstract Thread getMainThread();

    protected boolean executables() {
        return !this.isMainThread();
    }

    public int getPendingRunnables() {
        return this.pendingRunnables.size();
    }

    public String getName() {
        return this.name;
    }

    public <V> CompletableFuture<V> submit(Supplier<V> task) {
        return this.executables() ? CompletableFuture.supplyAsync(task, this) : CompletableFuture.completedFuture(task.get());
    }

    private CompletableFuture<Void> submitAsync(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> submit(Runnable task) {
        if (this.executables()) {
            return this.submitAsync(task);
        } else {
            task.run();
            return CompletableFuture.completedFuture(null);
        }
    }

    public void performBlocking(Runnable runnable) {
        if (!this.isMainThread()) {
            this.submitAsync(runnable).join();
        } else {
            runnable.run();
        }

    }

    public void call(R runnable) {
        this.pendingRunnables.add(runnable);
        LockSupport.unpark(this.getMainThread());
    }

    @Override
    public void execute(Runnable runnable) {
        if (this.executables()) {
            this.call(this.packUpRunnable(runnable));
        } else {
            runnable.run();
        }

    }

    protected void clearAllRunnable() {
        this.pendingRunnables.clear();
    }

    public void runAllRunnable() {
        while(this.drawRunnable()) {
        }
    }

    public boolean drawRunnable() {
        R runnable = this.pendingRunnables.peek();
        if (runnable == null) {
            return false;
        } else if (this.terminateCount == 0 && !this.shouldRun(runnable)) {
            return false;
        } else {
            this.doRunnable(this.pendingRunnables.remove());
            return true;
        }
    }

    public void controlTerminate(BooleanSupplier stopCondition) {
        ++this.terminateCount;

        try {
            while(!stopCondition.getAsBoolean()) {
                if (!this.drawRunnable()) {
                    this.waitForRuns();
                }
            }
        } finally {
            --this.terminateCount;
        }

    }

    protected void waitForRuns() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", 100000L);
    }

    protected void doRunnable(R task) {
        try {
            task.run();
        } catch (Exception e) {
            if (e.getCause() instanceof ThreadDeath) throw e; // Paper
            LOGGER.fatal("Error executing task on {}", this.getName(), e);
        }

    }
}
