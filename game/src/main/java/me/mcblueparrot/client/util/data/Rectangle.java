package me.mcblueparrot.client.util.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mcblueparrot.client.util.Utils;

@AllArgsConstructor
public class Rectangle {

	@Getter
	private int x;
	@Getter
	private int y;
	@Getter
	private int width;
	@Getter
	private int height;

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
		return y + height;
	}

	public int getEndY() {
		return y + height;
	}

}
