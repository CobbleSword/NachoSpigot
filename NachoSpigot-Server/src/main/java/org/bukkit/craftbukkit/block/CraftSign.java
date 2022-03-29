package org.bukkit.craftbukkit.block;

import net.minecraft.server.ChatComponentText;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.TileEntitySign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.jetbrains.annotations.NotNull;

public class CraftSign extends CraftBlockState implements Sign {
    private final TileEntitySign sign;
    private java.util.ArrayList<net.kyori.adventure.text.Component> lines; // Paper - ArrayList for RandomAccess

    public CraftSign(final Block block) {
        super(block);

        CraftWorld world = (CraftWorld) block.getWorld();
        sign = (TileEntitySign) world.getTileEntityAt(getX(), getY(), getZ());
        /* Nacho - lazy init
        // Spigot start
        if (sign == null) {
            lines = new String[]{"", "", "", ""};
            return;
        }
        // Spigot end
        lines = new String[sign.lines.length];
        System.arraycopy(revertComponents(sign.lines), 0, lines, 0, lines.length);
        */ // Nacho
    }

    public CraftSign(final Material material, final TileEntitySign te) {
        super(material);
        sign = te;
        /*lines = new String[sign.lines.length]; // Nacho - lazy init
        System.arraycopy(revertComponents(sign.lines), 0, lines, 0, lines.length);*/ // Nacho - lazy init
    }

    // Paper start
    public java.util.@NotNull List<net.kyori.adventure.text.Component> lines() {
        this.loadLines();
        return this.lines;
    }

    @Override
    public net.kyori.adventure.text.@NotNull Component line(int index) {
        this.loadLines();
        return this.lines.get(index);
    }

    @Override
    public void line(int index, net.kyori.adventure.text.@NotNull Component line) {
        this.loadLines();
        this.lines.set(index, line);
    }

    private void loadLines() {
        if (lines != null) {
            return;
        }

        // Lazy initialization:
        lines = io.papermc.paper.adventure.PaperAdventure.asAdventure(com.google.common.collect.Lists.newArrayList(sign.lines));
    }
    // Paper end

    public String[] getLines() {
        this.loadLines(); // Paper
        return this.lines.stream().map(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection()::serialize).toArray(String[]::new); // Paper
    }

    public String getLine(int index) throws IndexOutOfBoundsException {
        this.loadLines(); // Paper
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(this.lines.get(index)); // Paper
    }

    public void setLine(int index, @NotNull String line) throws IndexOutOfBoundsException {
        this.loadLines(); // Paper
        this.lines.set(index, net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(line)); // Paper
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            IChatBaseComponent[] newLines = sanitizeLines(lines);
            System.arraycopy(newLines, 0, sign.lines, 0, 4);
            sign.update();
        }

        return result;
    }

    // Paper start
    public static IChatBaseComponent[] sanitizeLines(java.util.List<net.kyori.adventure.text.Component> lines) {
        IChatBaseComponent[] components = new IChatBaseComponent[4];
        for (int i = 0; i < 4; i++) {
            if (i < lines.size() && lines.get(i) != null) {
                components[i] = io.papermc.paper.adventure.PaperAdventure.asVanilla(lines.get(i));
            } else {
                components[i] = new ChatComponentText("");
            }
        }
        return components;
    }
    // Paper end

    public static IChatBaseComponent[] sanitizeLines(String[] lines) {
        IChatBaseComponent[] components = new IChatBaseComponent[4];

        for (int i = 0; i < 4; i++) {
            if (i < lines.length && lines[i] != null) {
                components[i] = CraftChatMessage.fromString(lines[i])[0];
            } else {
                components[i] = new ChatComponentText("");
            }
        }

        return components;
    }

    public static String[] revertComponents(IChatBaseComponent[] components) {
        String[] lines = new String[components.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = revertComponent(components[i]);
        }
        return lines;
    }

    private static String revertComponent(IChatBaseComponent component) {
        return CraftChatMessage.fromComponent(component);
    }

    @Override
    public TileEntitySign getTileEntity() {
        return sign;
    }
}
