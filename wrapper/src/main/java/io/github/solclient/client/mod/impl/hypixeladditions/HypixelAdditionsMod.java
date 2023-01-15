/**
 * Many of these options are based on the ideas of Sk1er LLC's mods.
 */

package io.github.solclient.client.mod.impl.hypixeladditions;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.mod.impl.hypixeladditions.commands.*;
import io.github.solclient.client.packet.Popup;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import net.hypixel.api.HypixelAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;

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
	// Borrowed (nicked) and updated from
	// https://static.sk1er.club/autogg/regex_triggers_3.json.
	private final List<Pattern> autoggTriggers = Arrays.asList(
			"^ +1st Killer - ?\\[?\\w*\\+*\\]? \\w+ - \\d+(?: Kills?)?$",
			"^ *1st (?:Place ?)?(?:-|:)? ?\\[?\\w*\\+*\\]? \\w+(?: : \\d+| - \\d+(?: Points?)?| - \\d+(?: x .)?| \\(\\w+ .{1,6}\\) - \\d+ Kills?|: \\d+:\\d+| - \\d+ (?:Zombie )?(?:Kills?|Blocks? Destroyed)| - \\[LINK\\])?$",
			"^ +Winn(?:er #1 \\(\\d+ Kills\\): \\w+ \\(\\w+\\)|er(?::| - )(?:Hiders|Seekers|Defenders|Attackers|PLAYERS?|MURDERERS?|Red|Blue|RED|BLU|\\w+)(?: Team)?|ers?: ?\\[?\\w*\\+*\\]? \\w+(?:, ?\\[?\\w*\\+*\\]? \\w+)?|ing Team ?[\\:-] (?:Animals|Hunters|Red|Green|Blue|Yellow|RED|BLU|Survivors|Vampires))$",
			"^ +Alpha Infected: \\w+ \\(\\d+ infections?\\)$", "^ +Murderer: \\w+ \\(\\d+ Kills?\\)$",
			"^ +You survived \\d+ rounds!$",
			"^ +(((?:UHC|SkyWars|(The )?Bridge|Sumo|Classic|OP|MegaWalls|Bow|NoDebuff|Blitz|Combo|Bow Spleef|Boxing) (?:Duel|Doubles|Teams|Deathmatch|2v2v2v2|3v3v3v3)?)|Hypixel Parkour) ?- \\d+:\\d+$",
			"^ +They captured all wools!$", "^ +Game over!$", "^ +[\\d\\.]+k?/[\\d\\.]+k? \\w+$",
			"^ +(?:Criminal|Cop)s won the game!$", "^ +\\[?\\w*\\+*\\]? \\w+ - \\d+ Final Kills$",
			"^ +Zombies - \\d*:?\\d+:\\d+ \\(Round \\d+\\)$", "^ +. YOUR STATISTICS .$", "^ {36}Winner(s?)$",
			"^ {21}Bridge CTF [a-zA-Z]+ - \\d\\d:\\d\\d$").stream().map(Pattern::compile).collect(Collectors.toList());
	@Expose
	@Option
	private boolean hidegg = false;
	private final List<Pattern> hideggTriggers = Arrays.asList(".*: (([gG]{2})|([gG]ood [gG]ame))", "\\+\\d* Karma!")
			.stream().map(Pattern::compile).collect(Collectors.toList());
	private boolean donegg;
	private final Pattern hideChannelMessageTrigger = Pattern
			.compile("(You are now in the (ALL|PARTY|GUILD|OFFICER) channel|You're already in this channel!)");
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
		if (id.version() != 4 || !(enabled && levelhead)
				|| (name.contains(Formatting.OBFUSCATED.toString()) && !isMainPlayer)) {
			return null;
		}

		if (levelCache.containsKey(id)) {
			String result = levelCache.get(id);
			if (result.isEmpty()) {
				return null;
			}
			return result;
		}

		else if (api != null) {
			levelCache.put(id, "");
			api.getPlayerByUuid(id).whenCompleteAsync((response, error) -> {
				if (!response.isSuccess() || error != null) {
					return;
				}

				if (response.getPlayer().exists()) {
					levelCache.put(id, Integer.toString((int) response.getPlayer().getNetworkLevel()));
				} else {
					// At this stage, the player is either nicked, or an NPC, but all NPCs and fake
					// players I've tested do not get to this stage.
					levelCache.put(id, Integer.toString(Utils.randomInt(180, 280))); // Based on looking at YouTubers'
																						// Hypixel levels. It won't
																						// actually be the true level,
																						// and may not look quite right,
																						// but it's more plausible than
																						// a Level 1 god bridger.
				}
			});
		}
		return null;
	}

	public boolean isLobby() {
		if (mc.player != null && mc.player.inventory != null) {
			ItemStack stack = mc.player.inventory.getInvStack(8);
			if (stack != null) {
				return stack.getCustomName()
						.equals(Formatting.GREEN + "Lobby Selector " + Formatting.GRAY + "(Right Click)");
			}
		}
		return false;
	}

	public boolean isHousing() {
		return "HOUSING".equals(Utils.getScoreboardTitle());
	}

	public static boolean isHypixel() {
//        return true; // Uncomment for testing purposes
		return DetectedServer.current() == DetectedServer.HYPIXEL;
	}

	public static boolean isEffective() {
		return enabled && isHypixel();
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	private void updateState() {
		if (Client.INSTANCE.getCommands().isRegistered("visithousing")) {
			if (isEffective()) {
				Client.INSTANCE.getCommands().register("visithousing", new VisitHousingCommand(this));
			}
		} else if (!isEffective())
			Client.INSTANCE.getCommands().unregister("visithousing");

		if (Client.INSTANCE.getCommands().isRegistered("chat")) {
			if (isEffective()) {
				if (mc.player != null) {
					mc.player.sendChatMessage("/chat a");
				}
				Client.INSTANCE.getCommands().register("chat", new ChatChannelCommand(this));
			}
		} else {
			if (!isEffective()) {
				Client.INSTANCE.getCommands().unregister("chat");
			}
		}

		if (Client.INSTANCE.getChatExtensions().getChannelSystem() == null) {
			if (isEffective()) {
				Client.INSTANCE.getChatExtensions().setChannelSystem(new HypixelChatChannels());
			}
		} else {
			if (!isEffective()
					&& Client.INSTANCE.getChatExtensions().getChannelSystem() instanceof HypixelChatChannels) {
				Client.INSTANCE.getChatExtensions().setChannelSystem(null);
			}
		}

		if (isEffective() && apiKey == null) {
			Text component = new LiteralText(
					"Could not find API key (required for Levelhead). Click here or run /api new.");
			component.setStyle(new Style().setFormatting(Formatting.RED)
					.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/api new")));

			mc.inGameHud.getChatHud().addMessage(component);
		}
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
		if (apiKey != null) {
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
	public void onWorldLoad(WorldLoadEvent event) {
		donegg = donegl = false;
		levelCache.clear();

		if (!isHypixel()) {
			return;
		}

		locationData = null;
		ticksUntilLocraw = 20;
	}

	@EventHandler
	public void onMessage(ReceiveChatMessageEvent event) {
		if (!isHypixel()) {
			return;
		}

		if (locrawTrigger.matcher(event.message).matches()) {
			try {
				event.cancelled = true;
				locationData = new Gson().fromJson(event.message, HypixelLocationData.class);
				return;
			} catch (Throwable error) {
				logger.warn("Could not detect location", error);
			}
		}

		if (hidegg) {
			for (Pattern pattern : hideggTriggers) {
				if (pattern.matcher(event.message).matches()) {
					event.cancelled = true;
					return;
				}
			}
		}

		if (hidegl && hideglTrigger.matcher(event.message).matches()) {
			event.cancelled = true;
			return;
		}

		if (hideChannelMessageTrigger.matcher(event.message).matches()) {
			event.cancelled = true;
			return;
		}

		if (event.replay) {
			return;
		}

		if (event.actionBar && isHousing() && event.message.startsWith("Now playing:")) {
			event.cancelled = true;
			return;
		}

		if (popupEvents) {
			for (String line : event.message.split("\\n")) {
				Popup popup = HypixelPopupType.popupFromMessage(line);
				if (popup != null) {
					Client.INSTANCE.getPopups().add(popup);
					return;
				}
			}
		}

		if (autogg && !donegg) {
			for (Pattern pattern : autoggTriggers) {
				if (pattern.matcher(event.message).matches()) {
					donegg = true;
					mc.player.sendChatMessage("/achat " + autoggMessage);
					return;
				}
			}
		}

		if (autogl && !donegl && event.message.equals(autoglTrigger)) {
			ticksUntilAutogl = 20;
			return;
		}

		Matcher apiKeyMatcher = apiKeyMessageTrigger.matcher(event.message);
		if (apiKeyMatcher.matches()) {
			setApiKey(apiKeyMatcher.group(1));
		}
	}

	@EventHandler
	public void onTick(PostTickEvent event) {
		if (mc.world == null) {
			return;
		}

		if (ticksUntilLocraw != -1 && --ticksUntilLocraw == 0) {
			ticksUntilLocraw = -1;

			mc.player.sendChatMessage("/locraw");
		}

		if (ticksUntilAutogl != -1 && --ticksUntilAutogl == 0) {
			if (locationData != null && (("BEDWARS".equals(locationData.getType())
					&& !("BEDWARS_EIGHT_ONE".equals(locationData.getMode())
							|| "BEDWARS_CASTLE".equals(locationData.getMode())))
					|| ("DUELS".equals(locationData.getType()) && locationData.getMode() != null
							&& !(locationData.getMode().endsWith("_DUEL")
									|| locationData.getMode().equals("DUELS_UHC_MEETUP")
									|| locationData.getMode().equals("DUELS_PARKOUR_EIGHT")))
					|| ("ARCADE".equals(locationData.getType()) && "PVP_CTW".equals(locationData.getMode()))
					|| ("SURVIVAL_GAMES".equals(locationData.getType())
							&& "teams_normal".equals(locationData.getMode()))
					|| ("BUILD_BATTLE".equals(locationData.getType())
							&& !("BUILD_BATTLE_SOLO_NORMAL".equals(locationData.getMode())
									|| "BUILD_BATTLE_GUESS_THE_BUILD".equals(locationData.getMode())))
					|| ("ARENA".equals(locationData.getType()) && !"1v1".equals(locationData.getMode()))
					|| "WALLS".equals(locationData.getType())
					|| "MCGO" /* google translate: cops & crims */ .equals(locationData.getType())
					|| "WALLS3".equals(locationData.getType())
					|| ("PROTOTYPE".equals(locationData.getType())
							&& "TOWERWARS_TEAMS_OF_TWO".equals(locationData.getMode()))
					|| ("SKYWARS".equals(locationData.getType()) && locationData.getMode() != null
							&& !(locationData.getMode().startsWith("solo_")
									|| locationData.getMode().startsWith("ranked_")))
					|| ("TNTGAMES".equals(locationData.getType()) && locationData.getMode().equals("CAPTURE"))
					|| ("UHC".equals(locationData.getType()) && !"SOLO".equals(locationData.getType()))
					|| ("SPEED_UHC".equals(locationData.getType()) && !"solo_nomal".equals(locationData.getType()))
					|| "BATTLEGROUND" /* Warlords */ .equals(locationData.getType()))) {
				mc.player.sendChatMessage("/shout " + autoglMessage);
			} else {
				mc.player.sendChatMessage("/achat " + autoglMessage);
			}

			donegl = true;

			ticksUntilAutogl = -1;
		}
	}

	@EventHandler
	public void onSoundPlay(SoundPlayEvent event) {
		if (!isHypixel()) {
			return;
		}

		if (isLobby()) {
			if (event.soundName.startsWith("mob") || event.soundName.equals("random.orb")
					|| event.soundName.equals("random.pop") || event.soundName.equals("random.levelup")
					|| event.soundName.equals("game.tnt.primed") || event.soundName.equals("random.explode")
					|| event.soundName.equals("mob.chicken.plop") || event.soundName.startsWith("note")
					|| event.soundName.equals("random.click") || event.soundName.startsWith("fireworks")
					|| event.soundName.equals("fire.fire") || event.soundName.equals("random.bow")) {
				event.volume *= lobbySoundsVolume / 100F;
			}
		} else {
			if (isHousing() && event.soundName.startsWith("note")) {
				event.volume *= housingMusicVolume / 100F;
			}
		}
	}

}
