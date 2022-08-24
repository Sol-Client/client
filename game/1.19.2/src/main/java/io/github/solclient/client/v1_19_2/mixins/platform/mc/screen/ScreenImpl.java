package io.github.solclient.client.v1_19_2.mixins.platform.mc.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.screen.Screen;
import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(net.minecraft.client.gui.screen.Screen.class)
public abstract class ScreenImpl extends AbstractParentElement implements Screen {

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
		render(SharedObjects.primary2dMatrixStack, mouseX, mouseY, tickDelta);
	}

	@Shadow
	public abstract void render(MatrixStack stack, int mouseX, int mouseY, float tickDelta);

	@Override
	public void keyDown(int key, int scancode, int mods) {
		keyDown(key, scancode, mods);
	}

	@Override
	public void mouseDown(int x, int y, int button) {
		mouseClicked(x, y, button);
	}

	@Override
	public void mouseUp(int x, int y, int button) {
		mouseReleased(x, y, button);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Shadow
	protected int width;

	@Override
	public int getHeight() {
		return height;
	}

	@Shadow
	protected int height;

	@Override
	public void tickScreen() {
		tick();
	}

	@Shadow
	public abstract void tick();

}
