package dev.cobblesword.nachospigot.commons;

public class ClassUtils {

    public static boolean exists(String className, ClassLoader loader) {
        try {
            Class.forName(className, true, loader);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

}
