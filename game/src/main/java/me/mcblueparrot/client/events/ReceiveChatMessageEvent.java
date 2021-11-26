package me.mcblueparrot.client.events;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReceiveChatMessageEvent {

	public final boolean actionBar;
	public final String message;
	public boolean cancelled;

}
