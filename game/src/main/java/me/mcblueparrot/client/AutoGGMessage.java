package me.mcblueparrot.client;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AutoGGMessage {
	GG("gg"),
	GF("gf"),
	GOOD_GAME("good game"),
	GOOD_FIGHT("good fight");

	private String message;

	@Override
	public String toString() {
		return message;
	}

}
