package com.github.sadcenter.auth.session;

import com.github.sadcenter.auth.NachoAuthenticatorService;
import com.github.sadcenter.auth.response.HasJoinedServerResponse;
import com.github.sadcenter.auth.utils.HashMapBuilder;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NachoSessionService implements MinecraftSessionService {

    private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
    private static final String JOINED_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";

    private final NachoAuthenticatorService authenticator;

    public NachoSessionService(NachoAuthenticatorService authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void joinServer(GameProfile gameProfile, String authToken, String serverId) {
        Map<String, Object> request = new HashMapBuilder()
                .withParam("accessToken", authToken)
                .withParam("selectedProfile", gameProfile.getId())
                .withParam("serverId", serverId).getMap();

        try {
            this.authenticator.fetchPost(NachoAuthenticatorService.url(JOIN_URL), NachoAuthenticatorService.json(request));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile gameProfile, String serverId) {
        Map<String, Object> request = new HashMapBuilder()
                .withParam("username", gameProfile.getName())
                .withParam("serverId", serverId).getMap();

        return this.authenticator.get(JOINED_URL + NachoAuthenticatorService.query(request), HasJoinedServerResponse.class).thenApply(response -> {

            if (response == null || response.getUuid() == null) {
                return null;
            }

            GameProfile profile = new GameProfile(response.getUuid(), gameProfile.getName());

            PropertyMap propertyMap = response.getPropertyMap();
            if (propertyMap != null && !propertyMap.isEmpty()) {
                profile.getProperties().putAll(response.getPropertyMap());
            }

            return profile;

        }).join();
    }


    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile, boolean secure) {
        return new HashMap<>();
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile gameProfile, boolean secure) {
        Property property = Iterables.getFirst(this.authenticator.getProfile(gameProfile.getName()).join()
                .getProperties()
                .get("textures"), null);

        if (property != null) {
            gameProfile.getProperties()
                    .put("textures", property);
        }

        return gameProfile;
    }
}
