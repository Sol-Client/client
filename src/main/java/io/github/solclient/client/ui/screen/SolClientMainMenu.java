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

package io.github.solclient.client.ui.screen;

import org.lwjgl.nanovg.NanoVG;

import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class SolClientMainMenu extends PanoramaBackgroundScreen {

	public SolClientMainMenu() {
		super(new MainMenuComponent());
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawPanorama(mouseX, mouseY, partialTicks);

		client.getTextureManager().bindTexture(new Identifier("sol_client",
				"textures/gui/sol_client_logo_with_text_" + MinecraftUtils.getTextureScale() + ".png"));
		drawTexture(width / 2 - 64, getStartY(this), 0, 0, 128, 32, 128, 32);

		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyPressed(char character, int code) {
		if (code == 1) {
			return;
		}

		super.keyPressed(character, code);
	}

	private static int getStartY(Screen screen) {
		return screen.height / 2 - 123 / 2;
	}

	private static class MainMenuComponent extends Component {

		private int bubblesX;
		private int bubblesY;

		public MainMenuComponent() {

			add(new ButtonComponent((component, defaultText) -> I18n.translate("menu.singleplayer"), Theme.accent(),
					Theme.accentFg()).withIcon("person").width(200).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new SelectWorldScreen(screen));
							return true;
						}

						return false;
					}), (component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, getStartY(screen) + 44,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> I18n.translate("menu.multiplayer"), Theme.accent(),
					Theme.accentFg()).withIcon("people").width(200).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new MultiplayerScreen(screen));
							return true;
						}

						return false;
					}), (component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, getStartY(screen) + 69,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			// 🫧

			add(new ButtonComponent((component, defaultText) -> "", Theme.button(), Theme.fg()).withIcon("language")
					.width(20).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new LanguageOptionsScreen(screen, mc.options, mc.getLanguageManager()));
							return true;
						}

						return false;
					}), (component, defaultBounds) -> {
						int buttonsCount = 3;

						if (SCReplayMod.enabled) {
							buttonsCount++;
						}

						bubblesX = screen.width / 2 - (12 * buttonsCount);
						bubblesY = getStartY(screen) + 103;
						return new Rectangle(bubblesX, bubblesY, defaultBounds.getWidth(), defaultBounds.getHeight());
					});

			add(new ButtonComponent((component, defaultText) -> "", Theme.button(), Theme.fg()).withIcon("options")
					.width(20).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new SettingsScreen(screen, mc.options));
							return true;
						}

						return false;
					}), (component, defaultBounds) -> new Rectangle(bubblesX + 26, bubblesY, defaultBounds.getWidth(),
							defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "", Theme.button(), Theme.fg()).withIcon("mods")
					.width(20).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new ModsScreen());
							return true;
						}

						return false;
					}), (component, defaultBounds) -> new Rectangle(bubblesX + 52, bubblesY,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "", Theme.button(), Controller.of(Colour.WHITE))
					.withIcon("replay_menu").width(20).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							new GuiReplayViewer(ReplayModReplay.instance).display();
							return true;
						}

						return false;
					}).visibilityController((component, defaultVisibility) -> SCReplayMod.enabled),
					(component, defaultBounds) -> new Rectangle(bubblesX + 78, bubblesY, defaultBounds.getWidth(),
							defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "", Theme.danger(), Controller.of(Colour.WHITE))
					.onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.stop();
							return true;
						}

						return false;
					}).width(20).withIcon("exit"),
					(component, defaultBounds) -> new Rectangle(getBounds().getWidth() - 25, 5,
							defaultBounds.getWidth(), defaultBounds.getHeight()));
		}

		@Override
		public void render(ComponentRenderInfo info) {
			super.render(info);

			NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());

			String copyrightString = "Copyright Mojang AB. Do not distribute!";
			regularFont.renderString(nvg, copyrightString,
					(int) (screen.width - regularFont.getWidth(nvg, copyrightString) - 10), screen.height - 15);
			String versionString = "Minecraft 1.8.9";
			regularFont.renderString(nvg, versionString,
					(int) (screen.width - regularFont.getWidth(nvg, versionString) - 10), screen.height - 25);

			regularFont.renderString(nvg, GlobalConstants.COPYRIGHT, 10, screen.height - 15);
			regularFont.renderString(nvg, GlobalConstants.NAME, 10, screen.height - 25);
		}

	}

}
