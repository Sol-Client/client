package io.github.solclient.client.mixin.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

import com.replaymod.replay.ReplayModReplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;

@Mixin(ScreenShotHelper.class)
public class MixinScreenshotHelper {

	@Overwrite
	public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height,
			Framebuffer buffer) {
		try {
			File screenshots = new File(gameDirectory, "screenshots");
			screenshots.mkdir();

			if(OpenGlHelper.isFramebufferEnabled()) {
				width = buffer.framebufferTextureWidth;
				height = buffer.framebufferTextureHeight;
			}

			int pixels = width * height;

			if(pixelBuffer == null || pixelBuffer.capacity() < pixels) {
				pixelBuffer = BufferUtils.createIntBuffer(pixels);
				pixelValues = new int[pixels];
			}

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			pixelBuffer.clear();

			if(OpenGlHelper.isFramebufferEnabled()) {
				GlStateManager.bindTexture(buffer.framebufferTexture);
				GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
			}
			else {
				GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
			}

			pixelBuffer.get(pixelValues);
			TextureUtil.processPixelValues(pixelValues, width, height);
			BufferedImage image = null;

			if(OpenGlHelper.isFramebufferEnabled()) {
				image = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
				int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

				for(int k = j; k < buffer.framebufferTextureHeight; ++k) {
					for(int l = 0; l < buffer.framebufferWidth; ++l) {
						image.setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
					}
				}
			}
			else {
				image = new BufferedImage(width, height, 1);
				image.setRGB(0, 0, width, height, pixelValues, 0, width);
			}

			File screenshot;

			if(screenshotName == null) {
				screenshot = getTimestampedPNGFileForDirectory(screenshots);
			}
			else {
				screenshot = new File(screenshots, screenshotName);
			}

			BufferedImage finalImage = image;

			Thread thread = new Thread(() -> {
				try {
					ImageIO.write(finalImage, "png", (File) screenshot);

					Minecraft.getMinecraft().ingameGUI.getChatGUI()
							.printChatMessage(new ChatComponentTranslation("screenshot.success", screenshot.getName()));

					IChatComponent secondaryText = new ChatComponentText("[" + I18n.format("sol_client.screenshot.view") + "]")
							.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE).setChatClickEvent(
									new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshot.getAbsolutePath())))
							.appendSibling(
									new ChatComponentText(" ").appendSibling(new ChatComponentText("[" + I18n.format("sol_client.screenshot.open_folder") + "]")
											.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW)
													.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE,
															screenshot.getAbsolutePath() + "§scshowinfolder§")))));

					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(secondaryText);
				}
				catch(Exception error) {
					logger.warn("Couldn\'t save screenshot", error);
					Minecraft.getMinecraft().ingameGUI.getChatGUI()
							.printChatMessage(new ChatComponentTranslation("screenshot.failure", error.getMessage()));
				}
			});

			if(ReplayModReplay.instance.getReplayHandler() != null) {
				thread.run();
			}
			else {
				thread.start();
			}

			return null;
		}
		catch(Exception exception) {
			logger.warn("Couldn\'t save screenshot", exception);
			return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
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
