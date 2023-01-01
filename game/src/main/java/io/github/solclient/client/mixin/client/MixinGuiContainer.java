package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.inventory.GuiContainer;

/**
 * @reason Allow mouse hotkeys in container GUI.
 */
@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

	@Inject(method = "mouseClicked(III)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;ignoreMouseUp:Z"), cancellable = true)
	public void allowMouseInput(int mouseX, int mouseY, int mouseButton, CallbackInfo callback) {
		if (checkHotbarKeys(mouseButton - 100)) {
			callback.cancel();
		}
	}

	@Shadow
	protected abstract boolean checkHotbarKeys(int keyCode);

}
