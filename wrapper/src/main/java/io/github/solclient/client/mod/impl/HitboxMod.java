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

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;

public class HitboxMod extends SolClientMod {

	@Option
	private final KeyBinding toggleHitboxes = new KeyBinding(getTranslationKey("option.toggleHitboxes"), 0,
			GlobalConstants.KEY_CATEGORY);
	@Expose
	@Option
	private boolean boundingBox = true;
	@Expose
	@Option
	private Colour boundingBoxColour = Colour.WHITE;
	@Expose
	@Option
	private boolean eyeHeight = true;
	@Expose
	@Option
	private Colour eyeHeightColour = Colour.PURE_RED;
	@Expose
	@Option
	private boolean lookVector = true;
	@Expose
	@Option
	private Colour lookVectorColour = Colour.PURE_BLUE;
	@Expose
	@Option
	@Slider(min = 1, max = 10, step = 0.5F)
	private float lineWidth = 2;
	@Expose
	private boolean toggled;

	@Override
	public void lateInit() {
		super.lateInit();
		if (isEnabled())
			mc.getEntityRenderManager().setRenderHitboxes(toggled);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		if (mc.getEntityRenderManager() != null)
			toggled = mc.getEntityRenderManager().getRenderHitboxes();

		while (toggleHitboxes.isPressed())
			;
	}

	@Override
	public String getId() {
		return "hitbox";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@EventHandler
	public void onHitboxRender(HitboxRenderEvent event) {
		event.cancelled = true;
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GL11.glLineWidth(lineWidth);

		float half = event.entity.width / 2.0F;

		if (boundingBox) {
			Box box = event.entity.getBoundingBox();
			Box offsetBox = new Box(box.minX - event.entity.x + event.x, box.minY - event.entity.y + event.y,
					box.minZ - event.entity.z + event.z, box.maxX - event.entity.x + event.x,
					box.maxY - event.entity.y + event.y, box.maxZ - event.entity.z + event.z);
			WorldRenderer.drawBox(offsetBox, boundingBoxColour.getRed(), boundingBoxColour.getGreen(),
					boundingBoxColour.getBlue(), boundingBoxColour.getAlpha());
		}

		if (eyeHeight && event.entity instanceof LivingEntity) {
			WorldRenderer.drawBox(
					new Box(event.x - half, event.y + event.entity.getEyeHeight() - 0.009999999776482582D,
							event.z - half, event.x + half,
							event.y + event.entity.getEyeHeight() + 0.009999999776482582D, event.z + half),
					eyeHeightColour.getRed(), eyeHeightColour.getGreen(), eyeHeightColour.getBlue(),
					eyeHeightColour.getAlpha());
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		if (lookVector) {
			Vec3d look = event.entity.getRotationVector(event.partialTicks);
			buffer.begin(3, VertexFormats.POSITION_COLOR);
			buffer.vertex(event.x, event.y + event.entity.getEyeHeight(), event.z).color(0, 0, 255, 255).next();
			buffer.vertex(event.x + look.x * 2, event.y + event.entity.getEyeHeight() + look.y * 2,
					event.z + look.z * 2)
					.color(lookVectorColour.getRed(), lookVectorColour.getGreen(), lookVectorColour.getBlue(),
							lookVectorColour.getAlpha())
					.next();
			tessellator.draw();
		}

		GlStateManager.enableTexture();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		MinecraftUtils.resetLineWidth();
	}

	@EventHandler
	public void onHitboxToggle(HitboxToggleEvent event) {
		toggled = event.state;
		Client.INSTANCE.save();
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		while (toggleHitboxes.isPressed()) {
			// If debug shortcut is used, don't conflict.
			if (toggleHitboxes.getCode() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
				continue;
			}

			toggled = !mc.getEntityRenderManager().getRenderHitboxes();
			mc.getEntityRenderManager().setRenderHitboxes(toggled);
			Client.INSTANCE.save();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
