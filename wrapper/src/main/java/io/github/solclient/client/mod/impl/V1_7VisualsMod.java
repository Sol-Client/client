/**
 * Credit for OrangeMarshall for original mod.
 * It helped a lot when making this.
 */

package io.github.solclient.client.mod.impl;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.util.extension.LivingEntityExtension;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.BlockHitResult.Type;
import net.minecraft.util.math.MathHelper;

public class V1_7VisualsMod extends Mod {

	@Expose
	@Option
	public boolean useAndMine = true;
	@Expose
	@Option
	private boolean particles = true;
	@Expose
	@Option
	public boolean blocking = true;
	@Expose
	@Option
	public boolean eatingAndDrinking = true;
	@Expose
	@Option
	private boolean bow = true;
	@Expose
	@Option
	private boolean rod = true;
	@Expose
	@Option
	public boolean armourDamage = true;
	@Expose
	@Option
	public boolean sneaking = true;

	public static V1_7VisualsMod instance;
	public static boolean enabled;

	@Override
	public String getId() {
		return "1.7_visuals";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public void onRegister() {
		super.onRegister();
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (mc.player != null && mc.player.abilities.allowModifyWorld && isEnabled() && useAndMine
				&& mc.result  != null
				&& mc.result.type == BlockHitResult.Type.BLOCK && mc.player != null
				&& mc.options.attackKey.isPressed() && mc.options.useKey.isPressed()
				&& mc.player.getItemUseTicks() > 0) {
			if ((!mc.player.handSwinging
					|| mc.player.handSwingTicks >= ((LivingEntityExtension) mc.player)
							.privateGetArmSwingAnimationEnd() / 2
					|| mc.player.handSwingTicks < 0)) {
				mc.player.handSwingTicks = -1;
				mc.player.handSwinging = true;
			}

			if (particles) {
				mc.particleManager.addBlockBreakingParticles(mc.result.getBlockPos(), mc.result.direction);
			}
		}
	}

	@EventHandler
	public void onItemTransform(TransformFirstPersonItemEvent event) {
		if (!(bow || rod)) {
			return;
		}

		// https://github.com/sp614x/optifine/issues/2098
		if (mc.player.isUsingItem() && event.itemToRender.getItem() instanceof BowItem) {
			if (bow)
				GlStateManager.translate(-0.01f, 0.05f, -0.06f);
		} else if (event.itemToRender.getItem() instanceof FishingRodItem) {
			if (rod) {
				GlStateManager.translate(0.08f, -0.027f, -0.33f);
				GlStateManager.scale(0.93f, 1.0f, 1.0f);
			}
		}
	}

	public static void oldDrinking(ItemStack itemToRender, AbstractClientPlayerEntity clientPlayer, float partialTicks) {
		float var14 = clientPlayer.getItemUseTicks() - partialTicks + 1.0F;
		float var15 = 1.0F - var14 / itemToRender.getMaxUseTime();
		float var16 = 1.0F - var15;
		var16 = var16 * var16 * var16;
		var16 = var16 * var16 * var16;
		var16 = var16 * var16 * var16;
		var16 -= 0.05F;
		float var17 = 1.0F - var16;
		GlStateManager.translate(0.0F, MathHelper.abs(MathHelper.cos(var14 / 4F * (float) Math.PI) * 0.11F)
				* (float) ((double) var15 > 0.2D ? 1 : 0), 0.0F);
		GlStateManager.translate(var17 * 0.6F, -var17 * 0.5F, 0.0F);
		GlStateManager.rotate(var17 * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(var17 * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(var17 * 30.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0, -0.0F, 0.06F);
		GlStateManager.rotate(-4F, 1, 0, 0);
	}

	public static void oldBlocking() {
		GlStateManager.scale(0.83F, 0.88F, 0.85F);
		GlStateManager.translate(-0.3F, 0.1F, 0.0F);
	}

}
