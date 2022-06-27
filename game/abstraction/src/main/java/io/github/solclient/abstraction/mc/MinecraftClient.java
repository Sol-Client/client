package io.github.solclient.abstraction.mc;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.mc.hud.IngameHud;
import io.github.solclient.abstraction.mc.lang.LanguageManager;
import io.github.solclient.abstraction.mc.maths.Vec3d;
import io.github.solclient.abstraction.mc.network.LocalPlayerState;
import io.github.solclient.abstraction.mc.network.ServerData;
import io.github.solclient.abstraction.mc.option.Options;
import io.github.solclient.abstraction.mc.raycast.HitResult;
import io.github.solclient.abstraction.mc.screen.Screen;
import io.github.solclient.abstraction.mc.text.Font;
import io.github.solclient.abstraction.mc.texture.TextureManager;
import io.github.solclient.abstraction.mc.world.entity.Entity;
import io.github.solclient.abstraction.mc.world.entity.player.LocalPlayer;
import io.github.solclient.abstraction.mc.world.entity.player.Player;
import io.github.solclient.abstraction.mc.world.item.ItemRenderer;
import io.github.solclient.abstraction.mc.world.level.ClientLevel;
import io.github.solclient.abstraction.mc.world.level.Level;

import java.io.File;
import java.util.Optional;

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

	boolean hasLevel();

	@Nullable ClientLevel getLevel();

	boolean hasPlayer();

	@Nullable LocalPlayer getPlayer();

	@Nullable LocalPlayerState getPlayerState();

	/**
	 * Gets the camera entity.
	 * Guaranteed to return a non-null value if {@link #hasPlayer()} is <code>true</code>.
	 * @return The focused entity, or <code>null</code> if
	 */
	@Nullable Entity getCameraEntity();

	@NotNull Options getOptions();

	@NotNull File getDataFolder();

	@NotNull File getPackFolder();

	@NotNull Font getFont();

	@Nullable Screen getScreen();

	@Nullable Optional<Screen> getScreen(Class<?> type);

	void setScreen(@Nullable Screen screen);

	void closeScreen();

	boolean isInMenu();

	boolean isPaused();

	void runSync(@NotNull Runnable runnable);

	void runSyncLater(@NotNull Runnable runnable, int ticks);

	@NotNull TextureManager getTextureManager();

	@NotNull LanguageManager getLanguageManager();

	boolean hasSingleplayerServer();

	@Nullable ServerData getCurrentServer();

	@NotNull ItemRenderer getItemRenderer();

	@NotNull IngameHud getIngameHud();

	boolean isRunning();

	@NotNull HitResult getHitResult();

	@NotNull Timer getTimer();

}
