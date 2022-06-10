package io.github.solclient.client.event.impl.chat;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.api.Cancellable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Called when the player sends a message.
 */
@Data
@RequiredArgsConstructor
public class ChatMessageOutgoingEvent implements Cancellable {

	private @NotNull String message;
	private boolean cancelled;

}
