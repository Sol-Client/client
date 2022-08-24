/**
 * Many of these options are based on the ideas of Sk1er LLC's mods.
 */

package io.github.solclient.client.mod.impl.hypixeladditions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.DetectedServer;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.network.ServerConnectEvent;
import io.github.solclient.client.event.impl.network.chat.ActionBarPlayEvent;
import io.github.solclient.client.event.impl.network.chat.IncomingChatMessageEvent;
import io.github.solclient.client.event.impl.sound.SoundPlayEvent;
import io.github.solclient.client.event.impl.world.level.LevelLoadEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.mod.impl.hypixeladditions.commands.ChatChannelCommand;
import io.github.solclient.client.mod.impl.hypixeladditions.commands.VisitHousingCommand;
import io.github.solclient.client.packet.Popup;
import io.github.solclient.client.platform.mc.text.ClickEvent;
import io.github.solclient.client.platform.mc.text.ClickEvent.Action;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.text.TextColour;
import io.github.solclient.client.platform.mc.text.TextFormatting;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import io.github.solclient.client.util.ApacheHttpClient;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.AutoGGMessage;
import io.github.solclient.client.util.data.AutoGLMessage;
import net.hypixel.api.HypixelAPI;

public class HypixelAdditionsMod extends Mod {

	private static boolean enabled;
	public static HypixelAdditionsMod instance;
	@Expose
	@Option
	public boolean visitHousingCommand = true;
	@Expose
	@Option
	@Slider(min = 0, max = 100, step = 1, format = "sol_client.slider.percent")
	public float lobbySoundsVolume = 100;
	@Expose
	@Option
	@Slider(min = 0, max = 100, step = 1, format = "sol_client.slider.percent")
	public float housingMusicVolume = 100;
	@Expose
	@Option
	private boolean popupEvents = true;
	@Expose
	@Option
	private boolean autogg = true;
	@Expose
	@Option
	private AutoGGMessage autoggMessage = AutoGGMessage.GG;
	// Borrowed (nicked) and updated from https://static.sk1er.club/autogg/regex_triggers_3.json.
	private final List<Pattern> autoggTriggers = Arrays.asList(
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
	@Option
	private boolean hidegg = false;
	private final List<Pattern> hideggTriggers = Arrays.asList(
			".*: (([gG]{2})|([gG]ood [gG]ame))",
			"\\+\\d* Karma!").stream().map(Pattern::compile).collect(Collectors.toList());
	private boolean donegg;
	private final Pattern hideChannelMessageTrigger = Pattern.compile("(You are now in the (ALL|PARTY|GUILD|OFFICER) channel|You're already in this channel!)");
	private final Pattern apiKeyMessageTrigger = Pattern.compile("Your new API key is (.*)");
	@Expose
	@Option
	private boolean autogl;
	@Expose
	@Option
	private AutoGLMessage autoglMessage = AutoGLMessage.GLHF;
	private final String autoglTrigger = "The game starts in 1 second!";
	private long ticksUntilAutogl = -1;
	private long ticksUntilLocraw = -1;
	@Expose
	@Option
	private boolean hidegl = false;
	private final Pattern hideglTrigger = Pattern.compile(".*: [gG](ood )?[lL](uck,? ?)?([hH](ave )?[fF](un)?!?)?");
	private boolean donegl;
	@Expose
	@Option
	public boolean levelhead;
	private final Map<UUID, String> levelCache = new HashMap<>();
	@Expose
	private String apiKey;
	private HypixelAPI api;
	private HypixelLocationData locationData;
	private final Pattern locrawTrigger = Pattern.compile("\\{(\".*\":\".*\",)?+\".*\":\".*\"\\}");

	@Override
	public String getId() {
		return "hypixel_util";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.INTEGRATION;
	}

	public String getLevelhead(boolean isMainPlayer, String name, UUID id) {
		if((!(enabled && levelhead))
				|| (name.contains(TextFormatting.OBFUSCATED.toString()) && !isMainPlayer)) {
			return null;
		}

		if(levelCache.containsKey(id)) {
			String result = levelCache.get(id);
			if(result.isEmpty()) {
				return null;
			}
			return result;
		}

		else if(api != null) {
			levelCache.put(id, "");
			api.getPlayerByUuid(id).whenCompleteAsync((response, error) -> {
				if(!response.isSuccess() || error != null) {
					return;
				}

				if(response.getPlayer().exists()) {
					levelCache.put(id, Integer.toString((int) response.getPlayer().getNetworkLevel()));
				}
				else {
					// At this stage, the player is either nicked, or an NPC, but all NPCs and fake players I've tested do not get to this stage.
					levelCache.put(id, Integer.toString(Utils.randomInt(180, 280))); // Based on looking at YouTubers' Hypixel levels. It won't actually be the true level, and may not look quite right, but it's more plausible than a Level 1 god bridger.
				}
			});
		}
		return null;
	}

	public boolean isLobby() {
		if(mc.hasPlayer()) {
			ItemStack stack = mc.getPlayer().getInventory().getItem(8);
			if(stack != null) {
				return stack.getLegacyDisplayName()
						.equals(TextFormatting.GREEN + "Lobby Selector " + TextFormatting.GREY + "(Right Click)");
			}
		}
		return false;
	}

	public boolean isHousing() {
		return Text.plainEquals(mc.getLevel().getScoreboardTitle(), "HOUSING");
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
				if(mc.hasPlayer()) {
					mc.getPlayer().executeCommand("chat a");
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
			mc.getPlayer().sendSystemMessage(Text.literal("Could not find API key (required for Levelhead). Click here or run /api new.").withStyle((style) -> {
				style.setColour(TextColour.RED);
				style.setClickEvent(ClickEvent.create(Action.RUN_COMMAND, "/api"));
			}));
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
		instance = this;
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
	public void onWorldLoad(LevelLoadEvent event) {
		donegg = donegl = false;
		levelCache.clear();

		if(!isHypixel()) {
			return;
		}

		locationData = null;
		ticksUntilLocraw = 20;
	}

	@EventHandler
	public void onMessage(IncomingChatMessageEvent event) {
		if(!isHypixel()) {
			return;
		}

		if(locrawTrigger.matcher(event.getPlainText()).matches()) {
			try {
				event.cancel();
				locationData = new Gson().fromJson(event.getPlainText(), HypixelLocationData.class);
				return;
			}
			catch(Throwable error) {
				logger.warn("Could not detect location", error);
			}
		}

		if(hidegg) {
			for(Pattern pattern : hideggTriggers) {
				if(pattern.matcher(event.getPlainText()).matches()) {
					event.cancel();
					return;
				}
			}
		}

		if(hidegl && hideglTrigger.matcher(event.getPlainText()).matches()) {
			event.cancel();
			return;
		}


		if(hideChannelMessageTrigger.matcher(event.getPlainText()).matches()) {
			event.cancel();
			return;
		}

		if(event.isReplay()) {
			return;
		}

		if(popupEvents) {
			for(String line : event.getPlainText().split("\\n")) {
				Popup popup = HypixelPopupType.popupFromMessage(line);
				if(popup != null) {
					Client.INSTANCE.getPopupManager().add(popup);
					return;
				}
			}
		}

		if(autogg && !donegg) {
			for(Pattern pattern : autoggTriggers) {
				if(pattern.matcher(event.getPlainText()).matches()) {
					donegg = true;
					mc.getPlayer().executeCommand("achat " + autoggMessage);
					return;
				}
			}
		}

		if(autogl && !donegl && event.getPlainText().equals(autoglTrigger)) {
			ticksUntilAutogl = 20;
			return;
		}

		Matcher apiKeyMatcher = apiKeyMessageTrigger.matcher(event.getPlainText());
		if(apiKeyMatcher.matches()) {
			setApiKey(apiKeyMatcher.group(1));
		}
	}

	@EventHandler
	public void onActionBar(ActionBarPlayEvent event) {
		event.setCancelled(event.isCancelled() || (isHousing() && event.getPlainText().startsWith("Now playing:")));
	}

	@EventHandler
	public void onTick(PostTickEvent event) {
		if(!mc.hasLevel()) {
			return;
		}

		if(ticksUntilLocraw != -1 && --ticksUntilLocraw == 0) {
			ticksUntilLocraw = -1;

			mc.getPlayer().executeCommand("locraw");
		}

		if(ticksUntilAutogl != -1 && --ticksUntilAutogl == 0) {
			if(locationData != null &&
					(("BEDWARS".equals(locationData.getType()) && !("BEDWARS_EIGHT_ONE".equals(locationData.getMode()) || "BEDWARS_CASTLE".equals(locationData.getMode())))
					|| ("DUELS".equals(locationData.getType()) && locationData.getMode() != null && !(locationData.getMode().endsWith("_DUEL") ||
							locationData.getMode().equals("DUELS_UHC_MEETUP") || locationData.getMode().equals("DUELS_PARKOUR_EIGHT")))
					|| ("ARCADE".equals(locationData.getType()) && "PVP_CTW".equals(locationData.getMode()))
					|| ("SURVIVAL_GAMES".equals(locationData.getType()) && "teams_normal".equals(locationData.getMode()))
					|| ("BUILD_BATTLE".equals(locationData.getType()) && !("BUILD_BATTLE_SOLO_NORMAL".equals(locationData.getMode())
							|| "BUILD_BATTLE_GUESS_THE_BUILD".equals(locationData.getMode())))
					|| ("ARENA".equals(locationData.getType()) && !"1v1".equals(locationData.getMode()))
					|| "WALLS".equals(locationData.getType())
					|| "MCGO" /* google translate: cops & crims */ .equals(locationData.getType())
					|| "WALLS3".equals(locationData.getType())
					|| ("PROTOTYPE".equals(locationData.getType()) && "TOWERWARS_TEAMS_OF_TWO".equals(locationData.getMode()))
					|| ("SKYWARS".equals(locationData.getType()) && locationData.getMode() != null &&
							!(locationData.getMode().startsWith("solo_") || locationData.getMode().startsWith("ranked_")))
					|| ("TNTGAMES".equals(locationData.getType()) && locationData.getMode().equals("CAPTURE"))
					|| ("UHC".equals(locationData.getType()) && !"SOLO".equals(locationData.getType()))
					|| ("SPEED_UHC".equals(locationData.getType()) && !"solo_nomal".equals(locationData.getType()))
					|| "BATTLEGROUND" /* Warlords */ .equals(locationData.getType()))) {
				mc.getPlayer().executeCommand("shout " + autoglMessage);
			}
			else {
				mc.getPlayer().executeCommand("achat " + autoglMessage);
			}

			donegl = true;

			ticksUntilAutogl = -1;
		}
	}

	@EventHandler
	public void onSoundPlay(SoundPlayEvent event) {
		if(!isHypixel()) {
			return;
		}

		String soundName = event.getType().getId().path();

		if(isLobby()) {
			if(soundName.startsWith("mob")
					|| soundName.equals("random.orb")
					|| soundName.equals("random.pop")
					|| soundName.equals("random.levelup")
					|| soundName.equals("game.tnt.primed")
					|| soundName.equals("random.explode")
					|| soundName.equals("mob.chicken.plop")
					|| soundName.startsWith("note")
					|| soundName.equals("random.click")
					|| soundName.startsWith("fireworks")
					|| soundName.equals("fire.fire")
					|| soundName.equals("random.bow")) {
				event.multiplyVolume(lobbySoundsVolume / 100F);
			}
		}
		else {
			if(isHousing() && soundName.startsWith("note")) {
				event.multiplyVolume(housingMusicVolume / 100F);
			}
		}
	}

}
