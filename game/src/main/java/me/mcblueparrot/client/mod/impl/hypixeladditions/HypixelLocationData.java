package me.mcblueparrot.client.mod.impl.hypixeladditions;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import net.hypixel.api.data.type.GameType;

@Data
public class HypixelLocationData {

	@SerializedName("gametype")
	private String type;
	private String map;
	private String server;
	private String mode;

}
