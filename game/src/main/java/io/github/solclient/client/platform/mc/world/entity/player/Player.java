package io.github.solclient.client.platform.mc.world.entity.player;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.entity.LivingEntity;
import io.github.solclient.client.platform.mc.world.inventory.Inventory;

public interface Player extends LivingEntity {

	@NotNull Inventory getInventory();

	@NotNull Abilities getAbilities();

}
