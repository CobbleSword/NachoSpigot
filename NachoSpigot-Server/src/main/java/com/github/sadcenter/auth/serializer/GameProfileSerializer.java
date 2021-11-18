package com.github.sadcenter.auth.serializer;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Type;
import java.util.UUID;

public class GameProfileSerializer implements JsonDeserializer<GameProfile> {

    @Override
    public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        UUID id = jsonObject.has("uuid") ? UUID.fromString(jsonObject.getAsJsonPrimitive("uuid").getAsString()) : null;
        String name = jsonObject.has("username") ? jsonObject.getAsJsonPrimitive("username").getAsString() : null;

        GameProfile gameProfile = new GameProfile(id, name);

        if (jsonObject.has("textures")) {
            JsonObject propertyJson = jsonObject.get("textures").getAsJsonObject().get("raw").getAsJsonObject();
            Property property = new Property("textures", propertyJson.getAsJsonPrimitive("value").getAsString());
            gameProfile.getProperties().put("textures", property);
        }

        return gameProfile;
    }
}