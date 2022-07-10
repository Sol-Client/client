package io.github.solclient.client.platform.mc.option;

import io.github.solclient.client.platform.VirtualEnum;

public interface Perspective extends VirtualEnum {

	Perspective FIRST_PERSON = null,
			THIRD_PERSON_BACK = null,
			THIRD_PERSON_FRONT = null;

	boolean isFirstPerson();

	boolean isThirdPerson();

	boolean isReversed();

}
