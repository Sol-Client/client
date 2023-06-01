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
import io.github.solclient.client.mod.impl.core.CoreMod;
import io.github.solclient.client.mod.impl.VisibleSeasonsMod;
import io.github.solclient.client.ui.ScreenAnimation;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.ui.component.impl.BlockComponent;
import io.github.solclient.client.ui.component.impl.LabelComponent;
import io.github.solclient.client.ui.screen.PanoramaBackgroundScreen;
import io.github.solclient.client.util.ActiveMainMenu;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.NanoVGManager;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.input.Keyboard;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SnakeScreen extends PanoramaBackgroundScreen {

	protected MinecraftClient mc = MinecraftClient.getInstance();
	private final ScreenAnimation animation = new ScreenAnimation();

	public static ArrayList<int[]> snowflakes = new ArrayList<>();
	private final long nvg;

	public SnakeScreen() {
		super(new Component() {
			{
				add(new SnakeComponent(), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
			}
		});

		nvg = NanoVGManager.getNvg();
		background = false;
	}

	@Override
	public void init() {
		super.init();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if ((VisibleSeasonsMod.instance.visibleSeasonsOverride) || LocalDate.now().getMonth() == Month.DECEMBER) {
			Random random = new Random();
			int snowflakeAmount = (int) VisibleSeasonsMod.instance.visibleSeasonsAmount;
			for (int i = 0; i < snowflakeAmount; i++) {
				if (snowflakes.size() < snowflakeAmount) {
					int x = random.nextInt(client.width);
					int y = random.nextInt(client.height);
					int size = 5 + random.nextInt(6);
					int speed = 1 + random.nextInt(3);

					snowflakes.add(new int[]{x, y, size, speed});
				}

				int x = snowflakes.get(i)[0];
				int y = snowflakes.get(i)[1];
				int size = snowflakes.get(i)[2];
				int speed = snowflakes.get(i)[3];

				if (VisibleSeasonsMod.instance.visibleSeasonsLowDetail) {
					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgRect(nvg, x, y, size, size);
					NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
					NanoVG.nvgFill(nvg);
				} else {
					NanoVG.nvgBeginPath(nvg);
					NVGPaint paint = MinecraftUtils.nvgMinecraftTexturePaint(nvg, new Identifier("sol_client", "textures/gui/snowflake.png"), x, y, size, size, 0);
					NanoVG.nvgFillPaint(nvg, paint);
					NanoVG.nvgRect(nvg, 0, 0, width, height);
					NanoVG.nvgFill(nvg);
				}

				// Reset the snowflake if it goes beyond the screen bounds
				if (snowflakes.get(i)[1] > client.height) {
					x = random.nextInt(client.width);
					y = random.nextInt(client.height);
					size = 5 + random.nextInt(6);
					speed = 1 + random.nextInt(3);
				}

				snowflakes.set(i, new int[]{x, y + speed, size, snowflakes.get(i)[3]});
			}
		}

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
		private final int snakeSize = 5;
		private List<Integer> snakeX = new ArrayList<>();
		private List<Integer> snakeY = new ArrayList<>();
		private int snakeDirection = 3;  // 0 = up, 1 = right, 2 = down, 3 = left

		private long lastFrameTime = 0;

		public SnakeComponent() {
			super(Theme.bg(), Controller.of(12F), Controller.of(0F));
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
			foodX = (int) (Math.random() * 300);
			foodY = (int) (Math.random() * 300);
			foodX = foodX - (foodX % snakeSize);
			foodY = foodY - (foodY % snakeSize);
		}

		private void moveSnake() {
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
			if (snakeX.get(0) < 0 || snakeX.get(0) >= 300 || snakeY.get(0) < 0 || snakeY.get(0) >= 300) {
				resetSnake();
			}
		}


		private void renderSnake() {
			for (int i = 0; i < snakeLength; i++) {
				if (i == 0) {
					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgRect(nvg, snakeX.get(i), snakeY.get(i), snakeSize, snakeSize);
					NanoVG.nvgFillColor(nvg, Colour.PURE_RED.nvg());
					NanoVG.nvgFill(nvg);
				} else {
					NanoVG.nvgBeginPath(nvg);
					NanoVG.nvgRect(nvg, snakeX.get(i), snakeY.get(i), snakeSize, snakeSize);
					NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
					NanoVG.nvgFill(nvg);
				}
			}
		}

		private void renderFood() {
			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgRect(nvg, foodX, foodY, snakeSize, snakeSize);
			NanoVG.nvgFillColor(nvg, Colour.PURE_GREEN.nvg());
			NanoVG.nvgFill(nvg);
		}

		private void resetSnake() {
			snakeLength = 3;
			snakeDirection = 3;
			snakeX.clear();
			snakeY.clear();

			for (int i = 0; i < snakeLength; i++) {
				snakeX.add(150 - (i * snakeSize));
				snakeY.add(150);
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
				changeDirection(0);
			}
			if (keyCode == Keyboard.KEY_RIGHT) {
				changeDirection(1);
			}
			if (keyCode == Keyboard.KEY_DOWN) {
				changeDirection(2);
			}
			if (keyCode == Keyboard.KEY_LEFT) {
				changeDirection(3);
			}

			return super.keyPressed(info, keyCode, character);
		}

		@Override
		public Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(300, 300);
		}

	}

}
