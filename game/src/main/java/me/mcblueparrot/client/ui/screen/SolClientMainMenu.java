package me.mcblueparrot.client.ui.screen;

import java.io.IOException;

import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.gui.screen.GuiReplayViewer;

import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.mod.impl.replay.SCReplayMod;
import me.mcblueparrot.client.ui.element.Button;
import me.mcblueparrot.client.ui.screen.mods.ModsScreen;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class SolClientMainMenu extends GuiScreen {

	private boolean wasMouseDown;
	private boolean mouseDown;

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawRect(0, 0, width, height, Colour.BACKGROUND.getValue());
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_logo_with_text_" +
						Utils.getTextureScale() + ".png"));
		Gui.drawModalRectWithCustomSizedTexture(width / 2 - 64, 50, 0, 0, 128, 32, 128, 32);

		Button singleplayerButton = new Button(SolClientMod.getFont(), "Singleplayer",
				new Rectangle(width / 2 - 100, height / 4 + 48, 200, 20), SolClientMod.instance.uiColour, SolClientMod.instance.uiHover)
				.withIcon("textures/gui/sol_client_player");
		Button multiplayerButton = new Button(SolClientMod.getFont(), "Multiplayer",
				new Rectangle(width / 2 - 100, height / 4 + 48 + 25, 200, 20), SolClientMod.instance.uiColour, SolClientMod.instance.uiHover)
				.withIcon("textures/gui/sol_client_players");

		if(singleplayerButton.contains(mouseX, mouseY) && mouseDown && !wasMouseDown) {
			Utils.playClickSound();
			mc.displayGuiScreen(new GuiSelectWorld(this));
		}
		else if(multiplayerButton.contains(mouseX, mouseY) && mouseDown && !wasMouseDown) {
			Utils.playClickSound();
			mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		singleplayerButton.render(mouseX, mouseY);
		multiplayerButton.render(mouseX, mouseY);

		boolean replay = SCReplayMod.enabled;
		int buttonsCount = 3;

		if(replay) {
			buttonsCount++;
		}

		int buttonsX = width / 2 - (12 * buttonsCount);

		Button languageButton = new Button(SolClientMod.getFont(), "",
				new Rectangle(buttonsX, height / 4 + 48 + 70, 20, 20), SolClientMod.instance.uiColour,
				SolClientMod.instance.uiHover).withIcon("textures/gui/sol_client_language");
		Button optionsButton = new Button(SolClientMod.getFont(), "",
				new Rectangle(buttonsX += 26, height / 4 + 48 + 70, 20, 20), SolClientMod.instance.uiColour,
				SolClientMod.instance.uiHover).withIcon("textures/gui/sol_client_settings_small");
		Button modsButton = new Button(SolClientMod.getFont(), "",
				new Rectangle(buttonsX += 26, height / 4 + 48 + 70, 20, 20), SolClientMod.instance.uiColour,
				SolClientMod.instance.uiHover).withIcon("textures/gui/sol_client_mods");

		Button replayButton = null;

		if(replay) {
			replayButton = new Button(SolClientMod.getFont(), "",
					new Rectangle(buttonsX += 26, height / 4 + 48 + 70, 20, 20), SolClientMod.instance.uiColour,
					SolClientMod.instance.uiHover).withIcon("textures/gui/sol_client_replay_button");
		}

		if(mouseDown && !wasMouseDown) {
			if(optionsButton.contains(mouseX, mouseY)) {
				Utils.playClickSound();
				mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
			}
			else if(modsButton.contains(mouseX, mouseY)) {
				Utils.playClickSound();
				mc.displayGuiScreen(new ModsScreen(this));
			}
			else if(languageButton.contains(mouseX, mouseY)) {
				Utils.playClickSound();
				mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
			}
			else if(replayButton != null && replayButton.contains(mouseX, mouseY)) {
				Utils.playClickSound();
				new GuiReplayViewer(ReplayModReplay.instance).display();
			}
		}

		languageButton.render(mouseX, mouseY);
		optionsButton.render(mouseX, mouseY);
		modsButton.render(mouseX, mouseY);

		if(replayButton != null) {
			replayButton.render(mouseX, mouseY);
		}

		wasMouseDown = mouseDown;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if(mouseButton == 0) {
			mouseDown = true;
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);

		if(state == 0) {
			mouseDown = false;
		}
	}

}
