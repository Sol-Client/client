package io.github.solclient.client.v1_19_2.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import io.github.solclient.client.Constants;
import io.github.solclient.client.event.impl.game.PostStartEvent;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.game.PreTickEvent;
import io.github.solclient.client.event.impl.world.level.LevelLoadEvent;
import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
	public void setTitle(CallbackInfoReturnable<String> callback) {
		callback.setReturnValue(Constants.NAME + " | Minecraft " + SharedConstants.getGameVersion().getName());
	}

	@Inject(method = "<init>", at = @At(value = "FIELD",
			target = "options:Lnet/minecraft/client/option/GameOptions;", shift = At.Shift.AFTER, ordinal = 0))
	public void init(CallbackInfo callback) {
		Client.INSTANCE.init();
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	public void postStart(CallbackInfo callback) {
		Client.INSTANCE.getBus().post(new PostStartEvent());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void preTick(CallbackInfo callback) {
		Client.INSTANCE.getBus().post(new PreTickEvent());
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void postTick(CallbackInfo callback) {
		Client.INSTANCE.getBus().post(new PostTickEvent());
	}

	@Inject(method = "setWorld", at = @At("HEAD"))
	private void loadWorld(ClientWorld world, CallbackInfo callback) {
		Client.INSTANCE.getBus().post(new LevelLoadEvent((ClientLevel) world));
	}

}
