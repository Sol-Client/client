package io.github.solclient.client.v1_8_9.mixins.platform;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

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
import net.minecraft.client.options.GameOptions;
import net.minecraft.resource.ReloadableResourceManager;

@Mixin(net.minecraft.client.MinecraftClient.class)
public class MinecraftClientImpl implements MinecraftClient {

	@Shadow
	private @Final ReloadableResourceManager resourceManager;
	@Shadow
	public @Final GameOptions options;
	private net.minecraft.client.util.Window window;

	@Override
	public @NotNull Window getWindow() {
		return (Window) window;
	}

	@Inject(method = "method_2923", at = @At("RETURN"))
	public void updateWindow() {
		window = new net.minecraft.client.util.Window((net.minecraft.client.MinecraftClient) (Object) this);
	}

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

	@Override
	public @NotNull File getDataFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull File getPackFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Font getFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable Screen getScreen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setScreen(@Nullable Screen screen) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isInMenu() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPaused() {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull LanguageManager getLanguageManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull ResourceManager getResourceManager() {
		return (ResourceManager) resourceManager;
	}

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
	public boolean isRunning() {
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
		// TODO Auto-generated method stub
		return null;
	}

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
	public void toggleFullscreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public @NotNull SoundEngine getSoundEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable TitleScreen getMainMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub

	}

}

@Mixin(MinecraftClient.class)
interface MinecraftClientImpl$Static {

	@Overwrite
	static MinecraftClient getInstance() {
		return (MinecraftClient) net.minecraft.client.MinecraftClient.getInstance();
	}

}
