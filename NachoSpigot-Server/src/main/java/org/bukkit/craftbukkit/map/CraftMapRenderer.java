package org.bukkit.craftbukkit.map;

import net.minecraft.server.MapIcon;
import net.minecraft.server.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.UUID;

public class CraftMapRenderer extends MapRenderer {

    private final WorldMap worldMap;

    public CraftMapRenderer(CraftMapView mapView, WorldMap worldMap) {
        super(false);
        this.worldMap = worldMap;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        // Map
        for (int x = 0; x < 128; ++x) {
            for (int y = 0; y < 128; ++y) {
                canvas.setPixel(x, y, worldMap.colors[y * 128 + x]);
            }
        }

        // Cursors
        MapCursorCollection cursors = canvas.getCursors();
        while (cursors.size() > 0) {
            cursors.removeCursor(cursors.getCursor(0));
        }

        for (UUID key : worldMap.decorations.keySet()) { // Spigot string -> uuid.
            // If this cursor is for a player check visibility with vanish system
            Player other = Bukkit.getPlayer(key); // Spigot
            if (other != null && !player.canSee(other)) {
                continue;
            }
      
            MapIcon decoration = worldMap.decorations.get(key);
            cursors.addCursor(decoration.getX(), decoration.getY(), (byte) (decoration.getRotation() & 15), decoration.getType());
        }
    }

}
