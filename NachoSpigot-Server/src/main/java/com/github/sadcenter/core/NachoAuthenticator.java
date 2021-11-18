package com.github.sadcenter.core;

import com.github.sadcenter.impl.storage.CachedProfile;
import com.github.sadcenter.impl.storage.ProfileCache;
import com.github.sadcenter.impl.utils.ProfileUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class NachoAuthenticator implements AsyncHttpAuthenticator {

    private static final String API = "https://api.ashcon.app/mojang/v2/user/";
    private static final String UUID_API = "https://api.ashcon.app/mojang/v2/uuid/";

    protected final LoadingCache<String, CompletableFuture<GameProfile>> gameProfileCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.HOURS)
            .build(new CacheLoader<String, CompletableFuture<GameProfile>>() {
                @Override
                public CompletableFuture<GameProfile> load(String key) {
                    EntityPlayer player = MinecraftServer.getServer().getPlayerList().getPlayer(key);
                    CompletableFuture<GameProfile> gameProfile = player == null ? get(API + key, GameProfile.class) : CompletableFuture.completedFuture(player.getProfile());
                    CachedProfile cachedProfile = profileCache.getCachedProfile(key);

                    if (cachedProfile == null) {
                        gameProfile
                                .thenAccept(profile -> profileCache.putAndSave(profile.getName(), CachedProfile.fromGameProfile(profile)));
                    } else {
                        GameProfile profile = cachedProfile.toGameProfile(key);
                        CompletableFuture.runAsync(() -> {
                            GameProfile join = gameProfile.join();

                            if (!ProfileUtil.equals(profile, join)) {
                                profileCache.putAndSave(join.getName(), CachedProfile.fromGameProfile(join));
                            }
                        }, ProfileCache.EXECUTOR);

                        return CompletableFuture.completedFuture(profile);
                    }

                    return gameProfile;
                }
            });

    private final ProfileCache profileCache = new ProfileCache();

    public CompletableFuture<GameProfile> getProfile(String name) {
        try {
            return this.gameProfileCache.get(name);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GameProfile getPresentProfile(String name) {
        CompletableFuture<GameProfile> profile = this.gameProfileCache.getIfPresent(name);
        return profile == null ? null : profile.join();
    }

    public CompletableFuture<UUID> getUuid(String name) {
        return get(UUID_API + name, UUID.class);
    }

    public String fetchGet(URL url) throws IOException {
        return IOUtils.toString(url, StandardCharsets.UTF_8);
    }

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

    public ProfileCache getProfileCache() {
        return this.profileCache;
    }
}
