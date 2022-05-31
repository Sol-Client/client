package io.github.solclient.client.util.data;

import io.github.solclient.client.util.Utils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Rectangle {

	@Getter
	private final int x;
	@Getter
	private final int y;
	@Getter
	private final int width;
	@Getter
	private final int height;

	public static final Rectangle ZERO = ofDimensions(0, 0);

	public static Rectangle ofDimensions(int width, int height) {
		return new Rectangle(0, 0, width, height);
	}

	public Rectangle offset(int x, int y) {
		return new Rectangle(this.x + x, this.y + y, width, height);
	}

	public boolean contains(Position position) {
		return contains(position.getX(), position.getY());
	}

	public boolean contains(int x, int y) {
		return x > this.x && x < this.x + width
				&& y > this.y && y < this.y + height;
	}

	public void fill(Colour colour) {
		Utils.drawRectangle(this, colour);
	}

	public void stroke(Colour colour) {
		Utils.drawOutline(this, colour);
	}

	public Rectangle multiply(float scale) {
		return multiply(scale, scale);
	}

	public Rectangle multiply(float width, float height) {
		return new Rectangle(x, y, (int) (this.width * width), (int) (this.height * height));
	}

	public float[] highPrecisionMultiply(float scale) {
		return highPrecisionMultiply(scale, scale);
	}

	public float[] highPrecisionMultiply(float width, float height) {
		return new float[] { x, y, this.width * width, this.height * height };
	}

	public Rectangle(int x, int y) {
		this(x, y, 1, 1);
	}

	public int getEndX() {
		return x + width;
	}

	public int getEndY() {
		return y + height;
	}

}
