package io.github.solclient.client.v1_19_2.mixins.platform.mc.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.util.math.Quaternion;

@Mixin(GlStateManager.class)
public class GlStateManagerImpl {

	@Overwrite(remap = false)
	public static void enableBlend() {
		RenderSystem.enableBlend();
	}

	@Overwrite(remap = false)
	public static void disableBlend() {
		RenderSystem.disableBlend();
	}

	@Overwrite(remap = false)
	public static void enableDepth() {
		RenderSystem.enableDepthTest();
	}

	@Overwrite(remap = false)
	public static void disableDepth() {
		RenderSystem.disableDepthTest();
	}

	@Overwrite(remap = false)
	public static void enableCull() {
		RenderSystem.enableCull();
	}

	@Overwrite(remap = false)
	public static void disableCull() {
		RenderSystem.disableCull();
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
		SharedObjects.primary2dMatrixStack.push();
	}

	@Overwrite(remap = false)
	public static void popMatrix() {
		SharedObjects.primary2dMatrixStack.pop();
	}

	@Overwrite(remap = false)
	public static void disableTexture2d() {
		RenderSystem.disableTexture();
	}

	@Overwrite(remap = false)
	public static void enableTexture2d() {
		RenderSystem.disableTexture();
	}

	@Overwrite(remap = false)
	public static void depthMask(boolean mask) {
		RenderSystem.depthMask(mask);
	}

	@Overwrite(remap = false)
	public static void scale(float x, float y, float z) {
		SharedObjects.primary2dMatrixStack.scale(x, y, z);
	}

	@Overwrite(remap = false)
	public static void scale(double x, double y, double z) {
		scale((float) x, y, z);
	}

	@Overwrite(remap = false)
	public static void translate(float x, float y, float z) {
		translate((double) x, y, z);
	}

	@Overwrite(remap = false)
	public static void translate(double x, double y, double z) {
		SharedObjects.primary2dMatrixStack.translate(x, y, z);
	}

	@Overwrite(remap = false)
	public static void colour(float r, float g, float b, float a) {
		RenderSystem.setShaderColor(r, g, b, a);
	}

	@Overwrite(remap = false)
	public static void blendFunction(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
		RenderSystem.blendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
	}

	@Overwrite(remap = false)
	public static void blendFunction(int sfactor, int dfactor) {
		RenderSystem.blendFunc(sfactor, dfactor);
	}

	@Overwrite(remap = false)
	public static void lineWidth(float width) {
		RenderSystem.lineWidth(width);
	}

	@Overwrite(remap = false)
	public static void resetLineWidth() {
		RenderSystem.lineWidth(2); // TODO: verify this
	}

	@Overwrite(remap = false)
	public static void rotate(float angle, float x, float y, float z) {
		SharedObjects.primary2dMatrixStack.multiply(Quaternion.fromEulerXyz(x * angle, y * angle, z * angle));
	}

	@Overwrite(remap = false)
	public static void bindTexture(int id) {
		RenderSystem.setShaderTexture(0, id);
	}

}
