package io.github.solclient.client.platform.mc.world.entity;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.entity.effect.StatusEffect;
import io.github.solclient.client.platform.mc.world.entity.effect.StatusEffectType;
import io.github.solclient.client.platform.mc.world.item.ItemStack;

public interface LivingEntity extends Entity {

	float getHealth();

	@NotNull List<StatusEffect> getStatusEffects();

	boolean hasStatusEffect(StatusEffectType effect);

	boolean isClimbing();

	@NotNull ItemStack getMainHandItem();

	@NotNull LivingEntityType getLivingEntityType();

}
