package io.github.solclient.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import io.github.solclient.client.api.ClientApi;
import io.github.solclient.client.api.PopupManager;
import io.github.solclient.client.config.ConfigVersion;
import io.github.solclient.client.culling.CullTask;
import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.GameQuitEvent;
import io.github.solclient.client.event.impl.PostGameStartEvent;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.event.impl.SendChatMessageEvent;
import io.github.solclient.client.event.impl.ServerConnectEvent;
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
import io.github.solclient.client.mod.impl.hud.PotionEffectsMod;
import io.github.solclient.client.mod.impl.hud.ReachDisplayMod;
import io.github.solclient.client.mod.impl.hud.ScoreboardMod;
import io.github.solclient.client.mod.impl.hud.armour.ArmourMod;
import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import io.github.solclient.client.mod.impl.hud.crosshair.CrosshairMod;
import io.github.solclient.client.mod.impl.hud.keystrokes.KeystrokesMod;
import io.github.solclient.client.mod.impl.hud.ping.PingMod;
import io.github.solclient.client.mod.impl.hud.speedometer.SpeedometerMod;
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
import io.github.solclient.client.util.access.AccessMinecraft;
import io.github.solclient.client.util.font.SlickFontRenderer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

/**
 * Main class for Sol Client.
 */
public class Client {

	public static final Client INSTANCE = new Client();

	private final Minecraft mc = Minecraft.getMinecraft();
	private JsonObject data;
	@Getter
	private List<Mod> mods = new ArrayList<Mod>();
	private Map<String, Mod> modsById = new HashMap<>();
	@Getter
	private List<HudElement> huds = new ArrayList<HudElement>();
	public static final Logger LOGGER = LogManager.getLogger();

	private final File DATA_FILE = new File(mc.mcDataDir, "sol_client_mods.json");
	// data file for beta versions - this is no longer very neccessary.
	private final File LEGACY_DATA_FILE = new File(mc.mcDataDir, "parrot_client_mods.json");

	public DetectedServer detectedServer;

	public EventBus bus = new EventBus();

	private Map<ResourceLocation, IResource> resources = new HashMap<>();
	private Map<String, CommandBase> commands = new HashMap<>();
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
	private GuiMainMenu mainMenu;

	public void init() {
		Utils.resetLineWidth();
		new File(mc.mcDataDir, "server-resource-packs").mkdirs(); // Fix crash
		System.setProperty("http.agent", "Sol Client/" + Client.VERSION);

		LOGGER.info("Initialising...");
		bus.register(this);

		CpsMonitor.forceInit();

		LOGGER.info("Loading settings...");

		if(!DATA_FILE.exists() && LEGACY_DATA_FILE.exists()) {
			LEGACY_DATA_FILE.renameTo(DATA_FILE);
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

			private WorldClient world;

			@Override
			public boolean prepareChunk(int x, int z) {
				return (world = mc.theWorld) != null;
			}

			@Override
			public boolean isOpaqueFullCube(int x, int y, int z) {
				return world.isBlockNormalCube(new BlockPos(x, y, z), false);
			}

		}));

		try {
			// Group together the mod file listener and culling thread
			// as it makes sense considering both tasks can deal with a 10ms pause,
			// and file listeners will not take much time.

			FilePollingTask filePolling = new FilePollingTask(mods);

			Thread generalUpdateThread = new Thread(() -> {
				while(((AccessMinecraft) mc).isRunning()) {
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

		bus.register(new ClientApi());
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

	public void registerKeyBinding(KeyBinding keyBinding) {
		mc.gameSettings.keyBindings = ArrayUtils.add(mc.gameSettings.keyBindings, keyBinding);
	}

	public void unregisterKeyBinding(KeyBinding keyBinding) {
		mc.gameSettings.keyBindings = ArrayUtils.removeElement(mc.gameSettings.keyBindings, keyBinding);
		keyBinding.setKeyCode(0);
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
			if(DATA_FILE.exists()) {
				// 1.8 uses old libraries, so this warning cannot be easily fixed.
				data = new JsonParser().parse(FileUtils.readFileToString(DATA_FILE)).getAsJsonObject();
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
			FileUtils.writeStringToFile(DATA_FILE, gson.toJson(data));
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

	public void addResource(ResourceLocation location, IResource resource) {
		resources.put(location, resource);
	}

	public IResource getResource(ResourceLocation location) {
		return resources.get(location);
	}

	public CommandBase getCommand(String name) {
		return commands.get(name);
	}

	public Set<String> getCommandNames() {
		return commands.keySet();
	}

	public CommandBase registerCommand(String name, CommandBase command) {
		commands.put(name, command);
		for(String alias : command.getCommandAliases()) {
			commands.put(alias, command);
		}

		return command;
	}

	public CommandBase unregisterCommand(String name) {
		CommandBase command;

		for(String alias : (command = commands.remove(name)).getCommandAliases()) {
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
	public void onPostStart(PostGameStartEvent event) {
		mods.forEach(Mod::postStart);
		cacheHudList();

		try {
			unregisterKeyBinding((KeyBinding) GameSettings.class.getField("ofKeyBindZoom").get(mc.gameSettings));
		}
		catch(NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
			// OptiFine is not enabled.
		}
	}

	@EventHandler
	public void onSendMessage(SendChatMessageEvent event) {
		// TODO Tab completion. Skipped during port to mixin.

		if(event.message.startsWith("/")) {
			List<String> args = new ArrayList<>(Arrays.asList(event.message.split(" ")));
			String commandKey = args.get(0).substring(1);
			if(commands.containsKey(commandKey)) {
				event.cancelled = true;

				try {
					args.remove(0);

					commands.get(commandKey).processCommand(mc.thePlayer, args.toArray(new String[0]));
				}
				catch(CommandException error) {
					mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.RED + error.getMessage()));
				}
				catch(Exception error) {
					mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could " +
							"not execute client-sided command, see log for details"));
					LOGGER.info("Could not execute client-sided command: " + event.message + ", error: ", error);
				}
			}
		}
		else if(getChatChannelSystem() != null) {
			event.cancelled = true;
			getChatChannelSystem().getChannel().sendMessage(mc.thePlayer, event.message);
		}
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if(SolClientMod.instance.modsKey.isPressed()) {
			mc.displayGuiScreen(new ModsScreen());
		}
		else if(SolClientMod.instance.editHudKey.isPressed()) {
			mc.displayGuiScreen(new ModsScreen());
			mc.displayGuiScreen(new MoveHudsScreen());
		}
	}

	@EventHandler
	public void onQuit(GameQuitEvent event) {
		SlickFontRenderer.DEFAULT.free();
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
		if(!(mc.currentScreen instanceof ModsScreen)) {
			save();
		}
	}

}
