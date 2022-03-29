package org.bukkit.craftbukkit.block;

import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.server.ChatComponentText;
import net.minecraft.server.TileEntityBeacon;
import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.Beacon;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventoryBeacon;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public class CraftBeacon extends CraftContainer implements Beacon {
    private final CraftWorld world;
    private final TileEntityBeacon beacon;

    public CraftBeacon(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        beacon = (TileEntityBeacon) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public CraftBeacon(final Material material, final TileEntityBeacon te) {
        super(material, te);
        world = null;
        beacon = te;
    }

    public Inventory getInventory() {
        return new CraftInventoryBeacon(beacon);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);

        if (result) {
            beacon.update();
        }

        return result;
    }

    @Override
    public TileEntityBeacon getTileEntity() {
        return beacon;
    }

    // Paper start
    @Override
    public net.kyori.adventure.text.Component customName() {
        return beacon.hasCustomName() ? io.papermc.paper.adventure.PaperAdventure.asAdventure(new ChatComponentText(beacon.getName())) : null;
    }

    @Override
    public void customName(final net.kyori.adventure.text.Component customName) {
        this.beacon.setCustomName(customName != null ? PaperAdventure.LEGACY_SECTION_UXRC.serialize(customName) : null);
    }
    // Paper end

    @Override
    public @Nullable String getCustomName() {
        return beacon.hasCustomName() ? beacon.getName() : null;
    }

    @Override
    public void setCustomName(@Nullable String name) {
        beacon.setCustomName(name);
    }
}

