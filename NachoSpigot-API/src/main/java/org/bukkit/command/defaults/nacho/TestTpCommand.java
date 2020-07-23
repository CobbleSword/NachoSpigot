package org.bukkit.command.defaults.nacho;


import dev.cobblesword.nachospigot.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class TestTpCommand extends Command {
    public TestTpCommand(String name)
    {
        super(name);
        this.description = "teleport test mobs";
        this.usageMessage = "/testtp";
        setPermission("NachoSpigot.command.testtp");
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender))
            return true;

        if(!(sender instanceof Player))
        {
            sender.sendMessage(CC.red + "Please run this command as a player");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();


        player.teleport(new Location(location.getWorld(), 10, 80, 12));
        player.sendMessage(CC.green + "Teleported...");
        return false;
    }
}