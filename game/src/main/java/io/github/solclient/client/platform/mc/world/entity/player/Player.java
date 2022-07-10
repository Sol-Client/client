package io.github.solclient.client.platform.mc.world.entity.player;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.entity.LivingEntity;
import io.github.solclient.client.platform.mc.world.entity.effect.StatusEffect;
import io.github.solclient.client.platform.mc.world.inventory.Inventory;
import io.github.solclient.client.platform.mc.world.level.chunk.ChunkPos;

public interface Player extends LivingEntity {

	@NotNull Inventory getInventory();

	float getFallDistance();

	@NotNull Abilities getAbilities();

	boolean isUsingItem();

	int getItemUsageRemaining();

}
