package net.techcable.tacospigot.function;

@FunctionalInterface
public interface ObjIntFunction<T, R> {
    public R apply(T t, int i);
}
