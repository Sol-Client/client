package io.github.solclient.client.event.impl.world.entity;

import io.github.solclient.abstraction.mc.world.entity.Entity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Fires when the local player attacks another entity.
 */
@Data
@RequiredArgsConstructor
public class EntityAttackEvent {

	private final Entity entity;

}
