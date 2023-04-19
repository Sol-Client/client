package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import com.replaymod.replay.camera.VanillaCameraController;

@Mixin(VanillaCameraController.class)
public class VanillaCameraControllerMixin {

	private static final int SPEED_MODIFIER = 64;

	@ModifyConstant(method = "decreaseSpeed", constant = @Constant(intValue = 1), remap = false)
	public int getDecreaseSpeedModifier(int original) {
		return SPEED_MODIFIER;
	}

	@ModifyConstant(method = "increaseSpeed", constant = @Constant(intValue = 1), remap = false)
	public int getIncreaseSpeedModifier(int original) {
		return SPEED_MODIFIER;
	}

}
