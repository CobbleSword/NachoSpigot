package com.github.sadcenter.auth.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.util.UUIDTypeAdapter;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDSerializer implements JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            return UUIDTypeAdapter.fromString(json.getAsJsonObject().get("id").getAsString());
        } else {
            return UUID.fromString(json.getAsString());
        }
    }
}
