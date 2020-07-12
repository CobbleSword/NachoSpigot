package net.minecraft.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

// CraftBukkit start
import java.util.ArrayList;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import com.google.common.base.Joiner;
import java.util.logging.Level;
// CraftBukkit end

public abstract class CommandBlockListenerAbstract implements ICommandListener {

    private static final SimpleDateFormat a = new SimpleDateFormat("HH:mm:ss");
    private int b;
    private boolean c = true;
    private IChatBaseComponent d = null;
    private String e = "";
    private String f = "@";
    private final CommandObjectiveExecutor g = new CommandObjectiveExecutor();
    protected org.bukkit.command.CommandSender sender; // CraftBukkit - add sender

    public CommandBlockListenerAbstract() {}

    public int j() {
        return this.b;
    }

    public IChatBaseComponent k() {
        return this.d;
    }

    public void a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Command", this.e);
        nbttagcompound.setInt("SuccessCount", this.b);
        nbttagcompound.setString("CustomName", this.f);
        nbttagcompound.setBoolean("TrackOutput", this.c);
        if (this.d != null && this.c) {
            nbttagcompound.setString("LastOutput", IChatBaseComponent.ChatSerializer.a(this.d));
        }

        this.g.b(nbttagcompound);
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.e = nbttagcompound.getString("Command");
        this.b = nbttagcompound.getInt("SuccessCount");
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.f = nbttagcompound.getString("CustomName");
        }

        if (nbttagcompound.hasKeyOfType("TrackOutput", 1)) {
            this.c = nbttagcompound.getBoolean("TrackOutput");
        }

        if (nbttagcompound.hasKeyOfType("LastOutput", 8) && this.c) {
            this.d = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("LastOutput"));
        }

        this.g.a(nbttagcompound);
    }

    public boolean a(int i, String s) {
        return i <= 2;
    }

    public void setCommand(String s) {
        this.e = s;
        this.b = 0;
    }

    public String getCommand() {
        return this.e;
    }

    public void a(World world) {
        if (world.isClientSide) {
            this.b = 0;
        }

        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (minecraftserver != null && minecraftserver.O() && minecraftserver.getEnableCommandBlock()) {
            ICommandHandler icommandhandler = minecraftserver.getCommandHandler();

            try {
                this.d = null;
                // this.b = icommandhandler.a(this, this.e);
                // CraftBukkit start - Handle command block commands using Bukkit dispatcher
                this.b = executeCommand(this, sender, this.e);
                // CraftBukkit end
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Executing command block");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Command to be executed");

                crashreportsystemdetails.a("Command", new Callable() {
                    public String a() throws Exception {
                        return CommandBlockListenerAbstract.this.getCommand();
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                crashreportsystemdetails.a("Name", new Callable() {
                    public String a() throws Exception {
                        return CommandBlockListenerAbstract.this.getName();
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                throw new ReportedException(crashreport);
            }
        } else {
            this.b = 0;
        }

    }

    // CraftBukkit start
    public static int executeCommand(ICommandListener sender, org.bukkit.command.CommandSender bSender, String command) {
        org.bukkit.command.SimpleCommandMap commandMap = sender.getWorld().getServer().getCommandMap();
        Joiner joiner = Joiner.on(" ");
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        String[] args = command.split(" ");
        ArrayList<String[]> commands = new ArrayList<String[]>();

        String cmd = args[0];
        if (cmd.startsWith("minecraft:")) cmd = cmd.substring("minecraft:".length());
        if (cmd.startsWith("bukkit:")) cmd = cmd.substring("bukkit:".length());

        // Block disallowed commands
        if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("kick") || cmd.equalsIgnoreCase("op")
                || cmd.equalsIgnoreCase("deop") || cmd.equalsIgnoreCase("ban") || cmd.equalsIgnoreCase("ban-ip")
                || cmd.equalsIgnoreCase("pardon") || cmd.equalsIgnoreCase("pardon-ip") || cmd.equalsIgnoreCase("reload")) {
            return 0;
        }

        // If the world has no players don't run
        if (sender.getWorld().players.isEmpty()) {
            return 0;
        }

        // Handle vanilla commands;
        org.bukkit.command.Command commandBlockCommand = commandMap.getCommand(args[0]);
        if (sender.getWorld().getServer().getCommandBlockOverride(args[0])) {
            commandBlockCommand = commandMap.getCommand("minecraft:" + args[0]);
        }
        if (commandBlockCommand instanceof VanillaCommandWrapper) {
            command = command.trim();
            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            String as[] = command.split(" ");
            as = VanillaCommandWrapper.dropFirstArgument(as);
            if (!((VanillaCommandWrapper) commandBlockCommand).testPermission(bSender)) {
                return 0;
            }
            return ((VanillaCommandWrapper) commandBlockCommand).dispatchVanillaCommand(bSender, sender, as);
        }

        // Make sure this is a valid command
        if (commandMap.getCommand(args[0]) == null) {
            return 0;
        }

        commands.add(args);

        // Find positions of command block syntax, if any        
        WorldServer[] prev = MinecraftServer.getServer().worldServer;
        MinecraftServer server = MinecraftServer.getServer();
        server.worldServer = new WorldServer[server.worlds.size()];
        server.worldServer[0] = (WorldServer) sender.getWorld();
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
            ArrayList<String[]> newCommands = new ArrayList<String[]>();
            for (int i = 0; i < args.length; i++) {
                if (PlayerSelector.isPattern(args[i])) {
                    for (int j = 0; j < commands.size(); j++) {
                        newCommands.addAll(buildCommands(sender, commands.get(j), i));
                    }
                    ArrayList<String[]> temp = commands;
                    commands = newCommands;
                    newCommands = temp;
                    newCommands.clear();
                }
            }
        } finally {
            MinecraftServer.getServer().worldServer = prev;
        }

        int completed = 0;

        // Now dispatch all of the commands we ended up with
        for (int i = 0; i < commands.size(); i++) {
            try {
                if (commandMap.dispatch(bSender, joiner.join(java.util.Arrays.asList(commands.get(i))))) {
                    completed++;
                }
            } catch (Throwable exception) {
                if (sender.f() instanceof EntityMinecartCommandBlock) {
                    MinecraftServer.getServer().server.getLogger().log(Level.WARNING, String.format("MinecartCommandBlock at (%d,%d,%d) failed to handle command", sender.getChunkCoordinates().getX(), sender.getChunkCoordinates().getY(), sender.getChunkCoordinates().getZ()), exception);
                } else if (sender instanceof CommandBlockListenerAbstract) {
                    CommandBlockListenerAbstract listener = (CommandBlockListenerAbstract) sender;
                    MinecraftServer.getServer().server.getLogger().log(Level.WARNING, String.format("CommandBlock at (%d,%d,%d) failed to handle command", listener.getChunkCoordinates().getX(), listener.getChunkCoordinates().getY(), listener.getChunkCoordinates().getZ()), exception);
                } else {
                    MinecraftServer.getServer().server.getLogger().log(Level.WARNING, String.format("Unknown CommandBlock failed to handle command"), exception);
                }
            }
        }

        return completed;
    }

    private static ArrayList<String[]> buildCommands(ICommandListener sender, String[] args, int pos) {
        ArrayList<String[]> commands = new ArrayList<String[]>();
        java.util.List<EntityPlayer> players = (java.util.List<EntityPlayer>)PlayerSelector.getPlayers(sender, args[pos], EntityPlayer.class);

        if (players != null) {
            for (EntityPlayer player : players) {
                if (player.world != sender.getWorld()) {
                    continue;
                }
                String[] command = args.clone();
                command[pos] = player.getName();
                commands.add(command);
            }
        }

        return commands;
    }
    // CraftBukkit end

    public String getName() {
        return this.f;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatComponentText(this.getName());
    }

    public void setName(String s) {
        this.f = s;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        if (this.c && this.getWorld() != null && !this.getWorld().isClientSide) {
            this.d = (new ChatComponentText("[" + CommandBlockListenerAbstract.a.format(new Date()) + "] ")).addSibling(ichatbasecomponent);
            this.h();
        }

    }

    public boolean getSendCommandFeedback() {
        MinecraftServer minecraftserver = MinecraftServer.getServer();

        return minecraftserver == null || !minecraftserver.O() || minecraftserver.worldServer[0].getGameRules().getBoolean("commandBlockOutput");
    }

    public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
        this.g.a(this, commandobjectiveexecutor_enumcommandresult, i);
    }

    public abstract void h();

    public void b(IChatBaseComponent ichatbasecomponent) {
        this.d = ichatbasecomponent;
    }

    public void a(boolean flag) {
        this.c = flag;
    }

    public boolean m() {
        return this.c;
    }

    public boolean a(EntityHuman entityhuman) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            return false;
        } else {
            if (entityhuman.getWorld().isClientSide) {
                entityhuman.a(this);
            }

            return true;
        }
    }

    public CommandObjectiveExecutor n() {
        return this.g;
    }
}
