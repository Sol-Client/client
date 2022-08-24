package io.github.solclient.client.v1_8_9.mixins.platform.mc.screen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.screen.ProxyScreen;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.util.Input;
import net.minecraft.client.gui.screen.Screen;

@Mixin(ProxyScreen.class)
public abstract class ProxyScreenImpl extends Screen {

	private char currentCharacter;

	@Overwrite
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		renderScreen(mouseX, mouseY, tickDelta);
	}

	@Overwrite
	public void keyDown(int code, int scancode, int mods) {
		super.keyPressed(currentCharacter, code);
	}

	@Overwrite
	public void characterTyped(char character, int key) {
		// no call to super is currently necessary.
	}

	@Override
	protected void keyPressed(char character, int code) {
		int mods = 0;

		// Modifiers bitfield for compatibility with LWJGL 3.

		if((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && code != Keyboard.KEY_LSHIFT)
				|| (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && code != Keyboard.KEY_RSHIFT)) {
			mods |= Input.SHIFT_MODIFIER;
		}

		if((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && code != Keyboard.KEY_LCONTROL)
				|| (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && code != Keyboard.KEY_RCONTROL)) {
			mods |= Input.CONTROL_MODIFIER;
		}

		if((Keyboard.isKeyDown(Keyboard.KEY_LMENU) && code != Keyboard.KEY_LMENU)
				|| (Keyboard.isKeyDown(Keyboard.KEY_RMENU) && code != Keyboard.KEY_RMENU)) {
			mods |= Input.ALT_MODIFIER;
		}

		if((Keyboard.isKeyDown(Keyboard.KEY_LMETA) && code != Keyboard.KEY_LMETA)
				|| (Keyboard.isKeyDown(Keyboard.KEY_RMETA) && code != Keyboard.KEY_RMETA)) {
			mods |= Input.SUPER_MODIFIER;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_CAPITAL) && code != Keyboard.KEY_CAPITAL) {
			mods |= Input.CAPS_LOCK_MODIFIER;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_NUMLOCK) && code != Keyboard.KEY_NUMLOCK) {
			mods |= Input.NUM_LOCK;
		}

		currentCharacter = character;
		keyDown(code, -1, mods);
		currentCharacter = '\0';

		if(character >= 31) {
			characterTyped(character, code);
		}
	}

	@Overwrite
	public void mouseDown(int x, int y, int button) {
		super.mouseClicked(x, y, button);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		mouseDown(mouseX, mouseY, button);
	}

	@Overwrite
	public void mouseUp(int x, int y, int button) {
		super.mouseReleased(x, y, button);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		mouseUp(mouseX, mouseY, button);
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		int dWheel = Mouse.getEventDWheel();

		if(dWheel != 0) {
			scroll(dWheel);
		}
	}

	@Shadow
	public abstract void scroll(int by);

	@Overwrite
	public void initScreen() {
		super.init();
		mc = (MinecraftClient) client;
		font = (Font) textRenderer;
		width = super.width;
		height = super.height;
	}

	@Override
	public void init() {
		initScreen();
	}

	@Override
	public void removed() {
		onClose();
	}

	@Shadow
	public void onClose() {}

	@Overwrite
	protected final void renderDefaultBackground() {
		renderBackground(0);
	}

	@Overwrite
	public void tickScreen() {
		super.tick();
	}

	@Override
	public void tick() {
		tickScreen();
	}

	@Override
	public boolean shouldPauseGame() {
		return pausesGame();
	}

	@Shadow
	protected abstract boolean pausesGame();

	@Shadow
	protected int width, height;

	@Shadow
	protected MinecraftClient mc;

	@Shadow
	protected Font font;

}
