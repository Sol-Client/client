package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.entity.Entity;

@AllArgsConstructor
public class EntityAttackEvent {

	public final Entity victim;

}
