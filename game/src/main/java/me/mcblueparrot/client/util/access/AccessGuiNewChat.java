package me.mcblueparrot.client.util.access;

import java.util.List;

import net.minecraft.client.gui.ChatLine;

public interface AccessGuiNewChat {

    List<ChatLine> getDrawnChatLines();

    boolean getIsScrolled();

    int getScrollPos();

    void clearChat();

}
