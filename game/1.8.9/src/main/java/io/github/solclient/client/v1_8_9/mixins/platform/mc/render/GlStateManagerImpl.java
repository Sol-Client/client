package io.github.solclient.client.v1_8_9.mixins.platform.mc.render;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.render.GlStateManager;

@Mixin(GlStateManager.class)
public class GlStateManagerImpl {

	// blaze3d?

	@Overwrite(remap = false)
	public static void enableBlend() {
		com.mojang.blaze3d.platform.GlStateManager.enableBlend();
	}

	@Overwrite(remap = false)
	public static void disableBlend() {
		com.mojang.blaze3d.platform.GlStateManager.disableBlend();
	}

	@Overwrite(remap = false)
	public static void enableDepth() {
		com.mojang.blaze3d.platform.GlStateManager.enableDepthTest();
	}

	@Overwrite(remap = false)
	public static void disableDepth() {
		com.mojang.blaze3d.platform.GlStateManager.disableDepthTest();
	}

	@Overwrite(remap = false)
	public static void enableCull() {
		com.mojang.blaze3d.platform.GlStateManager.enableCull();
	}

	@Overwrite(remap = false)
	public static void disableCull() {
		com.mojang.blaze3d.platform.GlStateManager.disableCull();
	}

	@Overwrite(remap = false)
	public static void enableLighting() {
		throw new UnsupportedOperationException();
	}

	@Overwrite(remap = false)
	public static void disableLighting() {
		throw new UnsupportedOperationException();
	}

	@Overwrite(remap = false)
	public static void pushMatrix() {
		com.mojang.blaze3d.platform.GlStateManager.pushMatrix();
	}

	@Overwrite(remap = false)
	public static void popMatrix() {
		com.mojang.blaze3d.platform.GlStateManager.popMatrix();
	}

	@Overwrite(remap = false)
	public static void disableTexture2d() {
		com.mojang.blaze3d.platform.GlStateManager.disableTexture();
	}

	@Overwrite(remap = false)
	public static void enableTexture2d() {
		com.mojang.blaze3d.platform.GlStateManager.enableTexture();
	}

	@Overwrite(remap = false)
	public static void depthMask(boolean mask) {
		com.mojang.blaze3d.platform.GlStateManager.depthMask(mask);
	}

	@Overwrite(remap = false)
	public static void scale(float x, float y, float z) {
		com.mojang.blaze3d.platform.GlStateManager.scalef(x, y, z);
	}

	@Overwrite(remap = false)
	public static void scale(double x, double y, double z) {
		com.mojang.blaze3d.platform.GlStateManager.scaled(x, y, z);
	}

	@Overwrite(remap = false)
	public static void translate(float x, float y, float z) {
		com.mojang.blaze3d.platform.GlStateManager.translatef(x, y, z);
	}

	@Overwrite(remap = false)
	public static void translate(double x, double y, double z) {
		com.mojang.blaze3d.platform.GlStateManager.translated(x, y, z);
	}

	@Overwrite(remap = false)
	public static void colour(float r, float g, float b, float a) {
		com.mojang.blaze3d.platform.GlStateManager.color4f(r, g, b, a);
	}

	@Overwrite(remap = false)
	public static void blendFunction(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
		com.mojang.blaze3d.platform.GlStateManager.blendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
	}

	@Overwrite(remap = false)
	public static void blendFunction(int sfactor, int dfactor) {
		com.mojang.blaze3d.platform.GlStateManager.blendFunc(sfactor, dfactor);
	}

	@Overwrite(remap = false)
	public static void lineWidth(float width) {
		GL11.glLineWidth(width);
	}

	@Overwrite(remap = false)
	public static void resetLineWidth() {
		GL11.glLineWidth(2);
	}

	@Overwrite(remap = false)
	public static void rotate(float angle, float x, float y, float z) {
		com.mojang.blaze3d.platform.GlStateManager.rotatef(angle, x, y, z);
	}

	@Overwrite(remap = false)
	public static void bindTexture(int id) {
		com.mojang.blaze3d.platform.GlStateManager.bindTexture(id);
	}

}
