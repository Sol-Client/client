package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.tweaks.*;
import net.minecraft.client.MouseInput;

@Mixin(MouseInput.class)
public class MouseInputMixin {

	@Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
	public void applyRawInput(CallbackInfo callback) {
		if (!(TweaksMod.enabled && TweaksMod.instance.rawInput))
			return;
		if (!Mouse.isGrabbed())
			return;
		if (!TweaksMod.instance.getRawInputManager().isAvailable())
			return;

		callback.cancel();
		RawInput input = TweaksMod.instance.getRawInputManager();
		x = (int) input.getDx();
		y = (int) -input.getDy();
		input.reset();
	}

	@Shadow
	public int x;
	@Shadow
	public int y;

}
