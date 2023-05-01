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

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostProcessingEvent;
import io.github.solclient.client.mod.impl.core.mixins.client.ShaderEffectAccessor;
import io.github.solclient.client.mod.option.annotation.*;
import net.minecraft.client.gl.*;
import net.minecraft.util.Identifier;

public class ColourSaturationMod extends StandardMod {

	@Expose
	@Option
	@Slider(min = 0, max = 2F, step = 0.1F)
	private float saturation = 1f;
	private ShaderEffect effect;
	private float realSaturation;

	public void update() {
		if (effect == null) {
			realSaturation = -1;
			try {
				effect = new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new Identifier("minecraft:shaders/post/color_convolve.json"));
				effect.setupDimensions(mc.width, mc.height);
			} catch (JsonSyntaxException | IOException error) {
				logger.error("Could not load saturation shader", error);
			}
		}

		if (realSaturation != saturation) {
			((ShaderEffectAccessor) effect).getPasses().forEach(shader -> {
				GlUniform saturationUniform = shader.getProgram().getUniformByName("Saturation");
				if (saturationUniform != null)
					saturationUniform.set(saturation);
			});
			realSaturation = saturation;
		}
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		update();
		event.effects.add(effect);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		effect = null;
	}

}
