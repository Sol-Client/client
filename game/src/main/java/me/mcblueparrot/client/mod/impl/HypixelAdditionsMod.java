/**
 * Some of these options are based on the ideas of Sk1er LLC's mods.
 */

// TODO packages instead of inner classes

package me.mcblueparrot.client.mod.impl;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.mcblueparrot.client.events.*;
import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.ChatChannelSystem;
import me.mcblueparrot.client.ChatChannelSystem.ChatChannel.DefaultChatChannel;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.DetectedServer;
import me.mcblueparrot.client.ServerConnectEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.util.ApacheHttpClient;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Rectangle;
import me.mcblueparrot.client.util.Utils;
import net.hypixel.api.HypixelAPI;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class HypixelAdditionsMod extends Mod {

    private static boolean enabled;
    public static HypixelAdditionsMod instance;
    @Expose
    @ConfigOption("/visithousing")
    public boolean visitHousingCommand = true;
    @Expose
    @ConfigOption("Lobby Sounds Volume")
    @Slider(min = 0, max = 100, step = 1)
    public float lobbySoundsVolume = 100;
    @Expose
    @ConfigOption("Housing Music Volume")
    @Slider(min = 0, max = 100, step = 1)
    public float housingMusicVolume = 100;
    @Expose
    @ConfigOption("Pop-up Events")
    private boolean popupEvents = true;
    // Borrowed (nicked) from https://static.sk1er.club/autogg/regex_triggers_3.json.
    @Expose
    @ConfigOption("Auto GG")
    private boolean autogg = true;
    private List<Pattern> autoggTriggers = Arrays.asList(
            "^ +1st Killer - ?\\[?\\w*\\+*\\]? \\w+ - \\d+(?: Kills?)?$",
            "^ *1st (?:Place ?)?(?:-|:)? ?\\[?\\w*\\+*\\]? \\w+(?: : \\d+| - \\d+(?: Points?)?| - \\d+(?: x .)?| \\(\\w+ .{1,6}\\) - \\d+ Kills?|: \\d+:\\d+| - \\d+ (?:Zombie )?(?:Kills?|Blocks? Destroyed)| - \\[LINK\\])?$",
            "^ +Winn(?:er #1 \\(\\d+ Kills\\): \\w+ \\(\\w+\\)|er(?::| - )(?:Hiders|Seekers|Defenders|Attackers|PLAYERS?|MURDERERS?|Red|Blue|RED|BLU|\\w+)(?: Team)?|ers?: ?\\[?\\w*\\+*\\]? \\w+(?:, ?\\[?\\w*\\+*\\]? \\w+)?|ing Team ?[\\:-] (?:Animals|Hunters|Red|Green|Blue|Yellow|RED|BLU|Survivors|Vampires))$",
            "^ +Alpha Infected: \\w+ \\(\\d+ infections?\\)$",
            "^ +Murderer: \\w+ \\(\\d+ Kills?\\)$",
            "^ +You survived \\d+ rounds!$",
            "^ +(?:UHC|SkyWars|The Bridge|Sumo|Classic|OP|MegaWalls|Bow|NoDebuff|Blitz|Combo|Bow Spleef) (?:Duel|Doubles|Teams|Deathmatch|2v2v2v2|3v3v3v3)? ?- \\d+:\\d+$",
            "^ +They captured all wools!$",
            "^ +Game over!$",
            "^ +[\\d\\.]+k?/[\\d\\.]+k? \\w+$",
            "^ +(?:Criminal|Cop)s won the game!$",
            "^ +\\[?\\w*\\+*\\]? \\w+ - \\d+ Final Kills$",
            "^ +Zombies - \\d*:?\\d+:\\d+ \\(Round \\d+\\)$",
            "^ +. YOUR STATISTICS .$"
    ).stream().map(Pattern::compile).collect(Collectors.toList());
    @Expose
    @ConfigOption("Hide GG")
    private boolean hidegg = false;
    private List<Pattern> hideggTriggers = Arrays.asList(
            ".*: [gG]{2}",
            ".*: [gG]ood [gG]ame",
            "\\+\\d* Karma!").stream().map(Pattern::compile).collect(Collectors.toList());
    private boolean donegg;
    private Pattern hideChannelMessageTrigger = Pattern.compile("(You are now in the (ALL|PARTY|GUILD|OFFICER) channel|You're already in this channel!)");
    private Pattern apiKeyMessageTrigger = Pattern.compile("Your new API key is (.*)");
    @Expose
    @ConfigOption("Auto GL")
    private boolean autogl = true;
    private String autoglTrigger = "The game starts in 5 seconds!";
    @Expose
    @ConfigOption("Hide GL")
    private boolean hidegl = false;
    private Pattern hideglTrigger = Pattern.compile(".*: [gG](ood )?[lL](uck,? ?)?([hH](ave )?[fF](un)?!?)?");
    private boolean donegl;
    @Expose
    @ConfigOption("Levelhead")
    public boolean levelhead;
    private Map<UUID, String> levelCache = new HashMap<>();
    @Expose
    private String apiKey;
    private HypixelAPI api;
    public Deque<Request> requests = new ArrayDeque<>();
    public Request request;
    private KeyBinding keyAcceptRequest = new KeyBinding("Accept Request", Keyboard.KEY_Y, "Sol Client");
    private KeyBinding keyDismissRequest = new KeyBinding("Dismiss Request", Keyboard.KEY_N, "Sol Client");

    public HypixelAdditionsMod() {
        super("Hypixel Additions", "hypixel_util", "Various additions to Hypixel.", ModCategory.UTILITY);
        instance = this;
        Client.INSTANCE.registerKeyBinding(keyAcceptRequest);
        Client.INSTANCE.registerKeyBinding(keyDismissRequest);
    }


    public String getLevelhead(UUID player) {
        if(!(enabled && levelhead)) {
            return null;
        }
        if(levelCache.containsKey(player)) {
            String result = levelCache.get(player);
            if(result.isEmpty()) {
                return null;
            }
            return result;
        }
        else if(api != null) {
            levelCache.put(player, "");
            api.getPlayerByUuid(player).whenCompleteAsync((response, error) -> {
                if(!response.isSuccess() || error != null) {
                    levelCache.put(player, "Unknown");
                    return;
                }
                levelCache.put(player, Integer.toString((int) response.getPlayer().getNetworkLevel()));
            });
        }
        return null;
    }

    public boolean isLobby() {
        if(mc.thePlayer != null && mc.thePlayer.inventory != null) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(8);
            if(stack != null) {
                return stack.getDisplayName().equals(EnumChatFormatting.GREEN + "Lobby Selector " + EnumChatFormatting.GRAY + "(Right Click)");
            }
        }
        return false;
    }

    public boolean isHousing() {
        return (mc.theWorld != null && mc.theWorld.getScoreboard() != null && mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1) != null
                && "HOUSING".equals(EnumChatFormatting.getTextWithoutFormattingCodes(
                        mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName())));
    }

    public static boolean isHypixel() {
//        return true; // For testing purposes
        return Client.INSTANCE.detectedServer == DetectedServer.HYPIXEL;
    }

    public static boolean isEffective() {
        return enabled && isHypixel();
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    private void updateState() {
        if(Client.INSTANCE.getCommand("visithousing") == null) {
            if(isEffective()) {
                Client.INSTANCE.registerCommand("visithousing", new VisitHousingCommand());
            }
        }
        else {
            if(!isEffective()) {
                Client.INSTANCE.unregisterCommand("visithousing");
            }
        }

        if(Client.INSTANCE.getCommand("chat") == null) {
            if(isEffective()) {
                if(mc.thePlayer != null) {
                    mc.thePlayer.sendChatMessage("/chat a");
                }
                Client.INSTANCE.registerCommand("chat", new ChatCommand());
            }
        }
        else {
            if(!isEffective()) {
                Client.INSTANCE.unregisterCommand("chat");
            }
        }

        if(Client.INSTANCE.getChatChannelSystem() == null) {
            if(isEffective()) {
                Client.INSTANCE.setChatChannelSystem(new HypixelChatChannels());
            }
        }
        else {
            if(!isEffective() && Client.INSTANCE.getChatChannelSystem() instanceof HypixelChatChannels) {
                Client.INSTANCE.setChatChannelSystem(null);
            }
        }

        if(isEffective() && apiKey == null) {
            IChatComponent component = new ChatComponentText("Could not find API key (required for levelhead). Click here or run /api new.");
            component.setChatStyle(new ChatStyle()
                    .setColor(EnumChatFormatting.RED)
                    .setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/api new")));
        }
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        if(apiKey != null) {
            api = new HypixelAPI(new ApacheHttpClient(UUID.fromString(apiKey)));
        }
    }

    @Override
    public void onRegister() {
        super.onRegister();
        setApiKey(apiKey);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        enabled = true;
        updateState();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        enabled = false;
        updateState();
    }

    @EventHandler
    public void onServerChange(ServerConnectEvent event) {
        updateState();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        donegg = donegl = false;
        levelCache.clear();
    }

    @EventHandler
    public void onMessage(ReceiveChatMessageEvent event) {
        if(!isHypixel()) {
            return;
        }

        if(event.actionBar && isHousing() && event.message.startsWith("Now playing:")) {
            event.cancelled = true;
            return;
        }

        if(popupEvents) {
            for(String line : event.message.split("\\n")) {
                Request request = Request.fromMessage(line);
                if(request != null) {
                    requests.add(request);
                    return;
                }
            }
        }

        if(autogg && !donegg) {
            for(Pattern pattern : autoggTriggers) {
                if(pattern.matcher(event.message).matches()) {
                    donegg = true;
                    mc.thePlayer.sendChatMessage("/achat gg");
                    return;
                }
            }
        }

        if(hidegg) {
            for(Pattern pattern : hideggTriggers) {
                if(pattern.matcher(event.message).matches()) {
                    event.cancelled = true;
                    return;
                }
            }
        }

        if(autogl && !donegl) {
            if(event.message.equals(autoglTrigger)) {
                donegl = true;
                mc.thePlayer.sendChatMessage("/achat glhf");
                return;
            }
        }

        if(hidegl) {
            if(hideglTrigger.matcher(event.message).matches()) {
                event.cancelled = true;
                return;
            }
        }

        if(hideChannelMessageTrigger.matcher(event.message).matches()) {
            event.cancelled = true;
            return;
        }

        Matcher apiKeyMatcher = apiKeyMessageTrigger.matcher(event.message);
        if(apiKeyMatcher.matches()) {
            setApiKey(apiKeyMatcher.group(1));
        }
    }

    @EventHandler
    public void onRender(PostGameOverlayRenderEvent event) {
        if(event.type != GameOverlayElement.ALL) return;

        if(request != null) {
            long since = System.currentTimeMillis() - request.time;
            if(since > 10000) {
                request = null;
            }
            else {
                String message = request.message;
                String keys = EnumChatFormatting.GREEN + " [ " + GameSettings.getKeyDisplayString(keyAcceptRequest.getKeyCode()) + " ] Accept" +
                        EnumChatFormatting.RED + "  [ " + GameSettings.getKeyDisplayString(keyDismissRequest.getKeyCode()) + " ] Dismiss ";
                int width = Math.max(mc.fontRendererObj.getStringWidth(message), mc.fontRendererObj.getStringWidth(keys)) + 15;

                ScaledResolution resolution = new ScaledResolution(mc);

                Rectangle popupBounds = new Rectangle(resolution.getScaledWidth() / 2 - (width / 2), 10, width, 50);
                Utils.drawRectangle(popupBounds, new Colour(0, 0, 0, 100));
                Utils.drawRectangle(new Rectangle(popupBounds.getX(), popupBounds.getY() + popupBounds.getHeight() - 1, width, 2), Colour.BLACK);
                Utils.drawRectangle(new Rectangle(popupBounds.getX(),
                        popupBounds.getY() + popupBounds.getHeight() - 1,
                        (int) ((popupBounds.getWidth() / 10000F) * (since)), 2), Colour.BLUE);

                mc.fontRendererObj.drawString(message,
                        popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.fontRendererObj.getStringWidth(message) / 2), 20,
                        -1);

                mc.fontRendererObj.drawString(keys,
                        popupBounds.getX() + (popupBounds.getWidth() / 2) - (mc.fontRendererObj.getStringWidth(keys) / 2), 40,
                        -1);

                if(keyAcceptRequest.isPressed()) {
                    mc.thePlayer.sendChatMessage(request.command);
                    request = null;
                }
                else if(keyDismissRequest.isPressed()) {
                    request = null;
                }
            }
        }

        if(request == null && !requests.isEmpty()) {
            request = requests.pop();
            request.time = System.currentTimeMillis();
        }
        keyAcceptRequest.isPressed();
        keyDismissRequest.isPressed();
    }

    @EventHandler
    public void onSoundPlay(SoundPlayEvent event) {
        if(!isHypixel()) {
            return;
        }

        if(isLobby()) {
            if(event.soundName.startsWith("mob")
                    || event.soundName.equals("random.orb")
                    || event.soundName.equals("random.pop")
                    || event.soundName.equals("random.levelup")
                    || event.soundName.equals("game.tnt.primed")
                    || event.soundName.equals("random.explode")
                    || event.soundName.equals("mob.chicken.plop")
                    || event.soundName.startsWith("note")
                    || event.soundName.equals("random.click")
                    || event.soundName.startsWith("fireworks")
                    || event.soundName.equals("fire.fire")
                    || event.soundName.equals("random.bow")) {
                event.volume *= lobbySoundsVolume / 100F;
            }
        }
        else {
            if(isHousing() && event.soundName.startsWith("note")) {
                event.volume *= housingMusicVolume / 100F;
            }
        }
    }

    public static class Request {

        public String message;
        public String command;
        public long time;

        public static Request fromMessage(String message) {
            for(RequestType type : RequestType.values()) {
                Matcher matcher = type.pattern.matcher(message);
                if(matcher.matches()) {
                    Object[] groups = new String[matcher.groupCount()];

                    for(int i = 1; i <= matcher.groupCount(); i++) {
                        groups[i - 1] = matcher.group(i);
                    }

                    Request request = new Request();
                    request.message = String.format(type.message, groups);
                    request.command = String.format(type.command, groups);
                    return request;
                }
            }
            return null;
        }
    }

    public enum RequestType {
        // Taken from https://github.com/Sk1erLLC/PopupEvents/blob/master/src/main/resources/remoteresources/chat_regex.json
        FRIEND("Friend request from ((\\[.+] )?(\\S{1,16})).*",
                "Friend request from %3$s.",
                "/friend accept %3$s"),
        PARTY("(?:\\[.*] )?(\\S{1,16}) has invited you to join (?:their|(?:\\[.*] ?)?\\w{1,16}'s)? party!",
                "Party invite from %s.",
                "/party accept %s"),
        DUEL("(\\[.*] )?(\\S{1,16}) has invited you to (\\S+) Duels!",
                "%3$s duel request from %2$s.",
                "/duel accept %s"),
        GUILD("Click here to accept or type (\\/guild accept (\\w+))!",
                "Guild request from %2$s.",
                "%s"),
        GUILD_PARTY("(\\?[.*] )?(\\S{1,16}) has invited all members of (\\S+) to their party!",
                "Guild party invite from %2$s for %3$s.",
                "/party accept %2$s");

        private Pattern pattern;
        private String message;
        private String command;

        RequestType(String regex, String message, String command) {
            pattern = Pattern.compile(regex);
            this.message = message;
            this.command = command;
        }

    }

    public class VisitHousingCommand extends CommandBase {

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException {
            if(args.length == 1) {
                if(isHousing()) {
                    mc.thePlayer.sendChatMessage("/visit " + args[0]);
                }
                else {
                    mc.thePlayer.sendChatMessage("/lobby housing");
                    new Thread(() -> {
                        try {
                            Thread.sleep(300);
                        }
                        catch(InterruptedException e) {
                            e.printStackTrace();
                        }
                        mc.thePlayer.sendChatMessage("/visit " + args[0]);
                    }).start();
                }
                return;
            }
            throw new WrongUsageException("Usage: " + getCommandUsage(sender), new Object[0]);
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/visithousing <player>";
        }

        @Override
        public List<String> getCommandAliases() {
            return Arrays.asList("housing");
        }

        @Override
        public String getCommandName() {
            return null;
        }

    }

    public static class ChatCommand extends CommandBase {

        @Override
        public void processCommand(ICommandSender sender, String[] args) throws CommandException {
            ChatChannelSystem system = Client.INSTANCE.getChatChannelSystem();
            if(args.length == 1) {
                if(args[0].equals("c") || args[0].equals("coop") || args[0].equals("co-op") || args[0].equals("skyblock_coop") || args[0].equals("skyblock_co-op")) {
                    system.setChannel(HypixelChatChannels.COOP);
                }
                else if(args[0].equals("a") || args[0].equals("all")) {
                    system.setChannel(ChatChannelSystem.ALL);
                }
                else if(args[0].equals("p") || args[0].equals("party")) {
                    system.setChannel(HypixelChatChannels.PARTY);
                }
                else if(args[0].equals("o") || args[0].equals("officer") || args[0].equals("officers") || args[0].equals("guild_officer") || args[0].equals("guild_officers")) {
                    system.setChannel(HypixelChatChannels.OFFICER);
                }
                else if(args[0].equals("g") || args[0].equals("guild")) {
                    system.setChannel(HypixelChatChannels.GUILD);
                }
                else {
                    throw new WrongUsageException(getCommandUsage(sender));
                }
            }
            else if(args.length == 2 && (args[0].equals("2") || args[0].equals("t") || args[0].equals("to"))) {
                system.setChannel(ChatChannelSystem.getPrivateChannel(args[1]));
            }
            else {
                throw new WrongUsageException("Usage: " + getCommandUsage(sender));
            }
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Chat Channel: " + system.getChannelName()));
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/chat (all|party|guild|officer|coop)";
        }

        @Override
        public List<String> getCommandAliases() {
            return Arrays.asList("channel");
        }

        @Override
        public String getCommandName() {
            return null;
        }

    }

    public static class HypixelChatChannels extends ChatChannelSystem {

        public static final ChatChannel PARTY = new DefaultChatChannel("Party", "pchat");
        public static final ChatChannel GUILD = new DefaultChatChannel("Guild", "gchat");
        public static final ChatChannel OFFICER = new DefaultChatChannel("Guild Officer", "ochat");
        public static final ChatChannel COOP = new DefaultChatChannel("Skyblock Co-op", "coopchat");

        private static final List<ChatChannel> CHANNELS = Arrays.asList(ALL, PARTY, GUILD, OFFICER, COOP);

        @Override
        public List<ChatChannel> getChannels() {
            return CHANNELS;
        }

    }

}
