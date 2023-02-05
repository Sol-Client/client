/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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