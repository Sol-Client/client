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

	@Overwrite
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		super.render(SharedObjects.primary2dMatrixStack, mouseX, mouseY, tickDelta);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
		renderScreen(mouseX, mouseY, tickDelta);
	}

	@Overwrite
	public void characterTyped(char character, int key) {
		super.charTyped(character, key);
	}

	@Override
	public boolean charTyped(char character, int key) {
		characterTyped(character, key);
		return false;
	}

	@Overwrite
	public void keyDown(int key, int scancode, int mods) {
		super.keyPressed(key, scancode, mods);
	}

	@Override
	public boolean keyPressed(int key, int scancode, int mods) {
		keyDown(key, scancode, mods);
		return false;
	}

	@Overwrite
	public void mouseDown(int x, int y, int button) {
		super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		mouseDown((int) mouseX, (int) mouseY, button);
		return false;
	}

	@Overwrite
	public void mouseUp(int x, int y, int button) {
		super.mouseReleased(x, y, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		mouseUp((int) mouseX, (int) mouseY, button);
		return false;
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		scroll((int) f);
		return super.mouseScrolled(d, e, f);
	}

	@Shadow
	public abstract void scroll(int by);

	@Overwrite
	public void initScreen() {
		super.init();
		mc = (MinecraftClient) client;
//		font = (Font) textRenderer;
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

	@Shadow
	public void onClose() {}

	@Overwrite
	protected final void renderDefaultBackground() {
		renderBackground(SharedObjects.primary2dMatrixStack, 0);
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
	public boolean shouldPause() {
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
