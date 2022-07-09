package io.github.solclient.client.ui.component;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import io.github.solclient.abstraction.mc.MinecraftClient;
import io.github.solclient.abstraction.mc.screen.ProxyScreen;
import io.github.solclient.abstraction.mc.text.Text;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.controller.ParentBoundsController;
import io.github.solclient.client.util.access.AccessMinecraft;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class Screen extends ProxyScreen {

	@Getter
	protected io.github.solclient.abstraction.mc.screen.Screen parentScreen;
	protected Component root;
	private Component rootWrapper;
	private int mouseX;
	private int mouseY;
	protected boolean background = true;

	public Screen(Text title, Component root) {
		super(title);
		parentScreen = MinecraftClient.getInstance().getScreen();
		rootWrapper = new Component() {

			@Override
			public Rectangle getBounds() {
				return new Rectangle(0, 0, getWidth(), getHeight());
			}

		};

		rootWrapper.add(root, new ParentBoundsController());

		rootWrapper.setScreen(this);
		rootWrapper.setFont(SolClientConfig.getUIFont());

		this.root = root;
	}

	public Component getRoot() {
		return root;
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;

		if(background) {
			if(mc.hasLevel()) {
				fillRect(0, 0, getWidth(), getHeight(), Colour.BACKGROUND.getValue());
			}
			else {
				renderTranslucentBackground();
			}
		}

		rootWrapper.render(new ComponentRenderInfo(mouseX, mouseY, tickDelta));

		super.drawScreen(mouseX, mouseY, tickDelta);
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
			if(keyCode == 1) {
				closeAll();
			}
			else {
				super.keyTyped(typedChar, keyCode);
			}
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
		rootWrapper.setFont(SolClientConfig.getUIFont());
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
