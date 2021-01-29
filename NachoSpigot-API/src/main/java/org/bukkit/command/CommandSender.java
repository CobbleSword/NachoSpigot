package org.bukkit.command;

import org.bukkit.Server;
import org.bukkit.permissions.Permissible;

public interface CommandSender extends net.kyori.adventure.audience.Audience, Permissible { // Paper

    /**
     * Sends this sender a message
     *
     * @param message Message to be displayed
     * @see #sendMessage(net.kyori.adventure.text.Component)
     */
    public void sendMessage(String message);

    /**
     * Sends this sender multiple messages
     *
     * @param messages An array of messages to be displayed
     * @see #sendMessage(net.kyori.adventure.text.Component)
     */
    public void sendMessage(String[] messages);

    /**
     * Returns the server instance that this command is running on
     *
<<<<<<< found
     * @return Server instance
||||||| expected
     * @param message Message to be displayed
     * @param sender The sender of this message
     */
    public void sendMessage(@Nullable UUID sender, @NotNull String message);

=======
     * @param message Message to be displayed
     * @param sender The sender of this message
     * @see #sendMessage(net.kyori.adventure.identity.Identified, net.kyori.adventure.text.Component)
     */
    public void sendMessage(@Nullable UUID sender, @NotNull String message);

>>>>>>> replacement
<<<<<<< found
||||||| expected
     *
     * @param messages An array of messages to be displayed
     * @param sender The sender of this message
=======
     *
     * @param messages An array of messages to be displayed
     * @param sender The sender of this message
     * @see #sendMessage(net.kyori.adventure.identity.Identified, net.kyori.adventure.text.Component)
>>>>>>> replacement
     */
    public Server getServer();

    /**
     * Gets the name of this command sender
     *
     * @return Name of the sender
     */
    public String getName();

    // Paper start
    /**
     * Sends the component to the sender
     *
     * <p>If this sender does not support sending full components then
     * the component will be sent as legacy text.</p>
     *
     * @param component the component to send
     */
    default void sendMessage(net.md_5.bungee.api.chat.BaseComponent component) {
        this.sendMessage(component.toLegacyText());
    }

    /**
     * Sends an array of components as a single message to the sender
     *
     * <p>If this sender does not support sending full components then
     * the components will be sent as legacy text.</p>
     *
     * @param components the components to send
     */
    default void sendMessage(net.md_5.bungee.api.chat.BaseComponent... components) {
        this.sendMessage(new net.md_5.bungee.api.chat.TextComponent(components).toLegacyText());
    }
    // Paper end
<<<<<<< found
||||||| expected
         * Sends this sender a chat component.
         *
         * @param component the components to send
         */
        public void sendMessage(@NotNull net.md_5.bungee.api.chat.BaseComponent component) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         * Sends this sender a chat component.
         *
         * @param component the components to send
         * @deprecated use {@code sendMessage} methods that accept {@link net.kyori.adventure.text.Component}
         */
        @Deprecated // Paper
        public void sendMessage(@NotNull net.md_5.bungee.api.chat.BaseComponent component) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
         * Sends an array of components as a single message to the sender.
         *
         * @param components the components to send
         */
        public void sendMessage(@NotNull net.md_5.bungee.api.chat.BaseComponent... components) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         * Sends an array of components as a single message to the sender.
         *
         * @param components the components to send
         * @deprecated use {@code sendMessage} methods that accept {@link net.kyori.adventure.text.Component}
         */
        @Deprecated // Paper
        public void sendMessage(@NotNull net.md_5.bungee.api.chat.BaseComponent... components) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
         *
         * @param component the components to send
         * @param sender the sender of the message
         */
        public void sendMessage(@Nullable UUID sender, @NotNull net.md_5.bungee.api.chat.BaseComponent component) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         *
         * @param component the components to send
         * @param sender the sender of the message
         * @deprecated use {@code sendMessage} methods that accept {@link net.kyori.adventure.text.Component}
         */
        @Deprecated // Paper
        public void sendMessage(@Nullable UUID sender, @NotNull net.md_5.bungee.api.chat.BaseComponent component) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
         *
         * @param components the components to send
         * @param sender the sender of the message
         */
        public void sendMessage(@Nullable UUID sender, @NotNull net.md_5.bungee.api.chat.BaseComponent... components) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
=======
         *
         * @param components the components to send
         * @param sender the sender of the message
         * @deprecated use {@code sendMessage} methods that accept {@link net.kyori.adventure.text.Component}
         */
        @Deprecated // Paper
        public void sendMessage(@Nullable UUID sender, @NotNull net.md_5.bungee.api.chat.BaseComponent... components) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
>>>>>>> replacement
<<<<<<< found
||||||| expected
    @NotNull
    Spigot spigot();
    // Spigot end
=======
    @NotNull
    Spigot spigot();
    // Spigot end

    // Paper start
    /**
     * Gets the name of this command sender
     *
     * @return Name of the sender
     */
    public @NotNull net.kyori.adventure.text.Component name();

    @Override
    default void sendMessage(final @NotNull net.kyori.adventure.identity.Identity identity, final @NotNull net.kyori.adventure.text.Component message, final @NotNull net.kyori.adventure.audience.MessageType type) {
        this.sendMessage(org.bukkit.Bukkit.getUnsafe().legacyComponentSerializer().serialize(message));
    }
    // Paper end
>>>>>>> replacement
}
