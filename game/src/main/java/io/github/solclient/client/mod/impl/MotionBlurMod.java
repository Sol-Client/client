package io.github.solclient.client.mod.impl;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import io.github.solclient.abstraction.mc.Window;
import io.github.solclient.abstraction.mc.shader.ShaderChain;
import io.github.solclient.abstraction.mc.shader.ShaderUniform;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.shader.PostProcessingEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.PrimaryIntegerSettingMod;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;

public class MotionBlurMod extends Mod implements PrimaryIntegerSettingMod {

	@Expose
	@Option
	@Slider(min = 0, max = 0.99F, step = 0.01F)
	private float blur = 0.5f;
	private ShaderChain chain;
	private float uniformBlur;

	@Override
	public String getId() {
		return "motion_blur";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	private void update() {
		if(chain == null) {
			uniformBlur = blur;
			try {
				chain = ShaderChain.create("{" +
					"    \"targets\": [" +
					"        \"swap\"," +
					"        \"previous\"" +
					"    ]," +
					"    \"passes\": [" +
					"        {" +
					"            \"name\": \"motion_blur\"," +
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
					"                    \"name\": \"BlendFactor\"," +
					"                    \"values\": [ 0 ]" +
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
				chain.updateWindowSize(Window.displayWidth(), Window.displayHeight());
			}
			catch(JsonSyntaxException | IOException error) {
				logger.error("Could not load motion blur", error);
			}
		}
		if(uniformBlur != blur) {
			chain.getShaders().forEach((shader) -> {
				ShaderUniform blendFactor = shader.getUniform("BlendFactor");
				if(blendFactor != null) {
					blendFactor.set(blur);
				}
			});
			uniformBlur = blur;
		}
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		update();
		event.getShaders().add(chain);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		chain = null;
	}

	@Override
	public void decrement() {
		blur = Math.max(0, blur - 0.1F);
	}

	@Override
	public void increment() {
		blur = Math.min(1, blur + 0.1F);
	}

}
