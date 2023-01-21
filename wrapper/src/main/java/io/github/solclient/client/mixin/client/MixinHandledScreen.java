package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * @reason Allow mouse hotkeys in container GUI.
 */
@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen {

	@Inject(method = "mouseClicked(III)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;cancelNextRelease:Z", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
	public void allowMouseInput(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
		if (handleHotbarKeyPressed(mouseButton - 100))
			callback.cancel();
	}

	@Shadow
	protected abstract boolean handleHotbarKeyPressed(int keyCode);

}
