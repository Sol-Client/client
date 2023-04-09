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
import com.replaymod.replay.ReplayModReplay;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mixin.client.ShaderEffectAccessor;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.gl.*;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Window;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class MenuBlurMod extends SolClientMod {

	private static final Identifier ID = new Identifier("minecraft:shaders/post/menu_blur.json");

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
	public String getId() {
		return "menu_blur";
	}

	@Override
	public String getDetail() {
		return I18n.translate("sol_client.mod.screen.originally_by", "tterrag1098");
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public void init() {
		super.init();
		Client.INSTANCE.getPseudoResources().register(ID, new MenuBlurShader());
	}

	@EventHandler
	public void onOpenGui(InitialOpenGuiEvent event) {
		openTime = System.currentTimeMillis();
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		if (event.type == PostProcessingEvent.Type.UPDATE || (blur != 0 && (mc.currentScreen != null
				&& !(mc.currentScreen instanceof ChatScreen)
				&& !(mc.currentScreen.getClass().getName().startsWith(
						"com.replaymod.lib.de.johni0702.MinecraftClient.gui" + ".container." + "AbstractGuiOverlay$")
						&& ReplayModReplay.instance.getReplayHandler() != null && mc.world != null)))) {
			update();
			event.effects.add(effect);
		}
	}

	@EventHandler
	public void onRenderGuiBackground(RenderGuiBackgroundEvent event) {
		event.cancelled = true;
		Window window = new Window(mc);
		DrawableHelper.fill(0, 0, window.getWidth(), window.getHeight(),
				MinecraftUtils.lerpColour(0, backgroundColour.getValue(), getProgress()));
	}

	public void update() {
		if (effect == null) {
			try {
				effect = new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), ID);
				effect.setupDimensions(this.mc.width, this.mc.height);
			} catch (JsonSyntaxException | IOException error) {
				logger.error("Could not load menu blur", error);
			}
		}

		((ShaderEffectAccessor) effect).getPasses().forEach((shader) -> {
			GlUniform radius = shader.getProgram().getUniformByName("Radius");
			GlUniform progress = shader.getProgram().getUniformByName("Progress");

			if (radius != null) {
				radius.set(blur);
			}

			if (progress != null) {
				if (fadeTime > 0) {
					progress.set(getProgress());
				} else {
					progress.set(1);
				}
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

	public class MenuBlurShader implements Resource {

		@Override
		public Identifier getId() {
			return null;
		}

		@Override
		public InputStream getInputStream() {
			return IOUtils.toInputStream("{\n" + "    \"targets\": [\n" + "        \"swap\"\n" + "    ],\n"
					+ "    \"passes\": [\n" + "        {\n" + "            \"name\": \"menu_blur\",\n"
					+ "            \"intarget\": \"minecraft:main\",\n" + "            \"outtarget\": \"swap\",\n"
					+ "            \"uniforms\": [\n" + "                {\n"
					+ "                    \"name\": \"BlurDir\",\n" + "                    \"values\": [ 1.0, 0.0 ]\n"
					+ "                },\n" + "                {\n" + "                    \"name\": \"Radius\",\n"
					+ "                    \"values\": [ 0.0 ]\n" + "                }\n" + "            ]\n"
					+ "        },\n" + "        {\n" + "            \"name\": \"menu_blur\",\n"
					+ "            \"intarget\": \"swap\",\n" + "            \"outtarget\": \"minecraft:main\",\n"
					+ "            \"uniforms\": [\n" + "                {\n"
					+ "                    \"name\": \"BlurDir\",\n" + "                    \"values\": [ 0.0, 1.0 ]\n"
					+ "                },\n" + "                {\n" + "                    \"name\": \"Radius\",\n"
					+ "                    \"values\": [ 0.0 ]\n" + "                }\n" + "            ]\n"
					+ "        },\n" + "        {\n" + "            \"name\": \"menu_blur\",\n"
					+ "            \"intarget\": \"minecraft:main\",\n" + "            \"outtarget\": \"swap\",\n"
					+ "            \"uniforms\": [\n" + "                {\n"
					+ "                    \"name\": \"BlurDir\",\n" + "                    \"values\": [ 1.0, 0.0 ]\n"
					+ "                },\n" + "                {\n" + "                    \"name\": \"Radius\",\n"
					+ "                    \"values\": [ 0.0 ]\n" + "                }\n" + "            ]\n"
					+ "        },\n" + "        {\n" + "            \"name\": \"menu_blur\",\n"
					+ "            \"intarget\": \"swap\",\n" + "            \"outtarget\": \"minecraft:main\",\n"
					+ "            \"uniforms\": [\n" + "                {\n"
					+ "                    \"name\": \"BlurDir\",\n" + "                    \"values\": [ 0.0, 1.0 ]\n"
					+ "                },\n" + "                {\n" + "                    \"name\": \"Radius\",\n"
					+ "                    \"values\": [ 0.0 ]\n" + "                }\n" + "            ]\n"
					+ "        }\n" + "    ]\n" + "}");
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
