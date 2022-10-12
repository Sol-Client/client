package io.github.solclient.client.util;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Comparator;
import java.util.concurrent.*;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.*;
import io.github.solclient.client.platform.mc.hud.IngameHud;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.sound.*;
import io.github.solclient.client.platform.mc.util.*;
import io.github.solclient.client.todo.TODO;
import io.github.solclient.client.util.data.*;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

	public static final String REVEAL_SUFFIX = "§sol_client:showinfolder";
	public final ExecutorService MAIN_EXECUTOR = Executors
			.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors(), 2));
	public final Comparator<String> STRING_WIDTH_COMPARATOR = Comparator.comparingInt(Utils::getStringWidth);

	public int getStringWidth(String text) {
		return MinecraftClient.getInstance().getFont().getTextWidth(text);
	}

	public long toMegabytes(long bytes) {
		return bytes / 1024L / 1024L;
	}

	public int lerpColour(int start, int end, float percent) {
		if(percent >= 1) {
			return end;
		}

		Colour startColour = new Colour(start);
		Colour endColour = new Colour(end);

		if(startColour.getAlpha() == 0) {
			startColour = endColour.withAlpha(0);
		}
		else if(endColour.getAlpha() == 0) {
			endColour = startColour.withAlpha(0);
		}

		return new Colour(
				lerp(startColour.getRed(), endColour.getRed(), percent),
				lerp(startColour.getGreen(), endColour.getGreen(), percent),
				lerp(startColour.getBlue(), endColour.getBlue(), percent),
				lerp(startColour.getAlpha(), endColour.getAlpha(), percent)
		).getValue();
	}

	public int lerp(int start, int end, float percent) {
		return Math.round(start + ((end - start) * percent));
	}

	public void scissor(Rectangle rectangle) {
		scissor(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	public void scissor(double x, double y, double width, double height) {
		Window window = MinecraftClient.getInstance().getWindow();
		double scale = window.scaleFactor();

		GL11.glScissor((int) (x * scale), (int) ((window.scaledHeight() - height - y) * scale),
				(int) (width * scale), (int) (height * scale));
	}

	public void playClickSound(boolean ui) {
		if(ui && !SolClientConfig.INSTANCE.buttonClicks) {
			return;
		}

		MinecraftClient.getInstance().getSoundEngine().play(SoundInstance.ui(SoundType.BUTTON_CLICK, 1.0F));
	}

	@SneakyThrows
	public URL sneakyParse(String url) {
		return new URL(url);
	}

	public boolean isSpectatingEntityInReplay() {
		return TODO.L /* TODO replaymod */ != null
				&& !(MinecraftClient.getInstance().getCameraEntity() instanceof TODO /* TODO replaymod */ );
	}

	public String getTextureScale() {
		Window window = MinecraftClient.getInstance().getWindow();

		if (window.scaleFactor() > 0 && window.scaleFactor() < 5) {
			return window.scaleFactor() + "x";
		}

		return "4x";
	}

	public String urlToString(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("User-Agent", System.getProperty("http.agent")); // Force consistent behaviour

		InputStream in = connection.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();

		String line;
		while((line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}

		return result.toString();
	}

	public int randomInt(int from, int to) {
		return ThreadLocalRandom.current().nextInt(from, to + 1); // https://stackoverflow.com/a/363692
	}

	private String decodeUrl(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		}
		catch(UnsupportedEncodingException error) {
			// UTF-8 is required
			throw new Error(error);
		}
	}

	private String urlDirname(String url) {
		int lastSlash = url.lastIndexOf('/');
		int lastBacklash = url.lastIndexOf('\\');

		if(lastBacklash > lastSlash) {
			lastSlash = lastBacklash;
		}

		return url.substring(0, lastSlash);
	}

	public void openUrl(String url) {
		String[] command;
		boolean reveal = false;

		// ensure that the url starts with file:/// as opposed to file://
		if(url.startsWith("file:")) {
			url = url.replace("file:", "file://");
			url = url.substring(0, url.indexOf('/')) + '/' + url.substring(url.indexOf('/'));

			if(url.endsWith(REVEAL_SUFFIX)) {
				url = url.substring(0, url.length() - REVEAL_SUFFIX.length());
				reveal = true;
			}
		}

		switch(MinecraftUtil.getOperatingSystem().enumOrdinal()) {
			case 0: // Linux
				if(reveal) {
					if(new File("/usr/bin/xdg-mime").exists() && new File("/usr/bin/gio").exists()) {
						try {
							Process process = new ProcessBuilder("xdg-mime", "query", "default", "inode/directory").start();
							int code = process.waitFor();

							if(code > 0) {
								throw new IllegalStateException("xdg-mime exited with code " + code);
							}

							String file;
							try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
								file = reader.readLine();
							}

							if(file != null) {
								url = decodeUrl(url);
								url = url.substring(7);

								if(!file.startsWith("/")) {
									file = "/usr/share/applications/" + file;
								}

								command = new String[] { "gio", "launch", file, url };
								break;
							}
						}
						catch(IOException | InterruptedException | IllegalStateException error) {
							Client.LOGGER.error("Could not determine directory handler:", error);
						}
					}
					url = urlDirname(url);
				}

				if(new File("/usr/bin/xdg-open").exists()) {
					command = new String[] { "xdg-open", url };
					break;
				}
				// fall through to default
			default:
				// fall back to AWT, but without a message
				command = null;
				break;
			case 3: // OSX
				if(reveal) {
					command = new String[] { "open", "-R", decodeUrl(url).substring(7) };
				}
				else {
					command = new String[] { "open", url };
				}
				break;
			case 2: // Windows
				if(reveal) {
					command = new String[] { "Explorer", "/select," + decodeUrl(url).substring(8).replace('/', '\\') };
				}
				else {
					command = new String[] { "rundll32", "url.dll,FileProtocolHandler", url };
				}

				break;
		}

		if(command != null) {
			try {
				Process proc = new ProcessBuilder(command).start();
				proc.getInputStream().close();
				proc.getErrorStream().close();
				proc.getOutputStream().close();
				return;
			}
			catch(IOException error) {
				Client.LOGGER.warn("Could not execute " + String.join(" ", command) + " - falling back to AWT:", error);
			}
		}

		try {
			Desktop.getDesktop().browse(URI.create(url));
		}
		catch(IOException error) {
			Client.LOGGER.error("Could not open " + url + " with AWT:", error);

			// null checks in case a link is opened before Minecraft is fully initialised

			MinecraftClient mc = MinecraftClient.getInstance();
			if(mc == null) {
				return;
			}

			IngameHud hud = mc.getIngameHud();
			if(hud == null) {
				return;
			}

			hud.getChat().addMessage("§cCould not open " + url + ". Please open it manually.");
		}
	}

	public String getRelativeToPackFolder(File packFile) {
		String relative = MinecraftClient.getInstance().getPackFolder().toPath().toAbsolutePath()
				.relativize(packFile.toPath().toAbsolutePath()).toString();

		if (MinecraftUtil.getOperatingSystem() == OperatingSystem.WINDOWS) {
			relative = relative.replace("\\", "/"); // Just to be safe
		}

		return relative;
	}

	public static String getNativeFileExtension() {
		OperatingSystem system = MinecraftUtil.getOperatingSystem();

		if(system == OperatingSystem.WINDOWS) {
			return "dll";
		}
		else if(system == OperatingSystem.OSX) {
			return "dylib";
		}

		return "so";
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

	public void glColour(int colour) {
		GlStateManager.colour(((colour >> 16) & 0xFF) / 255F, ((colour >> 8) & 0xFF) / 255F, (colour & 0xFF) / 255F,
				((colour << 24) & 0xFF) / 255F);
	}

	// Code duplication required
	// thanks for not having proper templates <3

	public float clamp(float input, float min, float max) {
		return input > max ? max : (input < min ? min : input);
	}

	public double clamp(double input, double min, double max) {
		return input > max ? max : (input < min ? min : input);
	}

	public int clamp(int input, int min, int max) {
		return input > max ? max : (input < min ? min : input);
	}

	public double wrapYaw(double yaw) {
		yaw %= 360;

		if(yaw >= 180) {
			yaw -= 360;
		}

		if(yaw < -180) {
			yaw += 360;
		}

		return yaw;
	}

	public static void earlyLoad(String name) {
		try {
			Class.forName(name, true, Environment.CLASS_LOADER);
		}
		catch(Exception error) {
			Client.LOGGER.error("Could not early load " + name + ". This may cause further issues.");
		}
	}

	public static int getShadowColour(int value) {
		return (value & 0xFCFCFC) >> 2 | value & -0x1000000;
	}

	/**
	 * A version of {@link String#format(String, Object...)} that doesn't allocate an object if there are no arguments passed.
	 * @param fmt The format.
	 * @param args The args.
	 * @return The formatted string.
	 */
	public static String format(String fmt, Object... args) {
		return args.length == 0 ? fmt : String.format(fmt, args);
	}

	public static String getGitBranch() throws IOException, InterruptedException {
		String root = System.getProperty("io.github.solclient.client.project_root");

		if(root == null) {
			throw new UnsupportedOperationException("No source root found");
		}

		Process process = new ProcessBuilder("git", "branch", "--show-current").start();
		if(process.waitFor() > 0) {
			throw new IllegalStateException("git command exited with code " + process.exitValue());
		}

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			return reader.readLine();
		}
	}

	// not managed by Java GC for performance reasons
	public static ByteBuffer mallocAndRead(@NotNull InputStream in) throws IOException {
		ReadableByteChannel channel = Channels.newChannel(in);
		ByteBuffer buffer = MemoryUtil.memAlloc(8192);

		while(channel.read(buffer) != -1) {
			if(buffer.remaining() == 0) {
				buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() + buffer.capacity() * 3 / 2);
			}
		}

		buffer.flip();

		return buffer;
	}

}
