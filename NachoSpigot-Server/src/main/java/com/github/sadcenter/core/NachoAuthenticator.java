package com.github.sadcenter.core;

import com.avaje.ebean.validation.NotNull;
import com.github.sadcenter.impl.storage.CachedProfile;
import com.github.sadcenter.impl.storage.ProfileCache;
import com.github.sadcenter.impl.utils.ProfileUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class NachoAuthenticator implements AsyncHttpAuthenticator {

    public static final String API = "https://api.ashcon.app/mojang/v2/user/";
    public static final String UUID_API = "https://api.ashcon.app/mojang/v2/uuid/";
    public static final Gson GSON = new Gson();

    private final LoadingCache<String, CompletableFuture<GameProfile>> gameProfileCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build(new CacheLoader<String, CompletableFuture<GameProfile>>() {
                @Override
                public CompletableFuture<GameProfile> load(String key) {
                    CompletableFuture<GameProfile> gameProfile = get(API + key, GameProfile.class);
                    CachedProfile cachedProfile = profileCache.getCachedProfile(key);

                    if(cachedProfile == null) {
                        gameProfile
                                .thenApply(CachedProfile::fromGameProfile)
                                .thenAccept(profile -> profileCache.put(key, profile));
                    } else {
                        GameProfile profile = cachedProfile.toGameProfile(key);
                        CompletableFuture.supplyAsync(() -> profile).thenAcceptAsync(value -> {
                            GameProfile join = gameProfile.join();

                            if(!ProfileUtil.equals(profile, join)) {
                                profileCache.put(key, CachedProfile.fromGameProfile(join));
                                profileCache.save();
                            }
                        });

                        return CompletableFuture.completedFuture(profile);
                    }

                    return gameProfile;
                }
            });
    private final LoadingCache<String, CompletableFuture<UUID>> uuidCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build(new CacheLoader<String, CompletableFuture<UUID>>() {
                @Override
                public CompletableFuture<UUID> load(String key) {
                    return get(UUID_API + key, UUID.class);
                }
            });
    private final ProfileCache profileCache = new ProfileCache();

    public CompletableFuture<GameProfile> getProfile(String name) {
        try {
            return gameProfileCache.get(name);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GameProfile getPresentProfile(String name) {
        CompletableFuture<GameProfile> ifPresent = gameProfileCache.getIfPresent(name);

        return ifPresent == null ? null : ifPresent.join();
    }

    public CompletableFuture<UUID> getUuid(String name) {
        try {
            return uuidCache.get(name);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public CompletableFuture<Property> fetchTextures(String name) {
        return get(API + name, JsonElement.class).thenApply(json ->
                GSON.fromJson(json.getAsJsonObject().getAsJsonObject("textures").getAsJsonObject("raw"), Property.class));
    }

    @Override
    public String fetchGet(URL url) throws IOException {
        return IOUtils.toString(url, StandardCharsets.UTF_8);
    }

    @Override
    public String fetchPost(URL url, String content) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            IOUtils.write(content.getBytes(Charsets.UTF_8), outputStream);
        }

        try (InputStream inputStream = connection.getInputStream()) {
            return IOUtils.toString(inputStream);
        }
    }

    public static @NotNull URL url(String url) {
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
}
