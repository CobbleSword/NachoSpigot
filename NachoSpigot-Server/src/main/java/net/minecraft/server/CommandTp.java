package net.minecraft.server;

import java.util.EnumSet;
import java.util.List;

public class CommandTp extends CommandAbstract {

    public CommandTp() {}

    public String getCommand() {
        return "tp";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.tp.usage";
    }

    public void execute(ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length < 1) {
            throw new ExceptionUsage("commands.tp.usage", new Object[0]);
        } else {
            byte b0 = 0;
            Object object;

            if (astring.length != 2 && astring.length != 4 && astring.length != 6) {
                object = b(icommandlistener);
            } else {
                object = b(icommandlistener, astring[0]);
                b0 = 1;
            }

            if (astring.length != 1 && astring.length != 2) {
                if (astring.length < b0 + 3) {
                    throw new ExceptionUsage("commands.tp.usage", new Object[0]);
                } else if (((Entity) object).world != null) {
                    int i = b0 + 1;
                    CommandAbstract.CommandNumber commandabstract_commandnumber = a(((Entity) object).locX, astring[b0], true);
                    CommandAbstract.CommandNumber commandabstract_commandnumber1 = a(((Entity) object).locY, astring[i++], 0, 0, false);
                    CommandAbstract.CommandNumber commandabstract_commandnumber2 = a(((Entity) object).locZ, astring[i++], true);
                    CommandAbstract.CommandNumber commandabstract_commandnumber3 = a((double) ((Entity) object).yaw, astring.length > i ? astring[i++] : "~", false);
                    CommandAbstract.CommandNumber commandabstract_commandnumber4 = a((double) ((Entity) object).pitch, astring.length > i ? astring[i] : "~", false);
                    float f;

                    if (object instanceof EntityPlayer) {
                        EnumSet enumset = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);

                        if (commandabstract_commandnumber.c()) {
                            enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X);
                        }

                        if (commandabstract_commandnumber1.c()) {
                            enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y);
                        }

                        if (commandabstract_commandnumber2.c()) {
                            enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z);
                        }

                        if (commandabstract_commandnumber4.c()) {
                            enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT);
                        }

                        if (commandabstract_commandnumber3.c()) {
                            enumset.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT);
                        }

                        f = (float) commandabstract_commandnumber3.b();
                        if (!commandabstract_commandnumber3.c()) {
                            f = MathHelper.g(f);
                        }

                        float f1 = (float) commandabstract_commandnumber4.b();

                        if (!commandabstract_commandnumber4.c()) {
                            f1 = MathHelper.g(f1);
                        }

                        if (f1 > 90.0F || f1 < -90.0F) {
                            f1 = MathHelper.g(180.0F - f1);
                            f = MathHelper.g(f + 180.0F);
                        }

                        ((Entity) object).mount((Entity) null);
                        ((EntityPlayer) object).playerConnection.a(commandabstract_commandnumber.b(), commandabstract_commandnumber1.b(), commandabstract_commandnumber2.b(), f, f1, enumset);
                        ((Entity) object).f(f);
                    } else {
                        float f2 = (float) MathHelper.g(commandabstract_commandnumber3.a());

                        f = (float) MathHelper.g(commandabstract_commandnumber4.a());
                        if (f > 90.0F || f < -90.0F) {
                            f = MathHelper.g(180.0F - f);
                            f2 = MathHelper.g(f2 + 180.0F);
                        }

                        ((Entity) object).setPositionRotation(commandabstract_commandnumber.a(), commandabstract_commandnumber1.a(), commandabstract_commandnumber2.a(), f2, f);
                        ((Entity) object).f(f2);
                    }

                    a(icommandlistener, this, "commands.tp.success.coordinates", new Object[] { ((Entity) object).getName(), Double.valueOf(commandabstract_commandnumber.a()), Double.valueOf(commandabstract_commandnumber1.a()), Double.valueOf(commandabstract_commandnumber2.a())});
                }
            } else {
                Entity entity = b(icommandlistener, astring[astring.length - 1]);

                // CraftBukkit Start
                // Use Bukkit teleport method in all cases. It has cross dimensional handling, events
                if (((Entity) object).getBukkitEntity().teleport(entity.getBukkitEntity(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND)) {
                    a(icommandlistener, this, "commands.tp.success", new Object[]{((Entity) object).getName(), entity.getName()});
                    // CraftBukkit End
                }
            }
        }
    }

    public List<String> tabComplete(ICommandListener icommandlistener, String[] astring, BlockPosition blockposition) {
        return astring.length != 1 && astring.length != 2 ? null : a(astring, MinecraftServer.getServer().getPlayers());
    }

    public boolean isListStart(String[] astring, int i) {
        return i == 0;
    }

    // CraftBukkit start - fix decompile error
    @Override
    public int compareTo(ICommand o) {
        return a((ICommand) o);
    }
    // CraftBukkit end
}
