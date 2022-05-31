package io.github.solclient.client.mod.impl.replay;

import com.replaymod.replaystudio.util.I18n;

import lombok.AllArgsConstructor;

public enum SCInterpolatorType {
	CATMULL,
	CUBIC,
	LINEAR;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.replay.interpolator." + name().toLowerCase());
	}

}
