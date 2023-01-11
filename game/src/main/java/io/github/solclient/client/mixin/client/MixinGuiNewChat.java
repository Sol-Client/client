package io.github.solclient.client.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.ChatRenderEvent;
import io.github.solclient.client.mod.impl.hud.chat.ChatMod;
import io.github.solclient.client.util.extension.*;
import net.minecraft.client.gui.*;
import net.minecraft.util.IChatComponent;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat implements GuiNewChatExtension {

	@Inject(at = @At("HEAD"), cancellable = true, method = "drawChat")
	public void drawChat(int updateCounter, CallbackInfo callback) {
		if (Client.INSTANCE.getEvents().post(new ChatRenderEvent((GuiNewChat) (Object) /* hax */ this, updateCounter,
				MinecraftExtension.getInstance().getTimerSC().renderPartialTicks)).cancelled) {
			callback.cancel();
		}
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = "printChatMessage(Lnet/minecraft/util/IChatComponent;)V")
	public void allowNullMessage(IChatComponent component, CallbackInfo callback) {
		if (component == null) {
			callback.cancel();
		}
	}

	@Redirect(method = "setChatLine", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
	public int getSize(List instance) {
		if (ChatMod.enabled && ChatMod.instance.infiniteChat) {
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
