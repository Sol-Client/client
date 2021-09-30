package me.mcblueparrot.client;

import me.mcblueparrot.client.util.Colour;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
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
 * Main class for Parrot Client.
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
//        return builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
//
//            @Override
//            public boolean shouldSkipField(FieldAttributes field) {
//                if("value".equals(field.getName())
//                        && field.getDeclaringClass() == Colour.class) {
//                    return false;
//                }
//
//                Expose expose = field.getAnnotation(Expose.class);
//                return expose == null || !expose.serialize();
//            }
//
//            @Override
//            public boolean shouldSkipClass(Class<?> arg0) {
//                return false;
//            }
//
//        }).addDeserializationExclusionStrategy(new ExclusionStrategy() {
//
//            @Override
//            public boolean shouldSkipField(FieldAttributes field) {
//                if("value".equals(field.getName())
//                        && field.getDeclaringClass() == Colour.class) {
//                    return false;
//                }
//
//                Expose expose = field.getAnnotation(Expose.class);
//                return expose == null || !expose.deserialize();
//            }
//
//            @Override
//            public boolean shouldSkipClass(Class<?> clazz) {
//                return false;
//            }
//
//        }).create();
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
            mod.onRegister();
        }
        else {
            mods.add(mod);
            mod.onRegister();
        }
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
    }

    public ChatChannelSystem getChatChannelSystem() {
        return chatChannelSystem;
    }

//    @Subscribe
//	private void onRender(RenderEvent event) {
//	    PpsMonitor.tickMouseButtons();
//        if(mc.currentScreen instanceof MoveHudsScreen) return;
//        GlStateManager.disableLighting();
//	    for(Hud hud : huds) {
//            if(hud.isEnabled()) {
//                hud.render(hud.getPosition(), false);
//            }
//        }
//
//	    if(HypixelAdditionsMod.isEffective()) {
//	        HypixelAdditionsMod.instance.onRender();
//	    }
//	}

    @EventHandler
    public void onTick(TickEvent event) {
        if(keyMods.isPressed()) {
            mc.displayGuiScreen(new ModsScreen(null));
        }
	}

    public void registerChatButton(ChatButton button) {
        chatButtons.add(button);
    }

    public void unregisterChatButton(ChatButton button) {
        chatButtons.remove(button);
    }

	public void onServerChange(ServerData data) {
	    chatChannelSystem = null;

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

	public enum DetectedServer {
        HYPIXEL("([A-z]+\\.)?hypixel\\.net(:[0-9]+)?",
                "https://hypixel.net/threads/guide-allowed-modifications.345453/", "perspective"),
        GOMMEHD("gommehd\\.net(:[0-9]+)?", "https://www.gommehd.net/forum/threads/rules-minecraft.941059/",
                "perspective", "nightVision"),
        MINEPLEX("([A-z]+\\.)?mineplex\\.com(:[0-9]+)?", "https://www.mineplex.com/rules/", "perspective");

	    private Pattern pattern;
        private URI blockedModPage;
	    private List<String> blockedMods;

	    private DetectedServer(String regex, String blockModPage, String... blockedMods) {
	        pattern = Pattern.compile(regex);
	        try {
                this.blockedModPage = new URI(blockModPage);
            }
	        catch(URISyntaxException error) {
	            throw new IllegalStateException(error);
            }
	        this.blockedMods = Arrays.asList(blockedMods);
	    }

	    public URI getBlockedModPage() {
            return blockedModPage;
        }

	    public boolean shouldBlockMod(Mod mod) {
	        return blockedMods.contains(mod.getId());
	    }

	    public boolean matches(ServerData data) {
	        return pattern.matcher(data.serverIP).matches();
	    }

	}

    public List<ChatButton> getChatButtons() {
        return chatButtons;
    }

}
