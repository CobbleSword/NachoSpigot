package com.github.sadcenter.auth;

import com.github.sadcenter.auth.profile.NachoGameProfileRepository;
import com.github.sadcenter.auth.serializer.GameProfileSerializer;
import com.github.sadcenter.auth.serializer.UUIDSerializer;
import com.github.sadcenter.auth.session.NachoSessionService;
import com.github.sadcenter.auth.storage.CachedProfile;
import com.github.sadcenter.auth.storage.ProfileCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.*;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import me.elier.nachospigot.config.NachoConfig;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NachoAuthenticationService implements AuthenticationService {

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GameProfile.class, new GameProfileSerializer())
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .create();
    public static final Executor EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("Authenticator Thread - %1$d")
            .setUncaughtExceptionHandler((t, e) -> e.printStackTrace())
            .build());
    private static final String API = "https://api.ashcon.app/mojang/v2/user/";
    private static final String BACKUP_API = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String UUID_API = "https://api.ashcon.app/mojang/v2/uuid/";
    private static final String BACKUP_UUID_API = "https://api.mojang.com/users/profiles/minecraft/";

    private final LoadingCache<String, CompletableFuture<GameProfile>> gameProfileCache = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.HOURS)
            .maximumSize(5000) //idk what's size is the best so you can give some advices
            .build(new CacheLoader<String, CompletableFuture<GameProfile>>() {
                @Override
                public CompletableFuture<GameProfile> load(String key) {
                    EntityPlayer player = MinecraftServer.getServer().getPlayerList().getPlayer(key);
                    CachedProfile cachedProfile = profileCache.getCachedProfile(key);
                    CompletableFuture<GameProfile> gameProfile = player == null ? getProfileFromApi(key) : CompletableFuture.completedFuture(player.getProfile());

                    if (cachedProfile == null) {
                        gameProfile
                                .thenAccept(profile -> profileCache.putAndSave(profile.getName(), CachedProfile.fromGameProfile(profile)));
                    } else {
                        GameProfile cachedGameProfile = cachedProfile.toGameProfile(key);
                        gameProfile.thenAccept(profile -> {
                            if (!profile.equals(cachedGameProfile)) {
                                profileCache.putAndSave(profile.getName(), CachedProfile.fromGameProfile(profile));
                                gameProfileCache.put(profile.getName(), gameProfile);
                            }
                        });

                        return CompletableFuture.completedFuture(cachedGameProfile);
                    }

                    return gameProfile;
                }
            });
    private final ProfileCache profileCache = new ProfileCache();
    private final YggdrasilAuthenticationService yggdrasilAuthenticationService;

    public NachoAuthenticationService(YggdrasilAuthenticationService yggdrasilAuthenticationService) {
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
        return profile == null ? null :
                profile.getNow(null);
    }

    public CompletableFuture<UUID> getUuid(String name) {
        return this.getUuidFromApi(name, NachoConfig.alwaysUseMojang).exceptionallyCompose(throwable -> this.getUuidFromApi(name, !NachoConfig.alwaysUseMojang));
    }

    private CompletableFuture<GameProfile> getProfileFromApi(String name) {
        return this.getProfileFromApi(name, NachoConfig.alwaysUseMojang)
                .exceptionallyComposeAsync(throwable -> this.getProfileFromApi(name, !NachoConfig.alwaysUseMojang), EXECUTOR);
    }

    private CompletableFuture<UUID> getUuidFromApi(String name, boolean mojang) {
        return mojang ? this.get(BACKUP_UUID_API + name, UUID.class) :
                this.get(UUID_API + name, UUID.class);
    }

    private CompletableFuture<GameProfile> getProfileFromApi(String name, boolean mojang) {
        if (mojang) {
            CompletableFuture<UUID> uuidFuture = this.getUuidFromApi(name, true);
            return uuidFuture
                    .thenCompose(uuid -> this.get(BACKUP_API + UUIDTypeAdapter.fromUUID(uuid), GameProfile.class));
        } else {
            return this.get(API + name, GameProfile.class);
        }
    }

    public <T> CompletableFuture<T> get(String url, Class<T> type) {
        return CompletableFuture.supplyAsync(() -> {
            String result = null;
            try {
                result = this.fetchGet(url(url));
            } catch (Exception exception) {
                if (!(exception instanceof FileNotFoundException)) {
                    throw new CompletionException(exception);
                }
            }
            return GSON.fromJson(result, type);
        }, EXECUTOR);
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
        }, EXECUTOR);
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

    public void tick() {
        this.profileCache.setTicked(false);
    }

    public static URL url(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String json(T source) {
        return GSON.toJson(source);
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
