package io.github.solclient.abstraction.mc;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.Helper;
import io.github.solclient.abstraction.mc.hud.IngameHud;
import io.github.solclient.abstraction.mc.lang.LanguageManager;
import io.github.solclient.abstraction.mc.network.LocalPlayerState;
import io.github.solclient.abstraction.mc.network.ServerData;
import io.github.solclient.abstraction.mc.option.Options;
import io.github.solclient.abstraction.mc.raycast.HitResult;
import io.github.solclient.abstraction.mc.screen.Screen;
import io.github.solclient.abstraction.mc.sound.SoundEngine;
import io.github.solclient.abstraction.mc.text.Font;
import io.github.solclient.abstraction.mc.texture.TextureManager;
import io.github.solclient.abstraction.mc.world.entity.Entity;
import io.github.solclient.abstraction.mc.world.entity.EntityRenderDispatcher;
import io.github.solclient.abstraction.mc.world.entity.player.LocalPlayer;
import io.github.solclient.abstraction.mc.world.item.ItemRenderer;
import io.github.solclient.abstraction.mc.world.level.ClientLevel;
import io.github.solclient.abstraction.mc.world.level.LevelRenderer;
import io.github.solclient.abstraction.mc.world.particle.ParticleEngine;

/**
 * A representation of the Minecraft client.
 */
public interface MinecraftClient {

	static @NotNull MinecraftClient getInstance() {
		throw new UnsupportedOperationException();
	}

	static int getFps() {
		throw new UnsupportedOperationException();
	}

	@NotNull Window getWindow();

	@Helper
	default boolean hasLevel() {
		return getLevel() != null;
	}

	@Nullable ClientLevel getLevel();

	@NotNull LevelRenderer getLevelRenderer();

	@Helper
	default boolean hasPlayer() {
		return getPlayer() != null;
	}

	@Nullable LocalPlayer getPlayer();

	@Nullable LocalPlayerState getPlayerState();

	/**
	 * Gets the camera entity.
	 * Guaranteed to return a non-null value if {@link #hasPlayer()} is <code>true</code>.
	 * @return The focused entity, or <code>null</code>.
	 */
	@Nullable Entity getCameraEntity();

	@NotNull Options getOptions();

	@NotNull File getDataFolder();

	@Helper
	@NotNull File getPackFolder();

	@NotNull Font getFont();

	@Nullable Screen getScreen();

	@SuppressWarnings("unchecked")
	@Helper
	default @Nullable <T> Optional<T> getScreen(Class<T> type) {
		if(type.isAssignableFrom(getScreen().getClass())) {
			return Optional.of((T) getScreen());
		}
		return Optional.empty();
	}

	void setScreen(@Nullable Screen screen);

	@Helper
	default void closeScreen() {
		setScreen(null);
	}

	boolean isInMenu();

	boolean isPaused();

	void runSync(@NotNull Runnable runnable);

	@Helper
	void runSyncLater(@NotNull Runnable runnable, int ticks);

	@NotNull TextureManager getTextureManager();

	@NotNull LanguageManager getLanguageManager();

	boolean hasSingleplayerServer();

	@Nullable ServerData getCurrentServer();

	@NotNull ItemRenderer getItemRenderer();

	@NotNull IngameHud getIngameHud();

	boolean isRunning();

	// Should never be null, unlike vanilla.
	@NotNull HitResult getHitResult();

	@NotNull Timer getTimer();

	@NotNull EntityRenderDispatcher getEntityRenderDispatcher();

	@NotNull ParticleEngine getParticleEngine();

	@NotNull MouseHandler getMouseHandler();

	boolean isFullscreen();

	void toggleFullscreen();

	@NotNull SoundEngine getSoundEngine();

}
