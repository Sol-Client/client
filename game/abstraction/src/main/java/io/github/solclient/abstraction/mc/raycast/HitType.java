package io.github.solclient.abstraction.mc.raycast;

import io.github.solclient.abstraction.VirtualEnum;

public interface HitType extends VirtualEnum {

	HitType MISS = null;
	HitType ENTITY = null;
	HitType BLOCK = null;

}
