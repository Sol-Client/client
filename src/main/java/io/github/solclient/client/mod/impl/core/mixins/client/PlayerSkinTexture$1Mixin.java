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

import java.net.HttpURLConnection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.util.GlobalConstants;

@Mixin(targets = "net.minecraft.client.texture.PlayerSkinTexture$1")
public class PlayerSkinTexture$1Mixin {

	@Inject(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/HttpURLConnection;setDoOutput(Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void setUserAgent(CallbackInfo callback, HttpURLConnection connection) {
		// TODO maybe only do this when necessary
		connection.setRequestProperty("User-Agent", GlobalConstants.USER_AGENT);
	}

}
