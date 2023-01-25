package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.extension.ScreenExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.*;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

	@Shadow
	private MinecraftClient client;

	@Shadow
	private ClientWorld world;

	@Inject(method = "onCustomPayload", at = @At("RETURN"))
	public void handleCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo callback) {
		Client.INSTANCE.getEvents().post(packet); // Post as normal event object
	}

	@Inject(method = "onEntityStatus", at = @At("RETURN"))
	public void handleEntityStatus(EntityStatusS2CPacket packet, CallbackInfo callback) {
		if (packet.getStatus() == 2)
			Client.INSTANCE.getEvents().post(new EntityDamageEvent(packet.getEntity(world)));
	}

	@Redirect(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
	public void handleChat(ChatHud instance, Text message) {
		if (!Client.INSTANCE.getEvents().post(
				new ReceiveChatMessageEvent(false, Formatting.strip(message.asUnformattedString()), false)).cancelled) {
			instance.addMessage(message);
		}
	}

	@Redirect(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setOverlayMessage(Lnet/minecraft/text/Text;Z)V"))
	public void handleActionBar(InGameHud instance, Text text, boolean tinted) {
		if (!Client.INSTANCE.getEvents().post(
				new ReceiveChatMessageEvent(true, Formatting.strip(text.asUnformattedString()), false)).cancelled) {
			instance.setOverlayMessage(text, tinted);
		}
	}

	@Inject(method = "onCloseScreen", at = @At("HEAD"), cancellable = true)
	public void handleCloseScreen(CloseScreenS2CPacket packet, CallbackInfo callback) {
		if (client.currentScreen != null && !(((ScreenExtension) client.currentScreen).canBeForceClosed()
				|| client.currentScreen instanceof HandledScreen))
			callback.cancel();
	}

}
