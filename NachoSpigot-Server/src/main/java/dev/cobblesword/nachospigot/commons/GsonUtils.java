package dev.cobblesword.nachospigot.commons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {
    private static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson GSON = new GsonBuilder().create();

    public static Gson getGsonPretty() {
        return GSON_PRETTY;
    }

    public static Gson getGson() {
        return GSON;
    }
}