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

import java.io.*;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostProcessingEvent;
import io.github.solclient.client.mixin.client.ShaderEffectAccessor;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.*;
import net.minecraft.client.gl.*;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class MotionBlurMod extends SolClientMod {

	public static final Identifier ID = new Identifier("minecraft:shaders/post/motion_blur.json");

	@Expose
	@Option
	@Slider(min = 0, max = 0.99F, step = 0.01F)
	private float blur = 0.5f;
	private ShaderEffect effect;
	private float groupBlur;

	@Override
	public String getId() {
		return "motion_blur";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public void init() {
		super.init();
		Client.INSTANCE.getPseudoResources().register(ID, new MotionBlurShader());
	}

	public void update() {
		if (effect == null) {
			groupBlur = blur;
			try {
				effect = new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), ID);
				effect.setupDimensions(mc.width, mc.height);
			} catch (JsonSyntaxException | IOException error) {
				logger.error("Could not load motion blur", error);
			}
		}
		if (groupBlur != blur) {
			((ShaderEffectAccessor) effect).getPasses().forEach((pass) -> {
				GlUniform blendFactor = pass.getProgram().getUniformByName("BlendFactor");
				if (blendFactor != null) {
					blendFactor.set(blur);
				}
			});
			groupBlur = blur;
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

	public class MotionBlurShader implements Resource {

		@Override
		public Identifier getId() {
			return null;
		}

		@Override
		public InputStream getInputStream() {
			return IOUtils.toInputStream(String.format("{" + "    \"targets\": [" + "        \"swap\","
					+ "        \"previous\"" + "    ]," + "    \"passes\": [" + "        {"
					+ "            \"name\": \"motion_blur\"," + "            \"intarget\": \"minecraft:main\","
					+ "            \"outtarget\": \"swap\"," + "            \"auxtargets\": [" + "                {"
					+ "                    \"name\": \"PrevSampler\"," + "                    \"id\": \"previous\""
					+ "                }" + "            ]," + "            \"uniforms\": [" + "                {"
					+ "                    \"name\": \"BlendFactor\"," + "                    \"values\": [ %s ]"
					+ "                }" + "            ]" + "        }," + "        {"
					+ "            \"name\": \"blit\"," + "            \"intarget\": \"swap\","
					+ "            \"outtarget\": \"previous\"" + "        }," + "        {"
					+ "            \"name\": \"blit\"," + "            \"intarget\": \"swap\","
					+ "            \"outtarget\": \"minecraft:main\"" + "        }" + "    ]" + "}", blur, blur, blur));
		}

		@Override
		public boolean hasMetadata() {
			return false;
		}

		@Override
		public <T extends ResourceMetadataProvider> T getMetadata(String paramString) {
			return null;
		}

		@Override
		public String getResourcePackName() {
			return null;
		}

	}

}
