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

import io.github.solclient.client.SolClient;
import io.github.solclient.client.mod.impl.VisibleSeasonsMod;
import io.github.solclient.client.mod.impl.core.CoreMod;
import io.github.solclient.client.ui.ScreenAnimation;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.BlockComponent;
import io.github.solclient.client.ui.component.impl.LabelComponent;
import io.github.solclient.client.ui.screen.PanoramaBackgroundScreen;
import io.github.solclient.client.util.ActiveMainMenu;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.input.Keyboard;
import org.lwjgl.nanovg.NanoVG;

import java.util.ArrayList;
import java.util.List;


public class SnakeScreen extends PanoramaBackgroundScreen {

	protected MinecraftClient mc = MinecraftClient.getInstance();
	private final ScreenAnimation animation = new ScreenAnimation();

	public SnakeScreen() {
		super(new Component() {
			{
				add(new SnakeComponent(), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
			}
		});

		background = false;
	}

	@Override
	public void init() {
		super.init();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (client.world == null) {
			if (CoreMod.instance.fancyMainMenu) {
				background = false;
				drawPanorama(mouseX, mouseY, tickDelta);
			} else
				background = true;
		}

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	protected void wrap(Runnable task) {
		animation.wrap(task);
	}

	@Override
	public void removed() {
		super.removed();
		animation.close();
		SolClient.INSTANCE.saveAll();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void closeAll() {
		if (client.world == null && CoreMod.instance.fancyMainMenu) {
			client.setScreen(ActiveMainMenu.getInstance());
			return;
		}

		super.closeAll();
	}

	public static class SnakeComponent extends BlockComponent {

		public static final int[] keys = {Keyboard.KEY_S, Keyboard.KEY_E, Keyboard.KEY_A, Keyboard.KEY_S, Keyboard.KEY_O, Keyboard.KEY_N, Keyboard.KEY_S};
		private int keyIndex = 0;

		private int foodX = 0;
		private int foodY = 0;

		private int snakeLength = 1;
		private final int snakeSize = 10;
		private final List<Integer> snakeX = new ArrayList<>();
		private final List<Integer> snakeY = new ArrayList<>();
		private int snakeDirection = 3;  // 0 = up, 1 = right, 2 = down, 3 = left

		private long lastFrameTime = 0;
		private final List<Integer> nextMoves = new ArrayList<>();

		private final int screenSize = 500;

		public SnakeComponent() {
			super(new Colour(0xFF202020));
			add(new LabelComponent((component, defaultText) -> String.valueOf(snakeLength - 3)).scaled(1.45F), new AlignedBoundsController(Alignment.CENTRE, Alignment.START));

			resetSnake();
			generateFood();
		}

		@Override
		public void render(ComponentRenderInfo info) {
			super.render(info);

			renderSnake();
			renderFood();

			if (isCollidingFood()) {
				generateFood();
				addFood();
			}

			long currentTime = System.currentTimeMillis();
			if (currentTime - lastFrameTime >= 50) {
				moveSnake();
				lastFrameTime = currentTime;
			}
		}

		private void addFood() {
			snakeLength++;
			int lastIndex = snakeLength - 1;
			snakeX.add(snakeX.get(lastIndex - 1));
			snakeY.add(snakeY.get(lastIndex - 1));
		}

		private boolean isCollidingFood() {
			return snakeX.get(0) == foodX && snakeY.get(0) == foodY;
		}

		private void generateFood() {
			foodX = (int) (Math.random() * screenSize);
			foodY = (int) (Math.random() * screenSize);
			foodX = foodX - (foodX % snakeSize);
			foodY = foodY - (foodY % snakeSize);
		}

		private void moveSnake() {
			if (nextMoves.size() > 0) {
				changeDirection(nextMoves.get(0));
				nextMoves.remove(0);
			}

			for (int i = snakeLength - 1; i > 0; i--) {
				snakeX.set(i, snakeX.get(i - 1));
				snakeY.set(i, snakeY.get(i - 1));
			}

			if (snakeDirection == 0) {
				snakeY.set(0, snakeY.get(0) - snakeSize);
			} else if (snakeDirection == 1) {
				snakeX.set(0, snakeX.get(0) + snakeSize);
			} else if (snakeDirection == 2) {
				snakeY.set(0, snakeY.get(0) + snakeSize);
			} else if (snakeDirection == 3) {
				snakeX.set(0, snakeX.get(0) - snakeSize);
			}

			// Check if the new head position is within bounds
			if (snakeX.get(0) < 0 || snakeX.get(0) >= screenSize || snakeY.get(0) < 0 || snakeY.get(0) >= screenSize) {
				resetSnake();
			}
		}


		private void renderSnake() {
			for (int i = 0; i < snakeLength; i++) {
				if (i == 0) {
					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgRect(nvg, snakeX.get(i), snakeY.get(i), snakeSize, snakeSize);
					NanoVG.nvgFillColor(nvg, new Colour(0xFF5dd95b).nvg());
					NanoVG.nvgFill(nvg);
				} else {
					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgRect(nvg, snakeX.get(i), snakeY.get(i), snakeSize, snakeSize);
					NanoVG.nvgFillColor(nvg, new Colour(0xFF69ff67).nvg());
					NanoVG.nvgFill(nvg);
				}
			}
		}

		private void renderFood() {
			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgRect(nvg, foodX, foodY, snakeSize, snakeSize);
			NanoVG.nvgFillColor(nvg, new Colour(0xFFfe6666).nvg());
			NanoVG.nvgFill(nvg);
		}

		private void resetSnake() {
			snakeLength = 3;
			snakeDirection = 3;
			snakeX.clear();
			snakeY.clear();

			for (int i = 0; i < snakeLength; i++) {
				snakeX.add((screenSize / 2) - (i * snakeSize));
				snakeY.add(screenSize / 2);
			}

			moveSnake();
		}

		public void changeDirection(int newDirection) {
			// Prevent the snake from immediately reversing its direction
			if (Math.abs(snakeDirection - newDirection) != 2) {
				snakeDirection = newDirection;
			}
		}

		@Override
		public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
			if (keyCode == keys[keyIndex]) {
				keyIndex++;
				if (keyIndex == keys.length) {
					mc.setScreen(new ModsScreen(VisibleSeasonsMod.instance));
					keyIndex = 0;
				}
			} else {
				keyIndex = 0;
			}

			if (keyCode == Keyboard.KEY_UP) {
				nextMoves.add(0);
			}
			if (keyCode == Keyboard.KEY_RIGHT) {
				nextMoves.add(1);
			}
			if (keyCode == Keyboard.KEY_DOWN) {
				nextMoves.add(2);
			}
			if (keyCode == Keyboard.KEY_LEFT) {
				nextMoves.add(3);
			}

			return super.keyPressed(info, keyCode, character);
		}

		@Override
		public Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(screenSize, screenSize);
		}

	}

}
