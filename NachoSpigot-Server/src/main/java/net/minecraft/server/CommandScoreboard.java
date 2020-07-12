package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandScoreboard extends CommandAbstract {

    // TacoSpigot start - fix compile errors
    @Override
    public int compareTo(ICommand o) {
        return super.a(o);
    }
    // TacoSpigot end

    public CommandScoreboard() {}

    public String getCommand() {
        return "scoreboard";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.scoreboard.usage";
    }

    public void execute(ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (!this.b(icommandlistener, astring)) {
            if (astring.length < 1) {
                throw new ExceptionUsage("commands.scoreboard.usage", new Object[0]);
            } else {
                if (astring[0].equalsIgnoreCase("objectives")) {
                    if (astring.length == 1) {
                        throw new ExceptionUsage("commands.scoreboard.objectives.usage", new Object[0]);
                    }

                    if (astring[1].equalsIgnoreCase("list")) {
                        this.d(icommandlistener);
                    } else if (astring[1].equalsIgnoreCase("add")) {
                        if (astring.length < 4) {
                            throw new ExceptionUsage("commands.scoreboard.objectives.add.usage", new Object[0]);
                        }

                        this.b(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("remove")) {
                        if (astring.length != 3) {
                            throw new ExceptionUsage("commands.scoreboard.objectives.remove.usage", new Object[0]);
                        }

                        this.h(icommandlistener, astring[2]);
                    } else {
                        if (!astring[1].equalsIgnoreCase("setdisplay")) {
                            throw new ExceptionUsage("commands.scoreboard.objectives.usage", new Object[0]);
                        }

                        if (astring.length != 3 && astring.length != 4) {
                            throw new ExceptionUsage("commands.scoreboard.objectives.setdisplay.usage", new Object[0]);
                        }

                        this.j(icommandlistener, astring, 2);
                    }
                } else if (astring[0].equalsIgnoreCase("players")) {
                    if (astring.length == 1) {
                        throw new ExceptionUsage("commands.scoreboard.players.usage", new Object[0]);
                    }

                    if (astring[1].equalsIgnoreCase("list")) {
                        if (astring.length > 3) {
                            throw new ExceptionUsage("commands.scoreboard.players.list.usage", new Object[0]);
                        }

                        this.k(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("add")) {
                        if (astring.length < 5) {
                            throw new ExceptionUsage("commands.scoreboard.players.add.usage", new Object[0]);
                        }

                        this.l(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("remove")) {
                        if (astring.length < 5) {
                            throw new ExceptionUsage("commands.scoreboard.players.remove.usage", new Object[0]);
                        }

                        this.l(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("set")) {
                        if (astring.length < 5) {
                            throw new ExceptionUsage("commands.scoreboard.players.set.usage", new Object[0]);
                        }

                        this.l(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("reset")) {
                        if (astring.length != 3 && astring.length != 4) {
                            throw new ExceptionUsage("commands.scoreboard.players.reset.usage", new Object[0]);
                        }

                        this.m(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("enable")) {
                        if (astring.length != 4) {
                            throw new ExceptionUsage("commands.scoreboard.players.enable.usage", new Object[0]);
                        }

                        this.n(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("test")) {
                        if (astring.length != 5 && astring.length != 6) {
                            throw new ExceptionUsage("commands.scoreboard.players.test.usage", new Object[0]);
                        }

                        this.o(icommandlistener, astring, 2);
                    } else {
                        if (!astring[1].equalsIgnoreCase("operation")) {
                            throw new ExceptionUsage("commands.scoreboard.players.usage", new Object[0]);
                        }

                        if (astring.length != 7) {
                            throw new ExceptionUsage("commands.scoreboard.players.operation.usage", new Object[0]);
                        }

                        this.p(icommandlistener, astring, 2);
                    }
                } else {
                    if (!astring[0].equalsIgnoreCase("teams")) {
                        throw new ExceptionUsage("commands.scoreboard.usage", new Object[0]);
                    }

                    if (astring.length == 1) {
                        throw new ExceptionUsage("commands.scoreboard.teams.usage", new Object[0]);
                    }

                    if (astring[1].equalsIgnoreCase("list")) {
                        if (astring.length > 3) {
                            throw new ExceptionUsage("commands.scoreboard.teams.list.usage", new Object[0]);
                        }

                        this.f(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("add")) {
                        if (astring.length < 3) {
                            throw new ExceptionUsage("commands.scoreboard.teams.add.usage", new Object[0]);
                        }

                        this.c(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("remove")) {
                        if (astring.length != 3) {
                            throw new ExceptionUsage("commands.scoreboard.teams.remove.usage", new Object[0]);
                        }

                        this.e(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("empty")) {
                        if (astring.length != 3) {
                            throw new ExceptionUsage("commands.scoreboard.teams.empty.usage", new Object[0]);
                        }

                        this.i(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("join")) {
                        if (astring.length < 4 && (astring.length != 3 || !(icommandlistener instanceof EntityHuman))) {
                            throw new ExceptionUsage("commands.scoreboard.teams.join.usage", new Object[0]);
                        }

                        this.g(icommandlistener, astring, 2);
                    } else if (astring[1].equalsIgnoreCase("leave")) {
                        if (astring.length < 3 && !(icommandlistener instanceof EntityHuman)) {
                            throw new ExceptionUsage("commands.scoreboard.teams.leave.usage", new Object[0]);
                        }

                        this.h(icommandlistener, astring, 2);
                    } else {
                        if (!astring[1].equalsIgnoreCase("option")) {
                            throw new ExceptionUsage("commands.scoreboard.teams.usage", new Object[0]);
                        }

                        if (astring.length != 4 && astring.length != 5) {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.usage", new Object[0]);
                        }

                        this.d(icommandlistener, astring, 2);
                    }
                }

            }
        }
    }

    private boolean b(ICommandListener icommandlistener, String[] astring) throws CommandException {
        int i = -1;

        for (int j = 0; j < astring.length; ++j) {
            if (this.isListStart(astring, j) && "*".equals(astring[j])) {
                if (i >= 0) {
                    throw new CommandException("commands.scoreboard.noMultiWildcard", new Object[0]);
                }

                i = j;
            }
        }

        if (i < 0) {
            return false;
        } else {
            ArrayList arraylist = Lists.newArrayList(this.d().getPlayers());
            String s = astring[i];
            ArrayList arraylist1 = Lists.newArrayList();
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();

                astring[i] = s1;

                try {
                    this.execute(icommandlistener, astring);
                    arraylist1.add(s1);
                } catch (CommandException commandexception) {
                    ChatMessage chatmessage = new ChatMessage(commandexception.getMessage(), commandexception.getArgs());

                    chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
                    icommandlistener.sendMessage(chatmessage);
                }
            }

            astring[i] = s;
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, arraylist1.size());
            if (arraylist1.size() == 0) {
                throw new ExceptionUsage("commands.scoreboard.allMatchesFailed", new Object[0]);
            } else {
                return true;
            }
        }
    }

    protected Scoreboard d() {
        return MinecraftServer.getServer().getWorldServer(0).getScoreboard();
    }

    protected ScoreboardObjective a(String s, boolean flag) throws CommandException {
        Scoreboard scoreboard = this.d();
        ScoreboardObjective scoreboardobjective = scoreboard.getObjective(s);

        if (scoreboardobjective == null) {
            throw new CommandException("commands.scoreboard.objectiveNotFound", new Object[] { s});
        } else if (flag && scoreboardobjective.getCriteria().isReadOnly()) {
            throw new CommandException("commands.scoreboard.objectiveReadOnly", new Object[] { s});
        } else {
            return scoreboardobjective;
        }
    }

    protected ScoreboardTeam e(String s) throws CommandException {
        Scoreboard scoreboard = this.d();
        ScoreboardTeam scoreboardteam = scoreboard.getTeam(s);

        if (scoreboardteam == null) {
            throw new CommandException("commands.scoreboard.teamNotFound", new Object[] { s});
        } else {
            return scoreboardteam;
        }
    }

    protected void b(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        String s = astring[i++];
        String s1 = astring[i++];
        Scoreboard scoreboard = this.d();
        IScoreboardCriteria iscoreboardcriteria = (IScoreboardCriteria) IScoreboardCriteria.criteria.get(s1);

        if (iscoreboardcriteria == null) {
            throw new ExceptionUsage("commands.scoreboard.objectives.add.wrongType", new Object[] { s1});
        } else if (scoreboard.getObjective(s) != null) {
            throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", new Object[] { s});
        } else if (s.length() > 16) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.objectives.add.tooLong", new Object[] { s, Integer.valueOf(16)});
        } else if (s.length() == 0) {
            throw new ExceptionUsage("commands.scoreboard.objectives.add.usage", new Object[0]);
        } else {
            if (astring.length > i) {
                String s2 = a(icommandlistener, astring, i).c();

                if (s2.length() > 32) {
                    throw new ExceptionInvalidSyntax("commands.scoreboard.objectives.add.displayTooLong", new Object[] { s2, Integer.valueOf(32)});
                }

                if (s2.length() > 0) {
                    scoreboard.registerObjective(s, iscoreboardcriteria).setDisplayName(s2);
                } else {
                    scoreboard.registerObjective(s, iscoreboardcriteria);
                }
            } else {
                scoreboard.registerObjective(s, iscoreboardcriteria);
            }

            a(icommandlistener, this, "commands.scoreboard.objectives.add.success", new Object[] { s});
        }
    }

    protected void c(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        String s = astring[i++];
        Scoreboard scoreboard = this.d();

        if (scoreboard.getTeam(s) != null) {
            throw new CommandException("commands.scoreboard.teams.add.alreadyExists", new Object[] { s});
        } else if (s.length() > 16) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.teams.add.tooLong", new Object[] { s, Integer.valueOf(16)});
        } else if (s.length() == 0) {
            throw new ExceptionUsage("commands.scoreboard.teams.add.usage", new Object[0]);
        } else {
            if (astring.length > i) {
                String s1 = a(icommandlistener, astring, i).c();

                if (s1.length() > 32) {
                    throw new ExceptionInvalidSyntax("commands.scoreboard.teams.add.displayTooLong", new Object[] { s1, Integer.valueOf(32)});
                }

                if (s1.length() > 0) {
                    scoreboard.createTeam(s).setDisplayName(s1);
                } else {
                    scoreboard.createTeam(s);
                }
            } else {
                scoreboard.createTeam(s);
            }

            a(icommandlistener, this, "commands.scoreboard.teams.add.success", new Object[] { s});
        }
    }

    protected void d(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        ScoreboardTeam scoreboardteam = this.e(astring[i++]);

        if (scoreboardteam != null) {
            String s = astring[i++].toLowerCase();

            if (!s.equalsIgnoreCase("color") && !s.equalsIgnoreCase("friendlyfire") && !s.equalsIgnoreCase("seeFriendlyInvisibles") && !s.equalsIgnoreCase("nametagVisibility") && !s.equalsIgnoreCase("deathMessageVisibility")) {
                throw new ExceptionUsage("commands.scoreboard.teams.option.usage", new Object[0]);
            } else if (astring.length == 4) {
                if (s.equalsIgnoreCase("color")) {
                    throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a(EnumChatFormat.a(true, false))});
                } else if (!s.equalsIgnoreCase("friendlyfire") && !s.equalsIgnoreCase("seeFriendlyInvisibles")) {
                    if (!s.equalsIgnoreCase("nametagVisibility") && !s.equalsIgnoreCase("deathMessageVisibility")) {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.usage", new Object[0]);
                    } else {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Object[]) ScoreboardTeamBase.EnumNameTagVisibility.a())});
                    }
                } else {
                    throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Collection) Arrays.asList(new String[] { "true", "false"}))});
                }
            } else {
                String s1 = astring[i];

                if (s.equalsIgnoreCase("color")) {
                    EnumChatFormat enumchatformat = EnumChatFormat.b(s1);

                    if (enumchatformat == null || enumchatformat.isFormat()) {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a(EnumChatFormat.a(true, false))});
                    }

                    scoreboardteam.a(enumchatformat);
                    scoreboardteam.setPrefix(enumchatformat.toString());
                    scoreboardteam.setSuffix(EnumChatFormat.RESET.toString());
                } else if (s.equalsIgnoreCase("friendlyfire")) {
                    if (!s1.equalsIgnoreCase("true") && !s1.equalsIgnoreCase("false")) {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Collection) Arrays.asList(new String[] { "true", "false"}))});
                    }

                    scoreboardteam.setAllowFriendlyFire(s1.equalsIgnoreCase("true"));
                } else if (s.equalsIgnoreCase("seeFriendlyInvisibles")) {
                    if (!s1.equalsIgnoreCase("true") && !s1.equalsIgnoreCase("false")) {
                        throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Collection) Arrays.asList(new String[] { "true", "false"}))});
                    }

                    scoreboardteam.setCanSeeFriendlyInvisibles(s1.equalsIgnoreCase("true"));
                } else {
                    ScoreboardTeamBase.EnumNameTagVisibility scoreboardteambase_enumnametagvisibility;

                    if (s.equalsIgnoreCase("nametagVisibility")) {
                        scoreboardteambase_enumnametagvisibility = ScoreboardTeamBase.EnumNameTagVisibility.a(s1);
                        if (scoreboardteambase_enumnametagvisibility == null) {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Object[]) ScoreboardTeamBase.EnumNameTagVisibility.a())});
                        }

                        scoreboardteam.setNameTagVisibility(scoreboardteambase_enumnametagvisibility);
                    } else if (s.equalsIgnoreCase("deathMessageVisibility")) {
                        scoreboardteambase_enumnametagvisibility = ScoreboardTeamBase.EnumNameTagVisibility.a(s1);
                        if (scoreboardteambase_enumnametagvisibility == null) {
                            throw new ExceptionUsage("commands.scoreboard.teams.option.noValue", new Object[] { s, a((Object[]) ScoreboardTeamBase.EnumNameTagVisibility.a())});
                        }

                        scoreboardteam.b(scoreboardteambase_enumnametagvisibility);
                    }
                }

                a(icommandlistener, this, "commands.scoreboard.teams.option.success", new Object[] { s, scoreboardteam.getName(), s1});
            }
        }
    }

    protected void e(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        ScoreboardTeam scoreboardteam = this.e(astring[i]);

        if (scoreboardteam != null) {
            scoreboard.removeTeam(scoreboardteam);
            a(icommandlistener, this, "commands.scoreboard.teams.remove.success", new Object[] { scoreboardteam.getName()});
        }
    }

    protected void f(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();

        if (astring.length > i) {
            ScoreboardTeam scoreboardteam = this.e(astring[i]);

            if (scoreboardteam == null) {
                return;
            }

            Collection collection = scoreboardteam.getPlayerNameSet();

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, collection.size());
            if (collection.size() <= 0) {
                throw new CommandException("commands.scoreboard.teams.list.player.empty", new Object[] { scoreboardteam.getName()});
            }

            ChatMessage chatmessage = new ChatMessage("commands.scoreboard.teams.list.player.count", new Object[] { Integer.valueOf(collection.size()), scoreboardteam.getName()});

            chatmessage.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage);
            icommandlistener.sendMessage(new ChatComponentText(a(collection.toArray())));
        } else {
            Collection collection1 = scoreboard.getTeams();

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, collection1.size());
            if (collection1.size() <= 0) {
                throw new CommandException("commands.scoreboard.teams.list.empty", new Object[0]);
            }

            ChatMessage chatmessage1 = new ChatMessage("commands.scoreboard.teams.list.count", new Object[] { Integer.valueOf(collection1.size())});

            chatmessage1.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage1);
            Iterator iterator = collection1.iterator();

            while (iterator.hasNext()) {
                ScoreboardTeam scoreboardteam1 = (ScoreboardTeam) iterator.next();

                icommandlistener.sendMessage(new ChatMessage("commands.scoreboard.teams.list.entry", new Object[] { scoreboardteam1.getName(), scoreboardteam1.getDisplayName(), Integer.valueOf(scoreboardteam1.getPlayerNameSet().size())}));
            }
        }

    }

    protected void g(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        String s = astring[i++];
        HashSet hashset = Sets.newHashSet();
        HashSet hashset1 = Sets.newHashSet();
        String s1;

        if (icommandlistener instanceof EntityHuman && i == astring.length) {
            s1 = b(icommandlistener).getName();
            if (scoreboard.addPlayerToTeam(s1, s)) {
                hashset.add(s1);
            } else {
                hashset1.add(s1);
            }
        } else {
            while (i < astring.length) {
                s1 = astring[i++];
                if (s1.startsWith("@")) {
                    List list = c(icommandlistener, s1);
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();
                        if (!entity.world.tacoSpigotConfig.nonPlayerEntitiesOnScoreboards && !(entity instanceof EntityHuman)) continue; // TacoSpigot
                        String s2 = e(icommandlistener, entity.getUniqueID().toString());

                        if (scoreboard.addPlayerToTeam(s2, s)) {
                            hashset.add(s2);
                        } else {
                            hashset1.add(s2);
                        }
                    }
                } else {
                    String s3 = e(icommandlistener, s1);

                    if (scoreboard.addPlayerToTeam(s3, s)) {
                        hashset.add(s3);
                    } else {
                        hashset1.add(s3);
                    }
                }
            }
        }

        if (!hashset.isEmpty()) {
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, hashset.size());
            a(icommandlistener, this, "commands.scoreboard.teams.join.success", new Object[] { Integer.valueOf(hashset.size()), s, a(hashset.toArray(new String[hashset.size()]))});
        }

        if (!hashset1.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.join.failure", new Object[] { Integer.valueOf(hashset1.size()), s, a(hashset1.toArray(new String[hashset1.size()]))});
        }
    }

    protected void h(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        HashSet hashset = Sets.newHashSet();
        HashSet hashset1 = Sets.newHashSet();
        String s;

        if (icommandlistener instanceof EntityHuman && i == astring.length) {
            s = b(icommandlistener).getName();
            if (scoreboard.removePlayerFromTeam(s)) {
                hashset.add(s);
            } else {
                hashset1.add(s);
            }
        } else {
            while (i < astring.length) {
                s = astring[i++];
                if (s.startsWith("@")) {
                    List list = c(icommandlistener, s);
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();
                        String s1 = e(icommandlistener, entity.getUniqueID().toString());

                        if (scoreboard.removePlayerFromTeam(s1)) {
                            hashset.add(s1);
                        } else {
                            hashset1.add(s1);
                        }
                    }
                } else {
                    String s2 = e(icommandlistener, s);

                    if (scoreboard.removePlayerFromTeam(s2)) {
                        hashset.add(s2);
                    } else {
                        hashset1.add(s2);
                    }
                }
            }
        }

        if (!hashset.isEmpty()) {
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, hashset.size());
            a(icommandlistener, this, "commands.scoreboard.teams.leave.success", new Object[] { Integer.valueOf(hashset.size()), a(hashset.toArray(new String[hashset.size()]))});
        }

        if (!hashset1.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.leave.failure", new Object[] { Integer.valueOf(hashset1.size()), a(hashset1.toArray(new String[hashset1.size()]))});
        }
    }

    protected void i(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        ScoreboardTeam scoreboardteam = this.e(astring[i]);

        if (scoreboardteam != null) {
            ArrayList arraylist = Lists.newArrayList(scoreboardteam.getPlayerNameSet());

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.AFFECTED_ENTITIES, arraylist.size());
            if (arraylist.isEmpty()) {
                throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", new Object[] { scoreboardteam.getName()});
            } else {
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    scoreboard.removePlayerFromTeam(s, scoreboardteam);
                }

                a(icommandlistener, this, "commands.scoreboard.teams.empty.success", new Object[] { Integer.valueOf(arraylist.size()), scoreboardteam.getName()});
            }
        }
    }

    protected void h(ICommandListener icommandlistener, String s) throws CommandException {
        Scoreboard scoreboard = this.d();
        ScoreboardObjective scoreboardobjective = this.a(s, false);

        scoreboard.unregisterObjective(scoreboardobjective);
        a(icommandlistener, this, "commands.scoreboard.objectives.remove.success", new Object[] { s});
    }

    protected void d(ICommandListener icommandlistener) throws CommandException {
        Scoreboard scoreboard = this.d();
        Collection collection = scoreboard.getObjectives();

        if (collection.size() <= 0) {
            throw new CommandException("commands.scoreboard.objectives.list.empty", new Object[0]);
        } else {
            ChatMessage chatmessage = new ChatMessage("commands.scoreboard.objectives.list.count", new Object[] { Integer.valueOf(collection.size())});

            chatmessage.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                icommandlistener.sendMessage(new ChatMessage("commands.scoreboard.objectives.list.entry", new Object[] { scoreboardobjective.getName(), scoreboardobjective.getDisplayName(), scoreboardobjective.getCriteria().getName()}));
            }

        }
    }

    protected void j(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        String s = astring[i++];
        int j = Scoreboard.getSlotForName(s);
        ScoreboardObjective scoreboardobjective = null;

        if (astring.length == 4) {
            scoreboardobjective = this.a(astring[i], false);
        }

        if (j < 0) {
            throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", new Object[] { s});
        } else {
            scoreboard.setDisplaySlot(j, scoreboardobjective);
            if (scoreboardobjective != null) {
                a(icommandlistener, this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[] { Scoreboard.getSlotName(j), scoreboardobjective.getName()});
            } else {
                a(icommandlistener, this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[] { Scoreboard.getSlotName(j)});
            }

        }
    }

    protected void k(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();

        if (astring.length > i) {
            String s = e(icommandlistener, astring[i]);
            Map map = scoreboard.getPlayerObjectives(s);

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, map.size());
            if (map.size() <= 0) {
                throw new CommandException("commands.scoreboard.players.list.player.empty", new Object[] { s});
            }

            ChatMessage chatmessage = new ChatMessage("commands.scoreboard.players.list.player.count", new Object[] { Integer.valueOf(map.size()), s});

            chatmessage.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage);
            Iterator iterator = map.values().iterator();

            while (iterator.hasNext()) {
                ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next();

                icommandlistener.sendMessage(new ChatMessage("commands.scoreboard.players.list.player.entry", new Object[] { Integer.valueOf(scoreboardscore.getScore()), scoreboardscore.getObjective().getDisplayName(), scoreboardscore.getObjective().getName()}));
            }
        } else {
            Collection collection = scoreboard.getPlayers();

            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, collection.size());
            if (collection.size() <= 0) {
                throw new CommandException("commands.scoreboard.players.list.empty", new Object[0]);
            }

            ChatMessage chatmessage1 = new ChatMessage("commands.scoreboard.players.list.count", new Object[] { Integer.valueOf(collection.size())});

            chatmessage1.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage1);
            icommandlistener.sendMessage(new ChatComponentText(a(collection.toArray())));
        }

    }

    protected void l(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        String s = astring[i - 1];
        int j = i;
        String s1 = e(icommandlistener, astring[i++]);

        if (s1.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s1, Integer.valueOf(40)});
        } else {
            ScoreboardObjective scoreboardobjective = this.a(astring[i++], true);
            int k = s.equalsIgnoreCase("set") ? a(astring[i++]) : a(astring[i++], 0);

            if (astring.length > i) {
                Entity entity = b(icommandlistener, astring[j]);

                try {
                    NBTTagCompound nbttagcompound = MojangsonParser.parse(a(astring, i));
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    entity.e(nbttagcompound1);
                    if (!GameProfileSerializer.a(nbttagcompound, nbttagcompound1, true)) {
                        throw new CommandException("commands.scoreboard.players.set.tagMismatch", new Object[] { s1});
                    }
                } catch (MojangsonParseException mojangsonparseexception) {
                    throw new CommandException("commands.scoreboard.players.set.tagError", new Object[] { mojangsonparseexception.getMessage()});
                }
            }

            Scoreboard scoreboard = this.d();
            ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s1, scoreboardobjective);

            if (s.equalsIgnoreCase("set")) {
                scoreboardscore.setScore(k);
            } else if (s.equalsIgnoreCase("add")) {
                scoreboardscore.addScore(k);
            } else {
                scoreboardscore.removeScore(k);
            }

            a(icommandlistener, this, "commands.scoreboard.players.set.success", new Object[] { scoreboardobjective.getName(), s1, Integer.valueOf(scoreboardscore.getScore())});
        }
    }

    protected void m(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        String s = e(icommandlistener, astring[i++]);

        if (astring.length > i) {
            ScoreboardObjective scoreboardobjective = this.a(astring[i++], false);

            scoreboard.resetPlayerScores(s, scoreboardobjective);
            a(icommandlistener, this, "commands.scoreboard.players.resetscore.success", new Object[] { scoreboardobjective.getName(), s});
        } else {
            scoreboard.resetPlayerScores(s, (ScoreboardObjective) null);
            a(icommandlistener, this, "commands.scoreboard.players.reset.success", new Object[] { s});
        }

    }

    protected void n(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        String s = d(icommandlistener, astring[i++]);

        if (s.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s, Integer.valueOf(40)});
        } else {
            ScoreboardObjective scoreboardobjective = this.a(astring[i], false);

            if (scoreboardobjective.getCriteria() != IScoreboardCriteria.c) {
                throw new CommandException("commands.scoreboard.players.enable.noTrigger", new Object[] { scoreboardobjective.getName()});
            } else {
                ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s, scoreboardobjective);

                scoreboardscore.a(false);
                a(icommandlistener, this, "commands.scoreboard.players.enable.success", new Object[] { scoreboardobjective.getName(), s});
            }
        }
    }

    protected void o(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        String s = e(icommandlistener, astring[i++]);

        if (s.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s, Integer.valueOf(40)});
        } else {
            ScoreboardObjective scoreboardobjective = this.a(astring[i++], false);

            if (!scoreboard.b(s, scoreboardobjective)) {
                throw new CommandException("commands.scoreboard.players.test.notFound", new Object[] { scoreboardobjective.getName(), s});
            } else {
                int j = astring[i].equals("*") ? Integer.MIN_VALUE : a(astring[i]);

                ++i;
                int k = i < astring.length && !astring[i].equals("*") ? a(astring[i], j) : Integer.MAX_VALUE;
                ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s, scoreboardobjective);

                if (scoreboardscore.getScore() >= j && scoreboardscore.getScore() <= k) {
                    a(icommandlistener, this, "commands.scoreboard.players.test.success", new Object[] { Integer.valueOf(scoreboardscore.getScore()), Integer.valueOf(j), Integer.valueOf(k)});
                } else {
                    throw new CommandException("commands.scoreboard.players.test.failed", new Object[] { Integer.valueOf(scoreboardscore.getScore()), Integer.valueOf(j), Integer.valueOf(k)});
                }
            }
        }
    }

    protected void p(ICommandListener icommandlistener, String[] astring, int i) throws CommandException {
        Scoreboard scoreboard = this.d();
        String s = e(icommandlistener, astring[i++]);
        ScoreboardObjective scoreboardobjective = this.a(astring[i++], true);
        String s1 = astring[i++];
        String s2 = e(icommandlistener, astring[i++]);
        ScoreboardObjective scoreboardobjective1 = this.a(astring[i], false);

        if (s.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s, Integer.valueOf(40)});
        } else if (s2.length() > 40) {
            throw new ExceptionInvalidSyntax("commands.scoreboard.players.name.tooLong", new Object[] { s2, Integer.valueOf(40)});
        } else {
            ScoreboardScore scoreboardscore = scoreboard.getPlayerScoreForObjective(s, scoreboardobjective);

            if (!scoreboard.b(s2, scoreboardobjective1)) {
                throw new CommandException("commands.scoreboard.players.operation.notFound", new Object[] { scoreboardobjective1.getName(), s2});
            } else {
                ScoreboardScore scoreboardscore1 = scoreboard.getPlayerScoreForObjective(s2, scoreboardobjective1);

                if (s1.equals("+=")) {
                    scoreboardscore.setScore(scoreboardscore.getScore() + scoreboardscore1.getScore());
                } else if (s1.equals("-=")) {
                    scoreboardscore.setScore(scoreboardscore.getScore() - scoreboardscore1.getScore());
                } else if (s1.equals("*=")) {
                    scoreboardscore.setScore(scoreboardscore.getScore() * scoreboardscore1.getScore());
                } else if (s1.equals("/=")) {
                    if (scoreboardscore1.getScore() != 0) {
                        scoreboardscore.setScore(scoreboardscore.getScore() / scoreboardscore1.getScore());
                    }
                } else if (s1.equals("%=")) {
                    if (scoreboardscore1.getScore() != 0) {
                        scoreboardscore.setScore(scoreboardscore.getScore() % scoreboardscore1.getScore());
                    }
                } else if (s1.equals("=")) {
                    scoreboardscore.setScore(scoreboardscore1.getScore());
                } else if (s1.equals("<")) {
                    scoreboardscore.setScore(Math.min(scoreboardscore.getScore(), scoreboardscore1.getScore()));
                } else if (s1.equals(">")) {
                    scoreboardscore.setScore(Math.max(scoreboardscore.getScore(), scoreboardscore1.getScore()));
                } else {
                    if (!s1.equals("><")) {
                        throw new CommandException("commands.scoreboard.players.operation.invalidOperation", new Object[] { s1});
                    }

                    int j = scoreboardscore.getScore();

                    scoreboardscore.setScore(scoreboardscore1.getScore());
                    scoreboardscore1.setScore(j);
                }

                a(icommandlistener, this, "commands.scoreboard.players.operation.success", new Object[0]);
            }
        }
    }

    public List<String> tabComplete(ICommandListener icommandlistener, String[] astring, BlockPosition blockposition) {
        if (astring.length == 1) {
            return a(astring, new String[] { "objectives", "players", "teams"});
        } else {
            if (astring[0].equalsIgnoreCase("objectives")) {
                if (astring.length == 2) {
                    return a(astring, new String[] { "list", "add", "remove", "setdisplay"});
                }

                if (astring[1].equalsIgnoreCase("add")) {
                    if (astring.length == 4) {
                        Set set = IScoreboardCriteria.criteria.keySet();

                        return a(astring, (Collection) set);
                    }
                } else if (astring[1].equalsIgnoreCase("remove")) {
                    if (astring.length == 3) {
                        return a(astring, (Collection) this.a(false));
                    }
                } else if (astring[1].equalsIgnoreCase("setdisplay")) {
                    if (astring.length == 3) {
                        return a(astring, Scoreboard.h());
                    }

                    if (astring.length == 4) {
                        return a(astring, (Collection) this.a(false));
                    }
                }
            } else if (astring[0].equalsIgnoreCase("players")) {
                if (astring.length == 2) {
                    return a(astring, new String[] { "set", "add", "remove", "reset", "list", "enable", "test", "operation"});
                }

                if (!astring[1].equalsIgnoreCase("set") && !astring[1].equalsIgnoreCase("add") && !astring[1].equalsIgnoreCase("remove") && !astring[1].equalsIgnoreCase("reset")) {
                    if (astring[1].equalsIgnoreCase("enable")) {
                        if (astring.length == 3) {
                            return a(astring, MinecraftServer.getServer().getPlayers());
                        }

                        if (astring.length == 4) {
                            return a(astring, (Collection) this.e());
                        }
                    } else if (!astring[1].equalsIgnoreCase("list") && !astring[1].equalsIgnoreCase("test")) {
                        if (astring[1].equalsIgnoreCase("operation")) {
                            if (astring.length == 3) {
                                return a(astring, this.d().getPlayers());
                            }

                            if (astring.length == 4) {
                                return a(astring, (Collection) this.a(true));
                            }

                            if (astring.length == 5) {
                                return a(astring, new String[] { "+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><"});
                            }

                            if (astring.length == 6) {
                                return a(astring, MinecraftServer.getServer().getPlayers());
                            }

                            if (astring.length == 7) {
                                return a(astring, (Collection) this.a(false));
                            }
                        }
                    } else {
                        if (astring.length == 3) {
                            return a(astring, this.d().getPlayers());
                        }

                        if (astring.length == 4 && astring[1].equalsIgnoreCase("test")) {
                            return a(astring, (Collection) this.a(false));
                        }
                    }
                } else {
                    if (astring.length == 3) {
                        return a(astring, MinecraftServer.getServer().getPlayers());
                    }

                    if (astring.length == 4) {
                        return a(astring, (Collection) this.a(true));
                    }
                }
            } else if (astring[0].equalsIgnoreCase("teams")) {
                if (astring.length == 2) {
                    return a(astring, new String[] { "add", "remove", "join", "leave", "empty", "list", "option"});
                }

                if (astring[1].equalsIgnoreCase("join")) {
                    if (astring.length == 3) {
                        return a(astring, this.d().getTeamNames());
                    }

                    if (astring.length >= 4) {
                        return a(astring, MinecraftServer.getServer().getPlayers());
                    }
                } else {
                    if (astring[1].equalsIgnoreCase("leave")) {
                        return a(astring, MinecraftServer.getServer().getPlayers());
                    }

                    if (!astring[1].equalsIgnoreCase("empty") && !astring[1].equalsIgnoreCase("list") && !astring[1].equalsIgnoreCase("remove")) {
                        if (astring[1].equalsIgnoreCase("option")) {
                            if (astring.length == 3) {
                                return a(astring, this.d().getTeamNames());
                            }

                            if (astring.length == 4) {
                                return a(astring, new String[] { "color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"});
                            }

                            if (astring.length == 5) {
                                if (astring[3].equalsIgnoreCase("color")) {
                                    return a(astring, EnumChatFormat.a(true, false));
                                }

                                if (astring[3].equalsIgnoreCase("nametagVisibility") || astring[3].equalsIgnoreCase("deathMessageVisibility")) {
                                    return a(astring, ScoreboardTeamBase.EnumNameTagVisibility.a());
                                }

                                if (astring[3].equalsIgnoreCase("friendlyfire") || astring[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                                    return a(astring, new String[] { "true", "false"});
                                }
                            }
                        }
                    } else if (astring.length == 3) {
                        return a(astring, this.d().getTeamNames());
                    }
                }
            }

            return null;
        }
    }

    protected List<String> a(boolean flag) {
        Collection collection = this.d().getObjectives();
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

            if (!flag || !scoreboardobjective.getCriteria().isReadOnly()) {
                arraylist.add(scoreboardobjective.getName());
            }
        }

        return arraylist;
    }

    protected List<String> e() {
        Collection collection = this.d().getObjectives();
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

            if (scoreboardobjective.getCriteria() == IScoreboardCriteria.c) {
                arraylist.add(scoreboardobjective.getName());
            }
        }

        return arraylist;
    }

    public boolean isListStart(String[] astring, int i) {
        return !astring[0].equalsIgnoreCase("players") ? (astring[0].equalsIgnoreCase("teams") ? i == 2 : false) : (astring.length > 1 && astring[1].equalsIgnoreCase("operation") ? i == 2 || i == 5 : i == 2);
    }
}
