package org.spigotmc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import dev.cobblesword.nachospigot.Nacho;
import me.elier.nachospigot.config.NachoConfig;

public class TicksPerSecondCommand extends Command
{

    public TicksPerSecondCommand(String name)
    {
        super( name );
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission( "bukkit.command.tps" );
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args)
    {
        if ( !testPermission( sender ) )
        {
            return true;
        }

        // PaperSpigot start - Further improve tick handling
        double[] tps = org.bukkit.Bukkit.spigot().getTPS();
        String[] tpsAvg = new String[tps.length];
        
        for ( int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format(tps[i], true);
        }
        
        sender.sendMessage( ChatColor.GOLD + "General TPS from last 1m, 5m, 15m: " + formatStringTPS(tpsAvg));
        
        if (NachoConfig.asyncHitDetection) {
            double[] hitDetectionTPS = Nacho.hitDetectionThread.getTPS();
            tpsAvg = new String[hitDetectionTPS.length];
            
            for ( int i = 0; i < hitDetectionTPS.length; i++) {
                tpsAvg[i] = format(hitDetectionTPS[i], false);
            }
            
            sender.sendMessage( ChatColor.GOLD + "Hit Detection TPS from last 1m, 5m, 15m: " + formatStringTPS(tpsAvg));
        }
        
        if (NachoConfig.asyncKnockback) {
            double[] knockbackTPS = Nacho.knockbackThread.getTPS();
            tpsAvg = new String[knockbackTPS.length];

            for ( int i = 0; i < knockbackTPS.length; i++) {
                tpsAvg[i] = format(knockbackTPS[i], false);
            }
            sender.sendMessage( ChatColor.GOLD + "Knockback TPS from last 1m, 5m, 15m: " + formatStringTPS(tpsAvg));
        }

        sender.sendMessage(ChatColor.GOLD + "Current Memory Usage: " + ChatColor.GREEN + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)) + "/" + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " mb (Max: " + (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " mb)");
        return true;
    }

    private static String formatStringTPS(String[] tpsAvg) {
        return org.apache.commons.lang.StringUtils.join(tpsAvg, ", ");
    }

    private static String format(double tps, boolean limit) // PaperSpigot - made static
    {
        if (limit) {
            return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
        }
        return (( tps > ((90.0 * tps) / 100.0) ) ? ChatColor.GREEN : ( tps > ((80.0 * tps) / 100.0) ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > NachoConfig.combatThreadTPS ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, NachoConfig.combatThreadTPS );
    }
}
