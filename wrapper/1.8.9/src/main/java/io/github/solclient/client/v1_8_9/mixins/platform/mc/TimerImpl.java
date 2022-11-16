package io.github.solclient.client.v1_8_9.mixins.platform.mc;

import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.Timer;
import net.minecraft.client.render.ClientTickTracker;

@Mixin(ClientTickTracker.class)
public class TimerImpl implements Timer {

	@Override
	public float getTickDelta() {
		return tickDelta;
	}

	@Shadow
	public float tickDelta;

}
