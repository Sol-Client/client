package io.github.solclient.client.mod.hud;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.Window;
import io.github.solclient.client.util.data.Position;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HudPosition {

	@Expose
	private float x;
	@Expose
	private float y;

	public Position toAbsolute() {
		Window window = MinecraftClient.getInstance().getWindow();
		return new Position((int) (window.scaledWidth() * x), (int) (window.scaledHeight() * y));
	}

	public static HudPosition fromAbsolute(Position absolute) {
		Window window = MinecraftClient.getInstance().getWindow();
		return new HudPosition((float) (absolute.getX() / window.scaledWidthD()),
				(float) (absolute.getY() / window.scaledHeightD()));
	}

}
