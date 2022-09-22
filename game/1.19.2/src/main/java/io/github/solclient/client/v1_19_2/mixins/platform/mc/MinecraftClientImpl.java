package io.github.solclient.client.v1_19_2.mixins.platform.mc;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.MouseHandler;
import io.github.solclient.client.platform.mc.Timer;
import io.github.solclient.client.platform.mc.Window;
import io.github.solclient.client.platform.mc.hud.IngameHud;
import io.github.solclient.client.platform.mc.lang.LanguageManager;
import io.github.solclient.client.platform.mc.network.LocalPlayerState;
import io.github.solclient.client.platform.mc.network.ServerData;
import io.github.solclient.client.platform.mc.option.Options;
import io.github.solclient.client.platform.mc.raycast.HitResult;
import io.github.solclient.client.platform.mc.resource.ResourceManager;
import io.github.solclient.client.platform.mc.screen.Screen;
import io.github.solclient.client.platform.mc.screen.TitleScreen;
import io.github.solclient.client.platform.mc.sound.SoundEngine;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.texture.TextureManager;
import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.entity.EntityRenderDispatcher;
import io.github.solclient.client.platform.mc.world.entity.player.LocalPlayer;
import io.github.solclient.client.platform.mc.world.item.ItemRenderer;
import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import io.github.solclient.client.platform.mc.world.level.LevelRenderer;
import io.github.solclient.client.platform.mc.world.particle.ParticleEngine;
import io.github.solclient.client.v1_19_2.mixins.accessor.MinecraftClientAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ReloadableResourceManagerImpl;

@Mixin(net.minecraft.client.MinecraftClient.class)
@Implements(@Interface(iface = MinecraftClient.class, prefix = "platform$"))
public abstract class MinecraftClientImpl {

	public @NotNull Window platform$getWindow() {
		return (Window) (Object) window;
	}

	@Shadow
	private @Final net.minecraft.client.util.Window window;

	public @Nullable ClientLevel platform$getLevel() {
		return (ClientLevel) world;
	}

	@Shadow
	private ClientWorld world;

	public @NotNull LevelRenderer platform$getLevelRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	public @Nullable LocalPlayer platform$getPlayer() {
		return (LocalPlayer) player;
	}

	private ClientPlayerEntity player;

	public @Nullable LocalPlayerState platform$getPlayerState() {
		// TODO Auto-generated method stub
		return null;
	}

	public @Nullable Entity platform$getCameraEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull Options platform$getOptions() {
		return (Options) options;
	}

	@Shadow
	public @Final GameOptions options;

	public @NotNull File platform$getDataFolder() {
		return runDirectory;
	}

	@Shadow
	public @Final File runDirectory;

	public @NotNull File platform$getPackFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull Font platform$getFont() {
		return (Font) textRenderer;
	}

	@Shadow
	public @Final TextRenderer textRenderer;

	public @Nullable Screen platform$getScreen() {
		return (Screen) currentScreen;
	}

	@Shadow
	public net.minecraft.client.gui.screen.Screen currentScreen;

	public void platform$setScreen(@Nullable Screen screen) {
		setScreen((net.minecraft.client.gui.screen.Screen) screen);
	}

	@Shadow
	public abstract void setScreen(net.minecraft.client.gui.screen.Screen screen);

	public boolean platform$isInMenu() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean platform$isGamePaused() {
		// TODO Auto-generated method stub
		return false;
	}

	public void platform$runSync(@NotNull Runnable runnable) {
		// TODO Auto-generated method stub

	}

	public void platform$runSyncLater(@NotNull Runnable runnable, int ticks) {
		// TODO Auto-generated method stub

	}

	public @NotNull TextureManager platform$getTextureManager() {
		return (TextureManager) textureManager;
	}

	@Shadow
	private @Final net.minecraft.client.texture.TextureManager textureManager;

	public @NotNull LanguageManager platform$getLanguageManager() {
		return (LanguageManager) languageManager;
	}

	@Shadow
	private @Final net.minecraft.client.resource.language.LanguageManager languageManager;

	public @NotNull ResourceManager platform$getResourceManager() {
		return (ResourceManager) resourceManager;
	}

	@Shadow
	private @Final ReloadableResourceManagerImpl resourceManager;

	public boolean platform$hasSingleplayerServer() {
		// TODO Auto-generated method stub
		return false;
	}

	public @Nullable ServerData platform$getCurrentServer() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull ItemRenderer platform$getItemRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull IngameHud platform$getIngameHud() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean platform$isGameRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	public @NotNull HitResult platform$getHitResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull Timer platform$getTimer() {
		return (Timer) renderTickCounter;
	}

	@Shadow
	private @Final RenderTickCounter renderTickCounter;


	public @NotNull EntityRenderDispatcher platform$getEntityRenderDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull ParticleEngine platform$getParticleEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	public @NotNull MouseHandler platform$getMouseHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean platform$isFullscreen() {
		// TODO Auto-generated method stub
		return false;
	}

	public void platform$toggleFullscreen() {
		// TODO Auto-generated method stub

	}

	public @NotNull SoundEngine platform$getSoundEngine() {
		return (SoundEngine) soundManager;
	}

	@Shadow
	private @Final SoundManager soundManager;

	public @Nullable TitleScreen platform$getTitleScreen() {
		return titleScreen;
	}

	private TitleScreen titleScreen;

	@Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
	public void setTitleScreen(net.minecraft.client.gui.screen.Screen screen, CallbackInfo callback) {
		if(screen instanceof net.minecraft.client.gui.screen.TitleScreen) {
			titleScreen = (TitleScreen) screen;
		}
	}

	public void platform$quit() {
		// TODO Auto-generated method stub

	}

}

@Mixin(MinecraftClient.class)
interface MinecraftClientImpl$Static {

	@Overwrite(remap = false)
	static MinecraftClient getInstance() {
		return (MinecraftClient) net.minecraft.client.MinecraftClient.getInstance();
	}

	@Overwrite(remap = false)
	static int getFps() {
		return MinecraftClientAccessor.getCurrentFps();
	}

}
