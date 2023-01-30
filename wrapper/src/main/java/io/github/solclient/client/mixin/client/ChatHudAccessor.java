package io.github.solclient.client.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.hud.*;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {

	@Accessor
	public List<ChatHudLine> getVisibleMessages();

	@Accessor
	public boolean getHasUnreadNewMessages();

	@Accessor
	public int getScrolledLines();

}
