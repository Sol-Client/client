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

@Mixin(targets = "org.lwjgl.opengl.LinuxKeyboard")
public class LinuxKeyboardMixin {

	@Shadow
	private @Final int numlock_mask, modeswitch_mask, caps_lock_mask, shift_lock_mask;

	@Overwrite(remap = false)
	private int getKeycode(long eventp, int eventState) {
		/*
		 * Copyright (c) 2002-2008 LWJGL Project All rights reserved.
		 *
		 * Redistribution and use in source and binary forms, with or without
		 * modification, are permitted provided that the following conditions are met:
		 *
		 * * Redistributions of source code must retain the above copyright notice, this
		 * list of conditions and the following disclaimer.
		 *
		 * * Redistributions in binary form must reproduce the above copyright notice,
		 * this list of conditions and the following disclaimer in the documentation
		 * and/or other materials provided with the distribution.
		 *
		 * * Neither the name of 'LWJGL' nor the names of its contributors may be used
		 * to endorse or promote products derived from this software without specific
		 * prior written permission.
		 *
		 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
		 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
		 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
		 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
		 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
		 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
		 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
		 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
		 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
		 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
		 * POSSIBILITY OF SUCH DAMAGE.
		 */
		boolean shift = (eventState & (1 | shift_lock_mask)) != 0;
		int group = (eventState & modeswitch_mask) != 0 ? 1 : 0;
		long keysym;
		if ((eventState & numlock_mask) != 0 && isKeypadKeysym(keysym = getKeySym(eventp, group, 1))) {
			if (shift)
				keysym = getKeySym(eventp, group, 0);
		} else {
			keysym = getKeySym(eventp, group, 0);
			if (shift ^ ((eventState & caps_lock_mask) != 0))
				keysym = toUpper(keysym);
		}
		return LinuxKeycodesAccessor.mapKeySymToLWJGLKeyCode(keysym);
	}

	@Shadow
	private static boolean isKeypadKeysym(long keysym) {
		throw new UnsupportedOperationException();
	}

	@Shadow
	private static long getKeySym(long eventp, int group, int index) {
		throw new UnsupportedOperationException();
	}

	@Shadow
	private static native long toUpper(long keysym);

}
