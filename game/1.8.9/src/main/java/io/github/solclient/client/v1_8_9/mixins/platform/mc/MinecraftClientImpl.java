package io.github.solclient.client.v1_8_9.mixins.platform.mc;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.ClientTickTracker;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.resource.ReloadableResourceManager;

@Mixin(net.minecraft.client.MinecraftClient.class)
public abstract class MinecraftClientImpl implements MinecraftClient {

	@Override
	public @NotNull Window getWindow() {
		return (Window) window;
	}

	@Inject(method = {"onResolutionChanged", "initializeGame"}, at = @At("RETURN"))
	public void updateWindow(CallbackInfo callback) {
		window = new net.minecraft.client.util.Window((net.minecraft.client.MinecraftClient) (Object) this);
	}

	private net.minecraft.client.util.Window window;

	@Override
	public @Nullable ClientLevel getLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull LevelRenderer getLevelRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable LocalPlayer getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable LocalPlayerState getPlayerState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable Entity getCameraEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Options getOptions() {
		return (Options) options;
	}

	@Shadow
	public @Final GameOptions options;

	@Override
	public @NotNull File getDataFolder() {
		return runDirectory;
	}

	@Shadow
	public @Final File runDirectory;

	@Override
	public @NotNull File getPackFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Font getFont() {
		return (Font) textRenderer;
	}

	@Shadow
	public TextRenderer textRenderer;

	@Override
	public @Nullable Screen getScreen() {
		return (Screen) currentScreen;
	}

	@Shadow
	public net.minecraft.client.gui.screen.Screen currentScreen;

	@Override
	public void setScreen(@Nullable Screen screen) {
		openScreen((net.minecraft.client.gui.screen.Screen) screen);
	}

	@Shadow
	public abstract void openScreen(net.minecraft.client.gui.screen.Screen screen);

	@Override
	public boolean isInMenu() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGamePaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runSync(@NotNull Runnable runnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void runSyncLater(@NotNull Runnable runnable, int ticks) {
		// TODO Auto-generated method stub

	}

	@Override
	public @NotNull TextureManager getTextureManager() {
		return (TextureManager) textureManager;
	}

	@Shadow
	private net.minecraft.client.texture.TextureManager textureManager;

	@Override
	public @NotNull LanguageManager getLanguageManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull ResourceManager getResourceManager() {
		return (ResourceManager) resourceManager;
	}

	@Shadow
	private @Final ReloadableResourceManager resourceManager;

	@Override
	public boolean hasSingleplayerServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public @Nullable ServerData getCurrentServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull ItemRenderer getItemRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull IngameHud getIngameHud() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGameRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public @NotNull HitResult getHitResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Timer getTimer() {
		return (Timer) tricker;
	}

	// :P tongue twister
	@Shadow
	private ClientTickTracker tricker;

	@Override
	public @NotNull EntityRenderDispatcher getEntityRenderDispatcher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull ParticleEngine getParticleEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull MouseHandler getMouseHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFullscreen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toggleFullscreenState() {
		// TODO Auto-generated method stub

	}

	@Override
	public @NotNull SoundEngine getSoundEngine() {
		return (SoundEngine) getSoundManager();
	}

	@Shadow
	public abstract SoundManager getSoundManager();

	@Override
	public @Nullable TitleScreen getTitleScreen() {
		return titleScreen;
	}

	private TitleScreen titleScreen;

	@Inject(method = "openScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
	public void setTitleScreen(net.minecraft.client.gui.screen.Screen screen, CallbackInfo callback) {
		if(screen instanceof net.minecraft.client.gui.screen.TitleScreen) {
			titleScreen = (TitleScreen) screen;
		}
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub

	}

}

@Mixin(MinecraftClient.class)
interface MinecraftClientImpl$Static {

	@Overwrite(remap = false)
	static MinecraftClient getInstance() {
		return (MinecraftClient) net.minecraft.client.MinecraftClient.getInstance();
	}

}
