package me.mcblueparrot.client.util.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.mcblueparrot.client.util.Utils;

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
		return new Rectangle(x, y, (int) (width * scale), (int) (height * scale));
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
