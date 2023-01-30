package io.github.solclient.client.mixin.lwjgl;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "org.lwjgl.opengl.WindowsDisplay")
public abstract class WindowsDisplayMixin {

	@Inject(method = "doHandleMessage", at = @At("HEAD"), cancellable = true, remap = false)
	private void doHandleMessage(long hwnd, int msg, long wParam, long lParam, long millis,
			CallbackInfoReturnable<Long> callback) {
		if (msg == 0x020B) {
			handleMouseButton((wParam >> 16) == 1L ? 3 : 4, 1, millis);
			callback.setReturnValue(1L);
		}
	}

	@Shadow
	private static native long defWindowProc(long hwnd, int msg, long wParam, long lParam);

	@Shadow
	protected abstract void handleMouseButton(int button, int state, long millis);

}