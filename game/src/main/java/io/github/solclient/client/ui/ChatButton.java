package io.github.solclient.client.ui;

import io.github.solclient.client.util.extension.GuiChatExtension;
import net.minecraft.client.Minecraft;

public interface ChatButton {

	int getPriority();

	default int getWidth() {
		return Minecraft.getMinecraft().fontRendererObj.getStringWidth(getText()) + 4;
	}

	int getPopupWidth();

	int getPopupHeight();

	String getText();

	void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX, int mouseY);

	default boolean isOpen() {
		GuiChatExtension chat = GuiChatExtension.getInstance();
		return chat != null && chat.getSelectedChatButton() == this;
	}

}
