package io.github.solclient.client.v1_19_2.mixins.platform.mc.screen;

import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.platform.mc.screen.TitleScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(net.minecraft.client.gui.screen.TitleScreen.class)
public abstract class TitleScreenImpl extends Screen implements TitleScreen {

	private boolean earlyExit;

	protected TitleScreenImpl(Text text) {
		super(text);
	}

	@Override
	public void renderTitlePanorama(int x, int y, float tickDelta) {
		earlyExit = true;
		render(SharedObjects.primary2dMatrixStack, x, y, tickDelta);
		earlyExit = false;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 0), cancellable = true)
	public void renderEarlyExit(MatrixStack matrixStack, int mouseX, int mouseY, float tickDelta, CallbackInfo callback) {
		if(earlyExit) {
			// hacky method
			callback.cancel();
		}
	}

}
