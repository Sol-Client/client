package me.mcblueparrot.client.mod.impl;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.EntityAttackEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.annotation.Slider;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumParticleTypes;

public class ParticlesMod extends Mod implements PrimaryIntegerSettingMod {

	@Expose
	@Option
	@Slider(min = 1, max = 10, step = 1)
	public float multiplier = 4;
	@Expose
	@Option
	public boolean sharpness = true;
	@Expose
	@Option
	public boolean snow;
	@Expose
	@Option
	public boolean slime;
	@Expose
	@Option
	public boolean flames;

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

		if(!(event.victim instanceof EntityLivingBase)) {
			return;
		}

		boolean crit = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater()
				&& !player.isPotionActive(Potion.blindness) && player.ridingEntity == null;

		if(crit) {
			for(int i = 0; i < multiplier - 1; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.CRIT);
			}
		}

		boolean usuallySharpness = EnchantmentHelper.getModifierForCreature(player.getHeldItem(),
				((EntityLivingBase) event.victim).getCreatureAttribute()) > 0;

		if(sharpness || usuallySharpness) {
			for(int i = 0; i < (usuallySharpness ? multiplier - 1 : multiplier); i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.CRIT_MAGIC);
			}
		}

		if(snow) {
			for(int i = 0; i < multiplier; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.SNOWBALL);
			}
		}

		if(slime) {
			for(int i = 0; i < multiplier; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.SLIME);
			}
		}

		if(flames) {
			for(int i = 0; i < multiplier; i++) {
				mc.effectRenderer.emitParticleAtEntity(event.victim, EnumParticleTypes.FLAME);
			}
		}
	}

}
