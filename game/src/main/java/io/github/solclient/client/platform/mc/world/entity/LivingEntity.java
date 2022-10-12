package io.github.solclient.client.platform.mc.world.entity;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.entity.effect.*;
import io.github.solclient.client.platform.mc.world.item.ItemStack;

public interface LivingEntity extends Entity {

	@NotNull List<StatusEffect> getStatusEffects();

	boolean hasStatusEffect(StatusEffectType effect);

	boolean isEntityClimbing();

	@NotNull ItemStack getMainHandItem();

	@NotNull LivingEntityType getLivingEntityType();

	boolean isEntityUsingItem();

	int getItemUsageRemaining();

}
