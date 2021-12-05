/**
 * Many of these options are based on the ideas of Sk1er LLC's mods.
 */

package me.mcblueparrot.client.mod.impl.hypixeladditions;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.DetectedServer;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GameOverlayElement;
import me.mcblueparrot.client.event.impl.PostGameOverlayRenderEvent;
import me.mcblueparrot.client.event.impl.PostTickEvent;
import me.mcblueparrot.client.event.impl.ReceiveChatMessageEvent;
import me.mcblueparrot.client.event.impl.ServerConnectEvent;
import me.mcblueparrot.client.event.impl.SoundPlayEvent;
import me.mcblueparrot.client.event.impl.WorldLoadEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.mod.impl.hypixeladditions.commands.ChatChannelCommand;
import me.mcblueparrot.client.mod.impl.hypixeladditions.commands.VisitHousingCommand;
import me.mcblueparrot.client.mod.impl.hypixeladditions.request.Request;
import me.mcblueparrot.client.util.ApacheHttpClient;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.hypixel.api.HypixelAPI;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class HypixelAdditionsMod extends Mod {

	private static final Logger LOGGER = LogManager.getLogger();

	private static boolean enabled;
	public static HypixelAdditionsMod instance;
	@Expose
	@ConfigOption("/visithousing")
	public boolean visitHousingCommand = true;
	@Expose
	@ConfigOption("Lobby Sounds Volume")
	@Slider(min = 0, max = 100, step = 1, suffix = "%")
	public float lobbySoundsVolume = 100;
	@Expose
	@ConfigOption("Housing Music Volume")
	@Slider(min = 0, max = 100, step = 1, suffix = "%")
	public float housingMusicVolume = 100;
	@Expose
	@ConfigOption("Pop-up Events")
	private boolean popupEvents = true;
	// Borrowed (nicked) and updated from https://static.sk1er.club/autogg/regex_triggers_3.json.
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
			"^ +(((?:UHC|SkyWars|(The )?Bridge|Sumo|Classic|OP|MegaWalls|Bow|NoDebuff|Blitz|Combo|Bow Spleef|Boxing) (?:Duel|Doubles|Teams|Deathmatch|2v2v2v2|3v3v3v3)?)|Hypixel Parkour) ?- \\d+:\\d+$",
			"^ +They captured all wools!$",
			"^ +Game over!$",
			"^ +[\\d\\.]+k?/[\\d\\.]+k? \\w+$",
			"^ +(?:Criminal|Cop)s won the game!$",
			"^ +\\[?\\w*\\+*\\]? \\w+ - \\d+ Final Kills$",
			"^ +Zombies - \\d*:?\\d+:\\d+ \\(Round \\d+\\)$",
			"^ +. YOUR STATISTICS .$",
			"^ {36}Winner(s?)$",
			"^ {21}Bridge CTF [a-zA-Z]+ - \\d\\d:\\d\\d$"
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
	private boolean autogl;
	private String autoglTrigger = "The game starts in 1 second!";
	private long ticksUntilAutogl = -1;
	private long ticksUntilLocraw = -1;
	@Expose
	@ConfigOption("Hide GL")
	private boolean hidegl = false;
	private Pattern hideglTrigger = Pattern.compile(".*: [gG](ood )?[lL](uck,? ?)?([hH](ave )?[fF](un)?!?)?");
	private boolean donegl;
	@Expose
	@ConfigOption("Level Head")
	public boolean levelhead;
	private Map<UUID, String> levelCache = new HashMap<>();
	@Expose
	private String apiKey;
	private HypixelAPI api;
	public Deque<Request> requests = new ArrayDeque<>();
	public Request request;
	private KeyBinding keyAcceptRequest = new KeyBinding("Accept Request", Keyboard.KEY_Y, "Sol Client");
	private KeyBinding keyDismissRequest = new KeyBinding("Dismiss Request", Keyboard.KEY_N, "Sol Client");
	private HypixelLocationData locationData;
	private Pattern locrawTrigger = Pattern.compile("\\{(\".*\":\".*\",)+\".*\":\".*\"\\}");

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
//        return true; // Uncomment for testing purposes
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
				Client.INSTANCE.registerCommand("visithousing", new VisitHousingCommand(this));
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
				Client.INSTANCE.registerCommand("chat", new ChatChannelCommand(this));
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
			IChatComponent component = new ChatComponentText("Could not find API key (required for Levelhead). Click here or run /api new.");
			component.setChatStyle(new ChatStyle()
					.setColor(EnumChatFormatting.RED)
					.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/api new")));

			mc.ingameGUI.getChatGUI().printChatMessage(component);
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

		if(!isHypixel()) {
			return;
		}

		locationData = null;
		ticksUntilLocraw = 20;
	}

	@EventHandler
	public void onMessage(ReceiveChatMessageEvent event) {
		if(!isHypixel()) {
			return;
		}

		if(locrawTrigger.matcher(event.message).matches() && locationData == null) {
			try {
				event.cancelled = true;
				locationData = new Gson().fromJson(event.message, HypixelLocationData.class);
				return;
			}
			catch(Throwable error) {
				LOGGER.warn("Could not detect location", error);
			}
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

		if(autogl && !donegl && event.message.equals(autoglTrigger)) {
			ticksUntilAutogl = 20;
			return;
		}

		if(hidegl && hideglTrigger.matcher(event.message).matches()) {
			event.cancelled = true;
			return;
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
	public void onTick(PostTickEvent event) {
		if(ticksUntilLocraw != -1 && --ticksUntilLocraw == 0) {
			ticksUntilLocraw = -1;

			mc.thePlayer.sendChatMessage("/locraw");
		}

		if(ticksUntilAutogl != -1 && --ticksUntilAutogl == 0) {
			if(locationData != null && "BEDWARS".equals(locationData.getType()) && !("BEDWARS_EIGHT_ONE".equals(locationData.getMode())) || "BEDWARS_CASTLE".equals(locationData.getMode())) {
				mc.thePlayer.sendChatMessage("/shout glhf");
				return;
			}

			donegl = true;
			mc.thePlayer.sendChatMessage("/achat glhf");

			ticksUntilAutogl = -1;
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

}
