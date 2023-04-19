package io.github.solclient.client.mod.impl.v1_7visuals.mixins;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.mod.impl.v1_7visuals.V1_7VisualsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	private float eyeHeightSubtractor;
	private long lastEyeHeightUpdate;

	// this code makes me long for spaghetti
	@Redirect(method = "transformCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
	public float smoothSneaking(Entity entity) {
		if (V1_7VisualsMod.enabled && V1_7VisualsMod.instance.sneaking && entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			float height = player.getEyeHeight();
			if (player.isSneaking()) {
				height += 0.08F;
			}
			float actualEyeHeightSubtractor = player.isSneaking() ? 0.08F : 0;
			long sinceLastUpdate = System.currentTimeMillis() - lastEyeHeightUpdate;
			lastEyeHeightUpdate = System.currentTimeMillis();
			if (actualEyeHeightSubtractor > eyeHeightSubtractor) {
				eyeHeightSubtractor += sinceLastUpdate / 500f;
				if (actualEyeHeightSubtractor < eyeHeightSubtractor) {
					eyeHeightSubtractor = actualEyeHeightSubtractor;
				}
			} else if (actualEyeHeightSubtractor < eyeHeightSubtractor) {
				eyeHeightSubtractor -= sinceLastUpdate / 500f;
				if (actualEyeHeightSubtractor > eyeHeightSubtractor) {
					eyeHeightSubtractor = actualEyeHeightSubtractor;
				}
			}
			return height - eyeHeightSubtractor;
		}
		return entity.getEyeHeight();
	}

	@Shadow
	private /* why you not final :( */ MinecraftClient client;

}
