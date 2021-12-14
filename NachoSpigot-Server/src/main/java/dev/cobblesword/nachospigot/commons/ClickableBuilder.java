package dev.cobblesword.nachospigot.commons;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author SaithTime
 */
public class ClickableBuilder {
    private final TextComponent textComponent;

    public ClickableBuilder(String message) {
        this.textComponent = new TextComponent(message);
    }

    public ClickableBuilder setHover(String hover) {
        this.textComponent.setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create())
        );
        return this;
    }

    public ClickableBuilder setClick(String command, ClickEvent.Action mode) {
        this.textComponent.setClickEvent(new ClickEvent(mode, command));
        return this;
    }

    public TextComponent build() {
        return this.textComponent;
    }
}