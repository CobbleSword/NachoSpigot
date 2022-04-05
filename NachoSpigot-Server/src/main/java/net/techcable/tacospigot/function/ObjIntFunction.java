package net.techcable.tacospigot.function;

@FunctionalInterface
public interface ObjIntFunction<T, R> {
    R apply(T t, int i);
}
