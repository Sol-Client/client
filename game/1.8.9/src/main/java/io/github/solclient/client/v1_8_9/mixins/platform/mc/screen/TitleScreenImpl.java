package io.github.solclient.client.v1_8_9.mixins.platform.mc.screen;

import io.github.solclient.client.platform.mc.screen.TitleScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screen.TitleScreen.class)
public abstract class TitleScreenImpl extends Screen implements TitleScreen {

	private boolean earlyExit;

	@Override
	public void renderTitlePanorama(int x, int y, float tickDelta) {
		earlyExit = true;
		render(x, y, tickDelta);
		earlyExit = false;
	}

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/TitleScreen;client:Lnet/minecraft/client/MinecraftClient;", ordinal = 0), cancellable = true)
	public void renderEarlyExit(int mouseX, int mouseY, float tickDelta, CallbackInfo callback) {
		if(earlyExit) {
			// hacky method
			callback.cancel();
		}
	}

}
