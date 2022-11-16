package io.github.solclient.client.mod.hud;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.platform.mc.*;
import io.github.solclient.client.util.data.Position;
import lombok.*;

@Data
@RequiredArgsConstructor
public final class HudPosition {

	@Expose
	private final float x, y;

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
