package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.impl.CameraRotateEvent;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(ActiveRenderInfo.class)
public class MixinActiveRenderInfo {

	// region Rotate Camera Event

	private static float rotationYaw;
	private static float prevRotationYaw;
	private static float rotationPitch;
	private static float prevRotationPitch;

	@Inject(method = "updateRenderInfo", at = @At("HEAD"))
	private static void orientCamera(EntityPlayer entityplayerIn, boolean reverseView, CallbackInfo ci) {
		rotationYaw = entityplayerIn.rotationYaw;
		prevRotationYaw = entityplayerIn.prevRotationYaw;
		rotationPitch = entityplayerIn.rotationPitch;
		prevRotationPitch = entityplayerIn.prevRotationPitch;

		CameraRotateEvent event = Client.INSTANCE.bus.post(new CameraRotateEvent(rotationYaw, rotationPitch, 0));
		rotationYaw = event.yaw;
		rotationPitch = event.pitch;

		event = Client.INSTANCE.bus.post(new CameraRotateEvent(prevRotationYaw, prevRotationPitch, 0));
		prevRotationYaw = event.yaw;
		prevRotationPitch = event.pitch;
	}

	@Redirect(method = "updateRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;rotationYaw:F"))
	private static float getRotationYaw(EntityPlayer entity) {
		return rotationYaw;
	}

	@Redirect(method = "updateRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;rotationPitch:F"))
	private static float getRotationPitch(EntityPlayer entity) {
		return rotationPitch;
	}

	// endregion

}
