package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.entity;

import java.util.*;

import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.entity.*;
import io.github.solclient.client.platform.mc.world.entity.effect.*;
import io.github.solclient.client.platform.mc.world.item.ItemStack;
import net.minecraft.entity.EntityGroup;

@Mixin(net.minecraft.entity.LivingEntity.class)
@Implements(@Interface(iface = LivingEntity.class, prefix = "platform$"))
public abstract class LivingEntityImpl {

	public Collection<StatusEffect> platform$getEntityStatusEffects() {
		return getStatusEffectInstances();
	}

	@Shadow
	public abstract Collection<StatusEffect> getStatusEffectInstances();

	public boolean platform$hasStatusEffect(StatusEffectType effect) {
		return hasStatusEffect((net.minecraft.entity.effect.StatusEffect) effect);
	}

	@Shadow
	public abstract boolean hasStatusEffect(net.minecraft.entity.effect.StatusEffect effect);

	public boolean platform$isEntityClimbing() {
		return isClimbing();
	}

	@Shadow
	public abstract boolean isClimbing();

	public ItemStack platform$getMainHandItem() {
		return (ItemStack) (Object) getStackInHand();
	}

	@Shadow
	public abstract net.minecraft.item.ItemStack getStackInHand();

	public LivingEntityType platform$getLivingEntityType() {
		return (LivingEntityType) (Object) getGroup();
	}

	@Shadow
	public abstract EntityGroup getGroup();

	public boolean platform$isEntityUsingItem() {
		// TODO Auto-generated method stub
		return false;
	}

	public int platform$getItemUsageRemaining() {
		// TODO Auto-generated method stub
		return 0;
	}

}

