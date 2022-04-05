package org.bukkit.craftbukkit.command;

import net.minecraft.server.CommandException;
import net.minecraft.server.*;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.Level;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.List;

public final class VanillaCommandWrapper extends VanillaCommand {
    private final CommandAbstract vanillaCommand;

    public VanillaCommandWrapper(CommandAbstract vanillaCommand) {
        super(vanillaCommand.getCommand());
        this.vanillaCommand = vanillaCommand;
    }

    public VanillaCommandWrapper(CommandAbstract vanillaCommand, String usage) {
        super(vanillaCommand.getCommand());
        this.vanillaCommand = vanillaCommand;
        this.description = "A Mojang provided command.";
        this.usageMessage = usage;
        this.setPermission("minecraft.command." + vanillaCommand.getCommand());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;

        ICommandListener icommandlistener = getListener(sender);
        dispatchVanillaCommand(sender, icommandlistener, args);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null); // PaperSpigot - location tab-completes. Original code moved below
    }

    // PaperSpigot start - location tab-completes
    /*
        this code is copied, except for the noted change, from the original tabComplete(CommandSender sender, String alias, String[] args) method
     */
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        if (location == null) { // PaperSpigot use location information if available
            return vanillaCommand.tabComplete(getListener(sender), args, new BlockPosition(0, 0, 0));
        } else {
            return vanillaCommand.tabComplete(getListener(sender), args, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
    }
    // PaperSpigot end

    public static CommandSender lastSender = null; // Nasty :(

    public int dispatchVanillaCommand(CommandSender bSender, ICommandListener icommandlistener, String[] as) {
        // Copied from net.minecraft.server.CommandHandler
        int i = getPlayerListSize(as);
        int j = 0;
        // Some commands use the worldserver variable but we leave it full of null values,
        // so we must temporarily populate it with the world of the commandsender
        WorldServer[] prev = MinecraftServer.getServer().worldServer;
        MinecraftServer server = MinecraftServer.getServer();
        server.worldServer = new WorldServer[server.worlds.size()];
        server.worldServer[0] = (WorldServer) icommandlistener.getWorld();
        int bpos = 0;
        for (int pos = 1; pos < server.worldServer.length; pos++) {
            WorldServer world = server.worlds.get(bpos++);
            if (server.worldServer[0] == world) {
                pos--;
                continue;
            }
            server.worldServer[pos] = world;
        }

        try {
            if (vanillaCommand.canUse(icommandlistener)) {
                if (i > -1) {
                    List<Entity> list = PlayerSelector.getPlayers(icommandlistener, as[i], Entity.class);
                    String s2 = as[i];
                    
                    icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, list.size());

                    for (Entity entity : list) {
                        CommandSender oldSender = lastSender;
                        lastSender = bSender;
                        try {
                            as[i] = entity.getUniqueID().toString();
                            vanillaCommand.execute(icommandlistener, as);
                            j++;
                        } catch (ExceptionUsage exceptionusage) {
                            ChatMessage chatmessage = new ChatMessage("commands.generic.usage", new ChatMessage(exceptionusage.getMessage(), exceptionusage.getArgs()));
                            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
                            icommandlistener.sendMessage(chatmessage);
                        } catch (CommandException commandexception) {
                            CommandAbstract.a(icommandlistener, vanillaCommand, 1, commandexception.getMessage(), commandexception.getArgs());
                        } finally {
                            lastSender = oldSender;
                        }
                    }
                    as[i] = s2;
                } else {
                    icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, 1);
                    vanillaCommand.execute(icommandlistener, as);
                    j++;
                }
            } else {
                ChatMessage chatmessage = new ChatMessage("commands.generic.permission");
                chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
                icommandlistener.sendMessage(chatmessage);
            }
        } catch (ExceptionUsage exceptionusage) {
            ChatMessage chatmessage1 = new ChatMessage("commands.generic.usage", new ChatMessage(exceptionusage.getMessage(), exceptionusage.getArgs()));
            chatmessage1.getChatModifier().setColor(EnumChatFormat.RED);
            icommandlistener.sendMessage(chatmessage1);
        } catch (CommandException commandexception) {
            CommandAbstract.a(icommandlistener, vanillaCommand, 1, commandexception.getMessage(), commandexception.getArgs());
        } catch (Throwable throwable) {
            ChatMessage chatmessage3 = new ChatMessage("commands.generic.exception");
            chatmessage3.getChatModifier().setColor(EnumChatFormat.RED);
            icommandlistener.sendMessage(chatmessage3);
            if (icommandlistener.f() instanceof EntityMinecartCommandBlock) {
                MinecraftServer.LOGGER.log(Level.WARN, String.format("MinecartCommandBlock at (%d,%d,%d) failed to handle command", icommandlistener.getChunkCoordinates().getX(), icommandlistener.getChunkCoordinates().getY(), icommandlistener.getChunkCoordinates().getZ()), throwable);
            } else if(icommandlistener instanceof CommandBlockListenerAbstract) {
                CommandBlockListenerAbstract listener = (CommandBlockListenerAbstract) icommandlistener;
                MinecraftServer.LOGGER.log(Level.WARN, String.format("CommandBlock at (%d,%d,%d) failed to handle command", listener.getChunkCoordinates().getX(), listener.getChunkCoordinates().getY(), listener.getChunkCoordinates().getZ()), throwable);
            } else {
                MinecraftServer.LOGGER.log(Level.WARN, "Unknown CommandBlock failed to handle command", throwable);
            }
        } finally {
            MinecraftServer.getServer().worldServer = prev;
        }
        icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.SUCCESS_COUNT, j);
        return j;
    }

    private ICommandListener getListener(CommandSender sender) {
        if (sender instanceof Player) {
            return ((CraftPlayer) sender).getHandle();
        }
        if (sender instanceof BlockCommandSender) {
            return ((CraftBlockCommandSender) sender).getTileEntity();
        }
        if (sender instanceof CommandMinecart) {
            return ((EntityMinecartCommandBlock) ((CraftMinecartCommand) sender).getHandle()).getCommandBlock();
        }
        if (sender instanceof RemoteConsoleCommandSender) {
            return RemoteControlCommandListener.getInstance();
        }
        if (sender instanceof ConsoleCommandSender) {
            return ((CraftServer) sender.getServer()).getServer();
        }
        if (sender instanceof ProxiedCommandSender) {
            return ((ProxiedNativeCommandSender) sender).getHandle();
        }
        throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
    }

    private int getPlayerListSize(String[] as) {
        for (int i = 0; i < as.length; i++) {
            if (vanillaCommand.isListStart(as, i) && PlayerSelector.isList(as[i])) {
                return i;
            }
        }
        return -1;
    }

    public static String[] dropFirstArgument(String[] as) {
        String[] as1 = new String[as.length - 1];
        System.arraycopy(as, 1, as1, 0, as.length - 1);

        return as1;
    }
}
