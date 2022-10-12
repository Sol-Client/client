package io.github.solclient.client.event.impl.world.entity;

import io.github.solclient.client.platform.mc.world.entity.Entity;
import lombok.*;

/**
 * Fires when the local player attacks another entity.
 */
@Data
@RequiredArgsConstructor
public class EntityAttackEvent {

	private final Entity entity;

}
