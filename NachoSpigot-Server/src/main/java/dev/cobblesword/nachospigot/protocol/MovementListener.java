package dev.cobblesword.nachospigot.protocol;

import net.minecraft.server.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface MovementListener {
  default boolean updateLocation(Player paramPlayer, Location paramLocation1, Location paramLocation2, PacketPlayInFlying paramPacketPlayInFlying) {
    return true;
  }
  default boolean updateRotation(Player paramPlayer, Location paramLocation1, Location paramLocation2, PacketPlayInFlying paramPacketPlayInFlying) {
    return true;
  }
}
