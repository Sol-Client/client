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

package io.github.solclient.client.ui.component;

import io.github.solclient.client.mod.impl.visibleseasons.Snowflake;
import io.github.solclient.client.mod.impl.visibleseasons.VisibleSeasonsMod;
import io.github.solclient.client.ui.component.controller.ParentBoundsController;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.NanoVGManager;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import java.time.LocalDate;
import java.time.Month;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class ComponentScreen extends Screen {

	private static final Logger LOGGER = LogManager.getLogger();
	@Getter
	protected Screen parentScreen;
	protected Component root;
	private Component rootWrapper;
	protected boolean background = true;
	private float mouseX, mouseY;

	private long lastFrameTime = 0;
	private final long nvg;

	public ComponentScreen(Component root) {
		this.parentScreen = MinecraftClient.getInstance().currentScreen;
		rootWrapper = new Component() {

			@Override
			public Rectangle getBounds() {
				return new Rectangle(0, 0, width, height);
			}

		};
		rootWrapper.setScreen(this);

		nvg = NanoVGManager.getNvg();

		rootWrapper.add(root, new ParentBoundsController());
		this.root = root;
	}

	public Component getRoot() {
		return root;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (VisibleSeasonsMod.instance.forceVisibleSeasons || LocalDate.now().getMonth() == Month.DECEMBER) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastFrameTime >= ThreadLocalRandom.current().nextInt(400)) {
				if (VisibleSeasonsMod.instance.snowflakes.size() < (int) VisibleSeasonsMod.instance.visibleSeasonsAmount) {
					int snowflakeX = ThreadLocalRandom.current().nextInt(client.width);
					int snowflakeSize = 5 + ThreadLocalRandom.current().nextInt(6);
					int snowflakeSpeed = 1 + ThreadLocalRandom.current().nextInt(3);
					VisibleSeasonsMod.instance.snowflakes.add(new Snowflake(snowflakeX, 0, snowflakeSize, snowflakeSpeed));
					lastFrameTime = currentTime;
				}
			}


			Iterator<Snowflake> iterator = VisibleSeasonsMod.instance.snowflakes.iterator();
			while (iterator.hasNext()) {
				Snowflake snowflake = iterator.next();
				int x = snowflake.getX();
				int y = snowflake.getY();
				int size = snowflake.getSize();

				NanoVG.nvgBeginPath(nvg);
				if (VisibleSeasonsMod.instance.visibleSeasonsLowDetail) {
					NanoVG.nvgRect(nvg, x, y, size, size);
					NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
					NanoVG.nvgFill(nvg);
				} else {
					NVGPaint paint = MinecraftUtils.nvgMinecraftTexturePaint(nvg, new Identifier("sol_client", "textures/gui/snowflake.png"), x, y, size, size, 0);
					NanoVG.nvgFillPaint(nvg, paint);
					NanoVG.nvgFill(nvg);
				}

				// Reset the snowflake if it goes beyond the screen bounds
				if (y > client.height) {
					iterator.remove();
				} else {
					snowflake.setY(y + snowflake.getSpeed());
				}
			}
		}

		try {
			Window window = new Window(client);

			if (client.currentScreen == this) {
				this.mouseX = Mouse.getX() / (float) window.getScaleFactor();
				this.mouseY = window.getHeight() - Mouse.getY() / (float) window.getScaleFactor();
			} else
				this.mouseX = this.mouseY = 0;

			if (background) {
				if (client.world == null) {
					fill(0, 0, width, height, Colour.BACKGROUND.getValue());
				} else {
					renderBackground();
				}
			}

			wrap(() -> rootWrapper.render(new ComponentRenderInfo(this.mouseX, this.mouseY, tickDelta)));

			super.render(mouseX, mouseY, tickDelta);
		} catch (Throwable error) {
			LogManager.getLogger().error("Error rendering " + this, error);
			client.setScreen(null);
		}
		try {
			Component.applyCursor();
		} catch (LWJGLException error) {
			LOGGER.warn("Couldn't set cursor", error);
		}

	}

	@Override
	public void removed() {
		try {
			Mouse.setNativeCursor(null);
		} catch (LWJGLException error) {
			LOGGER.warn("Couldn't set cursor", error);
		}
	}

	protected void wrap(Runnable task) {
		MinecraftUtils.withNvg(task, true);
	}

	@Override
	public void handleKeyboard() {
		if (!Keyboard.getEventKeyState())
			rootWrapper.keyReleased(getInfo(), Keyboard.getEventKey(), Keyboard.getEventCharacter());

		super.handleKeyboard();
	}

	@Override
	public void handleMouse() {
		super.handleMouse();

		if (Mouse.getEventDWheel() != 0) {
			rootWrapper.mouseScroll(getInfo(), Mouse.getEventDWheel());
		}
	}

	@Override
	protected void keyPressed(char character, int code) {
		if (!rootWrapper.keyPressed(getInfo(), code, character)) {
			if (code == Keyboard.KEY_ESCAPE) {
				Component withDialog = rootWrapper;

				if (withDialog.getDialog() == null) {
					if (!withDialog.getSubComponents().isEmpty()) {
						withDialog = withDialog.getSubComponents().get(0);
					} else {
						withDialog = null;
					}
				}

				if (withDialog != null && withDialog.getDialog() != null) {
					withDialog.setDialog(null);
					return;
				}

				closeAll();
			} else {
				super.keyPressed(character, code);
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		if (!rootWrapper.mouseClickedAnywhere(getInfo(), button, true, false))
			super.mouseClicked(x, y, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (!rootWrapper.mouseReleasedAnywhere(getInfo(), state, true))
			super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void tick() {
		super.tick();
		rootWrapper.tick();
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	public void close() {
		client.setScreen(parentScreen);
	}

	public void closeAll() {
		client.setScreen(null);
	}

	private ComponentRenderInfo getInfo() {
		return new ComponentRenderInfo(mouseX, mouseY, MinecraftUtils.getTickDelta());
	}

}
