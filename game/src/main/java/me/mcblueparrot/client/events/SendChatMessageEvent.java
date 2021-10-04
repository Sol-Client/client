package me.mcblueparrot.client.events;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SendChatMessageEvent {

    public final String message;
    public boolean cancelled;

}
