package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.core.events.KeyBindingEventCallback;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(IZ)V"))
	public void keyPressEvent(CallbackInfo callback) {
		if (!SCReplayMod.enabled)
			return;

		KeyBindingEventCallback.EVENT.invoker().onKeybindingEvent();
	}

}
