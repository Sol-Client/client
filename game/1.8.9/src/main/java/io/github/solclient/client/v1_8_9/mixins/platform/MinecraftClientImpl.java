//package io.github.solclient.client.v1_8_9.mixins.platform;
//
//import java.io.File;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//
//import io.github.solclient.client.platform.mc.MinecraftClient;
//import io.github.solclient.client.platform.mc.MouseHandler;
//import io.github.solclient.client.platform.mc.Timer;
//import io.github.solclient.client.platform.mc.Window;
//import io.github.solclient.client.platform.mc.hud.IngameHud;
//import io.github.solclient.client.platform.mc.lang.LanguageManager;
//import io.github.solclient.client.platform.mc.network.LocalPlayerState;
//import io.github.solclient.client.platform.mc.network.ServerData;
//import io.github.solclient.client.platform.mc.option.Options;
//import io.github.solclient.client.platform.mc.raycast.HitResult;
//import io.github.solclient.client.platform.mc.screen.Screen;
//import io.github.solclient.client.platform.mc.screen.TitleScreen;
//import io.github.solclient.client.platform.mc.sound.SoundEngine;
//import io.github.solclient.client.platform.mc.text.Font;
//import io.github.solclient.client.platform.mc.texture.TextureManager;
//import io.github.solclient.client.platform.mc.world.entity.Entity;
//import io.github.solclient.client.platform.mc.world.entity.EntityRenderDispatcher;
//import io.github.solclient.client.platform.mc.world.entity.player.LocalPlayer;
//import io.github.solclient.client.platform.mc.world.item.ItemRenderer;
//import io.github.solclient.client.platform.mc.world.level.ClientLevel;
//import io.github.solclient.client.platform.mc.world.level.LevelRenderer;
//import io.github.solclient.client.platform.mc.world.particle.ParticleEngine;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.client.multiplayer.PlayerControllerMP;
//import net.minecraft.client.multiplayer.WorldClient;
//import net.minecraft.client.renderer.EntityRenderer;
//import net.minecraft.client.renderer.RenderGlobal;
//import net.minecraft.client.settings.GameSettings;
//
//@Mixin(Minecraft.class)
//public class MinecraftClientImpl implements MinecraftClient {
//
//	// injected fields
//
//	private Window window = new WindowImpl();
//
//	// shadow fields
//
//	@Shadow
//	public WorldClient theWorld;
//	@Shadow
//	public EntityPlayerSP thePlayer;
//	@Shadow
//	private Entity renderViewEntity;
//	@Shadow
//	public PlayerControllerMP playerController;
//	@Shadow
//	public RenderGlobal renderGlobal;
//	@Shadow
//	public GameSettings gameSettings;
//	@Shadow
//	public File mcDataDir;
//	@Shadow
//	private File fileResourcepacks;
//
//	// impl
//
//	@Override
//	public @NotNull Window getWindow() {
//		return window;
//	}
//
//	@Override
//	public @Nullable ClientLevel getLevel() {
//		return (ClientLevel) theWorld;
//	}
//
//	@Override
//	public @NotNull LevelRenderer getLevelRenderer() {
//		return (LevelRenderer) renderGlobal;
//	}
//
//	@Override
//	public @Nullable LocalPlayer getPlayer() {
//		return (LocalPlayer) thePlayer;
//	}
//
//	@Override
//	public @Nullable LocalPlayerState getPlayerState() {
//		return (LocalPlayerState) playerController;
//	}
//
//	@Override
//	public @Nullable Entity getCameraEntity() {
//		if(renderViewEntity == null) {
//			return getPlayer();
//		}
//
//		return (Entity) renderViewEntity;
//	}
//
//	@Override
//	public @NotNull Options getOptions() {
//		return (Options) gameSettings;
//	}
//
//	@Override
//	public @NotNull File getDataFolder() {
//		return mcDataDir;
//	}
//
//	@Override
//	public @NotNull File getPackFolder() {
//		return fileResourcepacks;
//	}
//
//	@Override
//	public @NotNull Font getFont() {
//		return null;
//	}
//
//	@Override
//	public @Nullable Screen getScreen() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void setScreen(@Nullable Screen screen) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean isInMenu() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isPaused() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void runSync(@NotNull Runnable runnable) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void runSyncLater(@NotNull Runnable runnable, int ticks) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public @NotNull TextureManager getTextureManager() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public @NotNull LanguageManager getLanguageManager() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean hasSingleplayerServer() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public @Nullable ServerData getCurrentServer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public @NotNull ItemRenderer getItemRenderer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public @NotNull IngameHud getIngameHud() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isRunning() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public @NotNull HitResult getHitResult() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public @NotNull Timer getTimer() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public @NotNull EntityRenderDispatcher getEntityRenderDispatcher() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public @NotNull ParticleEngine getParticleEngine() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public @NotNull MouseHandler getMouseHandler() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isFullscreen() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void toggleFullscreen() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public @NotNull SoundEngine getSoundEngine() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public @Nullable TitleScreen getMainMenu() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void quit() {
//		// TODO Auto-generated method stub
//
//	}
//
//}
