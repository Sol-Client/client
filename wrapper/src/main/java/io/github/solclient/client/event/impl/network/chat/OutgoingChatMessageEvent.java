package io.github.solclient.client.event.impl.network.chat;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.event.Cancellable;
import lombok.*;

/**
 * Called when the player sends a message.
 */
@Data
@RequiredArgsConstructor
public class OutgoingChatMessageEvent implements Cancellable {

	private @NotNull String message;
	private boolean cancelled;

}
