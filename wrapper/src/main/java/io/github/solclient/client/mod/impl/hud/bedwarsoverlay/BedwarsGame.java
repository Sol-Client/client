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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BedwarsGame {

    private final static Pattern DISCONNECT = Pattern.compile("(\\b[A-Za-z0-9_§]{3,16}\\b) disconnected\\.$");
    private final static Pattern RECONNECT = Pattern.compile("(\\b[A-Za-z0-9_§]{3,16}\\b) reconnected\\.$");
    private final static Pattern FINAL_KILL = Pattern.compile("FINAL KILL!");
    private final static Pattern BED_DESTROY = Pattern.compile("^\\s*?BED DESTRUCTION > (\\w+) Bed");
    private final static Pattern TEAM_ELIMINATED = Pattern.compile("^\\s*?TEAM ELIMINATED > (\\w+) Team");

    private final static Pattern GAME_END = Pattern.compile("^ +1st Killer - ?\\[?\\w*\\+*\\]? \\w+ - \\d+(?: Kills?)?$");

    private final static Pattern[] DIED = {
            Pattern.compile(formatPlaceholder("^{killed} fell into the void.(?: FINAL KILL!)?\\s*?")),
            Pattern.compile(formatPlaceholder("^{killed} died.(?: FINAL KILL!)?\\s*?"))
    };

    private final static Pattern[] ANNOYING_MESSAGES = {
            Pattern.compile("^You will respawn in \\d* seconds!$"),
            Pattern.compile("^You purchased Wool$"),
            Pattern.compile("^Cross-teaming is not allowed"),
            Pattern.compile("^\\+\\d+ Coins!"),
            Pattern.compile("^\\+\\d+ coins!"),
            Pattern.compile("^Coins just earned DOUBLE"),
            Pattern.compile("^\\+\\d+ Bed Wars Experience"),
            Pattern.compile("^You have respawned"),
    };

    private final static Pattern[] BED_BREAK = {
            Pattern.compile(formatPlaceholder("Bed was broken by {player}")),
            Pattern.compile(formatPlaceholder("Bed was incinerated by {player}")),
            Pattern.compile(formatPlaceholder("Bed was iced by {player}")),
            Pattern.compile(formatPlaceholder("Bed had to raise the white flag to {player}")),
            Pattern.compile(formatPlaceholder("Bed was dismantled by {player}")),
            Pattern.compile(formatPlaceholder("Bed was deep fried by {player}")),
            Pattern.compile(formatPlaceholder("Bed was ripped apart by {player}")),
            Pattern.compile(formatPlaceholder("Bed was traded in for milk and cookies by {player}")),
            Pattern.compile(formatPlaceholder("Bed was sacrificed by {player}")),
            Pattern.compile(formatPlaceholder("Bed was gulped by {player}")),
            Pattern.compile(formatPlaceholder("Bed was gulped by {player}")),
            Pattern.compile(formatPlaceholder("Bed was squeaked apart by {player}")),
            Pattern.compile(formatPlaceholder("Bed was stung by {player}")),
            Pattern.compile(formatPlaceholder("Bed was impaled by {player}")),
            Pattern.compile(formatPlaceholder("Bed be shot with cannon by {player}")),
            Pattern.compile(formatPlaceholder("Bed got memed by {player}")),
            Pattern.compile(formatPlaceholder("Bed was made into a snowman by {player}")),
            Pattern.compile(formatPlaceholder("Bed was scrambled by {player}")),
            Pattern.compile(formatPlaceholder("Bed was stuffed with tissue paper by {player}")),
            Pattern.compile(formatPlaceholder("Bed was scrambled by {player}")),
            Pattern.compile(formatPlaceholder("Bed was bed #{number} destroyed by {player}")),
            Pattern.compile(formatPlaceholder("Bed was spooked by {player}")),
            Pattern.compile(formatPlaceholder("Bed was dreadfully corrupted by {player}")),
            Pattern.compile(formatPlaceholder("Bed was bed #{number} destroyed by {player}")),
            Pattern.compile(formatPlaceholder("Bed exploded from a firework by {player}")),
            Pattern.compile(formatPlaceholder("Bed was blasted to dust by {player}")),
            Pattern.compile(formatPlaceholder("Bed was melted by {player}'s holiday spirit")),
            Pattern.compile(formatPlaceholder("Bed was ripped to shreds by {player}")),
            Pattern.compile(formatPlaceholder("Bed has left the game after seeing {player}")),
            Pattern.compile(formatPlaceholder("Bed was spooked by {player}")),
            Pattern.compile(formatPlaceholder("Bed was contaminated by {player}")),
            Pattern.compile(formatPlaceholder("Bed was sold in a garage sale by {player}")),
    };

    private final static String[] KILLS = {
            "{killed} was struck down by {player}.",
            "{killed} was turned to dust by {player}.",
            "{killed} was melted by {player}.",
            "{killed} was turned to ash by {player}.",
            "{killed} was fried by {player}'s Golem.",
            "{killed} was filled full of lead by {player}.",
            "{killed} met their end by {player}.",
            "{killed} was killed with dynamite by {player}.",
            "{killed} lost a drinking contest with {player}.",
            "{killed} lost the draw to {player}'s Golem.",
            "{killed} died in close combat to {player}.",
            "{killed} fought to the edge with {player}.",
            "{killed} fell to the great marksmanship of {player}.",
            "{killed} stumbled off a ledge with help by {player}.",
            "{killed} tangoed with {player}'s Golem.",
            "{killed} was given the cold shoulder by {player}.",
            "{killed} was hit off by a love bomb from {player}.",
            "{killed} was struck with Cupid's arrow by {player}.",
            "{killed} was out of the league of {player}.",
            "{killed} was no match for {player}'s Golem.",
            "{killed} was glazed in BBQ sauce by {player}.",
            "{killed} slipped in BBQ sauce off the edge spilled by {player}.",
            "{killed} was thrown chili powder at by {player}.",
            "{killed} was not spicy enough for {player}.",
            "{killed} was sliced up by {player}'s Golem.",
            "{killed} was bitten by {player}.",
            "{killed} howled into the void for {player}.",
            "{killed} caught the ball thrown by {player}.",
            "{killed} was distracted by a puppy placed by {player}.",
            "{killed} played too rough with {player}'s Golem.",
            "{killed} was wrapped into a gift by {player}.",
            "{killed} hit the hard-wood floor because of {player}.",
            "{killed} was put on the naughty list by {player}.",
            "{killed} was pushed down a slope by {player}.",
            "{killed} was turned to gingerbread by {player}'s Golem.",
            "{killed} was hunted down by {player}.",
            "{killed} stumbled on a trap set by {player}.",
            "{killed} got skewered by {player}.",
            "{killed} was thrown into a volcano by {player}.",
            "{killed} was mauled by {player}'s Golem.",
            "{killed} was oinked by {player}.",
            "{killed} slipped into void for {player}.",
            "{killed} got attacked by a carrot from {player}.",
            "{killed} was distracted by a piglet from {player}.",
            "{killed} was oinked by {player}'s Golem.",
            "{killed} was chewed up by {player}.",
            "{killed} was scared into the void by {player}.",
            "{killed} stepped in a mouse trap placed by {player}.",
            "{killed} was distracted by a rat dragging pizza from {player}.",
            "{killed} squeaked around with {player}'s Golem.",
            "{killed} was buzzed to death by {player}.",
            "{killed} was bzzz'd into the void by {player}.",
            "{killed} was startled by {player}.",
            "{killed} was stung off the edge by {player}.",
            "{killed} was bee'd by {player}'s Golem.",
            "{killed} was trampled by {player}.",
            "{killed} was back kicked into the void by {player}.",
            "{killed} was impaled from a distance by {player}.",
            "{killed} was headbutted off a cliff by {player}.",
            "{killed} was trampled by {player}'s Golem.",
            "{killed} be sent to Davy Jones' locker by {player}.",
            "{killed} be cannonballed to death by {player}.",
            "{killed} be shot and killed by {player}.",
            "{killed} be killed with magic by {player}.",
            "{killed} be killed with metal by {player}'s Golem.",
            "{killed} got rekt by {player}.",
            "{killed} took the L to {player}.",
            "{killed} got smacked by {player}.",
            "{killed} got roasted by {player}.",
            "{killed} got bamboozled by {player}'s Golem.",
            "{killed} was locked outside during a snow storm by {player}.",
            "{killed} was pushed into a snowbank by {player}.",
            "{killed} was hit with a snowball from {player}.",
            "{killed} was shoved down an icy slope by {player}.",
            "{killed} got snowed in by {player}'s Golem.",
            "{killed} was painted pretty by {player}.",
            "{killed} was deviled into the void by {player}.",
            "{killed} slipped into a pan placed by {player}.",
            "{killed} was flipped off the edge by {player}.",
            "{killed} was made sunny side up by {player}'s Golem.",
            "{killed} was wrapped up by {player}.",
            "{killed} was tied into a bow by {player}.",
            "{killed} was glued up by {player}.",
            "{killed} tripped over a present placed by {player}.",
            "{killed} was taped together by {player}'s Golem.",
            "{killed} was stomped by {player}.",
            "{killed} was {player}'s final #{number}.",
            "{killed} was thrown down a pit by {player}.",
            "{killed} was shot by {player}.",
            "{killed} was thrown to the ground by {player}.",
            "{killed} was outclassed by {player}'s Golem.",
            "{killed} was spooked by {player}.",
            "{killed} was spooked off the map by {player}.",
            "{killed} was remotely spooked by {player}.",
            "{killed} was totally spooked by {player}.",
            "{killed} was spooked by {player}'s Golem.",
            "{killed} was tragically backstabbed by {player}.",
            "{killed} was heartlessly let go by {player}.",
            "{killed}'s heart was pierced by {player}.",
            "{killed} was delivered into nothingness by {player}.",
            "{killed} was dismembered by {player}'s Golem.",
            "{killed} was crushed by {player}.",
            "{killed} was {player}'s final #5,794.",
            "{killed} was dominated by {player}.",
            "{killed} was assassinated by {player}.",
            "{killed} was thrown off their high horse by {player}.",
            "{killed} was degraded by {player}'s Golem.",
            "{killed} was whacked with a party balloon by {player}.",
            "{killed} was popped into the void by {player}.",
            "{killed} was shot with a roman candle by {player}.",
            "{killed} was launched like a firework by {player}.",
            "{killed} was lit up by {player}'s Golem.",
            "{killed} was crushed into moon dust by {player}.",
            "{killed} was sent the wrong way by {player}.",
            "{killed} was hit by an asteroid from {player}.",
            "{killed} was blasted to the moon by {player}.",
            "{killed} was blown up by {player}'s Golem.",
            "{killed} was smothered in holiday cheer by {player}.",
            "{killed} was banished into the ether by {player}'s holiday spirit.",
            "{killed} was sniped by a missile of festivity by {player}.",
            "{killed} was pushed by {player}'s holiday spirit.",
            "{killed} was sung holiday tunes to by {player}'s Golem.",
            "{killed} was ripped to shreds by {player}.",
            "{killed} was charged by {player}.",
            "{killed} was pounced on by {player}.",
            "{killed} was ripped and thrown by {player}.",
            "{killed} was ripped to shreds by {player}'s Golem.",
            "{killed} was bested by {player}.",
            "{killed} was {player}'s final #5,794.",
            "{killed} was knocked into the void by {player}.",
            "{killed} was shot by {player}.",
            "{killed} was knocked off an edge by {player}.",
            "{killed} was bested by {player}'s Golem.",
            "{killed} had a small brain moment while fighting {player}.",
            "{killed} was not able to block clutch against {player}.",
            "{killed} got 360 no-scoped by {player}.",
            "{killed} forgot how many blocks they had left while fighting {player}.",
            "{killed} got absolutely destroyed by {player}'s Golem.",
            "{killed} was too shy to meet {player}.",
            "{killed} didn't distance themselves properly from {player}.",
            "{killed} was coughed at by {player}.",
            "{killed} tripped while trying to run away from {player}.",
            "{killed} got too close to {player}'s Golem.",
            "{killed} was yelled at by {player}.",
            "{killed} was thrown off the lawn by {player}.",
            "{killed} was accidentally spit on by {player}.",
            "{killed} slipped on the fake teeth of {player}.",
            "{killed} was chased away by {player}'s Golem.",
            "{killed} was killed by {player}.",
            "{killed} was knocked into the void by {player}."
    };

    private final static Pattern[] KILLS_COMPILED = new Pattern[KILLS.length];

    private BedwarsTeam won = null;
    private int wonTick = -1;

    private int seconds = 0;
    private Text topBarText = new LiteralText("");

    private static String formatPlaceholder(String input) {
        return input
                .replace("{killed}", "(\\b[A-Za-z0-9_§]{3,16}\\b)")
                .replace("{player}", "(\\b[A-Za-z0-9_§]{3,16}\\b)")
                .replace("{number}", "[0-9,]+");
    }

    static {
        for (int i = 0; i < KILLS.length; i++) {
            String kill = KILLS[i];
            KILLS_COMPILED[i] = Pattern.compile(formatPlaceholder("^" + kill.replace(".", "\\.") + "(?: FINAL KILL!)?\\s*?"));
        }
    }

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

    private void died(ReceiveChatMessageEvent event, BedwarsPlayer player, @Nullable BedwarsPlayer killer, boolean finalDeath) {
        player.died();
        if (killer != null) {
            killer.killed(finalDeath);
        }
        event.newMessage = mod.gameLog.died(player, killer, finalDeath);
    }

    public boolean isTeamEliminated(BedwarsTeam team) {
        return players.values().stream().filter(b -> b.getTeam() == team).allMatch(BedwarsPlayer::isFinalKilled);
    }

    public void onChatMessage(String rawMessage, ReceiveChatMessageEvent event) {
        try {
            if (matched(ANNOYING_MESSAGES, rawMessage).isPresent()) {
                event.cancelled = true;
                return;
            }
            if (matched(DIED, rawMessage, m -> {
                BedwarsPlayer killed = getPlayer(m.group(1)).orElse(null);
                if (killed == null) {
                    debug("Player " + m.group(1) + " was not found");
                    return;
                }
                died(event, killed, null, matched(FINAL_KILL, rawMessage).isPresent());
            })) {
                return;
            }
            if (matched(KILLS_COMPILED, rawMessage, m -> {
                BedwarsPlayer killed = getPlayer(m.group(1)).orElse(null);
                BedwarsPlayer killer = getPlayer(m.group(2)).orElse(null);
                if (killed == null) {
                    debug("Player " + m.group(1) + " was not found");
                    return;
                }
                died(event, killed, killer, matched(FINAL_KILL, rawMessage).isPresent());
            })) {
                return;
            }
            if (matched(BED_DESTROY, rawMessage, m -> {
                BedwarsPlayer player = matched(BED_BREAK, rawMessage).flatMap(m1 -> getPlayer(m1.group(1))).orElse(null);
                BedwarsTeam.fromName(m.group(1)).ifPresent(t -> bedDestroyed(t, player));

            })) {
                return;
            }
            if (matched(DISCONNECT, rawMessage, m -> getPlayer(m.group(1)).ifPresent(this::disconnected))) {
                return;
            }
            if (matched(RECONNECT, rawMessage, m -> getPlayer(m.group(1)).ifPresent(this::reconnected))) {
                return;
            }
            if (matched(GAME_END, rawMessage, m -> {
                BedwarsTeam win = players.values().stream().filter(p -> !p.isFinalKilled()).findFirst().map(BedwarsPlayer::getTeam).orElse(null);
                this.won = win;
                this.wonTick = mc.inGameHud.getTicks() + 10;
            })) {
                return;
            }
            if (matched(TEAM_ELIMINATED, rawMessage, m -> BedwarsTeam.fromName(m.group(1)).ifPresent(this::teamEliminated))) {
                return;
            }
            upgrades.onMessage(rawMessage);
        } catch (Exception e) {
            debug("Error: " + e);
        }
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

    public static boolean matched(Pattern pattern, String input, Consumer<Matcher> consumer) {
        Optional<Matcher> matcher = matched(pattern, input);
        if (!matcher.isPresent()) {
            return false;
        }
        consumer.accept(matcher.get());
        return true;
    }

    public static boolean matched(Pattern[] pattern, String input, Consumer<Matcher> consumer) {
        Optional<Matcher> matcher = matched(pattern, input);
        if (!matcher.isPresent()) {
            return false;
        }
        consumer.accept(matcher.get());
        return true;
    }

    public static Optional<Matcher> matched(Pattern[] pattern, String input) {
        for (Pattern p : pattern) {
            Optional<Matcher> m = matched(p, input);
            if (m.isPresent()) {
                return m;
            }
        }
        return Optional.empty();
    }

    public static Optional<Matcher> matched(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Optional.of(matcher);
        }
        return Optional.empty();
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
