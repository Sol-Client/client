package io.github.solclient.client.ui.screen;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.Environment;
import io.github.solclient.client.platform.mc.Identifier;
import io.github.solclient.client.platform.mc.lang.I18n;
import io.github.solclient.client.platform.mc.screen.LanguageScreen;
import io.github.solclient.client.platform.mc.screen.MultiplayerScreen;
import io.github.solclient.client.platform.mc.screen.OptionsScreen;
import io.github.solclient.client.platform.mc.screen.SingleplayerScreen;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.text.I18nText;
import io.github.solclient.client.todo.TODO;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.ui.component.impl.ButtonType;
import io.github.solclient.client.ui.screen.mods.ModsScreen;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;

public class SolClientMainMenu extends PanoramaBackgroundScreen {

	public SolClientMainMenu() {
		super(I18nText.create(Environment.MINOR_RELEASE >= 12 ? "narrator.screen.title" : "deathScreen.titleScreen"),
				new MainMenuComponent());
	}

	@Override
	public void render(int x, int y, float tickDelta) {
		renderPanorama(x, y, tickDelta);

		Font font = SolClientConfig.instance.getUIFont();

		String copyrightString = "Copyright Mojang " + (Environment.MINOR_RELEASE >= 16 ? "Studios" : "AB") + ". Do not distribute!";
		font.render(copyrightString, (int) (getWidth() - font.getWidth(copyrightString) - 10), getHeight() - 15, -1);
		String versionString = "Minecraft 1.8.9";
		font.render(versionString, (int) (getWidth() - font.getWidth(versionString) - 10), getHeight() - 25, -1);

		font.render("Copyright TheKodeToad and contributors.", 10, getHeight() - 15, -1);
		font.render(Client.NAME, 10, getHeight() - 25, -1);

		mc.getTextureManager().bind(Identifier.minecraft("textures/gui/sol_client_logo_with_text_" +
						Utils.getTextureScale() + ".png"));
		DrawableHelper.fillTexturedRect(getWidth() / 2 - 64, 50, 0, 0, 128, 32, 128, 32);

		super.render(x, y, tickDelta);
	}

	@Override
	public void keyDown(char character, int key) {
		if(key == 1) {
			return;
		}

		super.keyDown(character, key);
	}

	private static class MainMenuComponent extends Component {

		private int buttonsX;

		public MainMenuComponent() {
			Controller<Colour> defaultColourController =
					(component, defaultColour) -> component.isHovered() ? SolClientConfig.instance.uiHover
							: SolClientConfig.instance.uiColour;

			add(new ButtonComponent((component, defaultText) -> I18n.translate("menu.singleplayer"),
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_player")
							.type(ButtonType.LARGE).onClick((info, button) -> {
								if (button == 0) {
									Utils.playClickSound(true);
									mc.setScreen(SingleplayerScreen.create(screen));
									return true;
								}

								return false;
							}),
					(component, defaultBounds) -> new Rectangle(screen.getWidth() / 2 - 100, screen.getHeight() / 4 + 48,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> I18n.translate("menu.multiplayer"), new AnimatedColourController(defaultColourController))
					.withIcon("sol_client_players").type(ButtonType.LARGE).onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.setScreen(MultiplayerScreen.create(screen));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(screen.getWidth() / 2 - 100, screen.getHeight() / 4 + 73,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_language").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.setScreen(LanguageScreen.create(screen, mc.getOptions(), mc.getLanguageManager()));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> {
						int buttonsCount = 3;

						if(TODO.Z /* TODO replaymod */) {
							buttonsCount++;
						}

						buttonsX = screen.getWidth() / 2 - (12 * buttonsCount);

						return new Rectangle(buttonsX, screen.getHeight() / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight());
					});

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_settings_small").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.setScreen(OptionsScreen.create(screen, mc.getOptions()));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(buttonsX + 26, screen.getHeight() / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_mods").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.setScreen(new ModsScreen());
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(buttonsX + 52, screen.getHeight() / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_replay_button")
							.type(ButtonType.SMALL).onClick((info, button) -> {
								// TODO replaymod
//								if(button == 0) {
//									Utils.playClickSound(true);
//									new GuiReplayViewer(ReplayModReplay.instance).display();
//									return true;
//								}

								return false;
							}).visibilityController((component, defaultVisibility) -> TODO.Z /* TODO replaymod */ ),
					(component, defaultBounds) -> new Rectangle(buttonsX + 78, screen.getHeight() / 4 + 48 + 70,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(
							(component, defaultColour) -> component.isHovered() ? Colour.RED_HOVER : Colour.PURE_RED))
									.onClick((info, button) -> {
										if (button == 0) {
											Utils.playClickSound(true);
											mc.quit();
											return true;
										}

										return false;
									}).type(ButtonType.SMALL).withIcon("sol_client_exit"),
					(component, defaultBounds) -> new Rectangle(getBounds().getWidth() - 25, 5,
							defaultBounds.getWidth(), defaultBounds.getHeight()));
		}

	}

}
