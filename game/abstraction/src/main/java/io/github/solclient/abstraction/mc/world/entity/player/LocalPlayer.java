package io.github.solclient.abstraction.mc.world.entity.player;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.network.Connection;
import io.github.solclient.abstraction.mc.text.Text;
import io.github.solclient.abstraction.mc.world.entity.effect.StatusEffect;
import io.github.solclient.abstraction.mc.world.inventory.Inventory;
import io.github.solclient.abstraction.mc.world.level.chunk.ChunkPos;

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

	/**
	 * Gets the X position of the current chunk.
	 * @return The X.
	 */
	int getChunkX();

	/**
	 * Gets the Z position of the current chunk.
	 * @return The Z.
	 */
	int getChunkZ();

	/**
	 * Gets the current chunk position.
	 * @return The position.
	 */
	@NotNull ChunkPos getChunkPos();

	@NotNull Inventory getInventory();

	@NotNull List<StatusEffect> getStatusEffects();

}
