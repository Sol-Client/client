package io.github.solclient.abstraction.mc.option;

import io.github.solclient.abstraction.VirtualEnum;

public interface Perspective extends VirtualEnum {

	Perspective FIRST_PERSON = null,
			THIRD_PERSON_BACK = null,
			THIRD_PERSON_FRONT = null;

	boolean isFirstPerson();

	boolean isThirdPerson();

	boolean isReversed();

}
