package com.github.sadcenter.auth.response;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;

import java.util.UUID;

public class HasJoinedServerResponse {

    private final String id;
    private final PropertyMap properties;

    public HasJoinedServerResponse(String uuid, PropertyMap propertyMap) {
        this.id = uuid;
        this.properties = propertyMap;
    }

    public UUID getUuid() {
        return UUIDTypeAdapter.fromString(this.id);
    }

    public PropertyMap getPropertyMap() {
        return this.properties;
    }
}
