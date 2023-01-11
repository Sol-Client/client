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

import com.replaymod.replay.ReplayModReplay;

import io.github.solclient.client.mod.impl.ScreenshotsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.*;

public class MixinScreenshotsMod {

	@Mixin(ScreenShotHelper.class)
	public static class MixinScreenshotHelper {

		@Inject(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/IChatComponent;", at = @At("HEAD"), cancellable = true)
		private static void saveScreenshot(File gameDirectory, String screenshotName, int width, int height,
				Framebuffer buffer, CallbackInfoReturnable<IChatComponent> callback) {
			if (!ScreenshotsMod.enabled)
				return;

			try {
				File screenshots = new File(gameDirectory, "screenshots");
				screenshots.mkdir();

				if (OpenGlHelper.isFramebufferEnabled()) {
					width = buffer.framebufferTextureWidth;
					height = buffer.framebufferTextureHeight;
				}

				int pixels = width * height;

				if (pixelBuffer == null || pixelBuffer.capacity() < pixels) {
					pixelBuffer = BufferUtils.createIntBuffer(pixels);
					pixelValues = new int[pixels];
				}

				GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				pixelBuffer.clear();

				if (OpenGlHelper.isFramebufferEnabled()) {
					GlStateManager.bindTexture(buffer.framebufferTexture);
					GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
							pixelBuffer);
				} else {
					GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
				}

				pixelBuffer.get(pixelValues);
				TextureUtil.processPixelValues(pixelValues, width, height);
				BufferedImage image = null;

				if (OpenGlHelper.isFramebufferEnabled()) {
					image = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
					int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

					for (int k = j; k < buffer.framebufferTextureHeight; ++k) {
						for (int l = 0; l < buffer.framebufferWidth; ++l) {
							image.setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
						}
					}
				} else {
					image = new BufferedImage(width, height, 1);
					image.setRGB(0, 0, width, height, pixelValues, 0, width);
				}

				File screenshot;

				if (screenshotName == null) {
					screenshot = getTimestampedPNGFileForDirectory(screenshots);
				} else {
					screenshot = new File(screenshots, screenshotName);
				}

				BufferedImage finalImage = image;

				Thread thread = new Thread(() -> {
					try {
						ImageIO.write(finalImage, "png", screenshot);
						ScreenshotsMod.instance.postShot(screenshot);
					} catch (Exception error) {
						logger.warn("Couldn't save screenshot", error);
						Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
								new ChatComponentTranslation("screenshot.failure", error).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
					}
				});

				if (ReplayModReplay.instance.getReplayHandler() != null) {
					thread.run();
				} else {
					thread.start();
				}

				callback.setReturnValue(null);
			} catch (Throwable error) {
				logger.warn("Couldn't save screenshot", error);
				callback.setReturnValue(new ChatComponentTranslation("screenshot.failure", error));
			}
		}

		@Shadow
		private static IntBuffer pixelBuffer;

		@Shadow
		@Final
		private static Logger logger;

		@Shadow
		private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
			throw new UnsupportedOperationException();
		}

		@Shadow
		private static int[] pixelValues;

	}

}
