package me.mcblueparrot.client.util.access;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

public interface AccessGuiNewChat {

    List<ChatLine> getDrawnChatLines();

    boolean getIsScrolled();

    int getScrollPos();

}
