package io.github.solclient.client.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.GL11;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.Environment;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.Window;
import io.github.solclient.client.platform.mc.sound.SoundInstance;
import io.github.solclient.client.platform.mc.sound.SoundType;
import io.github.solclient.client.platform.mc.util.MinecraftUtil;
import io.github.solclient.client.platform.mc.util.OperatingSystem;
import io.github.solclient.client.todo.TODO;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

	private PrintStream out;
	public final ExecutorService MAIN_EXECUTOR = Executors
			.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors(), 2));
	public final Comparator<String> STRING_WIDTH_COMPARATOR = Comparator.comparingInt(Utils::getStringWidth);

	static {
		try {
			out = new PrintStream(System.out, true, "UTF-8");
		} catch (UnsupportedEncodingException error) {
			out = System.out;
		}
	}

	public int getStringWidth(String text) {
		return MinecraftClient.getInstance().getFont().getWidth(text);
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
		double scale = window.getScaleFactor();

		GL11.glScissor((int) (x * scale), (int) ((window.getScaledHeight() - height - y) * scale),
				(int) (width * scale), (int) (height * scale));
	}

	public void playClickSound(boolean ui) {
		if (ui && !SolClientConfig.instance.buttonClicks) {
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

		if (window.getScaleFactor() > 0 && window.getScaleFactor() < 5) {
			return window.getScaleFactor() + "x";
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
		while ((line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}

		return result.toString();
	}

	public int randomInt(int from, int to) {
		return ThreadLocalRandom.current().nextInt(from, to + 1); // https://stackoverflow.com/a/363692
	}

	public void openUrl(String url) {
		sendLauncherMessage("openUrl", url);
	}

	private void sendLauncherMessage(String type, String... arguments) {
		out.println("message " + System.getProperty("io.github.solclient.client.secret") + " " + type + " "
				+ String.join(" ", arguments));
	}

	public String getRelativeToPackFolder(File packFile) {
		String relative = MinecraftClient.getInstance().getPackFolder().toPath().toAbsolutePath()
				.relativize(packFile.toPath().toAbsolutePath()).toString();

		if (MinecraftUtil.getOperatingSystem() == OperatingSystem.WINDOWS) {
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
		GL11.glColor4f(((colour >> 16) & 0xFF) / 255F, ((colour >> 8) & 0xFF) / 255F, (colour & 0xFF) / 255F,
				((colour << 24) & 0xFF) / 255F);
	}

	// Code duplication required

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

		return 0;
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

}
