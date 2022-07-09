package io.github.solclient.abstraction.mc.world.entity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.Helper;
import io.github.solclient.abstraction.mc.maths.Box;
import io.github.solclient.abstraction.mc.maths.Vec3d;
import io.github.solclient.abstraction.mc.world.entity.player.Player;
import io.github.solclient.abstraction.mc.world.item.ItemStack;
import io.github.solclient.abstraction.mc.world.level.chunk.ChunkPos;

public interface Entity {

	double getX();

	double getY();

	double getZ();

	double getPreviousX();

	double getPreviousY();

	double getPreviousZ();

	float getYaw();

	float getPitch();

	@NotNull Vec3d getPosition();

	/**
	 * Gets the numeric entity ID. Only guaranteed to be unique for the current
	 * session.
	 * @return The ID.
	 */
	int getNumericId();

	@NotNull UUID getId();

	@NotNull Box getBounds();

	boolean isInvisible();

	boolean isInvisibleTo(@NotNull Player player);

	Vec3d getEyePosition();

	Vec3d getEyePosition(float tickDelta);

	float getWidth();

	float getEyeHeight();

	Vec3d getView(float tickDelta);

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

	boolean isPassenger();

	boolean isOnGround();

	boolean isInWeb();

	boolean isInWater();

	boolean isSprinting();

}
