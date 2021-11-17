package com.github.sadcenter.impl.payload;

import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.properties.Property;

public class AshconPayloadTexture {

    @SerializedName("raw")
    private final Property property;

    public AshconPayloadTexture(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }
}
