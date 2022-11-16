package io.github.solclient.client.platform.mc.world.entity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.maths.*;
import io.github.solclient.client.platform.mc.world.entity.player.Player;
import io.github.solclient.client.platform.mc.world.level.chunk.ChunkPos;

public interface Entity {

	double x();

	double y();

	double z();

	double previousX();

	double previousY();

	double previousZ();

	float yaw();

	float pitch();

	@NotNull Vec3d getPosition();

	/**
	 * Gets the numeric entity ID. Only guaranteed to be unique for the current
	 * session.
	 * @return The ID.
	 */
	int getNumericId();

	@NotNull UUID getId();

	@NotNull Box getBounds();

	boolean isGloballyInvisible();

	boolean isInvisibleTo(@NotNull Player player);

	Vec3d getEyePosition();

	Vec3d getEyePosition(float tickDelta);

	float getEntityWidth();

	float getEntityEyeHeight();

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

	boolean isEntityOnGround();

	boolean isInWeb();

	boolean isInWater();

	boolean isEntitySprinting();

	float getFallDistance();

}
