package io.papermc.paper.adventure;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.server.ChatModifier;
import net.minecraft.server.IChatBaseComponent;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

public class AdventureComponent implements IChatBaseComponent {
    final Component adventure;
    private @MonotonicNonNull IChatBaseComponent vanilla;

    public AdventureComponent(final Component adventure) {
        this.adventure = adventure;
    }

    public IChatBaseComponent deepConverted() {
        IChatBaseComponent vanilla = this.vanilla;
        if (vanilla == null) {
            vanilla = PaperAdventure.WRAPPER_AWARE_SERIALIZER.serialize(this.adventure);
            this.vanilla = vanilla;
        }
        return vanilla;
    }

    public @Nullable IChatBaseComponent deepConvertedIfPresent() {
        return this.vanilla;
    }

    @Override
    public ChatModifier getChatModifier() {
        return this.deepConverted().getChatModifier();
    }

    @Override
    public String getText() {
        if (this.adventure instanceof TextComponent) {
            return ((TextComponent) this.adventure).content();
        } else {
            return this.deepConverted().getText();
        }
    }

    @Override
    public String getString() {
        return PaperAdventure.PLAIN.serialize(this.adventure);
    }

    @Override
    public List<IChatBaseComponent> getSiblings() {
        return this.deepConverted().getSiblings();
    }

    @Override
    public IChatBaseComponent copy() {
        return this.deepConverted().copy();
    }

    // Nacho start - fix conflicts
    @Override
    public IChatBaseComponent addSibling(String s) {
        return this.deepConverted().addSibling(s);
    }

    @Override
    public IChatBaseComponent addSibling(IChatBaseComponent ichatbasecomponent) {
        return this.deepConverted().addSibling(ichatbasecomponent);
    }

    @Override
    public IChatBaseComponent setChatModifier(ChatModifier chatmodifier) {
        return this.deepConverted().setChatModifier(chatmodifier);
    }

    public static class Serializer implements JsonSerializer<AdventureComponent> {
        @Override
        public JsonElement serialize(final AdventureComponent src, final Type type, final JsonSerializationContext context) {
            return PaperAdventure.GSON.serializer().toJsonTree(src.adventure, Component.class);
        }
     }
}