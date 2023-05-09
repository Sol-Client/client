/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.util;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.IntConsumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.nanovg.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.replay.ReplayModReplay;
import com.replaymod.replay.camera.CameraEntity;

import io.github.solclient.client.mod.impl.core.CoreMod;
import io.github.solclient.client.mod.impl.core.mixins.client.MinecraftClientAccessor;
import io.github.solclient.client.util.data.*;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.option.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.Window;
import net.minecraft.network.*;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.*;
import net.minecraft.network.packet.s2c.query.*;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.Util.OperatingSystem;
import net.minecraft.util.math.Box;

/**
 * Utils involving Minecraft classes.
 */
@UtilityClass
public class MinecraftUtils {

	private static final Logger LOGGER = LogManager.getLogger();

	public ModelLoader modelLoader;

	public final String REVEAL_SUFFIX = "\0sol_client:showinfolder";
	public final MonitoringExecutorService USER_DATA;
	public final Comparator<String> STRING_WIDTH_COMPARATOR = Comparator.comparingInt(MinecraftUtils::getStringWidth);

	private final Map<Identifier, Integer> NVG_CACHE = new HashMap<>();

	static {
		int threads = 8;
		String threadsStr = System.getProperty("io.github.solclient.client.user_data_threads");
		if (threadsStr != null) {
			try {
				threads = Integer.parseInt(threadsStr);
			} catch (NumberFormatException ignored) {
			}
		}
		USER_DATA = new MonitoringExecutorService(threads, threads, 0, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>());
	}

	private int getStringWidth(String text) {
		return MinecraftClient.getInstance().textRenderer.getStringWidth(text);
	}

	public void drawHorizontalLine(double startX, double endX, double y, int colour) {
		if (endX < startX) {
			double swap = startX;
			startX = endX;
			endX = swap;
		}

		drawRectangle(startX, y, endX + 1, y + 1, colour);
	}

	public void drawVerticalLine(double x, double startY, double endY, int colour) {
		if (endY < startY) {
			double swap = startY;
			startY = endY;
			endY = swap;
		}

		drawRectangle(x, startY + 1, x + 1, endY, colour);
	}

	public void drawOutline(Rectangle rectangle, Colour colour) {
		drawOutline(rectangle.getX(), rectangle.getY(), rectangle.getX() + rectangle.getWidth(),
				rectangle.getY() + rectangle.getHeight(), colour.getValue());
	}

	public void drawOutline(double left, double top, double right, double bottom, int colour) {
		drawHorizontalLine(left, right - 1, top, colour);
		drawHorizontalLine(left, right - 1, bottom - 1, colour);
		drawVerticalLine(left, top, bottom - 1, colour);
		drawVerticalLine(right - 1, top, bottom - 1, colour);
	}

	public void drawRectangle(double x, double y, double right, double bottom, int colour) {
		if (x < right) {
			double swap = x;
			x = right;
			right = swap;
		}

		if (y < bottom) {
			double swap = y;
			y = bottom;
			bottom = swap;
		}

		float r = (colour >> 24 & 255) / 255.0F;
		float g = (colour >> 16 & 255) / 255.0F;
		float b = (colour >> 8 & 255) / 255.0F;
		float a = (colour & 255) / 255.0F;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(g, b, a, r);

		buffer.begin(7, VertexFormats.POSITION);
		buffer.vertex(x, bottom, 0.0D).next();
		buffer.vertex(right, bottom, 0.0D).next();
		buffer.vertex(right, y, 0.0D).next();
		buffer.vertex(x, y, 0.0D).next();
		tessellator.draw();

		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	public void drawRectangle(Rectangle rectangle, Colour colour) {
		DrawableHelper.fill(rectangle.getX(), rectangle.getY(), rectangle.getX() + rectangle.getWidth(),
				rectangle.getY() + rectangle.getHeight(), colour.getValue());
	}

	public long toMegabytes(long bytes) {
		return bytes / 1024L / 1024L;
	}

	public int lerpColour(int start, int end, float percent) {
		if (percent >= 1) {
			return end;
		}

		Colour startColour = new Colour(start);
		Colour endColour = new Colour(end);

		if (startColour.getAlpha() == 0) {
			startColour = endColour.withAlpha(0);
		} else if (endColour.getAlpha() == 0) {
			endColour = startColour.withAlpha(0);
		}

		return new Colour(lerpInt(startColour.getRed(), endColour.getRed(), percent),
				lerpInt(startColour.getGreen(), endColour.getGreen(), percent),
				lerpInt(startColour.getBlue(), endColour.getBlue(), percent),
				lerpInt(startColour.getAlpha(), endColour.getAlpha(), percent)).getValue();
	}

	public int lerpInt(int start, int end, float percent) {
		return Math.round(lerp(start, end, percent));
	}

	public float lerp(float start, float end, float percent) {
		return start + ((end - start) * percent);
	}

	public void scissor(Rectangle rectangle) {
		scissor(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	public void scissor(double x, double y, double width, double height) {
		Window window = new Window(MinecraftClient.getInstance());
		double scale = window.getScaleFactor();

		GL11.glScissor((int) (x * scale), (int) ((window.getScaledHeight() - height - y) * scale),
				(int) (width * scale), (int) (height * scale));
	}

	public void nvgScissor(long ctx, Rectangle rectangle) {
		NanoVG.nvgIntersectScissor(ctx, rectangle.getX(), rectangle.getY(), rectangle.getWidth(),
				rectangle.getHeight());
	}

	public void playClickSound(boolean ui) {
		if (ui && !CoreMod.instance.buttonClicks) {
			return;
		}

		MinecraftClient.getInstance().getSoundManager()
				.play(PositionedSoundInstance.master(new Identifier("gui.button.press"), 1.0F));
	}

	public ChatScreen getChatScreen() {
		Screen currentScreen = MinecraftClient.getInstance().currentScreen;
		if (currentScreen != null && currentScreen instanceof ChatScreen) {
			return (ChatScreen) currentScreen;
		}
		return null;
	}

	public void drawTexture(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
		float xMultiplier = 0.00390625F;
		float yMultiplier = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, VertexFormats.POSITION_TEXTURE);
		buffer.vertex(x, y + height, zLevel).texture(((textureX) * xMultiplier), (textureY + height) * yMultiplier)
				.next();
		buffer.vertex(x + width, y + height, zLevel)
				.texture((textureX + width) * xMultiplier, (textureY + height) * yMultiplier).next();
		buffer.vertex(x + width, y + 0, zLevel).texture((textureX + width) * xMultiplier, (textureY + 0) * yMultiplier)
				.next();
		buffer.vertex(x, y, zLevel).texture(((textureX) * xMultiplier), ((textureY + 0) * yMultiplier)).next();
		tessellator.draw();
	}

	public void drawGradientRect(int left, int top, int right, int bottom, int startColour, int endColour) {
		float alpha1 = (startColour >> 24 & 255) / 255.0F;
		float red1 = (startColour >> 16 & 255) / 255.0F;
		float green1 = (startColour >> 8 & 255) / 255.0F;
		float blue1 = (startColour & 255) / 255.0F;
		float alpha2 = (endColour >> 24 & 255) / 255.0F;
		float red2 = (endColour >> 16 & 255) / 255.0F;
		float green2 = (endColour >> 8 & 255) / 255.0F;
		float blue2 = (endColour & 255) / 255.0F;
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, VertexFormats.POSITION_COLOR);
		buffer.vertex(right, top, 0).color(red1, green1, blue1, alpha1).next();
		buffer.vertex(left, top, 0).color(red1, green1, blue1, alpha1).next();
		buffer.vertex(left, bottom, 0).color(red2, green2, blue2, alpha2).next();
		buffer.vertex(right, bottom, 0).color(red2, green2, blue2, alpha2).next();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	public boolean isSpectatingEntityInReplay() {
		return ReplayModReplay.instance.getReplayHandler() != null
				&& !(MinecraftClient.getInstance().getCameraEntity() instanceof CameraEntity);
	}

	public String getTextureScale() {
		Window window = new Window(MinecraftClient.getInstance());

		if (window.getScaleFactor() > 0 && window.getScaleFactor() < 5) {
			return window.getScaleFactor() + "x";
		}

		return "4x";
	}

	public int getShadowColour(int value) {
		return (value & 16579836) >> 2 | value & -16777216;
	}

	public void pingServer(String address, IntConsumer callback) throws UnknownHostException {
		ServerAddress serverAddress = ServerAddress.parse(address);
		ClientConnection connection = ClientConnection.connect(InetAddress.getByName(serverAddress.getAddress()),
				serverAddress.getPort(), false);

		connection.setPacketListener(new ClientQueryPacketListener() {

			private boolean expected = false;
			private long time = 0L;

			@Override
			public void onResponse(QueryResponseS2CPacket paramQueryResponseS2CPacket) {
				if (expected) {
					connection.disconnect(new LiteralText("Received unrequested status"));
				} else {
					expected = true;
					time = MinecraftClient.getTime();
					connection.send(new QueryPingC2SPacket(time));
				}
			}

			@Override
			public void onPong(QueryPongS2CPacket paramQueryPongS2CPacket) {
				long systemTime = MinecraftClient.getTime();
				callback.accept((int) (systemTime - time));
				connection.disconnect(new LiteralText("Finished"));
			}

			@Override
			public void onDisconnected(Text reason) {
				callback.accept(-1);
			}

		});

		connection.send(
				new HandshakeC2SPacket(47, serverAddress.getAddress(), serverAddress.getPort(), NetworkState.STATUS));
		connection.send(new QueryRequestC2SPacket());
	}

	public int randomInt(int from, int to) {
		return ThreadLocalRandom.current().nextInt(from, to + 1); // https://stackoverflow.com/a/363692
	}

	private String decodeUrl(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException error) {
			// UTF-8 is required
			throw new Error(error);
		}
	}

	private String urlDirname(String url) {
		int lastSlash = url.lastIndexOf('/');
		int lastBacklash = url.lastIndexOf('\\');

		if (lastBacklash > lastSlash) {
			lastSlash = lastBacklash;
		}

		return url.substring(0, lastSlash);
	}

	public void revealUrl(String url) {
		openUrl(url + REVEAL_SUFFIX);
	}

	public void openUrl(String url) {
		String[] command;
		boolean reveal = false;

		// ensure that the url starts with file:/// as opposed to file://
		if (url.startsWith("file:")) {
			url = url.replace("file:", "file://");
			url = url.substring(0, url.indexOf('/')) + '/' + url.substring(url.indexOf('/'));

			if (url.endsWith(REVEAL_SUFFIX)) {
				url = url.substring(0, url.length() - REVEAL_SUFFIX.length());
				reveal = true;
			}
		}

		switch (Util.getOperatingSystem()) {
			case LINUX:
				if (reveal) {
					if (new File("/usr/bin/xdg-mime").exists() && new File("/usr/bin/gio").exists()) {
						try {
							Process process = new ProcessBuilder("xdg-mime", "query", "default", "inode/directory")
									.start();
							int code = process.waitFor();

							if (code > 0) {
								throw new IllegalStateException("xdg-mime exited with code " + code);
							}

							String file;
							try (BufferedReader reader = new BufferedReader(
									new InputStreamReader(process.getInputStream()))) {
								file = reader.readLine();
							}

							if (file != null) {
								url = decodeUrl(url);
								url = url.substring(7);

								if (!file.startsWith("/")) {
									file = "/usr/share/applications/" + file;
								}

								command = new String[] { "gio", "launch", file, url };
								break;
							}
						} catch (IOException | InterruptedException | IllegalStateException error) {
							LOGGER.error("Could not determine directory handler:", error);
						}
					}
					url = urlDirname(url);
				}

				if (new File("/usr/bin/xdg-open").exists()) {
					command = new String[] { "xdg-open", url };
					break;
				}
				// fall through to default
			default:
				// fall back to AWT, but without a message
				command = null;
				break;
			case MACOS:
				if (reveal) {
					command = new String[] { "open", "-R", decodeUrl(url).substring(7) };
				} else {
					command = new String[] { "open", url };
				}
				break;
			case WINDOWS:
				if (reveal) {
					command = new String[] { "Explorer", "/select," + decodeUrl(url).substring(8).replace('/', '\\') };
				} else {
					command = new String[] { "rundll32", "url.dll,FileProtocolHandler", url };
				}

				break;
		}

		if (command != null) {
			try {
				Process proc = new ProcessBuilder(command).start();
				proc.getInputStream().close();
				proc.getErrorStream().close();
				proc.getOutputStream().close();
				return;
			} catch (IOException error) {
				LOGGER.warn("Could not execute " + String.join(" ", command) + " - falling back to AWT:", error);
			}
		}

		try {
			Desktop.getDesktop().browse(URI.create(url));
		} catch (IOException error) {
			LOGGER.error("Could not open " + url + " with AWT:", error);

			// null checks in case a link is opened before MinecraftClient is fully
			// initialised

			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc == null) {
				return;
			}

			InGameHud hud = mc.inGameHud;
			if (hud == null) {
				return;
			}

			hud.getChatHud().addMessage(new LiteralText("§cCould not open " + url + ". Please open it manually."));
		}
	}

	public String getRelativeToPackFolder(File packFile) {
		String relative = new File(MinecraftClient.getInstance().runDirectory, "resourcepacks").toPath()
				.toAbsolutePath().relativize(packFile.toPath().toAbsolutePath()).toString();

		if (Util.getOperatingSystem() == OperatingSystem.WINDOWS) {
			relative = relative.replace("\\", "/"); // Just to be safe
		}

		return relative;
	}

	public void resetLineWidth() {
		// Reset the fishing rod line back to its normal width.
		// Fun fact: the line should actually be thinner, but it's overriden by the
		// block selection.
		GL11.glLineWidth(2);
	}

	public void drawFloatRectangle(float left, float top, float right, float bottom, int colour) {
		if (left < right) {
			float swap = left;
			left = right;
			right = swap;
		}

		if (top < bottom) {
			float swap = top;
			top = bottom;
			bottom = swap;
		}

		float f3 = (colour >> 24 & 255) / 255.0F;
		float f = (colour >> 16 & 255) / 255.0F;
		float f1 = (colour >> 8 & 255) / 255.0F;
		float f2 = (colour & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(f, f1, f2, f3);
		buffer.begin(7, VertexFormats.POSITION);
		buffer.vertex(left, bottom, 0.0D).next();
		buffer.vertex(right, bottom, 0.0D).next();
		buffer.vertex(right, top, 0.0D).next();
		buffer.vertex(left, top, 0.0D).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	public String getScoreboardTitle() {
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.world != null && mc.world.getScoreboard() != null) {
			ScoreboardObjective first = mc.world.getScoreboard().getObjectiveForSlot(1);

			if (first != null) {
				return Formatting.strip(first.getDisplayName());
			}
		}

		return null;
	}

	public String getNativeFileExtension() {
		switch (Util.getOperatingSystem()) {
			case WINDOWS:
				return "dll";
			case MACOS:
				return "dylib";
			default:
				return "so";
		}
	}

	public double max(double[] doubles) {
		double max = 0;

		for (double d : doubles) {
			if (max < d) {
				max = d;
			}
		}

		return max;
	}

	public void fillBox(Box box) {
		GlStateManager.disableCull();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(6, VertexFormats.POSITION);
		buffer.vertex(box.minX, box.minY, box.minZ).next();
		buffer.vertex(box.maxX, box.minY, box.minZ).next();
		buffer.vertex(box.maxX, box.maxY, box.minZ).next();
		buffer.vertex(box.minX, box.maxY, box.minZ).next();
		buffer.vertex(box.minX, box.minY, box.minZ).next();
		tessellator.draw();

		buffer.begin(6, VertexFormats.POSITION);
		buffer.vertex(box.maxX, box.minY, box.minZ).next();
		buffer.vertex(box.maxX, box.minY, box.maxZ).next();
		buffer.vertex(box.maxX, box.maxY, box.maxZ).next();
		buffer.vertex(box.maxX, box.maxY, box.minZ).next();
		buffer.vertex(box.maxX, box.minY, box.minZ).next();
		tessellator.draw();

		buffer.begin(6, VertexFormats.POSITION);
		buffer.vertex(box.minX, box.minY, box.maxZ).next();
		buffer.vertex(box.maxX, box.minY, box.maxZ).next();
		buffer.vertex(box.maxX, box.maxY, box.maxZ).next();
		buffer.vertex(box.minX, box.maxY, box.maxZ).next();
		buffer.vertex(box.minX, box.minY, box.maxZ).next();
		tessellator.draw();

		buffer.begin(6, VertexFormats.POSITION);
		buffer.vertex(box.minX, box.minY, box.maxZ).next();
		buffer.vertex(box.minX, box.minY, box.minZ).next();
		buffer.vertex(box.minX, box.maxY, box.minZ).next();
		buffer.vertex(box.minX, box.maxY, box.maxZ).next();
		buffer.vertex(box.minX, box.minY, box.maxZ).next();
		tessellator.draw();

		buffer.begin(6, VertexFormats.POSITION);
		buffer.vertex(box.minX, box.maxY, box.minZ).next();
		buffer.vertex(box.maxX, box.maxY, box.minZ).next();
		buffer.vertex(box.maxX, box.maxY, box.maxZ).next();
		buffer.vertex(box.minX, box.maxY, box.maxZ).next();
		buffer.vertex(box.minX, box.maxY, box.minZ).next();
		tessellator.draw();

		buffer.begin(6, VertexFormats.POSITION);
		buffer.vertex(box.minX, box.minY, box.minZ).next();
		buffer.vertex(box.maxX, box.minY, box.minZ).next();
		buffer.vertex(box.maxX, box.minY, box.maxZ).next();
		buffer.vertex(box.minX, box.minY, box.maxZ).next();
		buffer.vertex(box.minX, box.minY, box.minZ).next();
		tessellator.draw();

		GlStateManager.enableCull();
	}

	public String onlyKeepDigits(String string) {
		StringBuilder builder = new StringBuilder();

		for (char character : string.toCharArray()) {
			if (character < '0' || character > '9') {
				continue;
			}
			builder.append(character);
		}

		return builder.toString();
	}

	// not managed by Java GC for performance reasons
	public ByteBuffer mallocAndRead(@NotNull InputStream in) throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(in)) {
			ByteBuffer buffer = MemoryUtil.memAlloc(8192);

			while (channel.read(buffer) != -1)
				if (buffer.remaining() == 0)
					buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() + buffer.capacity() * 3 / 2);

			buffer.flip();

			return buffer;
		}
	}

	public NVGPaint nvgTexturePaint(long nvg, int image, int x, int y, int width, int height, float angle) {
		NVGPaint paint = NVGPaint.create();
		NanoVG.nvgImagePattern(nvg, x, y, width, height, angle, image, 1, paint);
		return paint;
	}

	public NVGPaint nvgMinecraftTexturePaint(long nvg, Identifier id, int x, int y, int width, int height,
			float angle) {
		try {
			return nvgTexturePaint(nvg, nvgMinecraftTexture(nvg, id), x, y, width, height, angle);
		} catch (IOException error) {
			return NVGPaint.create().innerColor(Colour.WHITE.nvg());
		}
	}

	public int nvgMinecraftTexture(long nvg, Identifier id) throws IOException {
		if (NVG_CACHE.containsKey(id))
			return NVG_CACHE.get(id);

		MinecraftClient mc = MinecraftClient.getInstance();
		InputStream in = mc.getResourceManager().getResource(id).getInputStream();

		ByteBuffer buffer = mallocAndRead(in);
		int handle = NanoVG.nvgCreateImageMem(nvg, 0, buffer);
		MemoryUtil.memFree(buffer);

		NVG_CACHE.put(id, handle);

		return handle;
	}

	public boolean isConflicting(KeyBinding keybinding) {
		return isConflicting(KeyBindingInterface.from(keybinding));
	}

	public boolean isConflicting(KeyBindingInterface keybinding) {
		if (keybinding.getKeyCode() == 0)
			return false;

		for (KeyBinding other : MinecraftClient.getInstance().options.allKeys)
			if (other != keybinding && other.getCode() == keybinding.getKeyCode()
					&& KeyBindingInterface.from(other).getMods() == keybinding.getMods())
				return true;

		return false;
	}

	// losely based on
	// https://github.com/apache/httpcomponents-client/blob/d2016eaacf31c0de4b2ca788d74e65c18c5fc8d7/httpclient5/src/main/java/org/apache/hc/client5/http/entity/mime/MultipartEntityBuilder.java#L192
	// CharBuffers could be better ¯\(°_o)/¯

	private final byte[] BOUNDARY_CHARS = { '-', '_', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	public String generateHttpBoundary() {
		int count = ThreadLocalRandom.current().nextInt(11) + 30;
		byte[] result = new byte[count];
		for (int i = 0; i < count; i++) {
			result[i] = BOUNDARY_CHARS[ThreadLocalRandom.current().nextInt(BOUNDARY_CHARS.length)];
		}
		return new String(result, StandardCharsets.US_ASCII);
	}

	public void registerKeyBinding(KeyBinding keyBinding) {
		GameOptions options = MinecraftClient.getInstance().options;
		options.allKeys = ArrayUtils.add(options.allKeys, keyBinding);
	}

	public void unregisterKeyBinding(KeyBinding keyBinding) {
		GameOptions options = MinecraftClient.getInstance().options;
		options.allKeys = ArrayUtils.removeElement(options.allKeys, keyBinding);
		keyBinding.setCode(0);
	}

	public float getTickDelta() {
		return ((MinecraftClientAccessor) MinecraftClient.getInstance()).getTicker().tickDelta;
	}

	public void withNvg(Runnable task, boolean scale) {
		long nvg = NanoVGManager.getNvg();
		MinecraftClient mc = MinecraftClient.getInstance();

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

		Window window = new Window(mc);

		NanoVG.nvgBeginFrame(nvg, mc.width, mc.height, 1);

		if (scale)
			NanoVG.nvgScale(nvg, window.getScaleFactor(), window.getScaleFactor());

		task.run();

		// the alpha test seems to prevent some colours rendering
		// do it here so nothing can stop it :o
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		NanoVG.nvgEndFrame(nvg);
		GL11.glPopAttrib();
	}

	public void renderCheckerboard(long nvg, Colour a, Colour b, int startX, int startY, int width, int height,
			int scale) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				boolean square = x % 2 == 0;
				if (y % 2 == 0)
					square = !square;

				NanoVG.nvgBeginPath(nvg);
				NanoVG.nvgRect(nvg, startX + x * scale, startY + y * scale, scale, scale);
				NanoVG.nvgFillColor(nvg, (square ? a : b).nvg());
				NanoVG.nvgFill(nvg);
			}
		}
	}

	public UUID getPlayerUuid() {
		return MinecraftClient.getInstance().getSession().getProfile().getId();
	}

}
