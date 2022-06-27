package io.github.solclient.abstraction.mc.world.level;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.entity.Entity;

public interface ClientLevel extends Level {

	@NotNull Iterable<Entity> getRenderedEntities();

}
