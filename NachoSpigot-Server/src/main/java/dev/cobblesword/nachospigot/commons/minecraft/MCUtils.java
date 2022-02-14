package dev.cobblesword.nachospigot.commons.minecraft;

import net.minecraft.server.LocaleLanguage;
import net.minecraft.server.MinecraftKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NBTTagString;

import java.lang.reflect.Field;

public final class MCUtils {
    private MCUtils() {}

    public static void ensureMain(Runnable runnable) {
        MinecraftServer.getServer().processQueue.add(runnable);
    }

    public static LocaleLanguage getLanguage() {
        try {
            for (Field field: LocaleLanguage.class.getDeclaredFields()) {
                if(LocaleLanguage.class.isAssignableFrom(field.getType())) return (LocaleLanguage) field.get(null);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get Language instance", e);
        }
        throw new RuntimeException(new NoSuchFieldException("Failed to get Language instance"));
    }

    public static MinecraftKey newKey(String namespace, String value) {
        try {
            return MinecraftKey.class.getDeclaredConstructor(int.class, String[].class).newInstance(0, new String[] { namespace, value });
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create new MinecraftKey", e);
        }
    }

    private static final NBTTagString EMPTY_STRING_TAG = new NBTTagString("");

    public static NBTTagString valueOf(String value) {
        return value.isEmpty() ? EMPTY_STRING_TAG : new NBTTagString(value);
    }
}
