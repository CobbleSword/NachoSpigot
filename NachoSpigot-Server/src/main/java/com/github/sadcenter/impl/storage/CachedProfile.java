package com.github.sadcenter.impl.storage;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CachedProfile {

    public static final long CACHE_TIME = TimeUnit.DAYS.toMillis(1);

    private final UUID uuid;
    private final String texture;
    private long expiresOn = System.currentTimeMillis() + CACHE_TIME;

    public CachedProfile(UUID uuid, String property) {
        this.uuid = uuid;
        this.texture = property;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getTexture() {
        return texture;
    }

    public void refreshExpire() {
        this.expiresOn += CACHE_TIME;
    }

    public boolean isExpired() {
        return this.expiresOn < System.currentTimeMillis();
    }

    public GameProfile toGameProfile(String name) {
        GameProfile gameProfile = new GameProfile(this.uuid, name);
        gameProfile.getProperties().put("textures", new Property("textures", texture));
        return gameProfile;
    }

    public static CachedProfile fromGameProfile(GameProfile gameProfile) {
        return new CachedProfile(gameProfile.getId(), Iterables.getFirst(gameProfile.getProperties().get("textures"), null).getValue());
    }

}
