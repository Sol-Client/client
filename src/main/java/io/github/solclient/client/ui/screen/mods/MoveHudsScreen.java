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

package io.github.solclient.client.ui.screen.mods;

import java.util.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.Client;
import io.github.solclient.client.extension.KeyBindingExtension;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.ui.screen.PanoramaBackgroundScreen;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.util.Window;

public class MoveHudsScreen extends PanoramaBackgroundScreen {

	public MoveHudsScreen() {
		super(new MoveHudsComponent());
	}

	@Override
	public void render(int relativeMouseX, int relativeMouseY, float tickDelta) {
		if (client.world == null) {
			if (SolClientConfig.instance.fancyMainMenu) {
				background = false;
				drawPanorama(relativeMouseX, relativeMouseY, tickDelta);
			} else
				background = true;

			for (HudElement hud : Client.INSTANCE.getMods().getHuds())
				hud.render(true);
		}

		super.render(relativeMouseX, relativeMouseY, tickDelta);
	}

	@Override
	protected void keyPressed(char character, int code) {
		if (code == 1 || (code == SolClientConfig.instance.editHudKey.getCode()
				&& KeyBindingExtension.from(SolClientConfig.instance.editHudKey).areModsPressed())) {
			Client.INSTANCE.save();
			client.setScreen(null);
			return;
		}

		super.keyPressed(character, code);
	}

	@Override
	public void removed() {
		super.removed();
		Keyboard.enableRepeatEvents(false);
	}

	public static class MoveHudsComponent extends Component {

		private Position selectStart;
		private Map<HudElement, Position> dragHudStart;
		private Position dragMouseStart;
		private final List<HudElement> selectedHuds = new ArrayList<>();

		public MoveHudsComponent() {
			add(ButtonComponent.done(() -> screen.close()).width(50),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() - 30,
									defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

		@Override
		public void render(ComponentRenderInfo info) {
			float lineWidth = 1F / new Window(mc).getScaleFactor();
			NanoVG.nvgStrokeWidth(nvg, lineWidth);

			Rectangle selectRect = null;

			if (selectStart != null) {
				selectedHuds.clear();
				selectRect = selectStart.rectangle((int) info.relativeMouseX() - selectStart.getX(),
						(int) info.relativeMouseY() - selectStart.getY());

				// update selection
				for (HudElement hud : Client.INSTANCE.getMods().getHuds()) {
					if (!hud.isVisible())
						continue;

					if (selectRect.intersects(hud.getMultipliedBounds()))
						selectedHuds.add(hud);
				}
			}

			for (HudElement hud : Client.INSTANCE.getMods().getHuds()) {
				if (!hud.isVisible())
					continue;

				Rectangle bounds = hud.getMultipliedBounds();
				if (bounds == null)
					continue;

				if (dragHudStart != null && dragHudStart.containsKey(hud)) {
					Position hudStart = dragHudStart.get(hud);
					// move it
					if (info.relativeMouseX() != dragMouseStart.getX()
							|| info.relativeMouseY() != dragMouseStart.getY())
						hud.setPosition(hudStart.offset((int) info.relativeMouseX() - dragMouseStart.getX(),
								(int) info.relativeMouseY() - dragMouseStart.getY()));
				}

				if (selectedHuds.contains(hud)) {
					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgFillColor(nvg, Theme.getCurrent().accent.withAlpha(50).nvg());
					NanoVG.nvgRect(nvg, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
					NanoVG.nvgFill(nvg);
				}

				NanoVG.nvgBeginPath(nvg);
				NanoVG.nvgRect(nvg, bounds.getX() + lineWidth / 2, bounds.getY() + lineWidth / 2,
						bounds.getWidth() - lineWidth, bounds.getHeight() - lineWidth);
				NanoVG.nvgStrokeColor(nvg, Theme.getCurrent().accent.nvg());
				NanoVG.nvgStroke(nvg);
			}

			if (selectRect != null) {
				NanoVG.nvgBeginPath(nvg);
				NanoVG.nvgFillColor(nvg, Theme.getCurrent().accent.withAlpha(100).nvg());
				NanoVG.nvgRect(nvg, selectRect.getX(), selectRect.getY(), selectRect.getWidth(),
						selectRect.getHeight());
				NanoVG.nvgFill(nvg);

				NanoVG.nvgBeginPath(nvg);
				NanoVG.nvgStrokeColor(nvg, Theme.getCurrent().accent.nvg());
				NanoVG.nvgRect(nvg, selectRect.getX() - lineWidth / 2, selectRect.getY() - lineWidth / 2,
						selectRect.getWidth(), selectRect.getHeight());
				NanoVG.nvgStroke(nvg);
			}

			if (dragHudStart != null && selectedHuds.size() == 1) {
				NanoVG.nvgStrokeColor(nvg, Theme.getCurrent().accent.nvg());

				HudElement hud = selectedHuds.get(0);
				Position targetPosition = hud.getPosition();

				int midX = screen.width / 2 - hud.getMultipliedBounds().getWidth() / 2;
				int midY = screen.height / 2 - hud.getMultipliedBounds().getHeight() / 2;

				NanoVG.nvgShapeAntiAlias(nvg, false);
				if (Math.abs(hud.getMultipliedBounds().getY() + hud.getMultipliedBounds().getHeight() / 2
						- screen.height / 2) <= 6) {
					// horizontal

					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgMoveTo(nvg, 0, screen.height / 2);
					NanoVG.nvgLineTo(nvg, screen.width, screen.height / 2);
					NanoVG.nvgStroke(nvg);

					targetPosition = new Position(targetPosition.getX(), midY);
				}

				if (Math.abs(hud.getMultipliedBounds().getX() + hud.getMultipliedBounds().getWidth() / 2
						- screen.width / 2) <= 6) {
					// vertical

					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgMoveTo(nvg, screen.width / 2, 0);
					NanoVG.nvgLineTo(nvg, screen.width / 2, screen.height);
					NanoVG.nvgStroke(nvg);

					targetPosition = new Position(midX, targetPosition.getY());
				}

				NanoVG.nvgShapeAntiAlias(nvg, true);

				hud.setPosition(targetPosition);
			}

			super.render(info);
		}

		private static Optional<HudElement> getHud(int x, int y) {
			for (HudElement hud : Client.INSTANCE.getMods().getHuds()) {
				if (!hud.isVisible())
					continue;

				Rectangle bounds = hud.getMultipliedBounds();
				if (bounds == null)
					continue;
				if (!bounds.contains(x, y))
					continue;

				return Optional.of(hud);
			}
			return Optional.empty();
		}

		@Override
		public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
			if (selectStart != null)
				selectStart = null;

			return super.mouseReleasedAnywhere(info, button, inside);
		}

		@Override
		public boolean mouseClicked(ComponentRenderInfo info, int button) {
			Optional<HudElement> hudOpt = getHud((int) info.relativeMouseX(), (int) info.relativeMouseY());

			if (button == 0) {
				if (hudOpt.isPresent()) {
					HudElement hud = hudOpt.get();

					if (selectedHuds.contains(hud)) {
						if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
							selectedHuds.remove(hud);
					} else {
						if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
							selectedHuds.clear();

						selectedHuds.add(hud);
					}

					dragHudStart = new HashMap<>();
					dragMouseStart = new Position((int) info.relativeMouseX(), (int) info.relativeMouseY());
					for (HudElement element : selectedHuds)
						dragHudStart.put(element, element.getPosition());
				} else if (selectStart == null) {
					selectedHuds.clear();
					dragHudStart = null;
					selectStart = new Position((int) info.relativeMouseX(), (int) info.relativeMouseY());
				}
			} else if (button == 1 && hudOpt.isPresent()) {
				((ModsScreen) screen.getParentScreen()).switchMod(hudOpt.get().getMod());
				screen.close();
			}

			// still allow buttons to be interacted with
			return super.mouseClicked(info, button);
		}

		@Override
		public boolean mouseReleased(ComponentRenderInfo info, int button) {
			dragHudStart = null;
			return super.mouseReleased(info, button);
		}

		private void shift(int x, int y) {
			selectedHuds.forEach((hud) -> hud.setPosition(hud.getPosition().offset(x, y)));
		}

		// yayy writing almost the same code that I did nearly two years ago!
		@Override
		public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
			if (keyCode == Keyboard.KEY_LEFT) {
				shift(-1, 0);
				return true;
			} else if (keyCode == Keyboard.KEY_RIGHT) {
				shift(1, 0);
				return true;
			} else if (keyCode == Keyboard.KEY_UP) {
				shift(0, -1);
				return true;
			} else if (keyCode == Keyboard.KEY_DOWN) {
				shift(0, 1);
				return true;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && keyCode == Keyboard.KEY_A) {
				selectedHuds.addAll(Client.INSTANCE.getMods().getHuds());
				return true;
			} else if (keyCode == Keyboard.KEY_0) {
				selectedHuds.forEach((hud) -> hud.setPosition(new Position(0, 0)));
				return true;
			}

			return super.keyPressed(info, keyCode, character);
		}

	}

}
