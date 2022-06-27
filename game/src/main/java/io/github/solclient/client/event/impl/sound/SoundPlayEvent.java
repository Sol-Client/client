package io.github.solclient.client.event.impl.sound;

import io.github.solclient.abstraction.mc.sound.SoundType;
import lombok.Data;

@Data
public class SoundPlayEvent {

	private SoundType type;
	private float volume;

	public void multiplyVolume(float by) {
		volume *= by;
	}

	public void divideVolume(float by) {
		volume /= by;
	}

}
