package io.github.solclient.client.mod.impl.itemphysics;

import lombok.Data;

@Data
public class ItemData {

	private long lastUpdate;
	private float rotation;

	public ItemData(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
