package com.github.sadcenter.impl;

import com.github.sadcenter.core.NachoAuthenticator;
import com.github.sadcenter.impl.repo.NachoGameProfileRepository;
import com.github.sadcenter.impl.session.NachoSessionService;
import com.google.gson.*;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import java.io.FileNotFoundException;
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
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
            .create();

    private final YggdrasilAuthenticationService yggdrasilAuthenticationService;

    public NachoAuthenticatorService(YggdrasilAuthenticationService yggdrasilAuthenticationService) {
        this.yggdrasilAuthenticationService = yggdrasilAuthenticationService;
    }

    @Override
    public GameProfileRepository createProfileRepository() {
        return new NachoGameProfileRepository(this);
    }

    @Override
    public UserAuthentication createUserAuthentication(Agent agent) {
        return this.yggdrasilAuthenticationService.createUserAuthentication(agent);
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return new NachoSessionService(this);
    }

    @Override
    public <T> CompletableFuture<T> get(String url, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            String result = null;
            try {
                result = super.fetchGet(url(url));
            } catch (IOException exception) {
                if (!(exception instanceof FileNotFoundException)) {
                    exception.printStackTrace();
                }
            }
            return GSON.fromJson(result, type);
        });
    }

    @Override
    public <T> CompletableFuture<T> post(String url, Object content, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return GSON.fromJson(super.fetchPost(url(url), GSON.toJson(content)), type);
            } catch (IOException exception) {
                if (!(exception instanceof FileNotFoundException)) {
                    exception.printStackTrace();
                }
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
