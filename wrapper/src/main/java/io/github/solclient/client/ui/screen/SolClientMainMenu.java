package io.github.solclient.client.ui.screen;

import org.lwjgl.nanovg.NanoVG;

import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.util.GlobalConstants;
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

		client.getTextureManager().bindTexture(
				new Identifier("textures/gui/sol_client_logo_with_text_" + MinecraftUtils.getTextureScale() + ".png"));
		drawTexture(width / 2 - 64, 50, 0, 0, 128, 32, 128, 32);

		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyPressed(char character, int code) {
		if (code == 1) {
			return;
		}

		super.keyPressed(character, code);
	}

	private static class MainMenuComponent extends Component {

		private int buttonsX;

		public MainMenuComponent() {

			add(new ButtonComponent((component, defaultText) -> I18n.translate("menu.singleplayer"), theme.accent(),
					Controller.of(Colour.BLACK)).withIcon("person").width(200).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new SelectWorldScreen(screen));
							return true;
						}

						return false;
					}), (component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, screen.height / 4 + 48,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> I18n.translate("menu.multiplayer"), theme.accent(),
					Controller.of(Colour.BLACK)).withIcon("people").width(200).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new MultiplayerScreen(screen));
							return true;
						}

						return false;
					}), (component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, screen.height / 4 + 73,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "", theme.button(), Controller.of(Colour.WHITE))
					.withIcon("language").width(20).onClick((info, button) -> {
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

						buttonsX = screen.width / 2 - (12 * buttonsCount);

						return new Rectangle(buttonsX, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight());
					});

			add(new ButtonComponent((component, defaultText) -> "", theme.button(), Controller.of(Colour.WHITE))
					.withIcon("options").width(20).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new SettingsScreen(screen, mc.options));
							return true;
						}

						return false;
					}), (component, defaultBounds) -> new Rectangle(buttonsX + 26, screen.height / 4 + 48 + 70,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "", theme.button(), Controller.of(Colour.WHITE))
					.withIcon("sol_client_mods").width(20).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.setScreen(new ModsScreen());
							return true;
						}

						return false;
					}), (component, defaultBounds) -> new Rectangle(buttonsX + 52, screen.height / 4 + 48 + 70,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "", theme.button(), Controller.of(Colour.WHITE))
					.withIcon("replay_menu").width(20).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							new GuiReplayViewer(ReplayModReplay.instance).display();
							return true;
						}

						return false;
					}).visibilityController((component, defaultVisibility) -> SCReplayMod.enabled),
					(component, defaultBounds) -> new Rectangle(buttonsX + 78, screen.height / 4 + 48 + 70,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(
							(component, defaultColour) -> component.isHovered() ? Colour.RED_HOVER : Colour.PURE_RED),
					Controller.of(Colour.WHITE)).onClick((info, button) -> {
						if (button == 0) {
							MinecraftUtils.playClickSound(true);
							mc.stop();
							return true;
						}

						return false;
					}).width(20).withIcon("sol_client_exit"),
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
