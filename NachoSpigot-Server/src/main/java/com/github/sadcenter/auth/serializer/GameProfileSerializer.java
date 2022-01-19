package com.github.sadcenter.auth.serializer;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;

import java.lang.reflect.Type;
import java.util.UUID;

public class GameProfileSerializer implements JsonDeserializer<GameProfile> {

    private static final String ASHCON_UUID_FIELD = "uuid";
    private static final String MOJANG_UUID_FIELD = "id";

    @Override
    public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        boolean isAshCon = jsonObject.has(ASHCON_UUID_FIELD);
        String stringId = jsonObject.getAsJsonPrimitive(isAshCon ? ASHCON_UUID_FIELD : MOJANG_UUID_FIELD).getAsString();

        UUID id = isAshCon ? UUID.fromString(stringId) : UUIDTypeAdapter.fromString(stringId);
        String name = jsonObject.getAsJsonPrimitive(isAshCon ? "username" : "name").getAsString();
        GameProfile gameProfile = new GameProfile(id, name);

        JsonObject propertyJson = (isAshCon ?
                jsonObject.get("textures").getAsJsonObject().get("raw") : jsonObject.getAsJsonArray("properties").get(0))
                .getAsJsonObject();

        Property property = new Property("textures", propertyJson.getAsJsonPrimitive("value").getAsString());
        gameProfile.getProperties().put("textures", property);

        return gameProfile;
    }
}