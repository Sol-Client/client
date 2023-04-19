package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.replay.events.RenderHotbarCallback;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
	public void skipHotbar(Window window, float partialTicks, CallbackInfo callback) {
		if (!SCReplayMod.enabled)
			return;

		if (RenderHotbarCallback.EVENT.invoker().shouldRenderHotbar() == Boolean.FALSE)
			callback.cancel();
	}

}
