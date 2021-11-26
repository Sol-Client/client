package me.mcblueparrot.client.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Position {

	@Getter
	private int x;
	@Getter
	private int y;

	public Position offset(int x, int y) {
		return new Position(this.x + x, this.y - y);
	}

	public Rectangle expandToRectangle(int width, int height) {
		return new Rectangle(x, y, width, height);
	}

}
