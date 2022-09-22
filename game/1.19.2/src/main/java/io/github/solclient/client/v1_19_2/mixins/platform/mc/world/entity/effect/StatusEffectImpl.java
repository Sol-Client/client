package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.world.entity.effect.StatusEffect;
import io.github.solclient.client.platform.mc.world.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;

@Mixin(StatusEffectInstance.class)
@Implements(@Interface(iface = StatusEffect.class, prefix = "platform$"))
public abstract class StatusEffectImpl {

	public @NotNull StatusEffectType platform$getType() {
		return (StatusEffectType) getEffectType();
	}

	@Shadow
	public abstract net.minecraft.entity.effect.StatusEffect getEffectType();

	public int platform$getEffectDuration() {
		return getDuration();
	}

	@Shadow
	public abstract int getDuration();

	public @NotNull String getDurationText() {
		return StatusEffectUtil.durationToString((StatusEffectInstance) (Object) this, 1);
	}

	public int platform$getEffectAmplifier() {
		return getAmplifier();
	}

	@Shadow
	public abstract int getAmplifier();

	public boolean platform$showIcon() {
		return shouldShowIcon();
	}

	@Shadow
	public abstract boolean shouldShowIcon();

	public boolean platform$showAmplifier() {
		return getAmplifier() >= 1 && getAmplifier() <= 9;
	}

	public @NotNull String getAmplifierName() {
		return "enchantment.level." + (getAmplifier() + 1);
	}

}

@Mixin(StatusEffect.class)
interface StatusEffectImpl$Static {

	@Overwrite(remap = false)
	static StatusEffect create(StatusEffectType type) {
		return (StatusEffect) new StatusEffectInstance((net.minecraft.entity.effect.StatusEffect) type);
	}

	@Overwrite(remap = false)
	static StatusEffect create(StatusEffectType type, int duration) {
		return (StatusEffect) new StatusEffectInstance((net.minecraft.entity.effect.StatusEffect) type, duration);
	}

	@Overwrite(remap = false)
	static StatusEffect create(StatusEffectType type, int duration, int amplifier) {
		return (StatusEffect) new StatusEffectInstance((net.minecraft.entity.effect.StatusEffect) type, duration, amplifier);
	}

}
