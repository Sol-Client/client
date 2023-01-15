package io.github.solclient.client.mod.impl.replay;

import net.minecraft.client.resource.language.I18n;

public enum SCInterpolatorType {
	CATMULL, CUBIC, LINEAR;

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.replay.interpolator." + name().toLowerCase());
	}

}
