/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

public class ParticlesMod extends SolClientMod {

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
