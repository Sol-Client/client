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
	 * @param input the input string.
	 * @param out the crosshair.
	 * @throws IllegalArgumentException if the string is not valid.
	 */
	public void parse(String input, PixelMatrix out) throws IllegalArgumentException {
		validate(out);

		if (!input.startsWith("LCCH-"))
			throw new IllegalArgumentException("Missing prefix");

		input = input.substring(input.indexOf('-') + 1);
		if (input.indexOf('-') == -1)
			throw new IllegalArgumentException("Missing '-'");

		int srcSize = Integer.parseInt(input.substring(0, input.indexOf('-')));
		int dstSize = out.getWidth();
		input = input.substring(input.indexOf('-') + 1);

		byte[] data = Base64.getDecoder().decode(input);
		int offset = MathHelper.floor((dstSize - srcSize) / 2F);

		out.clear();
		for (int y = 0; y < srcSize; y++) {
			for (int x = 0; x < srcSize; x++) {
				int pixelIndex = y * srcSize + x;
				int srcIndex = pixelIndex / 8;
				int mask = 1 << pixelIndex % 8;

				if (srcIndex < 0 || srcIndex >= data.length)
					continue;
				if (x + offset < 0 || x + offset >= dstSize || y + offset < 0 || x + offset >= dstSize)
					continue;

				if ((data[srcIndex] & mask) != 0)
					out.set(x + offset + (y + offset) * dstSize);
			}
		}
	}

	/**
	 * Convert a crosshair to a Lunar Client string.
	 * Broken but it is in Lunar too. I promise!
	 * @param in the pixel matrix.
	 * @return the string.
	 */
	public String stringify(PixelMatrix in) {
		validate(in);

		StringBuilder result = new StringBuilder();
		result.append("LCCH-");

		int srcSize = in.getWidth();
		int dstSize = getSize(in, srcSize);
		result.append(dstSize);
		result.append('-');

		int offset = MathHelper.floor((srcSize - dstSize) / 2);
		byte[] data = new byte[MathHelper.ceil(dstSize * dstSize / 8F)];

		for (int y = 0; y < dstSize; y++) {
			for (int x = 0; x < dstSize; x++) {
				if (!in.get(x + offset, y + offset))
					continue;

				int pixelIndex = y * dstSize + x;
				data[pixelIndex / 8] |= 1 << (pixelIndex % 8);
			}
		}

		result.append(new String(Base64.getEncoder().withoutPadding().encode(data)));
		return result.toString();
	}

	private int getSize(PixelMatrix matrix, int srcSize) {
		int result = 1;
		for (int i = 0; i < matrix.pixels(); i++) {
			if (!matrix.get(i))
				continue;

			int x = i % srcSize;
			int y = i / srcSize;

			int pxSize;
			if ((pxSize = applyOffset(x, srcSize)) > result)
				result = pxSize;
			if ((pxSize = applyOffset(y, srcSize)) > result)
				result = pxSize;
		}
		return result;
	}

	private int applyOffset(int value, int srcSize) {
		value -= srcSize / 2;
		value *= 2;
		if (value <= 0)
			value = -value + 1;
		return value;
	}

	private void validate(PixelMatrix matrix) {
		if (matrix.getWidth() == matrix.getHeight())
			return;

		throw new IllegalArgumentException("Matrix must be square");
	}

}
