package me.mcblueparrot.client.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Popup {

	@Getter
	private final String text;
	@Getter
	private final String command;
	@Getter
	private long time;

	public void setTime() {
		this.time = System.currentTimeMillis();
	}

}
