package io.github.solclient.client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.*;

import com.google.gson.*;
import com.logisticscraft.occlusionculling.*;

import io.github.solclient.client.chat.*;
import io.github.solclient.client.command.*;
import io.github.solclient.client.config.ConfigVersion;
import io.github.solclient.client.culling.CullTask;
import io.github.solclient.client.event.*;
import io.github.solclient.client.event.impl.game.*;
import io.github.solclient.client.event.impl.network.ServerConnectEvent;
import io.github.solclient.client.event.impl.network.chat.OutgoingChatMessageEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.packet.*;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.network.ServerData;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.screen.TitleScreen;
import io.github.solclient.client.platform.mc.text.*;
import io.github.solclient.client.platform.mc.world.level.Level;
import io.github.solclient.client.platform.mc.world.level.block.BlockPos;
import io.github.solclient.client.ui.screen.mods.*;
import lombok.*;

/**
 * Main class for Sol Client.
 */
public final class Client {

	public static final Client INSTANCE = new Client();

	private final MinecraftClient mc = MinecraftClient.getInstance();
	private JsonObject data;
	@Getter
	private final List<Mod> mods = new ArrayList<>();
	private final Map<String, Mod> modsById = new HashMap<>();
	@Getter
	private List<HudElement> huds = new ArrayList<>();
	public static final Logger LOGGER = LogManager.getLogger();

	private final File dataFile = new File(mc.getDataFolder(), "sol_client_mods.json");
	// data file for beta versions - this is no longer very necessary.
	private final File legacyDataFile = new File(mc.getDataFolder(), "parrot_client_mods.json");

	public DetectedServer detectedServer;

	private final Map<Identifier, Supplier<String>> resources = new HashMap<>();
	private final Map<String, Command> commands = new HashMap<>();
	private final List<ChatButton> chatButtons = new ArrayList<>();

	private ChatChannelSystem chatChannelSystem;

	@Getter
	private PopupManager popupManager;
	@Getter
	private CapeManager capeManager;
	@Getter
	@Setter
	private TitleScreen mainMenu;

	public void init() {
		new File(mc.getDataFolder(), "server-resource-packs").mkdirs(); // Fix crash
		System.setProperty("http.agent", "Sol Client/" + Constants.VERSION);

		LOGGER.info("Initialising...");
		EventBus.DEFAULT.register(this);

		CpsCounter.register();

		LOGGER.info("Loading settings...");

		if(!dataFile.exists() && legacyDataFile.exists()) {
			legacyDataFile.renameTo(dataFile);
		}

		load();

		LOGGER.info("Loading mods...");
		DefaultMods.register();
		LOGGER.info("Loaded {} mods", new Object[] {mods.size()});

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
				while(mc.isGameRunning()) {
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

		EventBus.DEFAULT.register(new PacketApi());
		EventBus.DEFAULT.register(popupManager = new PopupManager());

		capeManager = new CapeManager();
	}

	private static Gson getGson(Mod mod) {
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

	public boolean load() {
		try {
			if(dataFile.exists()) {
				data = JsonParser.parseString(FileUtils.readFileToString(dataFile, StandardCharsets.UTF_8)).getAsJsonObject();
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

	public void register(Mod... mods) {
		for(Mod mod : mods) {
			try {
				if(data.has(mod.getId())) {
					getGson(mod).fromJson(data.get(mod.getId()), mod.getClass());
				}
				this.mods.add(mod);

				modsById.put(mod.getId(), mod);

				mod.onRegister();
			}
			catch(Throwable error) {
				LOGGER.error("Could not register mod " + mod.getId(), error);
				this.mods.remove(mod);
			}
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
			registerChatButton(ChatChannelButton.INSTANCE);
		}
		else {
			unregisterChatButton(ChatChannelButton.INSTANCE);
		}
	}

	public ChatChannelSystem getChatChannelSystem() {
		return chatChannelSystem;
	}

	@EventHandler
	public void onPostStart(PostStartEvent event) {
		for(Mod mod : mods) {
			try {
				mod.postStart();
			}
			catch(Throwable error) {
				LOGGER.error("Could not fire postStart()", error);
			}
		}

		cacheHudList();

		try {
			mc.getOptions()
					.removeKey((KeyBinding) mc.getOptions().getClass().getField("ofKeyBindZoom").get(mc.getOptions()));
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
					mc.getPlayer().sendSystemMessage(Text.literal(error.getMessage())
							.style((style) -> style.withColour(TextColour.RED)));
				}
				catch(Exception error) {
					mc.getPlayer()
							.sendSystemMessage(Text
									.literal("Could " + "not execute client-sided command. See log for extra details.")
									.style((style) -> style.withColour(TextColour.RED)));
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
		if(SolClientConfig.INSTANCE.modsKey.consumePress()) {
			mc.setScreen(new ModsScreen());
		}
		else if(SolClientConfig.INSTANCE.editHudKey.consumePress()) {
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

		EventBus.DEFAULT.post(new ServerConnectEvent(data, detectedServer));
	}

	public List<ChatButton> getChatButtons() {
		return chatButtons;
	}

	/**
	 * Saves if the mod screen is not opened.
	 */
	public void optionChanged() {
		if(!(mc.getScreen() instanceof ModsScreen)) {
			save();
		}
	}

}
