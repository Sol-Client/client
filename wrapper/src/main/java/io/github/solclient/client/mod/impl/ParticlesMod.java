package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.EntityAttackEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.*;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;

public class ParticlesMod extends SolClientMod implements PrimaryIntegerSettingMod {

	@Expose
	@Option
	@Slider(min = 1, max = 10, step = 1)
	private float multiplier = 4;
	@Expose
	@Option
	private boolean sharpness = true;
	@Expose
	@Option
	private boolean snow;
	@Expose
	@Option
	private boolean slime;
	@Expose
	@Option
	private boolean flames;

	@Override
	public String getId() {
		return "particles";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public void decrement() {
		multiplier = Math.max(1, multiplier - 1);
	}

	@Override
	public void increment() {
		multiplier = Math.min(10, multiplier + 1);
	}

	@EventHandler
	public void onAttack(EntityAttackEvent event) {
		PlayerEntity player = mc.player;

		if (!(event.victim instanceof LivingEntity)) {
			return;
		}

		boolean crit = player.fallDistance > 0.0F && !player.onGround && !player.isClimbing()
				&& !player.isTouchingWater() && !player.hasStatusEffect(StatusEffect.BLINDNESS)
				&& player.vehicle == null;

		if (crit) {
			for (int i = 0; i < multiplier - 1; i++) {
				mc.particleManager.addEmitter(event.victim, ParticleType.CRIT);
			}
		}

		boolean usuallySharpness = EnchantmentHelper.getAttackDamage(player.getMainHandStack(),
				((LivingEntity) event.victim).getGroup()) > 0;

		if (sharpness || usuallySharpness) {
			for (int i = 0; i < (usuallySharpness ? multiplier - 1 : multiplier); i++) {
				mc.particleManager.addEmitter(event.victim, ParticleType.CRIT_MAGIC);
			}
		}

		if (snow) {
			for (int i = 0; i < multiplier; i++) {
				mc.particleManager.addEmitter(event.victim, ParticleType.SNOWBALL);
			}
		}

		if (slime) {
			for (int i = 0; i < multiplier; i++) {
				mc.particleManager.addEmitter(event.victim, ParticleType.SLIME);
			}
		}

		if (flames) {
			for (int i = 0; i < multiplier; i++) {
				mc.particleManager.addEmitter(event.victim, ParticleType.FIRE);
			}
		}
	}

}
