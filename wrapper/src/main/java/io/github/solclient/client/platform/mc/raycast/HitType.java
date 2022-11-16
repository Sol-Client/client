package io.github.solclient.client.platform.mc.raycast;

import io.github.solclient.client.platform.VirtualEnum;

public interface HitType extends VirtualEnum {

	HitType MISS = null,
			ENTITY = null,
			BLOCK = null;

}
