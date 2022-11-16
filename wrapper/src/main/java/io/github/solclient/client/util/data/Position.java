package io.github.solclient.client.util.data;

import lombok.*;

@Data
@AllArgsConstructor
public final class Position {

	private final int x, y;

	public Position offset(int x, int y) {
		return new Position(this.x + x, this.y - y);
	}

	public Rectangle rectangle(int width, int height) {
		return new Rectangle(x, y, width, height);
	}

}
