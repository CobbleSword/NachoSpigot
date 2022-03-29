package org.bukkit.craftbukkit.block;

import net.minecraft.server.TileEntityCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.craftbukkit.CraftWorld;
import org.jetbrains.annotations.NotNull;

public class CraftCommandBlock extends CraftBlockState implements CommandBlock {
    private final TileEntityCommand commandBlock;
    private String command;
    private String name;

    public CraftCommandBlock(Block block) {
        super(block);

        CraftWorld world = (CraftWorld) block.getWorld();
        commandBlock = (TileEntityCommand) world.getTileEntityAt(getX(), getY(), getZ());
        command = commandBlock.getCommandBlock().getCommand();
        name = commandBlock.getCommandBlock().getName();
    }

    public CraftCommandBlock(final Material material, final TileEntityCommand te) {
        super(material);
        commandBlock = te;
        command = commandBlock.getCommandBlock().getCommand();
        name = commandBlock.getCommandBlock().getName();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command != null ? command : "";
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "@";
    }

    // Paper start
    @Override
    public net.kyori.adventure.text.Component name() {
        return io.papermc.paper.adventure.PaperAdventure.LEGACY_SECTION_UXRC.deserialize(getTileEntity().getCommandBlock().getName());
    }

    @Override
    public void name(net.kyori.adventure.text.Component name) {
        getTileEntity().getCommandBlock().setName(name == null ? "@" : io.papermc.paper.adventure.PaperAdventure.LEGACY_SECTION_UXRC.serialize(name));
    }
    // Paper end

    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            commandBlock.getCommandBlock().setCommand(command);
            commandBlock.getCommandBlock().setName(name);
        }

        return result;
    }

    @Override
    public TileEntityCommand getTileEntity() {
        return commandBlock;
    }
}
