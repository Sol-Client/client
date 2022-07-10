package io.github.solclient.client.platform.mc.world.entity.player;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.network.Connection;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.world.entity.effect.StatusEffect;
import io.github.solclient.client.platform.mc.world.inventory.Inventory;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import io.github.solclient.client.platform.mc.world.level.chunk.ChunkPos;

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

	boolean isSpectator();

	void clientSwing();

}
