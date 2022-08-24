package io.github.solclient.client.ui.component;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.screen.ProxyScreen;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.ui.component.controller.ParentBoundsController;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Getter;

public class Screen extends ProxyScreen {

	@Getter
	protected io.github.solclient.client.platform.mc.screen.Screen parentScreen;
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
				return new Rectangle(0, 0, width, height);
			}

		};

		rootWrapper.add(root, new ParentBoundsController());

		rootWrapper.setScreen(this);
		rootWrapper.setFont(SolClientConfig.instance.getUIFont());

		this.root = root;
	}

	public Component getRoot() {
		return root;
	}

	@Override
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;

		if(background) {
			if(mc.hasLevel()) {
				DrawableHelper.fillRect(0, 0, width, height, Colour.BACKGROUND.getValue());
			}
			else {
				renderDefaultBackground();
			}
		}

		rootWrapper.render(new ComponentRenderInfo(mouseX, mouseY, tickDelta));

		super.renderScreen(mouseX, mouseY, tickDelta);
	}

	@Override
	public void scroll(int by) {
		super.scroll(by);
		rootWrapper.mouseScroll(getInfo(), by);
	}

	@Override
	public void keyDown(int code, int scancode, int mods) {
		if(!rootWrapper.keyPressed(getInfo(), code, scancode, mods)) {
			if(code == 1) {
				closeAll();
			}
			else {
				super.keyDown(code, scancode, mods);
			}
		}
	}

	@Override
	public void characterTyped(char character, int key) {
		if(!rootWrapper.characterTyped(getInfo(), character)) {
			super.characterTyped(character, key);
		}
	}

	@Override
	public void mouseDown(int x, int y, int button) {
		if(!rootWrapper.mouseClickedAnywhere(getInfo(), button, true, false)) {
			super.mouseDown(x, y, button);
		}
	}

	@Override
	public void mouseUp(int x, int y, int button) {
		if(!rootWrapper.mouseReleasedAnywhere(getInfo(), button, true)) {
			super.mouseUp(x, y, button);
		}
	}

	@Override
	public void tickScreen() {
		super.tickScreen();
		rootWrapper.tick();
	}

	@Override
	protected boolean pausesGame() {
		return false;
	}

	public void updateFont() {
		rootWrapper.setFont(SolClientConfig.instance.getUIFont());
	}

	public void close() {
		mc.setScreen(parentScreen);
	}

	public void closeAll() {
		mc.setScreen(null);
	}

	private ComponentRenderInfo getInfo() {
		return new ComponentRenderInfo(mouseX, mouseY, mc.getTimer().getTickDelta());
	}

}