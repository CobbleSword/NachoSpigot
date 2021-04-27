package me.suicidalkids.ion.blocks.redstone;

import java.lang.reflect.Field;

/*
 * Decompiled from https://www.spigotmc.org/resources/pandawire-1-8-8-1-15-2.41991/
 */
public class ReflectUtil {

    public static <T> T getOfT(Object obj, Class<T> type) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (type.equals(field.getType())) {
                return get(obj, field, type);
            }
        }

        return null;
    }

    public static <T> T get(Object obj, Field field, Class<T> type) {
        try {
            field.setAccessible(true);
            return type.cast(field.get(obj));
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}