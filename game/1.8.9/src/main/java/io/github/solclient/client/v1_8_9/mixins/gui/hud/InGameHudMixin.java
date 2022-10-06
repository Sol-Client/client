package io.github.solclient.client.v1_8_9.mixins.gui.hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.event.EventBus;
import io.github.solclient.client.event.impl.hud.PostHudRenderEvent;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(method = "render", at = @At("TAIL"))
	public void preRender(CallbackInfo callback) {
		EventBus.DEFAULT.post(new PostHudRenderEvent());
	}

}
