package io.github.solclient.client.util;

import net.minecraft.client.resources.I18n;

public enum Perspective {
	FIRST_PERSON,
	THIRD_PERSON_BACK,
	THIRD_PERSON_FRONT;

	@Override
	public String toString() {
		return I18n.format("sol_client.perspective." + name().toLowerCase());
	}

}