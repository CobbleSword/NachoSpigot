package com.github.sadcenter.impl.profile;

import com.github.sadcenter.impl.payload.AshconPayloadTexture;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

public class UserProfile {

    private final UUID uuid;
    private final String username;
    @SerializedName("textures")
    private AshconPayloadTexture property;

    public UserProfile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public AshconPayloadTexture getProperty() {
        return property;
    }

    public GameProfile toGameProfile() {
        GameProfile gameProfile = new GameProfile(uuid, username);
        if(property != null ) {
            gameProfile.getProperties()
                    .put("textures", new Property("textures", property.getProperty().getValue(), property.getProperty().getSignature()));
        }
        return gameProfile;
    }
}
