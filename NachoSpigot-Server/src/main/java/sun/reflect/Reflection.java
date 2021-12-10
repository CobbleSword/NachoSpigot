package sun.reflect;

public class Reflection {
    public static Class<?> getCallerClass(int n) {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        return elements[n].getClass();
    }
}
