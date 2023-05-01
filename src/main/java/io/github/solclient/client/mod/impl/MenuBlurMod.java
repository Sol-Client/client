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

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.core.mixins.client.ShaderEffectAccessor;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.gl.*;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

public class MenuBlurMod extends StandardMod {

	@Expose
	@Option
	@Slider(min = 0, max = 100, step = 1)
	private float blur = 8;
	@Expose
	@Option
	@Slider(min = 0, max = 1, step = 0.1F, format = "sol_client.slider.seconds")
	private float fadeTime = 0.1F;
	@Expose
	@Option
	private Colour backgroundColour = new Colour(0, 0, 0, 100);
	private ShaderEffect effect;
	private long openTime;

	@Override
	public String getDetail() {
		return I18n.translate("sol_client.mod.screen.originally_by", "tterrag1098");
	}

	@EventHandler
	public void onOpenGui(InitialOpenGuiEvent event) {
		openTime = System.currentTimeMillis();
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		if (event.type == PostProcessingEvent.Type.UPDATE
				|| (blur != 0 && (mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)
				// includes replay clicking screen
						&& !(mc.currentScreen.getClass().getName().startsWith(
								"com.replaymod.lib.de.johni0702.MinecraftClient.gui.container.AbstractGuiOverlay$"))))) {
			update();
			event.effects.add(effect);
		}
	}

	@EventHandler
	public void onRenderGuiBackground(RenderGuiBackgroundEvent event) {
		event.cancelled = true;
		Window window = new Window(mc);

		int colour = MinecraftUtils.lerpColour(0, backgroundColour.getValue(), getProgress());
		if (colour == 0) {
			// nothing to see, just apply side-effects of method
			// yes Minecraft code is bad :p
			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
		} else
			DrawableHelper.fill(0, 0, window.getWidth(), window.getHeight(), colour);
	}

	public void update() {
		if (effect == null) {
			try {
				effect = new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(),
						new Identifier("minecraft:shaders/effect/menu_blur.json"));
				effect.setupDimensions(mc.width, mc.height);
			} catch (JsonSyntaxException | IOException error) {
				logger.error("Could not load menu blur", error);
			}
		}

		((ShaderEffectAccessor) effect).getPasses().forEach(shader -> {
			GlUniform radius = shader.getProgram().getUniformByName("radius");
			GlUniform multiplier = shader.getProgram().getUniformByName("radiusMultiplier");

			if (radius != null)
				radius.set(blur);

			if (multiplier != null) {
				if (fadeTime > 0)
					multiplier.set(getProgress());
				else
					multiplier.set(1);
			}
		});
	}

	public float getProgress() {
		return Math.min((System.currentTimeMillis() - openTime) / (fadeTime * 1000F), 1);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		effect = null;
	}

}
