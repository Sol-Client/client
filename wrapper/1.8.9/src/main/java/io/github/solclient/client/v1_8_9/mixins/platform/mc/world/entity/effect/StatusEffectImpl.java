package io.github.solclient.client.v1_8_9.mixins.platform.mc.world.entity.effect;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.world.entity.effect.*;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(StatusEffectInstance.class)
@Implements(@Interface(iface = StatusEffect.class, prefix = "platform$"))
public abstract class StatusEffectImpl {

	public @NotNull StatusEffectType platform$getType() {
		return (StatusEffectType) net.minecraft.entity.effect.StatusEffect.STATUS_EFFECTS[getEffectId()];
	}

	@Shadow
	public abstract int getEffectId();

	public int platform$getEffectDuration() {
		return getDuration();
	}

	@Shadow
	public abstract int getDuration();

	public @NotNull String getDurationText() {
		return net.minecraft.entity.effect.StatusEffect.method_2436((net.minecraft.entity.effect.StatusEffectInstance) (Object) this);
	}

	public int platform$getEffectAmplifier() {
		return getAmplifier();
	}

	@Shadow
	public abstract int getAmplifier();

	public boolean platform$showIcon() {
		return net.minecraft.entity.effect.StatusEffect.STATUS_EFFECTS[getEffectId()].method_2443();
	}

	public boolean platform$showAmplifier() {
		return getAmplifier() >= 1 && getAmplifier() <= 9;
	}

	public @NotNull String getAmplifierName() {
		return "enchantment.level." + getAmplifier();
	}

}

@Mixin(StatusEffect.class)
interface StatusEffectImpl$Static {

	@Overwrite(remap = false)
	static StatusEffect create(StatusEffectType type) {
		return (StatusEffect) new StatusEffectInstance(((net.minecraft.entity.effect.StatusEffect) type).getId(), 0);
	}

	@Overwrite(remap = false)
	static StatusEffect create(StatusEffectType type, int duration) {
		return (StatusEffect) new StatusEffectInstance(((net.minecraft.entity.effect.StatusEffect) type).getId(), duration);
	}

	@Overwrite(remap = false)
	static StatusEffect create(StatusEffectType type, int duration, int amplifier) {
		return (StatusEffect) new StatusEffectInstance(((net.minecraft.entity.effect.StatusEffect) type).getId(), duration, amplifier);
	}

}
