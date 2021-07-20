package net.minecraft.server;

import java.util.function.Supplier;

public class LazyInitVar<T> {
    private T a;
    private boolean b = false;
    private final Supplier<T> supplier;

    public LazyInitVar(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T c() {
        if (!this.b) {
            this.b = true;
            this.a = this.supplier.get();
        }
        return this.a;
    }
}