package io.github.solclient.client.util.data;

import java.util.Iterator;
import java.util.stream.Stream;

import io.github.solclient.client.util.MinecraftUtils;
import lombok.*;

@ToString
@EqualsAndHashCode
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

	public static Rectangle encompassing(Iterable<Rectangle> rectangles) {
		return encompassing(rectangles.iterator());
	}

	public static Rectangle encompassing(Stream<Rectangle> rectangles) {
		return encompassing(rectangles.iterator());
	}

	public static Rectangle encompassing(Iterator<Rectangle> rectangles) {
		int beginx = Integer.MAX_VALUE;
		int endx = 0;
		int beginy = Integer.MAX_VALUE;
		int endy = 0;

		while (rectangles.hasNext()) {
			Rectangle rectangle = rectangles.next();
			beginx = Math.min(rectangle.getX(), beginx);
			beginy = Math.min(rectangle.getY(), beginy);
			endx = Math.max(rectangle.getEndX(), endx);
			endy = Math.max(rectangle.getEndY(), endy);
		}

		if (beginx == Integer.MAX_VALUE)
			beginx = 0;
		if (beginy == Integer.MAX_VALUE)
			beginy = 0;

		return new Rectangle(beginx, beginy, endx - beginx, endy - beginy);
	}

	public Rectangle(int x, int y, int width, int height) {
		if (width < 0) {
			x += width;
			width *= -1;
		}
		if (height < 0) {
			y += height;
			height *= -1;
		}

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public static Rectangle ofDimensions(int width, int height) {
		return new Rectangle(0, 0, width, height);
	}

	public Rectangle offset(int x, int y) {
		return new Rectangle(this.x + x, this.y + y, width, height);
	}

	public Rectangle grow(int x, int y) {
		return new Rectangle(this.x, this.y, width + x, height + y);
	}

	public boolean contains(Position position) {
		return position != null && contains(position.getX(), position.getY());
	}

	public boolean contains(int x, int y) {
		return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height;
	}

	public boolean intersects(Rectangle rectangle) {
		// https://stackoverflow.com/a/306332
		return rectangle != null && x < rectangle.getEndX() && getEndX() > rectangle.x && getEndY() > rectangle.y && y < rectangle.getEndY();
	}

	public void fill(Colour colour) {
		MinecraftUtils.drawRectangle(this, colour);
	}

	public void stroke(Colour colour) {
		MinecraftUtils.drawOutline(this, colour);
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
