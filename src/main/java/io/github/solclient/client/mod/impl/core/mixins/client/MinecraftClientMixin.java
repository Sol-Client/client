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

package io.github.solclient.client.mod.impl.core.mixins.client;

import java.util.ConcurrentModificationException;

import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.*;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.core.versions.MCVer;
import com.replaymod.replay.InputReplayTimer;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.extension.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.api.chat.ChatApiMod;
import io.github.solclient.client.mod.impl.core.CoreMod;
import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import io.github.solclient.client.ui.component.ComponentScreen;
import io.github.solclient.client.ui.screen.*;
import io.github.solclient.client.util.*;
import io.github.solclient.util.GlobalConstants;
import lombok.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.network.*;
import net.minecraft.client.option.*;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.*;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientExtension, MCVer.MinecraftMethodAccessor {

	private boolean debugPressed;
	private boolean cancelDebug;

	// security tm
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", ordinal = 1))
	public void removeSessionId(Logger logger, String sessionIdDebug) {
	}

	// Mojang has made some questionable decisions with their code.
	// https://github.com/Sk1erLLC/Patcher/blob/master/src/main/java/club/sk1er/patcher/mixins/performance/MinecraftMixin_OptimizedWorldSwapping.java
	@Redirect(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
	public void noneOfYourGarbage() {
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;initializeStream()V", shift = At.Shift.AFTER))
	public void init(CallbackInfo callback) {
		SolClient.INSTANCE.init();
		options.load();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;<init>()V"))
	public void postStart(CallbackInfo callback) {
		EventBus.INSTANCE.post(new PostGameStartEvent());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void preRunTick(CallbackInfo callback) {
		EventBus.INSTANCE.post(new PreTickEvent());
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void postRunTick(CallbackInfo callback) {
		EventBus.INSTANCE.post(new PostTickEvent());
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z"))
	public boolean next() {
		boolean next = Mouse.next();

		if (next && Mouse.getEventButtonState())
			if (EventBus.INSTANCE.post(new MouseClickEvent(Mouse.getEventButton())).cancelled)
				next = next(); // Skip

		return next;
	}

	@Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJ)V", shift = At.Shift.BEFORE))
	public void preRenderTick(CallbackInfo callback) {
		EventBus.INSTANCE.post(new PreRenderTickEvent());
	}

	@Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJ)V", shift = At.Shift.AFTER))
	public void postRenderTick(CallbackInfo callback) {
		EventBus.INSTANCE.post(new PostRenderTickEvent());
	}

	/**
	 * @author TheKodeToad
	 */
	@Overwrite
	public int getMaxFramerate() {
		if ((world == null && !(currentScreen instanceof ComponentScreen)) || !Display.isActive()) {
			return 30; // Only limit framerate to 30. This means there is no noticeable lag spike.
		}

		return options.maxFramerate;
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z", ordinal = 2))
	public boolean cancelHotbarSwitch(KeyBinding instance) {
		return instance.isPressed() && !MinecraftUtils.isSpectatingEntityInReplay();
	}

	/**
	 * Particles mod causes crashes. GitHub gist authors: pls take note of this.
	 * This is bad! I'll fix it later maybe.
	 */
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;tick()V"))
	public void concurrencyHack(ParticleManager effectRenderer) {
		try {
			effectRenderer.tick();
		} catch (NullPointerException | ConcurrentModificationException | ArrayIndexOutOfBoundsException ignored) {
		}
	}

	@Inject(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At("HEAD"))
	public void loadWorld(ClientWorld world, String loadingMessage, CallbackInfo callback) {
		EventBus.INSTANCE.post(new WorldLoadEvent(world));
	}

	@Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
	public void setScreen(Screen screen, CallbackInfo callback) {
		if (((screen == null && world == null) || screen instanceof TitleScreen)
				&& CoreMod.instance.fancyMainMenu) {
			callback.cancel();

			if (screen == null)
				new TitleScreen();
			else
				ActiveMainMenu.setInstance((TitleScreen) screen);

			setScreen(new SolClientMainMenu());
			return;
		} else if (screen instanceof SolClientMainMenu && !CoreMod.instance.fancyMainMenu) {
			callback.cancel();
			setScreen(null);
			return;
		}

		EventBus.INSTANCE.post(new OpenGuiEvent(screen));
		if (currentScreen == null && screen != null) {
			EventBus.INSTANCE.post(new InitialOpenGuiEvent(screen));
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
	public int onScroll() {
		int dWheel = Mouse.getEventDWheel();

		int divided = 0;

		if (dWheel != 0) {
			divided = dWheel / Math.abs(dWheel);

			if (EventBus.INSTANCE.post(new ScrollEvent(dWheel)).cancelled) {
				dWheel = 0;
			} else if (MinecraftUtils.isSpectatingEntityInReplay()) {
				dWheel = 0;
			} else {
				InputReplayTimer.handleScroll(divided);
			}
		}

		if (dWheel != 0 && !interactionManager.isSpectator() && TweaksMod.enabled
				&& TweaksMod.instance.disableHotbarScrolling) {
			dWheel = 0;
		}

		return dWheel;
	}

	@Redirect(method = "setPixelFormat", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
	public void overrideTitle(String oldTitle) {
		Display.setTitle(GlobalConstants.NAME + " on " + oldTitle);
	}

	@Getter
	private ServerInfo previousServer;
	private boolean hadWorld;

	@Inject(method = "setCurrentServerEntry", at = @At("TAIL"))
	public void onDisconnect(ServerInfo info, CallbackInfo callback) {
		if (info == null)
			onServerChange(null);
		else
			previousServer = info;
	}

	@Inject(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At("RETURN"))
	public void onWorldLoad(ClientWorld world, String loadingText, CallbackInfo callback) {
		if (world == null && hadWorld)
			hadWorld = false;
		else if (world != null && !hadWorld) {
			hadWorld = true;
			onServerChange(currentServerEntry);
		}
	}

	private static void onServerChange(ServerInfo info) {
		ChatApiMod.instance.setChannelSystem(null);

		if (info == null) {
			DetectedServer.setCurrent(null);
			SolClient.INSTANCE.forEach(Mod::unblock);
		}

		if (info != null) {
			for (DetectedServer server : DetectedServer.values()) {
				if (server.matches(info)) {
					DetectedServer.setCurrent(server);
					SolClient.INSTANCE.modStream().filter(server::shouldBlockMod).forEach(Mod::block);
					break;
				}
			}
		}

		EventBus.INSTANCE.post(new ServerConnectEvent(info, DetectedServer.current()));
	}

	@Shadow
	public Screen currentScreen;

	@Shadow
	protected abstract void loadLogo(TextureManager textures) throws LWJGLException;

	@Shadow
	private TextureManager textureManager;

	private boolean loading = true;

	// region Splash Screen Rendering

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PlayerSkinProvider;<init>(Lnet/minecraft/client/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V"))
	public void splashSkinManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/AnvilLevelStorage;<init>(Ljava/io/File;)V"))
	public void splashSaveLoader(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;<init>(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/client/option/GameOptions;)V"))
	public void splashSoundHandler(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/MusicTracker;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
	public void splashMusicTicker(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;<init>(Lnet/minecraft/client/option/GameOptions;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/texture/TextureManager;Z)V"))
	public void splashFontRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseInput;<init>()V"))
	public void splashMouseHelper(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;<init>(Ljava/lang/String;)V"))
	public void splashTextureMap(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModelManager;<init>(Lnet/minecraft/client/texture/SpriteAtlasTexture;)V"))
	public void splashModelManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;<init>(Lnet/minecraft/client/texture/TextureManager;Lnet/minecraft/client/render/model/BakedModelManager;)V"))
	public void splashRenderItem(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;<init>(Lnet/minecraft/client/texture/TextureManager;Lnet/minecraft/client/render/item/ItemRenderer;)V"))
	public void splashRenderManager(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
	public void splashItemRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/resource/ResourceManager;)V"))
	public void splashEntityRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;<init>(Lnet/minecraft/client/render/block/BlockModelShapes;Lnet/minecraft/client/option/GameOptions;)V"))
	public void splashBlockRenderDispatcher(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
	public void splashRenderGlobal(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/AchievementNotification;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
	public void splashGuiAchievement(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/texture/TextureManager;)V"))
	public void splashEffectRenderer(CallbackInfo callback) {
		draw();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;<init>(Lnet/minecraft/client/MinecraftClient;)V"))
	public void splashGuiIngame(CallbackInfo callback) {
		draw();
	}

	@SneakyThrows
	private void draw() {
		loadLogo(textureManager);
	}

	@Inject(method = "loadLogo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;"
			+ "updateDisplay()V", shift = At.Shift.BEFORE))
	public void drawProgress(TextureManager textureManagerInstance, CallbackInfo callback) {
		SplashScreen.INSTANCE.draw();
	}

	@Redirect(method = "resizeFramebuffer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;gameRenderer:Lnet/minecraft/client/render/GameRenderer;"))
	public GameRenderer hideGameRenderer(MinecraftClient instance) {
		if (loading)
			return null;

		return instance.gameRenderer;
	}

	// endregion

	// region Better F3
	// Backport 1.9 F3 fixes

	@Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;"
			+ "debugEnabled:Z", ordinal = 1))
	public void preventDefaultF3(GameOptions options, boolean f3) {
	}

	@Inject(method = "handleKeyInput", at = @At(value = "HEAD"))
	public void updateF3(CallbackInfo callback) {
		boolean debugPressed = Keyboard.isKeyDown(61);
		if (this.debugPressed && !debugPressed) {
			if (cancelDebug)
				cancelDebug = false;
			else
				options.debugEnabled = !options.debugEnabled;
		}
		this.debugPressed = debugPressed;
	}

	private void debugChatInfo(String message) {
		inGameHud.getChatHud()
				.addMessage(new LiteralText("[" + Formatting.GREEN + "Debug" + Formatting.RESET + "] " + message));
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear()V"))
	public void betterClearMessages(ChatHud instance) {
		((ChatHudExtension) instance).clearChat();
		cancelDebug = true;
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;reloadResources()V"))
	public void betterRefreshResources(CallbackInfo callback) {
		debugChatInfo("Reloaded Resources");
		cancelDebug = true;
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getBooleanValue(Lnet/minecraft/client/option/GameOptions$Option;I)V"))
	public void betterRenderDistance(CallbackInfo callback) {
		// Broken in vanilla.
		options.viewDistance = (int) GameOptions.Option.RENDER_DISTANCE
				.adjust(options.viewDistance + (Screen.hasShiftDown() ? -1 : 1));

		debugChatInfo("Render Distance: " + options.viewDistance);
		options.save();
		cancelDebug = true;
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;reload()V"))
	public void betterLoadRenderers(CallbackInfo callback) {
		debugChatInfo("Reloaded Chunks");
		cancelDebug = true;
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;save()V"))
	public void advancedTootipsAndPauseOnLostFocus(CallbackInfo callback) {
		cancelDebug = true;
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;save()V", ordinal = 0))
	public void advancedTootips(CallbackInfo callback) {
		debugChatInfo("Advanced Item Tooltips: " + options.advancedItemTooltips);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;save()V", ordinal = 1))
	public void pauseOnLostFocus(CallbackInfo callback) {
		debugChatInfo("Pause on Lost Focus: " + options.pauseOnLostFocus);
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;setRenderHitboxes(Z)V"))
	public void betterHitboxes(EntityRenderDispatcher instance, boolean value) {
		if (!EventBus.INSTANCE.post(new HitboxToggleEvent(value)).cancelled) {
			instance.setRenderHitboxes(value);
			debugChatInfo("Entity Hitboxes: " + value);
			cancelDebug = true;
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(IZ)V", ordinal = 1))
	public void nextKey(int keyCode, boolean pressed) {
		if (pressed && debugPressed) {
			if (Keyboard.getEventKey() == 32 || Keyboard.getEventKey() == 31 || Keyboard.getEventKey() == 20
					|| Keyboard.getEventKey() == 33 || Keyboard.getEventKey() == 30 || Keyboard.getEventKey() == 35
					|| Keyboard.getEventKey() == 48 || Keyboard.getEventKey() == 25) {
				return;
			}
		}

		KeyBinding.setKeyPressed(keyCode, pressed);
	}

	// endregion

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;"
			+ "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1))
	public void display(CallbackInfo callback) {
		loading = false;
	}

	@Inject(method = "toggleFullscreen", at = @At("HEAD"), cancellable = true)
	public void handleToggle(CallbackInfo callback) {
		FullscreenToggleEvent event = EventBus.INSTANCE.post(new FullscreenToggleEvent(!fullscreen));

		if (event.cancelled) {
			callback.cancel();
			options.fullscreen = fullscreen;
		} else if (!event.applyState) {
			callback.cancel();
			options.fullscreen = (fullscreen = !fullscreen);
		}
	}

	// Fix Replay Mod bug - textures animate too fast.

	private boolean earlyBird;

	@Override
	public void replayModSetEarlyReturnFromRunTick(boolean earlyBird) {
		this.earlyBird = earlyBird;
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;tick()V"))
	public void cancelTextureTick(TextureManager instance) {
		if (!earlyBird)
			instance.tick();
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;tick()V"))
	public void cancelHudTick(InGameHud instance) {
		if (!earlyBird)
			instance.tick();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I"))
	public void forceScroll(CallbackInfo callback) {
		if (earlyBird)
			onScroll();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"), cancellable = true)
	public void doEarlyReturnFromRunTick(CallbackInfo callback) {
		if (earlyBird)
			callback.cancel();
	}

	@Inject(method = "stop", at = @At("HEAD"))
	public void preShutdown(CallbackInfo callback) {
		EventBus.INSTANCE.post(new GameQuitEvent());
	}

	@Shadow
	public abstract void setScreen(Screen screen);

	@Shadow
	public GameOptions options;

	@Shadow
	private ServerInfo currentServerEntry;

	@Shadow
	public ClientWorld world;

	@Shadow
	public GameRenderer gameRenderer;

	@Shadow
	private String serverAddress;

	@Shadow
	public InGameHud inGameHud;

	@Shadow
	public ClientPlayerInteractionManager interactionManager;

	@Shadow
	private boolean fullscreen;

}
