package io.github.solclient.client.v1_19_2.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Redirect(method = "render", at = @At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack", ordinal = 1))
	public MatrixStack sharedMatrixStack() {
		return SharedObjects.primary2dMatrixStack = new MatrixStack();
	}

}
