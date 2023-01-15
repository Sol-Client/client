package io.github.solclient.client.mod.impl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;

public class HitboxMod extends Mod {

	@Option
	private final KeyBinding toggleHitboxes = new KeyBinding(getTranslationKey() + ".option.toggleHitboxes", 0,
			GlobalConstants.KEY_CATEGORY);
	@Expose
	@Option
	private boolean boundingBox = true;
	@Expose
	@Option
	private Colour boundingBoxColour = Colour.WHITE;
	@Expose
	@Option
	private boolean eyeHeight = true;
	@Expose
	@Option
	private Colour eyeHeightColour = Colour.PURE_RED;
	@Expose
	@Option
	private boolean lookVector = true;
	@Expose
	@Option
	private Colour lookVectorColour = Colour.PURE_BLUE;
	@Expose
	@Option
	@Slider(min = 1, max = 10, step = 0.5F)
	private float lineWidth = 2;
	@Expose
	private boolean toggled;

	@Override
	public void postStart() {
		super.postStart();
		if (isEnabled())
			mc.getEntityRenderManager().setRenderHitboxes(toggled);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		if (mc.getEntityRenderManager() != null)
			toggled = mc.getEntityRenderManager().getRenderHitboxes();

		while (toggleHitboxes.isPressed())
			;
	}

	@Override
	public String getId() {
		return "hitbox";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	@EventHandler
	public void onHitboxRender(HitboxRenderEvent event) {
		event.cancelled = true;
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GL11.glLineWidth(lineWidth);

		float half = event.entity.width / 2.0F;

		if (boundingBox) {
			Box box = event.entity.getBoundingBox();
			Box offsetBox = new Box(box.minX - event.entity.x + event.x, box.minY - event.entity.y + event.y,
					box.minZ - event.entity.z + event.z, box.maxX - event.entity.x + event.x,
					box.maxY - event.entity.y + event.y, box.maxZ - event.entity.z + event.z);
			WorldRenderer.drawBox(offsetBox, boundingBoxColour.getRed(), boundingBoxColour.getGreen(),
					boundingBoxColour.getBlue(), boundingBoxColour.getAlpha());
		}

		if (eyeHeight && event.entity instanceof LivingEntity) {
			WorldRenderer.drawBox(
					new Box(event.x - half, event.y + event.entity.getEyeHeight() - 0.009999999776482582D,
							event.z - half, event.x + half,
							event.y + event.entity.getEyeHeight() + 0.009999999776482582D, event.z + half),
					eyeHeightColour.getRed(), eyeHeightColour.getGreen(), eyeHeightColour.getBlue(),
					eyeHeightColour.getAlpha());
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		if (lookVector) {
			Vec3d look = event.entity.getRotationVector(event.partialTicks);
			buffer.begin(3, VertexFormats.POSITION_COLOR);
			buffer.vertex(event.x, event.y + event.entity.getEyeHeight(), event.z).color(0, 0, 255, 255).end();
			buffer.vertex(event.x + look.x * 2, event.y + event.entity.getEyeHeight() + look.y * 2,
					event.z + look.z * 2)
					.color(lookVectorColour.getRed(), lookVectorColour.getGreen(), lookVectorColour.getBlue(),
							lookVectorColour.getAlpha())
					.end();
			tessellator.draw();
		}

		GlStateManager.enableTexture();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
		Utils.resetLineWidth();
	}

	@EventHandler
	public void onHitboxToggle(HitboxToggleEvent event) {
		toggled = event.state;
		Client.INSTANCE.save();
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		while (toggleHitboxes.isPressed()) {
			// If debug shortcut is used, don't conflict.
			if (toggleHitboxes.getCode() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
				continue;
			}

			toggled = !mc.getEntityRenderManager().getRenderHitboxes();
			mc.getEntityRenderManager().setRenderHitboxes(toggled);
			Client.INSTANCE.save();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
