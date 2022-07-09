package io.github.solclient.abstraction.mc.world.entity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.maths.Box;
import io.github.solclient.abstraction.mc.maths.Vec3d;
import io.github.solclient.abstraction.mc.world.entity.player.Player;

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

}
