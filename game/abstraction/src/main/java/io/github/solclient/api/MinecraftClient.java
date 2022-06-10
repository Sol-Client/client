package io.github.solclient.api;

import io.github.solclient.api.option.Options;
import io.github.solclient.api.screen.Screen;
import io.github.solclient.api.text.TextRenderer;
import io.github.solclient.api.world.entity.LocalPlayer;
import io.github.solclient.api.world.entity.Player;
import io.github.solclient.api.world.World;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * A representation of the Minecraft client.
 */
public interface MinecraftClient {

	static @NotNull MinecraftClient getInstance() {
		throw new UnsupportedOperationException();
	}

	@NotNull Window getWindow();

	boolean hasWorld();

	@Nullable World getWorld();

	boolean hasPlayer();

	@Nullable LocalPlayer getPlayer();

	@NotNull Options getOptions();

	@NotNull File getDataFolder();

	@NotNull TextRenderer getTextRenderer();

	@Nullable Screen getScreen();

	void openScreen(@Nullable Screen screen);

	default void closeScreen() {
		openScreen(null);
	}

	default boolean isInMenu() {
		return getScreen() != null;
	}

	boolean isPaused();

}
