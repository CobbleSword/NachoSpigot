package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;

public class StatisticManager {

    protected final Map<Statistic, StatisticWrapper> a = Maps.newConcurrentMap();

    public StatisticManager() {}

    public boolean hasAchievement(Achievement achievement) {
        return this.getStatisticValue(achievement) > 0;
    }

    public boolean b(Achievement achievement) {
        return achievement.c == null || this.hasAchievement(achievement.c);
    }

    public void b(EntityHuman entityhuman, Statistic statistic, int i) {
        if (!statistic.d() || this.b((Achievement) statistic)) {
            // CraftBukkit start - fire Statistic events
            org.bukkit.event.Cancellable cancellable = org.bukkit.craftbukkit.event.CraftEventFactory.handleStatisticsIncrease(entityhuman, statistic, this.getStatisticValue(statistic), i);
            if (cancellable != null && cancellable.isCancelled()) {
                return;
            }
            // CraftBukkit end
            this.setStatistic(entityhuman, statistic, this.getStatisticValue(statistic) + i);
        }
    }

    public void setStatistic(EntityHuman entityhuman, Statistic statistic, int i) {
        StatisticWrapper statisticwrapper = (StatisticWrapper) this.a.get(statistic);

        if (statisticwrapper == null) {
            statisticwrapper = new StatisticWrapper();
            this.a.put(statistic, statisticwrapper);
        }

        statisticwrapper.a(i);
    }

    public int getStatisticValue(Statistic statistic) {
        StatisticWrapper statisticwrapper = (StatisticWrapper) this.a.get(statistic);

        return statisticwrapper == null ? 0 : statisticwrapper.a();
    }

    public <T extends IJsonStatistic> T b(Statistic statistic) {
        StatisticWrapper statisticwrapper = (StatisticWrapper) this.a.get(statistic);

        return statisticwrapper != null ? (T) statisticwrapper.b() : null; // CraftBukkit - fix decompile error
    }

    public <T extends IJsonStatistic> T a(Statistic statistic, T t0) {
        StatisticWrapper statisticwrapper = (StatisticWrapper) this.a.get(statistic);

        if (statisticwrapper == null) {
            statisticwrapper = new StatisticWrapper();
            this.a.put(statistic, statisticwrapper);
        }

        statisticwrapper.a(t0);
        return t0;
    }
}
