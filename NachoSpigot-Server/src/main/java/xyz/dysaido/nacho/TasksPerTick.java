package xyz.dysaido.nacho;

public class TasksPerTick implements Runnable {
    private final int tick;
    private final Runnable task;

    public TasksPerTick(int creationTicks, Runnable task) {
        this.tick = creationTicks;
        this.task = task;
    }

    public int getTick() {
        return tick;
    }

    @Override
    public void run() {
        task.run();
    }
}
