package io.github.solclient.client.mod.impl;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import io.github.solclient.abstraction.mc.Identifier;
import io.github.solclient.abstraction.mc.shader.ShaderChain;
import io.github.solclient.abstraction.mc.shader.ShaderUniform;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.shader.PostProcessingEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.PrimaryIntegerSettingMod;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;

public class ColourSaturationMod extends Mod implements PrimaryIntegerSettingMod {

	private static final Identifier RESOURCE_LOCATION = Identifier.solClient("shader/color_convolve.json");

	@Expose
	@Option
	@Slider(min = 0, max = 2F, step = 0.1F)
	private float saturation = 1f;
	private ShaderChain group;
	private float groupSaturation;

	@Override
	public String getId() {
		return "colour_saturation";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	public void update() {
		if(group == null) {
			groupSaturation = saturation;
			try {
				group = ShaderChain.create(RESOURCE_LOCATION, "{" +
						"    \"targets\": [" +
						"        \"swap\"," +
						"        \"previous\"" +
						"    ]," +
						"    \"passes\": [" +
						"        {" +
						"            \"name\": \"color_convolve\"," +
						"            \"intarget\": \"minecraft:main\"," +
						"            \"outtarget\": \"swap\"," +
						"            \"auxtargets\": [" +
						"                {" +
						"                    \"name\": \"PrevSampler\"," +
						"                    \"id\": \"previous\"" +
						"                }" +
						"            ]," +
						"            \"uniforms\": [" +
						"                {" +
						"                    \"name\": \"Saturation\"," +
						"                    \"values\": [ %s ]" +
						"                }" +
						"            ]" +
						"        }," +
						"        {" +
						"            \"name\": \"blit\"," +
						"            \"intarget\": \"swap\"," +
						"            \"outtarget\": \"previous\"" +
						"        }," +
						"        {" +
						"            \"name\": \"blit\"," +
						"            \"intarget\": \"swap\"," +
						"            \"outtarget\": \"minecraft:main\"" +
						"        }" +
						"    ]" +
						"}");
				group.updateWindowSize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
			}
			catch(JsonSyntaxException | IOException error) {
				logger.error("Could not load saturation shader", error);
			}
		}

		if(groupSaturation != saturation) {
			group.getShaders().forEach((shader) -> {
				ShaderUniform saturationUniform = shader.getShaderUniform("Saturation");
				if(saturationUniform != null) {
					saturationUniform.set(saturation);
				}
			});
			groupSaturation = saturation;
		}
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		update();
		event.getShaders().add(group);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		group = null;
	}

	@Override
	public void decrement() {
		saturation = Math.max(0, saturation - 0.1F);
	}

	@Override
	public void increment() {
		saturation = Math.min(2, saturation + 0.1F);
	}

}
