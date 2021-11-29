package me.mcblueparrot.client.event.impl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SendChatMessageEvent {

	public final String message;
	public boolean cancelled;

}
