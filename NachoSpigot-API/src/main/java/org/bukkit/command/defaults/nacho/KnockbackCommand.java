package org.bukkit.command.defaults.nacho;

import dev.cobblesword.nachospigot.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class KnockbackCommand extends Command {
    public KnockbackCommand(String name, List<String> aliases) {
        super(name, "", "/" + name, aliases);
        setPermission("ns.knockback");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] str) {
        if (!testPermission(sender)) return true; // by GreenMC - thank you!
        if(str.length == 0) {
            sendHelp(sender);
            return false;
        }
        switch (str[0].toLowerCase()) {
            case "reload": {
                sender.sendMessage(CC.gray + "Reloading KB config..");
                Bukkit.reloadKB();
                sender.sendMessage(CC.gray + "Reloaded KB config!");
                break;
            }
            case "toggle": {
                Bukkit.toggleKnockback(!Bukkit.customKnockback());
                sender.sendMessage(CC.yellow + "Custom Knockback value has been changed to " + Bukkit.customKnockback());
                break;
            }
            
            case "set": {
                if(!Bukkit.customKnockback()) {
                    sender.sendMessage(CC.red + "Custom knockback is disabled."); 
                    return false;
                }
                String[] args = Arrays.copyOfRange(str, 1, str.length);
                if (args.length < 2) {
                    sendHelpKB(sender);
                    break;
                }
                double value;
                try {
                    value = Double.parseDouble(args[1]);
                } catch (Exception ignored) {
                    sender.sendMessage(CC.red + "Invalid value.");
                    break;
                }
                boolean success = true;
                switch (args[0].toLowerCase()) {
                    case "f": {
                        Bukkit.setKnockbackFriction(value);
                        break;
                    }
                    case "h": {
                        Bukkit.setKnockbackHorizontal(value);
                        break;
                    }
                    case "v": {
                        Bukkit.setKnockbackVertical(value);
                        break;
                    }
                    case "vl": {
                        Bukkit.setKnockbackVerticalLimit(value);
                        break;
                    }
                    case "eh": {
                        Bukkit.setKnockbackExtraHorizontal(value);
                        break;
                    }
                    case "ev": {
                        Bukkit.setKnockbackExtraVertical(value);
                        break;
                    }
                    default: {
                        sendHelpKB(sender);
                        success = false;
                        break;
                    }
                }
                if (success) {
                    sender.sendMessage(CC.gray + args[0].toLowerCase() + CC.aqua + "set to " + CC.gray + value);
                }
                break;
            }
            default: {
                sendHelp(sender);
                break;
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(CC.red + "Please specify a subcommand. Possible subcommands:");
        sender.sendMessage(CC.gray + "<subcommand> | <description>");
        sender.sendMessage(CC.red + "reload | reload the config");
        sender.sendMessage(CC.red + "toggle | toggle the custom kb");
        sender.sendMessage(CC.red + "set <what to set> <value> | set a kb value to something and save to config");
    }

    private void sendHelpKB(CommandSender sender) {
        sender.sendMessage(CC.red + "Please specify what to set. Possible values:");
        sender.sendMessage(CC.gray + "<to type> | <description>");
        sender.sendMessage(CC.red + "f | friction");
        sender.sendMessage(CC.red + "h | horizontal");
        sender.sendMessage(CC.red + "v | vertical");
        sender.sendMessage(CC.red + "vl | vertical limit");
        sender.sendMessage(CC.red + "eh | extra horizontal");
        sender.sendMessage(CC.red + "ev | extra vertical");
    }
}
