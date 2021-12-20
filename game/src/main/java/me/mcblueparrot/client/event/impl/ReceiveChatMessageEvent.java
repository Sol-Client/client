package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReceiveChatMessageEvent {

	public final boolean actionBar;
	public final String message;
	/**
	 * Whether the event is fired from the replay mod.
	 */
	public final boolean replay;
	public boolean cancelled;

}
