package com.github.sadcenter.auth.session;

import com.github.sadcenter.auth.NachoAuthenticationService;
import com.github.sadcenter.auth.response.HasJoinedServerResponse;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class NachoSessionService implements MinecraftSessionService {

    private static final String JOIN_URL = "https://sessionserver.mojang.com/session/minecraft/join";
    private static final String JOINED_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined";

    private final NachoAuthenticationService authenticator;

    public NachoSessionService(NachoAuthenticationService authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void joinServer(GameProfile gameProfile, String authToken, String serverId) {
        Map<String, Object> request = new HashMap<String, Object>() {{
            put("accessToken", authToken);
            put("selectedProfile", gameProfile.getId());
            put("serverId", serverId);
        }};

        try {
            this.authenticator.fetchPost(NachoAuthenticationService.url(JOIN_URL), NachoAuthenticationService.json(request));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error loading profile " + gameProfile.getName() + " (UUID: " + gameProfile.getId() + ")", e);
        }
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile gameProfile, String serverId) {
        Map<String, Object> request = new HashMap<String, Object>() {{
            put("username", gameProfile.getName());
            put("serverId", serverId);
        }};

        return this.authenticator.get(JOINED_URL + NachoAuthenticationService.query(request), HasJoinedServerResponse.class).thenApply(response -> {
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
        return Collections.emptyMap(); //not used at all?
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile gameProfile, boolean secure) {
        this.authenticator.getProfile(gameProfile.getName()).thenAccept(profile -> {
            Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);

            if (property != null) {
                gameProfile.getProperties()
                        .put("textures", property);
            }
        }); //this might give more "steve" skin delay at the expense of better performance (not locking thread)

        return gameProfile;
    }
}
