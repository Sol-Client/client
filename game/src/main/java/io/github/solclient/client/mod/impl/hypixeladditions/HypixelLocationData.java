package io.github.solclient.client.mod.impl.hypixeladditions;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class HypixelLocationData {

	@SerializedName("gametype")
	private String type;
	private String map;
	private String server;
	private String mode;

}
