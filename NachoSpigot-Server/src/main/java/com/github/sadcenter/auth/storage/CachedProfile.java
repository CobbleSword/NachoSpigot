package com.github.sadcenter.auth.storage;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.concurrent.TimeUnit;

public class CachedProfile {

    private static final long CACHE_TIME = TimeUnit.DAYS.toMillis(1);

    private final String texture;
    private long expiresOn;

    public CachedProfile(String property) {
        this.texture = property;
        this.refreshExpire();
    }

    public void refreshExpire() {
        this.expiresOn = System.currentTimeMillis() + CACHE_TIME;
    }

    public boolean isExpired() {
        return this.expiresOn < System.currentTimeMillis();
    }

    public GameProfile toGameProfile(String name) {
        GameProfile gameProfile = new GameProfile(null, name);
        gameProfile.getProperties().put("textures", new Property("textures", this.texture));
        return gameProfile;
    }

    public static CachedProfile fromGameProfile(GameProfile gameProfile) {
        return new CachedProfile(Iterables.getFirst(gameProfile.getProperties().get("textures"), null).getValue());
    }
}
