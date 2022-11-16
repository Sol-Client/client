package io.github.solclient.client.v1_19_2.mixins.gui.hud;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.hud.PreHudElementRenderEvent;
import io.github.solclient.client.util.VanillaHudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(ChatHud.class)
public class ChatHudMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void preRender(MatrixStack matrices, int ticks, CallbackInfo callback) {
		if(EventBus.DEFAULT
				.post(new PreHudElementRenderEvent(VanillaHudElement.CHAT,
						((io.github.solclient.client.platform.mc.MinecraftClient) client).getTimer().getTickDelta()))
				.isCancelled()) {
			callback.cancel();
		}
	}

	@Shadow
	private @Final MinecraftClient client;

}
