package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import io.github.solclient.client.event.impl.ReceiveChatMessageEvent;
import io.github.solclient.client.event.impl.ScoreboardRenderEvent;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades.BedwarsTeamUpgrades;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class BedwarsGame {

    private BedwarsTeam won = null;
    private int wonTick = -1;
    private int seconds = 0;
    private Text topBarText = new LiteralText("");


    private BedwarsPlayer me = null;

    // Use a treemap here for the O(log(n)) time
    private final Map<String, BedwarsPlayer> players = new TreeMap<>();
    private final MinecraftClient mc;
    @Getter
    private boolean started = false;
    private final BedwarsMod mod;
    @Getter
    private final BedwarsTeamUpgrades upgrades = new BedwarsTeamUpgrades();


    public BedwarsGame(BedwarsMod mod) {
        mc = MinecraftClient.getInstance();
        this.mod = mod;
    }

    public void onStart() {
        debug("Game started");
        players.clear();
        Map<BedwarsTeam, List<PlayerListEntry>> teamPlayers = new HashMap<>();
        int maxLength = 1;
        for (PlayerListEntry player : mc.player.networkHandler.getPlayerList()) {
            String name = mc.inGameHud.getPlayerListWidget().getPlayerName(player).replaceAll("§.", "");
            if (name.charAt(1) != ' ') {
                continue;
            }
            BedwarsTeam team = BedwarsTeam.fromPrefix(name.charAt(0)).orElse(null);
            if (team == null) {
                continue;
            }
            teamPlayers.compute(team, (t, entries) -> {
                if (entries == null) {
                    List<PlayerListEntry> players = new ArrayList<>();
                    players.add(player);
                    return players;
                }
                entries.add(player);
                return entries;
            });
        }
        for (Map.Entry<BedwarsTeam, List<PlayerListEntry>> teamPlayerList : teamPlayers.entrySet()) {
            teamPlayerList.getValue().sort(Comparator.comparing(p -> p.getProfile().getName()));
            List<PlayerListEntry> value = teamPlayerList.getValue();
            for (int i = 0; i < value.size(); i++) {
                PlayerListEntry e = value.get(i);
                BedwarsPlayer p = new BedwarsPlayer(teamPlayerList.getKey(), e, i + 1);
                maxLength = Math.max(e.getProfile().getName().length(), maxLength);
                if (mc.player.getGameProfile().getName().equals(e.getProfile().getName())) {
                    me = p;
                }
                players.put(e.getProfile().getName(), p);
            }
        }
        mod.gameLog.gameStart(maxLength);
        this.started = true;
    }

    public Text getTopBarText() {
        return topBarText;
    }

    private String calculateTopBarText() {
        return getFormattedTime();
    }

    public String getFormattedTime() {
        int minute = seconds / 60;
        int second = seconds % 60;
        String time = minute + ":";
        if (second < 10) {
            time += "0" + second;
        } else {
            time += second;
        }
        return time;
    }

    public Optional<BedwarsPlayer> getPlayer(String name) {
        return Optional.ofNullable(players.getOrDefault(name, null));
    }

    private void debug(String message) {
        mc.inGameHud.getChatHud().addMessage(new LiteralText("§b§lINFO:§8 " + message));
    }

    private void died(ReceiveChatMessageEvent event, BedwarsPlayer player, @Nullable BedwarsPlayer killer, BedwarsDeathType type, boolean finalDeath) {
        player.died();
        if (killer != null) {
            killer.killed(finalDeath);
        }
        event.newMessage = formatDeath(player, killer, type, finalDeath);
    }

    private String formatDeath(BedwarsPlayer player, @Nullable BedwarsPlayer killer, BedwarsDeathType type, boolean finalDeath) {
        String time = "§7" + mod.getGame().get().getFormattedTime() + " ";
        String inner = type.getInner();
        if (finalDeath) {
            inner = "§6§b/" + inner.toUpperCase(Locale.ROOT) + "/";
        } else {
            inner = "§7/" + inner + "/";
        }
        String playerFormatted = getPlayerFormatted(player);
        if (killer == null) {
            return time + playerFormatted + " " + inner;
        }
        String killerFormatted = getPlayerFormatted(killer);
        return time + playerFormatted + " " + inner + " " + killerFormatted;
    }

    private String getPlayerFormatted(BedwarsPlayer player) {
        return player.getColoredTeamNumber() + " " + player.getProfile().getProfile().getName();
    }

    public boolean isTeamEliminated(BedwarsTeam team) {
        return players.values().stream().filter(b -> b.getTeam() == team).allMatch(BedwarsPlayer::isFinalKilled);
    }

    public void onChatMessage(String rawMessage, ReceiveChatMessageEvent event) {
        try {
            if (BedwarsMessages.matched(BedwarsMessages.ANNOYING_MESSAGES, rawMessage).isPresent()) {
                event.cancelled = true;
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.SELF_VOID, rawMessage, m -> {
                died(m, rawMessage, event, BedwarsDeathType.SELF_VOID);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.SELF_UNKNOWN, rawMessage, m -> {
                died(m, rawMessage, event, BedwarsDeathType.SELF_UNKNOWN);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.COMBAT_KILL, rawMessage, m -> {
                died(m, rawMessage, event, BedwarsDeathType.COMBAT);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.VOID_KILL, rawMessage, m -> {
                died(m, rawMessage, event, BedwarsDeathType.VOID);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.PROJECTILE_KILL, rawMessage, m -> {
                died(m, rawMessage, event, BedwarsDeathType.PROJECTILE);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.FALL_KILL, rawMessage, m -> {
                died(m, rawMessage, event, BedwarsDeathType.FALL);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.GOLEM_KILL, rawMessage, m -> {
                died(m, rawMessage, event, BedwarsDeathType.GOLEM);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.BED_DESTROY, rawMessage, m -> {
                BedwarsPlayer player = BedwarsMessages.matched(BedwarsMessages.BED_BREAK, rawMessage).flatMap(m1 -> getPlayer(m1.group(1))).orElse(null);
                BedwarsTeam.fromName(m.group(1)).ifPresent(t -> bedDestroyed(t, player));

            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.DISCONNECT, rawMessage, m -> getPlayer(m.group(1)).ifPresent(this::disconnected))) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.RECONNECT, rawMessage, m -> getPlayer(m.group(1)).ifPresent(this::reconnected))) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.GAME_END, rawMessage, m -> {
                BedwarsTeam win = players.values().stream().filter(p -> !p.isFinalKilled()).findFirst().map(BedwarsPlayer::getTeam).orElse(null);
                this.won = win;
                this.wonTick = mc.inGameHud.getTicks() + 10;
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.TEAM_ELIMINATED, rawMessage, m -> BedwarsTeam.fromName(m.group(1)).ifPresent(this::teamEliminated))) {
                return;
            }
            upgrades.onMessage(rawMessage);
        } catch (Exception e) {
            debug("Error: " + e);
        }
    }

    private void died(Matcher m, String rawMessage, ReceiveChatMessageEvent event, BedwarsDeathType type) {
        BedwarsPlayer killed = getPlayer(m.group(1)).orElse(null);
        BedwarsPlayer killer = null;
        if (type != BedwarsDeathType.SELF_UNKNOWN && type != BedwarsDeathType.SELF_VOID) {
            killer = getPlayer(m.group(2)).orElse(null);
        }
        if (killed == null) {
            debug("Player " + m.group(1) + " was not found");
            return;
        }
        died(event, killed, killer, type, BedwarsMessages.matched(BedwarsMessages.FINAL_KILL, rawMessage).isPresent());
    }

    private void gameEnd(BedwarsTeam win) {
        if (me == null) {
            BedwarsMod.getInstance().gameEnd();
            return;
        }

        for (BedwarsPlayer p : players.values()) {
            if (p.getStats() != null) {
                mc.inGameHud.getChatHud().addMessage(new LiteralText(p.getProfile().getProfile().getName() + " - " + p.getStats().getWinstreak()));
            }
        }

        BedwarsMod.getInstance().gameEnd();
    }

    private void teamEliminated(BedwarsTeam team) {
        // Make sure everyone is dead, just in case
        players.values().stream().filter(b -> b.getTeam() == team).forEach(b -> {
            b.setBed(false);
            b.died();
        });
    }

    private void bedDestroyed(BedwarsTeam team, @Nullable BedwarsPlayer breaker) {
        players.values().stream().filter(b -> b.getTeam() == team).forEach(b -> b.setBed(false));
    }

    private void disconnected(BedwarsPlayer player) {
        player.disconnected();
    }

    private void reconnected(BedwarsPlayer player) {
        player.reconnected();
    }

    public void onScoreboardRender(ScoreboardRenderEvent event) {
        Scoreboard scoreboard = event.objective.getScoreboard();
        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(event.objective);
        List<ScoreboardPlayerScore> filteredScores = scores.stream()
                                                           .filter(p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#"))
                                                           .collect(Collectors.toList());
        Collections.reverse(filteredScores);
        if (filteredScores.size() < 3) {
            return;
        }
        ScoreboardPlayerScore score = filteredScores.get(2);
        Team team = scoreboard.getPlayerTeam(score.getPlayerName());
        String timer = Team.decorateName(team, score.getPlayerName());
        if (!timer.contains(":")) {
            return;
        }
        int seconds;
        try {
            seconds = Integer.parseInt(timer.split(":")[1].substring(0, 2));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int target = (60 - seconds) % 60;
        if (this.seconds % 60 != target) {
            // Update seconds
            while (this.seconds % 60 != target) {
                this.seconds++;
            }
            topBarText = new LiteralText(calculateTopBarText());
        }
    }

    public void tick() {
        int currentTick = mc.inGameHud.getTicks();
        if (won != null && currentTick >= wonTick) {
            gameEnd(won);
        }
        players.values().forEach(p -> p.tick(currentTick));
    }

    public void updateEntries(List<PlayerListEntry> entries) {
        // Update latencies and other information for entries
        entries.forEach(entry ->
                getPlayer(entry.getProfile().getName()).ifPresent(player -> player.updateListEntry(entry))
        );
    }

    public List<PlayerListEntry> getTabPlayerList(List<PlayerListEntry> original) {
        updateEntries(original);
        return players.values().stream().filter(b -> !b.isFinalKilled()).sorted((b1, b2) -> {
            if (b1.getTeam() == b2.getTeam()) {
                return Integer.compare(b1.getNumber(), b2.getNumber());
            }
            return Integer.compare(b1.getTeam().ordinal(), b2.getTeam().ordinal());
        }).map(BedwarsPlayer::getProfile).collect(Collectors.toList());
    }

}
