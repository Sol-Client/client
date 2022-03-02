package me.mcblueparrot.client.ui.component;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import lombok.Getter;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.controller.ParentBoundsController;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class Screen extends GuiScreen {

	@Getter
	protected GuiScreen parentScreen;
	protected Component root;
	private Component rootWrapper;
	private int mouseX;
	private int mouseY;
	protected boolean background = true;

	public Screen(Component root) {
		this.parentScreen = Minecraft.getMinecraft().currentScreen;
		rootWrapper = new Component() {

			@Override
			public Rectangle getBounds() {
				return new Rectangle(0, 0, width, height);
			}

		};

		rootWrapper.add(root, new ParentBoundsController());

		rootWrapper.setScreen(this);
		rootWrapper.setFont(SolClientMod.getFont());

		this.root = root;
	}

	public Component getRoot() {
		return root;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;

		if(background) {
			if(mc.theWorld == null) {
				drawRect(0, 0, width, height, Colour.BACKGROUND.getValue());
			}
			else {
				drawDefaultBackground();
			}
		}

		rootWrapper.render(new ComponentRenderInfo(mouseX, mouseY, partialTicks));

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		if(Mouse.getEventDWheel() != 0) {
			rootWrapper.mouseScroll(getInfo(), Mouse.getEventDWheel());
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(!rootWrapper.keyPressed(getInfo(), keyCode, typedChar)) {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(!rootWrapper.mouseClickedAnywhere(getInfo(), mouseButton, true, false)) {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if(!rootWrapper.mouseReleasedAnywhere(getInfo(), state, true)) {
			super.mouseReleased(mouseX, mouseY, state);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		rootWrapper.tick();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public void updateFont() {
		rootWrapper.setFont(SolClientMod.getFont());
	}

	public void close() {
		mc.displayGuiScreen(parentScreen);
	}

	private ComponentRenderInfo getInfo() {
		return new ComponentRenderInfo(mouseX, mouseY, AccessMinecraft.getInstance().getTimerSC().renderPartialTicks);
	}

}
