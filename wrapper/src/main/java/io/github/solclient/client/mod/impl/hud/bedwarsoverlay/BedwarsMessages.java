package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BedwarsMessages {

    public final static Pattern[] COMBAT_KILL = convert(
            "{killed} was struck down by {player}.",
            "{killed} was filled full of lead by {player}.",
            "{killed} died in close combat to {player}.",
            "{killed} was given the cold shoulder by {player}.",
            "{killed} was glazed in BBQ sauce by {player}.",
            "{killed} was bitten by {player}.",
            "{killed} was wrapped into a gift by {player}.",
            "{killed} was hunted down by {player}.",
            "{killed} was oinked by {player}.",
            "{killed} was chewed up by {player}.",
            "{killed} was buzzed to death by {player}.",
            "{killed} was trampled by {player}.",
            "{killed} be sent to Davy Jones' locker by {player}.",
            "{killed} got rekt by {player}.",
            "{killed} was locked outside during a snow storm by {player}.",
            "{killed} was painted pretty by {player}.",
            "{killed} was wrapped up by {player}.",
            "{killed} was stomped by {player}.",
            "{killed} was {player}'s final #{number}",
            "{killed} was spooked by {player}.",
            "{killed} was tragically backstabbed by {player}.",
            "{killed} was crushed by {player}.",
            "{killed} was {player}'s final #{number}.",
            "{killed} was whacked with a party balloon by {player}.",
            "{killed} was crushed into moon dust by {player}.",
            "{killed} was smothered in holiday cheer by {player}.",
            "{killed} was ripped to shreds by {player}.",
            "{killed} was bested by {player}.",
            "{killed} was {player}'s final #{number}.",
            "{killed} had a small brain moment while fighting {player}.",
            "{killed} was too shy to meet {player}.",
            "{killed} was yelled at by {player}.",
            "{killed} was killed by {player}."
    );

    public final static Pattern[] VOID_KILL = convert(
            "{killed} was turned to dust by {player}.",
            "{killed} met their end by {player}.",
            "{killed} fought to the edge with {player}.",
            "{killed} was hit off by a love bomb from {player}.",
            "{killed} slipped in BBQ sauce off the edge spilled by {player}.",
            "{killed} howled into the void for {player}.",
            "{killed} hit the hard-wood floor because of {player}.",
            "{killed} stumbled on a trap set by {player}.",
            "{killed} slipped into void for {player}.",
            "{killed} was scared into the void by {player}.",
            "{killed} was bzzz'd into the void by {player}.",
            "{killed} was back kicked into the void by {player}.",
            "{killed} be cannonballed to death by {player}.",
            "{killed} took the L to {player}.",
            "{killed} was pushed into a snowbank by {player}.",
            "{killed} was deviled into the void by {player}.",
            "{killed} was tied into a bow by {player}.",
            "{killed} was thrown down a pit by {player}.",
            "{killed} was spooked off the map by {player}.",
            "{killed} was heartlessly let go by {player}.",
            "{killed} was dominated by {player}.",
            "{killed} was popped into the void by {player}.",
            "{killed} was sent the wrong way by {player}.",
            "{killed} was banished into the ether by {player}'s holiday spirit.",
            "{killed} was charged by {player}.",
            "{killed} was knocked into the void by {player}.",
            "{killed} was not able to block clutch against {player}.",
            "{killed} didn't distance themselves properly from {player}.",
            "{killed} was thrown off the lawn by {player}.",
            "{killed} was turned to dust by {player}."
    );

    public final static Pattern[] PROJECTILE_KILL = convert(
            "{killed} was melted by {player}.",
            "{killed} was killed with dynamite by {player}.",
            "{killed} fell to the great marksmanship of {player}.",
            "{killed} was struck with Cupid's arrow by {player}.",
            "{killed} was thrown chili powder at by {player}.",
            "{killed} caught the ball thrown by {player}.",
            "{killed} was put on the naughty list by {player}.",
            "{killed} got skewered by {player}.",
            "{killed} got attacked by a carrot from {player}.",
            "{killed} stepped in a mouse trap placed by {player}.",
            "{killed} was startled by {player}.",
            "{killed} was impaled from a distance by {player}.",
            "{killed} be shot and killed by {player}.",
            "{killed} got smacked by {player}.",
            "{killed} was hit with a snowball from {player}.",
            "{killed} slipped into a pan placed by {player}.",
            "{killed} was glued up by {player}.",
            "{killed} was shot by {player}.",
            "{killed} was remotely spooked by {player}.",
            "{killed}'s heart was pierced by {player}.",
            "{killed} was assassinated by {player}.",
            "{killed} was shot with a roman candle by {player}.",
            "{killed} was hit by an asteroid from {player}.",
            "{killed} was sniped by a missile of festivity by {player}.",
            "{killed} was pounced on by {player}.",
            "{killed} was shot by {player}.",
            "{killed} got 360 no-scoped by {player}.",
            "{killed} was coughed at by {player}.",
            "{killed} was accidentally spit on by {player}."
    );

    public final static Pattern[] FALL_KILL = convert(
            "{killed} was turned to ash by {player}.",
            "{killed} lost a drinking contest with {player}.",
            "{killed} stumbled off a ledge with help by {player}.",
            "{killed} was out of the league of {player}.",
            "{killed} was not spicy enough for {player}.",
            "{killed} was distracted by a puppy placed by {player}.",
            "{killed} was pushed down a slope by {player}.",
            "{killed} was thrown into a volcano by {player}.",
            "{killed} was distracted by a piglet from {player}.",
            "{killed} was distracted by a rat dragging pizza from {player}.",
            "{killed} was stung off the edge by {player}.",
            "{killed} was headbutted off a cliff by {player}.",
            "{killed} be killed with magic by {player}.",
            "{killed} got roasted by {player}.",
            "{killed} was shoved down an icy slope by {player}.",
            "{killed} was flipped off the edge by {player}.",
            "{killed} tripped over a present placed by {player}.",
            "{killed} was thrown to the ground by {player}.",
            "{killed} was totally spooked by {player}.",
            "{killed} was delivered into nothingness by {player}.",
            "{killed} was thrown off their high horse by {player}.",
            "{killed} was launched like a firework by {player}.",
            "{killed} was blasted to the moon by {player}.",
            "{killed} was pushed by {player}'s holiday spirit.",
            "{killed} was ripped and thrown by {player}.",
            "{killed} was knocked off an edge by {player}.",
            "{killed} was knocked off a cliff by {player}.",
            "{killed} forgot how many blocks they had left while fighting {player}.",
            "{killed} tripped while trying to run away from {player}.",
            "{killed} slipped on the fake teeth of {player}.",
            "{killed} was knocked into the void by {player}."
    );

    public final static Pattern[] GOLEM_KILL = convert(
            "{killed} was fried by {player}'s Golem.",
            "{killed} lost the draw to {player}'s Golem.",
            "{killed} tangoed with {player}'s Golem.",
            "{killed} was no match for {player}'s Golem.",
            "{killed} was sliced up by {player}'s Golem.",
            "{killed} played too rough with {player}'s Golem.",
            "{killed} was turned to gingerbread by {player}'s Golem.",
            "{killed} was mauled by {player}'s Golem.",
            "{killed} was oinked by {player}'s Golem.",
            "{killed} squeaked around with {player}'s Golem.",
            "{killed} was bee'd by {player}'s Golem.",
            "{killed} was trampled by {player}'s Golem.",
            "{killed} be killed with metal by {player}'s Golem.",
            "{killed} got bamboozled by {player}'s Golem.",
            "{killed} got snowed in by {player}'s Golem.",
            "{killed} was made sunny side up by {player}'s Golem.",
            "{killed} was taped together by {player}'s Golem.",
            "{killed} was outclassed by {player}'s Golem.",
            "{killed} was spooked by {player}'s Golem.",
            "{killed} was dismembered by {player}'s Golem.",
            "{killed} was degraded by {player}'s Golem.",
            "{killed} was lit up by {player}'s Golem.",
            "{killed} was blown up by {player}'s Golem.",
            "{killed} was sung holiday tunes to by {player}'s Golem.",
            "{killed} was ripped to shreds by {player}'s Golem.",
            "{killed} was bested by {player}'s Golem.",
            "{killed} got absolutely destroyed by {player}'s Golem.",
            "{killed} got too close to {player}'s Golem.",
            "{killed} was chased away by {player}'s Golem."
    );

    public final static Pattern[] BED_BREAK = {
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
            Pattern.compile(formatPlaceholder("Bed was destroyed by {player}")),
    };

    public final static Pattern DISCONNECT = Pattern.compile("(\\b[A-Za-z0-9_§]{3,16}\\b) disconnected\\.$");
    public final static Pattern RECONNECT = Pattern.compile("(\\b[A-Za-z0-9_§]{3,16}\\b) reconnected\\.$");
    public final static Pattern FINAL_KILL = Pattern.compile("FINAL KILL!");
    public final static Pattern BED_DESTROY = Pattern.compile("^\\s*?BED DESTRUCTION > (\\w+) Bed");
    public final static Pattern TEAM_ELIMINATED = Pattern.compile("^\\s*?TEAM ELIMINATED > (\\w+) Team");

    public final static Pattern GAME_END = Pattern.compile("^ +1st Killer - ?\\[?\\w*\\+*\\]? \\w+ - \\d+(?: Kills?)?$");

    public final static Pattern SELF_VOID = Pattern.compile(formatPlaceholder("^{killed} fell into the void.(?: FINAL KILL!)?\\s*?"));
    public final static Pattern SELF_UNKNOWN = Pattern.compile(formatPlaceholder("^{killed} died.(?: FINAL KILL!)?\\s*?"));

    public final static Pattern[] ANNOYING_MESSAGES = {
            Pattern.compile("^You will respawn in \\d* seconds!$"),
            Pattern.compile("^You will respawn in \\d* second!$"),
            Pattern.compile("^You purchased Wool$"),
            Pattern.compile("^Cross-teaming is not allowed"),
            Pattern.compile("^\\+\\d+ Coins!"),
            Pattern.compile("^\\+\\d+ coins!"),
            Pattern.compile("^Coins just earned DOUBLE"),
            Pattern.compile("^\\+\\d+ Bed Wars Experience"),
            Pattern.compile("^You have respawned"),
    };

    private static Pattern[] convert(String... input) {
        return Arrays.stream(input).map(str -> Pattern.compile("^" + formatPlaceholder(str) + "(?: FINAL KILL!)?\\s*?")).toArray(Pattern[]::new);
    }

    private static String formatPlaceholder(String input) {
        return input
                .replace("{killed}", "(\\b[A-Za-z0-9_§]{3,16}\\b)")
                .replace("{player}", "(\\b[A-Za-z0-9_§]{3,16}\\b)")
                .replace("{number}", "[0-9,]+");
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


}
