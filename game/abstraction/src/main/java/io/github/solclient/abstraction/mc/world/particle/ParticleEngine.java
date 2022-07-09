package io.github.solclient.abstraction.mc.world.particle;

import io.github.solclient.abstraction.mc.world.entity.Entity;

public interface ParticleEngine {

	void emit(Entity entity, ParticleType particle);

}
