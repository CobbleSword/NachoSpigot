package com.github.sadcenter.impl.storage;

import com.github.sadcenter.core.NachoAuthenticator;
import com.google.common.reflect.TypeToken;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ProfileCache {

    private static final File CACHE_FILE = new File("texturecache.json");
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private final Map<String, CachedProfile> cachedProfiles;

    public ProfileCache() {
        this.cachedProfiles = this.load();
        this.save();
    }

    private Map<String, CachedProfile> load() {
        if(!CACHE_FILE.exists()) {
            try {
                CACHE_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else try(FileReader fileReader = new FileReader(CACHE_FILE)) {
            Map<String, CachedProfile> loadedCache = NachoAuthenticator.GSON
                    .fromJson(fileReader, new TypeToken<Map<String, CachedProfile>>() {}.getType());

            return this.filter(loadedCache);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    private Map<String, CachedProfile> filter(Map<String, CachedProfile> map) {
        return map.entrySet().stream().filter(cachedProfile -> !cachedProfile.getValue().isExpired()).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));
    }

    public void save() {
        if(this.cachedProfiles.isEmpty()) {
            return;
        }

        EXECUTOR.execute(() -> {
            try (FileWriter fileWriter = new FileWriter(CACHE_FILE)) {
                NachoAuthenticator.GSON.toJson(this.cachedProfiles, fileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public CachedProfile put(String name, CachedProfile cachedProfile) {
        return this.cachedProfiles.put(name, cachedProfile);
    }

    public CachedProfile getCachedProfile(String name) {
        CachedProfile cachedProfile = this.cachedProfiles.get(name);
        if(cachedProfile != null) {
            cachedProfile.refreshExpire();
        }
        return cachedProfile;
    }
}
