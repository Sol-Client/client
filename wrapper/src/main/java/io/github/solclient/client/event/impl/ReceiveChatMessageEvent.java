package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.text.Text;

@RequiredArgsConstructor
public class ReceiveChatMessageEvent {

	public final boolean actionBar;
	public final String originalMessage;
    public final Text formattedMessage;
	/**
	 * Whether the event is fired from the replay mod.
	 */
	public final boolean replay;
	public boolean cancelled;
    public Text newMessage = null;

}
