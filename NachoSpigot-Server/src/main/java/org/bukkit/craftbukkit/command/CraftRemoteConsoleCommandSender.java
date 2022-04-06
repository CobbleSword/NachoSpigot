package org.bukkit.craftbukkit.command;

import net.minecraft.server.ChatComponentText;
import net.minecraft.server.RemoteControlCommandListener;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class CraftRemoteConsoleCommandSender extends ServerCommandSender implements RemoteConsoleCommandSender {
    public CraftRemoteConsoleCommandSender() {
        super();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        RemoteControlCommandListener.getInstance().sendMessage(new ChatComponentText(message + "\n")); // Send a newline after each message, to preserve formatting.
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public String getName() {
        return "Rcon";
    }

    @Override
    public boolean isOp() {
        return true;
    }

    // Paper start
    @Override
    public net.kyori.adventure.text.@NotNull Component name() {
        return net.kyori.adventure.text.Component.text(this.getName());
    }
    // Paper end

    @Override
    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of remote controller.");
    }
}
