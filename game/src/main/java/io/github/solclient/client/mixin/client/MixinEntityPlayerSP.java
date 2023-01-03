package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.impl.SendChatMessageEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.*;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {

	public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
	}

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void sendChatMessage(String message, CallbackInfo callback) {
		if (Client.INSTANCE.bus.post(new SendChatMessageEvent(message)).cancelled) {
			callback.cancel();
		}
	}

	@Override
	public boolean isWearing(EnumPlayerModelParts part) {
		return Minecraft.getMinecraft().gameSettings.getModelParts().contains(part);
	}

}
