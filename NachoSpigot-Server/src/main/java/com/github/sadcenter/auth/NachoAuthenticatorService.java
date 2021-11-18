package com.github.sadcenter.auth;

import com.github.sadcenter.auth.repo.NachoGameProfileRepository;
import com.github.sadcenter.auth.serializer.GameProfileSerializer;
import com.github.sadcenter.auth.session.NachoSessionService;
import com.github.sadcenter.auth.storage.CachedProfile;
import com.github.sadcenter.auth.storage.ProfileCache;
import com.github.sadcenter.auth.utils.ProfileUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.*;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NachoAuthenticatorService implements AuthenticationService {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GameProfile.class, new GameProfileSerializer())
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
            .create();
    private static final String API = "https://api.ashcon.app/mojang/v2/user/";
    private static final String UUID_API = "https://api.ashcon.app/mojang/v2/uuid/";

    private final LoadingCache<String, CompletableFuture<GameProfile>> gameProfileCache = CacheBuilder.newBuilder()
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

    public <T> CompletableFuture<T> get(String url, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            String result = null;
            try {
                result = this.fetchGet(url(url));
            } catch (IOException exception) {
                if (!(exception instanceof FileNotFoundException)) {
                    exception.printStackTrace();
                }
            }
            return GSON.fromJson(result, type);
        });
    }

    public <T> CompletableFuture<T> post(String url, Object content, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return GSON.fromJson(this.fetchPost(url(url), GSON.toJson(content)), type);
            } catch (IOException exception) {
                if (!(exception instanceof FileNotFoundException)) {
                    exception.printStackTrace();
                }
            }

            return null;
        });
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

    public static URL url(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
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
