package net.minecraft.server;

import java.util.function.Supplier;

public class LazyInitVar<T> {
    private T value;
    private boolean cached = false;
    private final Supplier<T> supplier;

    public LazyInitVar(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (!this.cached) {
            this.cached = true;
            this.value = this.supplier.get();
        }
        return this.value;
    }
}