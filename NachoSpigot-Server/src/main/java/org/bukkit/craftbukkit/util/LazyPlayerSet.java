package org.bukkit.craftbukkit.util;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;

public class LazyPlayerSet extends LazyHashSet<Player> {

    @Override
    HashSet<Player> makeReference() {
        if (reference != null) {
            throw new IllegalStateException("Reference already created!");
        }
        List<EntityPlayer> players = MinecraftServer.getServer().getPlayerList().players;
        HashSet<Player> reference = new HashSet<>(players.size());
        for (EntityPlayer player : players) {
            reference.add(player.getBukkitEntity());
        }
        return reference;
    }

}
