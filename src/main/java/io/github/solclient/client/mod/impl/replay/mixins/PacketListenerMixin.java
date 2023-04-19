package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.recording.packet.PacketListener;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.ReceiveChatMessageEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.util.Formatting;

@Mixin(PacketListener.class)
public class PacketListenerMixin {

	@Inject(method = "save(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
	public void handleChat(Packet<?> packet, CallbackInfo callback) {
		if (packet instanceof ChatMessageS2CPacket) {
			String messageString = Formatting.strip(((ChatMessageS2CPacket) packet).getMessage().asUnformattedString());

			if (EventBus.INSTANCE.post(new ReceiveChatMessageEvent(
					((ChatMessageS2CPacket) packet).getType() == 2, messageString, true)).cancelled)
				callback.cancel();
		}
	}

}