package io.github.solclient.client.util;

import java.util.Base64;

import io.github.solclient.client.util.data.PixelMatrix;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.MathHelper;

/**
 * Implementation of Lunar Client's crosshair format (LCCH).
 */
@UtilityClass
public class LCCH {

	/**
	 * Parse a Lunar Client crosshair and copy it to a pixel matrix.
	 *
	 * @param input the input string.
	 * @param out   the crosshair.
	 * @throws IllegalArgumentException if the string is not valid.
	 */
	public void parse(String input, PixelMatrix out) throws IllegalArgumentException {
		validate(out);

		if (!input.startsWith("LCCH-"))
			throw new IllegalArgumentException("Missing prefix");

		input = input.substring(input.indexOf('-') + 1);

		if (input.indexOf('-') == -1)
			throw new IllegalArgumentException("Missing data");

		int size = Integer.parseInt(input.substring(0, input.indexOf('-')));
		int offset = out.getWidth() / 2 - size / 2;

		input = input.substring(input.indexOf('-') + 1);

		if (input.isEmpty())
			throw new IllegalArgumentException("Data is empty");

		out.clear();

		byte[] data = Base64.getDecoder().decode(input);

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int srcIndex = y * size + x;
				int srcHunk = srcIndex / 8;
				int srcMask = 1 << srcIndex % 8;

				int dstIndex = out.getIndex(x + offset, y + offset);

				if (srcHunk >= data.length)
					continue;
				if (dstIndex < 0 || dstIndex >= out.pixels())
					continue;

				if ((data[srcHunk] & srcMask) != 0)
					out.set(dstIndex);
				else
					out.clear(dstIndex);
			}
		}
	}

	/**
	 * Convert a crosshair to a Lunar Client string.
	 *
	 * @param in the pixel matrix.
	 * @return the string.
	 */
	public String stringify(PixelMatrix in) {
		validate(in);

		StringBuilder result = new StringBuilder();
		result.append("LCCH-");

		int size = getEffectiveSize(in);
		int offset = in.getWidth() / 2 - size / 2;
		result.append(size);
		result.append('-');

		byte[] data = new byte[MathHelper.ceil(size * size / 8F)];

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int dstIndex = y * size + x;
				int dstHunk = dstIndex / 8;
				int dstMask = 1 << dstIndex % 8;

				if (in.get(x + offset, y + offset))
					data[dstHunk] |= dstMask;
			}
		}

		result.append(Base64.getEncoder().withoutPadding().encodeToString(data));
		return result.toString();
	}

	private int getEffectiveSize(PixelMatrix matrix) {
		int result = 1;

		for (int y = 0; y < matrix.getHeight(); y++) {
			for (int x = 0; x < matrix.getWidth(); x++) {
				if (!matrix.get(x, y))
					continue;

				int pxSize = sizeFromDistance(x, y, matrix.getWidth() / 2);
				result = Math.max(result, pxSize);
				// it's not going to get any larger...
				if (pxSize == matrix.getWidth())
					break;
			}
		}

		return result;
	}

	private int sizeFromDistance(int x, int y, int from) {
		return Math.max(Math.abs(x - from) * 2 + 1, Math.abs(y - from) * 2 + 1);
	}

	private void validate(PixelMatrix matrix) {
		if (matrix.getWidth() == matrix.getHeight())
			return;

		throw new IllegalArgumentException("Matrix must be square");
	}

}
