package io.github.solclient.client.v1_8_9.platform.mc.option;

import io.github.solclient.client.platform.VirtualEnum;
import io.github.solclient.client.platform.mc.option.Perspective;

public enum PerspectiveImpl implements Perspective {
	FIRST_PERSON,
	THIRD_PERSON_BACK,
	THIRD_PERSON_FRONT;

	@Override
	public String enumName() {
		return name();
	}

	@Override
	public int enumOrdinal() {
		return ordinal();
	}

	@Override
	public Enum<?> toEnum() {
		return this;
	}

	@Override
	public VirtualEnum[] getValues() {
		return values();
	}

	@Override
	public Enum<?>[] getEnumValues() {
		return values();
	}

	@Override
	public boolean isFirstPerson() {
		return this == FIRST_PERSON;
	}

	@Override
	public boolean isThirdPerson() {
		return this != FIRST_PERSON;
	}

	@Override
	public boolean isReversed() {
		return this == THIRD_PERSON_FRONT;
	}

}
