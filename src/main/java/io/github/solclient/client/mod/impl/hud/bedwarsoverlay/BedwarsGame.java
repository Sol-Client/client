/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

public class BedwarsGame {

    private static final int DIAMOND_START = 30;
    private static final int DIAMOND_1 = 30;
    private static final int DIAMOND_2 = 23;
    private static final int DIAMOND_3 = 16;
    private static final int EMERALD_START = 30;
    private static final int EMERALD_1 = 65;
    private static final int EMERALD_2 = 50;
    private static final int EMERALD_3 = 35;

    private int diamondsTimer = DIAMOND_START;
    private int emeraldsTimer = EMERALD_START;

    private BedwarsTeam won = null;
    private int wonTick = -1;
    private int seconds = 0;
    private Text topBarText = new LiteralText("");
    private Text bottomBarText = new LiteralText("");


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
        mod.upgradesOverlay.onStart(upgrades);
        players.clear();
        Map<BedwarsTeam, List<PlayerListEntry>> teamPlayers = new HashMap<>();
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
                if (mc.player.getGameProfile().getName().equals(e.getProfile().getName())) {
                    me = p;
                }
                players.put(e.getProfile().getName(), p);
            }
        }
        this.started = true;
    }

    public Text getTopBarText() {
        return topBarText;
    }

    public Text getBottomBarText() {
        return bottomBarText;
    }

    private String calculateTopBarText() {
        return getFormattedTime();
    }

    private String calculateBottomBarText() {
        return "§bDiamonds - " + diamondsTimer + " §8| " + "§aEmeralds - " + emeraldsTimer;
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
        if (mod.overrideMessages) {
            event.newMessage = new LiteralText(formatDeath(player, killer, type, finalDeath));
        }
    }

    private String formatDisconnect(BedwarsPlayer disconnected) {
        String playerFormatted = getPlayerFormatted(disconnected);
        return playerFormatted + " §7§o/disconnected/";
    }

    private String formatReconnect(BedwarsPlayer reconnected) {
        String playerFormatted = getPlayerFormatted(reconnected);
        return playerFormatted + " §7§o/reconnected/";
    }

    private String formatEliminated(BedwarsTeam team) {
        return "§6§l§oTEAM ELMINATED §8§l> " + team.getColorSection() + team.getName() + " Team §7/eliminated/ ";
    }

    private String formatBed(BedwarsTeam team, BedwarsPlayer breaker) {
        String playerFormatted = getPlayerFormatted(breaker);
        return "§6§l§oBED BROKEN §8§l> " + team.getColorSection() + team.getName() + " Bed §7/broken/ " + playerFormatted +
                (breaker.getStats() == null ? "" : " §6" + breaker.getStats().getBedsBroken());
    }

    private String formatDeath(BedwarsPlayer player, @Nullable BedwarsPlayer killer, BedwarsDeathType type, boolean finalDeath) {
        String inner = type.getInner();
        if (finalDeath) {
            inner = "§6§l/" + inner.toUpperCase(Locale.ROOT) + "/";
        } else {
            inner = "§7/" + inner + "/";
        }
        String playerFormatted = getPlayerFormatted(player);
        if (killer == null) {
            return playerFormatted + " " + inner;
        }
        String killerFormatted = getPlayerFormatted(killer);
        if (finalDeath && killer.getStats() != null) {
            killerFormatted += " §6" + killer.getStats().getFinalKills();
        }
        return playerFormatted + " " + inner + " " + killerFormatted;
    }

    private String getPlayerFormatted(BedwarsPlayer player) {
        return player.getColoredTeamNumber() + " " + player.getProfile().getProfile().getName();
    }

    public boolean isTeamEliminated(BedwarsTeam team) {
        return players.values().stream().filter(b -> b.getTeam() == team).allMatch(BedwarsPlayer::isFinalKilled);
    }

    public void onChatMessage(String rawMessage, ReceiveChatMessageEvent event) {
        try {
            if (mod.removeAnnoyingMessages && BedwarsMessages.matched(BedwarsMessages.ANNOYING_MESSAGES, rawMessage).isPresent()) {
                event.cancelled = true;
                return;
            }
            if (BedwarsDeathType.getDeath(rawMessage, (type, m) -> {
                died(m, rawMessage, event, type);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.BED_DESTROY, rawMessage, m -> {
                BedwarsPlayer player = BedwarsMessages.matched(BedwarsMessages.BED_BREAK, rawMessage).flatMap(m1 -> getPlayer(m1.group(1))).orElse(null);
                BedwarsTeam team = BedwarsTeam.fromName(m.group(1)).orElse(me.getTeam());
                bedDestroyed(event, team, player);
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.DISCONNECT, rawMessage, m -> getPlayer(m.group(1)).ifPresent(p -> disconnected(event, p)))) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.RECONNECT, rawMessage, m -> getPlayer(m.group(1)).ifPresent(p -> reconnected(event, p)))) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.GAME_END, rawMessage, m -> {
                BedwarsTeam win = players.values().stream().filter(p -> !p.isFinalKilled()).findFirst().map(BedwarsPlayer::getTeam).orElse(null);
                this.won = win;
                this.wonTick = mc.inGameHud.getTicks() + 10;
            })) {
                return;
            }
            if (BedwarsMessages.matched(BedwarsMessages.TEAM_ELIMINATED, rawMessage, m -> BedwarsTeam.fromName(m.group(1)).ifPresent(t -> teamEliminated(event, t)))) {
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
            BedwarsMod.instance.gameEnd();
            return;
        }

        mc.inGameHud.getChatHud().addMessage(
                new LiteralText("§8§m----------[§7Winstreaks§8]----------")
        );
        for (BedwarsPlayer p : players.values()) {
            if (p.getStats() != null && p.getStats().getWinstreak() > 0) {
                boolean winner = p.getTeam().equals(win);
                int before = p.getStats().getWinstreak();
                int after = winner ? before + 1 : 0;
                mc.inGameHud.getChatHud().addMessage(
                        new LiteralText(
                                getPlayerFormatted(p) + "§8: §7" + before + " §8→ §" + (winner ? "a" : "c") + after
                        ));
            }
        }

        BedwarsMod.instance.gameEnd();
    }

    private void teamEliminated(ReceiveChatMessageEvent event, BedwarsTeam team) {
        // Make sure everyone is dead, just in case
        players.values().stream().filter(b -> b.getTeam() == team).forEach(b -> {
            b.setBed(false);
            b.died();
        });
        if (mod.overrideMessages) {
            event.newMessage = new LiteralText(formatEliminated(team));
        }
    }

    private void bedDestroyed(ReceiveChatMessageEvent event, BedwarsTeam team, @Nullable BedwarsPlayer breaker) {
        players.values().stream().filter(b -> b.getTeam() == team).forEach(b -> b.setBed(false));
        if (breaker != null && breaker.getStats() != null) {
            breaker.getStats().addBed();
        }
        if (mod.overrideMessages) {
            event.newMessage = new LiteralText(formatBed(team, breaker));
        }
    }

    private void disconnected(ReceiveChatMessageEvent event, BedwarsPlayer player) {
        player.disconnected();
        if (mod.overrideMessages) {
            event.newMessage = new LiteralText(formatDisconnect(player));
        }
    }


    private void reconnected(ReceiveChatMessageEvent event, BedwarsPlayer player) {
        player.reconnected();
        if (mod.overrideMessages) {
            event.newMessage = new LiteralText(formatReconnect(player));
        }
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
                updateClock();
            }
            topBarText = new LiteralText(calculateTopBarText());
            bottomBarText = new LiteralText(calculateBottomBarText());
        }
    }

    private int getDiamondTimerTier(int tier) {
        if (tier <= 1) {
            return DIAMOND_1;
        }
        if (tier == 2) {
            return DIAMOND_2;
        }
        return DIAMOND_3;
    }

    private int getEmeraldTier(int tier) {
        if (tier <= 1) {
            return EMERALD_1;
        }
        if (tier == 2) {
            return EMERALD_2;
        }
        return EMERALD_3;
    }

    private void updateClock() {
        // This just straight up doesn't work. I think it's because hypixel doesn't follow strict timings
        // Also the math on this is just wrong somewhere
        this.seconds++;
        int minutes = seconds / 60;
        int diamondTier = Math.min((minutes + 6) / 12 + 1, 3);
        int emeraldTier = Math.min(minutes / 12 + 1, 3);
        diamondsTimer--;
        emeraldsTimer--;
        if ((seconds % 60 == 0) && (minutes < 24 && minutes % 6 == 0)) {
		    if ((minutes % 12) / 6 == 1) {
		        // Diamonds
		        diamondsTimer = 0;
		    } else {
		        // Emeralds
		        emeraldsTimer = 0;
		    }
		}
        if (diamondsTimer <= 0) {
            int secondsTillUpgrade = (((minutes) / 12 + 1)) * 12 * 60 - 6 * 60 - seconds;
            diamondsTimer = Math.min(getDiamondTimerTier(diamondTier), secondsTillUpgrade);
        }
        if (emeraldsTimer <= 0) {
            int secondsTillUpgrade = ((minutes / 12 + 1)) * 12 * 60 - seconds;
            emeraldsTimer = Math.min(getEmeraldTier(emeraldTier), secondsTillUpgrade);
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
