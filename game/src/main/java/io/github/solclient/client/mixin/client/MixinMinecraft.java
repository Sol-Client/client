package io.github.solclient.client.mixin.client;

import java.util.ConcurrentModificationException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.*;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.core.versions.MCVer;
import com.replaymod.replay.InputReplayTimer;

import io.github.solclient.client.*;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.ui.component.Screen;
import io.github.solclient.client.ui.screen.*;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.access.*;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.settings.*;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.util.*;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements AccessMinecraft, MCVer.MinecraftMethodAccessor {

	private boolean debugPressed;
	private boolean cancelDebug;

	// Don't see the benefit of logging the session ID, apart from for hackers.
	// I have decompiled dodgy-looking Minecraft plugins, and seen code that takes
	// advantage of this.
	// This is mainly for the crash reporting feature, as I don't want users
	// accounts to be hacked.
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Session;getSessionID()"
			+ "Ljava/lang/String;"))
	public String censorSessionId(Session instance) {
		return "☃︎";
	}

	// Mojang has made some questionable decisions with their code.
	// https://github.com/Sk1erLLC/Patcher/blob/master/src/main/java/club/sk1er/patcher/mixins/performance/MinecraftMixin_OptimizedWorldSwapping.java
	@Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
	public void noneOfYourGarbage() {
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;initStream()V", shift = At.Shift.AFTER))
	public void init(CallbackInfo callback) {
		Client.INSTANCE.init();
		gameSettings.loadOptions();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;<init>()V"))
	public void postStart(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PostGameStartEvent());
	}

	@Inject(method = "runTick", at = @At("HEAD"))
	public void preRunTick(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PreTickEvent());
	}

	@Inject(method = "runTick", at = @At("RETURN"))
	public void postRunTick(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PostTickEvent());
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z"))
	public boolean next() {
		boolean next = Mouse.next();

		if (next && Mouse.getEventButtonState()) {
			if (Client.INSTANCE.bus.post(new MouseClickEvent(Mouse.getEventButton())).cancelled) {
				next = next(); // Skip
			}
		}

		return next;
	}

	@Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer"
			+ "/EntityRenderer;updateCameraAndRender(FJ)V", shift = At.Shift.BEFORE))
	public void preRenderTick(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PreRenderTickEvent());
	}

	@Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer"
			+ "/EntityRenderer;updateCameraAndRender(FJ)V", shift = At.Shift.AFTER))
	public void postRenderTick(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new PostRenderTickEvent());
	}

	/**
	 * @author TheKodeToad
	 */
	@Overwrite
	public int getLimitFramerate() {
		if ((theWorld == null && !(currentScreen instanceof Screen)) || !Display.isActive()) {
			return 30; // Only limit framerate to 30. This means there is no noticeable lag spike.
		}

		return gameSettings.limitFramerate;
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;"
			+ "isPressed()Z", ordinal = 2))
	public boolean cancelHotbarSwitch(KeyBinding instance) {
		return instance.isPressed() && !Utils.isSpectatingEntityInReplay();
	}

	/**
	 * Particles mod causes crashes. This seems better than trying to fix the
	 * synchronisation issues - that would cause performance issues.
	 */
	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;updateEffects()V"))
	public void concurrencyHack(EffectRenderer effectRenderer) {
		try {
			effectRenderer.updateEffects();
		} catch (NullPointerException | ConcurrentModificationException | ArrayIndexOutOfBoundsException ignored) {
		}
	}

	@Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
	public void loadWorld(WorldClient world, String message, CallbackInfo callback) {
		Client.INSTANCE.bus.post(new WorldLoadEvent(world));
	}

	@Inject(method = "displayGuiScreen", at = @At(value = "HEAD"), cancellable = true)
	public void openGui(GuiScreen screen, CallbackInfo callback) {
		if (((screen == null && theWorld == null) || screen instanceof GuiMainMenu)
				&& SolClientMod.instance.fancyMainMenu) {
			callback.cancel();

			if (screen == null) {
				new GuiMainMenu();
			} else {
				Client.INSTANCE.setMainMenu((GuiMainMenu) screen);
			}

			displayGuiScreen(new SolClientMainMenu());
			return;
		} else if (screen instanceof SolClientMainMenu && !SolClientMod.instance.fancyMainMenu) {
			callback.cancel();
			displayGuiScreen(null);
			return;
		}

		Client.INSTANCE.bus.post(new OpenGuiEvent(screen));
		if (currentScreen == null && screen != null) {
			Client.INSTANCE.bus.post(new InitialOpenGuiEvent(screen));
		}
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
	public int onScroll() {
		int dWheel = Mouse.getEventDWheel();

		int divided = 0;

		if (dWheel != 0) {
			divided = dWheel / Math.abs(dWheel);

			if (Client.INSTANCE.bus.post(new ScrollEvent(dWheel)).cancelled) {
				dWheel = 0;
			} else if (Utils.isSpectatingEntityInReplay()) {
				dWheel = 0;
			} else {
				InputReplayTimer.handleScroll(divided);
			}
		}

		if (dWheel != 0 && !playerController.isSpectatorMode() && TweaksMod.enabled
				&& TweaksMod.instance.disableHotbarScrolling) {
			dWheel = 0;
		}

		return dWheel;
	}

	@Redirect(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
	public void overrideTitle(String oldTitle) {
		Display.setTitle(GlobalConstants.NAME + " on " + oldTitle);
	}

	@Inject(method = "setServerData", at = @At("TAIL"))
	public void onDisconnect(ServerData serverDataIn, CallbackInfo callback) {
		if (serverDataIn == null) {
			Client.INSTANCE.onServerChange(null);
		}
	}

	@Override
	@Accessor
	public abstract boolean isRunning();

	@Override
	@Accessor("mcDefaultResourcePack")
	public abstract DefaultResourcePack getDefaultResourcePack();

	@Override
	@Accessor("metadataSerializer_")
	public abstract IMetadataSerializer getMetadataSerialiser();

	@Override
	@Accessor(value = "timer")
	public abstract Timer getTimerSC();

	@Override
	@Invoker("resize")
	public abstract void resizeWindow(int width, int height);

	@Shadow
	public GuiScreen currentScreen;

	@Shadow
	protected abstract void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException;

	@Shadow
	private TextureManager renderEngine;

	private boolean loading = true;

	// region Splash Screen Rendering

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/SkinManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V"))
	public void splashSkinManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/storage/AnvilSaveConverter;<init>(Ljava/io/File;)V"))
	public void splashSaveLoader(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;<init>(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/settings/GameSettings;)V"))
	public void splashSoundHandler(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/MusicTicker;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashMusicTicker(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;<init>(Lnet/minecraft/client/settings/GameSettings;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/TextureManager;Z)V"))
	public void splashFontRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;<init>()V"))
	public void splashMouseHelper(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureMap;<init>(Ljava/lang/String;)V"))
	public void splashTextureMap(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelManager;<init>(Lnet/minecraft/client/renderer/texture/TextureMap;)V"))
	public void splashModelManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderItem;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/ModelManager;)V"))
	public void splashRenderItem(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/entity/RenderItem;)V"))
	public void splashRenderManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashItemRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V"))
	public void splashEntityRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;<init>(Lnet/minecraft/client/renderer/BlockModelShapes;Lnet/minecraft/client/settings/GameSettings;)V"))
	public void splashBlockRenderDispatcher(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashRenderGlobal(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashGuiAchivement(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
	public void splashEffectRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;<init>(Lnet/minecraft/client/Minecraft;)V"))
	public void splashGuiIngame(CallbackInfo callback) {
		draw();
	}

	@SneakyThrows
	private void draw() {
		drawSplashScreen(renderEngine);
	}

	@Inject(method = "drawSplashScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;"
			+ "updateDisplay()V", shift = At.Shift.BEFORE))
	public void drawProgress(TextureManager textureManagerInstance, CallbackInfo callback) {
		SplashScreen.INSTANCE.draw();
	}

	@Redirect(method = "updateFramebufferSize", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;"
			+ "entityRenderer:Lnet/minecraft/client/renderer/EntityRenderer;"))
	public EntityRenderer hideEntityRenderer(Minecraft instance) {
		if (loading) {
			return null;
		}

		return instance.entityRenderer;
	}

	// endregion

	// region Better F3
	// Backport 1.9 F3 fixes

	@Redirect(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;"
			+ "showDebugInfo:Z", ordinal = 1))
	public void preventDefaultF3(GameSettings settings, boolean f3) {
	}

	@Inject(method = "dispatchKeypresses", at = @At(value = "HEAD"))
	public void updateF3(CallbackInfo callback) {
		boolean debugPressed = Keyboard.isKeyDown(61);
		if (this.debugPressed && !debugPressed) {
			if (cancelDebug) {
				cancelDebug = false;
			} else {
				gameSettings.showDebugInfo = !gameSettings.showDebugInfo;
			}
		}
		this.debugPressed = debugPressed;
	}

	private void debugChatInfo(String message) {
		ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(
				"[" + EnumChatFormatting.GREEN + "Debug" + EnumChatFormatting.RESET + "] " + message));
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;"
			+ "clearChatMessages()V"))
	public void betterClearMessages(GuiNewChat guiNewChat) {
		((AccessGuiNewChat) guiNewChat).clearChat();
		cancelDebug = true;
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;refreshResources()V"))
	public void betterRefreshResources(CallbackInfo callback) {
		debugChatInfo("Reloaded Resources");
		cancelDebug = true;
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionValue(Lnet/minecraft/client/settings/GameSettings$Options;I)V"))
	public void betterRenderDistance(CallbackInfo callback) {
		// Broken in vanilla.
		gameSettings.renderDistanceChunks = (int) Options.RENDER_DISTANCE
				.snapToStepClamp(gameSettings.renderDistanceChunks + (GuiScreen.isShiftKeyDown() ? -1 : 1));

		debugChatInfo("Render Distance: " + gameSettings.renderDistanceChunks);
		gameSettings.saveOptions();
		cancelDebug = true;
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;loadRenderers()V"))
	public void betterLoadRenderers(CallbackInfo callback) {
		debugChatInfo("Reloaded Chunks");
		cancelDebug = true;
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;saveOptions()V"))
	public void advancedTootipsAndPauseOnLostFocus(CallbackInfo callback) {
		cancelDebug = true;
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;saveOptions()V", ordinal = 0))
	public void advancedTootips(CallbackInfo callback) {
		debugChatInfo("Advanced Item Tooltips: " + gameSettings.advancedItemTooltips);
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;saveOptions()V", ordinal = 1))
	public void pauseOnLostFocus(CallbackInfo callback) {
		debugChatInfo("Pause on Lost Focus: " + gameSettings.pauseOnLostFocus);
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;setDebugBoundingBox(Z)V"))
	public void betterHitboxes(RenderManager instance, boolean value) {
		if (!Client.INSTANCE.bus.post(new HitboxToggleEvent(value)).cancelled) {
			instance.setDebugBoundingBox(value);
			debugChatInfo("Entity Hitboxes: " + value);
			cancelDebug = true;
		}
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyBindState(IZ)V", ordinal = 1))
	public void nextKey(int keyCode, boolean pressed) {
		if (pressed && debugPressed) {
			if (Keyboard.getEventKey() == 32 || Keyboard.getEventKey() == 31 || Keyboard.getEventKey() == 20
					|| Keyboard.getEventKey() == 33 || Keyboard.getEventKey() == 30 || Keyboard.getEventKey() == 35
					|| Keyboard.getEventKey() == 48 || Keyboard.getEventKey() == 25) {
				return;
			}
		}

		KeyBinding.setKeyBindState(keyCode, pressed);
	}

	// endregion

	private boolean hadWorld;

	@Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
	public void onWorldLoad(WorldClient world, String loadingText, CallbackInfo callback) {
		if (world == null && hadWorld) {
			hadWorld = false;
		} else if (world != null && !hadWorld) {
			hadWorld = true;
			Client.INSTANCE.onServerChange(currentServerData);
		}
	}

	@Redirect(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;serverName:Ljava/lang/String;"))
	public String joinServer(Minecraft instance) {
		return null;
	}

	@Redirect(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;"
			+ "displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V", ordinal = 1))
	public void display(Minecraft instance, GuiScreen screen) {
		loading = false;

		if (serverName != null && serverName.startsWith("§sc§")) {
			ServerList list = new ServerList((Minecraft) (Object) this);
			instance.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(screen), (Minecraft) (Object) this,
					list.getServerData(Integer.parseInt(serverName.substring(4)))));
			return;
		}
		instance.displayGuiScreen(screen);
	}

	@Inject(method = "toggleFullscreen", at = @At("HEAD"), cancellable = true)
	public void handleToggle(CallbackInfo callback) {
		FullscreenToggleEvent event = Client.INSTANCE.bus.post(new FullscreenToggleEvent(!fullscreen));

		if (event.cancelled) {
			callback.cancel();
			gameSettings.fullScreen = fullscreen;
		} else if (!event.applyState) {
			callback.cancel();
			gameSettings.fullScreen = (fullscreen = !fullscreen);
		}
	}

	// Fix Replay Mod bug - textures animate too fast.

	private boolean earlyBird;

	@Override
	public void replayModSetEarlyReturnFromRunTick(boolean earlyBird) {
		this.earlyBird = earlyBird;
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/"
			+ "TextureManager;tick()V"))
	public void cancelTextureTick(TextureManager instance) {
		if (!earlyBird) {
			instance.tick();
		}
	}

	@Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;"
			+ "updateTick()V"))
	public void cancelHudTick(GuiIngame instance) {
		if (!earlyBird) {
			instance.updateTick();
		}
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I"))
	public void forceScroll(CallbackInfo callback) {
		if (earlyBird) {
			onScroll();
		}
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;"
			+ "sendClickBlockToController(Z)V"), cancellable = true)
	public void doEarlyReturnFromRunTick(CallbackInfo callback) {
		if (earlyBird) {
			callback.cancel();
		}
	}

	@Inject(method = "shutdownMinecraftApplet" /* applet? looks like MCP is a bit outdated */, at = @At("HEAD"))
	public void preShutdown(CallbackInfo callback) {
		Client.INSTANCE.bus.post(new GameQuitEvent());
	}

	@Shadow
	public GameSettings gameSettings;

	@Shadow
	private ServerData currentServerData;

	@Shadow
	public WorldClient theWorld;

	@Shadow
	public EntityRenderer entityRenderer;

	@Shadow
	public abstract void displayGuiScreen(GuiScreen guiScreenIn);

	@Shadow
	private String serverName;

	@Shadow
	public GuiIngame ingameGUI;

	@Shadow
	private RenderManager renderManager;

	@Shadow
	public PlayerControllerMP playerController;

	@Shadow
	private boolean fullscreen;

}
