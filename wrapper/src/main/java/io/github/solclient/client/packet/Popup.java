package io.github.solclient.client.packet;

import lombok.*;

@RequiredArgsConstructor
public class Popup {

	@Getter
	private final String text;
	@Getter
	private final String command;
	@Getter
	private final int time;
	@Getter
	private long startTime;

	public void setTime() {
		this.startTime = System.currentTimeMillis();
	}

}
