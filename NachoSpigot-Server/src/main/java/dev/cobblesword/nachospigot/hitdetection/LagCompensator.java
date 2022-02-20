package dev.cobblesword.nachospigot.hitdetection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import me.elier.nachospigot.config.NachoConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class LagCompensator {

    /*
        Lag Compensator based on https://github.com/Islandscout/ServerSideHitDetection
        Adjusts hit detection to be more fair for players with worse ping.
     */

    private final ListMultimap<UUID, Pair<Location, Long>> locationTimes = ArrayListMultimap.create();
    private final int historySize = 40;
    private final int pingOffset = 92;
    private final int timeResolution = 30;

    public Location getHistoryLocation(Player player) {
        if (!locationTimes.containsKey(player.getUniqueId()))
            return player.getLocation();

        int rewindMillisecs = player.spigot().getPing();

        List<Pair<Location, Long>> times = locationTimes.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();

        int rewindTime = rewindMillisecs + pingOffset;
        int timesSize = times.size() - 1;

        for (int i = timesSize; i >= 0; i--) {
            int elapsedTime = (int) (currentTime - times.get(i).getValue());

            if (elapsedTime >= rewindTime) {
                if (i == timesSize)
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

    private void processPosition(Location loc, Player p) {
        if (!NachoConfig.enableImprovedHitReg) return;

        int timesSize = locationTimes.get(p.getUniqueId()).size();
        long currTime = System.currentTimeMillis();

        if (timesSize > 0 && currTime - locationTimes.get(p.getUniqueId()).get(timesSize - 1).getValue() < timeResolution)
            return;

        locationTimes.put(p.getUniqueId(), Pair.of(loc, currTime));

        if (timesSize > historySize)
            locationTimes.get(p.getUniqueId()).remove(0);
    }


    public void registerMovement(Player player, Location to) {
        processPosition(to, player);
    }

    public void clearCache(Player player) {
        locationTimes.removeAll(player.getUniqueId());
    }

}
