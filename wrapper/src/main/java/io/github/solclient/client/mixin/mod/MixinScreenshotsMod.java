package io.github.solclient.client.mixin.mod;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.*;
import com.replaymod.replay.ReplayModReplay;

import io.github.solclient.client.mod.impl.ScreenshotsMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.text.*;
import net.minecraft.util.*;

public class MixinScreenshotsMod {

	@Mixin(ScreenshotUtils.class)
	public static class MixinScreenshotHelper {

		@Inject(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/gl/Framebuffer;)Lnet/minecraft/text/Text;", at = @At("HEAD"), cancellable = true)
		private static void saveScreenshot(File gameDirectory, String screenshotName, int width, int height,
				Framebuffer framebuffer, CallbackInfoReturnable<Text> callback) {
			if (!ScreenshotsMod.enabled)
				return;

			try {
				File screenshots = new File(gameDirectory, "screenshots");
				screenshots.mkdir();

				if (GLX.supportsFbo()) {
					width = framebuffer.textureWidth;
					height = framebuffer.textureHeight;
				}

				int pixels = width * height;

				if (intBuffer == null || intBuffer.capacity() < pixels) {
					intBuffer = BufferUtils.createIntBuffer(pixels);
					field_1035 = new int[pixels];
				}

				GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				intBuffer.clear();

				if (GLX.supportsFbo()) {
					GlStateManager.bindTexture(framebuffer.colorAttachment);
					GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
							intBuffer);
				} else
					GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);

				intBuffer.get(field_1035);
				TextureUtil.flipXY(field_1035, width, height);
				BufferedImage image = null;

				if (GLX.supportsFbo()) {
					image = new BufferedImage(framebuffer.viewportWidth, framebuffer.viewportHeight, 1);
					int j = framebuffer.textureHeight - framebuffer.viewportHeight;

					for (int k = j; k < framebuffer.textureHeight; ++k) {
						for (int l = 0; l < framebuffer.viewportWidth; ++l) {
							image.setRGB(l, k - j, field_1035[k * framebuffer.textureWidth + l]);
						}
					}
				} else {
					image = new BufferedImage(width, height, 1);
					image.setRGB(0, 0, width, height, field_1035, 0, width);
				}

				File screenshot;

				if (screenshotName == null) {
					screenshot = getScreenshotFile(screenshots);
				} else {
					screenshot = new File(screenshots, screenshotName);
				}

				BufferedImage finalImage = image;

				Thread thread = new Thread(() -> {
					try {
						ImageIO.write(finalImage, "png", screenshot);
						ScreenshotsMod.instance.postShot(screenshot);
					} catch (Exception error) {
						LOGGER.warn("Couldn't save screenshot", error);
						MinecraftClient.getInstance().inGameHud.getChatHud()
								.addMessage(new TranslatableText("screenshot.failure", error)
										.setStyle(new Style().setFormatting(Formatting.RED)));
					}
				});

				if (ReplayModReplay.instance.getReplayHandler() != null) {
					thread.run();
				} else {
					thread.start();
				}

				callback.setReturnValue(null);
			} catch (Throwable error) {
				LOGGER.warn("Couldn't save screenshot", error);
				callback.setReturnValue(new TranslatableText("screenshot.failure", error));
			}
		}

		@Shadow
		private static IntBuffer intBuffer;

		@Shadow
		private static @Final Logger LOGGER;

		@Shadow
		private static File getScreenshotFile(File gameDirectory) {
			throw new UnsupportedOperationException();
		}

		@Shadow
		private static int[] field_1035;

	}

}
