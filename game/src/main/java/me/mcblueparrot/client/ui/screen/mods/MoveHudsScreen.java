package me.mcblueparrot.client.ui.screen.mods;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.mod.hud.HudElement;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.element.Button;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class MoveHudsScreen extends GuiScreen {

	private GuiScreen previous;
	private GuiScreen title;
	private HudElement movingHud;
	private Position moveOffset;
	private boolean wasMouseDown;
	private boolean mouseDown;

	public MoveHudsScreen(GuiScreen previous, GuiScreen title) {
		this.previous = previous;
		this.title = title;
	}

	public HudElement getSelectedHud(int mouseX, int mouseY) {
		for(HudElement hud : Client.INSTANCE.getHuds()) {
			if(!(hud.isEnabled() && hud.isVisible())) continue;

			if(hud.isSelected(mouseX, mouseY)) {
				return hud;
			}
		}
		return null;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0) {
			mouseDown = true;
		}

		if(mouseButton == 1) {
			for(HudElement hud : Client.INSTANCE.getHuds()) {
				if(hud.isEnabled() && hud.isVisible() && hud.getMultipliedBounds() != null && hud.getMultipliedBounds()
						.contains(mouseX, mouseY)) {
					if(previous instanceof ModsScreen) {
						Utils.playClickSound();
						((ModsScreen) previous).switchMod(hud.getMod());
						Minecraft.getMinecraft().displayGuiScreen(previous);
					}
				}
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if(state == 0) {
			mouseDown = false;
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);

		if(title != null) title.setWorldAndResolution(mc, width, height);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if(title != null) title.updateScreen();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(title != null) {
			title.drawScreen(0, 0, partialTicks);
		}

		HudElement selectedHud = getSelectedHud(mouseX, mouseY);
		if(Mouse.isButtonDown(0)) {
			if(movingHud == null) {
				if(selectedHud != null) {
					movingHud = selectedHud;
					moveOffset = new Position(selectedHud.getPosition().getX() - mouseX,
							selectedHud.getPosition().getY() - mouseY);
				}
			}
			else {
				movingHud.setPosition(new Position(mouseX + moveOffset.getX(), mouseY + moveOffset.getY()));
			}
		}
		else {
			movingHud = null;
		}

		Button button = new Button(SolClientMod.getFont(), "Done", new Rectangle(width / 2 - 50, height - 60, 100, 20), new Colour(0, 255, 0),
				new Colour(150, 255, 150));
		button.render(mouseX, mouseY);

		if(button.contains(mouseX, mouseY) && !wasMouseDown && mouseDown) {
			Utils.playClickSound();
			mc.displayGuiScreen(previous);
		}

		for(HudElement hud : Client.INSTANCE.getHuds()) {
			if(!(hud.isEnabled() && hud.isVisible())) continue;

			if(mc.theWorld == null) {
				hud.render(true);
			}

			Rectangle bounds = hud.getMultipliedBounds();
			if(bounds != null) {
				bounds.stroke(SolClientMod.instance.uiColour);
			}
		}

		wasMouseDown = mouseDown;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1) {
			Client.INSTANCE.save();
			if(title != null) {
				mc.displayGuiScreen(title);
			}
			else {
				mc.displayGuiScreen(null);
			}
		}
		else if(keyCode == SolClientMod.instance.editHudKey.getKeyCode()) {
			mc.displayGuiScreen(null);
		}
	}

}
