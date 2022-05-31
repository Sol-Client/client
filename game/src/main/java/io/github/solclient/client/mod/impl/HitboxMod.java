package io.github.solclient.client.mod.impl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.HitboxRenderEvent;
import io.github.solclient.client.event.impl.HitboxToggleEvent;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class HitboxMod extends Mod {

	@Option
	private final KeyBinding toggleHitboxes = new KeyBinding(getTranslationKey() + ".key", 0, Client.KEY_CATEGORY);
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
	public void onRegister() {
		super.onRegister();
		Client.INSTANCE.registerKeyBinding(toggleHitboxes);
	}

	@Override
	public void postStart() {
		super.postStart();
		if(isEnabled()) {
			mc.getRenderManager().setDebugBoundingBox(toggled);
		}
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		if(mc.getRenderManager() != null) {
			toggled = mc.getRenderManager().isDebugBoundingBox();
		}
		while(toggleHitboxes.isPressed());
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
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GL11.glLineWidth(lineWidth);

		float half = event.entity.width / 2.0F;

		if(boundingBox) {
			AxisAlignedBB box = event.entity.getEntityBoundingBox();
			AxisAlignedBB offsetBox = new AxisAlignedBB(box.minX - event.entity.posX + event.x,
					box.minY - event.entity.posY + event.y, box.minZ - event.entity.posZ + event.z,
					box.maxX - event.entity.posX + event.x, box.maxY - event.entity.posY + event.y,
					box.maxZ - event.entity.posZ + event.z);
			RenderGlobal.drawOutlinedBoundingBox(offsetBox, boundingBoxColour.getRed(), boundingBoxColour.getGreen(), boundingBoxColour.getBlue(), boundingBoxColour.getAlpha());
		}

		if(eyeHeight && event.entity instanceof EntityLivingBase) {
			RenderGlobal.drawOutlinedBoundingBox(
					new AxisAlignedBB(event.x - half, event.y + event.entity.getEyeHeight() - 0.009999999776482582D,
							event.z - half, event.x + half,
							event.y + event.entity.getEyeHeight() + 0.009999999776482582D, event.z + half),
					eyeHeightColour.getRed(), eyeHeightColour.getGreen(), eyeHeightColour.getBlue(),
					eyeHeightColour.getAlpha());
		}

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		if(lookVector) {
			Vec3 look = event.entity.getLook(event.partialTicks);
			worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
			worldrenderer.pos(event.x, event.y + event.entity.getEyeHeight(), event.z).color(0, 0, 255, 255)
					.endVertex();
			worldrenderer.pos(event.x + look.xCoord * 2,
					event.y + event.entity.getEyeHeight() + look.yCoord * 2, event.z + look.zCoord * 2)
					.color(lookVectorColour.getRed(), lookVectorColour.getGreen(), lookVectorColour.getBlue(), lookVectorColour.getAlpha()).endVertex();
			tessellator.draw();
		}

		GlStateManager.enableTexture2D();
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
		while(toggleHitboxes.isPressed()) {
			// If debug shortcut is used, don't conflict.
			if(toggleHitboxes.getKeyCode() == Keyboard.KEY_B && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
				continue;
			}

			toggled = !mc.getRenderManager().isDebugBoundingBox();
			mc.getRenderManager().setDebugBoundingBox(toggled);
			Client.INSTANCE.save();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
