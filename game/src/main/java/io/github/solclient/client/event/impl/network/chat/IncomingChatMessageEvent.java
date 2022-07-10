package io.github.solclient.client.event.impl.network.chat;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.event.Cancellable;
import io.github.solclient.client.platform.mc.text.Text;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Called when the player receives a chat message.
 */
@Data
@RequiredArgsConstructor
public class IncomingChatMessageEvent implements Cancellable {

	private @NotNull Text message;
	private boolean cancelled;
	@Getter
	private boolean replay;

	public String getPlainText() {
		return message.getPlain();
	}

}
