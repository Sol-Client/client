package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.SendChatMessageEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.world.World;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

	public MixinClientPlayerEntity(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void sendChatMessage(String message, CallbackInfo callback) {
		if (Client.INSTANCE.getEvents().post(new SendChatMessageEvent(message)).cancelled) {
			callback.cancel();
		}
	}

	@Override
	public boolean isPartVisible(PlayerModelPart part) {
		return MinecraftClient.getInstance().options.getEnabledPlayerModelParts().contains(part);
	}

}
