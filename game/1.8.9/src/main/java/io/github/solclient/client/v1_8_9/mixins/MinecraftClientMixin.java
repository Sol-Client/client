package io.github.solclient.client.v1_8_9.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.game.*;
import io.github.solclient.client.event.impl.world.level.LevelLoadEvent;
import io.github.solclient.client.platform.mc.world.level.ClientLevel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@ModifyConstant(method = "setPixelFormat", constant = @Constant(stringValue = "Minecraft 1.8.9"))
	public String getTitle(String title) {
		return Constants.NAME + " | " + title;
	}

	@Inject(method = "initializeGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;options:Lnet/minecraft/client/options/GameOptions;", shift = At.Shift.AFTER, ordinal = 0))
	public void init(CallbackInfo callback) {
		Client.INSTANCE.init();
	}

	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	public void postStart(CallbackInfo callback) {
		EventBus.DEFAULT.post(new PostStartEvent());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void preTick(CallbackInfo callback) {
		EventBus.DEFAULT.post(new PreTickEvent());
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void postTick(CallbackInfo callback) {
		EventBus.DEFAULT.post(new PostTickEvent());
	}

	@Inject(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At("HEAD"))
	private void loadWorld(ClientWorld world, String loadingMessage, CallbackInfo callback) {
		EventBus.DEFAULT.post(new LevelLoadEvent((ClientLevel) world));
	}

	@Inject(method = "getMaxFramerate", at = @At("HEAD"), cancellable = true)
	public void getMaxFramerate(CallbackInfoReturnable<Integer> callback) {
		if(currentScreen instanceof Screen && world == null) {
			// limit to 60 instead of 30
			// it should be perfectly acceptable
			callback.setReturnValue(60);
		}
	}

	@Shadow
	public Screen currentScreen;

	@Shadow
	public ClientWorld world;

}
