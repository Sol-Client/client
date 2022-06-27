package io.github.solclient.abstraction.mc.raycast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.mc.world.entity.Entity;

public interface HitResult {

	@NotNull HitType getType();

	@Nullable Entity getEntity();

}
