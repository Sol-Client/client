package io.github.solclient.client.mod.impl.cosmetica;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.*;

import io.github.solclient.client.mixin.client.MixinTextureUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/**
 * An animatable texture deserialised and uploaded from Base64.
 */
final class Texture extends AbstractTexture implements ITickableTextureObject {

	private static Set<ResourceLocation> all = new HashSet<>();

	private int[] textures;
	private int frame;
	private int ticks;
	private final int aspectRatio;
	private final int frameDelay;
	private String base64;

	@Getter
	private int frameWidth, frameHeight;

	Texture(int aspectRatio, int frameDelay, String base64) {
		this.aspectRatio = aspectRatio;
		this.frameDelay = ticks = frameDelay;
		this.base64 = base64;
	}

	private static String strictParse(String input) {
		if (input.startsWith("data:image/png;base64,")) {
			return input.substring(22);
		}

		return null;
	}

	private static ResourceLocation target(String base64) {
		return new ResourceLocation("sol_client_base64", base64);
	}

	static void disposeAll() {
		all.forEach((location) -> Minecraft.getMinecraft().getTextureManager().deleteTexture(location));

		if (!all.isEmpty()) {
			all = new HashSet<>();
		}
	}

	static ResourceLocation load(int aspectRatio, int frameDelay, String base64) {
		{
			String newBase64 = strictParse(base64);
			if (newBase64 == null) {
				throw new IllegalArgumentException(base64);
			}
			base64 = newBase64;
		}

		ResourceLocation target = target(base64);

		if (all.contains(target)) {
			return target;
		}

		all.add(target);

		Texture texture = new Texture(aspectRatio, frameDelay, base64);
		Minecraft.getMinecraft().getTextureManager().loadTickableTexture(target, texture);
		return target;
	}

	@Override
	public void loadTexture(IResourceManager resources) throws IOException {
		deleteGlTexture();

		try (ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(base64))) {
			BufferedImage image = ImageIO.read(in);
			int frames = 0;

			if (aspectRatio != 0) {
				frames = (aspectRatio * image.getHeight()) / image.getWidth();
			}

			if (frames == 0) {
				frames++;
			}

			frameWidth = image.getWidth();
			frameHeight = image.getHeight() / frames;

			textures = new int[frames];
			for (int i = 0; i < frames; i++) {
				textures[i] = GL11.glGenTextures();
				TextureUtil.allocateTexture(textures[i], image.getWidth(), frameHeight);

				// modified from code in TextureUtil
				int z = 4194304 / frameWidth; // What is this?
				int[] sample = new int[z * frameWidth];

				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

				for (int j = 0; j < frameWidth * frameHeight; j += frameWidth * z) {
					int y = j / frameWidth;
					int sampleHeight = Math.min(z, frameHeight - y);
					int length = frameWidth * sampleHeight;
					image.getRGB(0, i * frameHeight + y, frameWidth, sampleHeight, sample, 0, frameWidth);
					MixinTextureUtil.copyToBuffer(sample, length);
					GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, y, frameWidth, sampleHeight, GL12.GL_BGRA,
							GL12.GL_UNSIGNED_INT_8_8_8_8_REV, MixinTextureUtil.getDataBuffer());
				}
			}
		}
	}

	@Override
	public int getGlTextureId() {
		if (textures == null) {
			return -1;
		}

		return textures[frame];
	}

	@Override
	public void deleteGlTexture() {
		if (textures == null) {
			return;
		}

		for (int texture : textures) {
			TextureUtil.deleteTexture(texture);
		}

		textures = null;
	}

	@Override
	public void tick() {
		if (textures == null) {
			return;
		}

		if (--ticks < 0) {
			ticks = frameDelay;
			if (++frame > textures.length - 1) {
				frame = 0;
			}
		}
	}

}
