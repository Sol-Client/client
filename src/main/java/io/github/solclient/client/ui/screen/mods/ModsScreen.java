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

import io.github.solclient.client.mod.impl.VisibleSeasonsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.input.Keyboard;

import io.github.solclient.client.SolClient;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.core.CoreMod;
import io.github.solclient.client.ui.*;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.PanoramaBackgroundScreen;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Random;


public class ModsScreen extends PanoramaBackgroundScreen {

	protected MinecraftClient mc = MinecraftClient.getInstance();
	private final ModsScreenComponent component;
	private final ScreenAnimation animation = new ScreenAnimation();

	public ArrayList<int[]> snowflakes = new ArrayList<>();
	private final long nvg;

	public ModsScreen() {
		this(null);
	}

	public ModsScreen(Mod mod) {
		super(new Component() {
			{
				add(new ModsScreenComponent(mod), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
			}
		});

		nvg = NanoVGManager.getNvg();
		component = (ModsScreenComponent) root.getSubComponents().get(0);
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

	public void switchMod(Mod mod) {
		component.switchMod(mod);
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

	public static class ModsScreenComponent extends BlockComponent {

		@Getter
		private Mod mod;
		private TextFieldComponent search;
		private ButtonComponent back;
		@Getter
		private ModsScroll scroll;
		private Component config;
		private boolean singleModMode;

		private ModEntry targetDraggingMod;
		private ModEntry draggingMod;
		private boolean drop;
		private ModGhost ghost;
		private int modIndex;
		private int mouseX;
		private int mouseY;
		private int dragX;
		private int dragY;

		public ModsScreenComponent(Mod startingMod) {
			super(Theme.bg(), Controller.of(12F), Controller.of(0F));

			if (startingMod != null) {
				singleModMode = true;
			}

			add(new LabelComponent((component, defaultText) -> mod != null ? I18n.translate(mod.getName())
					: I18n.translate("sol_client.mod.screen.title")).scaled(1.45F),
					new AlignedBoundsController(Alignment.START, Alignment.START, (component, defaultBounds) -> {
						Rectangle result = new Rectangle(getBaseX(), getBaseX() + 3, defaultBounds.getWidth(),
								defaultBounds.getHeight());
						if (!singleModMode && mod != null)
							result = result.offset(24, 0);

						return result;
					}));

			scroll = new ModsScroll(this);

			ButtonComponent done = ButtonComponent.done(() -> getScreen().close()).width(50);
			add(done, new AlignedBoundsController(Alignment.END, Alignment.START,
					(component, defaultBounds) -> defaultBounds.offset(-getBaseX(), getBaseX())));

			if (!singleModMode) {
				add(new ButtonComponent("sol_client.hud.edit", Theme.button(), Theme.fg()).onClick((info, button) -> {
					if (button == 0) {
						MinecraftUtils.playClickSound(true);
						mc.setScreen(new MoveHudsScreen());
						return true;
					}

					return false;
				}).withIcon("edit").width(60), (component, bounds) -> bounds
						.offset(done.getBounds().getX() - bounds.getWidth() - 4, getBaseX()));
			}

			search = new TextFieldComponent(0, 32, false).autoFlush().onUpdate((ignored) -> {
				scroll.snapTo(0);
				scroll.load();
				return true;
			}).withPlaceholder("sol_client.mod.screen.search").withIcon("search");
			back = new ButtonComponent("", Theme.button(), Theme.fg()).width(16).height(16).withIcon("back")
					.onClick((info, button) -> {
						if (button != 0)
							return false;

						MinecraftUtils.playClickSound(true);
						switchMod(null, false);
						return true;
					});

			switchMod(startingMod, true);
			scroll.load();
		}

		// based on start x for mods
		private int getBaseX() {
			return getBounds().getWidth() / 2 - 230 / 2;
		}

		public void singleModMode() {
			this.singleModMode = true;
		}

		public void switchMod(Mod mod) {
			switchMod(mod, false);
		}

		public void switchMod(Mod mod, boolean first) {
			if (mod == null && (this.mod != null || first)) {
				add(0, search, (component, bounds) -> new Rectangle(getBaseX(), 38,
						getBounds().getWidth() - getBaseX() * 2, bounds.getHeight()));
				add(scroll, (component, defaultBounds) -> new Rectangle(0, 60, getBounds().getWidth(),
						getBounds().getHeight() - 60));

				if (!first) {
					remove(back);
					remove(config);
				}

				config = null;
			} else if (mod != null) {
				if (config != null)
					remove(config);

				config = mod.createConfigComponent();
				add(config, Controller
						.of(() -> new Rectangle(0, 45, getBounds().getWidth(), getBounds().getHeight() - 45)));

				if (this.mod == null) {
					if (!singleModMode)
						add(back, (component, defaultBounds) -> defaultBounds.offset(getBaseX(), getBaseX() + 2));

					if (!first) {
						remove(search);
						remove(scroll);
					}
				}
			}

			this.mod = mod;
		}

		@Override
		public void render(ComponentRenderInfo info) {
			super.render(info);

			mouseX = (int) info.relativeMouseX();
			mouseY = (int) info.relativeMouseY();

			if (targetDraggingMod != null) {
				draggingMod = targetDraggingMod;
				targetDraggingMod = null;
				getScroll().getPinned().remove(draggingMod);
				ghost = new ModGhost();
				getScroll().getPinned().add(modIndex, ghost);
				add(draggingMod, (component, defaultBounds) -> defaultBounds.offset(mouseX - dragX, mouseY - dragY));
			} else if (draggingMod != null) {
				if (drop) {
					drop = false;
					remove(draggingMod);
					getScroll().getPinned().remove(ghost);
					getScroll().getPinned().add(modIndex, draggingMod);

					ModUiStateManager.INSTANCE.reorderPin(draggingMod.getMod(), modIndex - 1);

					draggingMod = null;
				} else {
					int ghostY = ghost.getBounds().getY();
					int mouse = draggingMod.getBounds().getY() - getScroll().getBounds().getY()
							+ getScroll().getScroll();
					getScroll().getPinned().remove(ghost);

					if (mouse > ghostY + 20) {
						modIndex++;
					} else if (mouse < ghostY - 20) {
						modIndex--;
					}

					int max = ModUiStateManager.INSTANCE.getPins().size();
					if (modIndex < 1) {
						modIndex = 1;
					} else if (modIndex > max) {
						modIndex = max;
					}

					getScroll().getPinned().add(modIndex, ghost);
				}
			}
		}

		@Override
		public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside, boolean processed) {
			if (draggingMod != null)
				return false;

			return super.mouseClickedAnywhere(info, button, inside, processed);
		}

		public static final int[] keys = {
				Keyboard.KEY_UP, Keyboard.KEY_UP, Keyboard.KEY_DOWN, Keyboard.KEY_DOWN, Keyboard.KEY_LEFT, Keyboard.KEY_RIGHT,
				Keyboard.KEY_LEFT, Keyboard.KEY_RIGHT, Keyboard.KEY_B, Keyboard.KEY_A
		};

		private int keyIndex = 0;

		@Override
		public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
			if (keyCode == keys[keyIndex]) {
				LogManager.getLogger().info("Konami code: " + keyIndex);
				keyIndex++;
				if (keyIndex == keys.length) {
					// switchMod(VisibleSeasonsMod.instance);
					mc.setScreen(new SnakeScreen());
					LogManager.getLogger().info("sucaiis");
					keyIndex = 0;
				}
			} else {
				keyIndex = 0;
			}

			if ((screen.getRoot().getDialog() == null
					&& (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER))) {
				if (mod == null) {
					if (!search.getText().isEmpty())
						return scroll.getSubComponents().get(0).mouseClickedAnywhere(info, 1, true, false);
				} else if (!mod.isForcedOn()) {
					MinecraftUtils.playClickSound(true);
					mod.setEnabled(!mod.isEnabled());
					return true;
				}
			} else if (draggingMod == null && mod == null && keyCode == Keyboard.KEY_F && hasControlDown()
					&& !hasShiftDown() && !hasAltDown()) {
				search.setFocused(true);
				return true;
			}

			if (character > 31 && !search.isFocused() && mod == null && draggingMod == null) {
				search.setFocused(true);
				search.setText("");
			}

			boolean result = super.keyPressed(info, keyCode, character);

			if (!result) {
				if (keyCode == CoreMod.instance.modsKey.getCode()
						&& KeyBindingInterface.from(CoreMod.instance.modsKey).areModsPressed()) {
					mc.setScreen(null);
					return true;
				} else if (mod != null && (keyCode == Keyboard.KEY_BACK
						|| (keyCode == Keyboard.KEY_LEFT && hasAltDown() && !hasControlDown() && !hasShiftDown()))
						&& screen.getRoot().getDialog() == null) {
					switchMod(null);
					return true;
				}
			}

			return result;
		}

		public String getFilter() {
			return search.getText();
		}

		void notifyDrag(ModEntry listing, int xOffset, int yOffset) {
			targetDraggingMod = listing;
			modIndex = getScroll().getPinned().getSubComponents().indexOf(listing);
			this.dragX = xOffset;
			this.dragY = yOffset;
		}

		void notifyDrop(ModEntry listing) {
			drop = true;
		}

		@Override
		public Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(256, 290);
		}

	}

}
