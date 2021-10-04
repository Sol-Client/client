package me.mcblueparrot.client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import me.mcblueparrot.client.events.SendChatMessageEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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

import me.mcblueparrot.client.events.EventBus;
import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.TickEvent;
import me.mcblueparrot.client.hud.*;
import me.mcblueparrot.client.mod.*;
import me.mcblueparrot.client.ui.ChatButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.IResource;
import net.minecraft.command.CommandBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

/**
 * Main class for Sol Client.
 */
public class Client {

    private Minecraft mc = Minecraft.getMinecraft();
    public static final Client INSTANCE = new Client();
    private JsonObject data;
    private List<Mod> mods = new ArrayList<Mod>();
    private List<Hud> huds = new ArrayList<Hud>();
    private final Logger LOGGER = LogManager.getLogger();
    private final File DATA_FILE = new File(Minecraft.getMinecraft().mcDataDir, "parrot_client_mods.json");
    public DetectedServer detectedServer;
    public EventBus bus = new EventBus();
    private Map<ResourceLocation, IResource> resources = new HashMap<>();
    private Map<String, CommandBase> commands = new HashMap<>();
    private List<ChatButton> chatButtons = new ArrayList<>();
    private ChatChannelSystem chatChannelSystem;
    public KeyBinding keyMods = new KeyBinding("Mods", Keyboard.KEY_RSHIFT, "Sol Client");
    public static final String VERSION = System.getProperty("me.mcblueparrot.client.version", "DEVELOPMENT TEST");
    public static final String NAME = "Sol Client " + VERSION;

    public void init() {
        LOGGER.info("Initialising...");
        bus.register(this);
        PpsMonitor.forceInit();
        LOGGER.info("Loading settings...");
        load();
        LOGGER.info("Loading mods...");
        register(new FpsHud());
        register(new PositionHud());
        register(new KeystrokeHud());
        register(new CpsHud());
        register(new PingHud());
        register(new SpeedHud());
        register(new ReachDisplayHud());
        register(new ComboCounterHud());
        register(new StatusEffectsHud());
        register(new ArmourHud());
        register(new TimerHud());
        register(new ChatHud());
        register(new CrosshairHud());
        register(new ScoreboardMod());
        register(new NightVisionMod());
        register(new MotionBlurMod());
        register(new MenuBlurMod());
        register(new ChunkAnimationMod());
        register(new PerspectiveMod());
        register(new ToggleSprintMod());
        register(new Old1_7AnimationsMod());
        register(new ItemPhysicsMod());
        register(new ZoomMod());
        register(new ParticlesMod());
        register(new TimeChangerMod());
        register(new HypixelAdditionsMod());
        register(new ArabicNumeralsMod());
        register(new NumeralPingMod());
        register(new ShowOwnTagMod());
        register(new BetterItemTooltipsMod());
        register(new BlockSelectionMod());
        register(new HitColourMod());
        registerKeybind(keyMods);
        try {
            unregisterKeybind((KeyBinding) GameSettings.class.getField("ofKeyBindZoom").get(mc.gameSettings));
        }
        catch(NoSuchFieldException | IllegalAccessException | ClassCastException error) {
            // OptiFine is not enabled.
        }
        organiseHuds();
        LOGGER.info("Loaded " + mods.size() + " mods");
        LOGGER.info("Saving settings...");
        save();
        LOGGER.info("Starting culling thread...");
        Thread cullThread = new Thread(new CullTask(new OcclusionCullingInstance(128, new DataProvider() {

            private WorldClient world;

            @Override
            public boolean prepareChunk(int x, int z) {
                return (world = mc.theWorld) != null;
            }

            @Override
            public boolean isOpaqueFullCube(int x, int y, int z) {
                return world.isBlockNormalCube((new BlockPos(x, y, z)), false);
            }

        })), "Culling Thread");
        cullThread.setUncaughtExceptionHandler((thread, error) -> {
            LOGGER.error("Culling Thread has crashed:", error);
        });
        cullThread.start();
    }

    public void registerKeybind(KeyBinding keybind) {
        mc.gameSettings.keyBindings = ArrayUtils.add(mc.gameSettings.keyBindings, keybind);
    }

    public void unregisterKeybind(KeyBinding keybind) {
        mc.gameSettings.keyBindings = ArrayUtils.removeElement(mc.gameSettings.keyBindings, keybind);
        keybind.setKeyCode(0);
    }

    private Gson getGson(Mod mod) {
        GsonBuilder builder = new GsonBuilder();
        if(mod != null) {
            builder.registerTypeAdapter(mod.getClass(), new InstanceCreator<Mod>() {

                @Override
                public Mod createInstance(Type type) {
                    return mod;
                }

            });
        }
        return builder.excludeFieldsWithoutExposeAnnotation().create();
    }

    public List<Mod> getMods() {
        return mods;
    }

    public List<Hud> getHuds() {
        return huds;
    }

    private void organiseHuds() {
        huds.clear();
        for(Mod mod : mods) {
            if(mod instanceof Hud) {
                huds.add((Hud) mod);
            }
        }
    }

    public boolean load() {
        try {
            if(DATA_FILE.exists()) {
                data = new JsonParser().parse(FileUtils.readFileToString(DATA_FILE)).getAsJsonObject();
            }
            else {
                data = new JsonObject();
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
        if(data.has(mod.getId())) {
            mods.add(getGson(mod).fromJson(data.get(mod.getId()), mod.getClass()));
        }
        else {
            mods.add(mod);
        }

        mod.onRegister();
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
    public void onSendMessage(SendChatMessageEvent event) {
        if(event.message.startsWith("/")) {
            String commandKey = event.message.substring(1, !event.message.contains(" ") ?
                    event.message.length() : event.message.indexOf(" "));
            if(commands.containsKey(commandKey)) {
                try {
                    String[] args = event.message.split(" ");
                    commands.get(commandKey).processCommand(mc.thePlayer, Arrays.copyOfRange(args,
                            1, args.length - 1));
                    event.cancelled = true;
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
    public void onTick(TickEvent event) {
        if(keyMods.isPressed()) {
            mc.displayGuiScreen(new ModsScreen(null));
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
	        return;
	    }

	    for(DetectedServer server : DetectedServer.values()) {
	        if(server.matches(data)) {
	            detectedServer = server;
                mods.stream().filter(server::shouldBlockMod).forEach(Mod::block);
	            break;
	        }
	    }

	    bus.post(new ServerChangeEvent(detectedServer));
    }

	public void onServerDisconnect() {
	    mods.forEach(Mod::unblock);
	}

    public List<ChatButton> getChatButtons() {
        return chatButtons;
    }

}
