package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.replaymod.core.events.*;
import com.replaymod.lib.de.johni0702.minecraft.gui.versions.MatrixStack;

import io.github.solclient.client.mod.impl.replay.SCReplayMod;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
	public void skipRenderHand(float partialTicks, int xOffset, CallbackInfo callback) {
		if (!SCReplayMod.enabled)
			return;

		if (PreRenderHandCallback.EVENT.invoker().preRenderHand())
			callback.cancel();
	}

	@Inject(method = "renderWorld(IFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;"
			+ "swap(Ljava/lang/String;)V", ordinal = 18, shift = At.Shift.BEFORE))
	public void postRenderWorld(int pass, float partialTicks, long finishTimeNano, CallbackInfo callback) {
		if (!SCReplayMod.enabled)
			return;

		PostRenderWorldCallback.EVENT.invoker().postRenderWorld(new MatrixStack());
	}

}
