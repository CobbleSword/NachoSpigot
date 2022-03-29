package xyz.dysaido.nacho;

public abstract class ReentrantIAsyncHandler<R extends Runnable> extends IAsyncHandler<R> {

    private int count;

    public ReentrantIAsyncHandler(String name) {
        super(name);
    }

    @Override
    protected boolean executables() {
        return this.runningTask() || super.executables();
    }

    protected boolean runningTask() {
        return this.count != 0;
    }

    @Override
    protected void doRunnable(R task) {
        ++this.count;
        try {
            super.doRunnable(task);
        } finally {
            --this.count;
        }
    }
}
