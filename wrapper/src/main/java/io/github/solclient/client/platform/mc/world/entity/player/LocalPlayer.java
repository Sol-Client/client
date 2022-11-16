package io.github.solclient.client.platform.mc.world.entity.player;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.network.Connection;
import io.github.solclient.client.platform.mc.text.Text;

public interface LocalPlayer extends ClientPlayer {

	@NotNull Connection getConnection();

	/**
	 * Sends a message to the chat gui.
	 * @param text The message, in plain text.
	 */
	void sendSystemMessage(@NotNull String text);

	/**
	 * Sends a component to the chat gui.
	 * @param text The text component.
	 */
	void sendSystemMessage(@NotNull Text text);

	/**
	 * Sends a chat packet from the player; acts as though this text was entered
	 * into the chat GUI.
	 * @param text The text.
	 */
	void chat(@NotNull String text);

	default void executeCommand(@NotNull String command) {
		chat("/" + command);
	}

	boolean isSpectatorMode();

	void clientSwing();

}
