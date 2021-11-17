package com.github.sadcenter.impl;

import com.github.sadcenter.core.AsyncHttpAuthenticator;
import com.github.sadcenter.core.NachoAuthenticator;
import com.github.sadcenter.impl.repo.NachoGameProfileRepository;
import com.github.sadcenter.impl.session.NachoSessionService;
import com.google.gson.*;
import com.mojang.authlib.*;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.util.UUIDTypeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NachoAuthenticatorService extends NachoAuthenticator {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(GameProfile.class, new GameProfileSerializer())
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer())
            .create();

    @Override
    public <T> CompletableFuture<T> get(String url, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            String result = null;
            try {
                result = super.fetchGet(NachoAuthenticator.url(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return gson.fromJson(result, type);
        });
    }

    @Override
    public <T> CompletableFuture<T> post(String url, Object content, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return gson.fromJson(super.fetchPost(NachoAuthenticator.url(url), gson.toJson(content)), type);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        });
    }

    private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile>
    {
        public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            UUID id = jsonObject.has("uuid") ? UUID.fromString(jsonObject.getAsJsonPrimitive("uuid").getAsString()) : null;
            String name = jsonObject.has("username") ? jsonObject.getAsJsonPrimitive("username").getAsString() : null;

            GameProfile gameProfile = new GameProfile(id, name);

            if (jsonObject.has("textures")) {
                JsonObject propertyJson = jsonObject.get("textures").getAsJsonObject().get("raw").getAsJsonObject();

                Property property = new Property("textures", propertyJson.getAsJsonPrimitive("value").getAsString(),
                        propertyJson.getAsJsonPrimitive("signature").getAsString());

                gameProfile.getProperties().put("textures", property);
            }

            return gameProfile;
        }

        public JsonElement serialize(final GameProfile src, final Type typeOfSrc, final JsonSerializationContext context) {
            final JsonObject result = new JsonObject();
            if (src.getId() != null) {
                result.add("id", context.serialize((Object)src.getId()));
            }
            if (src.getName() != null) {
                result.addProperty("name", src.getName());
            }

            return result;
        }
    }
}
