package io.github.solclient.client.mod.hud;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.util.data.Position;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class HudPosition {

	private static MinecraftClient mc = MinecraftClient.getInstance();
	@Expose
	public float x;
	@Expose
	public float y;

	public HudPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Hud @ " + x + ", " + y;
	}

	public Position toAbsolute() {
		Window window = new Window(mc);
		return new Position((int) (window.getScaledWidth() * x), (int) (window.getScaledHeight() * y));
	}

	public static HudPosition fromAbsolute(Position absolute) {
		Window window = new Window(mc);
		return new HudPosition((float) (absolute.getX() / window.getScaledWidth()),
				(float) (absolute.getY() / window.getScaledHeight()));
	}

}
