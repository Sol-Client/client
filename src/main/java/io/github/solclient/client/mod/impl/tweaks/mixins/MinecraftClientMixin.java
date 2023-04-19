package io.github.solclient.client.mod.impl.tweaks.mixins;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.mod.impl.tweaks.TweaksMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(method = "closeScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseInput;lockMouse()V"))
	public void afterLock(CallbackInfo callback) {
		if (TweaksMod.enabled && TweaksMod.instance.betterKeyBindings) {
			for (KeyBinding keyBinding : options.allKeys) {
				try {
					KeyBinding.setKeyPressed(keyBinding.getCode(),
							keyBinding.getCode() < 256 && Keyboard.isKeyDown(keyBinding.getCode())); // TODO
																										// modifier
																										// support
				} catch (IndexOutOfBoundsException ignored) {
				}
			}
		}
	}

	@Shadow
	public GameOptions options;

}
