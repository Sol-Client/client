package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.SoundPlayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;

@Mixin(WorldClient.class)
public class MixinWorldClient {

	@Shadow
	@Final
	private Minecraft mc;

	@Inject(method = "playSound", at = @At(value = "HEAD"), cancellable = true)
	public void handlePlaySound(double x, double y, double z, String soundName, float volume, float pitch,
			boolean distanceDelay, CallbackInfo callback) {
		SoundPlayEvent event = Client.INSTANCE.getEvents().post(new SoundPlayEvent(soundName, volume, pitch, volume, pitch));
		if (event.pitch != event.originalPitch || event.volume != event.originalVolume) {
			callback.cancel();
			volume = event.volume;
			pitch = event.pitch;
			double distanceSq = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
			PositionedSoundRecord positionedsoundrecord = new PositionedSoundRecord(new ResourceLocation(soundName),
					volume, pitch, (float) x, (float) y, (float) z);

			if (distanceDelay && distanceSq > 100.0D) {
				mc.getSoundHandler().playDelayedSound(positionedsoundrecord,
						(int) (Math.sqrt(distanceSq) / 40.0D * 20.0D));
			} else {
				mc.getSoundHandler().playSound(positionedsoundrecord);
			}
		}
	}

}
