package io.papermc.paper.adventure;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.util.Codec;
import net.minecraft.server.MojangsonParseException;
import net.minecraft.server.MojangsonParser;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

@SuppressWarnings({"PatternValidation", "deprecation", "NullableProblems"})
final class NBTLegacyHoverEventSerializer implements LegacyHoverEventSerializer {
    public static final NBTLegacyHoverEventSerializer INSTANCE = new NBTLegacyHoverEventSerializer();
    private static final Codec<NBTTagCompound, String, MojangsonParseException, RuntimeException> SNBT_CODEC = Codec.of(MojangsonParser::parse, NBTBase::toString);

    static final String ITEM_TYPE = "id";
    static final String ITEM_COUNT = "Count";
    static final String ITEM_TAG = "tag";

    static final String ENTITY_NAME = "name";
    static final String ENTITY_TYPE = "type";
    static final String ENTITY_ID = "id";

    NBTLegacyHoverEventSerializer() {}

    @Override
    public HoverEvent.@NotNull ShowItem deserializeShowItem(final @NotNull Component input) throws IOException {
        final String raw = PlainComponentSerializer.plain().serialize(input);

        try {
            final NBTTagCompound contents = SNBT_CODEC.decode(raw);
            final NBTTagCompound tag = contents.getCompound(ITEM_TAG);
            return HoverEvent.ShowItem.of(
                Key.key(contents.getString(ITEM_TYPE)),
                contents.hasKey(ITEM_COUNT) ? contents.getByte(ITEM_COUNT) : 1,
                tag.isEmpty() ? null : BinaryTagHolder.encode(tag, SNBT_CODEC)
            );
        } catch (final MojangsonParseException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public HoverEvent.@NotNull ShowEntity deserializeShowEntity(final @NotNull Component input, final Codec.Decoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
        final String raw = PlainComponentSerializer.plain().serialize(input);
        try {
            final NBTTagCompound contents = SNBT_CODEC.decode(raw);
            return HoverEvent.ShowEntity.of(
                Key.key(contents.getString(ENTITY_TYPE)),
                UUID.fromString(contents.getString(ENTITY_ID)),
                componentCodec.decode(contents.getString(ENTITY_NAME))
            );
        } catch (final MojangsonParseException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public @NotNull Component serializeShowItem(final HoverEvent.ShowItem input) throws IOException {
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString(ITEM_TYPE, input.item().asString());
        tag.setByte(ITEM_COUNT, (byte) input.count());
        if (input.nbt() != null) {
            try {
                tag.set(ITEM_TAG, input.nbt().get(SNBT_CODEC));
            } catch (final MojangsonParseException ex) {
                throw new IOException(ex);
            }
        }
        return Component.text(SNBT_CODEC.encode(tag));
    }

    @Override
    public @NotNull Component serializeShowEntity(final HoverEvent.ShowEntity input, final Codec.Encoder<Component, String, ? extends RuntimeException> componentCodec) {
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString(ENTITY_ID, input.id().toString());
        tag.setString(ENTITY_TYPE, input.type().asString());
        if (input.name() != null) {
            tag.setString(ENTITY_NAME, componentCodec.encode(input.name()));
        }
        return Component.text(SNBT_CODEC.encode(tag));
    }
}