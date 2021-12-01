package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScrollEvent {

	public final int amount;
	public boolean cancelled;

}
