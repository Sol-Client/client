package io.github.solclient.client.packet;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Popup {

	private final String text;
	private final String command;
	private long time;

	public void setTime() {
		this.time = System.currentTimeMillis();
	}

}
