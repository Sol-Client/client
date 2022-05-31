package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.EntityDamageEvent;
import io.github.solclient.client.event.impl.ReceiveChatMessageEvent;
import io.github.solclient.client.util.access.AccessGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

	@Shadow
	private Minecraft gameController;

	@Shadow
	private WorldClient clientWorldController;

	@Inject(method = "handleCustomPayload", at = @At("RETURN"))
	public void handleCustomPayload(S3FPacketCustomPayload payload, CallbackInfo callback) {
		Client.INSTANCE.bus.post(payload); // Post as normal event object
	}

	@Inject(method = "handleEntityStatus", at = @At("RETURN"))
	public void handleEntityStatus(S19PacketEntityStatus packetIn, CallbackInfo callback) {
		if(packetIn.getOpCode() == 2) {
			Client.INSTANCE.bus.post(new EntityDamageEvent(packetIn.getEntity(clientWorldController)));
		}
	}

	@Redirect(method = "handleChat", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiNewChat;printChatMessage(Lnet/minecraft/util/IChatComponent;)V"))
	public void handleChat(GuiNewChat guiNewChat, IChatComponent chatComponent) {
		if(!Client.INSTANCE.bus.post(new ReceiveChatMessageEvent(false,
				EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText()), false)).cancelled) {
			guiNewChat.printChatMessage(chatComponent);
		}
	}

	@Redirect(method = "handleChat", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiIngame;setRecordPlaying(Lnet/minecraft/util/IChatComponent;Z)V"))
	public void handleActionBar(GuiIngame guiIngame, IChatComponent component, boolean isPlaying) {
		if(!Client.INSTANCE.bus.post(new ReceiveChatMessageEvent(true,
				EnumChatFormatting.getTextWithoutFormattingCodes(component.getUnformattedText()), false)).cancelled) {
			guiIngame.setRecordPlaying(component, isPlaying);
		}
	}

	@Inject(method = "handleCloseWindow", at = @At("HEAD"), cancellable = true)
	public void handleCloseWindow(S2EPacketCloseWindow packetIn, CallbackInfo callback) {
		if(gameController.currentScreen != null && !(((AccessGuiScreen) gameController.currentScreen).canBeForceClosed() || gameController.currentScreen instanceof GuiContainer)) {
			callback.cancel();
		}
	}

}
