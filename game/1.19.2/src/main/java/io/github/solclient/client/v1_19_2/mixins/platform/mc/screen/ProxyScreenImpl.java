package io.github.solclient.client.v1_19_2.mixins.platform.mc.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.screen.ProxyScreen;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Mixin(ProxyScreen.class)
public abstract class ProxyScreenImpl extends Screen {

	protected ProxyScreenImpl(Text text) {
		super(text);
	}

	@Overwrite(remap = false)
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		super.render(SharedObjects.primary2dMatrixStack, mouseX, mouseY, client.getLastFrameDuration());
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		renderScreen(mouseX, mouseY, client.getTickDelta());
	}

	@Overwrite(remap = false)
	public boolean characterTyped(char character, int key) {
		return super.charTyped(character, key);
	}

	@Override
	public boolean charTyped(char character, int key) {
		return characterTyped(character, key);
	}

	@Overwrite(remap = false)
	public boolean keyDown(int key, int scancode, int mods) {
		return super.keyPressed(key, scancode, mods);
	}

	@Override
	public boolean keyPressed(int key, int scancode, int mods) {
		return keyDown(key, scancode, mods);
	}

	@Overwrite(remap = false)
	public boolean mouseDown(int x, int y, int button) {
		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return mouseDown((int) mouseX, (int) mouseY, button);
	}

	@Overwrite(remap = false)
	public boolean mouseUp(int x, int y, int button) {
		return super.mouseReleased(x, y, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return mouseUp((int) mouseX, (int) mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		if(f < 1 && f > -1) {
			if(f < 0) {
				scroll(-1);
			}
			else {
				scroll(1);
			}
		}
		else {
			scroll((int) f);
		}
		return super.mouseScrolled(d, e, f);
	}

	@Shadow(remap = false)
	public abstract void scroll(int by);

	@Overwrite(remap = false)
	public void initScreen() {
		super.init();
		mc = (MinecraftClient) client;
		font = (Font) textRenderer;
		width = super.width;
		height = super.height;
	}

	@Override
	protected void init() {
		initScreen();
	}

	@Override
	public void removed() {
		onClose();
	}

	@Shadow(remap = false)
	public void onClose() {}

	@Overwrite(remap = false)
	protected final void renderDefaultBackground() {
		renderBackground(SharedObjects.primary2dMatrixStack, 0);
	}

	@Overwrite(remap = false)
	public void tickScreen() {
		super.tick();
	}

	@Override
	public void tick() {
		tickScreen();
	}

	@Override
	public boolean shouldPause() {
		return pausesGame();
	}

	@Shadow(remap = false)
	protected abstract boolean pausesGame();

	@Shadow(remap = false)
	protected int width, height;

	@Shadow(remap = false)
	protected MinecraftClient mc;

	@Shadow(remap = false)
	protected Font font;

}
