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

package io.github.solclient.client.mod.impl.hypixeladditions;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.StandardMod;
import io.github.solclient.client.mod.impl.api.chat.ChatApiMod;
import io.github.solclient.client.mod.impl.api.commands.CommandsApiMod;
import io.github.solclient.client.mod.impl.api.popups.*;
import io.github.solclient.client.mod.impl.hypixeladditions.commands.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;

// Many of these options are based on the ideas of Sk1er LLC's mods.
public class HypixelAdditionsMod extends StandardMod {

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
	@Expose
	private String apiKey;
	private HypixelLocationData locationData;
	private final Pattern locrawTrigger = Pattern.compile("\\{(\".*\":\".*\",)?+\".*\":\".*\"\\}");

	public String getLevelhead(boolean isMainPlayer, String name, UUID id) {
		if (id.version() != 4 || !(enabled && levelhead)
				|| (name.contains(Formatting.OBFUSCATED.toString()) && !isMainPlayer)) {
			return null;
		}

		return HypixelAPICache.getInstance().getLevelHead(id);
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
		return "HOUSING".equals(MinecraftUtils.getScoreboardTitle());
	}

	public static boolean isHypixel() {
//        return true; // Uncomment for testing purposes
		return DetectedServer.current() == DetectedServer.HYPIXEL;
	}

	public static boolean isEffective() {
		return enabled && isHypixel();
	}

	private void updateState() {
		if (CommandsApiMod.instance.isRegistered("visithousing")) {
			if (isEffective()) {
				CommandsApiMod.instance.register("visithousing", new VisitHousingCommand(this));
			}
		} else if (!isEffective())
			CommandsApiMod.instance.unregister("visithousing");

		if (CommandsApiMod.instance.isRegistered("chat")) {
			if (isEffective()) {
				if (mc.player != null) {
					mc.player.sendChatMessage("/chat a");
				}
				CommandsApiMod.instance.register("chat", new ChatChannelCommand(this));
			}
		} else {
			if (!isEffective()) {
				CommandsApiMod.instance.unregister("chat");
			}
		}

		if (ChatApiMod.instance.getChannelSystem() == null) {
			if (isEffective()) {
				ChatApiMod.instance.setChannelSystem(new HypixelChatChannels());
			}
		} else {
			if (!isEffective()
					&& ChatApiMod.instance.getChannelSystem() instanceof HypixelChatChannels) {
				ChatApiMod.instance.setChannelSystem(null);
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
            HypixelAPICache.getInstance().setAPIKey(apiKey);
		}
	}

	@Override
	public void init() {
		super.init();
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
		HypixelAPICache.getInstance().clear();

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

		if (locrawTrigger.matcher(event.originalMessage).matches()) {
			try {
				event.cancelled = true;
				locationData = new Gson().fromJson(event.originalMessage, HypixelLocationData.class);
				return;
			} catch (Throwable error) {
				logger.warn("Could not detect location", error);
			}
		}

		if (hidegg) {
			for (Pattern pattern : hideggTriggers) {
				if (pattern.matcher(event.originalMessage).matches()) {
					event.cancelled = true;
					return;
				}
			}
		}

		if (hidegl && hideglTrigger.matcher(event.originalMessage).matches()) {
			event.cancelled = true;
			return;
		}

		if (hideChannelMessageTrigger.matcher(event.originalMessage).matches()) {
			event.cancelled = true;
			return;
		}

		if (event.replay) {
			return;
		}

		if (event.actionBar && isHousing() && event.originalMessage.startsWith("Now playing:")) {
			event.cancelled = true;
			return;
		}

		if (popupEvents) {
			for (String line : event.originalMessage.split("\\n")) {
				Popup popup = HypixelPopupType.popupFromMessage(line);
				if (popup != null) {
					PopupsApiMod.instance.add(popup);
					return;
				}
			}
		}

		if (autogg && !donegg) {
			for (Pattern pattern : autoggTriggers) {
				if (pattern.matcher(event.originalMessage).matches()) {
					donegg = true;
					mc.player.sendChatMessage("/achat " + autoggMessage);
					return;
				}
			}
		}

		if (autogl && !donegl && event.originalMessage.equals(autoglTrigger)) {
			ticksUntilAutogl = 20;
			return;
		}

		Matcher apiKeyMatcher = apiKeyMessageTrigger.matcher(event.originalMessage);
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
