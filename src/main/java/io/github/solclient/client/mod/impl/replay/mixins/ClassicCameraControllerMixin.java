package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import com.replaymod.replay.camera.ClassicCameraController;

@Mixin(ClassicCameraController.class)
public class ClassicCameraControllerMixin {

	private static final double SPEED_MODIFIER = 1;

	@ModifyConstant(method = "decreaseSpeed", constant = @Constant(doubleValue = 0.00999), remap = false)
	public double getDecreaseSpeedModifier(double original) {
		return SPEED_MODIFIER;
	}

	@ModifyConstant(method = "increaseSpeed", constant = @Constant(doubleValue = 0.00999), remap = false)
	public double getIncreaseSpeedModifier(double original) {
		return SPEED_MODIFIER;
	}

}
