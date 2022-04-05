package org.bukkit.craftbukkit.conversations;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;

import java.util.LinkedList;
import java.util.logging.Level;

/**
 */
public class ConversationTracker {

    private LinkedList<Conversation> conversationQueue = new LinkedList<>();

    public synchronized boolean beginConversation(Conversation conversation) {
        if (!conversationQueue.contains(conversation)) {
            conversationQueue.addLast(conversation);
            if (conversationQueue.getFirst() == conversation) {
                conversation.begin();
                conversation.outputNextPrompt();
                return true;
            }
        }
        return true;
    }

    public synchronized void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        if (!conversationQueue.isEmpty()) {
            if (conversationQueue.getFirst() == conversation) {
                conversation.abandon(details);
            }
            conversationQueue.remove(conversation);
            if (!conversationQueue.isEmpty()) {
                conversationQueue.getFirst().outputNextPrompt();
            }
        }
    }

    public synchronized void abandonAllConversations() {

        LinkedList<Conversation> oldQueue = conversationQueue;
        conversationQueue = new LinkedList<>();
        for (Conversation conversation : oldQueue) {
            try {
                conversation.abandon(new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
            } catch (Throwable t) {
                Bukkit.getLogger().log(Level.SEVERE, "Unexpected exception while abandoning a conversation", t);
            }
        }
    }

    public synchronized void acceptConversationInput(String input) {
        if (isConversing()) {
            conversationQueue.getFirst().acceptInput(input);
        }
    }

    public synchronized boolean isConversing() {
        return !conversationQueue.isEmpty();
    }

    public synchronized boolean isConversingModaly() {
        return isConversing() && conversationQueue.getFirst().isModal();
    }
}
