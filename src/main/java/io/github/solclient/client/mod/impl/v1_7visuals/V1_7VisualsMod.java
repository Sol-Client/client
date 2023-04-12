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

package io.github.solclient.client.mod.impl.v1_7visuals;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.event.impl.TransformFirstPersonItemEvent;
import io.github.solclient.client.mixin.client.LivingEntityAccessor;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.mod.option.annotation.Option;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.BowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;

// credit to OrangeMarshall for original mod.
// also credit to https://github.com/TAKfsg/oldblockhit-legacy-fabric.
public class V1_7VisualsMod extends SolClientMod {

	@Expose
	@Option
	public boolean useAndMine = true;
	@Expose
	@Option
	private final boolean bow = true;
	@Expose
	@Option
	private final boolean rod = true;
	@Expose
	@Option
	private final boolean particles = true;
	@Expose
	@Option
	public boolean blocking = true;
	@Expose
	@Option
	public boolean eatingAndDrinking = true;

	@Expose
	@Option
	public boolean armourDamage = true;
	@Expose
	@Option
	public boolean sneaking = true;
	@Expose
	@Option
	public boolean debug = true;

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
	public void init() {
		super.init();
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
		if (mc.player != null && isEnabled() && useAndMine && mc.player.abilities.allowModifyWorld &&
				mc.result != null && mc.result.type == BlockHitResult.Type.BLOCK && mc.options.attackKey.isPressed() &&
				mc.options.useKey.isPressed() && mc.player.getItemUseTicks() > 0) {
			if ((!mc.player.handSwinging || mc.player.handSwingTicks >= ((LivingEntityAccessor) mc.player).getArmSwingAnimationEnd() / 2
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
		// https://github.com/sp614x/optifine/issues/2098
		if (mc.player.isUsingItem() && event.itemToRender.getItem() instanceof BowItem) {
			if (bow)
				GlStateManager.translate(-0.01f, 0.05f, -0.06f);
		} else if ((event.itemToRender.getItem() instanceof FishingRodItem) && rod) {
			GlStateManager.translate(0.08f, -0.027f, -0.33f);
			GlStateManager.scale(0.93f, 1.0f, 1.0f);
		} else if (event.itemToRender.getItem() != null) {
			GlStateManager.rotate(-1, 0.0f, 1.0f, 0.0f);
			GlStateManager.translate(-0.027, 0.002, 0.0005);
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
				* (var15 > 0.2D ? 1 : 0), 0.0F);
		GlStateManager.translate(var17 * 0.6F, -var17 * 0.5F, 0.0F);
		GlStateManager.rotate(var17 * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(var17 * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(var17 * 30.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0, -0.0F, 0.06F);
		GlStateManager.rotate(-4F, 1, 0, 0);
	}
}
