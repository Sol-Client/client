package io.github.solclient.client.util.data;

import lombok.*;

@Data
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

	public boolean equals(int x, int y) {
		return this.x == x && this.y == y;
	}

}
