package io.github.solclient.client.ui.screen.mods;

import org.apache.logging.log4j.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.nanovg.*;

import io.github.solclient.client.mod.ModOption;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.util.LCCH;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

public final class PixelMatrixDialog extends BlockComponent {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final int SCALE = 12;
	private static final Colour COLOUR_1 = new Colour(40, 40, 40);
	private static final Colour COLOUR_2 = new Colour(60, 60, 60);

	private final PixelMatrix pixels;

	public PixelMatrixDialog(ModOption option) {
		super(theme.bg, 12, 0);

		pixels = (PixelMatrix) option.getValue();

		LabelComponent title = new LabelComponent(option.getName());
		add(title, new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
				(component, defaultBounds) -> defaultBounds.offset(0, 10)));
		add(new PixelMatrixComponent(), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
				(component, defaultBounds) -> defaultBounds.offset(0, -5)));
		add(ButtonComponent.done(() -> parent.setDialog(null)), new AlignedBoundsController(Alignment.CENTRE,
				Alignment.END, (component, defaultBounds) -> defaultBounds.offset(0, -8)));

		add(new ButtonComponent("", theme.button(), theme.fg()).withIcon("copy").width(20).onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			copy();
			return true;
		}), new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
				(component, defaultBounds) -> defaultBounds.offset(10, -25)));
		add(new ButtonComponent("", theme.button(), theme.fg()).withIcon("paste").width(20).onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			paste();
			return true;
		}), new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
				(component, defaultBounds) -> defaultBounds.offset(10, 0)));
		add(new ButtonComponent("", theme.button(), theme.fg()).withIcon("clear").width(20).onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			pixels.clear();
			return true;
		}), new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
				(component, defaultBounds) -> defaultBounds.offset(10, 25)));
	}

	private void copy() {
		try {
			Screen.setClipboard(LCCH.stringify(pixels));
		} catch (IllegalArgumentException error) {
			LOGGER.error("Failed to convert to LCCH", error);
		}
	}

	private void paste() {
		try {
			LCCH.parse(Screen.getClipboard(), pixels);
		} catch (IllegalArgumentException error) {
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
						if (x % 2 == 0)
							colour = COLOUR_1;
						else
							colour = COLOUR_2;

						if (y % 2 == 0) {
							if (colour == COLOUR_1)
								colour = COLOUR_2;
							else
								colour = COLOUR_1;
						}
					}

					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgFillColor(nvg, colour.nvg());
					NanoVG.nvgRect(nvg, x * SCALE, y * SCALE, SCALE, SCALE);
					NanoVG.nvgFill(nvg);

					if (info.getRelativeMouseX() >= x * SCALE && info.getRelativeMouseX() < x * SCALE + SCALE
							&& info.getRelativeMouseY() >= y * SCALE && info.getRelativeMouseY() < y * SCALE + SCALE) {
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
						NanoVG.nvgCircle(nvg, x * SCALE + SCALE / 2, y * SCALE + SCALE / 2, 2);
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
			return super.mouseClicked(info, button);
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
		return Rectangle.ofDimensions(260, 250);
	}

}