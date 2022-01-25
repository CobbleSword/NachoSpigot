package dev.cobblesword.nachospigot.hitdetection;

import me.elier.nachospigot.config.NachoConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class LagCompensator {

    /*
        Lag Compensator based on https://github.com/Islandscout/ServerSideHitDetection
        Adjusts hit detection to be more fair for players with worse ping.
     */

    private static final Map<UUID, List<Pair<Location, Long>>> locationTimes = new HashMap<>();
    private static final int historySize = 35;
    private static final int pingOffset = 175;
    private static final int TIME_RESOLUTION = 30;

    public static Location getHistoryLocation(int rewindMillisecs, Player player) {
        List<Pair<Location, Long>> times = locationTimes.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();

        if (times == null)
            return player.getLocation();

        int rewindTime = rewindMillisecs + pingOffset;

        for (int i = times.size() - 1; i >= 0; i--) {
            int elapsedTime = (int) (currentTime - times.get(i).getValue());

            if (elapsedTime >= rewindTime) {
                if (i == times.size() - 1)
                    return times.get(i).getKey();

                double nextMoveWeight = (elapsedTime - rewindTime) / (double) (elapsedTime - (currentTime - times.get(i + 1).getValue()));
                Location before = times.get(i).getKey().clone();
                Location after = times.get(i + 1).getKey();
                Vector interpolate = after.toVector().subtract(before.toVector());

                interpolate.multiply(nextMoveWeight);
                before.add(interpolate);

                return before;
            }
        }

        return player.getLocation();
    }

    private static void processPosition(Location loc, Player p) {
        if (!NachoConfig.enableImprovedHitReg) return;

        List<Pair<Location, Long>> times = locationTimes.getOrDefault(p.getUniqueId(), new ArrayList<>());
        long currTime = System.currentTimeMillis();

        if (times.size() > 0 && currTime - times.get(times.size() - 1).getValue() < TIME_RESOLUTION)
            return;

        times.add(new Pair<>(loc, currTime));

        if (times.size() > historySize)
            times.remove(0);

        locationTimes.put(p.getUniqueId(), times);
    }


    public static void registerMovement(Player player, Location to) {
        processPosition(to, player);
    }

    public static void registerRespawn(Player player, Location respawnLocation) {
        processPosition(respawnLocation, player);
    }

    public static void registerTeleport(Player player, Location to) {
        processPosition(to, player);
    }

    public static void registerWorldChange(Player player, Location to) {
        processPosition(to, player);
    }


    public int getHistorySize() {
        return historySize;
    }

    public int getPingOffset() {
        return pingOffset;
    }

}