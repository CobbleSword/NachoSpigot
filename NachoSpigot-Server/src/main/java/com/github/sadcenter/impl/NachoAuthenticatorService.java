package com.github.sadcenter.impl;

import com.github.sadcenter.core.NachoAuthenticator;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class NachoAuthenticatorService extends NachoAuthenticator {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GameProfile.class, new NachoAuthenticatorService.GameProfileSerializer())
            .create();

    @Override
    public <T> CompletableFuture<T> get(String url, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            String result = null;
            try {
                result = super.fetchGet(url(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return GSON.fromJson(result, type);
        });
    }

    @Override
    public <T> CompletableFuture<T> post(String url, Object content, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return GSON.fromJson(super.fetchPost(url(url), GSON.toJson(content)), type);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    public static URL url(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String json(Map<?, ?> map) {
        return GSON.toJson(map);
    }

    public static String query(Map<?, ?> map) {
        StringBuilder builder = new StringBuilder();
        AtomicBoolean firstPair = new AtomicBoolean(true);

        map.forEach((a, b) -> {
            builder.append((firstPair.get() ? "?" : "&") + a + "=" + b);

            firstPair.set(false);
        });

        return builder.toString();
    }

    public static class GameProfileSerializer implements JsonDeserializer<GameProfile> {
        @Override
        public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            UUID id = jsonObject.has("uuid") ? UUID.fromString(jsonObject.getAsJsonPrimitive("uuid").getAsString()) : null;
            String name = jsonObject.has("username") ? jsonObject.getAsJsonPrimitive("username").getAsString() : null;

            GameProfile gameProfile = new GameProfile(id, name);

            if (jsonObject.has("textures")) {
                JsonObject propertyJson = jsonObject.get("textures").getAsJsonObject().get("raw").getAsJsonObject();
                Property property = new Property("textures", propertyJson.getAsJsonPrimitive("value").getAsString());
                gameProfile.getProperties().put("textures", property);
            }

            return gameProfile;
        }
    }
}
