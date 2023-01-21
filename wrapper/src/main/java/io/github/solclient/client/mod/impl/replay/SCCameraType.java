package io.github.solclient.client.mod.impl.replay;

import net.minecraft.client.resource.language.I18n;

public enum SCCameraType {
	CLASSIC, VANILLA_ISH;

	@Override
	public String toString() {
		return I18n.translate("sol_client.mod.replay.option.camera." + name().toLowerCase());
	}

}
