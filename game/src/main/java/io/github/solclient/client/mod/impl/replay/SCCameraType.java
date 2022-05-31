package io.github.solclient.client.mod.impl.replay;

import lombok.AllArgsConstructor;
import net.minecraft.client.resources.I18n;

public enum SCCameraType {
	CLASSIC,
	VANILLA_ISH;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.replay.option.camera." + name().toLowerCase());
	}

}
