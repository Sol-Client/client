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

package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.SoundPlayEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

	@Shadow
	private @Final MinecraftClient client;

	@Inject(method = "playSound(DDDLjava/lang/String;FFZ)V", at = @At(value = "HEAD"), cancellable = true)
	public void handlePlaySound(double x, double y, double z, String soundName, float volume, float pitch,
			boolean distanceDelay, CallbackInfo callback) {
		SoundPlayEvent event = Client.INSTANCE.getEvents()
				.post(new SoundPlayEvent(soundName, volume, pitch, volume, pitch));
		if (event.pitch != event.originalPitch || event.volume != event.originalVolume) {
			callback.cancel();

			volume = event.volume;
			pitch = event.pitch;
			double distanceSq = this.client.getCameraEntity().squaredDistanceTo(x, y, z);
			PositionedSoundInstance sound = new PositionedSoundInstance(new Identifier(soundName), volume, pitch,
					(float) x, (float) y, (float) z);

			if (distanceDelay && distanceSq > 100.0D)
				client.getSoundManager().play(sound, (int) (Math.sqrt(distanceSq) / 40.0D * 20.0D));
			else
				client.getSoundManager().play(sound);
		}
	}

}
