package me.mcblueparrot.client.mixin.client;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.ChatRenderEvent;
import me.mcblueparrot.client.util.access.AccessGuiNewChat;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat implements AccessGuiNewChat {

    @Inject(at = @At("HEAD"), cancellable = true, method = "drawChat")
    public void drawChat(int updateCounter, CallbackInfo callback) {
        if(Client.INSTANCE.bus.post(new ChatRenderEvent((GuiNewChat) (Object) /* hacks */ this, updateCounter)).cancelled) {
            callback.cancel();
        }
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

}
