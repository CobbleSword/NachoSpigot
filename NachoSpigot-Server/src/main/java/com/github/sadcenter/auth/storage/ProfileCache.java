package com.github.sadcenter.auth.storage;

import com.github.sadcenter.auth.NachoAuthenticationService;
import com.google.common.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.spigotmc.CaseInsensitiveMap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ProfileCache {

    private static final File CACHE_FILE = new File("texturecache.json");

    private final Map<String, CachedProfile> cachedProfiles;
    private boolean ticked;

    public ProfileCache() {
        this.cachedProfiles = this.load();
        this.save();
    }

    private Map<String, CachedProfile> load() {
        if (!CACHE_FILE.exists()) {
            try {
                CACHE_FILE.createNewFile();
                FileUtils.writeStringToFile(CACHE_FILE, "{}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else try (BufferedReader fileReader = new BufferedReader(new FileReader(CACHE_FILE))) {
            Map<String, CachedProfile> loadedCache = NachoAuthenticationService.GSON
                    .fromJson(fileReader, new TypeToken<HashMap<String, CachedProfile>>() {
                    }.getType());
            return this.filter(loadedCache);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while loading profile caches", e);
        }

        return new CaseInsensitiveMap<>();
    }

    private Map<String, CachedProfile> filter(Map<String, CachedProfile> map) {
        return new CaseInsensitiveMap<>(map.entrySet().stream().filter(cachedProfile -> !cachedProfile.getValue().isExpired()).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        )));
    }

    public void save() {
        this.save(true);
    }

    public void save(boolean runAsync) {
        if (this.isTicked() || this.cachedProfiles.isEmpty()) {
            return;
        }

        Runnable runnable = () -> {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(CACHE_FILE))) {
                NachoAuthenticationService.GSON.toJson(this.cachedProfiles, fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        this.setTicked(true);
        MinecraftServer.getServer().processQueue.add(runAsync ? () -> NachoAuthenticationService.EXECUTOR.execute(runnable) : runnable);
    }

    public void put(String name, CachedProfile cachedProfile) {
        this.cachedProfiles.put(name, cachedProfile);
    }

    public void putAndSave(String name, CachedProfile cachedProfile) {
        this.put(name, cachedProfile);
        this.save();
    }

    public boolean remove(String name) {
        return this.cachedProfiles.remove(name) != null;
    }

    public CachedProfile getCachedProfile(String name) {
        CachedProfile cachedProfile = this.cachedProfiles.get(name);
        if (cachedProfile != null) {
            cachedProfile.refreshExpire();
        }
        return cachedProfile;
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    public boolean isTicked() {
        return this.ticked;
    }
}
