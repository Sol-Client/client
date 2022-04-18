package me.mcblueparrot.client.ui.screen;

import java.io.IOException;

import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.mod.impl.replay.SCReplayMod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.Screen;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.ui.component.impl.ButtonComponent;
import me.mcblueparrot.client.ui.component.impl.ButtonType;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessGuiMainMenu;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import me.mcblueparrot.client.util.font.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SolClientMainMenu extends PanoramaBackgroundScreen {

	private GuiMainMenu base;
	private boolean wasMouseDown;
	private boolean mouseDown;

	public SolClientMainMenu() {
		super(new MainMenuComponent());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawPanorama(mouseX, mouseY, partialTicks);

		Font font = SolClientMod.getFont();

		String copyrightString = "Copyright Mojang AB. Do not distribute!";
		font.renderString(copyrightString, (int) (width - font.getWidth(copyrightString) - 10), height - 15, -1);
		String versionString = "Minecraft 1.8.9";
		font.renderString(versionString, (int) (width - font.getWidth(versionString) - 10), height - 25, -1);

		font.renderString("Copyright TheKodeToad and contributors.", 10, height - 15, -1);
		font.renderString(Client.NAME, 10, height - 25, -1);

		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_logo_with_text_" +
						Utils.getTextureScale() + ".png"));
		Gui.drawModalRectWithCustomSizedTexture(width / 2 - 64, 50, 0, 0, 128, 32, 128, 32);

		wasMouseDown = mouseDown;

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			return;
		}

		super.keyTyped(typedChar, keyCode);
	}

	private static class MainMenuComponent extends Component {

		private int buttonsX;

		public MainMenuComponent() {
			Controller<Colour> defaultColourController =
					(component, defaultColour) -> component.isHovered() ? SolClientMod.instance.uiHover
							: SolClientMod.instance.uiColour;

			add(new ButtonComponent((component, defaultText) -> I18n.format("menu.singleplayer"),
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_player")
							.type(ButtonType.LARGE).onClick((info, button) -> {
								if (button == 0) {
									Utils.playClickSound(true);
									mc.displayGuiScreen(new GuiSelectWorld(screen));
									return true;
								}

								return false;
							}),
					(component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, screen.height / 4 + 48,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> I18n.format("menu.multiplayer"), new AnimatedColourController(defaultColourController))
					.withIcon("sol_client_players").type(ButtonType.LARGE).onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiMultiplayer(screen));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(screen.width / 2 - 100, screen.height / 4 + 73,
							defaultBounds.getWidth(), defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_language").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiLanguage(screen, mc.gameSettings, mc.getLanguageManager()));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> {
						int buttonsCount = 3;

						if(SCReplayMod.enabled) {
							buttonsCount++;
						}

						buttonsX = screen.width / 2 - (12 * buttonsCount);

						return new Rectangle(buttonsX, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight());
					});

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_settings_small").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new GuiOptions(screen, mc.gameSettings));
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(buttonsX + 26, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_mods").type(ButtonType.SMALL)
									.onClick((info, button) -> {
										if(button == 0) {
											Utils.playClickSound(true);
											mc.displayGuiScreen(new ModsScreen());
											return true;
										}

										return false;
									}),
					(component, defaultBounds) -> new Rectangle(buttonsX + 52, screen.height / 4 + 48 + 70, defaultBounds.getWidth(),
								defaultBounds.getHeight()));

			add(new ButtonComponent((component, defaultText) -> "",
					new AnimatedColourController(defaultColourController)).withIcon("sol_client_replay_button")
							.type(ButtonType.SMALL).onClick((info, button) -> {
								if (button == 0) {
									Utils.playClickSound(true);
									new GuiReplayViewer(ReplayModReplay.instance).display();
									return true;
								}

								return false;
							}).visibilityController((component, defaultVisibility) -> SCReplayMod.enabled),
					(component, defaultBounds) -> new Rectangle(buttonsX + 78, screen.height / 4 + 48 + 70,
							defaultBounds.getWidth(), defaultBounds.getHeight()));
		}

	}

}
