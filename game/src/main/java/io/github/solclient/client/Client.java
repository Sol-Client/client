package io.github.solclient.client;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.logisticscraft.occlusionculling.DataProvider;
import com.logisticscraft.occlusionculling.OcclusionCullingInstance;

import io.github.solclient.client.packet.PacketApi;
import io.github.solclient.client.packet.PopupManager;
import io.github.solclient.abstraction.mc.Environment;
import io.github.solclient.abstraction.mc.Identifier;
import io.github.solclient.abstraction.mc.MinecraftClient;
import io.github.solclient.abstraction.mc.network.ServerData;
import io.github.solclient.abstraction.mc.option.KeyBinding;
import io.github.solclient.abstraction.mc.screen.TitleScreen;
import io.github.solclient.abstraction.mc.text.TextColour;
import io.github.solclient.abstraction.mc.text.LiteralText;
import io.github.solclient.abstraction.mc.text.Style;
import io.github.solclient.abstraction.mc.world.item.ItemType;
import io.github.solclient.abstraction.mc.world.level.Level;
import io.github.solclient.abstraction.mc.world.level.block.BlockPos;
import io.github.solclient.client.command.Command;
import io.github.solclient.client.command.CommandException;
import io.github.solclient.client.config.ConfigVersion;
import io.github.solclient.client.culling.CullTask;
import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostStartEvent;
import io.github.solclient.client.event.impl.network.ServerConnectEvent;
import io.github.solclient.client.event.impl.network.chat.OutgoingChatMessageEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.BlockSelectionMod;
import io.github.solclient.client.mod.impl.ChunkAnimatorMod;
import io.github.solclient.client.mod.impl.ColourSaturationMod;
import io.github.solclient.client.mod.impl.FreelookMod;
import io.github.solclient.client.mod.impl.HitColourMod;
import io.github.solclient.client.mod.impl.HitboxMod;
import io.github.solclient.client.mod.impl.MenuBlurMod;
import io.github.solclient.client.mod.impl.MotionBlurMod;
import io.github.solclient.client.mod.impl.ParticlesMod;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.mod.impl.TNTTimerMod;
import io.github.solclient.client.mod.impl.TaplookMod;
import io.github.solclient.client.mod.impl.TimeChangerMod;
import io.github.solclient.client.mod.impl.TweaksMod;
import io.github.solclient.client.mod.impl.V1_7VisualsMod;
import io.github.solclient.client.mod.impl.ZoomMod;
import io.github.solclient.client.mod.impl.discordrpc.DiscordIntegrationMod;
import io.github.solclient.client.mod.impl.hud.ComboCounterMod;
import io.github.solclient.client.mod.impl.hud.CoordinatesMod;
import io.github.solclient.client.mod.impl.hud.CpsMod;
import io.github.solclient.client.mod.impl.hud.FpsMod;
import io.github.solclient.client.mod.impl.hud.PingMod;
import io.github.solclient.client.mod.impl.hud.PotionEffectsMod;
import io.github.solclient.client.mod.impl.hud.ReachDisplayMod;
import io.github.solclient.client.mod.impl.hud.ScoreboardMod;
import io.github.solclient.client.mod.impl.hud.SpeedometerMod;
import io.github.solclient.client.mod.impl.hud.armour.ArmourMod;
import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import io.github.solclient.client.mod.impl.hud.crosshair.CrosshairMod;
import io.github.solclient.client.mod.impl.hud.keystrokes.KeystrokesMod;
import io.github.solclient.client.mod.impl.hud.tablist.TabListMod;
import io.github.solclient.client.mod.impl.hud.timers.TimersMod;
import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAdditionsMod;
import io.github.solclient.client.mod.impl.itemphysics.ItemPhysicsMod;
import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.mod.impl.togglesprint.ToggleSprintMod;
import io.github.solclient.client.ui.ChatButton;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.ui.screen.mods.MoveHudsScreen;
import io.github.solclient.client.util.Utils;
import lombok.Getter;
import lombok.Setter;

/**
 * Main class for Sol Client.
 */
public class Client {

	private MinecraftClient mc = MinecraftClient.getInstance();
	public static final Client INSTANCE = new Client();
	private JsonObject data;
	@Getter
	private List<Mod> mods = new ArrayList<>();
	private Map<String, Mod> modsById = new HashMap<>();
	@Getter
	private List<HudElement> huds = new ArrayList<>();
	private static final Logger LOGGER = LogManager.getLogger();

	private final File dataFile = new File(mc.getDataFolder(), "sol_client_mods.json");
	private final File legacyDataFile = new File(mc.getDataFolder(), "parrot_client_mods.json" /* This was the old name. */ );

	public DetectedServer detectedServer;

	@Getter
	private EventBus bus = new EventBus();

	private Map<Identifier, Supplier<String>> resources = new HashMap<>();
	private Map<String, Command> commands = new HashMap<>();
	private List<ChatButton> chatButtons = new ArrayList<>();

	private ChatChannelSystem chatChannelSystem;

	public static final String VERSION = System.getProperty("io.github.solclient.client.version", "DEVELOPMENT TEST");
	public static final String NAME = "Sol Client " + VERSION;
	public static final String KEY_TRANSLATION_KEY = "sol_client.key";
	public static final String KEY_CATEGORY = KEY_TRANSLATION_KEY + ".category";
	public static final boolean DEV = isDevelopment();

	@Getter
	private PopupManager popupManager;
	@Getter
	private CapeManager capeManager;
	@Getter
	@Setter
	private TitleScreen mainMenu;

	public void init() {
		Utils.resetLineWidth();
		new File(mc.getDataFolder(), "server-resource-packs").mkdirs(); // Fix crash

		System.setProperty("http.agent", "Sol Client/" + VERSION);

		LOGGER.info("Initialising...");
		bus.register(this);

		CpsCounter.register();

		LOGGER.info("Loading settings...");

		if(!dataFile.exists() && legacyDataFile.exists()) {
			legacyDataFile.renameTo(dataFile);
		}

		load();

		LOGGER.info("Loading mods...");

		register(new SolClientMod());
		register(new FpsMod());
		register(new CoordinatesMod());
		register(new KeystrokesMod());
		register(new CpsMod());
		register(new PingMod());
		register(new SpeedometerMod());
		register(new ReachDisplayMod());
		register(new ComboCounterMod());
		register(new PotionEffectsMod());
		register(new ArmourMod());
		register(new TimersMod());
		register(new ChatMod());
		register(new TabListMod());
		register(new CrosshairMod());
		register(new ScoreboardMod());
		register(new TweaksMod());
		register(new MotionBlurMod());
		register(new MenuBlurMod());
		register(new ColourSaturationMod());
		register(new ChunkAnimatorMod());
		register(new SCReplayMod());
		register(new FreelookMod());
		register(new TaplookMod());
		register(new ToggleSprintMod());
		register(new TNTTimerMod());
		register(new V1_7VisualsMod());
		register(new ItemPhysicsMod());
		register(new ZoomMod());
		register(new ParticlesMod());
		register(new TimeChangerMod());
		register(new BlockSelectionMod());
		register(new HitboxMod());
		register(new HitColourMod());
		register(new HypixelAdditionsMod());
		register(new QuickPlayMod());
		register(new DiscordIntegrationMod());

		LOGGER.info("Loaded {} mods", mods.size());

		LOGGER.info("Saving settings...");
		save();

		LOGGER.info("Starting culling thread...");

		CullTask cullingTask = new CullTask(new OcclusionCullingInstance(128, new DataProvider() {

			private Level level;

			@Override
			public boolean prepareChunk(int x, int z) {
				return (level = mc.getLevel()) != null;
			}

			@Override
			public boolean isOpaqueFullCube(int x, int y, int z) {
				return level.isOpaqueFullCube(BlockPos.create(x, y, z));
			}

		}));

		try {
			// Group together the mod file listener and culling thread
			// as it makes sense considering both tasks can deal with a 10ms pause,
			// and file listeners will not take much time.

			FilePollingTask filePolling = new FilePollingTask(mods);

			Thread generalUpdateThread = new Thread(() -> {
				while(mc.isRunning()) {
					try {
						Thread.sleep(10);
					}
					catch(InterruptedException error) {
						return;
					}

					cullingTask.run();

					if(filePolling != null) {
						filePolling.run();
					}
				}

				filePolling.close();
			}, "Async Updates");
			generalUpdateThread.setUncaughtExceptionHandler((thread, error) -> {
				LOGGER.error("Async updates has crashed", error);
			});
			generalUpdateThread.start();
		}
		catch(IOException error) {
			LOGGER.error("Could not start async updates thread", error);
		}

		bus.register(new PacketApi());
		bus.register(popupManager = new PopupManager());

		capeManager = new CapeManager();
	}

	private static boolean isDevelopment() {
		for(StackTraceElement element : Thread.currentThread().getStackTrace()) {
			if(element.getClassName().equals("GradleStart")) {
				return true;
			}
		}

		return false;
	}

	private Gson getGson(Mod mod) {
		GsonBuilder builder = new GsonBuilder();
		if(mod != null) {
			builder.registerTypeAdapter(mod.getClass(), (InstanceCreator<Mod>) (type) -> mod);
		}
		return builder.excludeFieldsWithoutExposeAnnotation().create();
	}

	private void cacheHudList() {
		huds.clear();
		for(Mod mod : mods) {
			huds.addAll(mod.getHudElements());
		}
	}

	@SuppressWarnings("deprecation")
	public boolean load() {
		try {
			if(dataFile.exists()) {
				// 1.8 uses old libraries, so this warning cannot be easily fixed.
				data = new JsonParser().parse(FileUtils.readFileToString(dataFile)).getAsJsonObject();
				data = ConfigVersion.migrate(data);
			}
			else {
				data = new JsonObject();
				data.addProperty("version", ConfigVersion.values()[ConfigVersion.values().length - 1].name());
			}
			return true;
		}
		catch(IOException error) {
			LOGGER.error("Could not load data", error);
			data = new JsonObject();
			return false;
		}
	}

	public boolean save() {
		Gson gson = getGson(null);

		for(Mod mod : mods) {
			data.add(mod.getId(), gson.toJsonTree(mod));
		}

		try {
			FileUtils.writeStringToFile(dataFile, gson.toJson(data), StandardCharsets.UTF_8);
			return true;
		}
		catch(IOException error) {
			LOGGER.error("Could not save data", error);
			return false;
		}
	}

	private void register(Mod mod) {
		try {
			if(data.has(mod.getId())) {
				getGson(mod).fromJson(data.get(mod.getId()), mod.getClass());
			}
			mods.add(mod);

			modsById.put(mod.getId(), mod);

			mod.onRegister();
		}
		catch(Throwable error) {
			LOGGER.error("Could not register mod " + mod.getId(), error);
			mods.remove(mod);
		}
	}

	public Mod getModById(String id) {
		return modsById.get(id);
	}

	public void addPsuedoResource(Identifier location, Supplier<String> resource) {
		resources.put(location, resource);
	}

	public String getPsuedoResource(Identifier location) {
		return resources.get(location).get();
	}

	public Command getCommand(String name) {
		return commands.get(name);
	}

	public Set<String> getCommandNames() {
		return commands.keySet();
	}

	public Command registerCommand(String name, Command command) {
		commands.put(name, command);
		for(String alias : command.getAliases()) {
			commands.put(alias, command);
		}

		return command;
	}

	public Command unregisterCommand(String name) {
		Command command;

		for(String alias : (command = commands.remove(name)).getAliases()) {
			commands.remove(alias);
		}

		return command;
	}

	public void setChatChannelSystem(ChatChannelSystem chatChannelSystem) {
		this.chatChannelSystem = chatChannelSystem;

		if(chatChannelSystem != null) {
			registerChatButton(ChatChannelSystem.ChatChannelButton.INSTANCE);
		}
		else {
			unregisterChatButton(ChatChannelSystem.ChatChannelButton.INSTANCE);
		}
	}

	public ChatChannelSystem getChatChannelSystem() {
		return chatChannelSystem;
	}

	@EventHandler
	public void onPostStart(PostStartEvent event) {
		mods.forEach(Mod::postStart);
		cacheHudList();

		try {
			mc.getOptions().removeKey((KeyBinding) mc.getOptions().getClass().getField("ofKeyBindZoom").get(mc.getOptions()));
		}
		catch(NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
			// OptiFine is not enabled.
		}
	}

	@EventHandler
	public void onSendMessage(OutgoingChatMessageEvent event) {
		// TODO Tab completion. Skipped during port to mixin.

		if(event.getMessage().startsWith("/")) {
			List<String> args = new ArrayList<>(Arrays.asList(event.getMessage().split(" ")));
			String commandKey = args.get(0).substring(1);
			if(commands.containsKey(commandKey)) {
				event.cancel();

				try {
					args.remove(0);

					commands.get(commandKey).execute(mc.getPlayer(), args);
				}
				catch(CommandException error) {
					mc.getPlayer().sendSystemMessage(LiteralText.create(error.getMessage())
							.withStyle((style) -> style.setColour(TextColour.RED)));
				}
				catch(Exception error) {
					mc.getPlayer()
							.sendSystemMessage(LiteralText
									.create("Could " + "not execute client-sided command. See log for extra details.")
									.withStyle((style) -> style.setColour(TextColour.RED)));
					LOGGER.info("Could not execute client-sided command: " + event.getMessage() + ", error: ", error);
				}
			}
		}
		else if(getChatChannelSystem() != null) {
			event.cancel();
			getChatChannelSystem().getChannel().sendMessage(mc.getPlayer(), event.getMessage());
		}
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(SolClientMod.instance.modsKey.isHeld()) {
			mc.setScreen(new ModsScreen());
		}
		else if(SolClientMod.instance.editHudKey.isHeld()) {
			mc.setScreen(new ModsScreen());
			mc.setScreen(new MoveHudsScreen());
		}
	}

	public void registerChatButton(ChatButton button) {
		chatButtons.add(button);
		chatButtons.sort(Comparator.comparingInt(ChatButton::getPriority));
	}

	public void unregisterChatButton(ChatButton button) {
		chatButtons.remove(button);
	}

	public void onServerChange(ServerData data) {
		setChatChannelSystem(null);

		if(data == null) {
			detectedServer = null;
			mods.forEach(Mod::unblock);
		}

		if(data != null) {
			for(DetectedServer server : DetectedServer.values()) {
				if(server.matches(data)) {
					detectedServer = server;
					mods.stream().filter(server::shouldBlockMod).forEach(Mod::block);
					break;
				}
			}
		}

		bus.post(new ServerConnectEvent(data, detectedServer));
	}

	public List<ChatButton> getChatButtons() {
		return chatButtons;
	}

	/**
	 * Saves if the mod screen is not opened.
	 */
	public void optionChanged() {
		if(!(MinecraftClient.getInstance().getScreen() instanceof ModsScreen)) {
			save();
		}
	}

}
