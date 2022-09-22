package io.github.solclient.client.v1_8_9.mixins.platform.mc.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.screen.Screen;

@Mixin(net.minecraft.client.gui.screen.Screen.class)
public abstract class ScreenImpl implements Screen {

	@Override
	public void update(MinecraftClient mc, int width, int height) {
		init((net.minecraft.client.MinecraftClient) mc, width, height);
	}

	@Shadow
	public abstract void init(net.minecraft.client.MinecraftClient client, int width, int height);

	@Override
	public void initScreen() {
		init();
	}

	@Shadow
	public abstract void init();

	@Override
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		render(mouseX, mouseY, tickDelta);
	}

	@Shadow
	public abstract void render(int mouseX, int mouseY, float tickDelta);

	@Override
	public boolean characterTyped(char character, int key) {
		keyPressed(character, key);
		return false;
	}

	@Shadow
	protected abstract void keyPressed(char character, int code);

	@Override
	public boolean mouseDown(int x, int y, int button) {
		mouseClicked(x, y, button);
		return false;
	}

	@Override
	public boolean keyDown(int code, int scancode, int mods) {
		keyPressed('\0', code);
		return false;
	}

	@Shadow
	protected abstract void mouseClicked(int mouseX, int mouseY, int button);

	@Override
	public boolean mouseUp(int x, int y, int button) {
		mouseReleased(x, y, button);
		return false;
	}

	@Shadow
	protected abstract void mouseReleased(int mouseX, int mouseY, int button);

	@Override
	public int getWidth() {
		return width;
	}

	@Shadow
	public int width;

	@Override
	public int getHeight() {
		return height;
	}

	@Shadow
	public int height;

	@Override
	public void tickScreen() {
		tick();
	}

	@Shadow
	public abstract void tick();

}
