package io.github.solclient.client.mod.impl.hypixeladditions;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public final class HypixelLocationData {

	@SerializedName("gametype")
	private String type;
	private String map, server, mode;

}
