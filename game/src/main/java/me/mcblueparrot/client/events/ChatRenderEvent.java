package me.mcblueparrot.client.events;

import net.minecraft.client.gui.GuiNewChat;

public class ChatRenderEvent {

    public GuiNewChat chat;
    public int updateCounter;
    public boolean cancelled;

    public ChatRenderEvent(GuiNewChat chat, int updateCounter) {
        this.chat = chat;
        this.updateCounter = updateCounter;
    }

}
