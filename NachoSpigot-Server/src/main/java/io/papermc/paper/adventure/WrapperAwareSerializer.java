package io.papermc.paper.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.server.IChatBaseComponent;
import org.jetbrains.annotations.NotNull;

final class WrapperAwareSerializer implements ComponentSerializer<Component, Component, IChatBaseComponent> {
    @Override
    public @NotNull Component deserialize(final @NotNull IChatBaseComponent input) {
        if (input instanceof AdventureComponent) {
            return ((AdventureComponent) input).adventure;
        }
        return PaperAdventure.GSON.serializer().fromJson(IChatBaseComponent.ChatSerializer.toJsonTree(input), Component.class);
    }

    @Override
    public @NotNull IChatBaseComponent serialize(final @NotNull Component component) {
        return IChatBaseComponent.ChatSerializer.fromJson(PaperAdventure.GSON.serializer().toJsonTree(component));
    }
}