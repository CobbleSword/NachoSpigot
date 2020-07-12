package net.minecraft.server;

import java.util.List;

public class CommandGamemode extends CommandAbstract {

    public CommandGamemode() {}

    public String getCommand() {
        return "gamemode";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.gamemode.usage";
    }

    public void execute(ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length <= 0) {
            throw new ExceptionUsage("commands.gamemode.usage", new Object[0]);
        } else {
            WorldSettings.EnumGamemode worldsettings_enumgamemode = this.h(icommandlistener, astring[0]);
            EntityPlayer entityplayer = astring.length >= 2 ? a(icommandlistener, astring[1]) : b(icommandlistener);

            entityplayer.a(worldsettings_enumgamemode);
            // CraftBukkit start - handle event cancelling the change
            if (entityplayer.playerInteractManager.getGameMode() != worldsettings_enumgamemode) {
                icommandlistener.sendMessage(new ChatComponentText("Failed to set the gamemode of '" + entityplayer.getName() + "'"));
                return;
            }
            // CraftBukkit end

            entityplayer.fallDistance = 0.0F;
            if (icommandlistener.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
                entityplayer.sendMessage(new ChatMessage("gameMode.changed", new Object[0]));
            }

            ChatMessage chatmessage = new ChatMessage("gameMode." + worldsettings_enumgamemode.b(), new Object[0]);

            if (entityplayer != icommandlistener) {
                a(icommandlistener, this, 1, "commands.gamemode.success.other", new Object[] { entityplayer.getName(), chatmessage});
            } else {
                a(icommandlistener, this, 1, "commands.gamemode.success.self", new Object[] { chatmessage});
            }

        }
    }

    protected WorldSettings.EnumGamemode h(ICommandListener icommandlistener, String s) throws ExceptionInvalidNumber {
        return !s.equalsIgnoreCase(WorldSettings.EnumGamemode.SURVIVAL.b()) && !s.equalsIgnoreCase("s") ? (!s.equalsIgnoreCase(WorldSettings.EnumGamemode.CREATIVE.b()) && !s.equalsIgnoreCase("c") ? (!s.equalsIgnoreCase(WorldSettings.EnumGamemode.ADVENTURE.b()) && !s.equalsIgnoreCase("a") ? (!s.equalsIgnoreCase(WorldSettings.EnumGamemode.SPECTATOR.b()) && !s.equalsIgnoreCase("sp") ? WorldSettings.a(a(s, 0, WorldSettings.EnumGamemode.values().length - 2)) : WorldSettings.EnumGamemode.SPECTATOR) : WorldSettings.EnumGamemode.ADVENTURE) : WorldSettings.EnumGamemode.CREATIVE) : WorldSettings.EnumGamemode.SURVIVAL;
    }

    public List<String> tabComplete(ICommandListener icommandlistener, String[] astring, BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, new String[] { "survival", "creative", "adventure", "spectator"}) : (astring.length == 2 ? a(astring, this.d()) : null);
    }

    protected String[] d() {
        return MinecraftServer.getServer().getPlayers();
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 1;
    }

    // CraftBukkit start - fix decompiler error
    @Override
    public int compareTo(ICommand o) {
        return a((ICommand) o);
    }
    // CraftBukkit end
}
