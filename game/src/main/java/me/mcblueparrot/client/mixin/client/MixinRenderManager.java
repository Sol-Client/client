package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.replaymod.replay.camera.CameraEntity;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.culling.Cullable;
import me.mcblueparrot.client.event.impl.CameraRotateEvent;
import me.mcblueparrot.client.util.access.AccessRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager {

	@SuppressWarnings("unchecked")
	@Inject(method = "doRenderEntity", at = @At("HEAD"), cancellable = true)
	public void cullEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks,
						   boolean hideDebugBox, CallbackInfoReturnable<Boolean> callback) {
		if(entity instanceof CameraEntity) {
			callback.setReturnValue(renderEngine == null);
		}

		if(((Cullable) entity).isCulled()) {
			((AccessRender<Entity>) getEntityRenderObject(entity)).doRenderName(entity, x, y, z);
			callback.setReturnValue(renderEngine == null);
		}
	}

	// region Rotate Camera Event

	private static float rotationYaw;
	private static float prevRotationYaw;
	private static float rotationPitch;
	private static float prevRotationPitch;

	@Inject(method = "cacheActiveRenderInfo", at = @At("HEAD"))
	public void orientCamera(World worldIn, FontRenderer textRendererIn, Entity livingPlayerIn, Entity pointedEntityIn,
							 GameSettings optionsIn, float partialTicks, CallbackInfo callback) {
		rotationYaw = Minecraft.getMinecraft().getRenderViewEntity().rotationYaw;
		prevRotationYaw = Minecraft.getMinecraft().getRenderViewEntity().prevRotationYaw;
		rotationPitch = Minecraft.getMinecraft().getRenderViewEntity().rotationPitch;
		prevRotationPitch = Minecraft.getMinecraft().getRenderViewEntity().prevRotationPitch;

		CameraRotateEvent event = Client.INSTANCE.bus.post(new CameraRotateEvent(rotationYaw, rotationPitch, 0));
		rotationYaw = event.yaw;
		rotationPitch = event.pitch;

		event = Client.INSTANCE.bus.post(new CameraRotateEvent(prevRotationYaw, prevRotationPitch, 0));
		prevRotationYaw = event.yaw;
		prevRotationPitch = event.pitch;
	}

	@Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F"))
	public float getRotationYaw(Entity entity) {
		return rotationYaw;
	}

	@Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F"))
	public float getPrevRotationYaw(Entity entity) {
		return prevRotationYaw;
	}

	@Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;" +
			"rotationPitch:F"))
	public float getRotationPitch(Entity entity) {
		return rotationPitch;
	}

	@Redirect(method = "cacheActiveRenderInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;" +
			"prevRotationPitch:F"))
	public float getPrevRotationPitch(Entity entity) {
		return prevRotationPitch;
	}

	// endregion

	@Shadow
	public TextureManager renderEngine;

	@Shadow
	public abstract <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn);

}
