package io.github.solclient.client.chat;

import org.jetbrains.annotations.Nullable;

public interface ChatAccessor {

	void setSelectedButton(@Nullable ChatButton button);

	@Nullable ChatButton getSelectedButton();

}
