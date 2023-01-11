package io.github.solclient.client.chatextensions;

import java.util.*;

import io.github.solclient.client.chatextensions.channel.ChatChannelSystem;
import lombok.*;

public final class ChatExtensionManager {

	@Getter
	@Setter
	private ChatChannelSystem channelSystem;

	private boolean sortingDirty;
	private final List<ChatButton> buttons = new LinkedList<>();

	public void registerButton(ChatButton button) {
		buttons.add(button);
		// we could use insersion sort... but me be very lazy today zzzz
		sortingDirty = true;
	}

	public void unregisterButton(ChatButton button) {
		buttons.remove(button);
	}

	public List<ChatButton> getButtons() {
		if (sortingDirty) {
			sortingDirty = false;
			buttons.sort(Comparator.comparingInt(ChatButton::getPriority));
		}

		return buttons;
	}

}
