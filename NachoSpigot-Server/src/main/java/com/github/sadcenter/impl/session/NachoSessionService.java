package com.github.sadcenter.impl.session;

import com.github.sadcenter.core.NachoAuthenticator;
import com.github.sadcenter.impl.NachoAuthenticatorService;
import com.github.sadcenter.impl.response.HasJoinedServerResponse;
import com.github.sadcenter.impl.utils.HashMapBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NachoSessionService implements MinecraftSessionService {

    private static final URL JOIN_URL = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/join");
    private static final URL JOINED_URL = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/hasJoined");
    private static final String TEXTURE = "textures";

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
            authenticator.fetchPost(JOIN_URL, NachoAuthenticator.json(request));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile gameProfile, String serverId) {
        Map<String, Object> request = new HashMapBuilder()
                .withParam("username", gameProfile.getName())
                .withParam("serverId", serverId).getMap();

        String url = JOINED_URL + NachoAuthenticator.query(request);

        return authenticator.get(url, HasJoinedServerResponse.class).thenApply(response -> {

            if (response == null || response.getUuid() == null) {
                return null;
            }

            GameProfile profile = new GameProfile(response.getUuid(), gameProfile.getName());

            PropertyMap propertyMap = response.getPropertyMap();
            if (propertyMap != null && !propertyMap.isEmpty()) {
                profile.getProperties().putAll(response.getPropertyMap());
            }

            return profile;

        }).join(); //ow
    }


    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile, boolean secure) {
        return new HashMap<>();
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile gameProfile, boolean secure) {
        return authenticator.getProfile(gameProfile.getName()).thenApply(profile -> {
            if(profile.getProperties().get(TEXTURE) == null) {
                authenticator.fetchTextures(gameProfile.getName()).thenAccept(texture -> {
                    profile.getProperties().put(TEXTURE, texture);
                });
            }

            return profile;
        }).join();
    }
}
