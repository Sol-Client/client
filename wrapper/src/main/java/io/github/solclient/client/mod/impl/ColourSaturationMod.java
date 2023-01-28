package io.github.solclient.client.mod.impl;

import java.io.*;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostProcessingEvent;
import io.github.solclient.client.extension.ShaderEffectExtension;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.option.annotation.*;
import net.minecraft.client.gl.*;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class ColourSaturationMod extends SolClientMod implements PrimaryIntegerSettingMod {

	private static final Identifier ID = new Identifier("minecraft:shaders/post/" + "color_convolve.json");

	@Expose
	@Option
	@Slider(min = 0, max = 2F, step = 0.1F)
	private float saturation = 1f;
	private ShaderEffect effect;
	private float groupSaturation;

	@Override
	public String getId() {
		return "colour_saturation";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@Override
	public void init() {
		super.init();
		Client.INSTANCE.getPseudoResources().register(ID, new SaturationShader());
	}

	public void update() {
		if (effect == null) {
			groupSaturation = saturation;
			try {
				effect = new ShaderEffect(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), ID);
				effect.setupDimensions(mc.width, mc.height);
			} catch (JsonSyntaxException | IOException error) {
				logger.error("Could not load saturation shader", error);
			}
		}

		if (groupSaturation != saturation) {
			((ShaderEffectExtension) effect).getPasses().forEach((shader) -> {
				GlUniform saturationUniform = shader.getProgram().getUniformByName("Saturation");
				if (saturationUniform != null)
					saturationUniform.set(saturation);
			});
			groupSaturation = saturation;
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

	@Override
	public void decrement() {
		saturation = Math.max(0, saturation - 0.1F);
	}

	@Override
	public void increment() {
		saturation = Math.min(2, saturation + 0.1F);
	}

	public class SaturationShader implements Resource {

		@Override
		public Identifier getId() {
			return null;
		}

		@Override
		public InputStream getInputStream() {
			return IOUtils.toInputStream(String.format("{" + "    \"targets\": [" + "        \"swap\","
					+ "        \"previous\"" + "    ]," + "    \"passes\": [" + "        {"
					+ "            \"name\": \"color_convolve\"," + "            \"intarget\": \"minecraft:main\","
					+ "            \"outtarget\": \"swap\"," + "            \"auxtargets\": [" + "                {"
					+ "                    \"name\": \"PrevSampler\"," + "                    \"id\": \"previous\""
					+ "                }" + "            ]," + "            \"uniforms\": [" + "                {"
					+ "                    \"name\": \"Saturation\"," + "                    \"values\": [ %s ]"
					+ "                }" + "            ]" + "        }," + "        {"
					+ "            \"name\": \"blit\"," + "            \"intarget\": \"swap\","
					+ "            \"outtarget\": \"previous\"" + "        }," + "        {"
					+ "            \"name\": \"blit\"," + "            \"intarget\": \"swap\","
					+ "            \"outtarget\": \"minecraft:main\"" + "        }" + "    ]" + "}", saturation,
					saturation, saturation));
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
