package io.github.solclient.client.event.impl.world.entity;

import io.github.solclient.abstraction.mc.world.entity.Entity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EntityDamageEffectEvent {

	private final Entity entity;

}
