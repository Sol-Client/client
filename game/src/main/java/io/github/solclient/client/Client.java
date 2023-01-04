package io.github.solclient.client;

import java.io.*;
import java.util.*;

import io.github.solclient.client.mod.impl.togglesneak.ToggleSneakMod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.*;

import com.google.gson.*;
import com.logisticscraft.occlusionculling.*;

import io.github.solclient.client.api.*;
import io.github.solclient.client.config.ConfigVersion;
import io.github.solclient.client.culling.CullTask;
import io.github.solclient.client.event.*;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.impl.cosmetica.CosmeticaMod;
import io.github.solclient.client.mod.impl.discordrpc.DiscordIntegrationMod;
import io.github.solclient.client.mod.impl.hud.*;
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
import io.github.solclient.client.ui.screen.mods.*;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.access.AccessMinecraft;
import lombok.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.settings.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

/**
 * Main class for Sol Client.
 */
public final class Client {

	public static final Client INSTANCE = new Client();

	private final Minecraft mc = Minecraft.getMinecraft();
	private JsonObject data;
	@Getter
	private final List<Mod> mods = new ArrayList<Mod>();
	private final Map<String, Mod> modsById = new HashMap<>();
	@Getter
	private final List<HudElement> huds = new ArrayList<HudElement>();

	@Getter
	private PinManager pins = new PinManager();

	public static final Logger LOGGER = LogManager.getLogger();

	private final File configFolder = new File(mc.mcDataDir, "config/sol-client");
	private final File modsFile = new File(configFolder, "mods.json");
	private final File pinsFile = new File(configFolder, "pins.json");
	private final File legacyModsFile = new File(mc.mcDataDir, "sol_client_mods.json");

	public DetectedServer detectedServer;

	public EventBus bus = new EventBus();

	private Map<ResourceLocation, IResource> resources = new HashMap<>();
	private Map<String, CommandBase> commands = new HashMap<>();
	private List<ChatButton> chatButtons = new ArrayList<>();

	private ChatChannelSystem chatChannelSystem;

	@Getter
	private PopupManager popupManager;
	@Getter
	@Setter
	private GuiMainMenu mainMenu;

	private boolean remindedUpdate;

	public void init() {
		try {
			NanoVGManager.createContext();
		} catch (IOException error) {
			throw new IllegalStateException("Cannot initialise NanoVG", error);
		}

		new File(mc.mcDataDir, "server-resource-packs").mkdirs(); // Fix crash
		Utils.resetLineWidth();
		System.setProperty("http.agent", "Sol Client/" + GlobalConstants.VERSION_STRING);

		LOGGER.info("Initialising...");
		bus.register(this);

		CpsMonitor.forceInit();

		LOGGER.info("Loading settings...");

		if (legacyModsFile.exists()) {
			legacyModsFile.renameTo(modsFile);
		}

		configFolder.mkdirs();

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
		register(new CosmeticaMod());
		register(new HypixelAdditionsMod());
		register(new QuickPlayMod());
		register(new DiscordIntegrationMod());
		register(new ScrollableTooltipsMod());
		register(new ToggleSneakMod());

		LOGGER.info("Loaded {} mods", mods.size());

		try {
			pins.load(pinsFile);
		} catch (IOException error) {
			LOGGER.error("Could not load pins", error);
		}

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
				while (((AccessMinecraft) mc).isRunning()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException error) {
						return;
					}

					cullingTask.run();

					if (filePolling != null) {
						filePolling.run();
					}
				}

				filePolling.close();
			}, "Async Updates");
			generalUpdateThread.setUncaughtExceptionHandler((thread, error) -> {
				LOGGER.error("Async updates has crashed", error);
			});
			generalUpdateThread.start();
		} catch (IOException error) {
			LOGGER.error("Could not start async updates thread", error);
		}

		bus.register(new ClientApi());
		bus.register(popupManager = new PopupManager());
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
		if (mod != null) {
			builder.registerTypeAdapter(mod.getClass(), (InstanceCreator<Mod>) (type) -> mod);
		}
		return builder.excludeFieldsWithoutExposeAnnotation().create();
	}

	private void cacheHudList() {
		huds.clear();
		for (Mod mod : mods) {
			huds.addAll(mod.getHudElements());
		}
	}

	@SuppressWarnings("deprecation")
	public boolean load() {
		try {
			if (modsFile.exists()) {
				// 1.8 uses old libraries, so this warning cannot be easily fixed.
				data = new JsonParser().parse(FileUtils.readFileToString(modsFile)).getAsJsonObject();
				data = ConfigVersion.migrate(data);
			} else {
				data = new JsonObject();
				data.addProperty("version", ConfigVersion.values()[ConfigVersion.values().length - 1].name());
			}
			return true;
		} catch (IOException error) {
			LOGGER.error("Could not load data", error);
			data = new JsonObject();
			return false;
		}
	}

	public void save() {
		Gson gson = getGson(null);

		for (Mod mod : mods) {
			data.add(mod.getId(), gson.toJsonTree(mod));
		}

		try {
			FileUtils.writeStringToFile(modsFile, gson.toJson(data));
		} catch (Throwable error) {
			LOGGER.error("Could not save data", error);
		}

		try {
			pins.save(pinsFile);
		} catch (Throwable error) {
			LOGGER.error("Could not save pins", error);
		}
	}

	private void register(Mod mod) {
		try {
			// quite broken
			if (Boolean.getBoolean("io.github.solclient.client.mod." + mod.getId() + ".disable")) {
				return;
			}

			if (data.has(mod.getId())) {
				getGson(mod).fromJson(data.get(mod.getId()), mod.getClass());
			}

			mods.add(mod);
			modsById.put(mod.getId(), mod);
			mod.onRegister();
		} catch (Throwable error) {
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
		for (String alias : command.getCommandAliases()) {
			commands.put(alias, command);
		}

		return command;
	}

	public CommandBase unregisterCommand(String name) {
		CommandBase command;

		for (String alias : (command = commands.remove(name)).getCommandAliases()) {
			commands.remove(alias);
		}

		return command;
	}

	public void setChatChannelSystem(ChatChannelSystem chatChannelSystem) {
		this.chatChannelSystem = chatChannelSystem;

		if (chatChannelSystem != null) {
			registerChatButton(ChatChannelSystem.ChatChannelButton.INSTANCE);
		} else {
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
		} catch (NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
			// OptiFine is not enabled.
		}
	}

	@EventHandler
	public void onSendMessage(SendChatMessageEvent event) {
		// TODO Tab completion. Skipped during port to mixin.

		if (event.message.startsWith("/")) {
			List<String> args = new ArrayList<>(Arrays.asList(event.message.split(" ")));
			String commandKey = args.get(0).substring(1);
			if (commands.containsKey(commandKey)) {
				event.cancelled = true;

				try {
					args.remove(0);

					commands.get(commandKey).processCommand(mc.thePlayer, args.toArray(new String[0]));
				} catch (CommandException error) {
					mc.ingameGUI.getChatGUI()
							.printChatMessage(new ChatComponentText(EnumChatFormatting.RED + error.getMessage()));
				} catch (Exception error) {
					mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could "
							+ "not execute client-sided command, see log for details"));
					LOGGER.info("Could not execute client-sided command: " + event.message + ", error: ", error);
				}
			}
		} else if (getChatChannelSystem() != null) {
			event.cancelled = true;
			getChatChannelSystem().getChannel().sendMessage(mc.thePlayer, event.message);
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		Utils.USER_DATA.cancel();
		if (!remindedUpdate && SolClientMod.instance.remindMeToUpdate) {
			remindedUpdate = true;
			SemVer latest = SolClientMod.instance.latestRelease;
			if (latest != null && latest.isNewerThan(GlobalConstants.VERSION)) {
				IChatComponent message = new ChatComponentText("A new version of Sol Client is available: " + latest
						+ ".\nYou are currently on version " + GlobalConstants.VERSION_STRING + '.');
				message.setChatStyle(message.getChatStyle().setColor(EnumChatFormatting.GREEN));
				mc.ingameGUI.getChatGUI().printChatMessage(message);
			}
		}
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (SolClientMod.instance.modsKey.isPressed()) {
			mc.displayGuiScreen(new ModsScreen());
		} else if (SolClientMod.instance.editHudKey.isPressed()) {
			mc.displayGuiScreen(new ModsScreen());
			mc.displayGuiScreen(new MoveHudsScreen());
		}
	}

	@EventHandler
	public void onQuit(GameQuitEvent event) {
		NanoVGManager.closeContext();
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

		if (data == null) {
			detectedServer = null;
			mods.forEach(Mod::unblock);
		}

		if (data != null) {
			for (DetectedServer server : DetectedServer.values()) {
				if (server.matches(data)) {
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
		if (!(mc.currentScreen instanceof ModsScreen)) {
			save();
		}
	}

}
