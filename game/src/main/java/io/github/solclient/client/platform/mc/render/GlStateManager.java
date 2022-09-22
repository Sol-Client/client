package io.github.solclient.client.platform.mc.render;

import io.github.solclient.client.platform.Helper;

public class GlStateManager {

	public static void enableBlend() {
		throw new UnsupportedOperationException();
	}

	public static void disableBlend() {
		throw new UnsupportedOperationException();
	}

	public static void enableDepth() {
		throw new UnsupportedOperationException();
	}

	public static void disableDepth() {
		throw new UnsupportedOperationException();
	}

	public static void enableCull() {
		throw new UnsupportedOperationException();
	}

	public static void disableCull() {
		throw new UnsupportedOperationException();
	}

	public static void enableLighting() {
		throw new UnsupportedOperationException();
	}

	public static void disableLighting() {
		throw new UnsupportedOperationException();
	}

	public static void pushMatrix() {
		throw new UnsupportedOperationException();
	}

	public static void popMatrix() {
		throw new UnsupportedOperationException();
	}

	public static void disableTexture2d() {
		throw new UnsupportedOperationException();
	}

	public static void enableTexture2d() {
		throw new UnsupportedOperationException();
	}

	public static void depthMask(boolean mask) {
		throw new UnsupportedOperationException();
	}

	public static void scale(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	public static void scale(double x, double y, double z) {
		throw new UnsupportedOperationException();
	}

	public static void translate(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	public static void translate(double x, double y, double z) {
		throw new UnsupportedOperationException();
	}

	@Helper
	public static void resetColour() {
		colour(1, 1, 1, 1);
	}

	public static void colour(float r, float g, float b, float a) {
		throw new UnsupportedOperationException();
	}

	public static void blendFunction(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
		throw new UnsupportedOperationException();
	}

	public static void blendFunction(int sfactor, int dfactor) {
		throw new UnsupportedOperationException();
	}

	public static void lineWidth(float width) {
		throw new UnsupportedOperationException();
	}

	public static void resetLineWidth() {
		throw new UnsupportedOperationException();
	}

	public static void rotate(float angle, float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	public static void bindTexture(int id) {
		throw new UnsupportedOperationException();
	}

}
