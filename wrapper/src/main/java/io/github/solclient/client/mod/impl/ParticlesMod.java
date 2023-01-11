package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.EntityAttackEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumParticleTypes;

public class ParticlesMod extends Mod implements PrimaryIntegerSettingMod {

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
		EntityPlayer player = mc.thePlayer;

		if (!(event.victim instanceof EntityLivingBase)) {
			return;
		}

		boolean crit = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater()
				&& !player.isPotionActive(Potion.blindness) && player.ridingEntity == null;

		if (crit) {
			for (int i = 0; i < multiplier - 1; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.CRIT);
			}
		}

		boolean usuallySharpness = EnchantmentHelper.getModifierForCreature(player.getHeldItem(),
				((EntityLivingBase) event.victim).getCreatureAttribute()) > 0;

		if (sharpness || usuallySharpness) {
			for (int i = 0; i < (usuallySharpness ? multiplier - 1 : multiplier); i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.CRIT_MAGIC);
			}
		}

		if (snow) {
			for (int i = 0; i < multiplier; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.SNOWBALL);
			}
		}

		if (slime) {
			for (int i = 0; i < multiplier; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.SLIME);
			}
		}

		if (flames) {
			for (int i = 0; i < multiplier; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.FLAME);
			}
		}
	}

}
