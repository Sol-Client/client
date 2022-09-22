package io.github.solclient.client.v1_19_2.mixins.platform.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.Timer;
import net.minecraft.client.render.RenderTickCounter;

@Mixin(RenderTickCounter.class)
public class TimerImpl implements Timer {

	@Override
	public float getTickDelta() {
		return tickDelta;
	}

	@Shadow
	public float tickDelta;

}
