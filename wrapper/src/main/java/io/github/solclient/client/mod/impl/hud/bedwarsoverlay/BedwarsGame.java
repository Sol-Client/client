package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import io.github.solclient.client.event.impl.ReceiveChatMessageEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BedwarsGame {

    private final static Pattern DISCONNECT = Pattern.compile("(\\b[A-Za-z0-9_§]{3,16}\\b) disconnected\\.$");
    private final static Pattern RECONNECT = Pattern.compile("(\\b[A-Za-z0-9_§]{3,16}\\b) reconnected\\.$");
    private final static Pattern FINAL_KILL = Pattern.compile("FINAL KILL!$");
    private final static Pattern BED_DESTROY = Pattern.compile("^BED DESTRUCTION > (\\w*?) Bed");
    private final static Pattern TEAM_ELIMINATED = Pattern.compile("^TEAM ELIMINATED > (\\w*?) Team");
    private final static Pattern[] DIED = {
            Pattern.compile(formatPlaceholder("{killed} fell into the void.")),
            Pattern.compile(formatPlaceholder("{killed} died."))
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

    private static String formatPlaceholder(String input) {
        return input.replace("{killed}", "(\\b[A-Za-z0-9_§]{3,16}\\b)").replace("{player}", "(\\b[A-Za-z0-9_§]{3,16}\\b)");
    }

    static {
        for (int i = 0; i < KILLS.length; i++) {
            String kill = KILLS[i];
            KILLS_COMPILED[i] = Pattern.compile(formatPlaceholder(kill.replace(".", "\\.")));
        }
    }

    private final List<BedwarsPlayer> players = new ArrayList<>();
    private final MinecraftClient mc;
    private boolean started = false;


    public BedwarsGame() {
        mc = MinecraftClient.getInstance();
    }

    public void onStart() {
        this.started = true;
        players.clear();
        for (PlayerListEntry player : mc.player.networkHandler.getPlayerList()) {
            String name = mc.inGameHud.getPlayerListWidget().getPlayerName(player).replaceAll("§.", "");
            if (name.charAt(1) != ' ') {
                continue;
            }
            BedwarsTeam team = BedwarsTeam.fromPrefix(name.charAt(0)).orElse(null);
            if (team == null) {
                continue;
            }
            System.out.println(player.getProfile().getName() + " is in team " + team);
        }
    }

    public void onChatMessage(String rawMessage, ReceiveChatMessageEvent event) {
        try {
            matched(DIED, rawMessage).ifPresent(m -> System.out.println(m.group(1) + " died by themselves"));
            matched(KILLS_COMPILED, rawMessage).ifPresent(m -> System.out.println(m.group(1) + " was killed by " + m.group(2)));
            matched(BED_DESTROY, rawMessage).ifPresent(m -> System.out.println(m.group(1) + " bed broke"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tick() {}

    private static Optional<Matcher> matched(Pattern[] pattern, String input) {
        for (Pattern p : pattern) {
            Optional<Matcher> m = matched(p, input);
            if (m.isPresent()) {
                return m;
            }
        }
        return Optional.empty();
    }

    private static Optional<Matcher> matched(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return Optional.of(matcher);
        }
        return Optional.empty();
    }

}
