package io.github.solclient.client.event.impl.sound;

import io.github.solclient.client.platform.mc.sound.SoundType;
import lombok.Data;

@Data
public final class SoundPlayEvent {

	private SoundType type;
	private float volume;

	public void multiplyVolume(float by) {
		volume *= by;
	}

	public void divideVolume(float by) {
		volume /= by;
	}

}
