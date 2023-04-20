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

package io.github.solclient.client.mod.impl.core.mixins.client;

import java.nio.ByteBuffer;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexBuffer;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {

	// Thanks Sychic!
	// this is a much better way of doing what we were doing before
	// https://github.com/Sk1erLLC/Patcher/pull/98

	@Inject(method = "data", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexBuffer;bind()V"), cancellable = true)
	public void preventBindingToDeleted(ByteBuffer buffer, CallbackInfo callback) {
		if (id == -1)
			callback.cancel();
	}

	@Shadow
	private int id;

}
