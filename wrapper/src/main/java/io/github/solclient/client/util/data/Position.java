package io.github.solclient.client.util.data;

import com.google.gson.annotations.Expose;

import lombok.*;

@Data
public class Position {

	@Getter
	@Expose
	private final int x;
	@Getter
	@Expose
	private final int y;

	public Position offset(int x, int y) {
		return new Position(this.x + x, this.y + y);
	}

	public Rectangle rectangle(int width, int height) {
		return new Rectangle(x, y, width, height);
	}

	public boolean equals(int x, int y) {
		return this.x == x && this.y == y;
	}

}
