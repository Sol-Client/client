package io.github.solclient.client.event.impl.world.entity;

import io.github.solclient.client.platform.mc.world.entity.Entity;
import lombok.*;

@Data
@RequiredArgsConstructor
public final class EntityDamageEffectEvent {

	private final Entity entity;

}
