package io.github.solclient.client.util.data;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AutoGLMessage {
	GLHF("glhf"), GL("gl"), GOOD_LUCK("good luck!"), GOOD_LUCK_HAVE_FUN("good luck, have fun!");

	private final String message;

	@Override
	public String toString() {
		return message;
	}

}
