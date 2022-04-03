package org.spigotmc;

import dev.cobblesword.nachospigot.Nacho;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

public class RestartCommand extends Command
{

    public RestartCommand(String name)
    {
        super( name );
        this.description = "Restarts the server";
        this.usageMessage = "/restart";
        this.setPermission( "bukkit.command.restart" );
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args)
    {
        if ( testPermission( sender ) )
        {
            MinecraftServer.getServer().processQueue.add(RestartCommand::restart);
        }
        return true;
    }

    public static void restart()
    {
        restart( new File( SpigotConfig.restartScript ) );
    }

    public static void restart(final File script)
    {
        AsyncCatcher.enabled = false; // Disable async catcher incase it interferes with us
        try
        {
            if ( script.isFile() )
            {
                Nacho.LOGGER.info( "Attempting to restart with " + SpigotConfig.restartScript );

                // Disable Watchdog
                WatchdogThread.doStop();

                // Kick all players
                for ( EntityPlayer p : MinecraftServer.getServer().getPlayerList().players)
                {
                    p.playerConnection.disconnect(SpigotConfig.restartMessage);
                }
                // Give the socket a chance to send the packets
                try
                {
                    Thread.sleep( 100 );
                } catch ( InterruptedException ignored)
                {
                }
                // Close the socket so we can rebind with the new process
                MinecraftServer.getServer().getServerConnection().stopServer();

                // Give time for it to kick in
                try
                {
                    Thread.sleep( 100 );
                } catch ( InterruptedException ignored)
                {
                }

                // Actually shutdown
                try
                {
                    MinecraftServer.getServer().stop();
                } catch ( Throwable ignored)
                {
                }

                // This will be done AFTER the server has completely halted
                Thread shutdownHook = new Thread(() -> {
                    try
                    {
                        String os = System.getProperty( "os.name" ).toLowerCase();
                        if ( os.contains( "win" ) )
                        {
                            Runtime.getRuntime().exec( new String[]
                                    {
                                            "cmd /c start ", script.getPath()
                                    } );
                        } else
                        {
                            Runtime.getRuntime().exec( new String[]
                            {
                                "sh", script.getPath()
                            } );
                        }
                    } catch ( Exception e )
                    {
                        e.printStackTrace();
                    }
                });

                shutdownHook.setDaemon( true );
                Runtime.getRuntime().addShutdownHook( shutdownHook );
            } else
            {
                Nacho.LOGGER.error( "Startup script '" + SpigotConfig.restartScript + "' does not exist! Stopping server." );
            }
            System.exit( 0 );
        } catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }
}
