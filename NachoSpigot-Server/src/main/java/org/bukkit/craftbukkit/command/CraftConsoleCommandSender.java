package org.bukkit.craftbukkit.command;

import dev.cobblesword.nachospigot.Nacho;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.conversations.ConversationTracker;
import org.jetbrains.annotations.NotNull;

/**
 * Represents CLI input from a console
 */
public class CraftConsoleCommandSender extends ServerCommandSender implements ConsoleCommandSender {

    protected final ConversationTracker conversationTracker = new ConversationTracker();

    protected CraftConsoleCommandSender() {
        super();
    }

    public void sendMessage(@NotNull String message) {
        sendRawMessage(message);
    }

    public void sendRawMessage(String message) {
        Nacho.LOGGER.info(ChatColor.stripColor(message));
    }

    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public String getName() {
        return "CONSOLE";
    }

    // Paper start
    @Override
    public net.kyori.adventure.text.@NotNull Component name() {
        return net.kyori.adventure.text.Component.text(this.getName());
    }
    // Paper end

    public boolean isOp() {
        return true;
    }

    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of server console");
    }

    public boolean beginConversation(Conversation conversation) {
        return conversationTracker.beginConversation(conversation);
    }

    public void abandonConversation(Conversation conversation) {
        conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
    }

    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        conversationTracker.abandonConversation(conversation, details);
    }

    public void acceptConversationInput(String input) {
        conversationTracker.acceptConversationInput(input);
    }

    public boolean isConversing() {
        return conversationTracker.isConversing();
    }

    // Paper start
    @Override
    public void sendMessage(final net.kyori.adventure.identity.@NotNull Identity identity, final net.kyori.adventure.text.@NotNull Component message, final net.kyori.adventure.audience.@NotNull MessageType type) {
        this.sendRawMessage(io.papermc.paper.adventure.PaperAdventure.LEGACY_SECTION_UXRC.serialize(message));
    }
    // Paper end
}
