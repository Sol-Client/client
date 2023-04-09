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
import io.github.solclient.client.mixin.client.ShaderEffectAccessor;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.option.annotation.*;
import net.minecraft.client.gl.*;
import net.minecraft.util.Identifier;

public class MotionBlurMod extends SolClientMod {

	@Expose
	@Option
	@Slider(min = 0, max = 0.99F, step = 0.01F)
	private float blur = 0.5F;
	private float realBlur;
	private ShaderEffect effect;

	@Override
	public String getId() {
		return "motion_blur";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	public void update() {
		if (effect == null) {
			realBlur = -1;
			try {
				effect = new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(),
						new Identifier("minecraft:shaders/effect/motion_blur.json"));
				effect.setupDimensions(mc.width, mc.height);
			} catch (JsonSyntaxException | IOException error) {
				logger.error("Could not load motion blur", error);
			}
		}

		if (realBlur != blur) {
			((ShaderEffectAccessor) effect).getPasses().forEach(pass -> {
				GlUniform percent = pass.getProgram().getUniformByName("percent");
				if (percent != null)
					percent.set(blur);
			});
			realBlur = blur;
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
