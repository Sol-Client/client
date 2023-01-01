/*
 * Original mod by tterrag1098.
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
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.access.AccessShaderGroup;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.shader.*;
import net.minecraft.util.ResourceLocation;

public class MenuBlurMod extends Mod implements PrimaryIntegerSettingMod {

	private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(
			"minecraft:shaders/post/menu_blur.json");

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
	private ShaderGroup group;
	private long openTime;

	@Override
	public String getId() {
		return "menu_blur";
	}

	@Override
	public String getCredit() {
		return I18n.format("sol_client.originally_by", "tterrag1098");
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public void onRegister() {
		super.onRegister();
		Client.INSTANCE.addResource(RESOURCE_LOCATION, new MenuBlurShader());
	}

	@EventHandler
	public void onOpenGui(InitialOpenGuiEvent event) {
		openTime = System.currentTimeMillis();
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		if (event.type == PostProcessingEvent.Type.UPDATE
				|| (blur != 0 && (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)
						&& !(mc.currentScreen.getClass().getName().startsWith(
								"com.replaymod.lib.de.johni0702.minecraft.gui" + ".container." + "AbstractGuiOverlay$")
								&& ReplayModReplay.instance.getReplayHandler() != null && mc.theWorld != null)))) {
			update();
			event.groups.add(group);
		}
	}

	@EventHandler
	public void onRenderGuiBackground(RenderGuiBackgroundEvent event) {
		event.cancelled = true;
		ScaledResolution resolution = new ScaledResolution(mc);
		Gui.drawRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(),
				Utils.lerpColour(0, backgroundColour.getValue(), getProgress()));
	}

	public void update() {
		if (group == null) {
			try {
				group = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(),
						RESOURCE_LOCATION);
				group.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
			} catch (JsonSyntaxException | IOException error) {
				logger.error("Could not load menu blur", error);
			}
		}

		((AccessShaderGroup) group).getListShaders().forEach((shader) -> {
			ShaderUniform radius = shader.getShaderManager().getShaderUniform("Radius");
			ShaderUniform progress = shader.getShaderManager().getShaderUniform("Progress");

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
		group = null;
	}

	@Override
	public void decrement() {
		blur = Math.max(0, blur - 1);
	}

	@Override
	public void increment() {
		blur = Math.min(100, blur + 1);
	}

	public class MenuBlurShader implements IResource {

		@Override
		public ResourceLocation getResourceLocation() {
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
		public <T extends IMetadataSection> T getMetadata(String p_110526_1_) {
			return null;
		}

		@Override
		public String getResourcePackName() {
			return null;
		}

	}

}
