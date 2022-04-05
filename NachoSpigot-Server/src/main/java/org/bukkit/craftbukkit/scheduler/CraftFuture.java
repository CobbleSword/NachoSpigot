package org.bukkit.craftbukkit.scheduler;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.*;

class CraftFuture<T> extends CraftTask implements Future<T> {

    private final Callable<T> callable;
    private T value;
    private Exception exception = null;

    CraftFuture(final Callable<T> callable, final Plugin plugin, final int id) {
        super(plugin, null, id, -1L);
        this.callable = callable;
    }

    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (getPeriod() != -1L) {
            return false;
        }
        setPeriod(-2L);
        return true;
    }

    public boolean isCancelled() {
        return getPeriod() == -2L;
    }

    public boolean isDone() {
        final long period = this.getPeriod();
        return period != -1L && period != -3L;
    }

    public T get() throws CancellationException, InterruptedException, ExecutionException {
        try {
            return get(0, TimeUnit.MILLISECONDS);
        } catch (final TimeoutException e) {
            throw new Error(e);
        }
    }

    public synchronized T get(long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        timeout = unit.toMillis(timeout);
        long period = this.getPeriod();
        long timestamp = timeout > 0 ? System.currentTimeMillis() : 0L;
        while (true) {
            if (period == -1L || period == -3L) {
                this.wait(timeout);
                period = this.getPeriod();
                if (period == -1L || period == -3L) {
                    if (timeout == 0L) {
                        continue;
                    }
                    timeout += timestamp - (timestamp = System.currentTimeMillis());
                    if (timeout > 0) {
                        continue;
                    }
                    throw new TimeoutException();
                }
            }
            if (period == -2L) {
                throw new CancellationException();
            }
            if (period == -4L) {
                if (exception == null) {
                    return value;
                }
                throw new ExecutionException(exception);
            }
            throw new IllegalStateException("Expected " + -1L + " to " + -4L + ", got " + period);
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (getPeriod() == -2L) {
                return;
            }
            setPeriod(-3L);
        }
        try {
            value = callable.call();
        } catch (final Exception e) {
            exception = e;
        } finally {
            synchronized (this) {
                setPeriod(-4L);
                this.notifyAll();
            }
        }
    }

    synchronized boolean cancel0() {
        if (getPeriod() != -1L) {
            return false;
        }
        setPeriod(-2L);
        notifyAll();
        return true;
    }
}
