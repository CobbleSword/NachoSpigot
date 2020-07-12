package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ScoreboardServer extends Scoreboard {

    private final MinecraftServer a;
    private final Set<ScoreboardObjective> b = Sets.newHashSet();
    private PersistentScoreboard c;

    public ScoreboardServer(MinecraftServer minecraftserver) {
        this.a = minecraftserver;
    }

    public void handleScoreChanged(ScoreboardScore scoreboardscore) {
        super.handleScoreChanged(scoreboardscore);
        if (this.b.contains(scoreboardscore.getObjective())) {
            this.sendAll(new PacketPlayOutScoreboardScore(scoreboardscore));
        }

        this.b();
    }

    public void handlePlayerRemoved(String s) {
        super.handlePlayerRemoved(s);
        this.sendAll(new PacketPlayOutScoreboardScore(s));
        this.b();
    }

    public void a(String s, ScoreboardObjective scoreboardobjective) {
        super.a(s, scoreboardobjective);
        this.sendAll(new PacketPlayOutScoreboardScore(s, scoreboardobjective));
        this.b();
    }

    public void setDisplaySlot(int i, ScoreboardObjective scoreboardobjective) {
        ScoreboardObjective scoreboardobjective1 = this.getObjectiveForSlot(i);

        super.setDisplaySlot(i, scoreboardobjective);
        if (scoreboardobjective1 != scoreboardobjective && scoreboardobjective1 != null) {
            if (this.h(scoreboardobjective1) > 0) {
                this.sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            } else {
                this.g(scoreboardobjective1);
            }
        }

        if (scoreboardobjective != null) {
            if (this.b.contains(scoreboardobjective)) {
                this.sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            } else {
                this.e(scoreboardobjective);
            }
        }

        this.b();
    }

    public boolean addPlayerToTeam(String s, String s1) {
        if (super.addPlayerToTeam(s, s1)) {
            ScoreboardTeam scoreboardteam = this.getTeam(s1);

            this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, Arrays.asList(new String[] { s}), 3));
            this.b();
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String s, ScoreboardTeam scoreboardteam) {
        super.removePlayerFromTeam(s, scoreboardteam);
        this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, Arrays.asList(new String[] { s}), 4));
        this.b();
    }

    public void handleObjectiveAdded(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveAdded(scoreboardobjective);
        this.b();
    }

    public void handleObjectiveChanged(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveChanged(scoreboardobjective);
        if (this.b.contains(scoreboardobjective)) {
            this.sendAll(new PacketPlayOutScoreboardObjective(scoreboardobjective, 2));
        }

        this.b();
    }

    public void handleObjectiveRemoved(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveRemoved(scoreboardobjective);
        if (this.b.contains(scoreboardobjective)) {
            this.g(scoreboardobjective);
        }

        this.b();
    }

    public void handleTeamAdded(ScoreboardTeam scoreboardteam) {
        super.handleTeamAdded(scoreboardteam);
        this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 0));
        this.b();
    }

    public void handleTeamChanged(ScoreboardTeam scoreboardteam) {
        super.handleTeamChanged(scoreboardteam);
        this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 2));
        this.b();
    }

    public void handleTeamRemoved(ScoreboardTeam scoreboardteam) {
        super.handleTeamRemoved(scoreboardteam);
        this.sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 1));
        this.b();
    }

    public void a(PersistentScoreboard persistentscoreboard) {
        this.c = persistentscoreboard;
    }

    protected void b() {
        if (this.c != null) {
            this.c.c();
        }

    }

    public List<Packet> getScoreboardScorePacketsForObjective(ScoreboardObjective scoreboardobjective) {
        ArrayList arraylist = Lists.newArrayList();

        arraylist.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 0));

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) == scoreboardobjective) {
                arraylist.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            }
        }

        Iterator iterator = this.getScoresForObjective(scoreboardobjective).iterator();

        while (iterator.hasNext()) {
            ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next();

            arraylist.add(new PacketPlayOutScoreboardScore(scoreboardscore));
        }

        return arraylist;
    }

    public void e(ScoreboardObjective scoreboardobjective) {
        List list = this.getScoreboardScorePacketsForObjective(scoreboardobjective);
        Iterator iterator = this.a.getPlayerList().v().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            if (entityplayer.getBukkitEntity().getScoreboard().getHandle() != this) continue; // CraftBukkit - Only players on this board
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet packet = (Packet) iterator1.next();

                entityplayer.playerConnection.sendPacket(packet);
            }
        }

        this.b.add(scoreboardobjective);
    }

    public List<Packet> f(ScoreboardObjective scoreboardobjective) {
        ArrayList arraylist = Lists.newArrayList();

        arraylist.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 1));

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) == scoreboardobjective) {
                arraylist.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            }
        }

        return arraylist;
    }

    public void g(ScoreboardObjective scoreboardobjective) {
        List list = this.f(scoreboardobjective);
        Iterator iterator = this.a.getPlayerList().v().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            if (entityplayer.getBukkitEntity().getScoreboard().getHandle() != this) continue; // CraftBukkit - Only players on this board
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet packet = (Packet) iterator1.next();

                entityplayer.playerConnection.sendPacket(packet);
            }
        }

        this.b.remove(scoreboardobjective);
    }

    public int h(ScoreboardObjective scoreboardobjective) {
        int i = 0;

        for (int j = 0; j < 19; ++j) {
            if (this.getObjectiveForSlot(j) == scoreboardobjective) {
                ++i;
            }
        }

        return i;
    }

    // CraftBukkit start - Send to players
    private void sendAll(Packet packet) {
        for (EntityPlayer entityplayer : (List<EntityPlayer>) this.a.getPlayerList().players) {
            if (entityplayer.getBukkitEntity().getScoreboard().getHandle() == this) {
                entityplayer.playerConnection.sendPacket(packet);
            }
        }
    }
    // CraftBukkit end
}
