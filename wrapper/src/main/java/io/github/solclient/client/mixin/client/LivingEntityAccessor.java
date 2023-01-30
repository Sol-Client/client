package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

	@Invoker("getMiningSpeedMultiplier")
	int getArmSwingAnimationEnd();

}
