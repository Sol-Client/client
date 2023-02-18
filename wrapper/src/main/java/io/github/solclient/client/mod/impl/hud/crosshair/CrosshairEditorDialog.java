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

package io.github.solclient.client.mod.impl.hud.crosshair;

import org.apache.logging.log4j.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

public final class CrosshairEditorDialog extends Component {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int SCALE = 11;

	private final PixelMatrix pixels;

	public CrosshairEditorDialog(CrosshairOption option) {
		pixels = option.getValue();

		add(new PixelMatrixComponent(), new AlignedBoundsController(Alignment.CENTRE, Alignment.START));

		add(new ButtonComponent("", theme.button(), theme.fg()).withIcon("copy").width(20).onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			copy();
			return true;
		}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
				(component, defaultBounds) -> defaultBounds.offset(-25, 0)));
		add(new ButtonComponent("", theme.button(), theme.fg()).withIcon("paste").width(20).onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			paste();
			return true;
		}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
				(component, defaultBounds) -> defaultBounds.offset(0, 0)));
		add(new ButtonComponent("", theme.button(), theme.fg()).withIcon("clear").width(20).onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			pixels.clear();
			return true;
		}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
				(component, defaultBounds) -> defaultBounds.offset(25, 0)));
	}

	private void copy() {
		try {
			Screen.setClipboard(LCCH.stringify(pixels));
		} catch (Throwable error) {
			LOGGER.error("Failed to convert to LCCH", error);
		}
	}

	private void paste() {
		try {
			LCCH.parse(Screen.getClipboard(), pixels);
		} catch (Throwable error) {
			LOGGER.error("Failed to load from LCCH", error);
		}
	}

	private final class PixelMatrixComponent extends Component {

		// prevent instant input
		private boolean leftMouseDown;
		private boolean rightMouseDown;
		private int lastGridX = -1, lastGridY = -1;

		@Override
		public void render(ComponentRenderInfo info) {
			for (int y = 0; y < pixels.getHeight(); y++) {
				for (int x = 0; x < pixels.getWidth(); x++) {
					Colour colour;

					if (pixels.get(x, y))
						colour = Colour.WHITE;
					else {
						boolean square = x % 2 == 0;
						if (y % 2 == 0)
							square = !square;
						colour = square ? theme.transparent1 : theme.transparent2;
					}

					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgFillColor(nvg, colour.nvg());
					NanoVG.nvgRect(nvg, x * SCALE, y * SCALE, SCALE, SCALE);
					NanoVG.nvgFill(nvg);

					if (info.relativeMouseX() >= x * SCALE && info.relativeMouseX() < x * SCALE + SCALE
							&& info.relativeMouseY() >= y * SCALE && info.relativeMouseY() < y * SCALE + SCALE) {
						if (x != lastGridX || y != lastGridY) {
							if (leftMouseDown)
								pixels.set(x, y);
							else if (rightMouseDown)
								pixels.clear(x, y);
						}

						NanoVG.nvgBeginPath(nvg);
						// single pixel
						float strokeWidth = 1F / new Window(mc).getScaleFactor();
						NanoVG.nvgStrokeColor(nvg, pixels.get(x, y) ? Colour.BLACK.nvg() : Colour.WHITE.nvg());
						NanoVG.nvgStrokeWidth(nvg, strokeWidth);
						NanoVG.nvgRect(nvg, x * SCALE + strokeWidth / 2, y * SCALE + strokeWidth / 2,
								SCALE - strokeWidth, SCALE - strokeWidth);
						NanoVG.nvgStroke(nvg);

						lastGridX = x;
						lastGridY = y;
					}

					// draw centre marker
					if (x == pixels.getWidth() / 2 && y == pixels.getHeight() / 2) {
						NanoVG.nvgBeginPath(nvg);
						NanoVG.nvgFillColor(nvg, pixels.get(x, y) ? Colour.BLACK.nvg() : Colour.WHITE.nvg());
						NanoVG.nvgCircle(nvg, x * SCALE + SCALE / 2F, y * SCALE + SCALE / 2F, 2);
						NanoVG.nvgFill(nvg);
					}
				}
			}

			super.render(info);
		}

		@Override
		public boolean mouseClicked(ComponentRenderInfo info, int button) {
			if (button == 0)
				leftMouseDown = true;
			if (button == 1)
				rightMouseDown = true;
			lastGridX = -1;
			lastGridY = -1;
			return true;
		}

		@Override
		public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
			if (button == 0)
				leftMouseDown = false;
			if (button == 1)
				rightMouseDown = false;
			return super.mouseReleasedAnywhere(info, button, inside);
		}

		@Override
		public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
			if (keyCode == Keyboard.KEY_DELETE) {
				pixels.clear();
				return true;
			} else if (keyCode == Keyboard.KEY_C) {
				copy();
				return true;
			} else if (keyCode == Keyboard.KEY_V) {
				paste();
				return true;
			}

			return super.keyPressed(info, keyCode, character);
		}

		@Override
		protected Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(pixels.getWidth() * SCALE, pixels.getHeight() * SCALE);
		}

	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, 190);
	}

}
