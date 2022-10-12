/*
 * Original mod by tterrag1098.
 */

package io.github.solclient.client.mod.impl;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.screen.*;
import io.github.solclient.client.event.impl.shader.PostProcessingEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.platform.mc.*;
import io.github.solclient.client.platform.mc.screen.ChatScreen;
import io.github.solclient.client.platform.mc.shader.*;
import io.github.solclient.client.todo.TODO;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;

public class MenuBlurMod extends Mod implements PrimaryIntegerSettingMod {

	public static final MenuBlurMod INSTANCE = new MenuBlurMod();

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

	private ShaderChain chain;
	private long openTime;

	@Override
	public String getId() {
		return "menu_blur";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@EventHandler
	public void onScreenOpen(ScreenSwitchEvent event) {
		if(event.getPreviousScreen() != null) {
			return;
		}

		openTime = System.currentTimeMillis();
	}

	@EventHandler
	public void onPostProcessing(PostProcessingEvent event) {
		if(event.getType() == PostProcessingEvent.Type.UPDATE
				|| (blur != 0 && (mc.isInMenu() && !(mc.getScreen() instanceof ChatScreen)
						&& !(mc.getScreen().getClass().getName().startsWith(
								"com.replaymod.lib.de.johni0702.minecraft.gui" + ".container." + "AbstractGuiOverlay$")
								&& TODO.L /* TODO replaymod */ != null && mc.hasLevel())))) {
			update();
			event.getShaders().add(chain);
		}
	}

	@EventHandler
	public void customScreenBackground(ScreenBackgroundRenderEvent event) {
		event.cancel();
		DrawableHelper.fillRect(0, 0, mc.getWindow().scaledWidth(), mc.getWindow().scaledHeight(),
				Utils.lerpColour(0, backgroundColour.getValue(), getProgress()));
	}

	private void update() {
		if(chain == null) {
			try {
				chain = ShaderChain.create("{\n" + "    \"targets\": [\n" + "        \"swap\"\n" + "    ],\n"
						+ "    \"passes\": [\n" + "        {\n" + "            \"name\": \"menu_blur\",\n"
						+ "            \"intarget\": \"minecraft:main\",\n" + "            \"outtarget\": \"swap\",\n"
						+ "            \"uniforms\": [\n" + "                {\n"
						+ "                    \"name\": \"BlurDir\",\n"
						+ "                    \"values\": [ 1.0, 0.0 ]\n" + "                },\n"
						+ "                {\n" + "                    \"name\": \"Radius\",\n"
						+ "                    \"values\": [ 0.0 ]\n" + "                }\n" + "            ]\n"
						+ "        },\n" + "        {\n" + "            \"name\": \"menu_blur\",\n"
						+ "            \"intarget\": \"swap\",\n" + "            \"outtarget\": \"minecraft:main\",\n"
						+ "            \"uniforms\": [\n" + "                {\n"
						+ "                    \"name\": \"BlurDir\",\n"
						+ "                    \"values\": [ 0.0, 1.0 ]\n" + "                },\n"
						+ "                {\n" + "                    \"name\": \"Radius\",\n"
						+ "                    \"values\": [ 0.0 ]\n" + "                }\n" + "            ]\n"
						+ "        },\n" + "        {\n" + "            \"name\": \"menu_blur\",\n"
						+ "            \"intarget\": \"minecraft:main\",\n" + "            \"outtarget\": \"swap\",\n"
						+ "            \"uniforms\": [\n" + "                {\n"
						+ "                    \"name\": \"BlurDir\",\n"
						+ "                    \"values\": [ 1.0, 0.0 ]\n" + "                },\n"
						+ "                {\n" + "                    \"name\": \"Radius\",\n"
						+ "                    \"values\": [ 0.0 ]\n" + "                }\n" + "            ]\n"
						+ "        },\n" + "        {\n" + "            \"name\": \"menu_blur\",\n"
						+ "            \"intarget\": \"swap\",\n" + "            \"outtarget\": \"minecraft:main\",\n"
						+ "            \"uniforms\": [\n" + "                {\n"
						+ "                    \"name\": \"BlurDir\",\n"
						+ "                    \"values\": [ 0.0, 1.0 ]\n" + "                },\n"
						+ "                {\n" + "                    \"name\": \"Radius\",\n"
						+ "                    \"values\": [ 0.0 ]\n" + "                }\n" + "            ]\n"
						+ "        }\n" + "    ]\n" + "}");
				chain.updateWindowSize(Window.displayWidth(), Window.displayHeight());
			}
			catch(JsonSyntaxException | IOException error) {
				logger.error("Could not load menu blur", error);
			}
		}

		chain.getShaders().forEach((shader) -> {
			ShaderUniform radius = shader.getUniform("Radius");
			ShaderUniform progress = shader.getUniform("Progress");

			if(radius != null) {
				radius.set(blur);
			}

			if(progress != null) {
				if(fadeTime > 0) {
					progress.set(getProgress());
				}
				else {
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
		chain = null;
	}

	@Override
	public void decrement() {
		blur = Math.max(0, blur - 1);
	}

	@Override
	public void increment() {
		blur = Math.min(100, blur + 1);
	}

}
