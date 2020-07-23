package dev.cobblesword.nachospigot.protocol;

import net.minecraft.server.Packet;
import net.minecraft.server.PlayerConnection;

public interface PacketListener
{
  /**
   *
   * @param playerConnection
   * @param packet
   * @return allowed
   */
  default boolean onReceivedPacket(PlayerConnection playerConnection, Packet packet)
  {
    return true;
  }

  /**
   *
   * @param connection
   * @param packet
   * @return allowed
   */
  default boolean onSentPacket(PlayerConnection connection, Packet packet) {
    return true;
  }
}
