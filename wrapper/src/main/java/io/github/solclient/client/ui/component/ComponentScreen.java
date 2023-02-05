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

import org.apache.logging.log4j.LogManager;
import org.lwjgl.input.*;
import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.controller.ParentBoundsController;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

public class ComponentScreen extends Screen {

	@Getter
	protected Screen parentScreen;
	protected Component root;
	private Component rootWrapper;
	protected boolean background = true;
	private float mouseX, mouseY;

	public ComponentScreen(Component root) {
		this.parentScreen = MinecraftClient.getInstance().currentScreen;
		rootWrapper = new Component() {

			@Override
			public Rectangle getBounds() {
				return new Rectangle(0, 0, width, height);
			}

		};
		rootWrapper.setScreen(this);

		rootWrapper.add(root, new ParentBoundsController());
		this.root = root;
	}

	public Component getRoot() {
		return root;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
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
