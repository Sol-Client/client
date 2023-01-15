package io.github.solclient.client.util;

import net.minecraft.client.resource.language.I18n;

public enum Perspective {
	FIRST_PERSON, THIRD_PERSON_BACK, THIRD_PERSON_FRONT;

	@Override
	public String toString() {
		return I18n.translate("sol_client.perspective." + name().toLowerCase());
	}

}