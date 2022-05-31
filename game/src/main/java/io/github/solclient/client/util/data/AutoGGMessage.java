package io.github.solclient.client.util.data;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AutoGGMessage {
	GG("gg"),
	GF("gf"),
	GOOD_GAME("good game"),
	GOOD_FIGHT("good fight");

	private final String message;

	@Override
	public String toString() {
		return message;
	}

}
