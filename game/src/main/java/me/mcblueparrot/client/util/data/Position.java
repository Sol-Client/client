package me.mcblueparrot.client.util.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Position {

	@Getter
	private final int x;
	@Getter
	private final int y;

	public Position offset(int x, int y) {
		return new Position(this.x + x, this.y - y);
	}

	public Rectangle rectangle(int width, int height) {
		return new Rectangle(x, y, width, height);
	}

}
