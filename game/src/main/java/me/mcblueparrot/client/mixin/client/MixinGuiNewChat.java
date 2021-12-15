package me.mcblueparrot.client.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.ChatRenderEvent;
import me.mcblueparrot.client.mod.impl.hud.chat.ChatMod;
import me.mcblueparrot.client.util.access.AccessGuiNewChat;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat implements AccessGuiNewChat {

	@Inject(at = @At("HEAD"), cancellable = true, method = "drawChat")
	public void drawChat(int updateCounter, CallbackInfo callback) {
		if (Client.INSTANCE.bus.post(new ChatRenderEvent((GuiNewChat) (Object) /* hax */ this, updateCounter,
				AccessMinecraft.getInstance().getTimerSC().renderPartialTicks)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = "printChatMessage(Lnet/minecraft/util/IChatComponent;)V")
	public void allowNullMessage(IChatComponent component, CallbackInfo callback) {
		if(component == null) {
			callback.cancel();
		}
	}

	@Redirect(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
	public int getSize(List instance) {
		if(ChatMod.enabled && ChatMod.instance.infiniteChat) {
			return 0;
		}

		return instance.size();
	}

	@Override
	@Accessor
	public abstract List<ChatLine> getDrawnChatLines();

	@Override
	@Accessor
	public abstract boolean getIsScrolled();

	@Override
	@Accessor
	public abstract int getScrollPos();

	@Override
	public void clearChat() {
		drawnChatLines.clear();
		chatLines.clear();
	}

	@Shadow
	@Final
	private List<ChatLine> drawnChatLines;

	@Shadow
	@Final
	private List<ChatLine> chatLines;

}
