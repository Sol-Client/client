package io.github.solclient.client.util;

import io.github.solclient.client.platform.mc.lang.I18n;

public enum Perspective {
	FIRST_PERSON,
	THIRD_PERSON_BACK,
	THIRD_PERSON_FRONT;

	@Override
	public String toString() {
		return I18n.translate("sol_client.perspective." + name().toLowerCase());
	}

}