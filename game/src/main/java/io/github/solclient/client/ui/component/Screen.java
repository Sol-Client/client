package io.github.solclient.client.ui.component;

import java.io.IOException;

import org.lwjgl.input.*;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

import io.github.solclient.client.ui.component.controller.ParentBoundsController;
import io.github.solclient.client.util.NanoVGManager;
import io.github.solclient.client.util.access.AccessMinecraft;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

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

		this.root = root;
	}

	public Component getRoot() {
		return root;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;

		if (background) {
			if (mc.theWorld == null) {
				drawRect(0, 0, width, height, Colour.BACKGROUND.getValue());
			} else {
				drawDefaultBackground();
			}
		}

		long nvg = NanoVGManager.getNvg();

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		NanoVG.nvgBeginFrame(nvg, mc.displayWidth, mc.displayHeight, 1);
		NanoVG.nvgSave(nvg);

		NanoVG.nvgFontSize(nvg, 8);

		ScaledResolution resolution = new ScaledResolution(mc);
		NanoVG.nvgScale(nvg, resolution.getScaleFactor(), resolution.getScaleFactor());

		rootWrapper.render(new ComponentRenderInfo(mouseX, mouseY, partialTicks));

		NanoVG.nvgRestore(nvg);
		NanoVG.nvgEndFrame(nvg);
		GL11.glPopAttrib();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		if (Mouse.getEventDWheel() != 0) {
			rootWrapper.mouseScroll(getInfo(), Mouse.getEventDWheel());
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!rootWrapper.keyPressed(getInfo(), keyCode, typedChar)) {
			if (keyCode == Keyboard.KEY_ESCAPE) {
				Component withDialog = rootWrapper;

				if (withDialog.getDialog() == null) {
					if (!withDialog.getSubComponents().isEmpty()) {
						withDialog = withDialog.getSubComponents().get(0);
					} else {
						withDialog = null;
					}
				}

				if (withDialog != null && withDialog.getDialog() != null) {
					withDialog.setDialog(null);
					return;
				}

				closeAll();
			} else {
				super.keyTyped(typedChar, keyCode);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (!rootWrapper.mouseClickedAnywhere(getInfo(), mouseButton, true, false)) {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (!rootWrapper.mouseReleasedAnywhere(getInfo(), state, true)) {
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

	public void close() {
		mc.displayGuiScreen(parentScreen);
	}

	public void closeAll() {
		mc.displayGuiScreen(null);
	}

	private ComponentRenderInfo getInfo() {
		return new ComponentRenderInfo(mouseX, mouseY, AccessMinecraft.getInstance().getTimerSC().renderPartialTicks);
	}

}
