package net.minecraft.server;

import com.google.gson.JsonParseException;

public class TileEntitySign extends TileEntity {

    public final IChatBaseComponent[] lines = new IChatBaseComponent[] { new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")};
    public int f = -1;
    public boolean isEditable = true;
    private EntityHuman h;
    private final CommandObjectiveExecutor i = new CommandObjectiveExecutor();

    public TileEntitySign() {}

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            String s = IChatBaseComponent.ChatSerializer.a(this.lines[i]);

            nbttagcompound.setString("Text" + (i + 1), s);
        }

        // CraftBukkit start
        if (Boolean.getBoolean("convertLegacySigns")) {
            nbttagcompound.setBoolean("Bukkit.isConverted", true);
        }
        // CraftBukkit end

        this.i.b(nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.a(nbttagcompound);
        ICommandListener icommandlistener = new ICommandListener() {
            public String getName() {
                return "Sign";
            }

            public IChatBaseComponent getScoreboardDisplayName() {
                return new ChatComponentText(this.getName());
            }

            public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

            public boolean a(int i, String s) {
                return true;
            }

            public BlockPosition getChunkCoordinates() {
                return TileEntitySign.this.position;
            }

            public Vec3D d() {
                return new Vec3D((double) TileEntitySign.this.position.getX() + 0.5D, (double) TileEntitySign.this.position.getY() + 0.5D, (double) TileEntitySign.this.position.getZ() + 0.5D);
            }

            public World getWorld() {
                return TileEntitySign.this.world;
            }

            public Entity f() {
                return null;
            }

            public boolean getSendCommandFeedback() {
                return false;
            }

            public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {}
        };

        // CraftBukkit start - Add an option to convert signs correctly
        // This is done with a flag instead of all the time because
        // we have no way to tell whether a sign is from 1.7.10 or 1.8

        boolean oldSign = Boolean.getBoolean("convertLegacySigns") && !nbttagcompound.getBoolean("Bukkit.isConverted");

        for (int i = 0; i < 4; ++i) {
            String s = nbttagcompound.getString("Text" + (i + 1));
            if (s != null && s.length() > 2048) {
                s = "\"\"";
            }

            try {
                IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s);

                if (oldSign) {
                    lines[i] = org.bukkit.craftbukkit.util.CraftChatMessage.fromString(s)[0];
                    continue;
                }
                // CraftBukkit end

                try {
                    this.lines[i] = ChatComponentUtils.filterForDisplay(icommandlistener, ichatbasecomponent, (Entity) null);
                } catch (CommandException commandexception) {
                    this.lines[i] = ichatbasecomponent;
                }
            } catch (JsonParseException jsonparseexception) {
                this.lines[i] = new ChatComponentText(s);
            }
        }

        this.i.a(nbttagcompound);
    }

    public Packet getUpdatePacket() {
        IChatBaseComponent[] aichatbasecomponent = new IChatBaseComponent[4];

        System.arraycopy(this.lines, 0, aichatbasecomponent, 0, 4);
        return new PacketPlayOutUpdateSign(this.world, this.position, aichatbasecomponent);
    }

    public boolean F() {
        return true;
    }

    public boolean b() {
        return this.isEditable;
    }

    public void a(EntityHuman entityhuman) {
        this.h = entityhuman;
    }

    public EntityHuman c() {
        return this.h;
    }

    public boolean b(final EntityHuman entityhuman) {
        ICommandListener icommandlistener = new ICommandListener() {
            public String getName() {
                return entityhuman.getName();
            }

            public IChatBaseComponent getScoreboardDisplayName() {
                return entityhuman.getScoreboardDisplayName();
            }

            public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

            public boolean a(int i, String s) {
                return i <= 2;
            }

            public BlockPosition getChunkCoordinates() {
                return TileEntitySign.this.position;
            }

            public Vec3D d() {
                return new Vec3D((double) TileEntitySign.this.position.getX() + 0.5D, (double) TileEntitySign.this.position.getY() + 0.5D, (double) TileEntitySign.this.position.getZ() + 0.5D);
            }

            public World getWorld() {
                return entityhuman.getWorld();
            }

            public Entity f() {
                return entityhuman;
            }

            public boolean getSendCommandFeedback() {
                return false;
            }

            public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
                TileEntitySign.this.i.a(this, commandobjectiveexecutor_enumcommandresult, i);
            }
        };

        for (int i = 0; i < this.lines.length; ++i) {
            ChatModifier chatmodifier = this.lines[i] == null ? null : this.lines[i].getChatModifier();

            if (chatmodifier != null && chatmodifier.h() != null) {
                ChatClickable chatclickable = chatmodifier.h();

                if (chatclickable.a() == ChatClickable.EnumClickAction.RUN_COMMAND) {
                    // CraftBukkit start
                    // MinecraftServer.getServer().getCommandHandler().a(tileentitysignplayerwrapper, chatclickable.b());
                    CommandBlockListenerAbstract.executeCommand(entityhuman, (org.bukkit.entity.Player) entityhuman.getBukkitEntity(), chatclickable.b());
                    // CraftBukkit end
                }
            }
        }

        return true;
    }

    public CommandObjectiveExecutor d() {
        return this.i;
    }
}
