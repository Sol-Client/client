package io.github.solclient.abstraction.mc.world.particle;

import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.mc.Direction;
import io.github.solclient.abstraction.mc.world.entity.Entity;
import io.github.solclient.abstraction.mc.world.level.block.BlockPos;

public interface ParticleEngine {

	void emit(Entity entity, ParticleType particle);

	void emitDestruction(@Nullable BlockPos blockPos, @Nullable Direction side);

}
